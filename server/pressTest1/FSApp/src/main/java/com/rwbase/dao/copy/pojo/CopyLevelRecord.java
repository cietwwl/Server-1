package com.rwbase.dao.copy.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;


/**
 * 
 * @author 副本关卡记录
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "copy_level_record")
@SynClass
public class CopyLevelRecord implements IMapItem, CopyLevelRecordIF {
	@Id
	private String id;//记录唯一id
	private int levelId;// 关卡Id
	@IgnoreSynField
	private String userId;
	private int passStar; // 通过星级,0~3
	private int currentCount; // 当前打了几次
	private int buyCount; // 今日购买挑战次数,普通关卡为-1次,英雄和精英的大于等于0次
	private boolean isFirst = false;

	@Override
	public String getId() {
		return this.id;
	}
	

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return this.userId;
	}

	public int getPassStar() {
		return passStar;
	}

	public void setPassStar(int passStar) {
		this.passStar = passStar;
	}

	public int getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
	}

	public int getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String toClientData() {
		StringBuilder recordBuilder = new StringBuilder();

		recordBuilder.append(this.levelId).append(",").append(this.passStar)
				.append(",").append(this.currentCount).append(",")
				.append(this.buyCount);

		return recordBuilder.toString();
	}

	public int getLevelId() {
		return levelId;
	}

	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}


	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}


	

	

}
