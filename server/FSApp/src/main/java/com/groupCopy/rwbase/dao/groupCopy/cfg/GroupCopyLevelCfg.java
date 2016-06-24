package com.groupCopy.rwbase.dao.groupCopy.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.log.GameLog;
import com.log.LogModule;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCopyLevelCfg {
	
	private String id;
	
	private String chaterID;//所属章节id

	private String drop;
	
	private String roleReward;
	
	private String monsters;
	
	private int readyTime;//准备时间，单位为s
	
	private Map<Integer, String> dropMap;//格式化后的掉落数据
	
	private List<Integer> roleRewardList;//格式化后的个人奖励
	
	private List<String> mIDList;//格式化后的怪物列表
	
	/**
	 * 格式化数据
	 */
	public void formatData(){
		if(dropMap == null){
			dropMap = new HashMap<Integer, String>();
		}
		
		String[] dStr = drop.split(";");
		int k;
		for (String rew : dStr) {
			String[] str = rew.split(",");
			try {
				k = Integer.parseInt(str[0].toString().trim());
				dropMap.put(k, str[1].toString().trim());
			} catch (Exception e) {
				GameLog.error(LogModule.GroupCopy, "GroupCopyLevelCfg[formatData]", "初始化帮派副本关卡数据时出现问题，关卡id:" + id, e);
			}
		}
		
		
		if(roleRewardList == null){
			roleRewardList = new ArrayList<Integer>();
		}
		dStr = roleReward.split(",");
		for (String str : dStr) {
			roleRewardList.add(Integer.parseInt(str));
		}
		
		GroupCopyMapCfg cfg = GroupCopyMapCfgDao.getInstance().getCfgById(chaterID);
		cfg.addLvID(id);
		
		if(mIDList == null){
			mIDList = new ArrayList<String>();
		}
		dStr = monsters.split(",");
		for (String str : dStr) {
			mIDList.add(str);
		}
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChaterID() {
		return chaterID;
	}

	public void setChaterID(String chaterID) {
		this.chaterID = chaterID;
	}

	public String getDrop() {
		return drop;
	}

	public void setDrop(String drop) {
		this.drop = drop;
	}

	public String getRoleReward() {
		return roleReward;
	}

	public void setRoleReward(String roleReward) {
		this.roleReward = roleReward;
	}

	public Map<Integer, String> getDropMap() {
		return Collections.unmodifiableMap(dropMap);
	}

	public List<Integer> getRoleRewardList() {
		return Collections.unmodifiableList(roleRewardList);
	}

	
	public int getReadyTime() {
		return readyTime;
	}

	/**
	 * 获取本关卡的怪物id列表
	 * @return
	 */
	public List<String> getmIDList() {
		return mIDList;
	}

	
	
	
	
}
