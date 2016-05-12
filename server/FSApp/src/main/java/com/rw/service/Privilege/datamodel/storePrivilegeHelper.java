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
import com.rwproto.PrivilegeProtos.StorePrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.storePrivilegeHelper"  init-method="init" />

public class storePrivilegeHelper extends AbstractPrivilegeConfigHelper<StorePrivilegeNames, storePrivilege> {
	public static storePrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(storePrivilegeHelper.class);
	}

	@Override
	public Map<String, storePrivilege> initJsonCfg() {
		return initJsonCfg("Privilege/storePrivilege.csv",storePrivilege.class, StorePrivilegeNames.class);
	}
	
	@Override
	public void setValue(Builder holder, PrivilegeProperty.Builder value) {
		holder.setStore(value);
	}

	@Override
	public PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getStoreBuilder();
	}

	@Override
	protected IPrivilegeThreshold<StorePrivilegeNames> getThresholder() {
		return storePrivilegePropertiesHelper.getInstance();
	}

	@Override
	protected void putPrivilege(
			AbstractPrivilegeConfigHelper<StorePrivilegeNames, storePrivilege> abstractPrivilegeConfigHelper,
			IPrivilegeWare privilegeMgr,
			List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap) {
		privilegeMgr.putStorePrivilege(this,tmpMap);
	}

	@Override
	public PrivilegeProperty getValue(AllPrivilege pri) {
		return pri.getStore();
	}
}