package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GFightOnlineResourceHolder {
	private static GFightOnlineResourceHolder instance = new GFightOnlineResourceHolder();
	private static GFightOnlineResourceDAO gfResourceDao = GFightOnlineResourceDAO.getInstance();

	public static GFightOnlineResourceHolder getInstance() {
		return instance;
	}

	private GFightOnlineResourceHolder() { }
	final private eSynType synType = eSynType.GFightOnlineResourceData;
	
	public GFightOnlineResourceData get(String resourceID) {
		return gfResourceDao.get(resourceID);
	}
	
	public void update(Player player, GFightOnlineResourceData data) {
		gfResourceDao.update(data);
	}
	
	public void synData(Player player){
		List<GFightOnlineResourceData> gfResourceData = new ArrayList<GFightOnlineResourceData>();
		List<GFightOnlineResourceCfg> allResource = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		for(GFightOnlineResourceCfg cfg : allResource){
			gfResourceData.add(get(String.valueOf(cfg.getResID())));
		}
		ClientDataSynMgr.synDataList(player, gfResourceData, synType, eSynOpType.UPDATE_LIST);
	}
}
