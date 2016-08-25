package com.playerdata.activity.limitHeroType;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.util.Comparators;

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
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItemHolder;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeSubItem;
import com.rwproto.ActivityLimitHeroTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityLimitHeroTypeProto.ActivityCommonRspMsg.Builder;
import com.rwproto.ActivityLimitHeroTypeProto.GambleType;
import com.rwproto.ActivityLimitHeroTypeProto.GamebleReward;
import com.rwproto.GambleServiceProtos.GambleRewardData;

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
		// TODO Auto-generated method stub
		
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
		ArrayList<GamebleReward> dropList = new ArrayList<GamebleReward>(1);		
		GamebleReward.Builder data = GamebleReward.newBuilder();
		for(int i = 0;i< 10;i++){
			data.setRewardId(700007);
			data.setRewardNum(1);
			dropList.add(data.build());
		}
		
		response.addAllGamebleReward(dropList);		
		player.getUserGameDataMgr().addGold(cfg.getTencost());
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
		if(player.getUserGameDataMgr().getGold() < cfg.getSinglecost()){
			result.setReason("钻石不足");
			return result;
		}
		
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		itemBagMgr.addItem(803002,1);//需要模板方案来替换
		String str = "700007~1";//需要各种逻辑产生的一个droplist
		itemBagMgr.addItemByPrizeStr(str);
		ArrayList<GamebleReward> dropList = new ArrayList<GamebleReward>(1);
		
		GamebleReward.Builder data = GamebleReward.newBuilder();
		data.setRewardId(700007);
		data.setRewardNum(1);
		dropList.add(data.build());
		response.addAllGamebleReward(dropList);
		
		player.getUserGameDataMgr().addGold(cfg.getSinglecost());
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
		Map<Integer, ActivityLimitHeroRankRecord> map = scdData.getActivityLimitHeroRankRecord();
		ActivityLimitHeroRankRecord record = new ActivityLimitHeroRankRecord();
		record.setIntegral(dataItem.getIntegral());
		record.setPlayerName(player.getUserName());
		record.setUid(player.getUserId());
		record.setRegditTime(System.currentTimeMillis());
		
		if(map.size() < cfg.getRankNumer()){			
			record.setId(map.size());
			map.put(map.size(), record);
			ServerCommonDataHolder.getInstance().update(scdData);
			return;
		}
		map.remove(map.size() -1);
		record.setId(map.size());
		map.put(map.size(), record);
		
		
		
		List<Map.Entry<Integer, ActivityLimitHeroRankRecord>> list = new ArrayList<Map.Entry<Integer,ActivityLimitHeroRankRecord>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, ActivityLimitHeroRankRecord>>() {
			
			@Override
			public int compare(Entry<Integer, ActivityLimitHeroRankRecord> o1,
					Entry<Integer, ActivityLimitHeroRankRecord> o2) {
				// TODO Auto-generated method stub
				if(o1.getValue().getIntegral()>o2.getValue().getIntegral()){
					return -1;
				}else{
					return 1;
				}
			}
		});
		
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
