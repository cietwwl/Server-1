package com.rw.service.redpoint;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.RedPointMgr;
import com.playerdata.activity.ActivityRedPointManager;
import com.rw.service.redpoint.impl.RedPointCollector;
import com.rwproto.MsgDef;
import com.rwproto.RedPointProtos.DisplayRedPoint;
import com.rwproto.RedPointProtos.RedPoint;
import com.rwproto.RedPointProtos.RedPointPushMsg;

public class RedPointManager {

	public static RedPointManager instance = new RedPointManager();
	private ArrayList<RedPointCollector> list;

	public static RedPointManager getRedPointManager() {
		return instance;
	}

	private RedPointManager() {
		try {
			list = new ArrayList<RedPointCollector>();
			List<Class<RedPointCollector>> l = getAllAssignedClass(RedPointCollector.class);
			for (Class<RedPointCollector> c : l) {
				list.add(c.newInstance());
			}
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
			ArrayList<RedPoint> redPointList = new ArrayList<RedPoint>();
			for (Map.Entry<RedPointType, List<String>> entry : currentMap.entrySet()) {
				RedPoint.Builder builder = RedPoint.newBuilder();
				builder.setType(entry.getKey().ordinal());
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
		EnumMap<RedPointType, List<String>> map = new EnumMap<RedPointType, List<String>>(RedPointType.class);
		for (int i = list.size(); --i >= 0;) {
			try {
				list.get(i).fillRedPoints(player, map);
			} catch (Throwable e) {
				GameLog.error("RedPointManager", "#getRedPointMap()", "红点刷新异常", e);
			}
		}
		return map;
	}

	private static <T> List<Class<T>> getAllAssignedClass(Class<T> cls) throws ClassNotFoundException {
		List<Class<T>> classes = new ArrayList<Class<T>>();
		for (Class<T> c : getClass(cls)) {
			if (cls.isAssignableFrom(c) && !cls.equals(c)) {
				classes.add(c);
			}
		}
		return classes;
	}

	private static List<Class> getClass(Class cls) throws ClassNotFoundException {
		String pk = cls.getPackage().getName();
		String path = pk.replace(".", "/");
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader.getResource(path);
		return getClass(new File(url.getFile()), pk);
	}

	private static List<Class> getClass(File dir, String pk) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!dir.exists()) {
			return classes;
		}
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				classes.addAll(getClass(f, pk + "." + f.getName()));
			}
			String name = f.getName();
			if (name.endsWith(".class")) {
				classes.add(Class.forName(pk + "." + name.substring(0, name.length() - 6)));
			}
		}
		return classes;
	}

	public boolean reFreshRedPoint(Player player,int id, String extraInfo) {
		boolean issucce = false;
		RedPointType eNum = RedPointType.values()[id];
		switch (eNum) {
		case HOME_WINDOW_ACTIVITY:
			issucce = ActivityRedPointManager.getInstance().init(player, extraInfo);
			break;
		default:
			break;
		}		
		return issucce;
	}
}
