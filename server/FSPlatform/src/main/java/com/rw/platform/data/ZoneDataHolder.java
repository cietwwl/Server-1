package com.rw.platform.data;

import java.util.List;

import com.rwbase.dao.platformNotice.TablePlatformNotice;
import com.rwbase.dao.platformNotice.TablePlatformNoticeDAO;
import com.rwbase.dao.zone.TableZoneInfo;
import com.rwbase.dao.zone.TableZoneInfoDAO;

public class ZoneDataHolder extends ADataHolder{

	private volatile List<TableZoneInfo> list;
	
	public ZoneDataHolder() {
		// TODO Auto-generated constructor stub
	}
	

	
	public List<TableZoneInfo> getZoneList(){
		List<TableZoneInfo> list = TableZoneInfoDAO.getInstance().getAll();
		return list;
	}
	
	public void updateZoneInfo(TableZoneInfo zoneInfo){
		synchronized (_lock) {
			TableZoneInfoDAO.getInstance().update(zoneInfo);
		}
	}
}
