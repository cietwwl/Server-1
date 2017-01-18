package com.bm.login;

import java.util.List;

import com.rw.platform.PlatformService;
import com.rwbase.dao.zone.TableZoneInfo;
import com.rwbase.dao.zone.TableZoneInfoDAO;

public class ZoneBM {

	private static ZoneBM instance;
	private TableZoneInfoDAO tableZoneInfoDAO = TableZoneInfoDAO.getInstance();


	public static ZoneBM getInstance() {
		if(instance == null){
			instance = new ZoneBM();
		}
		return instance;
	}
	
	public List<TableZoneInfo> getAllZoneCfg()
	{
		return tableZoneInfoDAO.getAll();
	}

	public TableZoneInfo getLastZoneCfg() {
		List<TableZoneInfo> list = tableZoneInfoDAO.getAll();
		TableZoneInfo lastZone = null;
		//获取推荐服
		for (TableZoneInfo zoneInfo : list) {
			if(zoneInfo.getRecommand() == PlatformService.SERVER_RECOMMAND){
				lastZone = zoneInfo;
				break;
			}
		}
		return lastZone;
	}
	
	public TableZoneInfo getTableZoneInfo(int zoneId)
	{
		List<TableZoneInfo> list = tableZoneInfoDAO.getAll();
		for (TableZoneInfo zoneCfg : list) {

			if (zoneCfg.getZoneId() == zoneId) {
				return zoneCfg;
			}
		}
		return null;
	}

	public void setZoneCfgDAO(TableZoneInfoDAO tableZoneInfoDAO) {
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
