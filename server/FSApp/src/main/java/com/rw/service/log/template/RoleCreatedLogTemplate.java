package com.rw.service.log.template;

import java.util.Set;

import com.rw.service.log.template.maker.LogTemplate;

/**
 * 区账号注册
 * @author allen
 *
 */
public class RoleCreatedLogTemplate extends BILogTemplate{

	
//	final private String template="$logTime$|core_role|$loginZoneId$|$logTime$|role_reg|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$|$level$|||role_reg||1|";
	
	final private String template=LogTemplate.RoleCreatedLogTemplate;
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}
	
	public static void main(String[] args) {
		//角色注册
		String RoleCreatedLogTemplate = "打印时间|core_role|用户登录区ID|日志的触发时间|role_reg|用户登录区ID|注册渠道ID_UID|UID|角色ID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|角色创建时间|当前游戏客户端版本|用户VIP等级|用户角色等级|用户战力|职业ID|职业等级|此处为空|此处为空|role_reg|此处为空|1|此处为空";
		
		BILogTemplateHelper.toTemplate(RoleCreatedLogTemplate);
	}
}
