package com.rwbase.dao.group.pojo.readonly;

import java.util.Enumeration;

import com.rwbase.dao.groupSkill.db.GroupSkillItem;

/*
 * @author HC
 * @date 2016年2月23日 上午10:12:39
 * @Description 帮派基础信息的数据的接口
 */
public interface GroupBaseDataIF {
	/**
	 * 获取唯一的Id
	 */
	public String getGroupId();

	/**
	 * 获取创建帮派的Id
	 * 
	 * @return
	 */
	public String getCreateUserId();

	/**
	 * 获取创建帮派的时间
	 * 
	 * @return
	 */
	public long getCreateTime();

	/**
	 * 获取当前帮派的等级
	 * 
	 * @return
	 */
	public int getGroupLevel();

	/**
	 * 获取当前的帮派经验
	 * 
	 * @return
	 */
	public int getGroupExp();

	/**
	 * 获取当前帮派的状态
	 * 
	 * @return
	 */
	public int getGroupState();

	/**
	 * 获取当前帮派的名字
	 * 
	 * @return
	 */
	public String getGroupName();

	/**
	 * 获取当前帮派的图标
	 * 
	 * @return
	 */
	public String getIconId();

	/**
	 * 获取帮派当前的物资
	 * 
	 * @return
	 */
	public int getSupplies();

	/**
	 * 获取帮派的公告
	 * 
	 * @return
	 */
	public String getAnnouncement();

	/**
	 * 获取帮派的宣言
	 * 
	 * @return
	 */
	public String getDeclaration();

	/**
	 * 获取帮派的验证类型
	 * 
	 * @return
	 */
	public int getValidateType();

	/**
	 * 获取帮派申请的最低等级
	 * 
	 * @return
	 */
	public int getApplyLevel();

	/**
	 * 获取达成当前帮派等级的时间
	 * 
	 * @return
	 */
	public long getToLevelTime();

	/**
	 * 获取帮派请求解散的时间
	 * 
	 * @return
	 */
	public long getDismissTime();

	/**
	 * 获取所有研发的技能
	 * 
	 * @return
	 */
	public Enumeration<GroupSkillItem> getResearchSkill();
}