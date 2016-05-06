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
import com.rwproto.PrivilegeProtos.BattleTowerPrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.battleTowerPrivilegeHelper"  init-method="init" />

public class battleTowerPrivilegeHelper extends AbstractPrivilegeConfigHelper<BattleTowerPrivilegeNames, battleTowerPrivilege> {
	public static battleTowerPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(battleTowerPrivilegeHelper.class);
	}

	@Override
	public Map<String, battleTowerPrivilege> initJsonCfg() {
		return initJsonCfg("Privilege/battleTowerPrivilege.csv",battleTowerPrivilege.class, BattleTowerPrivilegeNames.class);
	}
	
	@Override
	public void setValue(Builder holder, PrivilegeProperty.Builder value) {
		holder.setBattleTower(value);
	}

	@Override
	public PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getBattleTowerBuilder();
	}

	@Override
	protected IPrivilegeThreshold<BattleTowerPrivilegeNames> getThresholder() {
		return battleTowerPrivilegePropertiesHelper.getInstance();
	}

	@Override
	protected void putPrivilege(
			AbstractPrivilegeConfigHelper<BattleTowerPrivilegeNames, battleTowerPrivilege> abstractPrivilegeConfigHelper,
			IPrivilegeWare privilegeMgr,
			List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap) {
		privilegeMgr.putBattleTowerPrivilege(this,tmpMap);
	}

	@Override
	public PrivilegeProperty getValue(AllPrivilege pri) {
		return pri.getBattleTower();
	}
}