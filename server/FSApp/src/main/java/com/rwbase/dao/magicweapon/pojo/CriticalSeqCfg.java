package com.rwbase.dao.magicweapon.pojo;

import com.common.ListParser;

public class CriticalSeqCfg {
	private String sequence;
	private int[] seqList;

	public void ExtraInit() {
		ParseSeq();
	}

	private void ParseSeq() {
		seqList = ListParser.ParseIntList(sequence, ",", "法宝", "配置错误", "暴击方案序列无效：");
	}

	public String getSequence() {
		return sequence;
	}

	public int[] getSeqList() {
		return seqList;
	}

}
