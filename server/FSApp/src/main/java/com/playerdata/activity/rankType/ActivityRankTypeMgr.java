package com.playerdata.activity.rankType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.RankType;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.RankingMgr;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfg;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfgDAO;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeSubCfg;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeSubCfgDAO;
import com.playerdata.activity.rankType.cfg.SendRewardRecord;
import com.playerdata.activity.rankType.data.ActivityRankTypeEntry;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rankType.data.ActivityRankTypeItemHolder;
import com.playerdata.activity.rankType.data.ActivityRankTypeUserInfo;
import com.rw.dataaccess.mapitem.MapItemValidateParam;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.ranking.RankingUtils;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwproto.RankServiceProtos.RankInfo;


public class ActivityRankTypeMgr implements ActivityRedPointUpdate{
	
	private static ActivityRankTypeMgr instance = new ActivityRankTypeMgr();
	
	
	private static Map<String, SendRewardRecord> sendMap = new HashMap<String, SendRewardRecord>();
	
	public static ActivityRankTypeMgr getInstance(){
		return instance;
	}	
	
	/**登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空*/
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkClose(player);
	}	

	private void checkNewOpen(Player player) {
		ActivityRankTypeItemHolder dataHolder = ActivityRankTypeItemHolder.getInstance();
		ActivityRankTypeCfgDAO activityRankTypeCfgDAO = ActivityRankTypeCfgDAO.getInstance();
		List<ActivityRankTypeCfg> allCfgList = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityRankTypeCfg activityRankTypeCfg : allCfgList) {//遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if(!isOpen(activityRankTypeCfg)){
				continue;
			}
			ActivityRankTypeEnum RankTypeEnum = ActivityRankTypeEnum.getById(activityRankTypeCfg.getEnumId());	
			if(RankTypeEnum == null){
				
				continue;
			}
			ActivityRankTypeItem targetItem = dataHolder.getItem(player.getUserId(), RankTypeEnum);//已在之前生成数据的活动
			if(targetItem != null){
				continue;
			}
			targetItem = activityRankTypeCfgDAO.newItem(player, activityRankTypeCfg);//生成新开启活动的数据
			if(targetItem!=null){
				dataHolder.addItem(player, targetItem);
			}
		}
	}
	
	public boolean isOpen(ActivityRankTypeCfg activityRankTypeCfg) {
		
		long startTime = activityRankTypeCfg.getStartTime();
		long endTime = activityRankTypeCfg.getEndTime();		
		long currentTime = System.currentTimeMillis();
		
		return currentTime < endTime && currentTime >= startTime;
	}

	private void checkCfgVersion(Player player) {
		ActivityRankTypeItemHolder dataHolder = ActivityRankTypeItemHolder.getInstance();
		ActivityRankTypeCfgDAO activityRankTypeCfgDAO = ActivityRankTypeCfgDAO.getInstance();
		List<ActivityRankTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityRankTypeItem targetItem : itemList) {			
			ActivityRankTypeCfg targetCfg = activityRankTypeCfgDAO.getCfgById(targetItem);
			if(targetCfg == null){
				continue;
			}
			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
				targetItem.reset(targetCfg);
				dataHolder.updateItem(player, targetItem);
			}
		}		
	}
	
	private void checkClose(Player player) {
		ActivityRankTypeItemHolder dataHolder = ActivityRankTypeItemHolder.getInstance();
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		List<ActivityRankTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		ActivityRankTypeCfgDAO activityRankTypeCfgDAO = ActivityRankTypeCfgDAO.getInstance();
		for (ActivityRankTypeItem activityRankTypeItem : itemList) {//每种活动
			ActivityRankTypeCfg cfgById = activityRankTypeCfgDAO.getCfgById(activityRankTypeItem.getCfgId());
			if(cfgById == null){
				continue;
			}
			if(!isClose(activityRankTypeItem)){
				continue;			
			}
			if(activityRankTypeItem.isClosed()){
				continue;
			}			
			if(activityRankTypeItem.getReward()!=null){//有奖励的进这里
				//派发；结算时没入榜，结算后不登陆更不会入榜，所以会在此处排除
				activityRankTypeItem.setTaken(true);
				activityRankTypeItem.setClosed(true);					
				dataHolder.updateItem(player, activityRankTypeItem);
				comGiftMgr.addtagInfoTOEmail(player, activityRankTypeItem.getReward(), activityRankTypeItem.getEmailId(), null);
				continue;
			}			
			//没奖的酱油进下边设置关闭
			SendRewardRecord record = sendMap.get(activityRankTypeItem.getEnumId());
			if(record == null){
				continue;					
			}
			long sendtime = record.getLasttime();				
			if(sendtime ==0){
				continue;
			}
			long nowtime = System.currentTimeMillis();
			if(sendtime > nowtime){
				long tmp = sendtime;
				sendtime = nowtime;
				nowtime = tmp;					
			}
			if(DateUtils.getAbsoluteHourDistance(sendtime, nowtime)>1){//设置固定时间后，再生成的奖励也不触发，防止当机；此限制应加在服务器数据表里，现在临时加在内存的静态变量中；
				activityRankTypeItem.setClosed(true);
				dataHolder.updateItem(player, activityRankTypeItem);
			}
			
			
		}	
	}

	
	public boolean isClose(ActivityRankTypeItem activityRankTypeItem) {		
		ActivityRankTypeCfg cfgById = ActivityRankTypeCfgDAO.getInstance().getCfgById(activityRankTypeItem.getCfgId());
		if(cfgById == null){
			return false;
		}
		long endTime = cfgById.getEndTime();		
		long currentTime = System.currentTimeMillis();
		return currentTime > endTime;
	}
	
	public boolean isCanGift(ActivityRankTypeCfg cfg,SendRewardRecord record){
		if(isOpen(cfg)){
			return false;
		}
		if(!StringUtils.equals(record.getId(), cfg.getId())){
			return false;
		}		
		return true;
	}
	
	
	public List<ActivityRankTypeEntry> getRankList(ActivityRankTypeEnum rankType, int offset, int limit){		
		return new ArrayList<ActivityRankTypeEntry>();
	}
	
	public ActivityRankTypeUserInfo getUserInfo(Player player, ActivityRankTypeEnum rankType){		
		return null;
	}
	
	/**定时核查一遍，将排行奖励派发到用户数据库*/
	public void sendGift(){
		if(sendMap.size() == 0){
			creatMap();//
		}
		RankingMgr rankingMgr = RankingMgr.getInstance();
		ActivityRankTypeItemHolder activityRankTypeItemHolder = ActivityRankTypeItemHolder.getInstance();
		PlayerMgr playerMgr = PlayerMgr.getInstance();
		ActivityRankTypeSubCfgDAO activityRankTypeSubCfgDAO = ActivityRankTypeSubCfgDAO.getInstance();
		
		List<ActivityRankTypeCfg> cfgList = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRankTypeCfg cfg:cfgList){//所有的配表活动
			ActivityRankTypeEnum activityRankTypeEnum = ActivityRankTypeEnum.getById(cfg.getEnumId());
			if(activityRankTypeEnum==null){
				continue;
			}
			
			SendRewardRecord record = sendMap.get(cfg.getEnumId());
			if(record.isSend()){
				//已经派发过，避免多次触发
//				System.out.println("activityrank.已经拍法过奖励。。。。。。。。。。。。。。。。"+ cfg.getId());
				continue;
			}
			if(!isCanGift(cfg,record)){
				//是否为同类型活动里处于激活派发状态的一个
//				System.out.println("activityrank.活动还未结束。。。。。。。。。。。。。。。。");
				continue;
			}
			record.setLasttime(System.currentTimeMillis());
			record.setSend(true);
			for(Integer ranktype:activityRankTypeEnum.getRankTypes()){//该配表对应的所有排行榜，比如竞技场就分4个职业				
				RankType rankType = RankType.getRankType(ranktype,1);
				List<RankInfo> rankList = new ArrayList<RankInfo>();
				List<RankingLevelData> tableranklist = rankingMgr.getRankList(rankType, cfg.getRewardNum());
				for (int i = 0; i < tableranklist.size(); i++) {
					RankingLevelData levelData = tableranklist.get(i);
					RankInfo rankInfo = RankingUtils.createOneRankInfo(levelData, i + 1);
					rankList.add(rankInfo);
				}				
				for(RankInfo rankInfo:rankList){//所有的该榜上榜用户
					if(rankInfo.getRankingLevel() > cfg.getRewardNum()){
						//奖励活动有效位数小于当前榜上用户的排名
						continue;
					}
					if(rankInfo.getLevel()<cfg.getLevelLimit()){
						//虽让上了榜，但级别不够不能触发榜对应的活动
						continue;
					}
					ActivityRankTypeItem targetItem = activityRankTypeItemHolder.getItem(rankInfo.getHeroUUID(), activityRankTypeEnum);
					if(targetItem==null){
						//有排行无登录时生成的排行榜活动奖励数据，说明是机器人或活动期间没登陆过
						continue;
					}
					
					Player player = playerMgr.find(rankInfo.getHeroUUID());
					if(player == null){
						//根据排行榜的英雄id找不到用户
						continue;
					}
					
					List<ActivityRankTypeSubCfg> subCfgList = activityRankTypeSubCfgDAO.getByParentCfgId(cfg.getId());
					String tmpReward= null;
					String emaiId = null;
					for(ActivityRankTypeSubCfg subCfg:subCfgList){
						if(rankInfo.getRankingLevel()>=subCfg.getRankRanges()[0]&&rankInfo.getRankingLevel()<=subCfg.getRankRanges()[1]){
							tmpReward = subCfg.getReward();
							emaiId = subCfg.getEmailId();
							break;
						}						
					}
					if(tmpReward !=null){
						targetItem.setReward(tmpReward);
						targetItem.setEmailId(emaiId);
						activityRankTypeItemHolder.updateItem(player, targetItem);
//						System.out.println("activityrank.往个人数据库增加奖励信息" + player.getUserId());
					}
				}			
			}			
		}		
	}


	/**开服第一次触发时，初始化排行榜派奖的id-版本号；后续核实活动过期后，初始化是否派发和派发时间*/
	public void creatMap() {
		ActivityRankTypeCfgDAO activityRankTypeCfgDAO = ActivityRankTypeCfgDAO.getInstance();
		List<ActivityRankTypeCfg> cfgList = activityRankTypeCfgDAO.getAllCfg();
		for(ActivityRankTypeCfg cfg:cfgList){
			if(sendMap.get(cfg.getEnumId()) != null){
				continue;
			}
			ActivityRankTypeCfg modelCfg = activityRankTypeCfgDAO.getCfgByModleCfgEnumId(cfg.getEnumId());
			
			
			SendRewardRecord record = new SendRewardRecord();
			record.setId(modelCfg.getId());
			record.setVersion(modelCfg.getVersion());
			record.setSend(false);
			record.setEnumId(modelCfg.getEnumId());
			sendMap.put(cfg.getEnumId(), record);
		}
	}
	
	/**每一个小时检查一遍配置文件对应的cfglist；如果有新增id或者更高的活动版本号，推进map;此方法可支持热更新，暂时不用*/
	public void changeMap() {
		List<ActivityRankTypeCfg> cfgList = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRankTypeCfg cfg:cfgList){
			if(sendMap.get(cfg.getEnumId()) == null){
				continue;
			}
			if(!isOpen(cfg)){
				continue;
			}
			if(!StringUtils.equals(sendMap.get(cfg.getEnumId()).getVersion(), cfg.getVersion())){
				SendRewardRecord record = new SendRewardRecord();
				record.setId(cfg.getId());
				record.setVersion(cfg.getVersion());
				record.setSend(false);
				record.setEnumId(cfg.getEnumId());
				sendMap.put(cfg.getEnumId(),record );				
			}		
		}		
	}



	@Override
	public void updateRedPoint(Player player, String eNum) {
		ActivityRankTypeItemHolder activityCountTypeItemHolder = new ActivityRankTypeItemHolder();
		
		ActivityRankTypeCfg cfg = ActivityRankTypeCfgDAO.getInstance().getCfgById(eNum);
		if(cfg == null ){
			return;
		}
		ActivityRankTypeEnum rankEnum = ActivityRankTypeEnum.getById(cfg.getEnumId());
		if(rankEnum == null){
			return;
		}
		ActivityRankTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId(),rankEnum);
		if(dataItem == null){
			return;
		}
		if(!dataItem.isTouchRedPoint()){
			dataItem.setTouchRedPoint(true);
			activityCountTypeItemHolder.updateItem(player, dataItem);
		}	
		
	}

	public boolean isOpen(MapItemValidateParam param) {
		List<ActivityRankTypeCfg> list = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRankTypeCfg cfg: list){
			if(isOpen(cfg,param)){
				return true;
			}
		}		
		return false;
	}

	private boolean isOpen(ActivityRankTypeCfg cfg, MapItemValidateParam param) {
		if (cfg != null) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = param.getCurrentTime();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}
	
	
}
