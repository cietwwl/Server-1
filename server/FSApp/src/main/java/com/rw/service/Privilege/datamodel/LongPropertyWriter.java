package com.rw.service.Privilege.datamodel;

public class LongPropertyWriter extends AbstractPropertyWriter<Long> {
	private static LongPropertyWriter instance = new LongPropertyWriter();

	public static LongPropertyWriter getShareInstance() {
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
