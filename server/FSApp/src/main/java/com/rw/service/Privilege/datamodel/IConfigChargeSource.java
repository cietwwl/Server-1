package com.rw.service.Privilege.datamodel;

import java.util.Map;

public interface IConfigChargeSource<NameEnumCl extends Enum<NameEnumCl>> extends IFieldReflector<NameEnumCl> {
	public String getSource();
	public void ExtraInitAfterLoad(IPrivilegeConfigSourcer<NameEnumCl> cfgHelper);
	public void FixEmptyValue(IPrivilegeConfigSourcer<NameEnumCl> cfgHelper);
	/**
	 * 限制最大值 依赖初始化顺序，必须在配置表加载后检查
	 * @param thresholdHelper
	 */
	public void checkThreshold(IPrivilegeThreshold<NameEnumCl> thresholdHelper,Map<NameEnumCl,PropertyWriter> combinatorMap);
}
