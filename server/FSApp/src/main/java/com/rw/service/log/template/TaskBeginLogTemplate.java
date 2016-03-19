package com.rw.service.log.template;

import java.util.Set;

/**
 * 区账号注册
 * @author allen
 *
 */
public class TaskBeginLogTemplate extends BILogTemplate{

	
	final private String template="$logTime$|core_task|$loginZoneId$|$logTime$|task|$loginZoneId$|$regChannelId_uid$|uid|$userId$|$regSubChannelId$|$loginChannelId$|$loginClientPlatForm$|$userCreatedTime$|$roleCreatedTime$|$clientVersion$|$vip$|$level$|$fighting$|$careerType$|$level$|||$taskId$|$biTaskType$|task_start||$result$|";
	
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}


	public static void main(String[] args) {
		//关卡开始
		String copyBegin = "打印时间|core_task|用户登录区ID|日志的触发时间|task|用户登录区ID|账号唯一识别符|uid|角色ID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|角色创建时间|当前游戏客户端版本|用户VIP等级|用户角色等级|用户战力|职业ID|职级|此处为空|此处为空|任务ID|1=主线/2=支线|task_start|此处为空|0=失败/1=成功|空";
		BILogTemplateHelper.toTemplate(copyBegin);
	}
	
}
