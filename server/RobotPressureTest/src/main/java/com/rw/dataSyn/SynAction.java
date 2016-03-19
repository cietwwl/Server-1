package com.rw.dataSyn;

import com.rwproto.DataSynProtos.MsgDataSyn;

public interface SynAction {
	
	public void doAction(MsgDataSyn dataSyn);
	
}
