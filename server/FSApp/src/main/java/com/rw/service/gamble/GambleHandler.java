package com.rw.service.gamble;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.common.HPCUtil;
import com.common.RefInt;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.UserGameDataMgr;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.gamble.datamodel.GambleDropCfgHelper;
import com.rw.service.gamble.datamodel.GambleDropGroup;
import com.rw.service.gamble.datamodel.GambleDropHistory;
import com.rw.service.gamble.datamodel.GambleHotHeroPlan;
import com.rw.service.gamble.datamodel.GamblePlanCfg;
import com.rw.service.gamble.datamodel.GamblePlanCfgHelper;
import com.rw.service.gamble.datamodel.GambleRecord;
import com.rw.service.gamble.datamodel.GambleRecordDAO;
import com.rw.service.gamble.datamodel.HotGambleCfgHelper;
import com.rw.service.gamble.datamodel.IDropGambleItemPlan;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwproto.GambleServiceProtos.EGambleResultType;
import com.rwproto.GambleServiceProtos.EGambleType;
import com.rwproto.GambleServiceProtos.GambleRequest;
import com.rwproto.GambleServiceProtos.GambleResponse;
import com.rwproto.GambleServiceProtos.GambleRewardData;

public class GambleHandler {
	public static final int HotHeroPoolSize = 3;
	

	public Random getRandom() {
		return HPCUtil.getRandom();
	}

	private GambleHandler() {
	}

	private static GambleHandler instance = new GambleHandler();

	public static GambleHandler getInstance() {
		if (instance == null) {
			instance = new GambleHandler();
		}
		return instance;
	}
	
	private ThreadLocal<Player> context=new ThreadLocal<Player>();
	private void setContext(Player p){
		context.set(p);
	}
	
	public Player getContext(){
		return context.get();
	}
	
	protected void clearContext(){
		context.remove();
	}
	
