package com.rw.service.redpoint;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.common.HPCUtil;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.RedPointMgr;
import com.rw.service.redpoint.impl.RedPointCollector;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.redpoint.RedPointMapCfgDAO;
import com.rwproto.MsgDef;
import com.rwproto.RedPointProtos.DisplayRedPoint;
import com.rwproto.RedPointProtos.RedPoint;
import com.rwproto.RedPointProtos.RedPointPushMsg;

public class RedPointManager {

	public static RedPointManager instance = new RedPointManager();
	private ArrayList<RedPointCollector> list;
	private final RedPointType[] redPointTypeArray;

	public static RedPointManager getRedPointManager() {
		return instance;
	}

	RedPointManager() {
		try {
			list = new ArrayList<RedPointCollector>();
			List<Class<? extends RedPointCollector>> l = HPCUtil.getAllAssignedClass(RedPointCollector.class, RedPointCollector.class.getPackage().getName());
			for (Class<? extends RedPointCollector> c : l) {
				list.add(c.newInstance());
			}
			this.redPointTypeArray = RedPointType.values();
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public void checkRedPointVersion(Player player, int version) {
		RedPointMgr mgr = player.getRedPointMgr();
		Map<RedPointType, List<String>> oldMap = mgr.getMap();
		Map<RedPointType, List<String>> currentMap = getRedPointMap(player);
		boolean changed = false;
		if (oldMap == null) {
			changed = true;
		} else if (oldMap.size() != currentMap.size()) {
			changed = true;
		} else {
			for (Map.Entry<RedPointType, List<String>> entry : oldMap.entrySet()) {
				List<String> oldList = entry.getValue();
				List<String> list = currentMap.get(entry.getKey());
				if (list == null) {
					changed = true;
					break;
				}
				int oldSize = oldList.size();
				int newSize = list.size();
				if (oldSize != newSize) {
					changed = true;
					break;
				}
				for (int i = oldSize; --i >= 0;) {
					if (!list.contains(oldList.get(i))) {
						changed = true;
						break;
					}
				}
			}
		}
		if (changed) {
			mgr.setMap(currentMap);
			mgr.setVersion(mgr.getVersion() + 1);
		}
		int curVersion = mgr.getVersion();
		if (version != curVersion) {
			RedPointMapCfgDAO cfgDAO = RedPointMapCfgDAO.getCfgDAO();
			ArrayList<RedPoint> redPointList = new ArrayList<RedPoint>();
			for (Map.Entry<RedPointType, List<String>> entry : currentMap.entrySet()) {
				RedPoint.Builder builder = RedPoint.newBuilder();
				// builder.setType(entry.getKey().ordinal());
				builder.setType(cfgDAO.getRedPointType(entry.getKey()));
				builder.addAllFunctionIdList(entry.getValue());
				redPointList.add(builder.build());
			}
			DisplayRedPoint.Builder drBuilder = DisplayRedPoint.newBuilder();
			drBuilder.addAllRedPoints(redPointList);
			drBuilder.setVersion(curVersion);
			RedPointPushMsg.Builder builder = RedPointPushMsg.newBuilder();
			builder.setAllRedPoints(drBuilder);
			player.SendMsg(MsgDef.Command.MSG_RED_POINT, builder.build().toByteString());
		}
	}

	public Map<RedPointType, List<String>> getRedPointMap(Player player) {
		int level = player.getLevel();
		CfgOpenLevelLimitDAO levelLimitDAO = CfgOpenLevelLimitDAO.getInstance();
		EnumMap<RedPointType, List<String>> map = new EnumMap<RedPointType, List<String>>(RedPointType.class);
		for (int i = list.size(); --i >= 0;) {
			try {
				RedPointCollector collector = list.get(i);
				eOpenLevelType openLevelType = collector.getOpenType();
				if (openLevelType != null) {
					if (!levelLimitDAO.isOpen(openLevelType, player)) {
						continue;
					}
				}
				collector.fillRedPoints(player, map, level);
			} catch (Throwable e) {
				GameLog.error("RedPointManager", "#getRedPointMap()", "红点刷新异常", e);
			}
		}
		return map;
	}

	public RedPointType getRedPointType(int order) {
		return redPointTypeArray[order];
	}

}
