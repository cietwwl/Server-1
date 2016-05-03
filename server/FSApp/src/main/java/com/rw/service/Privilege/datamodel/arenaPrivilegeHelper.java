package com.rw.service.Privilege.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.PrivilegeProtos.AllPrivilege.Builder;
import com.rwproto.PrivilegeProtos.ArenaPrivilegeNames;

public class arenaPrivilegeHelper extends AbstractPrivilegeConfigHelper<ArenaPrivilegeNames, arenaPrivilege> {
	public static arenaPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(arenaPrivilegeHelper.class);
	}

	@Override
	public Map<String, arenaPrivilege> initJsonCfg() {
		return initJsonCfg("Privilege/arenaPrivilege.csv", arenaPrivilege.class, ArenaPrivilegeNames.class);
	}

	@Override
	public void setValue(Builder holder, com.rwproto.PrivilegeProtos.PrivilegeProperty.Builder value) {
		holder.setArena(value);
	}

	@Override
	public com.rwproto.PrivilegeProtos.PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getArenaBuilder();
	}

}