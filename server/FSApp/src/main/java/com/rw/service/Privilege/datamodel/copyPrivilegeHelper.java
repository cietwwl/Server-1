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
import com.rwproto.PrivilegeProtos.CopyPrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.copyPrivilegeHelper"  init-method="init" />

public class copyPrivilegeHelper extends AbstractPrivilegeConfigHelper<CopyPrivilegeNames, copyPrivilege> {
	public static copyPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(copyPrivilegeHelper.class);
	}

	@Override
	public Map<String, copyPrivilege> initJsonCfg() {
		return initJsonCfg("Privilege/copyPrivilege.csv",copyPrivilege.class, CopyPrivilegeNames.class);
	}
	
	@Override
	public void setValue(Builder holder, PrivilegeProperty.Builder value) {
		holder.setCopy(value);
	}

	@Override
	public PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getCopyBuilder();
	}

	@Override
	protected IPrivilegeThreshold<CopyPrivilegeNames> getThresholder() {
		return copyPrivilegePropertiesHelper.getInstance();
	}

	@Override
	protected void putPrivilege(
			AbstractPrivilegeConfigHelper<CopyPrivilegeNames, copyPrivilege> abstractPrivilegeConfigHelper,
			IPrivilegeWare privilegeMgr,
			List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap) {
		privilegeMgr.putCopyPrivilege(this,tmpMap);
	}

	@Override
	public PrivilegeProperty getValue(AllPrivilege pri) {
		return pri.getCopy();
	}
}