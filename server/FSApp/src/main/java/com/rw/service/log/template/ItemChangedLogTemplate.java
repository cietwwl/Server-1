package com.rw.service.log.template;

import java.util.Set;

/**
 * 区账号注册
 * @author allen
 *
 */
public class ItemChangedLogTemplate extends BILogTemplate{

	
	final private String template="$logTime$|core_item|$loginZoneId$|$logTime$|item|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$|$level$|$scenceId$|$ItemChangedEventType_1$|$ItemChangedEventType_2$|$itemList_incr$|$itemList_decr$|1|";
	
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}


	public static void main(String[] args) {
		//注意第二个物品栏要手动加"_2"
		String copyBegin = "打印时间|core_item|用户登录区ID|日志的触发时间|item|用户登录区ID|注册渠道ID_UID|UID|角色ID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|角色创建时间|当前游戏客户端版本|用户VIP等级|用户角色等级|用户战力|职业ID|职业等级|场景id/地图id|一级变动原因|二级变动原因|物品code1：数量1&物品code2：数量2: 物品code3：数量3…|物品code1：数量1&物品code2：数量2: 物品code3：数量3…|1|此处留空";
		BILogTemplateHelper.toTemplate(copyBegin);
	}
	
}
