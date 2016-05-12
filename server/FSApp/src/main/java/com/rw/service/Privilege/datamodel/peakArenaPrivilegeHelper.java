package com.rw.service.Privilege.datamodel;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.Privilege.IPrivilegeProvider;
import com.rw.service.Privilege.IPrivilegeWare;
import com.rwproto.PrivilegeProtos.AllPrivilege;
import com.rwproto.PrivilegeProtos.AllPrivilege.Builder;
import com.rwproto.PrivilegeProtos.PeakArenaPrivilegeNames;
import com.rwproto.PrivilegeProtos.PrivilegeProperty;

//<bean class="com.rw.service.Privilege.datamodel.peakArenaPrivilegeHelper"  init-method="init" />
public class peakArenaPrivilegeHelper extends AbstractPrivilegeConfigHelper<PeakArenaPrivilegeNames, peakArenaPrivilege> {
	public static peakArenaPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaPrivilegeHelper.class);
	}

	@Override
	public Map<String, peakArenaPrivilege> initJsonCfg() {
		return initJsonCfg("Privilege/peakArenaPrivilege.csv", peakArenaPrivilege.class, PeakArenaPrivilegeNames.class);
	}

	@Override
	public void setValue(Builder holder, PrivilegeProperty.Builder value) {
		holder.setPeakArena(value);
	}

	@Override
	public PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getPeakArenaBuilder();
	}

	@Override
	protected IPrivilegeThreshold<PeakArenaPrivilegeNames> getThresholder() {
		return peakArenaPrivilegePropertiesHelper.getInstance();
	}

	@Override
	protected void putPrivilege(
			AbstractPrivilegeConfigHelper<PeakArenaPrivilegeNames, peakArenaPrivilege> abstractPrivilegeConfigHelper,
			IPrivilegeWare privilegeMgr,
			List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap) {
		privilegeMgr.putPeakArenaPrivilege(this,tmpMap);
	}

	@Override
	public PrivilegeProperty getValue(AllPrivilege pri) {
		return pri.getPeakArena();
	}
}