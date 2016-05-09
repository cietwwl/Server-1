package com.rw.service.TaoistMagic.datamodel;

import com.common.BaseConfig;
import com.common.ListParser;

public class TaoistCriticalPlanCfg extends BaseConfig {
	private int key; // 关键字段
	private String sequence; // 方案序列
	private int[] plans;

	public int getKey() {
		return key;
	}

	public String getSequence() {
		return sequence;
	}

	public int[] getPlans() {
		return plans;
	}

	@Override
	public void ExtraInitAfterLoad() {
		//解析暴击序列，看看是否正确
		plans = ListParser.ParseIntList(sequence, ",", "道术", "无效暴击序列", "暴击方案ID:"+key+",");
		for (int i = 0; i < plans.length; i++) {
			int val = plans[i];
			if (val < 0){
				throw new RuntimeException("无效暴击序列,ID="+key+",索引="+(i+1)+",值="+val);
			}
		}
	}
}
