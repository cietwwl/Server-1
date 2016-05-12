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
import com.rwproto.PrivilegeProtos.GroupPrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.groupPrivilegeHelper"  init-method="init" />

public class groupPrivilegeHelper extends AbstractPrivilegeConfigHelper<GroupPrivilegeNames, groupPrivilege> {
	public static groupPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(groupPrivilegeHelper.class);
	}

	@Override
	public Map<String, groupPrivilege> initJsonCfg() {
		return initJsonCfg("Privilege/groupPrivilege.csv",groupPrivilege.class, GroupPrivilegeNames.class);
	}
	
	@Override
	public void setValue(Builder holder, PrivilegeProperty.Builder value) {
		holder.setGroup(value);
	}

	@Override
	public PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getGroupBuilder();
	}

	@Override
	protected IPrivilegeThreshold<GroupPrivilegeNames> getThresholder() {
		return groupPrivilegePropertiesHelper.getInstance();
	}

	@Override
	protected void putPrivilege(
			AbstractPrivilegeConfigHelper<GroupPrivilegeNames, groupPrivilege> abstractPrivilegeConfigHelper,
			IPrivilegeWare privilegeMgr,
			List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap) {
		privilegeMgr.putGroupPrivilege(this,tmpMap);
	}

	@Override
	public PrivilegeProperty getValue(AllPrivilege pri) {
		return pri.getGroup();
	}
}