package com.rwbase.dao.task;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.dao.task.pojo.DailyActivityTaskItem;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class DailyActivityHolder {

	private final String userId;
	private TableDailyActivityItemDAO dao = TableDailyActivityItemDAO.getInstance();

	public DailyActivityHolder(Player playerP) {
		this.userId = playerP.getUserId();
	}

	public DailyActivityTaskItem getTaskItem() {
		return dao.get(userId);
	}

	public void save() {
		dao.update(userId);
	}

	public String getUserId() {
		return userId;
	}

}
