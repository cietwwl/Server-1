package com.bm.login;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.util.CollectionUtils;

import com.rw.fsutil.common.SimpleThreadFactory;
import com.rwbase.dao.zone.TableZoneInfo;
import com.rwbase.dao.zone.TableZoneInfoDAO;

public class ZoneBM {

	private static ZoneBM instance = new ZoneBM();
	private TableZoneInfoDAO tableZoneInfoDAO;

	private ScheduledThreadPoolExecutor checkExecutor;
	private volatile List<TableZoneInfo> list;

	public ZoneBM() {
		checkExecutor = new ScheduledThreadPoolExecutor(1, new SimpleThreadFactory("check_zone"));
		checkExecutor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				try {
					init();
					List<TableZoneInfo> currentList = tableZoneInfoDAO.getAll();
					if (currentList == null || currentList.isEmpty()) {
						return;
					}
					list = currentList;
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}, 3, 30, TimeUnit.SECONDS);
	}

	public static ZoneBM getInstance() {
		return instance;
	}

	public List<TableZoneInfo> getAllZoneCfg() {
		if (list == null) {
			init();
			return tableZoneInfoDAO.getAll();
		} else {
			return list;
		}
	}

	private void init() {
		if (tableZoneInfoDAO == null) {
			tableZoneInfoDAO = TableZoneInfoDAO.getInstance();
		}
	}

	public TableZoneInfo getLastZoneCfg() {
		// init();
		// List<TableZoneInfo> list = tableZoneInfoDAO.getAll();
		List<TableZoneInfo> list = getAllZoneCfg();
		if (!CollectionUtils.isEmpty(list) && list.size() > 0) {
			return list.get(list.size() - 1);
		}
		return null;
	}

	public TableZoneInfo getTableZoneInfo(int zoneId) {
		// init();
		// List<TableZoneInfo> list = tableZoneInfoDAO.getAll();
		List<TableZoneInfo> list = getAllZoneCfg();
		for (TableZoneInfo zoneCfg : list) {

			if (zoneCfg.getZoneId() == zoneId) {
				return zoneCfg;
			}
		}
		return null;
	}

	public void setZoneCfgDAO(TableZoneInfoDAO tableZoneInfoDAO) {
		init();
		this.tableZoneInfoDAO = tableZoneInfoDAO;
	}

	public boolean isListContains(List<String> list, String target) {
		if (list == null || list.size() <= 0 || target == null) {
			return false;
		}
		for (String str : list) {
			if (str.equals(target)) {
				return true;
			}
		}
		return false;
	}

}
