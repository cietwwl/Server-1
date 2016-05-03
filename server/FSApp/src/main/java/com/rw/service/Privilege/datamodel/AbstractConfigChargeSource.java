package com.rw.service.Privilege.datamodel;

import java.lang.reflect.Field;
import java.util.HashMap;

public abstract class AbstractConfigChargeSource<NameEnumCl extends Enum<NameEnumCl>>
		implements IConfigChargeSource<NameEnumCl> {
	private HashMap<NameEnumCl, Object> fieldValues;

	@Override
	public Object getValueByName(NameEnumCl pname) {
		return fieldValues.get(pname);
	}

	public void ExtraInitAfterLoad(Class<NameEnumCl> nameEnumCl,
			IPrivilegeConfigSourcer<NameEnumCl> cfgHelper,
			IPrivilegeThreshold<NameEnumCl> thresholdHelper)
			throws IllegalArgumentException, IllegalAccessException {
		NameEnumCl[] names = nameEnumCl.getEnumConstants();
		for (int i = 0; i < names.length; i++) {
			NameEnumCl name = names[i];
			Field field = cfgHelper.getConfigField(name);
			Object value = field.get(this);
			int threshold = thresholdHelper.getThreshold(name);
			if (threshold >0){
				//TODO 限制最大值 依赖初始化顺序了！
			}
			fieldValues.put(name, value);
		}
	}

}
