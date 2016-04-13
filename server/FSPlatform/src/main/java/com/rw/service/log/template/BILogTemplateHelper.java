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
		addTemplateToken("终端当前空闲内置存储空间大小", "$freeHardRam$");		
		addTemplateToken("终端当前空闲sd卡存储空间大小", "$freeSdSize$");
		addTemplateToken("终端当前空闲内存空间大小", "$freeRam$");
		addTemplateToken("0=内存足够/1=内存不足", "$enoughRam$");
		addTemplateToken("终端最大sd卡存储空间大小", "$sdSize$");
		addTemplateToken("OpenGL_RENDERER", "$OpenGL_RENDERER$");		
		addTemplateToken("4=安卓/5=ios/7=wm", "$clientPlatForm$");
		addTemplateToken("OpenGL_VERSION", "$OpenGL_VERSION$");
		addTemplateToken("2g/3g/4g/wifi等", "$netType$");
		addTemplateToken("OpenGL_VENDOR", "$OpenGL_VENDOR$");
		addTemplateToken("注册渠道ID_UID", "$regChannelId_uid$");
		addTemplateToken("当前游戏客户端版本", "$clientVersion$");
		addTemplateToken("ip地址（不包含端口）", "$clientIp$");
		addTemplateToken("android/ios", "$systemType$");
		
		addTemplateToken("0=失败/1=成功", "$result$");
		addTemplateToken("用户登录子渠道", "$subChannelId$");
		addTemplateToken("操作系统版本号", "$systemVersion$");
		addTemplateToken("日志的触发时间", "$logTime$");
		addTemplateToken("用户注册子渠道", "$subChannelId$");
		addTemplateToken("用户登录区ID", "0");
		addTemplateToken("uid创建时间", "$logTime$");
		
		addTemplateToken("手机运营商", "$phoneOp$");
		addTemplateToken("IMEI信息", "$imei$");
		addTemplateToken("mac地址", "$imac$");
		addTemplateToken("sdk版本", "$sdkVersion$");
		addTemplateToken("sdk_id", "$sdk_id$");
		addTemplateToken("cpu型号", "$cpuType$");
		addTemplateToken("cpu频率", "$cpuFrequency$");
		addTemplateToken("cpu核数", "$cpuKernal$");
		addTemplateToken("gpu类型", "$gpuType$");
		addTemplateToken("gpu频率", "$gpuFrequency$");
		addTemplateToken("gpu核数", "$gpuKernal$");
		
		
		addTemplateToken("打印时间", "$logTime$");
		addTemplateToken("手机型号", "$phoneType$");
		addTemplateToken("终端品牌", "$brandName$");
		addTemplateToken("运行内存", "$ram$");
		addTemplateToken("机身存储", "$hardRam$");
		addTemplateToken("此处为空", "");
		addTemplateToken("基带版本", "$baseband$");
		addTemplateToken("内核版本", "$kernal$");
		addTemplateToken("分辨率", "$resolution$");
		addTemplateToken("处理器", "$cpu$");
		
		
		addTemplateToken("UID", "$accountId$");
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
	
}
