package com.rw.service.log.template;

import java.util.Set;

import com.rw.service.log.template.maker.LogTemplate;

/**
 * 区账号注册
 * @author allen
 *
 */
public class CopyEndLogTemplate extends BILogTemplate{

	
//	final private String template="$logTime$|core_case|$loginZoneId$|$logTime$|case|$loginZoneId$|$regChannelId_uid$|$userId$|$userId$|$regSubChannelId$|$loginChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$|$level$|关卡入口|局次code|$copyId$|$copyStatus$|$operationCode$|$copyLevel$|$result$|fight_time:$fightTime$";
	
	final private String template=LogTemplate.CopyEndLogTemplate;
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}

	public static void main(String[] args) {
		//关卡结束
		String CopyEndLogTemplate = "打印时间|core_case|用户登录区ID|日志的触发时间|case|用户登录区ID|注册渠道ID_UID|UID|角色ID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|角色创建时间|当前游戏客户端版本|用户VIP等级|用户角色等级|用户战力|职业ID|职级|关卡入口|局次code|关卡code|关卡状态|操作码|1=普通关卡/2=精英关卡|0=失败/1=成功|fight_time";
		
		BILogTemplateHelper.toTemplate(CopyEndLogTemplate);
	}

}
