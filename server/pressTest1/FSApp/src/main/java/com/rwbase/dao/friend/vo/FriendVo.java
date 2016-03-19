package com.rwbase.dao.friend.vo;

import com.playerdata.readonly.FriendVoIF;
import com.rwbase.dao.friend.CfgFriendGiftDAO;

public class FriendVo implements FriendVoIF {
	private int receivePower;

	/**领取体力数*/
	public int getReceivePower() {
		return receivePower;
	}

	public void setReceivePower(int receivePower) {
		this.receivePower = receivePower;
	}
	
	public void addOnePower(int receivePower){
		this.receivePower+= receivePower;
	}
	
	/**今日还可领取次数*/
	public int getSurplusCount(int level){
		CfgFriendGift cfg = CfgFriendGiftDAO.getInstance().getFriendGiftCfg(level + "");
		return cfg.getReceiveLimit() - receivePower;
	}
	
	/**是否达到了领取上限*/
	public boolean isCanReceive(int level){
		CfgFriendGift cfg = CfgFriendGiftDAO.getInstance().getFriendGiftCfg(level + "");
		if(receivePower < cfg.getReceiveLimit()){
			return true;
		}else{
			return false;
		}
	}
}
