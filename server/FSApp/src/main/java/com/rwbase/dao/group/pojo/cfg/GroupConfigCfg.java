package com.rwbase.dao.group.pojo.cfg;

/*
 * @author HC
 * @date 2016年1月16日 下午12:33:44
 * @Description 帮派的基础配置
 */
public class GroupConfigCfg {
	private String cfgId;// 配置表的唯一Id
	private String createGroupPrice;// 创建帮派的花费信息
	private String renamePrice;// 修改帮派名字的花费信息
	private String defaultIconId;// 默认帮派图标的Id
	private int announcementCharLimit;// 帮派公告字节限制(直接以字节为限制，中文2个，英文是1个)
	private int declarationCharLimit; // 帮派宣言的字节限制
	private int groupEmailTitleCharLimit;// 邮件标题字数限制
	private int groupEmailContentCharLimit;// 邮件内容字数限制
	private int groupNameCharLimit;// 帮派名字的字数限制
	private int defaultValidateType;// 默认的验证通过类型
	private int defaultApplyLevel;// 默认的申请最低等级
	private String groupValidateTypeInfo;// 帮派验证通过的信息
	private String groupApplyLevelLimit;// 帮派验证的等级信息
	private int autoTransGroupLeaderDay;// 帮主不在线多少天可以自动转让位置(只有大于0才生效)
	private int dismissCoolingTime;// 解散帮派的冷却时间(秒记)
	private int joinGroupCoolingTime;// 退出之后再加入帮派的冷却时间(秒记)
	private int perDayDonateTimes;// 每天可以捐献的次数
	private int rankRecommendIndex;// 排行榜推荐多少名之前的帮派
	private int recommendSize;// 推荐帮派的个数
	private int uiShowRankGroupSize;// 界面上显示帮派的数量
	private int groupApplyMemberSize;// 帮派申请成员的数量
	private int groupRankSize;// 帮派排行榜的数量
	private int perDayMaxApplyGroupSize;// 每天最多可以申请的帮派数量
	private int memberNumRankMaxLimit;// 帮派成员数量排行榜的最高成员数量
	private int groupEmailCoolingTime;// 帮派邮件冷却时间（秒为单位）
	private int leaderLogoutTimeNoneRecommend;// 帮主离线多长时间不推荐(天为单位)
	private int groupLogCacheSize;// 帮派日志缓存的最大条数
	private int canDonateCoolingTime;// 进入帮派之后多久可以捐献
	private int maxExpLimitPerDay;// 每天可以获得最大的经验数量...
	private int maxSupplyLimitPerDay;// 每天可以获得最大的物资数量...
	private int maxContributionLimitPerDay;// 每天可以捐献的最大数量...
	private int tokenId;// 令牌的Id

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
	 * 获取帮派邮件的标题限制
	 * 
	 * @return
	 */
	public int getGroupEmailTitleCharLimit() {
		return groupEmailTitleCharLimit;
	}

	/**
	 * 获取帮派邮件的内容限制
	 * 
	 * @return
	 */
	public int getGroupEmailContentCharLimit() {
		return groupEmailContentCharLimit;
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
	 * 获取解散帮派的冷却时间
	 * 
	 * @return
	 */
	public int getDismissCoolingTime() {
		return dismissCoolingTime;
	}

	/**
	 * 获取退出帮派之后再加入新的帮派的冷却时间
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

	public String getCreateGroupPrice() {
		return createGroupPrice;
	}

	public String getRenamePrice() {
		return renamePrice;
	}

	public String getGroupValidateTypeInfo() {
		return groupValidateTypeInfo;
	}

	public String getGroupApplyLevelLimit() {
		return groupApplyLevelLimit;
	}

	public int getUiShowRankGroupSize() {
		return uiShowRankGroupSize;
	}

	public int getGroupApplyMemberSize() {
		return groupApplyMemberSize;
	}

	public int getGroupRankSize() {
		return groupRankSize;
	}

	public int getPerDayMaxApplyGroupSize() {
		return perDayMaxApplyGroupSize;
	}

	public int getMemberNumRankMaxLimit() {
		return memberNumRankMaxLimit;
	}

	public int getGroupEmailCoolingTime() {
		return groupEmailCoolingTime;
	}

	public int getLeaderLogoutTimeNoneRecommend() {
		return leaderLogoutTimeNoneRecommend;
	}

	public int getGroupLogCacheSize() {
		return groupLogCacheSize;
	}

	public int getCanDonateCoolingTime() {
		return canDonateCoolingTime;
	}

	public int getMaxExpLimitPerDay() {
		return maxExpLimitPerDay;
	}

	public int getMaxSupplyLimitPerDay() {
		return maxSupplyLimitPerDay;
	}

	public int getMaxContributionLimitPerDay() {
		return maxContributionLimitPerDay;
	}

	public int getTokenId() {
		return tokenId;
	}
}