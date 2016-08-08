package com.playerdata.activity.limitHeroType;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.common.HPCUtil;
import com.common.serverdata.ServerCommonData;
import com.common.serverdata.ServerCommonDataHolder;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGambleDropCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGambleDropCfgDAO;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGamblePlanCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGamblePlanCfgDAO;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroBoxCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroBoxCfgDAO;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfgDAO;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroRankCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroRankCfgDAO;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItemHolder;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeSubItem;
import com.playerdata.activity.limitHeroType.gamble.FreeGamble;
import com.playerdata.activity.limitHeroType.gamble.Gamble;
import com.playerdata.activity.limitHeroType.gamble.SingelGamble;
import com.playerdata.activity.limitHeroType.gamble.TenGamble;
import com.rw.dataaccess.mapitem.MapItemValidateParam;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.service.gamble.datamodel.DropMissingCfg;
import com.rw.service.gamble.datamodel.DropMissingCfgHelper;
import com.rw.service.gamble.datamodel.DropMissingLogic;
import com.rw.service.role.MainMsgHandler;
import com.rwproto.ActivityLimitHeroTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityLimitHeroTypeProto.ActivityCommonRspMsg.Builder;
import com.rwproto.ActivityLimitHeroTypeProto.GambleType;
import com.rwproto.ActivityLimitHeroTypeProto.GamebleReward;
import com.rwproto.ActivityLimitHeroTypeProto.RankRecord;

public class ActivityLimitHeroTypeMgr implements ActivityRedPointUpdate{
	public final static int TYPE_FREE_GAMBLE = 0;//免费单抽
	public final static int TYPE_SINGAL_GAMBLE = 1;//单抽
	public final static int TYPE_TEN_GAMBLE = 2;//十连抽
	
	private final static HashMap<Integer, Gamble> ActivityLimitGambleMap = new HashMap<Integer, Gamble>();
	static {
		ActivityLimitGambleMap.put(TYPE_FREE_GAMBLE, new FreeGamble());
		ActivityLimitGambleMap.put(TYPE_SINGAL_GAMBLE, new SingelGamble());
		ActivityLimitGambleMap.put(TYPE_TEN_GAMBLE, new TenGamble());
	}
	
