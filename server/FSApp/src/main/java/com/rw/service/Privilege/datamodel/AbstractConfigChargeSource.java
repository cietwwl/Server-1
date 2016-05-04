package com.rw.service.Privilege.datamodel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.log.GameLog;

import java.util.Set;

public abstract class AbstractConfigChargeSource<NameEnumCl extends Enum<NameEnumCl>>
		implements IConfigChargeSource<NameEnumCl> {
	private HashMap<NameEnumCl, Object> fieldValues = new HashMap<NameEnumCl, Object>();

	@Override
	public Object getValueByName(NameEnumCl pname) {
		return fieldValues.get(pname);
	}

	@Override
	public void checkThreshold(IPrivilegeThreshold<NameEnumCl> thresholdHelper,
			Map<NameEnumCl,PropertyWriter> combinatorMap) {
		Set<Entry<NameEnumCl, Object>> entrySet = fieldValues.entrySet();
		IntPropertyWriter intPro = IntPropertyWriter.getShareInstance();
		for (Entry<NameEnumCl, Object> entry : entrySet) {
			NameEnumCl proName = entry.getKey();
			PropertyWriter proWriter = combinatorMap.get(proName);
			if (proWriter != intPro){
				continue;
			}
			int threshold = thresholdHelper.getThreshold(proName);
			if (threshold >0){
				Integer val = intPro.extractVal(entry.getValue(), -1);
				if (val > threshold){
					GameLog.info("特权", "特权名:"+proName, "特权配置超过阀值,配置值："+val+",阀值:"+threshold, null);
					fieldValues.put(proName, threshold);
				}
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
