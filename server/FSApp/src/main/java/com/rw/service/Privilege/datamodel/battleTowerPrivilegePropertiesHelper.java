package com.rw.service.Privilege.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.PrivilegeProtos.BattleTowerPrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.battleTowerPrivilegePropertiesHelper"  init-method="init" />

public class battleTowerPrivilegePropertiesHelper extends AbstractPrivilegePropertiesHelper<BattleTowerPrivilegeNames, battleTowerPrivilegeProperties> {
	public static battleTowerPrivilegePropertiesHelper getInstance() {
		return SpringContextUtil.getBean(battleTowerPrivilegePropertiesHelper.class);
	}

	@Override
	public Map<String, battleTowerPrivilegeProperties> initJsonCfg() {
		return initJson("Privilege/battleTowerPrivilegeProperties.csv",battleTowerPrivilegeProperties.class, BattleTowerPrivilegeNames.class);
	}
}