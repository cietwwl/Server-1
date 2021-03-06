package com.rwbase.dao.equipment;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.OwnerId;
import com.rwproto.ItemBagProtos.EItemTypeDef;

/**
 * 装备信息
 * 
 * @author allen
 *
 */
@Table(name = "equip_item")
@SynClass
public class EquipItem implements RoleExtProperty, EquipItemIF {
	@Id
	private Integer id; // ownerId + slotId 装备唯一id
	@OwnerId
	private String ownerId;// 装备者（佣兵或主角）的id

	private int equipIndex; // 装备位置Id

	private EItemTypeDef type;// 装备类型 佣兵装备 主角装备 法宝

	private int modelId;// 物品Id

	private int level; // 等级

	private int exp; // 经验

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public int getEquipIndex() {
		return equipIndex;
	}

	public void setEquipIndex(int equipIndex) {
		this.equipIndex = equipIndex;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public EItemTypeDef getType() {
		return type;
	}

	public void setType(EItemTypeDef type) {
		this.type = type;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

}
