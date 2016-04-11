package com.rwbase.dao.group.pojo.db;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;

/*
 * @author HC
 * @date 2016年1月26日 上午11:06:39
 * @Description 帮派日志
 */
@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupLog implements IMapItem {
	private long time;// 发送时间
	private String name;// 名字
	private int post;// 职位
	private int groupLevel;// 帮派等级
	private String skillName;// 技能名字
	private int skillLevel;// 技能名字
	private int logType;// 日志类型
	private String opName;// 操作人的名字

	@Override
	public String getId() {
		return "";
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPost() {
		return post;
	}

	public void setPost(int post) {
		this.post = post;
	}

	public int getGroupLevel() {
		return groupLevel;
	}

	public void setGroupLevel(int groupLevel) {
		this.groupLevel = groupLevel;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public int getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(int skillLevel) {
		this.skillLevel = skillLevel;
	}

	public int getLogType() {
		return logType;
	}

	public void setLogType(int logType) {
		this.logType = logType;
	}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}
}