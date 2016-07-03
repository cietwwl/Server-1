package com.rwbase.dao.group.pojo.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;

/*
 * @author HC
 * @date 2016年1月16日 下午2:41:03
 * @Description 帮派的基础数据信息
 */
@SynClass
@Table(name = "group_data")
public class GroupBaseData implements GroupBaseDataIF, IMapItem {

	@Id
	private String id;// 唯一的Id
	@IgnoreSynField
	private String createUserId;// 创建帮派的角色Id
	@IgnoreSynField
	private long createTime;// 创建帮派的时间
	private int groupLevel;// 创建帮派的等级<byte>
	private int groupExp;// 当前帮派的经验
	@IgnoreSynField
	private int groupState;// 当前帮派的状态<byte>
	private String groupName;// 帮派的名字(数据库中是以Name来作为数据表的唯一索引)
	private String iconId;// 帮派的图标Id
	private int supplies;// 帮派的储备物资
	private String announcement;// 帮派内部公告 Base64加密
	private String declaration;// 帮派的宣言 Base64加密
	private int validateType;// 帮派的验证类型<byte>
	private int applyLevel;// 申请进入帮派的等级<short>
	@IgnoreSynField
	private long toLevelTime;// 达成当前帮派等级到达的时间
	private long dismissTime;// 帮派申请解散的时间
	@IgnoreSynField
	@SaveAsJson
	private ConcurrentHashMap<Integer, GroupSkillItem> researchSkillMap;// 研发的技能的数据
	@NonSave
	private int token;// 令牌
	@NonSave
	private int daySupplies;// 当天捐献的物资数量
	@NonSave
	private int dayExp;// 当天捐献的经验

	// ////////////////////////////////////////////////无需解析的属性区
	@IgnoreSynField
	@NonSave
	private volatile long lastCheckTime;// 上次检查帮主帮主是否在线的时间
	@IgnoreSynField
	@NonSave
	private volatile long distanceTransferTime;// 距离要转让还有多少时间

	public GroupBaseData() {
		researchSkillMap = new ConcurrentHashMap<Integer, GroupSkillItem>();
	}

	// ////////////////////////////////////////////////GET基础属性区域

	/**
	 * 获取唯一的Id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * 获取创建帮派的Id
	 * 
	 * @return
	 */
	@Override
	public String getCreateUserId() {
		return createUserId;
	}

	/**
	 * 获取创建帮派的时间
	 * 
	 * @return
	 */
	@Override
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * 获取当前帮派的等级
	 * 
	 * @return
	 */
	@Override
	public int getGroupLevel() {
		return groupLevel;
	}

	/**
	 * 获取当前的帮派经验
	 * 
	 * @return
	 */
	@Override
	public int getGroupExp() {
		return groupExp;
	}

	/**
	 * 获取当前帮派的状态
	 * 
	 * @return
	 */
	@Override
	public int getGroupState() {
		return groupState;
	}

	/**
	 * 获取当前帮派的名字
	 * 
	 * @return
	 */
	@Override
	public String getGroupName() {
		return groupName;
	}

	/**
	 * 获取当前帮派的图标
	 * 
	 * @return
	 */
	@Override
	public String getIconId() {
		return iconId;
	}

	/**
	 * 获取帮派当前的物资
	 * 
	 * @return
	 */
	@Override
	public int getSupplies() {
		return supplies;
	}

	/**
	 * 获取帮派的公告
	 * 
	 * @return
	 */
	@Override
	public String getAnnouncement() {
		return announcement;
	}

	/**
	 * 获取帮派的宣言
	 * 
	 * @return
	 */
	@Override
	public String getDeclaration() {
		return declaration;
	}

	/**
	 * 获取帮派的验证类型
	 * 
	 * @return
	 */
	@Override
	public int getValidateType() {
		return validateType;
	}

	/**
	 * 获取帮派申请的最低等级
	 * 
	 * @return
	 */
	@Override
	public int getApplyLevel() {
		return applyLevel;
	}

	/**
	 * 获取达成当前帮派等级的时间
	 * 
	 * @return
	 */
	@Override
	public long getToLevelTime() {
		return toLevelTime;
	}

	/**
	 * 获取帮派请求解散的时间
	 * 
	 * @return
	 */
	@Override
	public long getDismissTime() {
		return dismissTime;
	}

	/**
	 * 获取所有研发的技能
	 * 
	 * @return
	 */
	@Override
	public Enumeration<GroupSkillItem> getResearchSkill() {
		return researchSkillMap.elements();
	}

	/**
	 * 获取帮派的数据
	 */
	@Override
	public String getGroupId() {
		return id;
	}

	/**
	 * 获取帮派技能数据的列表
	 * 
	 * @return
	 */
	public List<GroupSkillItem> getResearchSkillList() {
		return new ArrayList<GroupSkillItem>(researchSkillMap.values());
	}

	/**
	 * 获取帮派令牌的数量
	 * 
	 * @return
	 */
	public int getToken() {
		return token;
	}

	/**
	 * 获取当天获取的帮派物资数量
	 * 
	 * @return
	 */
	public int getDaySupplies() {
		return daySupplies;
	}

	/**
	 * 获取当天获取的帮派经验数量
	 * 
	 * @return
	 */
	public int getDayExp() {
		return dayExp;
	}

	// ================================================无需检测字段的GET区域

	/**
	 * 设置帮派令牌的数量
	 * 
	 * @param token
	 */
	public void setToken(int token) {
		this.token = token;
	}

