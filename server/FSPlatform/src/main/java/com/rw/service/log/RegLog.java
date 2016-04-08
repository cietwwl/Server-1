package com.rw.service.log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.json.JSONObject;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.service.log.infoPojo.ClientInfo;


@JsonIgnoreProperties(ignoreUnknown = true)
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
    private String zoneId = "0";   //区id
    private String logactiveTime = "";  //日志的触发时间
    
    private String subChannelId = "";//子渠道

	/// <summary>
    /// 平台,客户端发来的变量名有问题,此处弃用,使用clientplatform的变量
    /// </summary>
    private String platformType;



	/**运营商*/
	private String carrier = "";    

    private String networkType = "";  //网路环境
    /**终端品牌*/
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
  /**cpu核数*/
    private String cpuKernal = "";  

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
   
    /**分辨率*/
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
    
	final private static Field[] fieldList;
	
	static{
		fieldList = RegLog.class.getDeclaredFields();
		for (Field field : fieldList) {
			field.setAccessible(true);
		}
	}
    
	public Map<String, String> getInfoMap(Map<String, String> infoMap) throws Exception {
		if(infoMap == null)
			infoMap = new HashMap<String, String>();
		for (Field field : fieldList) {
			Object value = field.get(this);
			if(value!=null){
				infoMap.put(field.getName(), value.toString());
			}
		}
		return infoMap;
	}


	
	public static RegLog fromJson(String json){
		
		RegLog reglog = JsonUtil.readValue(json, RegLog.class);
		if(reglog!=null){
		}else{
			reglog = new RegLog();
		}
		
		return reglog;
	}
    
    public String getUid() {
		return uid;
	}

	public String getSubChannelId() {
		return subChannelId;
	}

	
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
    public String getPlatformType() {
		return platformType;
	}

	public void setPlatformType(String platformType) {
		this.platformType = platformType;
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
					this.subChannelId = LogService.getInstance().parseJson(json,"subChannelId");
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
	
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public void setPrintTime(String printTime) {
		this.printTime = printTime;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setUidCreateTime(String uidCreateTime) {
		this.uidCreateTime = uidCreateTime;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public void setLogactiveTime(String logactiveTime) {
		this.logactiveTime = logactiveTime;
	}

	public void setSubChannelId(String subChannelId) {
		this.subChannelId = subChannelId;
	}



	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public void setSdk_id(String sdk_id) {
		this.sdk_id = sdk_id;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public void setCpuType(String cpuType) {
		this.cpuType = cpuType;
	}

	public void setCpuFrequency(String cpuFrequency) {
		this.cpuFrequency = cpuFrequency;
	}

	public void setCpuKernal(String cpuKernal) {
		this.cpuKernal = cpuKernal;
	}

	public void setGpuType(String gpuType) {
		this.gpuType = gpuType;
	}

	public void setGpuFrequency(String gpuFrequency) {
		this.gpuFrequency = gpuFrequency;
	}

	public void setGpuKernal(String gpuKernal) {
		this.gpuKernal = gpuKernal;
	}

	public void setRam(String ram) {
		this.ram = ram;
	}

	public void setFreeRam(String freeRam) {
		this.freeRam = freeRam;
	}

	public void setEnoughRam(String enoughRam) {
		this.enoughRam = enoughRam;
	}

	public void setHardRam(String hardRam) {
		this.hardRam = hardRam;
	}

	public void setFreeHardRam(String freeHardRam) {
		this.freeHardRam = freeHardRam;
	}

	public void setSdSize(String sdSize) {
		this.sdSize = sdSize;
	}

	public void setFreeSdSize(String freeSdSize) {
		this.freeSdSize = freeSdSize;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public void setBaseband(String baseband) {
		this.baseband = baseband;
	}

	public void setKernal(String kernal) {
		this.kernal = kernal;
	}

	public void setOpenGL_RENDERER(String openGL_RENDERER) {
		OpenGL_RENDERER = openGL_RENDERER;
	}

	public void setOpenGL_VENDOR(String openGL_VENDOR) {
		OpenGL_VENDOR = openGL_VENDOR;
	}

	public void setOpenGL_VERSION(String openGL_VERSION) {
		OpenGL_VERSION = openGL_VERSION;
	}

	public void setStatistical(String statistical) {
		this.statistical = statistical;
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
					+ clientInfo.getChannelId() + "_" + clientInfo.getAccountId() + "||" + subChannelId + "|" + subChannelId
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
