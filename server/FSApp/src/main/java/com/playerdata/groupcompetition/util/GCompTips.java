package com.playerdata.groupcompetition.util;

public class GCompTips {

	private static String tipsNotSelectionStageNow = "当前不是海选阶段";
	private static String tipsNotMatchStageNow = "当前不是赛事阶段";
	private static String tipsNotTeamEventsNow = "当前不是组队战阶段";
	private static String tipsEnterSelectionStage = "帮派争霸进入海选阶段!";
	private static String tipsEnterEventsStage = "帮派争霸进入赛事阶段!";
	private static String tipsEnterEventsType = "帮派争霸进入[%s]赛事！";
	private static String tipsYouAreNotInGroup = "你不属于任何帮派";
	private static String tipsHeroCountInvalidate = "上阵英雄数量不足！";
	private static String tipsYourGroupNotInMatch = "你所在的帮派没有进入[{}]赛事中";
	private static String tipsNoMatchDetailData = "没有当次赛事的详细信息！";
	private static String tipsYouAreInTeam = "你已经在队伍里面了！";
	private static String tipsYouAreNotInTeam = "你不在队伍里面！";
	private static String tipsTeamMustIncludeMainRole = "出战阵容必须包含主角！";
	private static String tipsNotAllHeroExists = "部分英雄不存在！";
	private static String tipsCreateTeamSuccess = "创建队伍成功";
	private static String tipsTeamNotExists = "队伍不存在！";
	private static String tipsTeamMemberIsMax = "队伍成员已经到达上限！";
	private static String tipsTargetNotInYourGroup = "目标和你不在同一个帮派";
	private static String tipsTargetIsInTeam = "目标已经有队伍了！";
	private static String tipsTargetNotExists = "目标不存在！";
	private static String tipsTargetNotOnline = "目标不在线！";
	private static String tipsInvitation = "[{}]邀请您加入队伍，是否同意加入？";
	private static String tipsYouAreNotLeader = "你不是队长，不能进行该操作！";
	private static String tipsTargetNotInYourTeam = "对方不在你的队伍中！";
	private static String tipsTeamIsMatchingCannotLeave = "队伍正在匹配中，不能离开队伍";
	private static String tipsTeamIsMatchingCannotCancelReady = "队伍正在匹配中，不能取消准备状态";
	private static String tipsCannotInviteYourselft = "不能邀请自己！";
	private static String tipsTeamMemberCountIsNotMax = "队伍未满员不能开始匹配！";
	private static String tipsTeamIsMatching = "队伍正在匹配中，无须重复操作！";
	private static String tipsTeamIsNotMatching = "队伍没有在匹配中！";
	private static String tipsTeamIsInBattle = "队伍正在战斗中！";
	private static String tipsCannotKickYourself = "不能踢除自己";
	private static String tipsYouAreInRandomMatching = "您正在随机匹配中！";
	private static String tipsYouAreNotInRandomMatching = "您并没有在随机匹配中！";
	private static String tipsYourGroupHaveNoEnemy = "您的帮派在本次赛事中轮空！";
	private static String tipsTeamAlreadyMatched = "取消匹配失败，队伍已经匹配上了！";
	private static String tipsYouAlreadyMatched = "取消匹配失败，您已经匹配上了！";
	private static String tipsNotPersonalEventsNow = "当前不是个人战阶段！";
	
	public static String getTipsNotSelectionStageNow() {
		return tipsNotSelectionStageNow;
	}

	public static String getTipsNotMatchStageNow() {
		return tipsNotMatchStageNow;
	}
	
	public static String getTipsNotTeamEventsNow() {
		return tipsNotTeamEventsNow;
	}
	
	public static String getTipsEnterSelectionStage() {
		return tipsEnterSelectionStage;
	}
	
	public static String getTipsEnterEventsStage() {
		return tipsEnterEventsStage;
	}
	
	public static String getTipsEnterEventsType(String name) {
		return String.format(tipsEnterEventsType, name);
	}

	public static String getTipsYouAreNotInGroup() {
		return tipsYouAreNotInGroup;
	}

	public static String getTipsHeroCountInvalidate() {
		return tipsHeroCountInvalidate;
	}

	public static String getTipsYourGroupNotInMatch(String eventsName) {
		return GCompUtil.format(tipsYourGroupNotInMatch, eventsName);
	}

	public static String getTipsNoMatchDetailData() {
		return tipsNoMatchDetailData;
	}

	public static String getTipsYouAreInTeam() {
		return tipsYouAreInTeam;
	}
	
	public static String getTipsYouAreNotInTeam() {
		return tipsYouAreNotInTeam;
	}

	public static String getTipsArmyMustIncludeMainRole() {
		return tipsTeamMustIncludeMainRole;
	}

	public static String getTipsNotAllHeroExists() {
		return tipsNotAllHeroExists;
	}

	public static String getTipsCreateTeamSuccess() {
		return tipsCreateTeamSuccess;
	}

	public static String getTipsTeamNotExists() {
		return tipsTeamNotExists;
	}

	public static String getTipsTeamMemberIsMax() {
		return tipsTeamMemberIsMax;
	}

	public static String getTipsTargetNotInYourGroup() {
		return tipsTargetNotInYourGroup;
	}

	public static String getTipsTargetIsInTeam() {
		return tipsTargetIsInTeam;
	}

	public static String getTipsTargetNotExists() {
		return tipsTargetNotExists;
	}

	public static String getTipsTargetNotOnline() {
		return tipsTargetNotOnline;
	}

	public static String getTipsInvitation(String invitorName) {
		return GCompUtil.format(tipsInvitation, invitorName);
	}

	public static String getTipsYouAreNotLeader() {
		return tipsYouAreNotLeader;
	}

	public static String getTipsTargetNotInYourTeam() {
		return tipsTargetNotInYourTeam;
	}

	public static String getTipsTeamIsMatchingCannotLeave() {
		return tipsTeamIsMatchingCannotLeave;
	}

	public static String getTipsTeamIsMatchingCannotCancelReady() {
		return tipsTeamIsMatchingCannotCancelReady;
	}

	public static String getTipsCannotInviteYourselft() {
		return tipsCannotInviteYourselft;
	}

	public static String getTipsTeamMemberCountIsNotMax() {
		return tipsTeamMemberCountIsNotMax;
	}

	public static String getTipsTeamIsMatching() {
		return tipsTeamIsMatching;
	}

	public static String getTipsTeamIsInBattle() {
		return tipsTeamIsInBattle;
	}

	public static String getTipsCannotKickYourself() {
		return tipsCannotKickYourself;
	}

	public static String getTipsTeamIsNotMatching() {
		return tipsTeamIsNotMatching;
	}
	
	public static String getTipsYouAreNotInRandomMatching() {
		return tipsYouAreNotInRandomMatching;
	}

	public static String getTipsYouAreInRandomMatching() {
		return tipsYouAreInRandomMatching;
	}
	
	public static String getTipsYourGroupHaveNoEnemy() {
		return tipsYourGroupHaveNoEnemy;
	}

	public static String getTipsTeamAlreadyMatched() {
		return tipsTeamAlreadyMatched;
	}

	public static String getTipsYouAlreadyMatched() {
		return tipsYouAlreadyMatched;
	}

	public static String getTipsNotPersonalEventsNow() {
		return tipsNotPersonalEventsNow;
	}
}
