package com.rwbase.dao.battletower.pojo.cfg;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.rwproto.BattleTowerServiceProtos.RewardInfoMsg;

/*
 * @author HC
 * @date 2015年9月7日 下午2:16:48
 * @Description 试练塔Boss模版
 */
public class BattleTowerBossCfg {
	private int bossId;// Boss配置Id
	private int pro;// 出现的权重
	private int levelLimit;// 当前分段属于那个等级段
	private String rewardInfo;// 奖励的物品数据
	private String dropIds;// 掉落的配置Id

	// ///////////////////////////////////JsonIgnore
	@JsonIgnore
	private String[] dropIdArr;
	@JsonIgnore
	private List<RewardInfoMsg> rewardInfoList;

	public int getBossId() {
		return bossId;
	}

	public void setBossId(int bossId) {
		this.bossId = bossId;
	}

	public int getPro() {
		return pro;
	}

	public void setPro(int pro) {
		this.pro = pro;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	public String getRewardInfo() {
		return rewardInfo;
	}

	public void setRewardInfo(String rewardInfo) {
		this.rewardInfo = rewardInfo;

		if (this.rewardInfo != null && !this.rewardInfo.isEmpty()) {
			String[] temp = this.rewardInfo.split(",");
			int len = temp.length;

			this.rewardInfoList = new ArrayList<RewardInfoMsg>(len);
			for (String temp0 : temp) {
				String[] temp1 = temp0.split(":");
				RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
				rewardInfoMsg.setType(Integer.parseInt(temp1[0]));
				rewardInfoMsg.setCount(Integer.parseInt(temp1[1]));
				this.rewardInfoList.add(rewardInfoMsg.build());
			}
		}
	}

	public String getDropIds() {
		return dropIds;
	}

	public void setDropIds(String dropIds) {
		this.dropIds = dropIds;

		if (this.dropIds != null && !this.dropIds.isEmpty()) {
			this.dropIdArr = this.dropIds.split(",");
		}
	}

	public String[] getDropIdArr() {
		return dropIdArr;
	}

	public List<RewardInfoMsg> getRewardInfoList() {
		return rewardInfoList;
	}

	// public static void main(String[] args) {
	// TreeMap<Integer, Integer> tMap = new TreeMap<Integer, Integer>();
	// for (int i = 1; i <= 10; i++) {
	// tMap.put(i * 10, i * 100);
	// }
	//
	// int key = 21;
	// Entry<Integer, Integer> ceilingEntry = tMap.ceilingEntry(key);
	// System.err.println(ceilingEntry.getKey() + "," + ceilingEntry.getValue());
	//
	// Entry<Integer, Integer> floorEntry = tMap.floorEntry(key);
	// System.err.println(floorEntry.getKey() + "," + floorEntry.getValue());
	//
	// Entry<Integer, Integer> higherEntry = tMap.higherEntry(key);
	// System.err.println(higherEntry.getKey() + "," + higherEntry.getValue());
	//
	// Entry<Integer, Integer> lowerEntry = tMap.lowerEntry(key);
	// System.err.println(lowerEntry.getKey() + "," + lowerEntry.getValue());
	// }
}