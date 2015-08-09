package com.playerdata.activity.rankType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.RankType;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.RankingMgr;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfg;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfgDAO;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeSubCfg;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeSubCfgDAO;
import com.playerdata.activity.rankType.cfg.SendRewardRecord;
import com.playerdata.activity.rankType.data.ActivityRankTypeEntry;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rankType.data.ActivityRankTypeItemHolder;
import com.playerdata.activity.rankType.data.ActivityRankTypeUserInfo;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.ranking.RankingUtils;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwproto.RankServiceProtos.RankInfo;


public class ActivityRankTypeMgr implements ActivityRedPointUpdate{
	
	private static ActivityRankTypeMgr instance = new ActivityRankTypeMgr();
	
	private final static int MAKEUPEMAIL = 10055;
	
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
		List<ActivityRankTypeCfg> allCfgList = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityRankTypeCfg activityRankTypeCfg : allCfgList) {//遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if(isOpen(activityRankTypeCfg)){
				ActivityRankTypeEnum RankTypeEnum = ActivityRankTypeEnum.getById(activityRankTypeCfg.getId());
				if(RankTypeEnum != null){
					ActivityRankTypeItem targetItem = dataHolder.getItem(player.getUserId(), RankTypeEnum);//已在之前生成数据的活动
					if(targetItem == null){
						targetItem = ActivityRankTypeCfgDAO.getInstance().newItem(player, RankTypeEnum);//生成新开启活动的数据
						if(targetItem!=null){
							dataHolder.addItem(player, targetItem);
						}
					}
				}				
			}
		}
	}
	
	private void checkCfgVersion(Player player) {
		ActivityRankTypeItemHolder dataHolder = ActivityRankTypeItemHolder.getInstance();
		List<ActivityRankTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityRankTypeItem targetItem : itemList) {			
			ActivityRankTypeCfg targetCfg = ActivityRankTypeCfgDAO.getInstance().getCfgById(targetItem.getCfgId());
			if(targetCfg == null){
				GameLog.error("activitycounttypemgr", "uid=" + player.getUserId(), "数据库有活动id，但当前配置无该类型");
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
		List<ActivityRankTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		
		for (ActivityRankTypeItem activityRankTypeItem : itemList) {//每种活动
			if(isClose(activityRankTypeItem)){
				if(!activityRankTypeItem.isClosed()&&activityRankTypeItem.getReward()!=null){
					//派发；结算时没入榜，结算后不登陆更不会入榜，所以会在此处排除
					activityRankTypeItem.setTaken(true);
					activityRankTypeItem.setClosed(true);
					dataHolder.updateItem(player, activityRankTypeItem);
					ComGiftMgr.getInstance().addtagInfoTOEmail(player, activityRankTypeItem.getReward(), activityRankTypeItem.getEmailId(), null);
					if(!StringUtils.isBlank(activityRankTypeItem.getFashionReward())){
//						ComGiftMgr.getInstance().addtagoffathionInfoTOEmail(player, activityRankTypeItem.getFashionReward(), activityRankTypeItem.getEmailId(), null);
					}
				}
				SendRewardRecord record = sendMap.get(activityRankTypeItem.getCfgId());
				if(record == null){
					GameLog.error(LogModule.ComActivityRank, player.getUserId(), "数据库数据的id找不到对应的cfg生成的record", null);
					return;					
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
				if(DateUtils.getAbsoluteHourDistance(sendtime, nowtime)>1&&!activityRankTypeItem.isClosed()){//设置固定时间后，再生成的奖励也不触发，防止当机；此限制应加在服务器数据表里，现在临时加在内存的静态变量中；
					activityRankTypeItem.setClosed(true);
					dataHolder.updateItem(player, activityRankTypeItem);
				}				
			}
		}	
	}

	
	public boolean isClose(ActivityRankTypeItem activityRankTypeItem) {
		
		ActivityRankTypeCfg cfgById = ActivityRankTypeCfgDAO.getInstance().getCfgById(activityRankTypeItem.getCfgId());
		boolean isclose = isClose(cfgById);

		return isclose;
	}
	
	public boolean isClose(ActivityRankTypeCfg cfg) {		
		long endTime = cfg.getEndTime();		
		long currentTime = System.currentTimeMillis();
		return currentTime > endTime;
	}
	
	
	
	public boolean isOpen(ActivityRankTypeCfg activityRankTypeCfg) {
		
		long startTime = activityRankTypeCfg.getStartTime();
		long endTime = activityRankTypeCfg.getEndTime();		
		long currentTime = System.currentTimeMillis();
		
		return currentTime < endTime && currentTime > startTime;
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
		
		List<ActivityRankTypeCfg> cfgList = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRankTypeCfg cfg:cfgList){//所有的配表活动
			ActivityRankTypeEnum activityRankTypeEnum = ActivityRankTypeEnum.getById(cfg.getId());
			if(activityRankTypeEnum==null){
				GameLog.error(LogModule.ComActivityRank, null, "配置文件有配，但枚举不存在", null);
				continue;
			}
			if(!isClose(cfg)){
				//活动要等过期后才派发奖励 
//				System.out.println("activityrank.活动还未结束。。。。。。。。。。。。。。。。");
				continue;
			}
			SendRewardRecord record = sendMap.get(cfg.getId());
			if(record.isSend()){
				//已经派发过，避免多次触发
//				System.out.println("activityrank.已经拍法过奖励。。。。。。。。。。。。。。。。"+ cfg.getId());
				continue;
			}
			record.setLasttime(System.currentTimeMillis());
			record.setSend(true);
			GameLog.info(LogModule.ComActivityRank.getName(), null, "开始派发排行榜奖励，cfg名字为 =" + cfg.getId(), null);
			int isrealtime = cfg.getDailyOrRealtime();
			for(Integer ranktype:activityRankTypeEnum.getRankTypes()){//该配表对应的所有排行榜，比如竞技场就分4个职业
				int noitem = 0;
				RankType rankType = RankType.getRankType(ranktype,1);
				List<RankInfo> rankList = new ArrayList<RankInfo>();
				List<RankingLevelData> tableranklist = RankingMgr.getInstance().getRankList(rankType, cfg.getRewardNum());
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
					ActivityRankTypeItemHolder dataHolder = ActivityRankTypeItemHolder.getInstance();
					ActivityRankTypeItem targetItem = dataHolder.getItem(rankInfo.getHeroUUID(), activityRankTypeEnum);
					if(targetItem==null){
						//有排行无登录时生成的排行榜活动奖励数据，说明是机器人或活动期间没登陆过
						noitem++;
//						System.out.println("activityrank.机器人没有数据。。。。。。。。。。。。。。。。名额="+ cfg.getRewardNum() + "  序列="+noitem);
						continue;
					}
					
					Player player = PlayerMgr.getInstance().find(rankInfo.getHeroUUID());
					if(player == null){
						//根据排行榜的英雄id找不到用户
//						System.out.println("activityrank.机器人。。。。。。。。。。。。。。。。名额=");
						continue;
					}
//					if(targetItem.getVersion()!=null&&StringUtils.equals(cfg.getVersion(), targetItem.getVersion())){
//						//已经在之前结算时写入过同版本的奖励；如果发生重启，会有部分用户合法结算时没进榜后来进了榜的用户生成了奖励和版本号
//						break;
//					}
					
					List<ActivityRankTypeSubCfg> subCfgList = ActivityRankTypeSubCfgDAO.getInstance().getByParentCfgId(cfg.getId());
					String tmpReward= null;
					String emaiId = null;
					String tmpfathionReward =null;
					for(ActivityRankTypeSubCfg subCfg:subCfgList){
						if(rankInfo.getRankingLevel()>=subCfg.getRankRanges()[0]&&rankInfo.getRankingLevel()<=subCfg.getRankRanges()[1]){
							tmpReward = subCfg.getReward();
							emaiId = subCfg.getEmailId();
							tmpfathionReward = subCfg.getFashionReward();
							break;
						}						
					}
					if(tmpReward !=null){
						if(tmpfathionReward != null){
							targetItem.setFashionReward(tmpfathionReward);
						}
						targetItem.setReward(tmpReward);
						targetItem.setEmailId(emaiId);
						dataHolder.updateItem(player, targetItem);
//						System.out.println("activityrank.往个人数据库增加奖励信息" + player.getUserId());
					}else{//所有条件都满足，但是cfg的排名范围和subcfg的排名范围不一致
						GameLog.error(LogModule.ComActivityRank, player.getUserId(), "所有条件都满足，但是cfg的排名范围和subcfg的排名范围不一致,排名="+rankInfo.getRankingLevel()+" 活动类型=" + cfg.getId(),null);
					}
				}			
			}			
		}		
	}


	/**开服第一次触发时，初始化排行榜派奖的id-版本号；后续核实活动过期后，初始化是否派发和派发时间*/
	public void creatMap() {
		List<ActivityRankTypeCfg> cfgList = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRankTypeCfg cfg:cfgList){
			SendRewardRecord record = new SendRewardRecord();
			record.setId(cfg.getId());
			record.setVersion(cfg.getVersion());
			record.setSend(false);
			sendMap.put(cfg.getId(), record);
		}		
	}
	
	/**每一个小时检查一遍配置文件对应的cfglist；如果有新增id或者更高的活动版本号，推进map;此方法可支持热更新，暂时不用*/
	private void changeMap() {
		List<ActivityRankTypeCfg> cfgList = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRankTypeCfg cfg:cfgList){
			SendRewardRecord record = new SendRewardRecord();
			record.setId(cfg.getId());
			record.setVersion(cfg.getVersion());
			record.setSend(false);
			if(sendMap.get(record.getId()) == null){
				sendMap.put(cfg.getId(), record);//新增
				continue;
			}				
			if(!StringUtils.equals(sendMap.get(record.getId()).getVersion(), cfg.getVersion())){				
				sendMap.put(cfg.getId(), record);//替换
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
		ActivityRankTypeEnum rankEnum = ActivityRankTypeEnum.getById(eNum);//cfg
		if(rankEnum == null){
			GameLog.error(LogModule.ComActivityRank, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动枚举", null);
			return;
		}
		ActivityRankTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId(),rankEnum);
		if(dataItem == null){
			GameLog.error(LogModule.ComActivityRank, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动数据", null);
			return;
		}
		if(!dataItem.isTouchRedPoint()){
			dataItem.setTouchRedPoint(true);
			activityCountTypeItemHolder.updateItem(player, dataItem);
		}	
		
	}
	
	
}
