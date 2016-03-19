package com.rw.service.log;

import com.rw.fsutil.json.JSONObject;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.log.infoPojo.ClientInfo;


public class RegLog implements ILog{


	private String gameName = "";
	/// <summary>
    /// 日志名称
    /// </summary>
    private String logName = "user_info";
    /// <summary>
    /// 主题ID-
    /// </summary>
    private String topicId = "model_reg";

    private String printTime = "";   //打印时间

	private String uid = "";
    private String uidCreateTime = "";
    private String zoneId = "";   //区id
    private String logactiveTime = "";  //日志的触发时间
    
    private String subChannel = "";//子渠道
    /// <summary>
    /// 平台
    /// </summary>
    private String platformType;

    public String getPlatformType() {
		return platformType;
	}

	public void setPlatformType(String platformType) {
		this.platformType = platformType;
	}

	private String carrier = "";    //运营商

    private String networkType = "";  //网路环境
    /// <summary>
    /// 终端品牌
    /// </summary>
    private String brandName = "";
    
    private String terminalType = "";

    private String clientVersion = "";  //游戏客户端版本


    private String ip = "";

    private String imei = "";

    private String mac = "";
    
    private String sdkVersion = "";  //sdk版本

    private String sdk_id = "";  //sdk用户唯一识别码

    private String systemVersion = "";  //操作系统版本号
    /// <summary>
    /// 安卓/ios版本
    /// </summary>
    private String systemType = "";

    private String cpu = ""; //处理器

    private String cpuType = ""; //cpu型号

    private String cpuFrequency = ""; //cpu频率

    private String cpuKernal = "";  //cpu核数

    private String gpuType = "";   //gpu类型

    private String gpuFrequency = "";  //gpu频率

    private String gpuKernal = "";   //gpu核数

    private String ram = "";  //运行内存

    private String freeRam = "";  //终端当前空闲内存空间大小
    /// <summary>
    /// 是否内存不足
    /// </summary>
    private String enoughRam = "1";

    /// <summary>
    /// 机身存储
    /// </summary>
    private String hardRam = "";
    /// <summary>
    /// 终端当前空闲内置存储空间大小
    /// </summary>
    private String freeHardRam = "";
    /// <summary>
    /// 终端最大sd卡存储空间大小
    /// </summary>
    private String sdSize = "";
    /// <summary>
    /// 终端当前空闲sd卡存储空间大小
    /// </summary>
    private String freeSdSize = "";
    /// <summary>
    /// 分辨率
    /// </summary>
    private String resolution = "";
    /// <summary>
    /// 基带版本
    /// </summary>
    private String baseband = "";
    /// <summary>
    /// 内核版本
    /// </summary>
    private String kernal = "";
    /// <summary>
    /// OpenGL_RENDERER
    /// </summary>
    private String OpenGL_RENDERER = "";
    /// <summary>
    /// OpenGL_VENDOR
    /// </summary>
    private String OpenGL_VENDOR = "";
    /// <summary>
    /// OpenGL_VERSION
    /// </summary>
    private String OpenGL_VERSION = "";
    /// <summary>
    /// 统计字段
    /// </summary>
    private String statistical = "";
	
	public String getLogName() {
		return logName;
	}

	public String getTopicId() {
		return topicId;
	}

	public String getPrintTime() {
		return printTime;
	}

	public String getZoneId() {
		return zoneId;
	}

	public String getCarrier() {
		return carrier;
	}

	public String getNetworkType() {
		return networkType;
	}

