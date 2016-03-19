package com.rwbase.dao.business;

import java.util.Collections;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

/**
 * @author weihua
 * @Description
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "seven_day_gifinfo")
@SynClass
public class SevenDayGifInfo {

	@Id
	private String userId;// 角色Id
	private int count;// 上线次数
	// private int getCount;// 已领次数
	private List<Integer> counts;// 已领取天数
	private boolean isGetGif;

	public boolean isGetGif() {
		return isGetGif;
	}

	public void setGetGif(boolean isGetGif) {
		this.isGetGif = isGetGif;
	}

	public List<Integer> getCounts() {
		List<Integer> list = Collections.emptyList();
		return counts == null ? list: counts;
	}

	public void setCounts(List<Integer> counts) {
		this.counts = counts;
	}

	private long lastResetTime;

	public SevenDayGifInfo() {

	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	// public int getGetCount() {
	// return getCount;
	// }
	//
	// public void setGetCount(int getCount) {
	// this.getCount = getCount;
	// }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getLastResetTime() {
		return lastResetTime;
	}

	public void setLastResetTime(long lastResetTime) {
		this.lastResetTime = lastResetTime;
	}
}