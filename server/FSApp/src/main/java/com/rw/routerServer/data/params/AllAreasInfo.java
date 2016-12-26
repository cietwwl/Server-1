package com.rw.routerServer.data.params;

import java.util.List;

import com.rwbase.dao.zone.TableZoneInfo;

public class AllAreasInfo {
	
	private List<TableZoneInfo> zoneList;

	public List<TableZoneInfo> getZoneList() {
		return zoneList;
	}

	public void setZoneList(List<TableZoneInfo> zoneList) {
		this.zoneList = zoneList;
	}
}
