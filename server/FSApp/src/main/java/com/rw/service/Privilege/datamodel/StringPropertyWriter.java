package com.rw.service.Privilege.datamodel;

public class StringPropertyWriter extends AbstractPropertyWriter<String> {

	private static StringPropertyWriter instance;
	
	public static StringPropertyWriter getShareInstance() {
		if (instance == null) {
			instance = new StringPropertyWriter();
		}
		return instance;
	}
	
	@Override
	protected String parse(String val) {
		// TODO Auto-generated method stub
		return val;
	}

	@Override
	protected Class<String> getTypeClass() {
		// TODO Auto-generated method stub
		return String.class;
	}

}
