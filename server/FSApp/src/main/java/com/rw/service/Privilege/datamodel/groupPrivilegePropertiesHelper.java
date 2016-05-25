package com.rw.service.Privilege.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.PrivilegeProtos.GroupPrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.groupPrivilegePropertiesHelper"  init-method="init" />

public class groupPrivilegePropertiesHelper extends AbstractPrivilegePropertiesHelper<GroupPrivilegeNames, groupPrivilegeProperties> {
	public static groupPrivilegePropertiesHelper getInstance() {
		return SpringContextUtil.getBean(groupPrivilegePropertiesHelper.class);
	}

	@Override
	public Map<String, groupPrivilegeProperties> initJsonCfg() {
		return initJson("Privilege/groupPrivilegeProperties.csv",groupPrivilegeProperties.class, GroupPrivilegeNames.class);
	}
}