package com.rw.service.TaoistMagic.datamodel;

//可行的优化是记住已经计算的结果，用空间换时间
public class TaoistMagicFormula {
	private int valueInit;//结果初值
	private int deltaInit;//速度初值
	private int accDelta;//加速度递增值
	private int levelDelta;//等级间隔，用于计算加速度
	
	public TaoistMagicFormula(int valueInit, int deltaInit, int accDelta, int levelDelta) {
		this.valueInit = valueInit;
		this.deltaInit = deltaInit;
		this.accDelta = accDelta;
		this.levelDelta = levelDelta;
		if (valueInit <0 || deltaInit <0 || accDelta <0 || levelDelta <0){
			throw new RuntimeException("无效公式参数:"+this.toString());
		}
	}
	
	@Override
	public String toString() {
		return "TaoistMagicFormula [valueInit=" + valueInit + ", deltaInit=" + deltaInit + ", accDelta=" + accDelta
				+ ", levelDelta=" + levelDelta + "]";
	}

	public int getValue(int level){
		if (level <=0) return 0;
		return value(level);
	}

	private int value(int level) {
		if (level <= 0) return valueInit;
		return value(level-1)+delta(level-1);
	}

	//速度
	private int delta(int level) {
		if (level<=0) return 0;
		return deltaInit+acc(level);
	}

	//加速度
	private int acc(int level) {
		if (level <= 0) return 0;
		if (levelDelta <=0) return 0;
		return accDelta * ((level-1) / levelDelta);
	}
	
}
