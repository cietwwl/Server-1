package com.playerdata.groupFightOnline.manager;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupFightOnline.bm.GFightFinalBM;
import com.playerdata.groupFightOnline.bm.GFightGroupBidBM;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;
import com.playerdata.groupFightOnline.dataForClient.GFFightRecord;
import com.playerdata.groupFightOnline.enums.GFResourceState;

public class GFightOnlineResourceMgr {
	
	private static GFightOnlineResourceMgr instance = new GFightOnlineResourceMgr();
	
	public static GFightOnlineResourceMgr getInstance() {
		return instance;
	}
	
	public GFightOnlineResourceData get(int resourceID) {
		return GFightOnlineResourceHolder.getInstance().get(resourceID);
	}
	
	public void update(GFightOnlineResourceData data) {
		GFightOnlineResourceHolder.getInstance().update(data);
	}
	
	/**
	 * 设置占领资源点的帮派
	 * 或者是更换占有者
	 * @param resourceID
	 * @param victoryGroupID
	 */
	public void setVictoryGroup(int resourceID, String victoryGroupID){
		GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(resourceID);
		resData.setOwnerGroupID(victoryGroupID);
		GFightOnlineResourceHolder.getInstance().update(resData);
	}
	
	public void synData(Player player){
		GFightOnlineResourceHolder.getInstance().synData(player);
	}
	
	public void checkGFightResourceState(){
		List<GFightOnlineResourceCfg> cfgs = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		for(GFightOnlineResourceCfg cfg : cfgs){
			GFResourceState state = cfg.checkResourceState();
			GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(cfg.getResID());
			if(resData == null) {
				resData = new GFightOnlineResourceData();
				resData.setResourceID(cfg.getResID());
			}
			switch (state) {
			case REST:
				if(GFResourceState.FIGHT.equals(resData.getState())){
					fightEndEvent(cfg.getResID());
				}
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
			GFightOnlineResourceHolder.getInstance().update(resData);
		}
	}
	
	private void fightEndEvent(int resourceID){
		GFightFinalBM.getInstance().handleGFightResult(resourceID);
	}
	
	private void biddingStartEvent(int resourceID){
		GFightGroupBidBM.getInstance().bidStart(resourceID);
	}
	
	private void prepareStartEvent(int resourceID){
		//GFightPrepareBM.getInstance().prepareStart(resourceID);
	}
	
	private void fightStartEvent(int resourceID){
		//GFightOnFightBM.getInstance().fightStart(resourceID);
	}

	public void addFightRecord(int resourceID, GFFightRecord record){
		GFightOnlineResourceHolder.getInstance().addFightRecord(resourceID, record);
	}
	
	public List<GFFightRecord> getFightRecord(int resourceID){
		return GFightOnlineResourceHolder.getInstance().getFightRecord(resourceID);
	}
}
