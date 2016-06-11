package com.rwbase.dao.group.pojo.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

/*
 * @author HC
 * @date 2016年1月16日 下午6:26:33
 * @Description 帮派的基础配置
 */
public class GroupBaseConfigTemplate {

	private final String cfgId;// 配置表的唯一Id
	private final String defaultIconId;// 默认帮派图标的Id
	private final int announcementCharLimit;// 帮派公告字节限制(直接以字节为限制，中文2个，英文是1个)
	private final int declarationCharLimit; // 帮派宣言的字节限制
	private final int groupEmailTitleCharLimit;// 邮件标题字数限制
	private final int groupEmailContentCharLimit;// 邮件内容字数限制
	private final int groupNameCharLimit;// 帮派名字的字数限制
	private final int groupEmailCoolingTime;// 帮派邮件的冷却时间
	private final int defaultValidateType;// 默认的验证通过类型
	private final int defaultApplyLevel;// 默认的申请最低等级
	private final int autoTransGroupLeaderDay;// 帮主不在线多少天可以自动转让位置(只有大于0才生效)
	private final int dismissCoolingTime;// 解散帮派的冷却时间(秒记)
	private final int joinGroupCoolingTime;// 退出之后再加入帮派的冷却时间(秒记)
	private final int perDayDonateTimes;// 每天可以捐献的次数
	private final int rankRecommendIndex;// 排行榜推荐多少名之前的帮派
	private final int recommendSize;// 推荐帮派的个数
	private final int[] createGroupPriceArr;// 创建帮派花费
	private final int[] renamePriceArr;// 重命名的花费
	private final Map<Integer, String> groupValidateTypeInfoMap;// 帮派验证通过的类型
	private final List<Integer> groupApplyLevelLimitList;// 帮派验证通过的等级信息
	private final int uiShowRankGroupSize;// 界面上显示帮派的数量
	private final int groupApplyMemberSize;// 帮派申请成员的数量
	private final int groupRankSize;// 帮派排行榜的数量
	private final int perDayMaxApplyGroupSize;// 每天最多可以申请的帮派数量
	private final int memberNumRankMaxLimit;// 帮派成员数量排行榜的最高成员数量
	private final int leaderLogoutTimeNoneRecommend;// 帮主离线多久不推荐（天为单位）
	private final int groupLogCacheSize;// 帮派日志缓存的最大条数
	private final int canDonateCoolingTime;// 进入帮派之后多久可以捐献

	public GroupBaseConfigTemplate(GroupConfigCfg baseCfg) {
		this.cfgId = baseCfg.getCfgId();
		this.defaultIconId = baseCfg.getDefaultIconId();
		this.announcementCharLimit = baseCfg.getAnnouncementCharLimit();
		this.declarationCharLimit = baseCfg.getDeclarationCharLimit();
		this.groupNameCharLimit = baseCfg.getGroupNameCharLimit();
		this.groupEmailTitleCharLimit = baseCfg.getGroupEmailTitleCharLimit();
		this.groupEmailContentCharLimit = baseCfg.getGroupEmailContentCharLimit();
		this.groupEmailCoolingTime = baseCfg.getGroupEmailCoolingTime();
		this.defaultValidateType = baseCfg.getDefaultValidateType();
		this.defaultApplyLevel = baseCfg.getDefaultApplyLevel();
		this.autoTransGroupLeaderDay = baseCfg.getAutoTransGroupLeaderDay();
		this.dismissCoolingTime = baseCfg.getDismissCoolingTime();
		this.joinGroupCoolingTime = baseCfg.getJoinGroupCoolingTime();
		this.perDayDonateTimes = baseCfg.getPerDayDonateTimes();
		this.rankRecommendIndex = baseCfg.getRankRecommendIndex();
		this.recommendSize = baseCfg.getRecommendSize();
		this.uiShowRankGroupSize = baseCfg.getUiShowRankGroupSize();
		this.groupApplyMemberSize = baseCfg.getGroupApplyMemberSize();
		this.groupRankSize = baseCfg.getGroupRankSize();
		this.perDayMaxApplyGroupSize = baseCfg.getPerDayMaxApplyGroupSize();
		this.memberNumRankMaxLimit = baseCfg.getMemberNumRankMaxLimit();
		this.leaderLogoutTimeNoneRecommend = baseCfg.getLeaderLogoutTimeNoneRecommend();
		this.groupLogCacheSize = baseCfg.getGroupLogCacheSize();
		this.canDonateCoolingTime = baseCfg.getCanDonateCoolingTime();

		this.createGroupPriceArr = switchStr2CreateGroupPriceArr(baseCfg.getCreateGroupPrice());
		this.renamePriceArr = switchStr2RenamePriceArr(baseCfg.getRenamePrice());
		this.groupValidateTypeInfoMap = switchStr2GroupValidateTypeInfoMap(baseCfg.getGroupValidateTypeInfo());
		this.groupApplyLevelLimitList = switchStr2GroupApplyLevelLimitList(baseCfg.getGroupApplyLevelLimit());
	}

