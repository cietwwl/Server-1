package com.rw.service.log.template;

import java.util.Set;

import com.rw.service.log.template.maker.LogTemplate;

/**
 * 区账号注册
 * @author allen
 *
 */
public class ZoneRegLogTemplate extends BILogTemplate{

	
//	final private String template="$logTime$|core_gamesvr|$loginZoneId$|$logTime$|gamesvr_reg|$loginZoneId$|$regChannelId_uid$|$userId$|$regSubChannelId$|$loginChannelId$|$loginClientPlatForm$|$userCreatedTime$|$vip$|$phoneOp$|$loginNetType$|$loginPhoneType$|$clientVersion$|$loginClientIp$|$loginImei$|$loginImac$|||gamesvr_reg||1|";
	
	final private String template=LogTemplate.ZoneRegLogTemplate;
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);
	
	public String getTextTemplate() {
		return template;
	}

	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}
	
	public static void main(String[] args) {
		String ZoneRegLogTemplate = "打印时间|core_gamesvr|用户登录区ID|日志的触发时间|gamesvr_reg|用户登录区ID|注册渠道ID_UID|UID|用户注册子渠道|用户登录渠道|4（4=安卓/5=ios/7=wm）|区UID创建时间|用户VIP等级|手机运营商|wifi（2g/3g/4g/wifi等）|手机型号|客户端版本|ip地址，不包含端口|IMEI信息|mac地址|此处为空|此处为空|gamesvr_reg|此处为空|1|此处为空";
		
		BILogTemplateHelper.toTemplate(ZoneRegLogTemplate);
	}

}
