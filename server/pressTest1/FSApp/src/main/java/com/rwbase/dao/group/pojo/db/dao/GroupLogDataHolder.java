package com.rwbase.dao.group.pojo.db.dao;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.group.pojo.db.GroupLog;
import com.rwbase.dao.group.pojo.db.GroupLogData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/**
 * 帮派日志的数据修改
 * 
 * @author HC
 *
 */
public class GroupLogDataHolder {

	private GroupLogData groupLogData;
	final private eSynType synType = eSynType.GroupLog;

	public GroupLogDataHolder(String groupId) {
		groupLogData = GroupLogDataDAO.getDAO().get(groupId);
		if (groupLogData == null) {
			groupLogData = new GroupLogData(groupId);
			GroupLogDataDAO.getDAO().add(groupLogData);
		}
	}

	/**
	 * 增加帮派日志
	 * 
	 * @param log
	 */
	public void add(GroupLog log) {
		groupLogData.addLog(log);
		flush();
	}

	/**
	 * 同步帮派日志
	 * 
	 * @param player
	 */
	public void synData(Player player, int version) {
		List<GroupLog> logList = groupLogData.getLogList();
		if (logList == null) {
			return;
		}

		ClientDataSynMgr.synDataList(player, logList, synType, eSynOpType.UPDATE_LIST);
	}

	/**
	 * 刷新帮派日志数据
	 */
	public void flush() {
		GroupLogDataDAO dao = GroupLogDataDAO.getDAO();
		dao.add(groupLogData);
	}
}