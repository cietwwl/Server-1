package com.rwbase.dao.randomBoss.cfg;

import java.util.HashMap;
import java.util.Map;


/**
 * 随机boss配置
 * @author Alex
 * 2016年9月7日 下午4:40:47
 */
public class RandomBossCfg {

	//怪物id
	private String id;
	//触发等级
	private String Playerlevel;
	
	//等级上限
	private int upperLv;
	//等级下限
	private int lowerLv;
	//每人可讨伐次数
	private int crusadeNum;
	
	private String battleReward;
	
	private String findReward;
	
	private String killReward;
	
	//存在时间s
	private int existTime;
	
	private Map<Integer,Integer> battleRewardMap = new HashMap<Integer, Integer>();
	private Map<Integer,Integer> findRewardMap = new HashMap<Integer, Integer>();
	private Map<Integer,Integer> KillRewardMap = new HashMap<Integer, Integer>();
	
	
	
	public void format(){
		String[] str = Playerlevel.split("_");
		if(str.length < 2){
			new RuntimeException("检查随机boss怪物配置表发现怪物["+id+"]触发等级数据不正确：" + Playerlevel);
			return;
		}
		
		upperLv = Integer.parseInt(str[1].trim());
		lowerLv = Integer.parseInt(str[0].trim());
		
		
		str = battleReward.split(",");
		for (int i = 0; i < str.length; i++) {
			String[] subStr = str[i].split("_");
			battleRewardMap.put(Integer.parseInt(subStr[0].trim()), Integer.parseInt(subStr[1].trim()));
		}
		
		
		
		str = findReward.split(",");
		for (int i = 0; i < str.length; i++) {
			String[] subStr = str[i].split("_");
			findRewardMap.put(Integer.parseInt(subStr[0].trim()), Integer.parseInt(subStr[1].trim()));
		}
		
		str = killReward.split(",");
		for (int i = 0; i < str.length; i++) {
			String[] subStr = str[i].split("_");
			KillRewardMap.put(Integer.parseInt(subStr[0].trim()), Integer.parseInt(subStr[1].trim()));
		}
		
		
		Playerlevel = null;
		battleReward = null;
		findReward = null;
		killReward = null;
	}



	public String getId() {
		return id;
	}



	public int getUpperLv() {
		return upperLv;
	}



	public int getLowerLv() {
		return lowerLv;
	}



	public int getCrusadeNum() {
		return crusadeNum;
	}



	public int getExistTime() {
		return existTime;
	}



	public Map<Integer, Integer> getBattleRewardMap() {
		return battleRewardMap;
	}



	public Map<Integer, Integer> getFindRewardMap() {
		return findRewardMap;
	}



	public Map<Integer, Integer> getKillRewardMap() {
		return KillRewardMap;
	}
	
	
	
	
	
}
