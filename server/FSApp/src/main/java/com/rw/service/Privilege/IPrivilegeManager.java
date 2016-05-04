package com.rw.service.Privilege;

import com.rw.fsutil.common.stream.IStream;
import com.rwproto.PrivilegeProtos.*;

public interface IPrivilegeManager {
	//竞技场特权点
	public IStream<PrivilegeProperty> getArenaPrivilege();
	public Object getArenaPri(ArenaPrivilegeNames pname);
	public IStream<PrivilegeProperty> getPeakArenaPrivilege();
	public Object getPeakArenaPri(PeakArenaPrivilegeNames pname);
}
