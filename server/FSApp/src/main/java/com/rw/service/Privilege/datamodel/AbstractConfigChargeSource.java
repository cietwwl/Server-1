package com.rw.service.Privilege.datamodel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public abstract class AbstractConfigChargeSource<NameEnumCl extends Enum<NameEnumCl>>
		implements IConfigChargeSource<NameEnumCl> {
	private HashMap<NameEnumCl, Object> fieldValues;

	@Override
	public Object getValueByName(NameEnumCl pname) {
		return fieldValues.get(pname);
	}

	@Override
	public void checkThreshold(IPrivilegeThreshold<NameEnumCl> thresholdHelper) {
		Set<Entry<NameEnumCl, Object>> entrySet = fieldValues.entrySet();
		for (Entry<NameEnumCl, Object> entry : entrySet) {
			int threshold = thresholdHelper.getThreshold(entry.getKey());
			if (threshold >0){
				//TODO 限制最大值 依赖初始化顺序了！
			}
		}
	}

	public void ExtraInitAfterLoad(Class<NameEnumCl> nameEnumCl,
			IPrivilegeConfigSourcer<NameEnumCl> cfgHelper)
			throws IllegalArgumentException, IllegalAccessException {
		NameEnumCl[] names = nameEnumCl.getEnumConstants();
		for (int i = 0; i < names.length; i++) {
			NameEnumCl name = names[i];
			Field field = cfgHelper.getConfigField(name);
			Object value = field.get(this);
			fieldValues.put(name, value);
		}
	}

}
