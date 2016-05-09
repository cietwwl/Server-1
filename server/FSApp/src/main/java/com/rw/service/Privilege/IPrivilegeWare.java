package com.rw.service.Privilege;

import java.util.List;

import com.rw.fsutil.common.Pair;
import com.rw.service.Privilege.datamodel.IPrivilegeConfigSourcer;
import com.rwproto.PrivilegeProtos.*;

public interface IPrivilegeWare {

	public void putArenaPrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap);

	public void putPeakArenaPrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap);

	public void putBattleTowerPrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap);

	public void putCopyPrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap);

	public void putGeneralPrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap);

	public void putGroupPrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap);

	public void putHeroPrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap);

	public void putLoginPrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap);

	public void putStorePrivilege(IPrivilegeConfigSourcer<?> config,List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> newPrivilegeMap);

}
