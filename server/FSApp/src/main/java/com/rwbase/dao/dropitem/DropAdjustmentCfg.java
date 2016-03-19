package com.rwbase.dao.dropitem;

/**
 * <pre>
 * 掉落调整配置
 * 每一个掉落调整配置关联的掉落记录，都会在用户数据有一条相应的记录
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class DropAdjustmentCfg {

	private int dropRecordId; // 掉落记录ID(关联到掉落表)
	private int additiveRate; // 当掉落记录的概率低于原生概率时额外增加的概率
	private int minRate; // 低于或者达到最低概率必定触发一次掉落
	private int firstDrop; // 是否首次必掉

	public int getDropRecordId() {
		return dropRecordId;
	}

	public void setDropRecordId(int dropRecordId) {
		this.dropRecordId = dropRecordId;
	}

	public int getAdditiveRate() {
		return additiveRate;
	}

	public void setAdditiveRate(int additiveRate) {
		this.additiveRate = additiveRate;
	}

	public int getMinRate() {
		return minRate;
	}

	public void setMinRate(int minRate) {
		this.minRate = minRate;
	}

	public int getFirstDrop() {
		return firstDrop;
	}

	public void setFirstDrop(int firstDrop) {
		this.firstDrop = firstDrop;
	}

}
