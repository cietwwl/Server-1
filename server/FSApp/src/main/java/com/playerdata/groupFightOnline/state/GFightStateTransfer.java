package com.playerdata.groupFightOnline.state;

import java.util.List;

import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.enums.GFResourceState;
import com.playerdata.groupFightOnline.manager.GFightOnlineResourceMgr;

public class GFightStateTransfer {
	
	private boolean isAutoCheck = true;
	
	private static GFightStateTransfer instance = new GFightStateTransfer();
	
	public static GFightStateTransfer getInstance(){
		return instance;
	}
	
	public void checkTransfer(){
		if(!isAutoCheck) return;
		List<GFightOnlineResourceCfg> cfgs = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		for(GFightOnlineResourceCfg cfg : cfgs){
			GFightOnlineResourceData resData = GFightOnlineResourceMgr.getInstance().get(cfg.getResID());
			if(resData == null) {
				resData = new GFightOnlineResourceData();
				resData.setResourceID(cfg.getResID());
				resData.setState(GFResourceState.INIT.getValue());
			}
			GFResourceState lastState = GFResourceState.getState(resData.getState());
			IGFightState gfState = getGFightState(cfg.getResID(), lastState);
			GFResourceState resCurrentState = cfg.checkResourceState();
			while(gfState.canExit(resCurrentState)){
				gfState = gfState.getNext();
				gfState.Enter();
			}
			if(gfState.getStateValue() != resData.getState()){
				resData.setState(gfState.getStateValue());
				GFightOnlineResourceMgr.getInstance().update(resData);
			}
		}
	}
	
	/**
	 * 设置是否自动切换服务端状态
	 * GM专用
	 * @param isAuto
	 */
	public void setAutoCheck(boolean isAuto){
		this.isAutoCheck = isAuto;
	}
	
	/**
	 * 只用于主动控制资源点时间段,使用这个的时候,需要关掉自动检测
	 * GM专用
	 * @param resourceID 资源点id
	 * @param state 要调整的状态
	 */
	public void transferToState(int resourceID, int state){
		setAutoCheck(false);
		GFightOnlineResourceData resData = GFightOnlineResourceMgr.getInstance().get(resourceID);
		GFResourceState lastState = GFResourceState.getState(resData.getState());
		IGFightState gfState = getGFightState(resourceID, lastState);
		GFResourceState resCurrentState = GFResourceState.getState(state);
		while(gfState.canExit(resCurrentState)){
			gfState = gfState.getNext();
			gfState.Enter();
		}
		if(gfState.getStateValue() != resData.getState()){
			resData.setState(gfState.getStateValue());
			GFightOnlineResourceMgr.getInstance().update(resData);
		}
	}
	
	public IGFightState getGFightState(int resourceID, GFResourceState state){
		switch (state) {
		case INIT:
			return new GFightInit(resourceID, state);
		case BIDDING:
			return new GFightBidding(resourceID, state);
		case PREPARE:
			return new GFightPrepare(resourceID, state);
		case FIGHT:
			return new GFightFight(resourceID, state);
		case REST:
			return new GFightRest(resourceID, state);
		default:
			return new GFightInit(resourceID, state);
		}
	}
}