	public String getBrandName() {
		return brandName;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public String getIp() {
		return ip;
	}

	public String getImei() {
		return imei;
	}

	public String getMac() {
		return mac;
	}

	public String getSdkVersion() {
		return sdkVersion;
	}

	public String getSdk_id() {
		return sdk_id;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public String getSystemType() {
		return systemType;
	}

	public String getCpu() {
		return cpu;
	}

	public String getCpuType() {
		return cpuType;
	}

	public String getCpuFrequency() {
		return cpuFrequency;
	}

	public String getCpuKernal() {
		return cpuKernal;
	}

	public String getGpuType() {
		return gpuType;
	}

	public String getGpuFrequency() {
		return gpuFrequency;
	}

	public String getGpuKernal() {
		return gpuKernal;
	}

	public String getRam() {
		return ram;
	}

	public String getFreeRam() {
		return freeRam;
	}

	public String getEnoughRam() {
		return enoughRam;
	}

	public String getHardRam() {
		return hardRam;
	}

	public String getFreeHardRam() {
		return freeHardRam;
	}

	public String getSdSize() {
		return sdSize;
	}

	public String getFreeSdSize() {
		return freeSdSize;
	}

	public String getResolution() {
		return resolution;
	}

	public String getBaseband() {
		return baseband;
	}

	public String getKernal() {
		return kernal;
	}

	public String getOpenGL_RENDERER() {
		return OpenGL_RENDERER;
	}

	public String getOpenGL_VENDOR() {
		return OpenGL_VENDOR;
	}

	public String getOpenGL_VERSION() {
		return OpenGL_VERSION;
	}

	public String getStatistical() {
		return statistical;
	}
	
	public void parseLog(String value) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				if (value == null || value.length() <= 0) {
					return;
				}
				try {
					JSONObject json = new JSONObject(value);
					this.gameName = LogService.getInstance().parseJson(json, "gameName");
					this.logName = LogService.getInstance().parseJson(json,"logName");
					this.topicId = LogService.getInstance().parseJson(json,"topicId");
					this.carrier = LogService.getInstance().parseJson(json,"carrier");
					this.subChannel = LogService.getInstance().parseJson(json,"subChannelId");
					this.networkType = LogService.getInstance().parseJson(json,"networkType");
					this.brandName = LogService.getInstance().parseJson(json,"brandName");
					this.terminalType = LogService.getInstance().parseJson(json,"terminalType");
					this.clientVersion = LogService.getInstance().parseJson(json,"clientVersion");
					this.ip = LogService.getInstance().parseJson(json,"ip");
					this.imei = LogService.getInstance().parseJson(json,"imei");
					this.mac = LogService.getInstance().parseJson(json,"mac");
					this.platformType = LogService.getInstance().parseJson(json, "platformType");
					this.sdkVersion = LogService.getInstance().parseJson(json,"sdkVersion");
					this.sdk_id = LogService.getInstance().parseJson(json,"sdk_id");
					this.systemVersion = LogService.getInstance().parseJson(json,"systemVersion");
					this.systemType = LogService.getInstance().parseJson(json,"systemType");
					this.cpu = LogService.getInstance().parseJson(json,"cpu");
					this.cpuType = LogService.getInstance().parseJson(json,"cpuType");
					this.cpuFrequency = LogService.getInstance().parseJson(json,"cpuFrequency");
					this.cpuKernal = LogService.getInstance().parseJson(json,"cpuKernal");
					this.gpuType = LogService.getInstance().parseJson(json,"gpuType");
					this.gpuFrequency = LogService.getInstance().parseJson(json,"gpuFrequency");
					this.gpuKernal = LogService.getInstance().parseJson(json,"gpuKernal");
					this.ram = LogService.getInstance().parseJson(json,"ram");
					this.freeRam = LogService.getInstance().parseJson(json,"freeRam");
					this.enoughRam = LogService.getInstance().parseJson(json,"enoughRam");
					this.hardRam = LogService.getInstance().parseJson(json,"hardRam");
					this.freeHardRam = LogService.getInstance().parseJson(json,"freeHardRam");
					this.sdSize = LogService.getInstance().parseJson(json,"sdSize");
					this.freeSdSize = LogService.getInstance().parseJson(json,"freeSdSize");
					this.resolution = LogService.getInstance().parseJson(json,"resolution");
					this.baseband = LogService.getInstance().parseJson(json,"baseband");
					this.kernal = LogService.getInstance().parseJson(json,"kernal");
					this.OpenGL_RENDERER = LogService.getInstance().parseJson(json,"OpenGL_RENDERER");
					this.OpenGL_VENDOR = LogService.getInstance().parseJson(json,"OpenGL_VENDOR");
					this.OpenGL_VERSION = LogService.getInstance().parseJson(json,"OpenGL_VERSION");
					this.statistical = LogService.getInstance().parseJson(json,"statistical");
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
	}
	
	public void fillInfoToClientInfo(ClientInfo clientInfo){
		if(clientInfo!=null){
			clientInfo.setSdkVersion(this.sdkVersion);
			clientInfo.setSdk_id(this.sdk_id);
		}
	}

	public void setLogValue(Object... values) {
		// TODO Auto-generated method stub
		this.uid = values[0].toString();
		this.uidCreateTime = DateUtils.getDateTimeFormatString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
	}

	public String logToString(ClientInfo clientInfo) {
		if(clientInfo==null){
			return "";
		}
		this.printTime = DateUtils.getDateTimeFormatString(
				System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
		String body = "";
		try {
			
			body = printTime + "|" + logName + "|" + zoneId + "|"
					+ printTime + "|" + topicId + "|" + zoneId + "|"
					+ clientInfo.getChannelId() + "_" + clientInfo.getAccountId() + "||" + subChannel + "|" + subChannel
					+ "|" + platformType + "|" + uidCreateTime + "|" + carrier
					+ "|" + networkType + "|" + brandName + "|" + terminalType
					+ "|" + clientVersion + "|" + ip + "|" + imei + "|" + mac
					+ "|" + sdkVersion + "|" + sdk_id + "|" + systemVersion
					+ "|" + systemType + "|" + cpu + "|" + cpuType + "|"
					+ cpuFrequency + "|" + cpuKernal + "|" + gpuType + "|"
					+ gpuFrequency + "|" + gpuKernal + "|" + ram + "|"
					+ freeRam + "|" + enoughRam + "|" + hardRam + "|"
					+ freeHardRam + "|" + sdSize + "|" + freeSdSize + "|"
					+ resolution + "|" + baseband + "|" + kernal + "|"
					+ OpenGL_RENDERER + "|" + OpenGL_VENDOR + "|"
					+ OpenGL_VERSION + "|" + statistical;
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return body;
	}

	
}
