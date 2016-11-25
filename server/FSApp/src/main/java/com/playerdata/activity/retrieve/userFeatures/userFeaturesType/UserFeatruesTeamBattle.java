package com.playerdata.activity.retrieve.userFeatures.userFeaturesType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.retrieve.cfg.NormalRewardsCfg;
import com.playerdata.activity.retrieve.cfg.PerfectRewardsCfg;
import com.playerdata.activity.retrieve.cfg.RewardBackCfg;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.data.TeamBattleRecord;
import com.playerdata.activity.retrieve.userFeatures.IUserFeatruesHandler;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.playerdata.teambattle.cfg.TeamCfg;
import com.playerdata.teambattle.cfg.TeamCfgDAO;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class UserFeatruesTeamBattle implements	IUserFeatruesHandler{
	
	public static final int[] idArr = {170101,170201,170301,170401,170501,170601};
	
	@Override
	public RewardBackTodaySubItem doEvent() {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem.setId(UserFeaturesEnum.teamBattle.getId());
		subItem.setCount(0);		
		int tmp = 0;
		subItem.setMaxCount(tmp);
		HashMap<Integer, TeamBattleRecord> map = new HashMap<Integer, TeamBattleRecord>();
		for(Integer id : idArr){
			TeamBattleRecord record = new TeamBattleRecord();
			record.setId(id);
			record.setMaxCount(2);
			record.setCount(0);
			map.put(id, record);
		}
		subItem.setTeambattleCountMap(map);
		return subItem;
	}

	@Override
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem, Player player, CfgOpenLevelLimitDAO dao) {
		if(!dao.isOpen(eOpenLevelType.TEAM_BATTLE, player)){
			todaySubItem.setMaxCount(0);
			return null;
		}
		int tmp = 0;
		List<TeamCfg> teamList = TeamCfgDAO.getInstance().getAllCfg();
		int level = player.getLevel();
		for(TeamCfg cfg : teamList){
			if(level >= cfg.getLevel())tmp += cfg.getTimes();
		}
		todaySubItem.setMaxCount(tmp);	
		HashMap<Integer, TeamBattleRecord> map = todaySubItem.getTeambattleCountMap();
		for(Map.Entry<Integer, TeamBattleRecord> entry: map.entrySet()){
			TeamBattleRecord record = new TeamBattleRecord();
			TeamCfg tmpCfg = null;
			for(TeamCfg cfg : teamList){
				if(Integer.parseInt(cfg.getId()) == entry.getKey()){
					tmpCfg = cfg;
					break;
				}
			}
			if(tmpCfg == null){
				continue;
			}
			if(tmpCfg.getLevel() <= level){
				record.setMaxCount(2);
			}else{
				record.setMaxCount(0);
			}
			record.setId(entry.getValue().getId());
			
			record.setCount(entry.getValue().getCount());
			map.put(entry.getValue().getId(), record);
		}
		
		
		return null;
	}

	@Override
	public String getNorReward(NormalRewardsCfg cfg,RewardBackSubItem subItem) {
		HashMap<Integer, TeamBattleRecord> map = subItem.getTeambattleCountMap();
		HashMap<Integer, Integer> rewardHashMap  = new HashMap<Integer, Integer>();
		for(Map.Entry<Integer, TeamBattleRecord> entry: map.entrySet()){
			boolean isCan = entry.getValue().getCount() <entry.getValue().getMaxCount()?true:false;
			if(entry.getKey() == idArr[0]&&isCan){
				add(rewardHashMap,cfg.getXinmo1NorRewards());
			}
			if(entry.getKey() == idArr[1]&&isCan){
				add(rewardHashMap,cfg.getXinmo2NorRewards());
			}
			if(entry.getKey() == idArr[2]&&isCan){
				add(rewardHashMap,cfg.getXinmo3NorRewards());
			}
			if(entry.getKey() == idArr[3]&&isCan){
				add(rewardHashMap,cfg.getXinmo4NorRewards());
			}
			if(entry.getKey() == idArr[4]&&isCan){
				add(rewardHashMap,cfg.getXinmo5NorRewards());
			}
			if(entry.getKey() == idArr[5]&&isCan){
				add(rewardHashMap,cfg.getXinmo6NorRewards());
			}
		}
		String reward = toReward(rewardHashMap);
		return reward;
	}

	private void add(HashMap<Integer, Integer> rewardHashMap, String xinmo1NorRewards) {
		String[] rewards = xinmo1NorRewards.split(";");
		for(String reward : rewards){
			String[] idAndCount = reward.split(":");
			int id = Integer.parseInt(idAndCount[0]);
			int count = Integer.parseInt(idAndCount[1]);
			if(rewardHashMap.get(id)==null){
				rewardHashMap.put(id, count);
			}else{
				rewardHashMap.put(id, rewardHashMap.get(id)+count);				
			}			
		}		
	}

	@Override
	public String getPerReward(PerfectRewardsCfg cfg,RewardBackSubItem subItem) {
		HashMap<Integer, TeamBattleRecord> map = subItem.getTeambattleCountMap();
		HashMap<Integer, Integer> rewardHashMap  = new HashMap<Integer, Integer>();
		for(Map.Entry<Integer, TeamBattleRecord> entry: map.entrySet()){
			boolean isCan = entry.getValue().getCount() <entry.getValue().getMaxCount()?true:false;
			if(entry.getKey() == idArr[0]&&isCan){
				add(rewardHashMap,cfg.getXinmo1PerRewards());
			}
			if(entry.getKey() == idArr[1]&&isCan){
				add(rewardHashMap,cfg.getXinmo2PerRewards());
			}
			if(entry.getKey() == idArr[2]&&isCan){
				add(rewardHashMap,cfg.getXinmo3PerRewards());
			}
			if(entry.getKey() == idArr[3]&&isCan){
				add(rewardHashMap,cfg.getXinmo4PerRewards());
			}
			if(entry.getKey() == idArr[4]&&isCan){
				add(rewardHashMap,cfg.getXinmo5PerRewards());
			}
			if(entry.getKey() == idArr[5]&&isCan){
				add(rewardHashMap,cfg.getXinmo6PerRewards());
			}
		}
		String reward = toReward(rewardHashMap);
		return reward;
	}
	
	
	private String toReward(HashMap<Integer, Integer> rewardHashMap) {
		StringBuilder str = new StringBuilder();
		int count = 0;
		for(Map.Entry<Integer, Integer> entry: rewardHashMap.entrySet()){			
			if(count == rewardHashMap.size()-1){
				str.append(entry.getKey()).append(":").append(entry.getValue());
			}else{
				str.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
			}
			
			count ++;
		}
		return str.toString();
	}

	@Override
	public int getNorCost(NormalRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		int cost = 0;
		HashMap<Integer, TeamBattleRecord> map = subItem.getTeambattleCountMap();
		for(Map.Entry<Integer, TeamBattleRecord> entry: map.entrySet()){
			boolean isCan = entry.getValue().getCount() <entry.getValue().getMaxCount()?true:false;
			if(entry.getKey() == idArr[0]&&isCan){
				cost += cfg.getXinmo1NorCost();
			}
			if(entry.getKey() == idArr[1]&&isCan){
				cost += cfg.getXinmo2NorCost();
			}
			if(entry.getKey() == idArr[2]&&isCan){
				cost += cfg.getXinmo3NorCost();
			}
			if(entry.getKey() == idArr[3]&&isCan){
				cost += cfg.getXinmo4NorCost();
			}
			if(entry.getKey() == idArr[4]&&isCan){
				cost += cfg.getXinmo5NorCost();
			}
			if(entry.getKey() == idArr[5]&&isCan){
				cost += cfg.getXinmo6NorCost();
			}
		}		
		return cost;
	}

	@Override
	public int getPerCost(PerfectRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		int cost = 0;
		HashMap<Integer, TeamBattleRecord> map = subItem.getTeambattleCountMap();
		for(Map.Entry<Integer, TeamBattleRecord> entry: map.entrySet()){
			boolean isCan = entry.getValue().getCount() <entry.getValue().getMaxCount()?true:false;
			if(entry.getKey() == idArr[0]&&isCan){
				cost += cfg.getXinmo1PerCost();
			}
			if(entry.getKey() == idArr[1]&&isCan){
				cost += cfg.getXinmo2PerCost();
			}
			if(entry.getKey() == idArr[2]&&isCan){
				cost += cfg.getXinmo3PerCost();
			}
			if(entry.getKey() == idArr[3]&&isCan){
				cost += cfg.getXinmo4PerCost();
			}
			if(entry.getKey() == idArr[4]&&isCan){
				cost += cfg.getXinmo5PerCost();
			}
			if(entry.getKey() == idArr[5]&&isCan){
				cost += cfg.getXinmo6PerCost();
			}
		}		
		return cost;
	}

}
