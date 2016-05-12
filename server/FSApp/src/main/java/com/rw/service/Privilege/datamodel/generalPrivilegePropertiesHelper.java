package com.rw.service.Privilege.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.PrivilegeProtos.GeneralPrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.generalPrivilegePropertiesHelper"  init-method="init" />

public class generalPrivilegePropertiesHelper extends AbstractPrivilegePropertiesHelper<GeneralPrivilegeNames, generalPrivilegeProperties> {
	public static generalPrivilegePropertiesHelper getInstance() {
		return SpringContextUtil.getBean(generalPrivilegePropertiesHelper.class);
	}

	@Override
	public Map<String, generalPrivilegeProperties> initJsonCfg() {
		return initJson("Privilege/generalPrivilegeProperties.csv",generalPrivilegeProperties.class, GeneralPrivilegeNames.class);
	}
}