	private int[] switchStr2CreateGroupPriceArr(String createGroupPrice) {
		if (!StringUtils.isEmpty(createGroupPrice)) {
			String[] split = createGroupPrice.split("_");
			int[] arr = new int[2];
			arr[0] = Integer.parseInt(split[0]);
			arr[1] = Integer.parseInt(split[1]);
			return arr;
		}
		return null;
	}

	private int[] switchStr2RenamePriceArr(String renamePrice) {
		// 重命名的花费
		if (!StringUtils.isEmpty(renamePrice)) {
			String[] split = renamePrice.split("_");
			int[] renamePriceArr = new int[2];
			renamePriceArr[0] = Integer.parseInt(split[0]);
			renamePriceArr[1] = Integer.parseInt(split[1]);
			return renamePriceArr;
		}

		return null;
	}

	private Map<Integer, String> switchStr2GroupValidateTypeInfoMap(String groupValidateTypeInfo) {
		// 帮派验证通过的类型
		if (!StringUtils.isEmpty(groupValidateTypeInfo)) {
			String[] split = groupValidateTypeInfo.split(";");
			int len = split.length;
			Map<Integer, String> groupValidateTypeInfoMap = new HashMap<Integer, String>(len);

			for (int i = 0; i < len; i++) {
				String[] split0 = split[i].split("_");
				groupValidateTypeInfoMap.put(Integer.valueOf(split0[0]), split0[1]);
			}

			return Collections.unmodifiableMap(groupValidateTypeInfoMap);
		}

		return Collections.emptyMap();
	}

	private List<Integer> switchStr2GroupApplyLevelLimitList(String groupApplyLevelLimit) {
		// 帮派验证通过的等级信息
		if (!StringUtils.isEmpty(groupApplyLevelLimit)) {
			String[] split = groupApplyLevelLimit.split(",");
			int len = split.length;
			List<Integer> l = new ArrayList<Integer>(len);

			for (int i = 0; i < len; i++) {
				Integer value = Integer.valueOf(split[i]);
				if (!l.contains(value)) {
					l.add(value);
				}
			}

			return Collections.unmodifiableList(l);
		}

		return Collections.emptyList();
	}

	public String getCfgId() {
		return cfgId;
	}

	/**
	 * 获取默认的图标
	 * 
	 * @return
	 */
	public String getDefaultIconId() {
		return defaultIconId;
	}

	/**
	 * 获取公告的长度
	 * 
	 * @return
	 */
	public int getAnnouncementCharLimit() {
		return announcementCharLimit;
	}

	/**
	 * 获取宣言的长度
	 * 
	 * @return
	 */
	public int getDeclarationCharLimit() {
		return declarationCharLimit;
	}

	/**
	 * 获取帮派邮件标题的字数限制
	 * 
	 * @return
	 */
	public int getGroupEmailTitleCharLimit() {
		return groupEmailTitleCharLimit;
	}

	/**
	 * 获取帮派邮件内容的字数限制
	 * 
	 * @return
	 */
	public int getGroupEmailContentCharLimit() {
		return groupEmailContentCharLimit;
	}

	/**
	 * 获取帮派邮件发送的冷却时间（秒为单位）
	 * 
	 * @return
	 */
	public int getGroupEmailCoolingTime() {
		return groupEmailCoolingTime;
	}

	/**
	 * 获取帮派名字的字数限制
	 * 
	 * @return
	 */
	public int getGroupNameCharLimit() {
		return groupNameCharLimit;
	}

