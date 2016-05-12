package com.rw.service.Privilege.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.PrivilegeProtos.PvePrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.pvePrivilegePropertiesHelper"  init-method="init" />

public class pvePrivilegePropertiesHelper extends AbstractPrivilegePropertiesHelper<PvePrivilegeNames, pvePrivilegeProperties> {
	public static pvePrivilegePropertiesHelper getInstance() {
		return SpringContextUtil.getBean(pvePrivilegePropertiesHelper.class);
	}

	@Override
	public Map<String, pvePrivilegeProperties> initJsonCfg() {
		return initJson("Privilege/pvePrivilegeProperties.csv",pvePrivilegeProperties.class, PvePrivilegeNames.class);
	}
}