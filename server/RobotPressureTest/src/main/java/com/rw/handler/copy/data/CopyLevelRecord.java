package com.rw.handler.copy.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;


/**
 * 
 * @author 副本关卡记录
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CopyLevelRecord implements SynItem {
	private String id;//记录唯一id
	private int levelId;// 关卡Id
	private int passStar; // 通过星级,0~3
	private int currentCount; // 当前打了几次
	private int buyCount; // 今日购买挑战次数,普通关卡为-1次,英雄和精英的大于等于0次
	private boolean isFirst = false;

	@Override
	public String getId() {
		return this.id;
	}

	public int getLevelId() {
		return levelId;
	}

	public int getPassStar() {
		return passStar;
	}

	public int getCurrentCount() {
		return currentCount;
	}

	public int getBuyCount() {
		return buyCount;
	}

	public boolean isFirst() {
		return isFirst;
	}
}
