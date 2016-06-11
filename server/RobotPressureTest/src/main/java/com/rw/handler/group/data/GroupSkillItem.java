package com.rw.handler.group.data;

import com.rw.dataSyn.SynItem;

/*
 * @author HC
 * @date 2016年3月15日 下午5:07:17
 * @Description 帮派技能的数据
 */
public class GroupSkillItem implements SynItem {
	private String id;// 唯一id
	private int level;// 技能等级
	private long time;// 技能研发或者学习的开始时间
	private int state;// 技能状态

	public String getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public long getTime() {
		return time;
	}

	public int getState() {
		return state;
	}
}