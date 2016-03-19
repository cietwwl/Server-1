package com.rwbase.dao.setting;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.setting.pojo.TableSettingData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SettingDataHolder {
	
	private TableSettingDataDAO m_SettingTableDAO = TableSettingDataDAO.getInstance();
	final private eSynType synType = eSynType.SETTING_DATA;
	private final String userId;

	public SettingDataHolder(Player player) {
		this.userId = player.getUserId();
	}

	public void syn(Player player) {
		TableSettingData m_SettingTable = get();
		if (m_SettingTable != null) {
			ClientDataSynMgr.synData(player, m_SettingTable, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("SettingDataHolder", "#syn()", "find TableSettingData fail:" + userId);
		}
	}

	public TableSettingData get() {
		return m_SettingTableDAO.get(userId);
	}

	public void update(Player player) {
		TableSettingData m_SettingTable = get();
		if (m_SettingTable != null) {
			ClientDataSynMgr.updateData(player, m_SettingTable, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("SettingDataHolder", "#update()", "find TableSettingData fail:" + userId);
		}
	}

}
