package com.rw.trace;

import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rw.fsutil.dao.cache.CacheKey;
import com.rwbase.dao.copypve.pojo.TableCopyData;
import com.rwbase.dao.dropitem.DropRecord;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.majorDatas.pojo.MajorData;
import com.rwbase.dao.skill.pojo.SkillItem;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserGameData;

/**
 * DataTrace注册器
 * 
 * @author Jamaz
 *
 */
public enum DataTraceRegistrator {

	ITEM_DATA(ItemData.class),
	DROP_RECORD(DropRecord.class),
	EQUIP_ITEM(EquipItem.class),
	FIX_EXP_EQUIP_ITEM(FixExpEquipDataItem.class),
	FIX_NOMR_EQUIP_ITEM(FixNormEquipDataItem.class),
	INLAY_ITEM(InlayItem.class),
	MAJOR_DATA(MajorData.class),
	SKILL(SkillItem.class),
	COPY_DATA(TableCopyData.class),
//	USER_HERO(TableUserHero.class),
	USER_GAME_DATA(UserGameData.class),
	//MAIN_ROLE_HERO(FSHero.class, MapItemStoreFactory.MAIN_ROLE_NAME),
	USER(User.class),
	CHARGE_DATA(ChargeInfo.class),
	;

	DataTraceRegistrator(Class<?> dataTraceClass) {
		this.dataCacheKey = new CacheKey(dataTraceClass);
	}

	DataTraceRegistrator(Class<?> dataTraceClass, String name) {
		this.dataCacheKey = new CacheKey(dataTraceClass, name);
	}

	private CacheKey dataCacheKey;

	public CacheKey getDataCacheKey() {
		return dataCacheKey;
	}
}
