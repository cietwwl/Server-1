package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.PrivilegeValue;

public interface PropertyWriter {
	public PrivilegeValue.Builder combine(PrivilegeValue.Builder acc, PrivilegeValue added,String name);

	/**
	 * 特殊处理：如果rightVal是空，而leftVal非空，则返回true！
	 * @param leftVal
	 * @param rightVal
	 * @return
	 */
	public boolean gt(Object leftVal, Object rightVal);

	public Object extractValue(String value);
}
