package com.rw.service.Privilege.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.PrivilegeProtos.LoginPrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.loginPrivilegePropertiesHelper"  init-method="init" />

public class loginPrivilegePropertiesHelper extends AbstractPrivilegePropertiesHelper<LoginPrivilegeNames, loginPrivilegeProperties> {
	public static loginPrivilegePropertiesHelper getInstance() {
		return SpringContextUtil.getBean(loginPrivilegePropertiesHelper.class);
	}

	@Override
	public Map<String, loginPrivilegeProperties> initJsonCfg() {
		return initJson("Privilege/loginPrivilegeProperties.csv",loginPrivilegeProperties.class, LoginPrivilegeNames.class);
	}
}