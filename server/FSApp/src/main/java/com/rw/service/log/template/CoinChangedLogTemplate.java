package com.rw.service.log.template;

import java.util.Set;

import com.rw.service.log.template.maker.LogTemplate;

/**
 * 区账号注册
 * @author allen
 *
 */
public class CoinChangedLogTemplate extends BILogTemplate{

	
//	final private String template="$logTime$|core_coin|$loginZoneId$|$logTime$|sub_coin|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$|$level$|$scenceId$|$ItemChangedEventType_1$|$ItemChangedEventType_2$|$coinChanged$|$coinRemain$|1|";
	
	final private String template=LogTemplate.CoinChangedLogTemplate;
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}


	public static void main(String[] args) {
		//注意第二个物品栏要手动加"_2"
		String CoinChangedLogTemplate = "打印时间|core_coin|用户登录区ID|日志的触发时间|sub_coin|用户登录区ID|注册渠道ID_UID|UID|角色ID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|角色创建时间|当前游戏客户端版本|用户VIP等级|用户角色等级|用户战力|职业ID|职业等级|场景id/地图id|一级变动原因|二级变动原因|游戏币新增消耗数量（新增为正数，消耗为负数）|变动后游戏币个人持有量|1|此处留空";
		BILogTemplateHelper.toTemplate(CoinChangedLogTemplate);
	}
	
}
