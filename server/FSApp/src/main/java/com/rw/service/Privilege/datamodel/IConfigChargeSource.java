package com.rw.service.Privilege.datamodel;

public interface IConfigChargeSource<NameEnumCl extends Enum<NameEnumCl>> extends IFieldReflector<NameEnumCl> {
	public String getSource();
	public void ExtraInitAfterLoad(IPrivilegeConfigSourcer<NameEnumCl> cfgHelper);
	public void checkThreshold(IPrivilegeThreshold<NameEnumCl> thresholdHelper);
}
