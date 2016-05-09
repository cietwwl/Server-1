package com.rw.service.Privilege.datamodel;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.Privilege.IPrivilegeProvider;
import com.rw.service.Privilege.IPrivilegeWare;
import com.rwproto.PrivilegeProtos.AllPrivilege;
import com.rwproto.PrivilegeProtos.AllPrivilege.Builder;
import com.rwproto.PrivilegeProtos.PrivilegeProperty;
import com.rwproto.PrivilegeProtos.PvePrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.pvePrivilegeHelper"  init-method="init" />

public class pvePrivilegeHelper extends AbstractPrivilegeConfigHelper<PvePrivilegeNames, pvePrivilege> {
	public static pvePrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(pvePrivilegeHelper.class);
	}

	@Override
	public Map<String, pvePrivilege> initJsonCfg() {
		return initJsonCfg("Privilege/pvePrivilege.csv",pvePrivilege.class, PvePrivilegeNames.class);
	}
	
	@Override
	public void setValue(Builder holder, PrivilegeProperty.Builder value) {
		holder.setPve(value);
	}

	@Override
	public PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getPveBuilder();
	}

	@Override
	protected IPrivilegeThreshold<PvePrivilegeNames> getThresholder() {
		return pvePrivilegePropertiesHelper.getInstance();
	}

	@Override
	protected void putPrivilege(
			AbstractPrivilegeConfigHelper<PvePrivilegeNames, pvePrivilege> abstractPrivilegeConfigHelper,
			IPrivilegeWare privilegeMgr,
			List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap) {
		privilegeMgr.putBattleTowerPrivilege(this,tmpMap);
	}

	@Override
	public PrivilegeProperty getValue(AllPrivilege pri) {
		return pri.getPve();
	}
}