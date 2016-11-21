package com.playerdata.commonsoul;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.commonsoul.CommonSoulConfigDAO;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class CommonSoulConfigHolder {

	private static CommonSoulConfigHolder _instance = new CommonSoulConfigHolder();
	
	public static CommonSoulConfigHolder getInstance() {
		return _instance;
	}
	private CommonSoulConfigDAO _commonSoulConfigDAO;
	private eSynType synType = eSynType.CommonSoulConfig;
	private eSynOpType synOpType = eSynOpType.UPDATE_SINGLE;
	
	protected CommonSoulConfigHolder() {
		_commonSoulConfigDAO = CommonSoulConfigDAO.getInstance();
	}
	
	public void synConfig(Player player) {
		ClientDataSynMgr.synData(player, _commonSoulConfigDAO, synType, synOpType);
	}
}
