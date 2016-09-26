package com.rw.handler.peakArena;

import java.util.List;

import com.rwproto.PeakArenaServiceProtos.ArenaInfo;

public class PeakArenaDataHolder {
	List<ArenaInfo> listInfoList;

	public List<ArenaInfo> getListInfoList() {
		return listInfoList;
	}

	public void setListInfoList(List<ArenaInfo> listInfoList) {
		this.listInfoList = listInfoList;
	}
	
}
