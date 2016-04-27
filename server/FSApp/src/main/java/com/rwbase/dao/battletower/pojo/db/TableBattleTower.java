package com.rwbase.dao.battletower.pojo.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.common.HPCUtil;
import com.rwbase.dao.battletower.pojo.BossCacheInfo;
import com.rwbase.dao.battletower.pojo.BossInfo;
import com.rwbase.dao.battletower.pojo.readonly.TableBattleTowerIF;

/*
 * @author HC
 * @date 2015年9月1日 下午12:03:03
 * @Description 试练塔的个人数据
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "battle_tower_data")
public class TableBattleTower implements TableBattleTowerIF {
	@Id
	private String userId;// 角色Id
	private int resetTimes;// 重置次数【重置时加1】
	private long resetTime;// 重置时间
	private int highestFloor;// 最高的层数
	// ////////////////////////////////////////
	private int rewardGroupId;// 当前已经领取奖励的组【重置时为0】
	private int curFloor;// 当前挑战的层数【重置为0】
	private boolean result;// 战斗结果成功or失败【重置时设置战斗结果为false】
	private boolean isBreak;// 挑战某一组的时候是否被中断【重置时要设置为false】
	// ////////////////////////////////////////
	@JsonIgnore
	private AtomicInteger generateBossId;// 产生的Boss的唯一Id

	private int curBossTimes;// 当前出现Boss的次数
	private List<BossCacheInfo> bossCacheInfo;// 当次产生Boss的信息【重置时清空】
	// private List<BossInfo> bossInfoList;// 产生的Boss信息
	private int dbBossId;// 数据库中存储的最后产生的Boss的Id
	private Map<Integer, BossInfo> bossInfoMap;// 产生的Boss的Map
	private int challengeBossId;// 正在挑战的BossId
	// ////////////////////////////////////////
	private long sweepStartTime;// 扫荡开始的时间【重置时为0】
	private int sweepStartFloor;// 扫荡开始的层数【重置时为0】
	private boolean sweepState;// 扫荡的状态【重置时为false】
	// ////////////////////////////////////////
	private int copper_key;// 铜钥匙
	private int silver_key;// 银钥匙
	private int gold_key;// 金钥匙
	// ////////////////////////////////////////
	private int use_copper_key;// 铜钥匙
	private int use_silver_key;// 银钥匙
	private int use_gold_key;// 金钥匙

	public TableBattleTower() {
		this.bossInfoMap = new HashMap<Integer, BossInfo>();// 产生的Boss的数据
		// this.bossInfoList = new ArrayList<BossInfo>();// 初始化Boss信息List
		this.bossCacheInfo = new ArrayList<BossCacheInfo>();
	}

	public TableBattleTower(String userId) {
		this();
		this.userId = userId;
	}

	// ////////////////////////////////////////////简单处理逻辑区
	/**
	 * 获取某个里程碑是否已经产生了Boss
	 * 
	 * @param markId
	 * @return
	 */
	public boolean hasBossInfoInMark(int markId) {
		if (this.bossCacheInfo.isEmpty()) {
			return false;
		}

		for (int i = 0, size = this.bossCacheInfo.size(); i < size; i++) {
			if (this.bossCacheInfo.get(i).getMarkId() == markId) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 增加新的Boss信息
	 * 
	 * @param bossInfo
	 * @return 返回添加Boss在列表中的索引位置（默认是当作Boss的Id）
	 */
	public int addBossInfo(BossInfo bossInfo) {
		this.curBossTimes++;// 增加一次产生Boss的次数
		if (generateBossId == null) {
			generateBossId = new AtomicInteger(this.dbBossId);
		}
		int bossId = this.generateBossId.incrementAndGet();
		this.dbBossId = bossId;
		bossInfo.setBossUniqueId(bossId);// 设置产出的Boss的唯一Id
		this.bossInfoMap.put(bossId, bossInfo);// 增加一个Boss
		return bossId;
	}

	/**
	 * 增加单次挑战中产生Boss的缓存数据
	 * 
	 * @param bossCacheInfo
	 */
	public void addBossCacheInfo(BossCacheInfo bossCacheInfo) {
		this.bossCacheInfo.add(bossCacheInfo);
	}

	/**
	 * 修改铜钥匙
	 * 
	 * @param count
	 */
	public void modifyCopperKey(int count) {
		int oldKeyCount = copper_key;
		copper_key = HPCUtil.safeCalculateChange(this.copper_key, count);

		if (oldKeyCount > copper_key) {// 如果之前的小于现在的，证明是被消耗了
			use_copper_key += (oldKeyCount - copper_key);
		}
	}

	/**
	 * 修改银钥匙
	 * 
	 * @param count
	 */
	public void modifySilverKey(int count) {
		int oldKeyCount = silver_key;
		silver_key = HPCUtil.safeCalculateChange(this.silver_key, count);

		if (oldKeyCount > silver_key) {// 如果之前的小于现在的，证明是被消耗了
			use_silver_key += (oldKeyCount - silver_key);
		}
	}

	/**
	 * 修改金钥匙
	 * 
	 * @param count
	 */
	public void modifyGoldKey(int count) {
		int oldKeyCount = gold_key;
		gold_key = HPCUtil.safeCalculateChange(this.gold_key, count);

		if (oldKeyCount > gold_key) {// 如果之前的小于现在的，证明是被消耗了
			use_silver_key += (oldKeyCount - gold_key);
		}
	}

	/**
	 * 重置试练塔的数据
	 */
	public void resetBattleTowerData() {
		this.resetTimes++;
		this.resetTime = System.currentTimeMillis();

		this.rewardGroupId = 0;
		this.curFloor = 0;
		this.result = false;
		this.isBreak = false;

		this.bossCacheInfo.clear();

		this.sweepStartFloor = 0;
		this.sweepStartTime = 0;
		this.sweepState = false;
	}

	/**
	 * 检查Boss的状态
	 * 
	 * @param now
	 * @param showTime
	 */
	public void checkBossShowState(long now, long showTime) {
		if (this.bossInfoMap == null || this.bossInfoMap.isEmpty()) {
			return;
		}

		Iterator<Entry<Integer, BossInfo>> itr = this.bossInfoMap.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Integer, BossInfo> next = itr.next();
			BossInfo bossInfo = next.getValue();
			if (now < bossInfo.getBossStartTime()) {
				continue;
			}

			long offTime = now - bossInfo.getBossStartTime();
			if (offTime >= showTime) {
				itr.remove();
			}
		}
	}

	/**
	 * 删除Boss
	 * 
	 * @param bossId
	 */
	public void removeBoss(int bossId) {
		if (this.bossInfoMap == null || this.bossInfoMap.isEmpty()) {
			return;
		}

		this.bossInfoMap.remove(bossId);
	}

	/**
	 * 通过Id获取Boss数据
	 * 
	 * @param bossId
	 * @return
	 */
	public BossInfo getBoss(int bossId) {
		if (this.bossInfoMap == null || this.bossInfoMap.isEmpty()) {
			return null;
		}

		return this.bossInfoMap.get(bossId);
	}

	/**
	 * 获取产生的Boss的数据
	 * 
	 * @return
	 */
	@JsonIgnore
	public List<BossInfo> getBossInfoList() {
		return new ArrayList<BossInfo>(this.bossInfoMap.values());
	}

	/**
	 * 获取已经使用的钥匙的数量（即消耗宝箱的数量）
	 * 
	 * @return
	 */
	@JsonIgnore
	public int getHasUsedKeyCount() {
		return use_copper_key + use_gold_key + use_silver_key;
	}

	// ////////////////////////逻辑GET区////////////////////////
	public String getUserId() {
		return userId;
	}

	public int getHighestFloor() {
		return this.highestFloor;
	}

	public int getCurFloor() {
		return this.curFloor;
	}

	public int getResetTimes() {
		return this.resetTimes;
	}

	public long getSweepStartTime() {
		return this.sweepStartTime;
	}

	public boolean getSweepState() {
		return this.sweepState;
	}

	public int getSweepStartFloor() {
		return this.sweepStartFloor;
	}

	public int getCurBossTimes() {
		return this.curBossTimes;
	}

	public long getResetTime() {
		return resetTime;
	}

	/**
	 * 获取Boss信息列表
	 * 
	 * @return
	 */
	public Map<Integer, BossInfo> getBossInfoMap() {
		return new HashMap<Integer, BossInfo>(this.bossInfoMap);
	}

	/**
	 * 获取当前层的战斗结果
	 * 
	 * @return
	 */
	public boolean getResult() {
		return this.result;
	}

	/**
	 * 获取已经领取了奖励的里程碑Id
	 * 
	 * @return
	 */
	public int getRewardGroupId() {
		return this.rewardGroupId;
	}

	public int getCopper_key() {
		return copper_key;
	}

	public int getSilver_key() {
		return silver_key;
	}

	public int getGold_key() {
		return gold_key;
	}

	public List<BossCacheInfo> getBossCacheInfo() {
		return bossCacheInfo;
	}

	public boolean isBreak() {
		return isBreak;
	}

	public int getChallengeBossId() {
		return challengeBossId;
	}

	public int getUse_copper_key() {
		return use_copper_key;
	}

	public int getUse_silver_key() {
		return use_silver_key;
	}

	public int getUse_gold_key() {
		return use_gold_key;
	}

	// ////////////////////////SET区////////////////////////
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setResetTimes(int resetTimes) {
		this.resetTimes = resetTimes;
	}

	public void setHighestFloor(int highestFloor) {
		this.highestFloor = highestFloor;
	}

	public void setRewardGroupId(int rewardGroupId) {
		this.rewardGroupId = rewardGroupId;
	}

	public void setCurFloor(int curFloor) {
		this.curFloor = curFloor;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public void setCurBossTimes(int curBossTimes) {
		this.curBossTimes = curBossTimes;
	}

	public void setBossInfoMap(Map<Integer, BossInfo> bossInfoMap) {
		this.bossInfoMap = bossInfoMap;
	}

	public void setSweepStartTime(long sweepStartTime) {
		this.sweepStartTime = sweepStartTime;
	}

	public void setSweepStartFloor(int sweepStartFloor) {
		this.sweepStartFloor = sweepStartFloor;
	}

	public void setSweepState(boolean sweepState) {
		this.sweepState = sweepState;
	}

	public void setCopper_key(int copper_key) {
		this.copper_key = copper_key;
	}

	public void setSilver_key(int silver_key) {
		this.silver_key = silver_key;
	}

	public void setGold_key(int gold_key) {
		this.gold_key = gold_key;
	}

	public void setBossCacheInfo(List<BossCacheInfo> bossCacheInfo) {
		this.bossCacheInfo = bossCacheInfo;
	}

	public void setBreak(boolean isBreak) {
		this.isBreak = isBreak;
	}

	public void setChallengeBossId(int challengeBossId) {
		this.challengeBossId = challengeBossId;
	}

	public void setResetTime(long resetTime) {
		this.resetTime = resetTime;
	}
}