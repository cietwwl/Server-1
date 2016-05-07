package com.rw.service.Privilege.datamodel;

import java.util.Map;

public interface IConfigChargeSource<NameEnumCl extends Enum<NameEnumCl>> extends IFieldReflector<NameEnumCl> {
	public String getSource();
	public void ExtraInitAfterLoad(IPrivilegeConfigSourcer<NameEnumCl> cfgHelper);
	
	/**
	 * 将充值类型转换为小写！
	 */
	void toLowerCase(IPrivilegeConfigSourcer<NameEnumCl> cfgHelper);
	
	/**
	 * 假设按优先级从小到大的顺序进行检查
	 * @param cfgHelper
	 */
	public void FixEmptyValue(IPrivilegeConfigSourcer<NameEnumCl> cfgHelper,IConfigChargeSource<NameEnumCl> pre);
	
	/**
	 * 限制最大值 依赖初始化顺序，必须在配置表加载后检查
	 * @param thresholdHelper
	 */
	public void checkThreshold(IPrivilegeThreshold<NameEnumCl> thresholdHelper,Map<NameEnumCl,PropertyWriter> combinatorMap);
}
