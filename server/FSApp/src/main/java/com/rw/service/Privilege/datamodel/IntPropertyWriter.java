package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.PrivilegeValue;
import com.rwproto.PrivilegeProtos.PrivilegeValue.Builder;

public class IntPropertyWriter implements PropertyWriter {
	private static IntPropertyWriter instance;

	public static IntPropertyWriter getShareInstance() {
		if (instance == null) {
			instance = new IntPropertyWriter();
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
