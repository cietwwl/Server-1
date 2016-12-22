package com.rwbase.dao.group.pojo.db;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.ArmyFashion;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;

/*
 * @author HC
 * @date 2016年1月18日 下午2:06:23
 * @Description 帮派成员信息
 */
@SynClass
@Table(name = "group_member")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupMemberData implements GroupMemberDataIF, IMapItem {
	@IgnoreSynField
	@Id
	private String id;// Id
	private String userId;// 对应的角色Id
	@IgnoreSynField
	private String groupId;// 帮派的Id
	private int post;// 帮派中的职位<byte>
	private long logoutTime;// 登出的时间
	// ///////////////////////////////////////成员基础信息
	private String name;// 成员名字
	private int level;// 成员的等级<short>
	private String headId;// 头像的Id
	private String headbox;// 头像框
	private int vipLevel;// 成员的Vip等级<byte>
	@IgnoreSynField
	private int job;// 成员的职业<byte>
	private int contribution;// 个人的贡献
	private String templateId = "";// 成员的模版Id
	// //////////////////////////////////////申请帮派时的数据
	private int fighting;// 战斗力
	private long applyTime;// 申请加入帮派的时间
	private long receiveTime;// 接受加入帮派的时间
	private int totalContribution;// 帮派个人总贡献
	@IgnoreSynField
	private int dayContribution;// 当天捐献的数量
	private int allotRewardCount;// 每天分配奖励次数 非管理员则为0
	@SaveAsJson
	private ArmyFashion armyFashion; // 时装

	// -----------------------------------成员的扩展数据
	@CombineSave
	@IgnoreSynField
	private int prayProcess;// 祈福的进度
	@CombineSave
	@IgnoreSynField
	private int prayCardId;// 祈福的卡Id

	// ////////////////////////////////////////////GET区域
	/**
	 * 获取成员的Id，实际上是角色的Id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * 对应的角色Id
	 *
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 获取成员所属的帮派Id
	 * 
	 * @return
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * 获取成员在帮派中的职位
	 * 
	 * @return
	 */
	public int getPost() {
		return post;
	}

	/**
	 * 获取成员最近登出游戏的时间
	 * 
	 * @return
	 */
	public long getLogoutTime() {
		return logoutTime;
	}

	/**
	 * 获取成员的名字
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获取成员的等级
	 * 
	 * @return
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * 获取成员的头像Id
	 * 
	 * @return
	 */
	public String getHeadId() {
		return headId;
	}

	/**
	 * 获取成员的Vip等级
	 * 
	 * @return
	 */
	public int getVipLevel() {
		return vipLevel;
	}

	/**
	 * 获取成员的职业
	 * 
	 * @return
	 */
	public int getJob() {
		return job;
	}

	/**
	 * 获取个人的贡献
	 *
	 * @return
	 */
	public int getContribution() {
		return contribution;
	}

	/**
	 * 获取成员请求时的战斗力
	 * 
	 * @return
	 */
	public int getFighting() {
		return fighting;
	}

	/**
	 * 获取成员的申请时间
	 * 
	 * @return
	 */
	public long getApplyTime() {
		return applyTime;
	}

	/**
	 * 获取成员被接收的时间
	 * 
	 * @return
	 */
	public long getReceiveTime() {
		return receiveTime;
	}

	/**
	 * 获取成员的模版Id
	 * 
	 * @return
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * 获取帮派个人总贡献
	 * 
	 * @return
	 */
	public int getTotalContribution() {
		return totalContribution;
	}

	public int getDayContribution() {
		return dayContribution;
	}

	public ArmyFashion getArmyFashion() {
		return armyFashion;
	}

	/**
	 * 获取祈福的进度
	 * 
	 * @return
	 */
	public int getPrayProcess() {
		return prayProcess;
	}

	/**
	 * 获取祈福的卡Id
	 * 
	 * @return
	 */
	public int getPrayCardId() {
		return prayCardId;
	}

	// ////////////////////////////////////////////SET区域
	/**
	 * 设置Id
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 设置对应的角色Id
	 *
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 设置加入帮派的Id
	 * 
	 * @param groupId
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * 设置在帮派中的职位（默认是成员）
	 * 
	 * @param post
	 */
	public synchronized void setPost(int post) {
		this.post = post;
	}

	/**
	 * 设置上次登出游戏的时间
	 * 
	 * @param logoutTime
	 */
	public synchronized void setLogoutTime(long logoutTime) {
		this.logoutTime = logoutTime;
	}

	/**
	 * 设置成员的名字
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 设置成员的等级
	 * 
	 * @param level
	 */
	public void setLevel(short level) {
		this.level = level;
	}

	/**
	 * 设置成员的头像Id
	 * 
	 * @param headId
	 */
	public void setHeadId(String headId) {
		this.headId = headId;
	}

	/**
	 * 设置成员的Vip等级
	 * 
	 * @param vipLevel
	 */
	public void setVipLevel(byte vipLevel) {
		this.vipLevel = vipLevel;
	}

	/**
	 * 设置成员的职业
	 * 
	 * @param job
	 */
	public void setJob(byte job) {
		this.job = job;
	}

	/**
	 * 设置个人贡献
	 *
	 * @param contribution
	 */
	public void setContribution(int contribution) {
		this.contribution = contribution;
	}

	/**
	 * 设置成员的战斗力（申请时战斗里最高的4个佣兵和角色的战斗力之和）
	 * 
	 * @param fighting
	 */
	public void setFighting(int fighting) {
		this.fighting = fighting;
	}

	/**
	 * 设置申请加如帮派的时间
	 * 
	 * @param applyTime
	 */
	public void setApplyTime(long applyTime) {
		this.applyTime = applyTime;
	}

	/**
	 * <pre>
	 * 设置加入帮派被接收的时间
	 * <b>注意：在这里帮主的接收时间和请求时间是一样的
	 * 对于普通的申请加入的成员来说，只有当申请通过之后，这里才会有时间。
	 * 这个时间为0也就是还在申请队列中，没有被加入。</b>
	 * </pre>
	 * 
	 * @param receiveTime
	 */
	public synchronized void setReceiveTime(long receiveTime) {
		this.receiveTime = receiveTime;
	}

	/**
	 * 设置成员的模版Id
	 * 
	 * @param templateId
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * 设置帮派个人总贡献
	 * 
	 * @param totalContribution
	 */
	public void setTotalContribution(int totalContribution) {
		this.totalContribution = totalContribution;
	}

	public String getHeadbox() {
		return headbox;
	}

	public void setHeadbox(String headbox) {
		this.headbox = headbox;
	}

	public void setDayContribution(int dayContribution) {
		this.dayContribution = dayContribution;
	}

	public int getAllotRewardCount() {
		return allotRewardCount;
	}

	public void setAllotRewardCount(int allotRewardCount) {
		this.allotRewardCount = allotRewardCount;
	}

	public void setArmyFashion(ArmyFashion armyFashion) {
		this.armyFashion = armyFashion;
	}

	/**
	 * 设置祈福的进度
	 * 
	 * @param prayProcess
	 */
	public void setPrayProcess(int prayProcess) {
		this.prayProcess = prayProcess;
	}

	/**
	 * 设置祈福的卡Id
	 * 
	 * @param prayCardId
	 */
	public void setPrayCardId(int prayCardId) {
		this.prayCardId = prayCardId;
	}
}