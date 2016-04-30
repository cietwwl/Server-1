package com.rw.service.Privilege;

import com.rw.fsutil.common.stream.IStream;
import com.rwproto.PrivilegeProtos.*;

public interface IPrivilegeManager {
	//竞技场特权点
	public IStream<ArenaPrivilege> getArenaPrivilege();
	public IStream<PeakArenaPrivilege> getPeakArenaPrivilege();
}
