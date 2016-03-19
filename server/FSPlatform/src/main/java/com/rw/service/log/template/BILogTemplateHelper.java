package com.rw.service.log.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class BILogTemplateHelper {

	
	final static private String NAME_SPLIT = "\\$\\|";
	
	public static Set<String> getInfoNameSet(String template){
		
		Set<String> nameSet = new HashSet<String>();
		String[] infoNameSplit = template.split(NAME_SPLIT);
		for (String infoName : infoNameSplit) {
			infoName = StringUtils.substringAfter(infoName, "$");
			infoName = StringUtils.substringBefore(infoName, "$");
			if(StringUtils.isNotBlank(infoName)){
				nameSet.add(infoName);
			}
		}
		return nameSet;
	}
	
	private static Map<String,String> docToTemplateMap = new HashMap<String,String>();
	
	private static List<String> repalceOrderList = new ArrayList<String>();
	
	static {
		addTemplateToken("4=安卓/5=ios/7=wm", "$clientPlatForm$");
		addTemplateToken("2g/3g/4g/wifi等", "$netType$");
		addTemplateToken("注册渠道ID_UID", "$regChannelId_uid$");
		addTemplateToken("当前游戏客户端版本", "$clientVersion$");
		addTemplateToken("ip地址（不包含端口）", "$clientIp$");
		addTemplateToken("0=失败/1=成功", "$result$");
		addTemplateToken("日志的触发时间", "$logTime$");
		addTemplateToken("用户注册子渠道", "$subChannelId$");
		addTemplateToken("用户登录区ID", "0");
		addTemplateToken("UID创建时间", "$registerTime$");
		addTemplateToken("用户登录渠道", "$subChannelId$");
		addTemplateToken("手机运营商", "$phoneOp$");
		addTemplateToken("打印时间", "$logTime$");
		addTemplateToken("手机型号", "$phoneType$");
		addTemplateToken("此处为空", "");
		addTemplateToken("IMEI信息", "$imei$");
		addTemplateToken("mac地址", "$imac$");
		addTemplateToken("sdk版本", "$sdkVersion$");
		addTemplateToken("sdk_id", "$sdk_id$");
		addTemplateToken("UID", "");
		addTemplateToken("为空", "");
		
	}
	
	private static void addTemplateToken(String token, String template){
		docToTemplateMap.put(token, template);
		repalceOrderList.add(token);
	}
	
	//文档到可处理template的转换
	public static void toTemplate(String original){
		String template = original;
		for (String nameTmp : repalceOrderList) {
			template = template.replace(nameTmp, docToTemplateMap.get(nameTmp));
		}
		
		System.out.println(template);
	}
	
	public static void main(String[] args) {
		String zoneReg = "打印时间|core_gamesvr|用户登录区ID|日志的触发时间|gamesvr_reg|用户登录区ID|注册渠道ID_UID|UID|用户注册子渠道|用户登录渠道|4（4=安卓/5=ios/7=wm）|区UID创建时间|用户VIP等级|手机运营商|wifi（2g/3g/4g/wifi等）|手机型号|客户端版本|ip地址，不包含端口|IMEI信息|mac地址|此处为空|此处为空|gamesvr_reg|此处为空|1|此处为空";
		//区账号登录
		String zoneLogin = "打印时间|core_gamesvr|用户登录区ID|日志的触发时间|gamesvr_act|用户登录区ID|注册渠道ID_UID|UID|用户注册子渠道|用户登录渠道|4（4=安卓/5=ios/7=wm）|区UID创建时间|用户VIP等级|手机运营商|wifi(2g/3g/4g/wifi等)|手机型号|客户端版本|ip地址，不包含端口|IMEI信息|mac地址|此处为空|此处为空|gamesvr_login|此处为空|失败为0，成功为1|此处为空";
		//区账号登出
		String zoneLogout = "打印时间|core_gamesvr|用户登录区ID|日志的触发时间|gamesvr_act|用户登录区ID|注册渠道ID_UID|UID|用户注册子渠道|用户登录渠道|4（4=安卓/5=ios/7=wm）|区UID创建时间|用户VIP等级|手机运营商|wifi(2g/3g/4g/wifi等)|手机型号|客户端版本|ip地址，不包含端口|IMEI信息|mac地址|此处为空|此处为空|gamesvr_logout|此处为空|online_time";
		//角色注册
		String roleCreated = "打印时间|core_role|用户登录区ID|日志的触发时间|role_reg|用户登录区ID|注册渠道ID_UID|UID|角色ID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|角色创建时间|当前游戏客户端版本|用户VIP等级|用户角色等级|用户战力|职业ID|职业等级|此处为空|此处为空|role_reg|此处为空|1|此处为空";
		//在线用户数
		String onlineCount = "打印时间|core_stat_1|用户登录区ID|日志的触发时间|stat_role_online|用户登录区ID|注册渠道ID|同时在线用户数|4=安卓/5=ios/7=wm";
		//关卡开始
		String copyBegin = "打印时间|core_case|用户登录区ID|日志的触发时间|case|用户登录区ID|注册渠道ID_UID|UID|角色ID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|角色创建时间|当前游戏客户端版本|用户VIP等级|用户角色等级|用户战力|职业ID|职级|关卡入口|局次code|关卡code|此处为空|case_start|此处为空|0=失败/1=成功|为空";
		//关卡结束
		String copyEnd = "打印时间|core_case|用户登录区ID|日志的触发时间|case|用户登录区ID|注册渠道ID_UID|UID|角色ID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|角色创建时间|当前游戏客户端版本|用户VIP等级|用户角色等级|用户战力|职业ID|职级|关卡入口|局次code|关卡code|此处为空|case_finish|此处为空|0=失败/1=成功|fight_time";
		
		BILogTemplateHelper.toTemplate(copyEnd);
	}
}
