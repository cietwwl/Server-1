package com.groupCopy.rwbase.dao.groupCopy.cfg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.log.GameLog;
import com.log.LogModule;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCopyMapCfg {
	
    private String id; //副本地图ID...
    private String name; //名称...
    private int unLockLv; //解锁等级...
    private int openCost; //开启消耗...
    private int extraRewardTime; //额外奖励时限，单位小时
    private int extraReward;//额外奖励
    private String damageExtraReward; //伤害额外奖励...
    private int passReward; //通关奖励...
    private Map<Integer, Integer> extRewMap = new HashMap<Integer, Integer>();//格式化后的伤害额外奖励集合
    private Set<String> lvList = new HashSet<String>();
    private int enterCount;//每天进入次数
    
    
    public void formatData(){
    	if(extRewMap == null){
    		extRewMap = new HashMap<Integer, Integer>();
    	}
    	String[] rews = damageExtraReward.split(",");
    	int k,v;
    	for (String rew : rews) {
			String[] str = rew.split("~");
			try {
				k = Integer.valueOf(str[0].toString().trim());
				v = Integer.valueOf(str[1].toString().trim());
				extRewMap.put(k, v);
			} catch (Exception e) {
				GameLog.error(LogModule.GroupCopy, "GroupCopyMapCfg[formatData]", "初始化帮派副本章节数据时出现问题，章节id:" + id, e);
				e.printStackTrace();
			}
		}
    	
    }

    public void addLvID(String lvID){
    	lvList.add(lvID);
    }
    
	public Set<String> getLvList() {
		return lvList;
	}



	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}


	public int getLevel() {
		return unLockLv;
	}


	public int getOpenCost() {
		return openCost;
	}


	public int getTimeExtraReward() {
		return extraRewardTime;
	}


	public int getUnLockLv() {
		return unLockLv;
	}


	public int getExtraRewardTime() {
		return extraRewardTime;
	}


	public int getExtraReward() {
		return extraReward;
	}


	public String getDamageExtraReward() {
		return damageExtraReward;
	}


	public int getPassReward() {
		return passReward;
	}

	public int getEnterCount() {
		return enterCount;
	}


    
}
