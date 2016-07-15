package com.playerdata.groupFightOnline.state;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;
import com.playerdata.groupFightOnline.enums.GFResourceState;

public class GFightStateTransfer {

	private List<IGFightState> curStates = new ArrayList<IGFightState>();
	private GFightStateTransfer instance = new GFightStateTransfer();
	
	public void init(){
		List<GFightOnlineResourceCfg> cfgs = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		for(GFightOnlineResourceCfg cfg : cfgs){
			GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(cfg.getResID());
			GFResourceState state = GFResourceState.getState(resData.getState());
			switch (state) {
			case REST:
				curStates.add(new GFightRest(cfg.getResID()));
				break;
			case BIDDING:
				curStates.add(new GFightBidding(cfg.getResID()));
				break;
			case PREPARE:
				curStates.add(new GFightPrepare(cfg.getResID()));
				break;
			case FIGHT:
				curStates.add(new GFightFight(cfg.getResID()));
				break;
			default:
				curStates.add(new GFightRest(cfg.getResID()));
				break;
			}
		}
	}
	
	public GFightStateTransfer getInstance(){
		return instance;
	}
	
	public void checkTransfer(){
		for(IGFightState curState : curStates){
			if(curState.canExit()){
				curState = curState.getNext();
				curState.Enter();
			}
		}
	}
}
