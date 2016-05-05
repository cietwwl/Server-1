package com.rw.service.Privilege.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.PrivilegeProtos.PeakArenaPrivilegeNames;

//<bean class="com.rw.service.Privilege.datamodel.peakArenaPrivilegePropertiesHelper"  init-method="init" />
public class peakArenaPrivilegePropertiesHelper extends AbstractPrivilegePropertiesHelper<PeakArenaPrivilegeNames,peakArenaPrivilegeProperties> {
	public static peakArenaPrivilegePropertiesHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaPrivilegePropertiesHelper.class);
	}

	@Override
	public Map<String, peakArenaPrivilegeProperties> initJsonCfg() {
		return initJson("Privilege/peakArenaPrivilegeProperties.csv",peakArenaPrivilegeProperties.class, PeakArenaPrivilegeNames.class);
	}
}