	/**
	 * 设置当天获取帮派物资的数量
	 * 
	 * @param daySupplies
	 */
	public void setDaySupplies(int daySupplies) {
		this.daySupplies = daySupplies;
	}

	/**
	 * 设置当天获取帮派经验的数量
	 * 
	 * @param dayExp
	 */
	public void setDayExp(int dayExp) {
		this.dayExp = dayExp;
	}

	/**
	 * 获取上次检查帮主在线的时间
	 * 
	 * @return
	 */
	public long getLastCheckTime() {
		return lastCheckTime;
	}

	/**
	 * 获取上次检查时距离帮主转换还剩余的时间
	 * 
	 * @return
	 */
	public long getDistanceTransferTime() {
		return distanceTransferTime;
	}

	// ////////////////////////////////////////////////SET基础属性区域
	/**
	 * 设置帮派的Id
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 设置创建者的UserId
	 * 
	 * @param createUserId
	 */
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	/**
	 * 设置创建帮派的时间
	 * 
	 * @param createTime
	 */
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	/**
	 * 设置帮派的等级
	 * 
	 * @param groupLevel
	 */
	public void setGroupLevel(int groupLevel) {
		this.groupLevel = groupLevel;
	}

	/**
	 * 设置帮派的经验
	 * 
	 * @param groupExp
	 */
	public void setGroupExp(int groupExp) {
		this.groupExp = groupExp;
	}

	/**
	 * 设置当前帮派的状态（默认是正常0，解散是1）
	 * 
	 * @param groupState
	 */
	public void setGroupState(byte groupState) {
		this.groupState = groupState;
	}

	/**
	 * 设置帮派的名字
	 * 
	 * @param groupName
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * 设置图标的Id
	 * 
	 * @param iconId
	 */
	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	/**
	 * 设置帮派的物资数量
	 * 
	 * @param supplies
	 */
	public void setSupplies(int supplies) {
		this.supplies = supplies;
	}

	/**
	 * 设置帮派内部的公告
	 * 
	 * @param announcement
	 */
	public void setAnnouncement(String announcement) {
		this.announcement = announcement;
	}

	/**
	 * 设置帮派宣言
	 * 
	 * @param declaration
	 */
	public void setDeclaration(String declaration) {
		this.declaration = declaration;
	}

	/**
	 * 设置帮派的验证类型，默认是验证后加入
	 * 
	 * @param validateType
	 */
	public void setValidateType(int validateType) {
		this.validateType = validateType;
	}

	/**
	 * 设置帮派申请的最低等级
	 * 
	 * @param applyLevel
	 */
	public void setApplyLevel(int applyLevel) {
		this.applyLevel = applyLevel;
	}

	/**
	 * 设置达成帮派当前等级的时间
	 * 
	 * @param toLevelTime
	 */
	public void setToLevelTime(long toLevelTime) {
		this.toLevelTime = toLevelTime;
	}

	/**
	 * 设置请求解散帮派的时间
	 * 
	 * @param dismissTime
	 */
	public void setDismissTime(long dismissTime) {
		this.dismissTime = dismissTime;
	}

	// ================================================无需检测字段的SET区域

	/**
	 * 设置上次检查帮主离线的时间
	 * 
	 * @param lastCheckTime
	 */
	public void setLastCheckTime(long lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}

	/**
	 * 设置距离帮主过期还有多少时间
	 * 
	 * @param distanceTransferTime
	 */
	public void setDistanceTransferTime(long distanceTransferTime) {
		this.distanceTransferTime = distanceTransferTime;
	}

	// ================================================逻辑区域

	/**
	 * 是否研发了某个技能
	 * 
	 * @param skillId
	 * @return
	 */
	public boolean hasResearchedSkill(int skillId) {
		return researchSkillMap.containsKey(skillId);
	}

	/**
	 * 检查研发的技能当中是否包含了某个等级的技能
	 * 
	 * @param skillId 技能Id
	 * @param level 技能等级
	 * @return
	 */
	public boolean checkHasResearchedSkill(int skillId, int level) {
		GroupSkillItem hasValue = researchSkillMap.get(skillId);
		return hasValue == null ? false : (hasValue.getLevel() >= level);
	}

	/**
	 * 获取技能已经研发的等级
	 * 
	 * @param skillId 技能Id
	 * @return 如果研究这个技能的记录就返回-1
	 */
	public int getResearchedSkillLevel(int skillId) {
		GroupSkillItem hasValue = researchSkillMap.get(skillId);
		return hasValue == null ? -1 : hasValue.getLevel();
	}

	/**
	 * 增加或者刷新新研究的某个技能
	 * 
	 * @param skillId 技能Id
	 * @param level 技能等级
	 * @param time <b>【注】如果不设置这个数据，就填入-1</b>
	 * @param state <b>【注】如果不设置这个数据，就填入-1</b>
	 */
	public void addOrUpdateResearchSkill(int skillId, int level, long time, int state) {
		GroupSkillItem skillItem = researchSkillMap.get(skillId);
		if (skillItem == null) {
			skillItem = new GroupSkillItem();
			skillItem.setId(skillId);
			researchSkillMap.put(skillId, skillItem);
		}

		skillItem.setLevel(level);
		if (time != -1) {
			skillItem.setTime(time);
		}

		if (state != -1) {
			skillItem.setState(state);
		}
	}
}