package com.rw.fsutil.dao.common;

import java.lang.reflect.Field;
import java.util.Map;

import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.annotation.FieldEntry;
import com.rw.fsutil.dao.mapitem.MapItemEntity;
import com.rw.fsutil.dao.mapitem.MapItemRowBuider;

public class OwnerRowMapper<T> extends CommonRowMapper<T> implements MapItemRowBuider<T> {

	public OwnerRowMapper(ClassInfo classInfo) {
		super(classInfo, null);
	}

	@Override
	public T mapRow(Object key, Map<String, Object> rs) {
		try {
			@SuppressWarnings("unchecked")
			T newInstance = (T) classInfo.newInstance();
			FieldEntry[] singleFields = classInfo.getSelectSingleFields();
			for (int i = singleFields.length; --i >= 0;) {
				FieldEntry entry = singleFields[i];
				Object value = rs.get(entry.columnName);
				if (value == null) {
					// 这里可以考虑记log
					continue;
				}
				handleSingleSave(newInstance, entry, value);
			}
			// 设置ownerId
			Field ownerField = classInfo.getOwnerField();
			if (ownerField != null) {
				ownerField.set(newInstance, key);
			}
			String combineName = classInfo.getCombineColumnName();
			if (combineName != null) {
				Object value = rs.get(combineName);
				if (value != null) {
					handleCombineSave(newInstance, combineName, (String) value, classInfo.getCombineSaveFields());
				}
			}
			return newInstance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public T builde(Object key, MapItemEntity entity) {
		try {
			@SuppressWarnings("unchecked")
			T newInstance = (T) classInfo.newInstance();
			// 设置id字段
			classInfo.getIdField().set(newInstance, entity.getId());
			// 设置ownerId
			Field ownerField = classInfo.getOwnerField();
			if (ownerField != null) {
				ownerField.set(newInstance, key);
			}
			// 设置extentsion
			String extColumnName = "extention";
			if (classInfo.isCombineSave(extColumnName)) {
				handleCombineSave(newInstance, extColumnName, entity.getExtention(), classInfo.getCombineSaveFields());
			} else {
				FieldEntry fieldEntry = classInfo.getSingleField(extColumnName);
				if (fieldEntry != null) {
					handleSingleSave(newInstance, fieldEntry, entity.getExtention());
				}
			}
			return newInstance;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
