package com.rwbase.dao.fightgrowth.pojo;

import java.util.Collections;
import java.util.Map;

public class FSUserFightingGrowthTitleCfg {

	private String key;
	private String fightingTitle; // 称号
	private String fightingIcon; // 图标
	private int fightingRequired; // 需要的战斗力
	private String itemRequired; // 晋级需要的道具（原生的字符串）
	private String rewards; // 奖励
	private String privilege; // 特权
	private String nextTitleKey; // 下一个title的key
	private boolean isFirst; // 是否第一个title
	private Map<String, Integer> _itemRequiredMap; // 通过itemRequired解析过来的
	private Map<String, Integer> _rewardItemsMap; // 通过reward解析过来的
	
	public void setItemRequiredMap(Map<String, Integer> map) {
		this._itemRequiredMap = Collections.unmodifiableMap(map);
	}
	
	public void setRewardItemsMap(Map<String, Integer> map) {
		this._rewardItemsMap = Collections.unmodifiableMap(map);
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
	 * 获取晋级要求的道具列表
	 * 
	 * @return
	 */
	public Map<String, Integer> getItemRequiredMap() {
		return _itemRequiredMap;
	}
	
	/**
	 * 获取晋级奖励的原生字符串（类似以下这种格式：806501,1）
	 * 
	 * @return
	 */
	public String getRewards() {
		return rewards;
	}
	
	/**
	 * 
	 * 获取奖励的道具列表
	 * 
	 * @return
	 */
	public Map<String, Integer> getRewardItemsMap() {
		return _rewardItemsMap;
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
