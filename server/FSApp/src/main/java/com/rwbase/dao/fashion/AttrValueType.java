package com.rwbase.dao.fashion;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;

public enum AttrValueType {
	Percentage, Value;

	public static AttrValueType valueOf(String str, AttrValueType value) {
		AttrValueType result = value;
		try {
			result = AttrValueType.valueOf(str);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static AttrValueType setDefaultIfEmpty(AttrValueType val){
		if (val == null) return val = AttrValueType.Value;
		return val;
	}
	
	public static void collectValue(List<IReadOnlyPair<String, Object>> sourceValues,
			List<IReadOnlyPair<String, Object>> sourcePer,
			AttrValueType valueType,String name,Integer value) {
		switch (valueType) {
		case Value:
			if (!StringUtils.isBlank(name)) {
				sourceValues.add(Pair.Create(name, (Object) value));
			}
			break;
		case Percentage:
			if (!StringUtils.isBlank(name)) {
				sourcePer.add(Pair.Create(name, (Object) value));
			}
			break;
		default:
			break;
		}
	}
}
