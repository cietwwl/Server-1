package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.PrivilegeValue;
import com.rwproto.PrivilegeProtos.PrivilegeValue.Builder;

public class BoolPropertyWriter implements PropertyWriter {
	private static BoolPropertyWriter instance;

	public static PropertyWriter getShareInstance() {
		if (instance == null) {
			instance = new BoolPropertyWriter();
		}
		return instance;
	}

	@Override
	public boolean gt(Object privilegeValue, Object maxVal) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Builder combine(Builder acc, PrivilegeValue added, String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
