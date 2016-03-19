package com.rwbase.dao.equipment;

import com.rwproto.ItemBagProtos.EItemTypeDef;

/**
 * 装备信息
 * 
 * @author allen
 *
 */

public interface EquipItemIF {
	public String getId();

	public String getOwnerId();

	public int getEquipIndex();

	public int getModelId();

	public int getLevel();

	public EItemTypeDef getType();

	public int getExp();
}