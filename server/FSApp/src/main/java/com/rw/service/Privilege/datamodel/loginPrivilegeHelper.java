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
import com.rwproto.PrivilegeProtos.LoginPrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.loginPrivilegeHelper"  init-method="init" />

public class loginPrivilegeHelper extends AbstractPrivilegeConfigHelper<LoginPrivilegeNames, loginPrivilege> {
	public static loginPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(loginPrivilegeHelper.class);
	}

	@Override
	public Map<String, loginPrivilege> initJsonCfg() {
		return initJsonCfg("Privilege/loginPrivilege.csv",loginPrivilege.class, LoginPrivilegeNames.class);
	}
	
	@Override
	public void setValue(Builder holder, PrivilegeProperty.Builder value) {
		holder.setLogin(value);
	}

	@Override
	public PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getLoginBuilder();
	}

	@Override
	protected IPrivilegeThreshold<LoginPrivilegeNames> getThresholder() {
		return loginPrivilegePropertiesHelper.getInstance();
	}

	@Override
	protected void putPrivilege(
			AbstractPrivilegeConfigHelper<LoginPrivilegeNames, loginPrivilege> abstractPrivilegeConfigHelper,
			IPrivilegeWare privilegeMgr,
			List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap) {
		privilegeMgr.putLoginPrivilege(this,tmpMap);
	}

	@Override
	public PrivilegeProperty getValue(AllPrivilege pri) {
		return pri.getLogin();
	}
}