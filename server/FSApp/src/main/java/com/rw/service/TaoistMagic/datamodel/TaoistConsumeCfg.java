package com.rw.service.TaoistMagic.datamodel;

import com.common.BaseConfig;
import com.common.ListParser;

public class TaoistConsumeCfg extends BaseConfig {
	private int key; // key
	private int consumeId; // 技能消耗ID
	private int skillLevel; // 技能等级
	private com.rwbase.common.enu.eSpecialItemId coinType; // 货币类型
	private int coinCount; // 消耗
	private String criticalPlans; // 暴击组合序列
	private int[] seqList;

	public int getKey() {
		return key;
	}

	public int getConsumeId() {
		return consumeId;
	}

	public int getSkillLevel() {
		return skillLevel;
	}

	public com.rwbase.common.enu.eSpecialItemId getCoinType() {
		return coinType;
	}

	public int getCoinCount() {
		return coinCount;
	}

	@Override
	public void ExtraInitAfterLoad() {
		// 检查货币类型是否正确，数量是否为正数
		if (coinType == null){
			throw new RuntimeException("无效货币类型,key="+key);
		}
		if (coinCount <= 0){
			throw new RuntimeException("无效消耗值:"+coinCount+",key="+key);
		}
		seqList = ListParser.ParseIntList(criticalPlans, "\\|", "道术", "配置错误", "暴击方案序列无效:");
	}

	public int[] getSeqList() {
		return seqList;
	}

}
