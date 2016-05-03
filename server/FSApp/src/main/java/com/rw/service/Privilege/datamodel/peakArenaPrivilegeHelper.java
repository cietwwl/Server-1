package com.rw.service.Privilege.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.PrivilegeProtos.AllPrivilege.Builder;
import com.rwproto.PrivilegeProtos.PeakArenaPrivilegeNames;

public class peakArenaPrivilegeHelper extends AbstractPrivilegeConfigHelper<PeakArenaPrivilegeNames, peakArenaPrivilege> {
	public static peakArenaPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaPrivilegeHelper.class);
	}

	@Override
	public Map<String, peakArenaPrivilege> initJsonCfg() {
		return initJsonCfg("Privilege/peakArenaPrivilege.csv", peakArenaPrivilege.class, PeakArenaPrivilegeNames.class);
	}

	@Override
	public void setValue(Builder holder, com.rwproto.PrivilegeProtos.PrivilegeProperty.Builder value) {
		holder.setPeakArena(value);
	}

	@Override
	public com.rwproto.PrivilegeProtos.PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getPeakArenaBuilder();
	}
}