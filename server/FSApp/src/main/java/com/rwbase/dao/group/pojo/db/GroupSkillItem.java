package com.rwbase.dao.group.pojo.db;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;

/**
 * 帮派技能数据
 * 
 * @author HC
 *
 */
@SynClass
public class GroupSkillItem implements IMapItem {

	@Id
	private String id;// 唯一id
	private int level;// 技能等级
	private long time;// 技能研发或者学习的开始时间
	private int state;// 技能状态

	// ////////////////////////////////////////////GET区
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

	// ////////////////////////////////////////////SET区
	public void setId(int id) {
		this.id = String.valueOf(id);
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setState(int state) {
		this.state = state;
	}
}