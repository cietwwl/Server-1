package com.rw.trace;

import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.copypve.pojo.TableCopyData;
import com.rwbase.dao.dropitem.DropRecord;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.hero.pojo.TableUserHero;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.majorDatas.pojo.MajorData;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.user.UserGameData;

/**
 * DataTrace注册器
 * @author Jamaz
 *
 */
public enum DataTraceRegistrator {

	ItemData(ItemData.class),
	DropRecord(DropRecord.class),
	EquipItem(EquipItem.class),
	FixExpEquipDataItem(FixExpEquipDataItem.class),
	FixNormEquipDataItem(FixNormEquipDataItem.class),
	InlayItem(InlayItem.class),
	MajorData(MajorData.class),
	Skill(Skill.class),
	TableCopyData(TableCopyData.class),
	TableUserHero(TableUserHero.class),
	UserGameData(UserGameData.class),
//	ARENA(TableArenaData.class),
	
	;

 	DataTraceRegistrator(Class<?> dataTraceClass){
		this.dataTraceClass = dataTraceClass;
	}
	private Class<?> dataTraceClass;
	
	public Class<?> getDataTraceClass() {
		return dataTraceClass;
	}
}
