package com.playerdata.activity.limitHeroType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfg;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.OwnerId;
@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_limitherotype_item")
public class ActivityLimitHeroTypeItem implements  RoleExtProperty{
	@Id
	private Integer id ;
	@OwnerId
	private String userId;
	
	@CombineSave
	private String cfgId;
	
	@CombineSave
	private boolean closed = false;
	
	@CombineSave
	private String version ;
	
	@CombineSave
	private boolean isTouchRedPoint;
	
	@CombineSave
	private int integral;//积分
	
	@CombineSave
	private long lastSingleTime;//上一次免费单抽时间
	
	@CombineSave
	private String rankRewards;//积分排行榜奖励，有数据代表上榜了且领取
	
	@CombineSave
	private List<ActivityLimitHeroTypeSubItem> subList = new ArrayList<ActivityLimitHeroTypeSubItem>();
	
	@CombineSave
	private int guarantee ;
	
	public void reset(ActivityLimitHeroCfg cfg,List<ActivityLimitHeroTypeSubItem> subList){
		this.cfgId = String.valueOf(cfg.getId());
		this.closed = false;
		this.version = String.valueOf(cfg.getVersion());
		this.isTouchRedPoint = false;
		this.integral  = 0;//和初始化区分，测试完变为0；
		this.lastSingleTime = 666;//0.0 和初始化区分
		this.rankRewards = "";
		this.subList = subList;
		this.guarantee = 0;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getGuarantee() {
		return guarantee;
	}

	public void setGuarantee(int guarantee) {
		this.guarantee = guarantee;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isTouchRedPoint() {
		return isTouchRedPoint;
	}

	public void setTouchRedPoint(boolean isTouchRedPoint) {
		this.isTouchRedPoint = isTouchRedPoint;
	}

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public long getLastSingleTime() {
		return lastSingleTime;
	}

	public void setLastSingleTime(long lastSingleTime) {
		this.lastSingleTime = lastSingleTime;
	}	

	public String getRankRewards() {
		return rankRewards;
	}

	public void setRankRewards(String rankRewards) {
		this.rankRewards = rankRewards;
	}

	public List<ActivityLimitHeroTypeSubItem> getSubList() {
		return subList;
	}

	public void setSubList(List<ActivityLimitHeroTypeSubItem> subList) {
		this.subList = subList;
	}	
}
