package com.rw.service.Privilege.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.PrivilegeProtos.HeroPrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.heroPrivilegePropertiesHelper"  init-method="init" />

public class heroPrivilegePropertiesHelper extends AbstractPrivilegePropertiesHelper<HeroPrivilegeNames, heroPrivilegeProperties> {
	public static heroPrivilegePropertiesHelper getInstance() {
		return SpringContextUtil.getBean(heroPrivilegePropertiesHelper.class);
	}

	@Override
	public Map<String, heroPrivilegeProperties> initJsonCfg() {
		return initJson("Privilege/heroPrivilegeProperties.csv",heroPrivilegeProperties.class, HeroPrivilegeNames.class);
	}
}