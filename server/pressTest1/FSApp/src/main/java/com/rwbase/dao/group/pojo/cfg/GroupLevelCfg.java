package com.rwbase.dao.group.pojo.cfg;

/*
 * @author HC
 * @date 2016年1月18日 上午10:24:11
 * @Description 
 */
public class GroupLevelCfg {
	private int level;// 帮派等级
	private int maxMemberLimit;// 最大成员数量
	private int assistantGroupLeaderLimit;// 副帮主数量上限
	private int officialLimit;// 官员的数量上限
	private int needExp;// 升级需要的经验值

	/**
	 * 获取帮派的等级
	 * 
	 * @return
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * 获取帮派成员的上限
	 * 
	 * @return
	 */
	public int getMaxMemberLimit() {
		return maxMemberLimit;
	}

	/**
	 * 获取帮派中副帮主数量的上限
	 * 
	 * @return
	 */
	public int getAssistantGroupLeaderLimit() {
		return assistantGroupLeaderLimit;
	}

	/**
	 * 获取帮派中官员的数量的上限
	 * 
	 * @return
	 */
	public int getOfficialLimit() {
		return officialLimit;
	}

	/**
	 * 获取升级需要的经验值
	 * 
	 * @return
	 */
	public int getNeedExp() {
		return needExp;
	}
}