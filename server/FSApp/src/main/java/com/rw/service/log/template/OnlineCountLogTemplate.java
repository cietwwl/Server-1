package com.rw.service.log.template;

import java.util.Set;

import com.rw.service.log.template.maker.LogTemplate;

/**
 * 区账号注册
 * @author allen
 *
 */
public class OnlineCountLogTemplate extends BILogTemplate{

	
//	final private String template="$logTime$|core_stat_1|$loginZoneId$|$logTime$|stat_role_online|$loginZoneId$|$regChannelId$|$onlineCount$|$loginClientPlatForm$";
	
	final private String template=LogTemplate.OnlineCountLogTemplate;
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}
	
	public static void main(String[] args) {
		String OnlineCountLogTemplate = "打印时间|core_stat_1|用户登录区ID|日志的触发时间|stat_role_online|用户登录区ID|注册渠道ID|同时在线用户数|4=安卓/5=ios/7=wm";
		BILogTemplateHelper.toTemplate(OnlineCountLogTemplate);
	}
}
