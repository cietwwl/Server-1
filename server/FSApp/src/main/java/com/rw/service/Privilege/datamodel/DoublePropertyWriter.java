package com.rw.service.Privilege.datamodel;

public class DoublePropertyWriter extends AbstractPropertyWriter<Double> {
	private static DoublePropertyWriter instance = new DoublePropertyWriter();

	public static DoublePropertyWriter getShareInstance() {
		return instance;
	}

	@Override
	protected Double parse(String val) {
		return Double.parseDouble(val);
	}

	@Override
	protected Class<Double> getTypeClass() {
		return Double.class;
	}

}
