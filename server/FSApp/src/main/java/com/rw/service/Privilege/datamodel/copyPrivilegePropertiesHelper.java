package com.rw.service.Privilege.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.PrivilegeProtos.CopyPrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.copyPrivilegePropertiesHelper"  init-method="init" />

public class copyPrivilegePropertiesHelper extends AbstractPrivilegePropertiesHelper<CopyPrivilegeNames, copyPrivilegeProperties> {
	public static copyPrivilegePropertiesHelper getInstance() {
		return SpringContextUtil.getBean(copyPrivilegePropertiesHelper.class);
	}

	@Override
	public Map<String, copyPrivilegeProperties> initJsonCfg() {
		return initJson("Privilege/copyPrivilegeProperties.csv",copyPrivilegeProperties.class, CopyPrivilegeNames.class);
	}
}