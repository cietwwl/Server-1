package com.rwbase.dao.dropitem;

import java.util.ArrayList;
import java.util.List;

/**
 * 一组掉落只随机一次
 * 
 * @author Jamaz
 *
 */
public class DropGroupCfg {

	private final List<DropCfg> dropCfgList;
	private final int dropRuleId;

	public DropGroupCfg(List<DropCfg> dropCfgList, int dropRuleId) {
		this.dropCfgList = dropCfgList;
		this.dropRuleId = dropRuleId;
	}

	public DropGroupCfg(DropCfg dropCfg) {
		this.dropCfgList = new ArrayList<DropCfg>();
		this.dropCfgList.add(dropCfg);
		this.dropRuleId = dropCfg.getItemsFormula();
	}

	public List<DropCfg> getDropCfgList() {
		return dropCfgList;
	}

	public void addDropCfg(DropCfg dropCfg) {
		this.dropCfgList.add(dropCfg);
	}

	public int getDropRuleId() {
		return dropRuleId;
	}
}
