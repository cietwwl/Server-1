package com.rw.service.Privilege.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.PrivilegeProtos.ArenaPrivilegeNames;

//<bean class="com.rw.service.Privilege.datamodel.arenaPrivilegePropertiesHelper"  init-method="init" />
public class arenaPrivilegePropertiesHelper
		extends AbstractPrivilegePropertiesHelper<ArenaPrivilegeNames, arenaPrivilegeProperties> {
	public static arenaPrivilegePropertiesHelper getInstance() {
		return SpringContextUtil.getBean(arenaPrivilegePropertiesHelper.class);
	}

	@Override
	public Map<String, arenaPrivilegeProperties> initJsonCfg() {
		return initJson("Privilege/arenaPrivilegeProperties.csv",arenaPrivilegeProperties.class, ArenaPrivilegeNames.class);
	}
}