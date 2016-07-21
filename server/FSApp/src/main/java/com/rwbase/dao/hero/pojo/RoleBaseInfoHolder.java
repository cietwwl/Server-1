package com.rwbase.dao.hero.pojo;

import java.util.ArrayList;
import java.util.List;

import com.common.Action;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class RoleBaseInfoHolder {// 战斗数据

	private RoleBaseInfoDAO roleBaseItemDAO = RoleBaseInfoDAO.getInstance();
	private final String uuid;
	private static eSynType synType = eSynType.ROLE_BASE_ITEM;

	public RoleBaseInfoHolder(String uuid) {
		this.uuid = uuid;
	}

	public boolean setBaseInfo(RoleBaseInfo roleBaseItemP) {
		boolean success = false;
		if (roleBaseItemP != null) {
			success = roleBaseItemDAO.update(roleBaseItemP);
		}
		return success;
	}

	public void syn(Player player, int version) {
		RoleBaseInfo roleBaseInfo = get();
		if (roleBaseInfo != null) {
			ClientDataSynMgr.synData(player, roleBaseInfo, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("hero", "RoleBaseInfoHolder#syn()", "get hero fail:" + uuid);
		}
	}

	public RoleBaseInfo get() {
		return roleBaseItemDAO.get(uuid);
	}

	public void update(Player player) {
		roleBaseItemDAO.update(uuid);
		RoleBaseInfo roleBaseInfo = get();
		if (roleBaseInfo != null) {
			ClientDataSynMgr.updateData(player, roleBaseInfo, synType, eSynOpType.UPDATE_SINGLE);
			notifyChange();
		} else {
			GameLog.error("hero", "RoleBaseInfoHolder#update()", "get hero fail:" + uuid);
		}
	}

	// public AttrData toAttrData() {
	// RoleBaseInfo roleBaseInfo = get();
	// if (roleBaseInfo != null) {
	// return RoleBaseInfoHelper.toAttrData(roleBaseInfo);
	// } else {
	// GameLog.error("hero", "RoleBaseInfoHolder#toAttrData()", "get hero fail:" + uuid);
	// return null;
	// }
	// }
	//
	// public AttrData toQualityAttrDataForLog() {
	// RoleBaseInfo roleBaseInfo = get();
	// if (roleBaseInfo != null) {
	// return RoleBaseInfoHelper.addQualityAttrData(roleBaseInfo);
	// } else {
	// GameLog.error("hero", "RoleBaseInfoHolder#toQualityAttrDataForLog()", "get hero fail:" + uuid);
	// return null;
	// }
	// }

	public void flush() {
	}

	private List<Action> callbackList = new ArrayList<Action>();

	public void regChangeCallBack(Action callBack) {
		callbackList.add(callBack);
	}

	private void notifyChange() {
		for (Action action : callbackList) {
			action.doAction();
		}
	}

}
