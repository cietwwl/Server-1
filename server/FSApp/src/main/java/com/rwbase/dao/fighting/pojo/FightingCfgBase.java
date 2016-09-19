package com.rwbase.dao.fighting.pojo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.rwbase.common.FightingIndexKey;

public abstract class FightingCfgBase {

	private Map<Integer, Integer> _fightingOfIndex;
	private int _max;
	private int _allFighting;
	
	public abstract int getRequiredLv();

	public void afterInit() {
		_fightingOfIndex = new HashMap<Integer, Integer>();
		Field[] allFields = this.getClass().getDeclaredFields();
		for (int i = 0; i < allFields.length; i++) {
			Field temp = allFields[i];
			FightingIndexKey indexKey = temp.getAnnotation(FightingIndexKey.class);
			if (indexKey != null) {
				Integer value = 0;
				try {
					temp.setAccessible(true);
					value = (Integer) temp.get(this);
					temp.setAccessible(false);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				Integer pre = _fightingOfIndex.put(indexKey.value(), value);
				if (pre != null) {
					throw new RuntimeException("重复的主键：" + indexKey.value());
				}
				if (_max < indexKey.value()) {
					_max = indexKey.value();
				}
				this._allFighting += value;
			}
		}
	}
	
	public final int getFightingOfIndex(int key) {
		return _fightingOfIndex.get(key);
	}
	
	public final int getAllFighting() {
		return _allFighting;
	}
	
	@Override
	public String toString() {
		return "FightingCfgBase, fightingOfIndex = " + _fightingOfIndex;
	}
}
