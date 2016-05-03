package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.PrivilegeValue;
import com.rwproto.PrivilegeProtos.PrivilegeValue.Builder;

public class LongPropertyWriter implements PropertyWriter {
	private static LongPropertyWriter instance;

	public static PropertyWriter getShareInstance() {
		if (instance == null) {
			instance = new LongPropertyWriter();
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
