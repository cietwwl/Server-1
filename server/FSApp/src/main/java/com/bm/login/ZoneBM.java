package com.bm.login;

import io.netty.util.collection.IntObjectHashMap;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.netty.UserChannelMgr;
import com.rwbase.dao.zone.TableZoneInfo;
import com.rwbase.dao.zone.TableZoneInfoDAO;

public class ZoneBM {

	private static ZoneBM instance = new ZoneBM();
	private ScheduledThreadPoolExecutor checkExecutor;
	private volatile IntObjectHashMap<TableZoneInfo> zoneInfoMap;
	private int runCount;

	public ZoneBM() {
		zoneInfoMap = createZoneInfoMap();
		checkExecutor = new ScheduledThreadPoolExecutor(1, new SimpleThreadFactory("check_zone"));
		checkExecutor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				try {
					IntObjectHashMap<TableZoneInfo> zoneInfoMap = createZoneInfoMap();
					if (zoneInfoMap != null) {
						ZoneBM.this.zoneInfoMap = zoneInfoMap;
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
				if (++runCount >= 10) {
					runCount = 0;
					UserChannelMgr.purgeMsgRecord();
				}
			}
		}, 3, 30, TimeUnit.SECONDS);
	}

	public static ZoneBM getInstance() {
		return instance;
	}

	private IntObjectHashMap<TableZoneInfo> createZoneInfoMap() {
		List<TableZoneInfo> list = TableZoneInfoDAO.getInstance().getAll();
		if (list == null) {
			return null;
		}
		int size = list.size();
		if (size == 0) {
			return null;
		}
		IntObjectHashMap<TableZoneInfo> zoneInfoMap = new IntObjectHashMap<TableZoneInfo>(size << 1);
		for (TableZoneInfo t : list) {
			zoneInfoMap.put(t.getZoneId(), t);
		}
		return zoneInfoMap;
	}

	public TableZoneInfo getTableZoneInfo(int zoneId) {
		return zoneInfoMap.get(zoneId);
	}

}