	/**
	 * 获取帮主不在线多少 天之后可以把帮主位置转让给别人(大于0才能生效)
	 * 
	 * @return
	 */
	public int getAutoTransGroupLeaderDay() {
		return autoTransGroupLeaderDay;
	}

	/**
	 * 获取解散帮派的冷却时间(秒记)
	 * 
	 * @return
	 */
	public int getDismissCoolingTime() {
		return dismissCoolingTime;
	}

	/**
	 * 获取退出帮派之后再加入新的帮派的冷却时间(秒记)
	 * 
	 * @return
	 */
	public int getJoinGroupCoolingTime() {
		return joinGroupCoolingTime;
	}

	/**
	 * 获取每天每个成员可以捐献的上限次数
	 * 
	 * @return
	 */
	public int getPerDayDonateTimes() {
		return perDayDonateTimes;
	}

	/**
	 * 获取推荐排行榜多少名前的多少名的帮派给玩家
	 * 
	 * @return
	 */
	public int getRankRecommendIndex() {
		return rankRecommendIndex;
	}

	/**
	 * 获取从帮派排行榜中随机多少个帮派给玩家
	 * 
	 * @return
	 */
	public int getRecommendSize() {
		return recommendSize;
	}

	/**
	 * 获取默认的验证类型
	 * 
	 * @return
	 */
	public int getDefaultValidateType() {
		return defaultValidateType;
	}

	/**
	 * 获取默认的申请最低等级
	 * 
	 * @return
	 */
	public int getDefaultApplyLevel() {
		return defaultApplyLevel;
	}

	/**
	 * 获取创建帮派的价格
	 * 
	 * @return 返回一个数组，索引0是消耗货币类型，所以1是消耗的数量
	 */
	public int[] getCreateGroupPriceArr() {
		return createGroupPriceArr;
	}

	/**
	 * 获取帮派重命名的价格
	 * 
	 * @return 返回一个数组，索引0是消耗货币类型，所以1是消耗的数量
	 */
	public int[] getRenamePriceArr() {
		return renamePriceArr;
	}

	/**
	 * 获取帮派验证类型的信息类型Map
	 * 
	 * @return 返回的是一个不可修改的Map，调用它的修改数据方法全部会抛出一个UnsupportedOperationException的异常
	 */
	public Map<Integer, String> getGroupValidateTypeInfoMap() {
		return groupValidateTypeInfoMap;
	}

	/**
	 * 获取帮派验证的最低等级
	 * 
	 * @return 返回的是一个不可修改的List，调用它的修改数据方法全部会抛出一个UnsupportedOperationException的异常
	 */
	public List<Integer> getGroupApplyLevelLimitList() {
		return groupApplyLevelLimitList;
	}

	/**
	 * 获取界面上显示排行榜的总条目数
	 * 
	 * @return
	 */
	public int getUiShowRankGroupSize() {
		return uiShowRankGroupSize;
	}

	/**
	 * 获取帮派申请队列的长度
	 * 
	 * @return
	 */
	public int getGroupApplyMemberSize() {
		return groupApplyMemberSize;
	}

	/**
	 * 获取帮派排行榜的长度
	 * 
	 * @return
	 */
	public int getGroupRankSize() {
		return groupRankSize;
	}

	/**
	 * 获取每天帮派申请的最大数量
	 * 
	 * @return
	 */
	public int getPerDayMaxApplyGroupSize() {
		return perDayMaxApplyGroupSize;
	}

	/**
	 * 获取帮派成员数量排行的最高上限
	 * 
	 * @return
	 */
	public int getMemberNumRankMaxLimit() {
		return memberNumRankMaxLimit;
	}

	/**
	 * 获取帮主离线多久之后，不推荐帮派（天为单位）
	 * 
	 * @return
	 */
	public int getLeaderLogoutTimeNoneRecommend() {
		return leaderLogoutTimeNoneRecommend;
	}

	/**
	 * 获取帮派日志的缓存的条数信息
	 * 
	 * @return
	 */
	public int getGroupLogCacheSize() {
		return groupLogCacheSize;
	}

	/**
	 * 获取进入帮派多久可以捐献（秒）
	 * 
	 * @return
	 */
	public int getCanDonateCoolingTime() {
		return canDonateCoolingTime;
	}
}