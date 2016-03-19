package com.rw.service.log.template;

import java.util.Set;

import com.rw.service.log.template.maker.LogTemplate;

/**
 * 区账号注册
 * @author allen
 *
 */
public class RoleLogoutLogTemplate extends BILogTemplate{

	
//	final private String template="$logTime$|core_role|$loginZoneId$|$logTime$|role_act|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$|$level$|||role_logout||1|$statInfo$";
	
	final private String template=LogTemplate.RoleLogoutLogTemplate;
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}
	
	public static void main(String[] args) {
		//区账号登录
		String RoleLogoutLogTemplate = "打印时间|core_role|用户登录区ID|日志的触发时间|role_act|用户登录区ID|注册渠道ID_UID|UID|角色ID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|角色创建时间|当前游戏客户端版本|用户VIP等级|用户角色等级|用户战力|职业ID|职业等级|此处为空|此处为空|role_logout|此处为空|1|用户统计信息";
		BILogTemplateHelper.toTemplate(RoleLogoutLogTemplate);
	}
}
