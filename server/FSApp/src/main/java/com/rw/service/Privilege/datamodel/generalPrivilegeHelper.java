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
import com.rwproto.PrivilegeProtos.GeneralPrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.generalPrivilegeHelper"  init-method="init" />

public class generalPrivilegeHelper extends AbstractPrivilegeConfigHelper<GeneralPrivilegeNames, generalPrivilege> {
	public static generalPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(generalPrivilegeHelper.class);
	}

	@Override
	public Map<String, generalPrivilege> initJsonCfg() {
		return initJsonCfg("Privilege/generalPrivilege.csv",generalPrivilege.class, GeneralPrivilegeNames.class);
	}
	
	@Override
	public void setValue(Builder holder, PrivilegeProperty.Builder value) {
		holder.setGeneral(value);
	}

	@Override
	public PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getGeneralBuilder();
	}

	@Override
	protected IPrivilegeThreshold<GeneralPrivilegeNames> getThresholder() {
		return generalPrivilegePropertiesHelper.getInstance();
	}

	@Override
	protected void putPrivilege(
			AbstractPrivilegeConfigHelper<GeneralPrivilegeNames, generalPrivilege> abstractPrivilegeConfigHelper,
			IPrivilegeWare privilegeMgr,
			List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap) {
		privilegeMgr.putGeneralPrivilege(this,tmpMap);
	}

	@Override
	public PrivilegeProperty getValue(AllPrivilege pri) {
		return pri.getGeneral();
	}
}