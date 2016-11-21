package com.rw.service.Privilege.datamodel;

public class FloatPropertyWriter extends AbstractPropertyWriter<Float> {
	private static FloatPropertyWriter instance = new FloatPropertyWriter();

	public static FloatPropertyWriter getShareInstance() {
		return instance;
	}

	@Override
	protected Float parse(String val) {
		return Float.parseFloat(val);
	}

	@Override
	protected Class<Float> getTypeClass() {
		return Float.class;
	}

}
