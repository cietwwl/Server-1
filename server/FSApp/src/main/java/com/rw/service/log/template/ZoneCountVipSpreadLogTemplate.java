package com.rw.service.log.template;

import java.util.Set;

import com.rw.service.log.template.maker.LogTemplate;

/**
 * 区账号注册
 * @author allen
 *
 */
public class ZoneCountVipSpreadLogTemplate extends BILogTemplate{

	
//	final private String template="$logTime$|core_stat_2|$loginZoneId$|$logTime$|stat_account_vip|$loginZoneId$|$regChannelId$|$vip$|$totalAccount$|$loginClientPlatForm$";
	
	final private String template=LogTemplate.ZoneCountVipSpreadLogTemplate;
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}
	
	public static void main(String[] args) {
		String ZoneCountVipSpreadLogTemplate = "打印时间|core_stat_2|用户登录区ID|日志的触发时间|stat_account_vip|用户登录区ID|注册渠道ID|用户VIP等级|总账号统计|4=安卓/5=ios/7=wm";
		BILogTemplateHelper.toTemplate(ZoneCountVipSpreadLogTemplate);
	}
}
