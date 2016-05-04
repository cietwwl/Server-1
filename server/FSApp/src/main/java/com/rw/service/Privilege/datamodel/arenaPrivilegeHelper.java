package com.rw.service.Privilege.datamodel;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.Privilege.IPrivilegeProvider;
import com.rw.service.Privilege.IPrivilegeWare;
import com.rwproto.PrivilegeProtos.AllPrivilege;
import com.rwproto.PrivilegeProtos.AllPrivilege.Builder;
import com.rwproto.PrivilegeProtos.ArenaPrivilegeNames;
import com.rwproto.PrivilegeProtos.PrivilegeProperty;
import com.rwproto.PrivilegeProtos.PrivilegePropertyOrBuilder;

//<bean class="com.rw.service.Privilege.datamodel.arenaPrivilegeHelper"  init-method="init" />
public class arenaPrivilegeHelper extends AbstractPrivilegeConfigHelper<ArenaPrivilegeNames, arenaPrivilege> {
	public static arenaPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(arenaPrivilegeHelper.class);
	}

	@Override
	public Map<String, arenaPrivilege> initJsonCfg() {
		return initJsonCfg("Privilege/arenaPrivilege.csv", arenaPrivilege.class, ArenaPrivilegeNames.class);
	}

	@Override
	public void setValue(Builder holder, PrivilegeProperty.Builder value) {
		holder.setArena(value);
	}

	@Override
	public PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getArenaBuilder();
	}

	@Override
	protected IPrivilegeThreshold<ArenaPrivilegeNames> getThresholder() {
		return arenaPrivilegePropertiesHelper.getInstance();
	}

	@Override
	protected void putPrivilege(
			AbstractPrivilegeConfigHelper<ArenaPrivilegeNames, arenaPrivilege> abstractPrivilegeConfigHelper,
			IPrivilegeWare privilegeMgr,
			List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap) {
		privilegeMgr.putArenaPrivilege(this,tmpMap);
	}

	@Override
	public PrivilegePropertyOrBuilder getValue(AllPrivilege pri) {
		return pri.getArena();
	}
}