package com.rw.service.log.behavior;

import com.google.protobuf.ProtocolMessageEnum;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.dao.cache.trace.DataEventRecorder;
import com.rwproto.MsgDef.Command;

public class GameBehaviorMgr {

	private static GameBehaviorMgr _instance = new GameBehaviorMgr();

	public static GameBehaviorMgr getInstance() {
		return _instance;
	}

	public void registerBehavior(String userId, Command command, ProtocolMessageEnum reqType, int viewId) {
		GameBehaviorRecord record = new GameBehaviorRecord(userId, command, reqType, viewId);
		DataEventRecorder.startDataEventCollect(record);
	}

	public void setMapId(String userId, int mapId) {
		GameBehaviorRecord record = (GameBehaviorRecord) DataEventRecorder.getParam();
		if (record == null) {
			return;
		}
		if (record.getUserId().equals(userId)) {
			record.setMapId(mapId);
		} else {
			FSUtilLogger.error("setMapId wrong userId:" + record.getUserId() + "," + userId);
		}
	}

}
