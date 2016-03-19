package com.rw.handler.group.data;

import com.rw.dataSyn.SynItem;

/*
 * @author HC
 * @date 2016年3月15日 下午4:27:23
 * @Description 帮派日志
 */
public class GroupLog implements SynItem {
	private long time;// 发送时间
	private String name;// 名字
	private int post;// 职位
	private int groupLevel;// 帮派等级
	private String skillName;// 技能名字
	private int skillLevel;// 技能名字
	private int logType;// 日志类型

	public long getTime() {
		return time;
	}

	public String getName() {
		return name;
	}

	public int getPost() {
		return post;
	}

	public int getGroupLevel() {
		return groupLevel;
	}

	public String getSkillName() {
		return skillName;
	}

	public int getSkillLevel() {
		return skillLevel;
	}

	public int getLogType() {
		return logType;
	}

	@Override
	public String getId() {
		return "";
	}
}