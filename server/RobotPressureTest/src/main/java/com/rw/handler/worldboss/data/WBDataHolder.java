package com.rw.handler.worldboss.data;

import com.rw.common.RobotLog;
import com.rw.dataSyn.DataSynHelper;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.eSynOpType;

public class WBDataHolder {

	
	private WBData data;
	private int version;
	
	public void syn(MsgDataSyn msgDataSyn){
		eSynOpType synType = msgDataSyn.getSynOpType();
		if(version == msgDataSyn.getVersion()){
			return;
		}
			
		version = msgDataSyn.getVersion();
		switch (synType) {
		case UPDATE_LIST:
		case UPDATE_SINGLE:
			data = DataSynHelper.ToObject(WBData.class, msgDataSyn.getSynData(0).getJsonData());
			break;
		case REMOVE_SINGLE:
			data = null;
			break;
		default:
			break;
		}
	}
	
	
	public WBState getCurState(){
		if(data == null){
			RobotLog.testInfo("检查世界boss数据状态的时候，发现数据为空！");
			return WBState.NewBoss;
		}
		return data.getState();
	}
	
	
	public int getWBDataVersion(){
		return version;
	}


	public long getCurLift() {
		if(data == null){
			return 0;
		}
		return data.getCurLife();
	}
}
