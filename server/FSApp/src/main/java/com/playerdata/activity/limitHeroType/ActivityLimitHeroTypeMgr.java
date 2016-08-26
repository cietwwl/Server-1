package com.playerdata.activity.limitHeroType;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.common.serverdata.ServerCommonData;
import com.common.serverdata.ServerCommonDataHolder;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfgDAO;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroRankCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroRankCfgDAO;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItemHolder;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeSubItem;
import com.rwproto.ActivityLimitHeroTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityLimitHeroTypeProto.ActivityCommonRspMsg.Builder;
import com.rwproto.ActivityLimitHeroTypeProto.GambleType;
import com.rwproto.ActivityLimitHeroTypeProto.GamebleReward;
import com.rwproto.ActivityLimitHeroTypeProto.RankRecord;

public class ActivityLimitHeroTypeMgr implements ActivityRedPointUpdate{
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
		List<ActivityLimitHeroCfg> allCfgList = ActivityLimitHeroCfgDAO.getInstance().getAllCfg();
		for (ActivityLimitHeroCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(cfg)) {
				// 活动未开启
				continue;
			}			
			ActivityLimitHeroTypeItem targetItem = dataHolder.getItem(player.getUserId());// 已在之前生成数据的活动
			if (targetItem != null) {					
				continue;
			}
			targetItem = ActivityLimitHeroCfgDAO.getInstance().newItem(player, cfg);// 生成新开启活动的数据
			if(targetItem == null){
				continue;
			}
			dataHolder.addItem(player, targetItem);
		}
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
		for (ActivityLimitHeroTypeItem targetItem : itemList) {			
			ActivityLimitHeroCfg targetCfg = ActivityLimitHeroCfgDAO.getInstance().getCfgListByItem(targetItem);
			if(targetCfg == null){
				continue;
			}			
			
			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
				targetItem.reset(targetCfg,ActivityLimitHeroCfgDAO.getInstance().newSubItemList(targetCfg));
				dataHolder.updateItem(player, targetItem);
			}
		}
	}

	private void checkClose(Player player) {
		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		List<ActivityLimitHeroTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for(ActivityLimitHeroTypeItem item : itemList){
			if(item.isClosed()){
				continue;			
			}
			ActivityLimitHeroCfg cfg =ActivityLimitHeroCfgDAO.getInstance().getCfgById(item.getCfgId());
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
		for (ActivityLimitHeroTypeSubItem subItem : subList) {// 配置表里的每种奖励
			
			if (item.getIntegral() >= subItem.getIntegral()
					&& !subItem.isTanken()) {
				boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(
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
		ActivityLimitHeroCfg cfg = ActivityLimitHeroCfgDAO.getInstance().getCfgById(item.getCfgId());
		if(cfg == null){
			return;
		}
		
		List<ActivityLimitHeroRankCfg> subCfgList = ActivityLimitHeroRankCfgDAO.getInstance().getByParentCfgId(cfg.getId());
		String tmpReward= null;
		for(ActivityLimitHeroRankCfg subCfg:subCfgList){
			if(num>=subCfg.getRankRanges()[0]&&num<=subCfg.getRankRanges()[1]){
				tmpReward = subCfg.getRewards();
				break;
			}						
		}
		item.setRankRewards(tmpReward);
		ComGiftMgr.getInstance().addtagInfoTOEmail(player, item.getRankRewards(),MAKEUPEMAIL+"" , null);
		
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

	

	public ActivityComResult gamble(Player player, ActivityCommonReqMsg commonReq, Builder response) {
		ActivityComResult result = ActivityComResult.newInstance(false);
		result.setReason("");
		if(commonReq.getGambleType() == GambleType.SINGLE){
			result = gambleSingle(player,response);
		}
		if(commonReq.getGambleType() == GambleType.TEN){
			result =  gambleTen(player,response);
		}		
		return result;
	}
	
	private ActivityComResult gamblenew(Player player,ActivityCommonReqMsg commonReq,Builder response){
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
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "有数据的用户发来了抽卡申请,没找到配置表", null);
			result.setReason("数据异常");
			return result;			
		}
		int spendNeed = cfg.getTencost();
		int integral = cfg.getTenintegral();
		if(!isEnoughGold(player,commonReq,spendNeed,integral,cfg,dataItem)){
			result.setReason("钻石不足");
			return result;
		}
		
		List<String> rewardsList = getRewards();
		doDropList(response,rewardsList);
		
		
		
		player.getUserGameDataMgr().addGold(-spendNeed);
		dataItem.setIntegral(dataItem.getIntegral() + integral);
		dataHolder.updateItem(player, dataItem);
		result.setSuccess(true);
		reFreshIntegralRank(player,dataItem,cfg);		
		return result;
	}
	
	


	/**是否钻石足够；如果是免费次数，则将消耗置0*/
	private boolean isEnoughGold(Player player, ActivityCommonReqMsg commonReq,int spendNeed,int integral,ActivityLimitHeroCfg cfg,ActivityLimitHeroTypeItem item) {
		if(commonReq.getGambleType() == GambleType.SINGLE){
			spendNeed = cfg.getSinglecost();
			integral = cfg.getSingleintegral();
			long now = System.currentTimeMillis();
			long lastTime = item.getLastSingleTime();
			if((now - lastTime) > cfg.getFreecd()* 1000){
				spendNeed = 0;
				item.setLastSingleTime(now);
			}		
			if(player.getUserGameDataMgr().getGold() < spendNeed){
				return false;
			}else{
				return true;
			}
		}else if(commonReq.getGambleType() == GambleType.TEN){
			spendNeed = cfg.getTencost();
			if(player.getUserGameDataMgr().getGold() < cfg.getTencost()){				
				return false;
			}else{				
				return true;
			}			
		}		
		return false;		
	}
	
	//获得抽取的奖励rewards
	private List<String> getRewards() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//加入response
	private void doDropList(Builder response, List<String> rewardsList) {
		ArrayList<GamebleReward> dropList = new ArrayList<GamebleReward>();	
		for(int i = 0;i< rewardsList.size();i++){
			GamebleReward.Builder data = GamebleReward.newBuilder();
			data.setRewardId(700007);
			data.setRewardNum(1);
			dropList.add(data.build());
		}
		response.addAllGamebleReward(dropList);	
	}
	
	private ActivityComResult gambleTen(Player player, Builder response) {
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
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "有数据的用户发来了抽卡申请,没找到配置表", null);
			result.setReason("数据异常");
			return result;			
		}
		if(player.getUserGameDataMgr().getGold() < cfg.getTencost()){
			result.setReason("钻石不足");
			return result;
		}
		
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		itemBagMgr.addItem(803002,10);//需要模板方案来替换
		String str = "700007~10";//需要各种逻辑产生的一个droplist
		itemBagMgr.addItemByPrizeStr(str);
		
		ArrayList<GamebleReward> dropList = new ArrayList<GamebleReward>();		
		GamebleReward.Builder data = GamebleReward.newBuilder();
		for(int i = 0;i< 10;i++){
			data.setRewardId(700007);
			data.setRewardNum(1);
			dropList.add(data.build());
		}
		
		response.addAllGamebleReward(dropList);		
		player.getUserGameDataMgr().addGold(-cfg.getTencost());
		dataItem.setIntegral(dataItem.getIntegral() + cfg.getTenintegral());
		dataHolder.updateItem(player, dataItem);
		result.setSuccess(true);
		reFreshIntegralRank(player,dataItem,cfg);
		return result;
	}

	private ActivityComResult gambleSingle(Player player, Builder response) {
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
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "有数据的用户发来了抽卡申请,没找到配置表", null);
			result.setReason("数据异常");
			return result;			
		}
		
		int spendNeed = cfg.getSinglecost();
		long now = System.currentTimeMillis();
		long lastTime = dataItem.getLastSingleTime();
		if((now - lastTime) > cfg.getFreecd()* 1000){
			spendNeed = 0;
			dataItem.setLastSingleTime(now);
		}		
		if(player.getUserGameDataMgr().getGold() < spendNeed){
			result.setReason("钻石不足");
			return result;
		}
		
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		itemBagMgr.addItem(803002,1);//需要模板方案来替换
		String str = "700007~1";//需要各种逻辑产生的一个droplist
		itemBagMgr.addItemByPrizeStr(str);
		
		ArrayList<GamebleReward> dropList = new ArrayList<GamebleReward>();		
		GamebleReward.Builder data = GamebleReward.newBuilder();
		data.setRewardId(700007);
		data.setRewardNum(1);
		dropList.add(data.build());
		response.addAllGamebleReward(dropList);
		
		player.getUserGameDataMgr().addGold(-spendNeed);
		dataItem.setIntegral(dataItem.getIntegral() + cfg.getSingleintegral());
		dataHolder.updateItem(player, dataItem);
		result.setSuccess(true);
		reFreshIntegralRank(player,dataItem,cfg);
		return result;
	}
	
	
	
	
	
	/**保存积分榜前xx名的玩家抽卡信息*/
	private void reFreshIntegralRank(Player player,
			ActivityLimitHeroTypeItem dataItem,ActivityLimitHeroCfg cfg) {
		ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
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
			compare(map);		
			ServerCommonDataHolder.getInstance().update(scdData);
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
			ServerCommonDataHolder.getInstance().update(scdData);
			compare(map);
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
		compare(map);
		ServerCommonDataHolder.getInstance().update(scdData);
	}
	
	/**降序排序，相同积分时先到先上*/
	private void compare(Map<Integer, ActivityLimitHeroRankRecord> map) {
		List<Map.Entry<Integer, ActivityLimitHeroRankRecord>> list = new ArrayList<Map.Entry<Integer,ActivityLimitHeroRankRecord>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, ActivityLimitHeroRankRecord>>() {
			@Override
			public int compare(Entry<Integer, ActivityLimitHeroRankRecord> o1,
					Entry<Integer, ActivityLimitHeroRankRecord> o2) {
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
		int num = 0;
		for(Map.Entry<Integer, ActivityLimitHeroRankRecord> entry : map.entrySet()){
			RankRecord.Builder record = RankRecord.newBuilder();
			record.setNumber(num);
			record.setName(entry.getValue().getPlayerName());
			record.setGetIntegral(entry.getValue().getIntegral());
			record.setUid(entry.getValue().getUid());			
			response.addRecord(record.build());
			num ++;
		}
		result.setSuccess(true);
		result.setReason("成功获得");
		
		ActivityLimitHeroTypeItemHolder dataHolder = new ActivityLimitHeroTypeItemHolder();
		ActivityLimitHeroTypeItem dataItem = dataHolder.getItem(player.getUserId());
		dataHolder.synData(player, dataItem);
		return result;
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

	
}
