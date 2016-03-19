package com.rw.service.log.template;

import java.util.Set;

/**
 * 区账号注册
 * @author allen
 *
 */
public class ZoneCountLevelSpreadLogTemplate extends BILogTemplate{

	
	final private String template="$logTime$|core_stat_2|$loginZoneId$|$logTime$|stat_role_level|$loginZoneId$|$regChannelId$|$level$|$levelCount$|$loginClientPlatForm$";
	
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}
	
	public static void main(String[] args) {
		String onlineCount = "打印时间|core_stat_2|用户登录区ID|日志的触发时间|stat_role_level|用户登录区ID|注册渠道ID|用户角色等级|角色统计|4=安卓/5=ios/7=wm";
		BILogTemplateHelper.toTemplate(onlineCount);
	}
}
