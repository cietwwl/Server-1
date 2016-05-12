package com.rw.service.Privilege.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.PrivilegeProtos.StorePrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.storePrivilegePropertiesHelper"  init-method="init" />

public class storePrivilegePropertiesHelper extends AbstractPrivilegePropertiesHelper<StorePrivilegeNames, storePrivilegeProperties> {
	public static storePrivilegePropertiesHelper getInstance() {
		return SpringContextUtil.getBean(storePrivilegePropertiesHelper.class);
	}

	@Override
	public Map<String, storePrivilegeProperties> initJsonCfg() {
		return initJson("Privilege/storePrivilegeProperties.csv",storePrivilegeProperties.class, StorePrivilegeNames.class);
	}
}