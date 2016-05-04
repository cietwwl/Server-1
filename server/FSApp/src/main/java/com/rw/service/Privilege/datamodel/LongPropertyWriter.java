package com.rw.service.Privilege.datamodel;

public class LongPropertyWriter extends AbstractPropertyWriter<Long> {
	private static LongPropertyWriter instance;

	public static LongPropertyWriter getShareInstance() {
		if (instance == null) {
			instance = new LongPropertyWriter();
		}
		return instance;
	}

	@Override
	protected Long parse(String val) {
		return Long.parseLong(val);
	}

	@Override
	protected Class<Long> getTypeClass() {
		return Long.class;
	}
}
