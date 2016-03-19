package com.rwbase.dao.battletower.pojo.cfg;

import java.util.ArrayList;
import java.util.List;

import com.rwproto.BattleTowerServiceProtos.RewardInfoMsg;

public class BattleTowerBossTemplate {
	private final int bossId;// Boss配置Id
	private final int pro;// 出现的权重
	private final int levelLimit;// 当前分段属于那个等级段
	private final String[] dropIdArr;
	private final List<RewardInfoMsg> rewardInfoList;

	public BattleTowerBossTemplate(BattleTowerBossCfg bossCfg) {
		this.bossId = bossCfg.getBossId();
		this.pro = bossCfg.getPro();
		this.levelLimit = bossCfg.getLevelLimit();

		String rewardInfo = bossCfg.getRewardInfo();
		if (rewardInfo != null && !rewardInfo.isEmpty()) {
			String[] temp = rewardInfo.split(",");
			int len = temp.length;

			this.rewardInfoList = new ArrayList<RewardInfoMsg>(len);
			for (String temp0 : temp) {
				String[] temp1 = temp0.split(":");
				RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
				rewardInfoMsg.setType(Integer.parseInt(temp1[0]));
				rewardInfoMsg.setCount(Integer.parseInt(temp1[1]));
				this.rewardInfoList.add(rewardInfoMsg.build());
			}
		} else {
			this.rewardInfoList = new ArrayList<RewardInfoMsg>();
		}

		String dropIds = bossCfg.getDropIds();
		if (dropIds != null && !dropIds.isEmpty()) {
			this.dropIdArr = dropIds.split(",");
		} else {
			this.dropIdArr = new String[0];
		}
	}

	public int getBossId() {
		return bossId;
	}

	public int getPro() {
		return pro;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public String[] getDropIdArr() {
		return dropIdArr;
	}

	public List<RewardInfoMsg> getRewardInfoList() {
		return rewardInfoList;
	}
}