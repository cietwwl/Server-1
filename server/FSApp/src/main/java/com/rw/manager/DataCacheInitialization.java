package com.rw.manager;

import java.util.ArrayList;
import com.playerdata.Player;
import com.rw.fsutil.dao.cache.DataCacheFactory;
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
		DataCacheFactory.init(ignoreList);
	}
}