	private static ActivityLimitHeroTypeMgr instance = new ActivityLimitHeroTypeMgr();
	public static ActivityLimitHeroTypeMgr getInstance (){
		return instance;
	}
	private final static int MAKEUPEMAIL = 10055;
	public void synCountTypeData(Player player) {
		ActivityLimitHeroTypeItemHolder.getInstance().synAllData(player);
	}
	
	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkClose(player);
	}
	
	private void checkNewOpen(Player player) {
		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		List<ActivityLimitHeroTypeItem> addItemList = null;
		String userId = player.getUserId();
		addItemList = creatItems(userId, dataHolder.getItemStore(userId));
		if (addItemList != null) {
			for(ActivityLimitHeroTypeItem item : addItemList ){
				System.out.println("~~~~~~~~~~~~~~~limithero.id = " + item.getId());
			}
			dataHolder.addItemList(player, addItemList);
		}		
	}
	
	public List<ActivityLimitHeroTypeItem> creatItems(String userid ,MapItemStore<ActivityLimitHeroTypeItem> itemStore){	
		List<ActivityLimitHeroTypeItem> addItemList = null;
		List<ActivityLimitHeroCfg> allCfgList = ActivityLimitHeroCfgDAO.getInstance().getAllCfg();
		for (ActivityLimitHeroCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if(itemStore != null){
				if(itemStore.getItem(userid) != null){					
					return addItemList;
				}
			}
			if (!isOpen(cfg)) {
				// 活动未开启
				continue;
			}			
			ActivityLimitHeroTypeItem item = new ActivityLimitHeroTypeItem();
			item.setId(userid);
			item.setCfgId(cfg.getId());
			item.setUserId(userid);
			item.setVersion(cfg.getVersion());
			item.setLastSingleTime(0);
			item.setIntegral(0);
			List<ActivityLimitHeroBoxCfg> boxCfgList = ActivityLimitHeroBoxCfgDAO.getInstance().getCfgListByParentID(cfg.getId());			
			List<ActivityLimitHeroTypeSubItem> subItemList = new ArrayList<ActivityLimitHeroTypeSubItem>();
			if(boxCfgList == null){
				boxCfgList = new ArrayList<ActivityLimitHeroBoxCfg>();
			}
			for(ActivityLimitHeroBoxCfg boxCfg : boxCfgList){
				ActivityLimitHeroTypeSubItem subItem = new ActivityLimitHeroTypeSubItem();
				subItem.setCfgId(boxCfg.getId());
				subItem.setIntegral(boxCfg.getIntegral());
				subItem.setRewards(boxCfg.getRewards());
				subItemList.add(subItem);
			}			
			item.setSubList(subItemList);
			if(addItemList == null){
				addItemList = new ArrayList<ActivityLimitHeroTypeItem>();
			}
			if (addItemList.size() >= 1) {
				// 同时生成了两条以上数据；
				GameLog.error(LogModule.ComActivityLimitHero, userid, "同时有多个活动开启", null);
				continue;
			}
			addItemList.add(item);			
		}
		return addItemList;
	}

	public boolean isOpen(ActivityLimitHeroCfg cfg) {
		if (cfg != null) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}
	
	private void checkCfgVersion(Player player) {
		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		List<ActivityLimitHeroTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		ActivityLimitHeroCfgDAO activityLimitHeroCfgDAO = ActivityLimitHeroCfgDAO.getInstance();
		for (ActivityLimitHeroTypeItem targetItem : itemList) {			
			ActivityLimitHeroCfg targetCfg = activityLimitHeroCfgDAO.getCfgListByItem(targetItem);
			if(targetCfg == null){
				continue;
			}			
			
			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
				targetItem.reset(targetCfg,activityLimitHeroCfgDAO.newSubItemList(targetCfg));
				dataHolder.updateItem(player, targetItem);
				
				ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
				if(scdData == null){
					continue;
				}
				TreeMap<Integer, ActivityLimitHeroRankRecord> map = scdData.getActivityLimitHeroRankRecord();	
				reFreshRankByVersion(map,targetCfg);				
			}
		}
	}

	private void checkClose(Player player) {
		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		List<ActivityLimitHeroTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		ActivityLimitHeroCfgDAO activityLimitHeroCfgDAO = ActivityLimitHeroCfgDAO.getInstance();
		for(ActivityLimitHeroTypeItem item : itemList){
			if(item.isClosed()){
				continue;			
			}
			ActivityLimitHeroCfg cfg =	activityLimitHeroCfgDAO.getCfgById(item.getCfgId());
			if(cfg == null){
				GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "玩家登录时服务器配置表已更新，只能通过版本核实来刷新数据", null);
				continue;
			}
			if(isOpen(cfg)){
				continue;
			}
			checkRankRewards(player,item);//邮件派发排行奖励
			sendEmailIfGiftNotTaken(player,  item);
			item.setClosed(true);
			item.setTouchRedPoint(true);
			dataHolder.updateItem(player, item);			
		}
	}

	private void sendEmailIfGiftNotTaken(Player player,
			ActivityLimitHeroTypeItem item) {
		List<ActivityLimitHeroTypeSubItem> subList = item.getSubList();
		ActivityLimitHeroCfg cfg = ActivityLimitHeroCfgDAO.getInstance().getCfgById(item.getCfgId());
		if(cfg == null){
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "cfg删除早了", null);
			return;
		}
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		for (ActivityLimitHeroTypeSubItem subItem : subList) {// 配置表里的每种奖励
			
			if (item.getIntegral() >= subItem.getIntegral()
					&& !subItem.isTanken()) {
				boolean isAdd = comGiftMgr.addGiftTOEmailById(
						player, subItem.getRewards(), MAKEUPEMAIL + "",
						cfg.getEmailTitle());
				subItem.setTanken(true);
				if (!isAdd){
					GameLog.error(LogModule.ComActivityVitality,
							player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
				}					
			}
		}
		
	}

	private void checkRankRewards(Player player, ActivityLimitHeroTypeItem item) {
		ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
		ActivityLimitHeroCfgDAO activityLimitHeroCfgDAO = ActivityLimitHeroCfgDAO.getInstance();
		ActivityLimitHeroRankCfgDAO activityLimitHeroRankCfgDAO = ActivityLimitHeroRankCfgDAO.getInstance();
		if(scdData == null){
			return;
		}
		TreeMap<Integer, ActivityLimitHeroRankRecord> map = scdData.getActivityLimitHeroRankRecord();		
		boolean isHas = false;
		int num = 0;
		for(Map.Entry<Integer, ActivityLimitHeroRankRecord> entry : map.entrySet()){
			if(StringUtils.equals(entry.getValue().getUid(), player.getUserId())){
				isHas = true;
				num++;
				break;
			}
			num ++;
		}
		/**有记录，刷新下*/
		if(!isHas){
			return;
		}
		ActivityLimitHeroCfg cfg = activityLimitHeroCfgDAO.getCfgById(item.getCfgId());
		if(cfg == null){
			return;
		}
		
		List<ActivityLimitHeroRankCfg> subCfgList = activityLimitHeroRankCfgDAO.getByParentCfgId(cfg.getId());
		String tmpReward= null;
		for(ActivityLimitHeroRankCfg subCfg:subCfgList){
			if(num>=subCfg.getRankRanges()[0]&&num<=subCfg.getRankRanges()[1]){
				tmpReward = subCfg.getRewards();
				break;
			}						
		}
		item.setRankRewards(tmpReward);
		ComGiftMgr.getInstance().addGiftTOEmailById(player, item.getRankRewards(),MAKEUPEMAIL+"" , cfg.getEmailTitle());
		
	}

	public ActivityComResult getRewards(Player player,int boxId) {
		ActivityComResult result = ActivityComResult.newInstance(false);
		result.setReason("");
		
		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		ActivityLimitHeroTypeItem dataItem = dataHolder.getItem(player.getUserId());
		List<ActivityLimitHeroTypeSubItem> subList = dataItem.getSubList();
		ActivityLimitHeroTypeSubItem tagratItem = null;
		for(ActivityLimitHeroTypeSubItem subItem : subList){
			if(!StringUtils.equals(subItem.getCfgId(), boxId+"")){
				continue;
			}
			if(subItem.isTanken()){
				continue;
			}
			tagratItem = subItem;
			break;
		}
		if(tagratItem == null){
			result.setReason("没有对应的奖励箱子");			
			return result;
		}
		tagratItem.setTanken(true);		
		ComGiftMgr.getInstance().addGiftById(player, tagratItem.getRewards());
		dataHolder.updateItem(player, dataItem);
		result.setReason("恭喜你获得奖励");
		result.setSuccess(true);
		return result;
	}

	

	
	public ActivityComResult gamble(Player player,ActivityCommonReqMsg commonReq,Builder response){
		ActivityComResult result = ActivityComResult.newInstance(false);
		result.setReason("");
		ActivityLimitHeroTypeItemHolder dataHolder = new ActivityLimitHeroTypeItemHolder();
		ActivityLimitHeroTypeItem dataItem = dataHolder.getItem(player.getUserId());
		if(dataItem == null){
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "没有数据的用户发来了抽卡申请", null);
			result.setReason("数据异常");
			return result;
		}
		ActivityLimitHeroCfg cfg = ActivityLimitHeroCfgDAO.getInstance().getCfgById(dataItem.getCfgId());
		if(cfg == null){
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "有数据的用户发来了抽卡申请,没找到配置活动表", null);
			result.setReason("数据异常");
			return result;			
		}
		ActivityLimitGamblePlanCfg planCfg = ActivityLimitGamblePlanCfgDAO.getInstance().getCfgByType(commonReq.getGambleType().getNumber(), player.getLevel());
		if(planCfg == null){
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "有数据的用户发来了抽卡申请,没找到配置抽卡方案表", null);
			result.setReason("数据异常");
			return result;	
		}
		boolean isFree = false;
		if(!isEnoughGold(player,commonReq,planCfg,cfg,dataItem,isFree)){
			result.setReason("钻石不足");
			return result;
		}
		int guatanteeTimes = 0;
		int type = getType(dataItem,planCfg,commonReq,isFree,guatanteeTimes);
		Gamble handler = ActivityLimitGambleMap.get(type);
		String map = handler.gamble(player, dataHolder, planCfg,guatanteeTimes);
		dataHolder.updateItem(player, dataItem);
		doDropList(player,response,map);		
		result.setSuccess(true);
		reFreshIntegralRank(player,dataItem,cfg);		
		return result;
	}	

	/**是否钻石足够；如果是免费次数，则将消耗置0
	 * @param isFree */
	private boolean isEnoughGold(Player player, ActivityCommonReqMsg commonReq,ActivityLimitGamblePlanCfg planCfg,ActivityLimitHeroCfg cfg,ActivityLimitHeroTypeItem item, boolean isFree) {
		int spendNeed = planCfg.getMoneyNum();
		if(commonReq.getGambleType() == GambleType.SINGLE){
			long now = System.currentTimeMillis();
			long lastTime = item.getLastSingleTime();
			if((now - lastTime) > planCfg.getRecoverTime()* 1000){
				item.setLastSingleTime(now);
				spendNeed = 0;
				isFree = true;
			}		
			if(player.getUserGameDataMgr().getGold() < spendNeed){
				return false;
			}else{				
				player.getUserGameDataMgr().addGold(-spendNeed);
				item.setIntegral(item.getIntegral() + cfg.getSingleintegral());
				return true;
			}
		}else if(commonReq.getGambleType() == GambleType.TEN){
			if(player.getUserGameDataMgr().getGold() < spendNeed){				
				return false;
			}else{
				player.getUserGameDataMgr().addGold(-spendNeed);
				item.setIntegral(item.getIntegral() + cfg.getTenintegral());
				return true;
			}			
		}		
		return false;		
	}
	
	/**获取方案，以及触发的保底次数
	 * @param guatanteeTimes */
	private int getType(ActivityLimitHeroTypeItem dataItem,
			ActivityLimitGamblePlanCfg planCfg, ActivityCommonReqMsg commonReq, boolean isFree, int guatanteeTimes) {
		int type = TYPE_SINGAL_GAMBLE;
		type = getBaseType(commonReq,isFree,type);		
		//根据单十抽来判断是否触发保底
		if(commonReq.getGambleType() == GambleType.SINGLE){			
			for(Integer time : planCfg.getGuaranteeList()){
				if(time == (dataItem.getGuarantee() + 1)){
					guatanteeTimes  = 1;
					break;
				}
			}
			if(dataItem.getGuarantee()!=0 &&(dataItem.getGuarantee() + 1)%planCfg.getMaxGuarantee() == 0){
				guatanteeTimes  = 1;
			}
			dataItem.setGuarantee(dataItem.getGuarantee() + 1);
		}else if (commonReq.getGambleType() == GambleType.TEN){
			int lastGuarantee = dataItem.getGuarantee() + planCfg.getDropItemCount();			
			for(Integer time : planCfg.getGuaranteeList()){
				if(lastGuarantee >= time&&dataItem.getGuarantee() < time){
					guatanteeTimes++;
					
				}
			}
			for(int i = dataItem.getGuarantee()+1;i < lastGuarantee+1;i++){
				if(i%planCfg.getMaxGuarantee() == 0&&dataItem.getGuarantee() != 0){
					guatanteeTimes++;
				}
			}			
		}		
		return type;
	}
	
	/**在不计算保底的基础上简单分类*/
	private int getBaseType(ActivityCommonReqMsg commonReq, boolean isFree,
			int type) {
		if(commonReq.getGambleType() == GambleType.SINGLE){
			if(isFree){
				type = TYPE_FREE_GAMBLE;
			}else{
				type = TYPE_SINGAL_GAMBLE;
			}
		}else if (commonReq.getGambleType() == GambleType.TEN){
			type = TYPE_TEN_GAMBLE;
		}
		return type;
	}

	public String  getGambleRewards(Player player,Map<Integer, Integer> planList){
		StringBuilder strbuild = new StringBuilder();
		int randomGroup = HPCUtil.getRandom().nextInt(10000);
		int groupId = getRandomGroup(planList,randomGroup);
		List<ActivityLimitGambleDropCfg> cfgList = ActivityLimitGambleDropCfgDAO.getInstance().getActivityLimitGambleDropCfgByPoolId(groupId);
		int sumWeight = ActivityLimitGambleDropCfgDAO.getInstance().getSumWeightByPoolId(groupId);
		int result = HPCUtil.getRandom().nextInt(sumWeight);
		DropMissingCfg cfg = DropMissingCfgHelper.getInstance().getCfgById(cfgList.get(0).getItemID());
		if(cfgList.size() == 1&&cfg != null){
			//从某个指定的道具组里随机一个，比如蓝装，绿装，紫装
			String id = DropMissingLogic.getInstance().searchMissingItem(player, cfg);	
			
			if (id == null){
				planList.remove(planList.get(groupId));
				if(planList.isEmpty()){
					return null;
				}
				return getGambleRewards(player,planList);
			}
			
			

			return strbuild.append(id).append("~").append(1).toString();
		}
		
		
		ActivityLimitGambleDropCfg resultCfg = getRandomCfg(cfgList,result);
		strbuild.append(resultCfg.getItemID()).append("~").append(resultCfg.getSlotCount());
		return strbuild.toString();		
	}
 	
	private ActivityLimitGambleDropCfg getRandomCfg(List<ActivityLimitGambleDropCfg> list, int result) {
		int value = 0;
		for (ActivityLimitGambleDropCfg cfg : list) {
			value += cfg.getWeight();
			if (result < value) {
				return cfg;
			}
		}
		return list.size() > 0 ? list.get(0) : null;
	}
	
	private int getRandomGroup(Map<Integer, Integer> planList, int result) {
		int value = 0;
		int firstPlan = 0;
		int i = 0;
		for(Map.Entry<Integer, Integer> entry : planList.entrySet()){
			if(i == 0)firstPlan = entry.getKey();
			i++;
			value += entry.getValue();
			if(result < value){
				return entry.getKey();
			}
		}		
		return planList.size() > 0 ? planList.get(firstPlan) : null;
	}
	
	//id~num,id2~num2格式奖励加入response和背包;如果是英雄，则为id_starlevel~num;
	private void doDropList(Player player,Builder response, String map) {
		ArrayList<GamebleReward> dropList = new ArrayList<GamebleReward>();		
		MainMsgHandler mainMsgHandler = MainMsgHandler.getInstance();
		String reward = "";
		if(map == null){
			return;
		}
		String[] splitList = map.split(",");
		for(String str : splitList){
			String[] idAndNum = str.split("~");			
			Integer modelId = -1;
			Integer count = Integer.parseInt(idAndNum[1]);
//			player.getItemBagMgr().addItem(modelId, count);
			if(idAndNum[0].indexOf("_") != -1){//佣兵
				player.getHeroMgr().addHero(player, idAndNum[0]);//自动转碎片
				mainMsgHandler.sendPmdJtYb(player, idAndNum[0]);
			}else{
				modelId = Integer.parseInt(idAndNum[0]);
				reward += "," + modelId + "~" + count;
				mainMsgHandler.sendPmdJtGoods(player, idAndNum[0]);
			}
			
			GamebleReward.Builder data = GamebleReward.newBuilder();
			data.setRewardId(idAndNum[0]);
			data.setRewardNum(count);
			dropList.add(data.build());		
		}
		player.getItemBagMgr().addItemByPrizeStr(reward);
		response.addAllGamebleReward(dropList);	
	}
		
	/**保存积分榜前xx名的玩家抽卡信息*/
	private void reFreshIntegralRank(Player player,
			ActivityLimitHeroTypeItem dataItem,ActivityLimitHeroCfg cfg) {
		ServerCommonDataHolder serverCommonDataHolder = ServerCommonDataHolder.getInstance();
		ServerCommonData scdData = serverCommonDataHolder.get();
		if(scdData == null){
			return;
		}
		TreeMap<Integer, ActivityLimitHeroRankRecord> map = scdData.getActivityLimitHeroRankRecord();		
		reFreshRankByVersion(map,cfg);			
		boolean isHas = false;
		for(Map.Entry<Integer, ActivityLimitHeroRankRecord> entry : map.entrySet()){
			if(StringUtils.equals(entry.getValue().getUid(), player.getUserId())){
				entry.getValue().setIntegral(dataItem.getIntegral());
				entry.getValue().setRegditTime(System.currentTimeMillis());
				isHas = true;
				break;
			}
		}
		/**有记录，刷新下*/
		if(isHas){
//			compare(map);		
			serverCommonDataHolder.update(scdData);
			return;
		}
		
		
		ActivityLimitHeroRankRecord record = new ActivityLimitHeroRankRecord();
		record.setIntegral(dataItem.getIntegral());
		record.setPlayerName(player.getUserName());
		record.setUid(player.getUserId());
		record.setRegditTime(System.currentTimeMillis());
		record.setVersion(cfg.getVersion());
		
		
		/**没记录，但不用抢*/
		if(map.size() < cfg.getRankNumer()){
			map.put(map.size(), record);
			serverCommonDataHolder.update(scdData);
//			compare(map);
			return;
		}
		/**抢*/
		ActivityLimitHeroRankRecord recordTmp = map.lastEntry().getValue();		
		if(record.getIntegral() <= recordTmp.getIntegral()){
			//**没抢过
			return;
		}
		
		map.remove(map.size() -1);
		map.put(map.size(), record);
//		compare(map);
		serverCommonDataHolder.update(scdData);
	}
	
	

	/**
	 * 
	 * @param map  活动换版本时清空数据
	 * @param cfg
	 */
	private void reFreshRankByVersion(
			Map<Integer, ActivityLimitHeroRankRecord> map,
			ActivityLimitHeroCfg cfg) {
		for(Map.Entry<Integer, ActivityLimitHeroRankRecord> entry: map.entrySet()){		
			if(!StringUtils.equals(entry.getValue().getVersion(), cfg.getVersion())){
				map.clear();
				return;
			}
		}		
	}

	/**
	 * 
	 * @param player
	 * @param commonReq  排行榜数据在此处推给客户端；同时触发一次holder推送免费时间
	 * @param response   要保证发过去的每条记录里的number数据时对应的排名，就必须确保插入更新时都有做排序操作
	 * @return
	 */
	public ActivityComResult viewRank(Player player,
			ActivityCommonReqMsg commonReq, Builder response) {
		ActivityComResult result = ActivityComResult.newInstance(false);
		result.setReason("");
		ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
		if(scdData == null){
			result.setSuccess(true);
			result.setReason("排行榜是空的");
			return result;
		}
		Map<Integer, ActivityLimitHeroRankRecord> map = scdData.getActivityLimitHeroRankRecord();
		if(map.isEmpty()){
			result.setSuccess(true);
			result.setReason("排行榜是空的");
			return result;
		}
		compareAndAddResponse(map,response);		
		
		result.setSuccess(true);
		result.setReason("成功获得");
		
		ActivityLimitHeroTypeItemHolder dataHolder = new ActivityLimitHeroTypeItemHolder();
		ActivityLimitHeroTypeItem dataItem = dataHolder.getItem(player.getUserId());
		dataHolder.synData(player, dataItem);
		return result;
	}
	
	/**降序排序，相同积分时先到先上,只在查询时排序；应该存为list，那样可以只在存储时处理而不是查询时
	 * @param response */
	private void compareAndAddResponse(Map<Integer, ActivityLimitHeroRankRecord> map, Builder response) {		
		List<Map.Entry<Integer, ActivityLimitHeroRankRecord>> list = new ArrayList<Map.Entry<Integer,ActivityLimitHeroRankRecord>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, ActivityLimitHeroRankRecord>>() {
			@Override
			public int compare(Map.Entry<Integer, ActivityLimitHeroRankRecord> o1,
					Map.Entry<Integer, ActivityLimitHeroRankRecord> o2) {
				// TODO Auto-generated method stub
				if(o1.getValue().getIntegral()>o2.getValue().getIntegral()){
					return -1;
				}else if(o1.getValue().getIntegral()==o2.getValue().getIntegral()){
					if(o1.getValue().getRegditTime() < o2.getValue().getRegditTime()){
						return -1;
					}					
					return 1;					
				}else{
					return 1;
				}
			}
		});
		int num = 0;
		for(Map.Entry<Integer, ActivityLimitHeroRankRecord> mapRecord : list){
			RankRecord.Builder record = RankRecord.newBuilder();
			record.setNumber(num);
			record.setName(mapRecord.getValue().getPlayerName());
			record.setGetIntegral(mapRecord.getValue().getIntegral());
			record.setUid(mapRecord.getValue().getUid());			
			response.addRecord(record.build());
			num++;
		}
	}
	
	
	@Override
	public void updateRedPoint(Player player, String enumStr) {
		ActivityLimitHeroTypeItemHolder activityLimitHeroItemHolder = new ActivityLimitHeroTypeItemHolder();
		ActivityLimitHeroCfg cfg = ActivityLimitHeroCfgDAO.getInstance().getCfgById(enumStr);
		if(cfg == null ){
			return;
		}
		
		ActivityLimitHeroTypeItem dataItem = activityLimitHeroItemHolder.getItem(player.getUserId());
		if(dataItem == null){
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动数据", null);
			return;
		}
		if(!dataItem.isTouchRedPoint()){
			dataItem.setTouchRedPoint(true);
			activityLimitHeroItemHolder.updateItem(player, dataItem);
		}			
	}

	public boolean isOpen(MapItemValidateParam param) {
		List<ActivityLimitHeroCfg> allCfgList = ActivityLimitHeroCfgDAO.getInstance().getAllCfg();
		for (ActivityLimitHeroCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (isOpen(cfg,param)) {
				return true;
			}			
		}
		return false;
	}
	
	public boolean isOpen(ActivityLimitHeroCfg cfg,MapItemValidateParam param) {
		if (cfg != null) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = param.getCurrentTime();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}
	
}
