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
	

	public static GFightOnlineResourceHolder getInstance() {
		return instance;
	}

	private GFightOnlineResourceHolder() { }
	final private eSynType synType = eSynType.GFightOnlineResourceData;
	
	
	public GFightOnlineResourceData get(int resourceID) {
		return GFightOnlineResourceDAO.getInstance().get(String.valueOf(resourceID));
	}
	
	public void update(GFightOnlineResourceData data) {
		GFightOnlineResourceDAO.getInstance().update(data);
	}
	
	public void add(GFightOnlineResourceData data) {
		GFightOnlineResourceDAO.getInstance().update(data);
	}
	
	public void synData(Player player){
		
		List<GFightOnlineResourceData> gfResourceData = new ArrayList<GFightOnlineResourceData>();
		List<GFightOnlineResourceCfg> allResource = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		for(GFightOnlineResourceCfg cfg : allResource){
			GFightOnlineResourceData data = get(cfg.getResID());
			if(data != null){
				gfResourceData.add(data);
			}
		}
		if(gfResourceData.size() > 0){
			ClientDataSynMgr.synDataList(player, gfResourceData, synType, eSynOpType.UPDATE_LIST);
		}
	}
	
	

	
}
