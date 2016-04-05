package com.rw.service.log.template.maker;

public class LogTemplate {

	final static public String AccountLogoutLogTemplate="$logTime$|core_account|$loginZoneId$|$logTime$|account_act|$loginZoneId$|$regChannelId_uid$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$phoneOp$|$loginNetType$|$loginPhoneType$|$clientVersion$|$loginClientIp$|$loginImei$|$loginImac$|||||account_logout||$result$|$statInfo$";
	final static public String ActivityBeginLogTemplate="$logTime$|core_activity|$loginZoneId$|$logTime$|activity|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$activityEntry$|$GamesCode$|$activityCode$||activity_start|$copyId$|$result$|";
	final static public String ActivityEndLogTemplate="$logTime$|core_activity|$loginZoneId$|$logTime$|activity|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$activityEntry$|$GamesCode$|$activityCode$||activity_finish|$copyId$|$result$|activity_time:$activityTime$";
	final static public String CoinChangedLogTemplate="$logTime$|core_coin|$loginZoneId$|$logTime$|sub_coin|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$scenceId$|$ItemChangedEventType_1$|$ItemChangedEventType_2$|$coinChanged$|$coinRemain$|1|";
	final static public String CopyBeginLogTemplate="$logTime$|core_case|$loginZoneId$|$logTime$|case|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$copyEntrance$|$GamesCode$|$copyId$|$copyStatus$|case_start|$copyLevel$|$result$|";
	final static public String CopyEndLogTemplate="$logTime$|core_case|$loginZoneId$|$logTime$|case|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$copyEntrance$|$GamesCode$|$copyId$|$copyStatus$|$operationCode$|$copyLevel$|$result$|fight_time:$fightTime$";
	final static public String GiftGoldChangedLogTemplate="$logTime$|core_coin|$loginZoneId$|$logTime$|gift_coin|$loginZoneId$|$regChannelId_uid$|uid|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$scenceId$|$ItemChangedEventType_1$|$ItemChangedEventType_2$|赠送充值币新增消耗数量（新增为正数，消耗为负数）|变动后赠送充值货币个人持有量|1|||";
	final static public String ItemChangedLogTemplate="$logTime$|core_item|$loginZoneId$|$logTime$|item|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$scenceId$|$ItemChangedEventType_1$|$ItemChangedEventType_2$|$itemList$|$itemList$|1|";
	final static public String OnlineCountLogTemplate="$logTime$|core_stat_1|$loginZoneId$|$logTime$|stat_account_online|$loginZoneId$|$regSubChannelId$|$onlineCount$|$loginClientPlatForm$";
	final static public String RoleCreatedLogTemplate="$logTime$|core_role|$loginZoneId$|$logTime$|role_reg|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||||role_reg||1|";
	final static public String RoleLoginLogTemplate="$logTime$|core_role|$loginZoneId$|$logTime$|role_act|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||||role_login||1|";
	final static public String RoleLogoutLogTemplate="$logTime$|core_role|$loginZoneId$|$logTime$|role_act|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||||role_logout||1|$statInfo$";
	final static public String RoleUpgradeLogTemplate="$logTime$|core_action|$loginZoneId$|$logTime$|upgrade|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||$mapid$||主$userId$||upgrade_role|$levelBeforeUp$|1|||";
	final static public String TaskBeginLogTemplate="$logTime$|core_task|$loginZoneId$|$logTime$|task|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||||$taskId$|$biTaskType$|$optype$||1|||";
	final static public String TaskEndLogTemplate="$logTime$|core_task|$loginZoneId$|$logTime$|task|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$||||$taskId$|$biTaskType$|$optype$||1|||";
	final static public String ZoneCountCoinLogTemplate="$logTime$|core_stat_1|$loginZoneId$|$logTime$|stat_sub_coin|$loginZoneId$|$regSubChannelId$|$zoneCoinRemain$|$loginClientPlatForm$|$threadId$|";
	final static public String ZoneCountLevelSpreadLogTemplate="$logTime$|core_stat_2|$loginZoneId$|$logTime$|stat_role_level|$loginZoneId$|$regSubChannelId$|$level$|$levelCount$|$loginClientPlatForm$|$threadId$|";
	final static public String ZoneCountTotalAccountLogTemplate="$logTime$|core_stat_1|$loginZoneId$|$logTime$|stat_account|$loginZoneId$|$regSubChannelId$|$totalAccount$|$loginClientPlatForm$|$threadId$|";
	final static public String ZoneCountVipSpreadLogTemplate="$logTime$|core_stat_2|$loginZoneId$|$logTime$|stat_account_vip|$loginZoneId$|$regSubChannelId$|$vip$|$totalAccount$|$loginClientPlatForm$|$threadId$|";
	final static public String ZoneLoginLogTemplate="$logTime$|core_gamesvr|$loginZoneId$|$logTime$|gamesvr_act|$loginZoneId$|$regChannelId_uid$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$vip$|$phoneOp$|$loginNetType$|$loginPhoneType$|$clientVersion$|$loginClientIp$|$loginImei$|$loginImac$|||gamesvr_login||$result$|";
	final static public String ZoneLogoutLogTemplate="$logTime$|core_gamesvr|$loginZoneId$|$logTime$|gamesvr_act|$loginZoneId$|$regChannelId_uid$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$vip$|$phoneOp$|$loginNetType$|$loginPhoneType$|$clientVersion$|$loginClientIp$|$loginImei$|$loginImac$|||gamesvr_logout||$result$|$statInfo$";
	final static public String ZoneRegLogTemplate="$logTime$|core_gamesvr|$loginZoneId$|$logTime$|gamesvr_reg|$loginZoneId$|$regChannelId_uid$|$userId$|$regSubChannelId$|$loginSubChannelId$|$loginClientPlatForm$|$userCreatedTime$|$vip$|$phoneOp$|$loginNetType$|$loginPhoneType$|$clientVersion$|$loginClientIp$|$loginImei$|$loginImac$|||gamesvr_reg||1|";
	
}
	