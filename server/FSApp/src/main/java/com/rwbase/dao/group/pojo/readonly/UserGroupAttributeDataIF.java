package com.rwbase.dao.group.pojo.readonly;

import java.util.List;

import com.rwbase.dao.group.pojo.db.GroupSkillItem;

/*
 * @author HC
 * @date 2016年2月23日 下午2:48:58
 * @Description 个人帮派的数据
 */
public interface UserGroupAttributeDataIF {
	/**
	 * 获取加入帮派的Id
	 * 
	 * @return
	 */
	public String getGroupId();

	/**
	 * 获取退出帮派的时间
	 * 
	 * @return
	 */
	public long getQuitGroupTime();

	/**
	 * 获取发送邮件的时间
	 * 
	 * @return
	 */
	public long getSendEmailTime();

	/**
	 * 获取已经申请的帮派的Id列表
	 * 
	 * @return
	 */
	public List<String> getApplyGroupIdList();

	/**
	 * 获取今天已经申请的帮派次数
	 * 
	 * @return
	 */
	public int getGroupApplySize();

	/**
	 * 获取上次请求重置申请帮派数量的时间
	 * 
	 * @return
	 */
	public long getLastResetApplyTime();

	/**
	 * 返回是否已经申请该帮派
	 * 
	 * @param groupId
	 * @return
	 */
	public boolean hasApplyGroup(String groupId);

	/**
	 * 获取帮派名字
	 * 
	 * @return
	 */
	public String getGroupName();

	/**
	 * 获取加入帮派的时间
	 * 
	 * @return
	 */
	public long getJoinTime();

	/**
	 * 获取已经学习的帮派技能
	 * 
	 * @return
	 */
	public List<GroupSkillItem> getSkillItemList();

	/**
	 * 获取技能已经学习的等级
	 * 
	 * @param skillId
	 * @return
	 */
	public int getStudySkillLevel(int skillId);

	/**
	 * 获取当前个人的贡献
	 * 
	 * @return
	 */
	public int getContribution();

	/**
	 * 获取捐献的次数
	 * 
	 * @return
	 */
	public int getDonateTimes();

	/**
	 * 获取上次捐献的次数
	 * 
	 * @return
	 */
	public long getLastDonateTime();
}