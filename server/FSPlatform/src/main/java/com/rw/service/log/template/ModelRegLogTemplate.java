package com.rw.service.log.template;

import java.util.Set;

public class ModelRegLogTemplate extends BILogTemplate{

	
	final private String template="$logTime$|user_info|0|$logTime$|model_reg|0|$regChannelId_uid$|$accountId$|$subChannelId$|$subChannelId$|$clientPlatForm$|$logTime$|$phoneOp$|$netType$|$brandName$|$phoneType$|$clientVersion$|$clientIp$|$imei$|$imac$|$sdkVersion$|$sdk_id$|$operateingSystemVersion$|$systemType$|$cpu$|$cpuType$|$cpuFrequency$|$cpuKernal$|$gpuType$|$gpuFrequency$|$gpuKernal$|$ram$|$freeRam$|$enoughRam$|$hardRam$|$freeHardRam$|$sdSize$|$freeSdSize$|$resolution$|$baseband$|$kernal$|$OpenGL_RENDERER$|$OpenGL_VENDOR$|$OpenGL_VERSION$|此处留空|$adLinkId$|$systemVersion$";
	

	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);
	
	public String getTextTemplate() {
		return template;
	}

	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}
	
	public static void main(String[] args) {
		String zoneReg = "打印时间|user_info|用户登录区ID|日志的触发时间|model_reg|用户登录区ID|注册渠道ID_UID|UID|用户注册子渠道|用户登录子渠道|4=安卓/5=ios/7=wm|uid创建时间|手机运营商|2g/3g/4g/wifi等|终端品牌|手机型号|当前游戏客户端版本|ip地址（不包含端口）|IMEI信息|mac地址|sdk版本|sdk_id|操作系统版本号|android/ios|处理器|cpu型号|cpu频率|cpu核数|gpu类型|gpu频率|gpu核数|运行内存|终端当前空闲内存空间大小|0=内存足够/1=内存不足|机身存储|终端当前空闲内置存储空间大小|终端最大sd卡存储空间大小|终端当前空闲sd卡存储空间大小|分辨率|基带版本|内核版本|OpenGL_RENDERER|OpenGL_VENDOR|OpenGL_VERSION|此处留空|广告短链接id|操作系统版本号";
		
		BILogTemplateHelper.toTemplate(zoneReg);
	}

}