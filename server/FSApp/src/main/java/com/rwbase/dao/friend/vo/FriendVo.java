package com.rwbase.dao.friend.vo;

import com.common.HPCUtil;
import com.playerdata.readonly.FriendVoIF;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.friend.CfgFriendGiftDAO;

public class FriendVo implements FriendVoIF {
	private int receivePower;
	private volatile long lastResetMillis;

	/** 领取体力数 */
	public int getReceivePower() {
		return receivePower;
	}

	public void setReceivePower(int receivePower) {
		this.receivePower = receivePower;
	}

	public void addOnePower(int receivePower) {
		this.receivePower += receivePower;
	}

	public long getLastResetMillis() {
		return lastResetMillis;
	}

	public void setLastResetMillis(long lastResetMillis) {
		this.lastResetMillis = lastResetMillis;
	}

	public int getAndCheckReceivePower() {
		//这里不主动保存，有修复附带一起
		if (HPCUtil.isResetTime(lastResetMillis)) {
			this.lastResetMillis = System.currentTimeMillis();
			this.receivePower = 0;
		}
		return receivePower;
	}

	/** 今日还可领取次数 */
	public int getSurplusCount(int level) {
		CfgFriendGift cfg = CfgFriendGiftDAO.getInstance().getFriendGiftCfg(level + "");
		return cfg.getReceiveLimit() - getAndCheckReceivePower();
	}

	/** 是否达到了领取上限 */
	public boolean isCanReceive(int level) {
		CfgFriendGift cfg = CfgFriendGiftDAO.getInstance().getFriendGiftCfg(level + "");
		if (getAndCheckReceivePower() < cfg.getReceiveLimit()) {
			return true;
		} else {
			return false;
		}
	}
}
