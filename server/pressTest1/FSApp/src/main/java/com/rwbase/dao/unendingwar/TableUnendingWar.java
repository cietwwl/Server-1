package com.rwbase.dao.unendingwar;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "unending_war")
public class TableUnendingWar {
	@Id
	private String userId;

	private Integer num = 0;// 进入次数

	private Integer zhCj = 0;// 最好成绩

	private Integer dqCj = 0;// 当前成绩
	private Integer resetNum = 0;// 重置次数
	private long lastChallengeTime; // 上次挑战时间

	private Map<Integer, Integer> cj = new HashMap<Integer, Integer>();// 成绩

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/** 进入次数 **/
	public Integer getNum() {
		return num;
	}

	/** 进入次数 **/
	public void setNum(Integer num) {
		this.num = num;
	}

	/** 最好成绩 **/
	public Integer getZhCj() {
		return zhCj;
	}

	/** 当前成绩 **/
	public void setZhCj(Integer zhCj) {
		this.zhCj = zhCj;
	}

	/** 当前成绩 **/
	public Integer getDqCj() {
		return dqCj;
	}

	public void setDqCj(Integer dqCj) {
		this.dqCj = dqCj;
	}

	public Integer getResetNum() {
		return resetNum;
	}

	public void setResetNum(Integer resetNum) {
		this.resetNum = resetNum;
	}

	public Map<Integer, Integer> getCj() {
		return cj;
	}

	public void setCj(Map<Integer, Integer> cj) {
		this.cj = cj;
	}

	public long getLastChallengeTime() {
		return lastChallengeTime;
	}

	public void setLastChallengeTime(long lastChallengeTime) {
		this.lastChallengeTime = lastChallengeTime;
	}

}