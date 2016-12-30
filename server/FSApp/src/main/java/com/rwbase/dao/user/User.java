package com.rwbase.dao.user;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.IgnoreUpdate;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rwbase.common.enu.ESex;
import com.rwbase.dao.user.readonly.TableUserIF;

@JsonIgnoreProperties(ignoreUnknown = false)
@Table(name = "user")
@SynClass
public class User implements TableUserIF {
	@Id
	private String userId;
	@IgnoreUpdate
	private int zoneId;
	private int vip;
	private int sex;
	@IgnoreUpdate
	private String account;
	@IgnoreUpdate
	private String userName;
	private String headImage;
	@IgnoreUpdate
	private long createTime; // 创建的时间
	@IgnoreUpdate
	private String openAccount;//

	private long lastLoginTime; // 上次登录时间

	// 踢出的用户冷却时间
	private long kickOffCoolTime;

	@SaveAsJson
	private ZoneRegInfo zoneRegInfo;

	@SaveAsJson
	private UserExtendInfo extendInfo;

	@SaveAsJson
	private ZoneLoginInfo zoneLoginInfo;

	// 冗余，和mainRoleHero的exp对齐
	private long exp;
	// 用于后台数据查询
	private int level;
	@IgnoreUpdate
	private boolean isRobot;// 是否是机器人

	@NonSave
	private String channelId;// 渠道Id

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getZoneId() {
		return zoneId;
	}

	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHeadImageWithDefault() {
		// 临时解决数据问题
		return getHeadImage();
	}

	public String getHeadImage() {
		// 临时解决数据问题
		if (headImage == null || headImage.isEmpty()) {
			if (sex == ESex.Men.getOrder()) {
				headImage = "10001";
			} else {
				headImage = "10002";
			}
		}
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public long getKickOffCoolTime() {
		return kickOffCoolTime;
	}

	public void setKickOffCoolTime(long kickOffCoolTime) {
		this.kickOffCoolTime = kickOffCoolTime;
	}

	public ZoneRegInfo getZoneRegInfo() {
		return zoneRegInfo;
	}

	public void setZoneRegInfo(ZoneRegInfo zoneRegInfo) {
		this.zoneRegInfo = zoneRegInfo;
	}

	public ZoneLoginInfo getZoneLoginInfo() {
		return zoneLoginInfo;
	}

	public void setZoneLoginInfo(ZoneLoginInfo zoneLoginInfo) {
		this.zoneLoginInfo = zoneLoginInfo;
	}

	public boolean isInKickOffCoolTime() {
		return getKickOffCoolTime() > 0 && getKickOffCoolTime() > System.currentTimeMillis();
	}

	public void block(String reason, long blockCoolTime) {
		getExtendInfo().setBlockReason(reason);
		getExtendInfo().setBlockCoolTime(blockCoolTime);
	}

	public boolean isBlocked() {
		if (extendInfo == null) {
			return false;
		}

		long blockCoolTime = extendInfo.getBlockCoolTime();
		boolean isBlocked = blockCoolTime != 0 && (blockCoolTime > 0 && blockCoolTime > System.currentTimeMillis());
		return isBlocked;
	}

	public String getBlockReason() {

		String reason = "";

		if (extendInfo != null) {
			reason = extendInfo.getBlockReason();
		}

		return reason;
	}

	public long getBlockCoolTime() {
		return extendInfo != null ? extendInfo.getBlockCoolTime() : 0;
	}

	public void chatBan(String reason, long chatBanCoolTime) {
		getExtendInfo().setChatBanReason(reason);
		getExtendInfo().setChatBanCoolTime(chatBanCoolTime);
	}

	public boolean isChatBan() {
		if (extendInfo == null) {
			return false;
		}

		long coolTime = extendInfo.getChatBanCoolTime();
		boolean isBan = coolTime != 0 && (coolTime > 0 && coolTime > System.currentTimeMillis());
		return isBan;
	}

	public String getChatBanReason() {
		return getExtendInfo().getChatBanReason();
	}

	public long getChatBanCoolTime() {
		return getExtendInfo().getChatBanCoolTime();
	}

	public UserExtendInfo getExtendInfo() {
		if (extendInfo == null) {
			extendInfo = new UserExtendInfo();
		}
		return extendInfo;
	}

	public void setExtendInfo(UserExtendInfo extendInfo) {
		this.extendInfo = extendInfo;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public boolean isRobot() {
		return isRobot;
	}

	public void setRobot(boolean isRobot) {
		this.isRobot = isRobot;
	}

	public String getOpenAccount() {
		return openAccount;
	}

	public void setOpenAccount(String openAccount) {
		this.openAccount = openAccount;
	}

}