package com.rwbase.dao.fashion;

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
}
