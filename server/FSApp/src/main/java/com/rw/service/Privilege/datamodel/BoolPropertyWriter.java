package com.rw.service.Privilege.datamodel;

public class BoolPropertyWriter extends AbstractPropertyWriter<Boolean> {
	private static BoolPropertyWriter instance;

	public static BoolPropertyWriter getShareInstance() {
		if (instance == null) {
			instance = new BoolPropertyWriter();
		}
		return instance;
	}

	@Override
	protected Boolean parse(String val) {
		return Boolean.parseBoolean(val);
	}

	@Override
	protected Class<Boolean> getTypeClass() {
		return Boolean.class;
	}
}