	public ByteString gamble(GambleRequest request, Player player) {
		GambleResponse.Builder response = GambleResponse.newBuilder();
		response.setRequest(request);
		
		int gamblePlanId = request.getGamblePlanId();
		String planIdStr = String.valueOf(gamblePlanId);
		GamblePlanCfg planCfg = GamblePlanCfgHelper.getInstance().getConfig(gamblePlanId,player.getLevel());
		if (planCfg == null){
			return GambleLogicHelper.SetError(response,player,String.format("找不到配置:ID=%s,level=%i", planIdStr,player.getLevel()),"没有配置");
		}
		
		if (player.getLevel() < planCfg.getOpenLevel()){
			return GambleLogicHelper.SetError(response,player,String.format("等级不足，配置:%s", planIdStr),String.format("%i级开放", planCfg.getOpenLevel()));
		}

		if (player.getVip() < planCfg.getOpenVipLevel()){
			return GambleLogicHelper.SetError(response,player,String.format("VIP等级不足，配置:%s", planIdStr),String.format("VIP%i级开放", planCfg.getOpenVipLevel()));
		}
		
		if (planCfg.getDropItemCount() <= 0){
			return GambleLogicHelper.SetError(response,player,String.format("无效掉落物品数量，配置:%s", planIdStr),"无法抽卡");
		}
		
		StringBuilder trace = gamblePlanId ==5 ? new StringBuilder() : null;
		
		UserGameDataMgr userGameDataMgr = player.getUserGameDataMgr();
		GambleRecordDAO gambleRecords = GambleRecordDAO.getInstance();
		String userId = player.getUserId();
		GambleRecord record = gambleRecords.getOrCreate(userId);
		GambleDropHistory historyRecord = record.getHistory(gamblePlanId);
		// 保证热点随机种子初始化
		if (planCfg.getHotCount() > 0 && historyRecord.getHotCheckThreshold() <= 0){
			historyRecord.GenerateHotCheckCount(getRandom(), planCfg.getHotCheckMin(), planCfg.getHotCheckMax());
		}

		IDropGambleItemPlan dropPlan;//免费或者收费方案组
		boolean isFree = historyRecord.canUseFree(planCfg);
		GambleLogicHelper.logTrace(trace,historyRecord);
		GambleLogicHelper.logTrace(trace,"isFree:"+isFree);

		if (isFree){//使用免费方案
			dropPlan = planCfg.getFreePlan();
		}else{//使用收费方案
			if (!userGameDataMgr.isEnoughCurrency(planCfg.getMoneyType(), planCfg.getMoneyNum())){
				return GambleLogicHelper.SetError(response,player,String.format("金钱不足，配置:%s", planIdStr),"金钱不足");
			}
			dropPlan = planCfg.getChargePlan();
		}

		setContext(player);
		Random ranGen = getRandom();
		RefInt slotCount = new RefInt();
		ArrayList<GambleRewardData> dropList = new ArrayList<GambleRewardData>(10);
		
		final int maxHistoryNumber = GamblePlanCfgHelper.getInstance().getMaxHistoryCount(planCfg.getDropType());// planCfg.getMaxCheckCount();
		GambleLogicHelper.logTrace(trace,"maxHistoryNumber:"+maxHistoryNumber);
		GambleDropCfgHelper gambleDropConfig = GambleDropCfgHelper.getInstance();
		String defaultItem = String.valueOf(planCfg.getGoods());
		int firstDropItemId = isFree ? planCfg.getFreeFirstDrop() : planCfg.getChargeFirstDrop();
		boolean isFirstTime = isFree ? historyRecord.isFreeGambleFirstTime() : historyRecord.isChargeGambleFirstTime();
		
		if (isFirstTime && firstDropItemId > 0){//firstDropItemId配置为0表示不想搞首抽必掉
			//计算首次必掉
			String itemModel = gambleDropConfig.getRandomDrop(ranGen, firstDropItemId, slotCount);
			if (StringUtils.isBlank(itemModel) || slotCount.value <= 0){
				//首抽配置错误，在日志纪录并跳过首抽
				//return SetError(response,player,String.format("首抽配置无效，配置:%s", planIdStr),"首抽未配置");
				GameLog.error("钓鱼台", userId, String.format("首抽配置无效，配置:%s", planIdStr));
			}else if (GambleLogicHelper.add2DropList(dropList, slotCount.value, itemModel,userId,planIdStr,defaultItem)){
				historyRecord.add(isFree,itemModel,slotCount.value,maxHistoryNumber);
			}
		}

		//判断是否需要使用热点方案
		if (planCfg.getHotCount() > 0) {
			HotGambleCfgHelper hotGambleConfig = HotGambleCfgHelper.getInstance();
			String heroId = hotGambleConfig.getTodayGuanrateeHotHero(slotCount);
			//特殊容错处理：如果保底英雄有效就作为容错默认值，否则使用必送丹药作为默认值
			String errDefaultModelId= GambleLogicHelper.isValidHeroOrItemId(heroId) ? heroId : defaultItem;
			
			//TODO 热点是否也需要考虑去重？
			if (historyRecord.getHotHistoryCount() < historyRecord.getHotCheckThreshold()-1) {
				// 用热点组生成N个英雄
				int hotPlanId = hotGambleConfig.getTodayHotPlanId();
				GambleHotHeroPlan hotPlan = GambleHotHeroPlan.getTodayHotHeroPlan(ranGen, hotPlanId, HotHeroPoolSize,errDefaultModelId);
				int hotCount = 0;
				while (hotCount < planCfg.getHotCount()) {
					String itemModel = hotPlan.getRandomDrop(ranGen,slotCount);
					if (!GambleLogicHelper.add2DropList(dropList, slotCount.value, itemModel, userId, planIdStr,errDefaultModelId)) {
						GameLog.error("钓鱼台", userId, "热点英雄配置有问题");
					}
					hotCount++;
				}
			} else {
				// 使用热点保底英雄
				if (GambleLogicHelper.add2DropList(dropList, slotCount.value, heroId, userId, planIdStr,errDefaultModelId)) {
					historyRecord.resetHotHistory(ranGen, planCfg.getHotCheckMin(), planCfg.getHotCheckMax());
				}
			}
			
			//每次热点英雄的抽取，只要纪录次数即可
			historyRecord.addHotHistoryCount();
		}
		
		final int maxCount = planCfg.getDropItemCount();
		RefInt selectedDropGroupIndex = new RefInt();
		IDropGambleItemPlan originalDropPlan = dropPlan;
		while (dropList.size() < maxCount){
			GambleLogicHelper.logTrace(trace,"dropListCount="+dropList.size());
			if (dropPlan == null){
				//最后容错：20个经验单
				GambleLogicHelper.add2DropList(dropList, 20, defaultItem,userId,planIdStr,defaultItem);
				GambleLogicHelper.logTrace(trace,"最后容错：20个经验单,ID="+defaultItem);
				continue;
			}
			int dropGroupId;
			if (historyRecord.passExclusiveCheck(isFree)){//前面N次的抽卡必须不一样，之后的就不需要唯一性检查
				GambleLogicHelper.logTrace(trace,"passExclusiveCheck:true");
				if(historyRecord.checkGuarantee(isFree,dropPlan,maxHistoryNumber)){
					dropGroupId = dropPlan.getGuaranteeGroup(ranGen,selectedDropGroupIndex);
					GambleLogicHelper.logTrace(trace,"checkGuarantee:true,dropGroupId="+dropGroupId);
				}else{
					dropGroupId = dropPlan.getOrdinaryGroup(ranGen,selectedDropGroupIndex);
					GambleLogicHelper.logTrace(trace,"checkGuarantee:false,dropGroupId="+dropGroupId);
				}
				String itemModel = gambleDropConfig.getRandomDrop(ranGen, dropGroupId, slotCount);
				GambleLogicHelper.logTrace(trace,"random generate itemModel="+itemModel+",slotCount="+slotCount.value);
				if (GambleLogicHelper.add2DropList(dropList, slotCount.value, itemModel,userId,planIdStr,defaultItem)){
					historyRecord.add(isFree,itemModel,slotCount.value,maxHistoryNumber);
				}else{
					//有错误，减少最大抽卡数量
					//maxCount --;
					dropPlan = dropPlan.removeHistoryFromOrdinaryGroup(selectedDropGroupIndex.value);
					GameLog.error("钓鱼台", userId, "严重错误,抽卡失败,itemModel:" + itemModel + ",isFree:" + isFree
							+ ",plan key:" + planCfg.getKey());
				}
				
			}else{
				GambleLogicHelper.logTrace(trace,"passExclusiveCheck:false");
				List<String> checkHistory = historyRecord.getHistory(isFree,dropPlan);
				GambleLogicHelper.logTrace(trace,"checkHistory:",checkHistory);
				GambleDropGroup tmpGroup=null;
				if(historyRecord.checkGuarantee(isFree,dropPlan,maxHistoryNumber)){
					tmpGroup = dropPlan.getGuaranteeGroup(ranGen,checkHistory,selectedDropGroupIndex);
					GambleLogicHelper.logTrace(trace,"checkGuarantee:true,tmpGroup=",tmpGroup);
				}else{
					tmpGroup = dropPlan.getOrdinaryGroup(ranGen,checkHistory,selectedDropGroupIndex);
					GambleLogicHelper.logTrace(trace,"checkGuarantee:false,tmpGroup=",tmpGroup);
				}
				
				if (tmpGroup == null){
					GameLog.error("钓鱼台", player.getUserId(), "严重错误,无法去重,history:"+checkHistory+",isFree:"+isFree+",plan key:"+planCfg.getKey());
					dropPlan = dropPlan.removeHistoryFromOrdinaryGroup(selectedDropGroupIndex.value);
					//maxCount --;
					continue;
				}
				
				RefInt tmpWeight = null;
				String itemModel = tmpGroup.getRandomGroup(ranGen, slotCount,tmpWeight);
				GambleLogicHelper.logTrace(trace,"random generate itemModel="+itemModel+",slotCount="+slotCount.value);
				if (GambleLogicHelper.add2DropList(dropList, slotCount.value, itemModel,userId,planIdStr,defaultItem)){
					historyRecord.add(isFree,itemModel,slotCount.value,maxHistoryNumber);
					historyRecord.checkDistinctTag(isFree,dropPlan.getExclusiveCount());
				}else{
					//有错误，减少最大抽卡数量
					//maxCount --;
					//删去当前使用的组，该用其他组
					dropPlan = dropPlan.removeHistoryFromOrdinaryGroup(selectedDropGroupIndex.value);
					GameLog.error("钓鱼台", userId, "严重错误,抽卡失败,itemModel:" + itemModel + ",isFree:" + isFree
							+ ",plan key:" + planCfg.getKey());
				}
			}
			
			historyRecord.clearGuaranteeHistory(isFree, originalDropPlan);
		}

		clearContext();
		
		GambleLogicHelper.testHasHero(dropList,trace,gamblePlanId,userId);
		
		//扣钱
		if (!isFree){//使用收费方案
			if (!userGameDataMgr.deductCurrency(planCfg.getMoneyType(), planCfg.getMoneyNum())){
				return GambleLogicHelper.SetError(response,player,String.format("金钱不足，配置:%s", planIdStr),"金钱不足");
			}
		}
		
		//必掉经验丹，个数跟掉落物品个数一样
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		itemBagMgr.addItem(planCfg.getGoods(), planCfg.getDropItemCount());
		//保存到背包，并发送跑马灯数据
		{
			String reward = "";
			for (int i = 0; i < dropList.size(); i++) {
				GambleRewardData rewardData = dropList.get(i);
				if (rewardData.getItemId().indexOf("_") != -1) {// 佣兵
					player.getHeroMgr().addHero(rewardData.getItemId());
					MainMsgHandler.getInstance().sendPmdJtYb(player, rewardData.getItemId());
				} else {
					reward += "," + rewardData.getItemId() + "~" + rewardData.getItemNum();
					MainMsgHandler.getInstance().sendPmdJtGoods(player, rewardData.getItemId());
				}
			}
			itemBagMgr.addItemByPrizeStr(reward);
		}

		//保存到历史
		if (!gambleRecords.commit(record)){
			GameLog.error("钓鱼台", userId, "更新历史纪录失败,table:gamble_record");
		}
		
		response.addAllItemList(dropList);
		
		GambleLogicHelper.pushGambleItem(player,ranGen,defaultItem);

		response.setResultType(EGambleResultType.SUCCESS);
		//魂匣抽不算入通用活动
		if(request.getGambleType() != EGambleType.ADVANCED){
			UserEventMgr.getInstance().Gamble(player,planCfg.getDropItemCount() ,planCfg.getMoneyType());
		}
		
		//抽卡完成后通知任务系统抽卡获得奖品的次数
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Altar,dropList.size());
		
		return response.build().toByteString();
	}

	public ByteString gambleData(GambleRequest request, Player player) {
		GambleResponse.Builder response = GambleResponse.newBuilder();
		Random ranGen = getRandom();
		// 热点保底默认值 燃灯道人(魂石)
		String defaultItem = "704012";
		response = GambleLogicHelper.prepareGambleData(request,ranGen, defaultItem,player);
		return response.build().toByteString();
	}

	public static boolean canGambleFreely(Player player) {
		return GambleLogicHelper.canGambleFreely(player);
	}

	public static boolean isFree(Player player, int dropType) {
		return GambleLogicHelper.isFree(player, dropType);
	}
}
