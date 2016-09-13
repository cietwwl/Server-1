package com.rwbase.dao.fightinggrowth.pojo;

import java.util.Collections;
import java.util.Map;

public class FSUserFightingGrowthTitleCfg {

	private String key;
	private String fightingTitle; // 称号
	private String fightingIcon; // 图标
	private int fightingRequired; // 需要的战斗力
	private String itemRequired; // 晋级需要的道具（原生的字符串）
	private String rewards; // 晋级奖励（原生的字符串）
	private String emailCfgIdOfReward; // 奖励
	private String privilege; // 特权
	private String nextTitleKey; // 下一个title的key
	private boolean isFirst; // 是否第一个title
	private Map<Integer, Integer> _itemRequiredMap; // 通过itemRequired解析过来的{key=道具的cfgId，value=需求的数量}
	private Map<Integer, Integer> _itemRewardMap; // 通过rewards解析过来的{key=道具的cfgId，value=需求的数量}
	
	public void setItemRequiredMap(Map<Integer, Integer> map) {
		this._itemRequiredMap = Collections.unmodifiableMap(map);
	}
	
	public void setItemRewardMap(Map<Integer, Integer> map) {
		this._itemRewardMap = Collections.unmodifiableMap(map);
	}
	
	public String getKey() {
		return key;
	}
	
	public String getFightingTitle() {
		return fightingTitle;
	}
	
	public String getFightingIcon() {
		return fightingIcon;
	}
	
	public int getFightingRequired() {
		return fightingRequired;
	}
	
	public String getNextTitleKey() {
		return nextTitleKey;
	}
	
	public boolean isFirst() {
		return this.isFirst;
	}
	
	/**
	 * 
	 * 获取晋级要求的原生字符串（类似以下这种格式：806501,1）
	 * 
	 * @return
	 */
	public String getItemRequired() {
		return itemRequired;
	}
	
	/**
	 * 
	 * 获取晋级要求的道具列表（key=道具的cfgId，value=需求的数量）
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getItemRequiredMap() {
		return _itemRequiredMap;
	}
	
	/**
	 * 
	 * 获取晋级奖励的原生字符串（类似以下这种格式：806501,1）
	 * 
	 * @return
	 */
	public String getRewards() {
		return rewards;
	}
	
	/**
	 * 
	 * 获取晋级奖励的道具列表（key=道具的cfgId，value=需求的数量）
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getItemRewardMap() {
		return _itemRewardMap;
	}
	
	public String getEmailCfgIdOfReward() {
		return emailCfgIdOfReward;
	}
	
	/**
	 * 
	 * 获取特权
	 * 
	 * @return
	 */
	public String getPrivilege() {
		return privilege;
	}
}