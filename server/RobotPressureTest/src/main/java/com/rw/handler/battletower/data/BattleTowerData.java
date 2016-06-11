package com.rw.handler.battletower.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rwproto.BattleTowerServiceProtos.BossInfoMsg;

/*
 * @author HC
 * @date 2016年3月20日 下午2:55:29
 * @Description 封神台数据
 */
public class BattleTowerData {
	private int highestFloor;// 记录最高的层数
	private int sweepFloor;// 扫荡层
	private List<BossInfoMsg> bossInfoMsg;// 产生的Boss的信息
	private int bossId;

	public BattleTowerData() {
		bossInfoMsg = new ArrayList<BossInfoMsg>();
	}

	public int getHighestFloor() {
		return highestFloor;
	}

	public void setHighestFloor(int highestFloor) {
		this.highestFloor = highestFloor;
	}

	public int getSweepFloor(Random r) {
		// if (r.nextBoolean()) {
		// return ((sweepFloor / 3) * 3) + 1;
		// }
		//
		// return r.nextInt(sweepFloor) + r.nextInt(5);
		return ((sweepFloor / 3) * 3) + 1;
	}

	public void setSweepFloor(int sweepFloor) {
		this.sweepFloor = sweepFloor;
	}

	public List<BossInfoMsg> getBossInfoMsg() {
		return bossInfoMsg;
	}

	public void setBossInfoMsg(List<BossInfoMsg> bossInfoMsg) {
		this.bossInfoMsg = bossInfoMsg;
	}

	public int getStrategyFloor(Random r) {
		return r.nextInt(sweepFloor);
	}

	public void addBossInfo(List<BossInfoMsg> addBossInfo) {
		this.bossInfoMsg.addAll(bossInfoMsg);
	}

	public int getRandomSweepFloor(Random r) {
		// if (r.nextBoolean()) {// 正确数据
		// return sweepFloor + 1;
		// }
		//
		// return r.nextInt(highestFloor - sweepFloor) + sweepFloor + (r.nextInt(3) - 5);
		return sweepFloor + 1;
	}

	public void setBattleTowerBossId(int bossId) {
		this.bossId = bossId;
	}

	public int getBossId() {
		return bossId;
	}
}