package com.rwbase.dao.gulid.faction;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;

/**
 * 
 * 用户自己有关帮派的数据
 * @author allen
 *
 */


@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "guild_userinfo")
@SynClass
public class GuildUserInfo {

	@Id
	private String userId;
	
	private String guildId;// 公会唯一id
	private String guildName;// 公会名字
	private int guildCoin;//工会币
	private int guildMaterial;// 公会材料	
	private long sendEmailTime;//发送邮件的时间，用来控制邮件的发送		
	private long exitTime;//离开工会的时间，加入工会的时候要检查这个时间
	private int applyCount = 0;//已经申请的次数
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getGuildId() {
		return guildId;
	}
	public void setGuildId(String guildId) {
		this.guildId = guildId;
	}
	public String getGuildName() {
		return guildName;
	}
	public void setGuildName(String guildName) {
		this.guildName = guildName;
	}

	public int getGuildMaterial() {
		return guildMaterial;
	}
	public void setGuildMaterial(int guildMaterial) {
		this.guildMaterial = guildMaterial;
	}
	public long getSendEmailTime() {
		return sendEmailTime;
	}
	public void setSendEmailTime(long sendEmailTime) {
		this.sendEmailTime = sendEmailTime;
	}
	public long getExitTime() {
		return exitTime;
	}
	public void setExitTime(long exitTime) {
		this.exitTime = exitTime;
	}
	public int getGuildCoin() {
		return guildCoin;
	}
	public void setGuildCoin(int guildCoin) {
		this.guildCoin = guildCoin;
	}
	public int getApplyCount() {
		return applyCount;
	}
	public void setApplyCount(int applyCount) {
		this.applyCount = applyCount;
	}
	
	

	
	
	
	
	
}
