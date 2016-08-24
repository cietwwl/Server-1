package com.playerdata.readonly;

import java.util.List;

import com.rwbase.dao.equipment.EquipItemIF;

public interface EquipMgrIF {
	
//	public int getEquipCount();
//
//	public List<? extends EquipItemIF> getEquipList();
//
//	// public ItemDataIF getEquipItem(int nSlotId);
//
//	// public EquipItemIF getMagic();
	
	public int getEquipCount(String heroId);

	public List<? extends EquipItemIF> getEquipList(String heroId);
}
