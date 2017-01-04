package com.rwbase.dao.group.pojo.db;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.group.pojo.db.dao.GroupLogDataDAO;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/**
 * 帮派日志的数据修改
 * 
 * @author HC
 *
 */
public class GroupLogDataHolder {

	private static Comparator<GroupLog> comparator = new Comparator<GroupLog>() {

		@Override
		public int compare(GroupLog o1, GroupLog o2) {
			long l = o1.getTime() - o2.getTime();
			if (l == 0) {
				return 0;
			}

			return l > 0 ? -1 : 1;
		}
	};

	private GroupLogData groupLogData;
	private eSynType synType = eSynType.GroupLog;

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
		if (logList == null || logList.isEmpty()) {
			return;
		}

		Collections.sort(logList, comparator);
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