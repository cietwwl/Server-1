package com.rw.service.log.behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.playerdata.Player;
import com.rw.fsutil.dao.cache.trace.DataEventRecorder;
import com.rwproto.MsgDef.Command;

public class GameBehaviorMgr {

	private static GameBehaviorMgr _instance = new GameBehaviorMgr();

	public static GameBehaviorMgr getInstance() {
		return _instance;
	}

	private final static HashMap<Command, HashMap<Object, String>> BehaviorMap = new HashMap<Command, HashMap<Object, String>>();

	public void registerBehavior(Player player, Command command, Object obj, String value, int viewId) {
		if (obj != null) {
			if (BehaviorMap.containsKey(command)) {
				HashMap<Object, String> subMap = BehaviorMap.get(command);
				if (!subMap.containsKey(obj)) {
					subMap.put(obj, value);
				}
			} else {
				HashMap<Object, String> subMap = new HashMap<Object, String>();
				subMap.put(obj, value);
				BehaviorMap.put(command, subMap);
			}
		}

		startRecordData(player, command, obj, viewId);
	}

	private void startRecordData(Player player, Command command, Object reqType, int viewId) {

		List<Object> list = new ArrayList<Object>();

		list.add(player); // 玩家
		list.add(command); // command
		list.add(viewId); // viewId 待提供
		list.add(reqType); // 二级协议类型
		DataEventRecorder.startDataEventCollect(list);
	}	
	
	@SuppressWarnings("unchecked")
	public void setMapId(Player player, int mapId){
		List<Object> param = (List<Object>)DataEventRecorder.getParam();
		if(param == null){
			return;
		}
		param.add(mapId);
	}

	public String getSecondBehavior(Command command, Object obj) {
		HashMap<Object, String> subMap = BehaviorMap.get(command);
		if (subMap == null) {
			return null;
		}
		String value = subMap.get(obj);
		return value;
	}
}
