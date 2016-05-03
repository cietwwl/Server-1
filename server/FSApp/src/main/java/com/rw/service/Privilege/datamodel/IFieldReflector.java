package com.rw.service.Privilege.datamodel;

public interface IFieldReflector<NameEnumCl extends Enum<NameEnumCl>> {
	public Object getValueByName(NameEnumCl pname);
}
