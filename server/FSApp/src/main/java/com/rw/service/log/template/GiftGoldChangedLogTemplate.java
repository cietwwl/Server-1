package com.rw.service.log.template;

import java.util.Set;

import com.rw.service.log.template.maker.LogTemplate;

/**
 * 区账号注册
 * @author allen
 *
 */
public class GiftGoldChangedLogTemplate extends BILogTemplate{

	
//	final private String template="$logTime$|core_coin|$loginZoneId$|$logTime$|sub_coin|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$|$level$|$scenceId$|$ItemChangedEventType_1$|$ItemChangedEventType_2$|$coinChanged$|$coinRemain$|1|";
	
	final private String template=LogTemplate.GiftGoldChangedLogTemplate;
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}
	
}
