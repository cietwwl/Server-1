package com.rw.dataaccess.hero;

import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rw.dataaccess.attachment.RoleExtPropertyType;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.cache.CacheKey;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.skill.pojo.SkillItem;
import com.rwbase.dao.spriteattach.SpriteAttachSyn;
import com.rwbase.dao.spriteattach.SpriteAttachSynCreator;

public enum HeroExtPropertyType implements RoleExtPropertyType{

	FIX_EXP_EQUIP(1, FixExpEquipDataItem.class, HeroFixExpEquipCreator.class),
	FIX_NORM_EQUIP(2, FixNormEquipDataItem.class, HeroFixNormEquipCreator.class),
	SKILL_ITEM(3, SkillItem.class, HeroSkillItemCreator.class),
	EQUIP_ITEM(4, EquipItem.class, HeroEquipItemCreator.class),
	INLAY_ITEM(5, InlayItem.class, HeroInlayItemCreator.class),
	SPRITE_ATTACH_ITEM(6, SpriteAttachSyn.class, SpriteAttachSynCreator.class),
	;
	
	
	private final Class<? extends RoleExtProperty> propertyClass;
	private final Class<? extends HeroExtPropertyCreator<?>> creatorClass;
	private final String propertyName;
	private final CacheKey cacheKey;
	private final short type;
	private final int capacity;

	<T extends RoleExtProperty> HeroExtPropertyType(int type, Class<T> attachmentClass, Class<? extends HeroExtPropertyCreator<T>> creatorClass) {
		this(type, attachmentClass, attachmentClass.getSimpleName(), creatorClass, 0);
	}
	
	<T extends RoleExtProperty> HeroExtPropertyType(int type, Class<T> attachmentClass, String name, Class<? extends HeroExtPropertyCreator<T>> creatorClass, int capacity) {
		if (type > Short.MAX_VALUE) {
			throw new ExceptionInInitializerError("out of range:" + type + ",max=" + Short.MAX_VALUE);
		}
		this.type = (short) type;
		this.propertyClass = attachmentClass;
		this.propertyName = name;
		this.creatorClass = creatorClass;
		this.capacity = capacity;
		this.cacheKey = new CacheKey(attachmentClass, name);
	}

	public Class<? extends RoleExtProperty> getPropertyClass() {
		return propertyClass;
	}

	public Class<? extends HeroExtPropertyCreator<?>> getCreatorClass() {
		return creatorClass;
	}

	public int getCapacity() {
		return capacity;
	}

	public short getType() {
		return type;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public CacheKey getCacheKey() {
		return cacheKey;
	}
}
