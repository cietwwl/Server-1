package com.rw.fsutil.ranking.impl;

/**
 * 条件比较型排行榜条目的实体对象(对应数据库表结构)
 * @author Jamaz
 */
public class RankingEntryData {

	private final long id;			//排行榜条目id(用于定义一个可排序的物理主键，对逻辑不可见)
	private final int type;			//排行榜类型(区分不同类型的排行榜)
	private final String key;		//排行榜条目主键(逻辑主键)
	private final String condition;	//排行榜条目的比较条件(逻辑实现，用于排行榜排序)
	private final String extension; //排行榜条目的扩展属性(逻辑扩展)

	public RankingEntryData(long id, int type, String key, String condition, String extension) {
		this.id = id;
		this.type = type;
		this.key = key;
		this.condition = condition;
		this.extension = extension;
	}

	public long getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public String getCondition() {
		return condition;
	}

	public String getExtension() {
		return extension;
	}
}