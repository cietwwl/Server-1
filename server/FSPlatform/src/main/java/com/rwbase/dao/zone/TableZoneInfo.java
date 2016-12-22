package com.rwbase.dao.zone;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_zone_info")
public class TableZoneInfo {

	@Id
	private int id;   			//自增长id
	private int zoneId;			//大区id 区的唯一标识
	private String zoneName;	//大区名称
	private String zonePath;    //大区路径
	//subzone 是用户注册的时候分配的，一个zone有一个或椟个subZone组成
	private String channelId; //渠道id
	private int subZone;
	private int status;  //状态  参考 LoginServiceProtos.java ZoneStatusType  0 hot 1 recommanded 2 new
	private int recommand;  //是否推荐  1是0否
//	private int chargeOpen;  //充值是否开放0 关闭 1 开放
	private int isSubZone;  //是否是被合并的分区 0 否 1 是
	private String serverIp; //对外Ip
	private String port;
	private String intranetIp;   //内网ip
	private String openTime;       //开服时间
	private String gmPort;         //gm开放端口
	private String closeTips;      //关闭提示语
	private int ucGiftRounterPort; //阿里直通车服监听端口

	public int getZoneId() {
		return zoneId;
	}
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	
	public String getZoneName() {
		return zoneName;
	}
	
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	public int getSubZone() {
		return subZone;
	}
	public void setSubZone(int subZone) {
		this.subZone = subZone;
	}

	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getRecommand() {
		return recommand;
	}
	public void setRecommand(int recommand) {
		this.recommand = recommand;
	}
	public int getIsSubZone() {
		return isSubZone;
	}
	public void setIsSubZone(int isSubZone) {
		this.isSubZone = isSubZone;
	}
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getZonePath() {
		return zonePath;
	}
	public void setZonePath(String zonePath) {
		this.zonePath = zonePath;
	}
	public String getIntranetIp() {
		return intranetIp;
	}
	public void setIntranetIp(String intranetIp) {
		this.intranetIp = intranetIp;
	}
	public String getOpenTime() {
		return openTime;
	}
	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}
	public String getGmPort() {
		return gmPort;
	}
	public void setGmPort(String gmPort) {
		this.gmPort = gmPort;
	}
	public String getCloseTips() {
		return closeTips;
	}
	public void setCloseTips(String closeTips) {
		this.closeTips = closeTips;
	}
	public int getUcGiftRounterPort() {
		return ucGiftRounterPort;
	}
	public void setUcGiftRounterPort(int ucGiftRounterPort) {
		this.ucGiftRounterPort = ucGiftRounterPort;
	}
}
