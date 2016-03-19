package com.rw.service.log.template;

import java.util.Set;

/**
 * 区账号注册
 * @author allen
 *
 */
public class AccountLogoutLogTemplate extends BILogTemplate{

	
	final private String template="$logTime$|core_account|$loginZoneId$|$logTime$|account_act|$loginZoneId$|$regChannelId_uid$|$userId$|$regSubChannelId$|$loginChannelId$|$loginClientPlatForm$|$userCreatedTime$|$phoneOp$|$loginNetType$|$loginPhoneType$|$clientVersion$|$loginClientIp$|$loginImei$|$loginImac$|||||account_logout||$result$|online_time:$onlineTime$";
	
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}

	public static void main(String[] args) {
		//区账号登出
		String zoneLogout = "打印时间|core_account|用户登录区ID|日志的触发时间|account_act|用户登录区ID|注册渠道ID_UID|UID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|手机运营商|2g/3g/4g/wifi等|手机型号|当前游戏客户端版本|ip地址（不包含端口）|IMEI信息|mac地址|sdk版本|sdk_id|此处为空|此处为空|account_logout|此处为空|0=失败/1=成功|online_time";
		BILogTemplateHelper.toTemplate(zoneLogout);
	}

}
