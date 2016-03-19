package com.rw.service.log.template;

import java.util.Set;

/**
 * 库存游戏币
 * @author allen
 *
 */
public class ZoneCountCoinLogTemplate extends BILogTemplate{

	
	final private String template="$logTime$|core_stat_1|$loginZoneId$|$logTime$|stat_sub_coin|$loginZoneId$|$regChannelId$|$zoneCoinRemain$|$loginClientPlatForm$";
	
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}
	
	public static void main(String[] args) {
		String onlineCount = "打印时间|core_stat_1|用户登录区ID|日志的触发时间|stat_sub_coin|用户登录区ID|注册渠道ID|游戏币余额|4=安卓/5=ios/7=wm";
		BILogTemplateHelper.toTemplate(onlineCount);
	}
}
