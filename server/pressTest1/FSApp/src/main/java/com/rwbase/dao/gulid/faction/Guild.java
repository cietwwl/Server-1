package com.rwbase.dao.gulid.faction;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "guild")
@SynClass
public class Guild {
	@Id
	private String Id;

	private int guildNum;//帮派数字id
	private int level;
	
	private int contribute;// 当前可用的帮贡	
	private int totalContribute;// 历史累计的总量帮贡

	private int activeValue;// 活跃值
	private long createTime;//创建时间
	
	private long dismissTime = 0;// 解散时间
	// 一天只能解散两次
	private long dismissCount = 0;// 一天不能2次解除

	// 用户可以在界面修改的信息
	private GuildSetting guildSetting;
	// 帮派技能列表
	private List<GuildSkill> skillList;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public int getGuildNum() {
		return guildNum;
	}

	public void setGuildNum(int guildNum) {
		this.guildNum = guildNum;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getContribute() {
		return contribute;
	}

	public void setContribute(int contribute) {
		this.contribute = contribute;
	}

	public int getTotalContribute() {
		return totalContribute;
	}

	public void setTotalContribute(int totalContribute) {
		this.totalContribute = totalContribute;
	}

	public int getActiveValue() {
		return activeValue;
	}

	public void setActiveValue(int activeValue) {
		this.activeValue = activeValue;
	}

	public long getCreateTimer() {
		return createTime;
	}

	public void setCreateTimer(long createTimer) {
		this.createTime = createTimer;
	}

	public long getDismissTime() {
		return dismissTime;
	}

	public void setDismissTime(long dismissTime) {
		this.dismissTime = dismissTime;
	}

	public long getDismissCount() {
		return dismissCount;
	}

	public void setDismissCount(long dismissCount) {
		this.dismissCount = dismissCount;
	}

	public GuildSetting getGuildSetting() {
		return guildSetting;
	}

	public void setGuildSetting(GuildSetting guildSetting) {
		this.guildSetting = guildSetting;
	}

	public List<GuildSkill> getSkillList() {
		return skillList;
	}

	public void setSkillList(List<GuildSkill> skillList) {
		this.skillList = skillList;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	
}