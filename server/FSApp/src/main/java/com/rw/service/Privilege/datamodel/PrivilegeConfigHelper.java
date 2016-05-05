package com.rw.service.Privilege.datamodel;

import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwproto.PrivilegeProtos.PrivilegeProperty;
import com.rwproto.PrivilegeProtos.PrivilegeValue;

public class PrivilegeConfigHelper {
	private static PrivilegeConfigHelper instance;

	public static PrivilegeConfigHelper getInstance() {
		if (instance == null) {
			instance = new PrivilegeConfigHelper();
		}
		return instance;
	}

	private HashMap<String, IPrivilegeConfigSourcer<?>> sourcerMap = new HashMap<String, IPrivilegeConfigSourcer<?>>();
	private HashMap<String, CfgCsvDao<?>> configHelperMap = new HashMap<String, CfgCsvDao<?>>();

	public void reloadAllPrivilegeConfigs() {
		Collection<CfgCsvDao<?>> values = configHelperMap.values();
		CfgCsvDao<?>[] localCopy = new CfgCsvDao<?>[values.size()];
		localCopy = values.toArray(localCopy);
		for (int i = 0; i < localCopy.length; i++) {
			CfgCsvDao<?> cfgHelper = localCopy[i];
			cfgHelper.reload();
		}
		for (int i = 0; i < localCopy.length; i++) {
			CfgCsvDao<?> cfgHelper = localCopy[i];
			cfgHelper.CheckConfig();
		}
	}

	public void update(String propertyConfigName, IPrivilegeThreshold<?> propertyHelper) {
		if (StringUtils.isNotBlank(propertyConfigName) && propertyHelper != null) {
			// proMap.put(propertyConfigName,propertyHelper);
			CfgCsvDao<?> helper = (CfgCsvDao<?>) propertyHelper;
			configHelperMap.put(propertyConfigName, helper);
		}
	}

	public void update(String configSourcerName, IPrivilegeConfigSourcer<?> sourcer) {
		if (StringUtils.isNotBlank(configSourcerName) && sourcer != null) {
			sourcerMap.put(configSourcerName, sourcer);
			CfgCsvDao<?> helper = (CfgCsvDao<?>) sourcer;
			configHelperMap.put(configSourcerName, helper);
		}
	}

	public Iterable<IPrivilegeConfigSourcer<?>> getSources() {
		return sourcerMap.values();
	}

	private <PrivilegeNameEnums extends Enum<PrivilegeNameEnums>> PrivilegeValue getPrivilegeProperty(
			PrivilegeProperty privilegeDataSet, PrivilegeNameEnums pname) {
		if (privilegeDataSet == null || pname == null) {
			return null;
		}

		if (privilegeDataSet.getKvCount() < pname.ordinal() + 1) {
			return null;
		}

		String priName = pname.name();
		PrivilegeValue kv = privilegeDataSet.getKv(pname.ordinal());
		if (!priName.equals(kv.getName())) {
			return null;
		}
		return kv;
	}

	public <PrivilegeNameEnums extends Enum<PrivilegeNameEnums>> Integer getIntPrivilege(
			PrivilegeProperty privilegeDataSet, PrivilegeNameEnums pname) {
		PrivilegeValue kv = getPrivilegeProperty(privilegeDataSet, pname);
		if (kv == null) {
			return null;
		}

		IntPropertyWriter pwriter = IntPropertyWriter.getShareInstance();
		return pwriter.extractVal(kv.getValue());
	}

	public <PrivilegeNameEnums extends Enum<PrivilegeNameEnums>> Boolean getBoolPrivilege(
			PrivilegeProperty privilegeDataSet, PrivilegeNameEnums pname) {
		PrivilegeValue kv = getPrivilegeProperty(privilegeDataSet, pname);
		if (kv == null) {
			return null;
		}

		BoolPropertyWriter pwriter = BoolPropertyWriter.getShareInstance();
		return pwriter.extractVal(kv.getValue());
	}
}
