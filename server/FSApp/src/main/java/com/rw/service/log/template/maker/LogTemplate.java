package com.rw.service.log.template.maker;

public class LogTemplate {

	final static public String AccountLogoutLogTemplate="$logTime$|core_account|$loginZoneId$|$logTime$|account_act|$loginZoneId$|$regChannelId_uid$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$phoneOp$|$loginNetType$|$loginPhoneType$|$clientVersion$|$loginClientIp$|$loginImei$|$loginImac$|$loginsdkVersion$||||account_logout||$result$|$statInfo$|$loginadLinkId$|$loginsystemVersion$";
	final static public String ActivityBeginLogTemplate="$logTime$|core_activity|$loginZoneId$|$logTime$|activity|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$activityEntry$|$GamesCode$|$activityCode$||activity_start|$copyId$|$result$||$loginadLinkId$";
	final static public String ActivityEndLogTemplate="$logTime$|core_activity|$loginZoneId$|$logTime$|activity|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$activityEntry$|$GamesCode$|$activityCode$||$operationCode$|$copyId$|$result$|$activityInfo$|$loginadLinkId$";
	final static public String CoinChangedLogTemplate="$logTime$|core_coin|$loginZoneId$|$logTime$|sub_coin|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$scenceId$|$ItemChangedEventType_1$|$ItemChangedEventType_2$|$coinChanged$|$coinRemain$|1||$loginadLinkId$";
	final static public String CopyBeginLogTemplate="$logTime$|core_case|$loginZoneId$|$logTime$|case|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$copyEntrance$|$GamesCode$|$copyId$|$copyStatus$|case_start|$copyLevel$|$result$||$loginadLinkId$";
	final static public String CopyEndLogTemplate="$logTime$|core_case|$loginZoneId$|$logTime$|case|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$copyEntrance$|$GamesCode$|$copyId$|$copyStatus$|$operationCode$|$copyLevel$|$result$|$copyInfo$|$loginadLinkId$";
	final static public String GiftGoldChangedLogTemplate="$logTime$|core_coin|$loginZoneId$|$logTime$|gift_coin|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$scenceId$|$ItemChangedEventType_1$|$ItemChangedEventType_2$|$giftGoldChanged$|$giftGoldRemain$|1||$loginadLinkId$";
	final static public String ItemChangedLogTemplate="$logTime$|core_item|$loginZoneId$|$logTime$|item|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$scenceId$|$ItemChangedEventType_1$|$ItemChangedEventType_2$|$itemList$|$itemList$|1||$loginadLinkId$";
	final static public String OnlineCountLogTemplate="$logTime$|core_stat_1|$loginZoneId$|$logTime$|stat_role_online|$loginZoneId$|$regSubChannelId$|$onlineCount$|$loginClientPlatForm$|$threadId$";
	final static public String RoleCreatedLogTemplate="$logTime$|core_role|$loginZoneId$|$logTime$|role_reg|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||||role_reg||1||$loginadLinkId$";
	final static public String RoleLoginLogTemplate="$logTime$|core_role|$loginZoneId$|$logTime$|role_act|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||||role_login||1||$loginadLinkId$";
	final static public String RoleLogoutLogTemplate="$logTime$|core_role|$loginZoneId$|$logTime$|role_act|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||||role_logout||1|$statInfo$|$loginadLinkId$";
	final static public String RoleUpgradeLogTemplate="$logTime$|core_action|$loginZoneId$|$logTime$|upgrade|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$mapid$||$userId$||upgrade_role|$levelBeforeUp$|1|$fightbeforelevelup$|$loginadLinkId$";
	final static public String TaskBeginLogTemplate="$logTime$|core_task|$loginZoneId$|$logTime$|task|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||||$taskId$|$biTaskType$|$optype$||1||$loginadLinkId$";
	final static public String TaskEndLogTemplate="$logTime$|core_task|$loginZoneId$|$logTime$|task|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||||$taskId$|$biTaskType$|$optype$||1|$taskInfo$|$loginadLinkId$";
	final static public String ZoneCountCoinLogTemplate="$logTime$|core_stat_1|$loginZoneId$|$logTime$|stat_sub_coin|$loginZoneId$|$regSubChannelId$|$zoneCoinRemain$|$loginClientPlatForm$|$threadId$";
	final static public String ZoneCountGiftGoldLogTemplate="$logTime$|core_stat_1|$loginZoneId$|$logTime$|stat_main_coin|$loginZoneId$|$regSubChannelId$|$zoneGiftGoldRemain$|$loginClientPlatForm$|$threadId$";
	final static public String ZoneCountLevelSpreadLogTemplate="$logTime$|core_stat_2|$loginZoneId$|$logTime$|stat_role_level|$loginZoneId$|$regSubChannelId$|$level$|$levelCount$|$loginClientPlatForm$|$threadId$";
	final static public String ZoneCountTotalAccountLogTemplate="$logTime$|core_stat_1|$loginZoneId$|$logTime$|stat_account|$loginZoneId$|$regSubChannelId$|$totalAccount$|$loginClientPlatForm$|$threadId$";
	final static public String ZoneCountVipSpreadLogTemplate="$logTime$|core_stat_2|$loginZoneId$|$logTime$|stat_account_vip|$loginZoneId$|$regSubChannelId$|$vip$|$totalAccount$|$loginClientPlatForm$|$threadId$";
	final static public String ZoneLoginLogTemplate="$logTime$|core_gamesvr|$loginZoneId$|$logTime$|gamesvr_act|$loginZoneId$|$regChannelId_uid$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$vip$|$phoneOp$|$loginNetType$|$loginPhoneType$|$clientVersion$|$loginClientIp$|$loginImei$|$loginImac$|||gamesvr_login||$result$||$loginadLinkId$";
	final static public String ZoneLogoutLogTemplate="$logTime$|core_gamesvr|$loginZoneId$|$logTime$|gamesvr_act|$loginZoneId$|$regChannelId_uid$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$vip$|$phoneOp$|$loginNetType$|$loginPhoneType$|$clientVersion$|$loginClientIp$|$loginImei$|$loginImac$|||gamesvr_logout||$result$|$statInfo$|$loginadLinkId$";
	final static public String ZoneRegLogTemplate="$logTime$|core_gamesvr|$loginZoneId$|$logTime$|gamesvr_reg|$loginZoneId$|$regChannelId_uid$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$vip$|$phoneOp$|$loginNetType$|$loginPhoneType$|$clientVersion$|$loginClientIp$|$loginImei$|$loginImac$|||gamesvr_reg||1||$loginadLinkId$";
}
	