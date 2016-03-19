package com.rw.fsutil.ranking.impl;

/**
 * 列表排行榜条目的实体对象(对应数据库表结构)
 * 
 * @author Jamaz
 *
 */
public class ListRankingEntryData {

	private final String key; 		//排行榜条目主键(逻辑主键)
	private final int ranking; 		//排行榜条目的排名
	private final int type; 		//排行榜类型(区分不同类型的排行榜)
	private final String extension; //排行榜条目的扩展属性(逻辑扩展)

	public ListRankingEntryData(String key, int type, int ranking, String extension) {
		this.key = key;
		this.ranking = ranking;
		this.type = type;
		this.extension = extension;
	}

	public String getExtension() {
		return extension;
	}

	public String getKey() {
		return key;
	}

	public int getRanking() {
		return ranking;
	}

	public int getType() {
		return type;
	}

}
