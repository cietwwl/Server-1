package com.rw.service.Privilege.datamodel;

public class FloatPropertyWriter extends AbstractPropertyWriter<Float> {
	private static FloatPropertyWriter instance;

	public static PropertyWriter getShareInstance() {
		if (instance == null) {
			instance = new FloatPropertyWriter();
		}
		return instance;
	}

	@Override
	protected Float parse(String val) {
		return Float.parseFloat(val);
	}

}
