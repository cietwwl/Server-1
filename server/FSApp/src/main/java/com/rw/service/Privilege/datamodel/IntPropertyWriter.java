package com.rw.service.Privilege.datamodel;

public class IntPropertyWriter extends AbstractPropertyWriter<Integer> {
	private static IntPropertyWriter instance;

	public static IntPropertyWriter getShareInstance() {
		if (instance == null) {
			instance = new IntPropertyWriter();
		}
		return instance;
	}

	@Override
	protected Integer parse(String val) {
		return Integer.parseInt(val);
	}

	@Override
	protected Class<Integer> getTypeClass() {
		return Integer.class;
	}
}
