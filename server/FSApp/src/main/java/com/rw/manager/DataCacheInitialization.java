package com.rw.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.common.HPCUtil;
import com.playerdata.Player;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.trace.DataChangedEvent;
import com.rw.fsutil.dao.cache.trace.DataChangedVisitor;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.trace.DataChangeListenRegister;
import com.rw.trace.CreateTrace;
import com.rwbase.dao.chat.pojo.UserPrivateChat;
import com.rwbase.dao.gameNotice.TableGameNotice;
import com.rwbase.dao.group.pojo.db.GroupLogData;
import com.rwbase.dao.guide.pojo.UserGuideProgress;
import com.rwbase.dao.guide.pojo.UserPlotProgress;
import com.rwbase.dao.serverData.ServerData;
import com.rwbase.dao.user.UserIdCache;
import com.rwbase.dao.zone.TableZoneInfo;

public class DataCacheInitialization {

	public static void init() {
		Class[] classArray = { ServerData.class, UserIdCache.class, TableZoneInfo.class, TableGameNotice.class, Player.class, GroupLogData.class, UserPrivateChat.class, UserPlotProgress.class, UserGuideProgress.class, GroupLogData.class };
		ArrayList<String> ignoreList = new ArrayList<String>(classArray.length);
		for (int i = 0; i < classArray.length; i++) {
			ignoreList.add(classArray[i].getName());
		}
		HashMap<Class<?>, DataValueParser<?>> map = new HashMap<Class<?>, DataValueParser<?>>();
		try {
			List<Class<? extends DataValueParser>> list = HPCUtil.getAllAssignedClass(DataValueParser.class, CreateTrace.PARSER_PATH);
			for (Class<? extends DataValueParser> clazz : list) {
				Class<?> type = HPCUtil.getInterfacesGeneric(clazz, DataValueParser.class);
				map.put(type, clazz.newInstance());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		DataChangeListenRegister[] changeArray = DataChangeListenRegister.values();
		List<Pair<Class<?>, Class<? extends DataChangedVisitor<DataChangedEvent<?>>>>> dataChangeListeners = new ArrayList<Pair<Class<?>, Class<? extends DataChangedVisitor<DataChangedEvent<?>>>>>();
		for (DataChangeListenRegister listener : changeArray) {
			Class<?> traceClass = listener.getTraceClass();
			Class<? extends DataChangedVisitor<?>> listenerClass = listener.getListenerClass();
			Pair<Class<?>, Class<? extends DataChangedVisitor<DataChangedEvent<?>>>> pair = Pair.<Class<?>, Class<? extends DataChangedVisitor<DataChangedEvent<?>>>> Create(traceClass, (Class<? extends DataChangedVisitor<DataChangedEvent<?>>>) listenerClass);
			dataChangeListeners.add(pair);
		}
		DataCacheFactory.init(ignoreList, map, dataChangeListeners);
	}

}
