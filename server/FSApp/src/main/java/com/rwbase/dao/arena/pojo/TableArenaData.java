package com.rwbase.dao.arena.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.PlayerMgr;
import com.playerdata.readonly.PlayerIF;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "arena_data")
public class TableArenaData {

	@Id
	private String userId; // 用户ID
	private int career;
	private int maxPlace;
	private int fighting;
	private int remainCount;
	private String headImage;
	private String headbox;
	private int level;
	private String name;
	private int magicId;
	private int magicLevel;
	private String templeteId;
	private int winCount;
	private long lastFightTime;
//	private List<RecordInfo> recordList = new ArrayList<RecordInfo>();
	// private List<String> atkHeroList = new ArrayList<String>(); //
	// 进攻阵容的id列表(templateId)
	private List<String> atkList = new ArrayList<String>(); // 进攻阵容的id列表(uuid)
	private List<String> heroIdList = new ArrayList<String>(); // 队伍佣兵id列表(uuid)
	// private volatile long lastResetMillis; // 上次重置的毫秒
	private int resetTimes; // 重置的次数
	// private volatile long lastBuyTimesMillis;// 上次购买挑战次数的时间
	private int buyTimes; // 购买挑战次数的次数..
	private int score; //
	private int challengeTime;// 挑战次数
	private boolean lastChallengeVictory;
	private List<Integer> rewardList = new ArrayList<Integer>(); // 记录每天领取的积分领取的奖励列表

	private List<Integer> historyRewards = new ArrayList<Integer>();// 记录曾经领取过的历史奖励

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getCareer() {
		return career;
	}

	public void setCareer(int career) {
		this.career = career;
	}

	public int getMaxPlace() {
		return maxPlace;
	}

	public void setMaxPlace(int maxPlace) {
		this.maxPlace = maxPlace;
	}

	public int getFighting() {
		return fighting;
	}

	public void setFighting(int fighting) {
		this.fighting = fighting;
	}

	public int getRemainCount() {
		return remainCount;
	}

	public void setRemainCount(int remainCount) {
		if (remainCount >= 0) {
			this.remainCount = remainCount;
		}
	}

	public String getHeadImage() {
		// TODO 临时解决数据问题
		if (headImage == null || headImage.isEmpty()
				|| headImage.equals("1001")) {
			PlayerIF player = PlayerMgr.getInstance().find(userId);
			headImage = player.getTableUser().getHeadImageWithDefault();
		}
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public int getLevel() {
		return level;
	}

	@SuppressWarnings("unchecked")
	public List<String> getHeroIdList() {
		return heroIdList == null ? Collections.EMPTY_LIST : new ArrayList<String>(heroIdList);
	}

	public void setHeroIdList(List<String> heroIdList) {
		this.heroIdList = heroIdList;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMagicId() {
		return magicId;
	}

	public void setMagicId(int magicId) {
		this.magicId = magicId;
	}

	public int getMagicLevel() {
		return magicLevel;
	}

	public void setMagicLevel(int magicLevel) {
		this.magicLevel = magicLevel;
	}

	public String getTempleteId() {
		return templeteId;
	}

	public void setTempleteId(String templeteId) {
		this.templeteId = templeteId;
	}

	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public long getLastFightTime() {
		return lastFightTime;
	}

	public void setLastFightTime(long lastFightTime) {
		this.lastFightTime = lastFightTime;
	}

//	public List<RecordInfo> getRecordList() {
//		return recordList;
//	}
//
//	public void setRecordList(List<RecordInfo> recordList) {
//		if (recordList != null) {
//			this.recordList = recordList;
//		}
//	}

	// public List<String> getAtkHeroList() {
	// return atkHeroList;
	// }
	//
	// public void setAtkHeroList(List<String> atkHeroList) {
	// this.atkHeroList = atkHeroList;
	// }

	public int getResetTimes() {
		return resetTimes;
	}

	public void setResetTimes(int resetTimes) {
		this.resetTimes = resetTimes;
	}

	public int getBuyTimes() {
		return buyTimes;
	}

	public void setBuyTimes(int buyTimes) {
		this.buyTimes = buyTimes;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public List<Integer> getRewardList() {
		return rewardList;
	}

	public void setRewardList(List<Integer> rewardList) {
		this.rewardList = rewardList;
	}

	public int getChallengeTime() {
		return challengeTime;
	}

	public void setChallengeTime(int challengeTime) {
		this.challengeTime = challengeTime;
	}

	public List<Integer> getHistoryRewards() {
		return historyRewards;
	}

	public void setHistoryRewards(List<Integer> historyRewards) {
		this.historyRewards = historyRewards;
	}

	public String getHeadbox() {
		return headbox;
	}

	public void setHeadbox(String headbox) {
		this.headbox = headbox;
	}

	public boolean isLastChallengeVictory() {
		return lastChallengeVictory;
	}

	public void setLastChallengeVictory(boolean lastChallengeVictory) {
		this.lastChallengeVictory = lastChallengeVictory;
	}

	public List<String> getAtkList() {
		return atkList;
	}

	public void setAtkList(List<String> atkList) {
		this.atkList = atkList;
	}

}
