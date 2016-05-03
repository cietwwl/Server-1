package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.PrivilegeValue;

public interface PropertyWriter {
	public PrivilegeValue.Builder combine(PrivilegeValue.Builder acc, PrivilegeValue added,String name);

	public boolean gt(Object privilegeValue, Object maxVal);
}
