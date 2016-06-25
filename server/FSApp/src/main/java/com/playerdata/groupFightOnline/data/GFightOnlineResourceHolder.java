package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.dataForClient.GFResourceState;
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
	
	public GFightOnlineResourceData get(int resourceID) {
		return gfResourceDao.get(String.valueOf(resourceID));
	}
	
	public void update(Player player, GFightOnlineResourceData data) {
		gfResourceDao.update(data);
	}
	
	public void synData(Player player){
		List<GFightOnlineResourceData> gfResourceData = new ArrayList<GFightOnlineResourceData>();
		List<GFightOnlineResourceCfg> allResource = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		for(GFightOnlineResourceCfg cfg : allResource){
			GFightOnlineResourceData data = get(cfg.getResID());
			if(data != null) gfResourceData.add(data);
		}
		if(gfResourceData.size() > 0) ClientDataSynMgr.synDataList(player, gfResourceData, synType, eSynOpType.UPDATE_LIST);
	}
	
	public void checkGFightResourceState(){
		List<GFightOnlineResourceCfg> cfgs = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		for(GFightOnlineResourceCfg cfg : cfgs){
			GFResourceState state = cfg.checkResourceState();
			GFightOnlineResourceData resData = gfResourceDao.get(String.valueOf(cfg.getResID()));
			if(resData == null) resData = new GFightOnlineResourceData();
			switch (state) {
			case REST:
				if(GFResourceState.FIGHT.equals(resData.getState()))
					fightEndEvent(cfg.getResID());
				resData.setState(GFResourceState.REST.getValue());
				break;
			case BIDDING:
				if(GFResourceState.REST.equals(resData.getState()) || GFResourceState.INIT.equals(resData.getState())){
					biddingStartEvent(cfg.getResID());
					resData.setState(GFResourceState.BIDDING.getValue());
				}else if(!GFResourceState.BIDDING.equals(resData.getState())){
					resData.setState(GFResourceState.REST.getValue());
				}
				break;
			case PREPARE:
				if(GFResourceState.BIDDING.equals(resData.getState())){
					prepareStartEvent(cfg.getResID());
					resData.setState(GFResourceState.PREPARE.getValue());
				}else if(!GFResourceState.PREPARE.equals(resData.getState())){
					resData.setState(GFResourceState.REST.getValue());
				}
				break;
			case FIGHT:
				if(GFResourceState.PREPARE.equals(resData.getState())){
					fightStartEvent(cfg.getResID());
					resData.setState(GFResourceState.FIGHT.getValue());
				}else if(!GFResourceState.FIGHT.equals(resData.getState())){
					resData.setState(GFResourceState.REST.getValue());
				}
				break;
			default:
				resData.setState(GFResourceState.REST.getValue());
				break;
			}
			gfResourceDao.update(resData);
		}
	}
	
	private void biddingStartEvent(int resourceID){
		
	}
	
	private void prepareStartEvent(int resourceID){
		
	}
	
	private void fightStartEvent(int resourceID){
		
	}
	
	private void fightEndEvent(int resourceID){
		
	}
}
