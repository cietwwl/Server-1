package com.rwbase.dao.group.pojo.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;

/*
 * @author HC
 * @date 2016年1月19日 上午11:58:12
 * @Description 成员的帮派数据信息
 */
@SynClass
@Table(name = "user_group_attribute")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGroupAttributeData implements UserGroupAttributeDataIF, IMapItem {
	@Id
	private String userId;// 角色Id
	private String groupId;// 角色加入帮派的Id，如果没有帮派的时候是空
	private long quitGroupTime;// 退出帮派的时间，包括被踢出帮派的时间
	private long sendEmailTime;// 发送邮件的时间
	@IgnoreSynField
	private int groupApplySize;// 今天已经申请的帮派数量
	@IgnoreSynField
	private long lastResetApplyTime;// 上次重置申请队列长度的时间
	@SaveAsJson
	private Map<Integer, GroupSkillItem> studySkill;// 已经学习了的帮派技能列表
	@SaveAsJson
	private List<String> applyGroupIdList;// 申请的列表
	private int contribution;// 个人贡献
	@IgnoreSynField
	private int donateTimes;// 捐献的次数
	@IgnoreSynField
	private long lastDonateTime;// 捐献的时间

	// ---------------------------------------扩展的内容
	@CombineSave
	@IgnoreSynField
	private List<String> prayList;// 已经给谁送过卡片
	@CombineSave
	@IgnoreSynField
	private long lastPrayTime;// 上次祈福的时间
	@CombineSave
	@IgnoreSynField
	private int state;// 当前状态，0：未领取到，1：完成并领了奖励了

	// ---------------------------------------内存缓存
	@NonSave
	private String groupName;// 帮派名字
	@NonSave
	private long joinTime;// 获取距离可以捐献还剩余多少时间
	@NonSave
	private int dayContribution;// 当天从令牌捐献获取的帮派贡献
	@NonSave
	private volatile boolean isInit;// 是否初始化了

	public UserGroupAttributeData() {
		studySkill = new HashMap<Integer, GroupSkillItem>();
		applyGroupIdList = new ArrayList<String>();
	}

	// ///////////////////////////////////////////////GET区域
	/**
	 * 获取角色的Id
	 * 
	 * @return
	 */
	@Override
	public String getId() {
		return userId;
	}

	/**
	 * 获取加入帮派的Id
	 * 
	 * @return
	 */
	@Override
	public String getGroupId() {
		return groupId;
	}

	/**
	 * 获取退出帮派的时间
	 * 
	 * @return
	 */
	@Override
	public long getQuitGroupTime() {
		return quitGroupTime;
	}

	/**
	 * 获取发送邮件的时间
	 * 
	 * @return
	 */
	@Override
	public long getSendEmailTime() {
		return sendEmailTime;
	}

	/**
	 * 获取已经申请的帮派的Id列表
	 * 
	 * @return
	 */
	@Override
	public List<String> getApplyGroupIdList() {
		return applyGroupIdList == null || applyGroupIdList.isEmpty() ? new ArrayList<String>() : new ArrayList<String>(applyGroupIdList);
	}

	/**
	 * 获取今天已经申请的帮派次数
	 * 
	 * @return
	 */
	@Override
	public int getGroupApplySize() {
		return groupApplySize;
	}

	/**
	 * 获取上次请求重置申请帮派数量的时间
	 * 
	 * @return
	 */
	@Override
	public long getLastResetApplyTime() {
		return lastResetApplyTime;
	}

	/**
	 * 获取个人学习帮派技能的数据
	 * 
	 * @return
	 */
	public List<GroupSkillItem> getSkillItemList() {
		return new ArrayList<GroupSkillItem>(studySkill.values());
	}

	/**
	 * 获取帮派名字
	 * 
	 * @return
	 */
	@JsonIgnore
	public String getGroupName() {
		return groupName;
	}

	/**
	 * 获取距离可以捐献还剩余多少时间
	 * 
	 * @return
	 */
	@JsonIgnore
	public long getJoinTime() {
		return joinTime;
	}

	public int getDayContribution() {
		return dayContribution;
	}

	public boolean isInit() {
		return isInit;
	}

	// ///////////////////////////////////////////////SET区域
	/**
	 * 设置角色Id
	 * 
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 设置帮派的Id
	 * 
	 * @param groupId
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * 设置退出帮派的时间
	 * 
	 * @param quitGroupTime
	 */
	public void setQuitGroupTime(long quitGroupTime) {
		this.quitGroupTime = quitGroupTime;
	}

	/**
	 * 设置发送帮派邮件的时间
	 * 
	 * @param sendEmailTime
	 */
	public void setSendEmailTime(long sendEmailTime) {
		this.sendEmailTime = sendEmailTime;
	}

	/**
	 * 设置当前已经申请的帮派列表
	 * 
	 * @param applyGroupIdList
	 */
	public void setApplyGroupIdList(List<String> applyGroupIdList) {
		this.applyGroupIdList = applyGroupIdList;
	}

	/**
	 * 设置当天已经申请的帮派个数
	 */
	public void setGroupApplySize(int groupApplySize) {
		this.groupApplySize = groupApplySize;
	}

	/**
	 * 设置上次重置申请帮派数量的时间
	 * 
	 * @param lastResetApplyTime
	 */
	public void setLastResetApplyTime(long lastResetApplyTime) {
		this.lastResetApplyTime = lastResetApplyTime;
	}

	/**
	 * 设置帮派名字
	 * 
	 * @param groupName
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * 设置个人的贡献值
	 * 
	 * @param contribution
	 */
	public void setContribution(int contribution) {
		this.contribution = contribution;
	}

	/**
	 * 获取个人帮贡
	 * 
	 * @return
	 */
	public int getContribution() {
		return contribution;
	}

	/**
	 * 设置进入帮派的时间
	 * 
	 * @param joinTime
	 */
	public void setJoinTime(long joinTime) {
		this.joinTime = joinTime;
	}

	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}

	// ///////////////////////////////////////////////逻辑处理区域
	/**
	 * 增加申请的帮派Id
	 * 
	 * @param applyGroupId
	 */
	public void addApplyGroupId(String applyGroupId) {
		if (applyGroupIdList == null) {
			applyGroupIdList = new ArrayList<String>();
		}

		applyGroupIdList.add(applyGroupId);
	}

	/**
	 * 当被通过之后就删除已经申请的帮派Id列表中的数据
	 * 
	 * @param applyGroupId
	 */
	public void removeApplyGroupId(String applyGroupId) {
		if (applyGroupIdList == null || applyGroupIdList.isEmpty()) {
			return;
		}

		applyGroupIdList.remove(applyGroupId);
	}

	/**
	 * 清除所有请求的帮派信息
	 */
	public void clearApplyGroupIdList() {
		if (applyGroupIdList == null || applyGroupIdList.isEmpty()) {
			return;
		}

		applyGroupIdList.clear();
	}

	/**
	 * 是否有学习过当前这个技能
	 * 
	 * @param skillId 技能的Id
	 * @param skillLevel 技能的等级
	 * @return
	 */
	public boolean hasStudySkill(int skillId, int skillLevel) {
		GroupSkillItem hasValue = studySkill.get(skillId);
		return hasValue == null ? false : hasValue.getLevel() >= skillLevel;
	}

	/**
	 * 增加或者更新新学习的技能数据
	 * 
	 * @param skillId
	 * @param skillLevel
	 * @param time <b>【注】如果不设置这个数据，就填入-1</b>
	 * @param state <b>【注】如果不设置这个数据，就填入-1</b>
	 */
	public void addOrUpdateStudySkill(int skillId, int skillLevel, long time, int state) {
		GroupSkillItem skillItem = studySkill.get(skillId);
		if (skillItem == null) {
			skillItem = new GroupSkillItem();
			skillItem.setId(skillId);
			studySkill.put(skillId, skillItem);
		}

		skillItem.setLevel(skillLevel);
		if (time != -1) {
			skillItem.setTime(time);
		}

		if (state != -1) {
			skillItem.setState(state);
		}
	}

	/**
	 * 获取已经学习的帮派技能等级
	 * 
	 * @param skillId
	 * @return
	 */
	public int getStudySkillLevel(int skillId) {
		GroupSkillItem hasValue = studySkill.get(skillId);
		return hasValue == null ? 0 : hasValue.getLevel();
	}

	/**
	 * 获取已经学习过的帮派技能数据
	 * 
	 * @return
	 */
	public Map<Integer, GroupSkillItem> getStudySkillMap() {
		return new HashMap<Integer, GroupSkillItem>(studySkill);
	}

	/**
	 * 返回是否已经申请该帮派
	 * 
	 * @param groupId
	 * @return
	 */
	@Override
	public boolean hasApplyGroup(String groupId) {
		if (applyGroupIdList == null) {
			return false;
		}
		return applyGroupIdList.contains(groupId);
	}

	/**
	 * 检查是否有学习过技能
	 * 
	 * @return
	 */
	public boolean hasStudySkill() {
		return !studySkill.isEmpty();
	}

	public void setDayContribution(int dayContribution) {
		this.dayContribution = dayContribution;
	}

	public int getDonateTimes() {
		return donateTimes;
	}

	public void setDonateTimes(int donateTimes) {
		this.donateTimes = donateTimes;
	}

	public long getLastDonateTime() {
		return lastDonateTime;
	}

	public void setLastDonateTime(long lastDonateTime) {
		this.lastDonateTime = lastDonateTime;
	}
}