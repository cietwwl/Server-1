package com.rw.account;

import com.rw.platform.PlatformFactory;
import com.rwbase.common.enu.EServerStatus;
import com.rwbase.dao.zone.TableZoneInfo;

public class ZoneInfoCache {
	
	private final static long SHUTDOWNTIME = 10*1000;
	
	private int zoneId;
	private String zoneName;
	//subzone 是用户注册的时候分配的，一个zone有一个或椟个subZone组成
	private String channelId; //渠道id
	private int subZone;
	private int status;  //状态  参考 LoginServiceProtos.java ZoneStatusType  0 hot 1 recommanded 2 new
	private int recommand;  //是否推荐  1是0否
	private int weight;   //权重，大的排前面
	private long startTime; //分区开始时间，这个时间会自动把enable设成1
	private int isOpen;  //是否对外开放0 关闭 1 开放
	private int chargeOpen;  //充值是否开放0 关闭 1 开放
	private int isSubZone;  //是否是被合并的分区 0 否 1 是
	private String serverIp;
	private String port;
	private int onlineNum;   //在线人数
	private String closeTips; //关闭提示语
	
	private long notifyTime; 	//服务器响应时间
	private boolean blnUpdate = false;  //是否更新
	
	
	public ZoneInfoCache(TableZoneInfo tableZoneInfo){
		this.zoneId = tableZoneInfo.getZoneId();
		this.zoneName = tableZoneInfo.getZoneName();
		this.channelId = tableZoneInfo.getChannelId();
		this.subZone = tableZoneInfo.getSubZone();
		this.status = tableZoneInfo.getStatus();
		this.recommand = tableZoneInfo.getRecommand();
		this.isSubZone = tableZoneInfo.getIsSubZone();
		this.serverIp = tableZoneInfo.getServerIp();
		this.port = tableZoneInfo.getPort();
	}

	public void updateZoneCache(TableZoneInfo tableZoneInfo){
		this.zoneId = tableZoneInfo.getZoneId();
		this.zoneName = tableZoneInfo.getZoneName();
		this.channelId = tableZoneInfo.getChannelId();
		this.subZone = tableZoneInfo.getSubZone();
		this.recommand = tableZoneInfo.getRecommand();
		this.isSubZone = tableZoneInfo.getIsSubZone();
		this.serverIp = tableZoneInfo.getServerIp();
		this.port = tableZoneInfo.getPort();
	}
	

	public boolean getIsOpen(int status) {
		return EServerStatus.isOpen(status);
	}


	public void setIsOpen(int isOpen) {
		this.isOpen = isOpen;
	}


	public int getOnlineNum() {
		return onlineNum;
	}


	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}


	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}


	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public void setRecommand(int recommand) {
		this.recommand = recommand;
	}


	public void setWeight(int weight) {
		this.weight = weight;
	}


	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}


	public void setChargeOpen(int chargeOpen) {
		this.chargeOpen = chargeOpen;
	}


	public void setIsSubZone(int isSubZone) {
		this.isSubZone = isSubZone;
	}


	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}


	public void setPort(String port) {
		this.port = port;
	}


	public void setOnlineNum(int onlineNum) {
		this.onlineNum = onlineNum;
	}


	public int getZoneId() {
		return zoneId;
	}


	public String getZoneName() {
		return zoneName;
	}


	public String getChannelId() {
		return channelId;
	}


	public int getSubZone() {
		return subZone;
	}

	public void setSubZone(int subZone) {
		this.subZone = subZone;
	}


	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		if(this.status != status){
			this.status = status;
			blnUpdate = true;
		}
	}


	public String getStatusDesc(){
		EServerStatus eServerStatus = EServerStatus.getStatus(status);
		return eServerStatus.getStatusDes();
	}
	
	public int getStatusColor(int status){
		EServerStatus eServerStatus = EServerStatus.getStatus(status);
		return eServerStatus.getColor().getValue();
	}

	public int getRecommand() {
		return recommand;
	}


	public int getWeight() {
		return weight;
	}


	public long getStartTime() {
		return startTime;
	}


	public int getChargeOpen() {
		return chargeOpen;
	}


	public int getIsSubZone() {
		return isSubZone;
	}


	public String getServerIp() {
		return serverIp;
	}


	public String getPort() {
		return port;
	}
	
	public long getNotifyTime() {
		return notifyTime;
	}


	public void setNotifyTime(long notifyTime) {
		this.notifyTime = notifyTime;
	}
	
	public void checkStatus(){
		if(System.currentTimeMillis() - this.notifyTime > SHUTDOWNTIME){
			setStatus(EServerStatus.CLOSE.getStatusId());
		}
	}
	

	public String getCloseTips() {
		return closeTips;
	}

	public void setCloseTips(String closeTips) {
		if(this.closeTips == null || !this.closeTips.equals(closeTips)){
			this.closeTips = closeTips;
			blnUpdate = true;
		}
		
	}


	/**
	 * 更新区信息进数据库
	 */
	public void update(){
		if(blnUpdate){
			blnUpdate = false;
			PlatformFactory.getPlatformService().updateZoneInfo(this);
		}
	}
}
