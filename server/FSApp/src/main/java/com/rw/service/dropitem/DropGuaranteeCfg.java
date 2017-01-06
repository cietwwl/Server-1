package com.rw.service.dropitem;

/**
 * 掉落保底，控制物品最低掉落次数，和最多连续掉落次数
 * @author Jamaz
 *
 */
public class DropGuaranteeCfg {

	// 保底ID
	private int id;
	// 道具ID
	private int itemTemplateId;
	// 最大连续不掉落次数(掉落伐值) 比如3表示最多连续3次不掉落，第4次必掉
	private int maxMisspCount;
	// 最大连续掉落次数(不掉落伐值) 比如2表示最多连续掉2次，第3次必定不掉，第4次会掉
	private int maxDropCount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItemTemplateId() {
		return itemTemplateId;
	}

	public void setItemTemplateId(int itemTemplateId) {
		this.itemTemplateId = itemTemplateId;
	}

	public int getMaxMisspCount() {
		return maxMisspCount;
	}

	public void setMaxMisspCount(int maxMisspCount) {
		this.maxMisspCount = maxMisspCount;
	}

	public int getMaxDropCount() {
		return maxDropCount;
	}

	public void setMaxDropCount(int maxDropCount) {
		this.maxDropCount = maxDropCount;
	}

}
