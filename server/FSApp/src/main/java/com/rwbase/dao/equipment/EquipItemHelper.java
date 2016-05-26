package com.rwbase.dao.equipment;

import org.apache.commons.lang3.StringUtils;

import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.ItemBagProtos.EItemAttributeType;
import com.rwproto.ItemBagProtos.EItemTypeDef;

public class EquipItemHelper {

	// public static AttrData toAttrData(HeroEquipCfg itemData, int equipLevel) {
	//
	// AttrData equipAttrData = AttrData.fromObject(itemData);
	// EquipAttachCfg pEquipAttachCfg = EquipAttachCfgDAO.getInstance().getConfig(equipLevel);
	// if (pEquipAttachCfg != null) {
	// int attriPercent = pEquipAttachCfg.getAttriPercent();
	// equipAttrData.addPercent(attriPercent);
	// }
	//
	// return equipAttrData;
	// }

	public static EquipItem toEquip(String ownerId, int equipIndex, ItemData itemData) {
		EquipItem equipItem = new EquipItem();
		equipItem.setOwnerId(ownerId);
		equipItem.setEquipIndex(equipIndex);
		equipItem.setId(getItemId(ownerId, equipIndex));
		equipItem.setModelId(itemData.getModelId());
		equipItem.setType(itemData.getType());

		String attachExp = itemData.getAllExtendAttr().get(EItemAttributeType.Equip_AttachExp_VALUE);
		String attachLevel = itemData.getAllExtendAttr().get(EItemAttributeType.Equip_AttachLevel_VALUE);
		if (StringUtils.isNotBlank(attachExp)) {
			equipItem.setExp(Integer.valueOf(attachExp));
		}
		if (StringUtils.isNotBlank(attachLevel)) {
			equipItem.setLevel(Integer.valueOf(attachLevel));
		}

		return equipItem;

	}

	// 法宝脱下的时候会用到
	public static ItemData toMagicItemData(EquipItem equipItem) {
		ItemData itemData = null;
		if (equipItem.getType() == EItemTypeDef.Magic) {
			itemData = new ItemData();
			itemData.setModelId(equipItem.getModelId());
			itemData.setCount(1);

			String exp = String.valueOf(equipItem.getExp());
			String level = String.valueOf(equipItem.getLevel());
			itemData.setExtendAttr(EItemAttributeType.Magic_Exp_VALUE, exp);
			itemData.setExtendAttr(EItemAttributeType.Magic_Level_VALUE, level);
			itemData.setExtendAttr(EItemAttributeType.Magic_State_VALUE, "0");
		}

		return itemData;

	}

	// 转职脱下装备的时候会用到
	public static ItemData toEquipItemData(EquipItem equipItem) {
		ItemData itemData = null;
		if (equipItem.getType() == EItemTypeDef.HeroEquip) {
			itemData = new ItemData();
			itemData.setModelId(equipItem.getModelId());
			itemData.setCount(1);
			// itemData.setType(equipItem.getType());
		}

		return itemData;

	}

	public static String getItemId(String ownerId, int slotId) {
		return ownerId + "_" + slotId;
	}

}
