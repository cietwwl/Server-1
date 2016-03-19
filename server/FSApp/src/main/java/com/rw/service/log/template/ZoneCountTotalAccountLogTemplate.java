package com.rw.service.log.template;

import java.util.Set;

/**
 * 总帐号数
 * @author allen
 *
 */
public class ZoneCountTotalAccountLogTemplate extends BILogTemplate{

	
	final private String template="$logTime$|core_stat_1|$loginZoneId$|$logTime$|stat_account|$loginZoneId$|$regChannelId$|$totalAccount$|$loginClientPlatForm$";
	
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}
	
	public static void main(String[] args) {
		String onlineCount = "打印时间|core_stat_1|用户登录区ID|日志的触发时间|stat_account|用户登录区ID|注册渠道ID|总账号统计|4=安卓/5=ios/7=wm";
		
		BILogTemplateHelper.toTemplate(onlineCount);
	}
}
