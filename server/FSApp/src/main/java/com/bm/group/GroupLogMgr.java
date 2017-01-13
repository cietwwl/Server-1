package com.bm.group;

import com.playerdata.Player;
import com.rwbase.dao.group.pojo.db.GroupLog;
import com.rwbase.dao.group.pojo.db.GroupLogDataHolder;

/*
 * @author HC
 * @date 2016年3月1日 下午6:24:21
 * @Description 帮派的日志管理类
 */
public class GroupLogMgr {
	private GroupLogDataHolder holder;// 帮派的日志数据类

	public GroupLogMgr(String groupId) {
		holder = new GroupLogDataHolder(groupId);
	}

	/**
	 * 增加帮派日志
	 * 
	 * @param log
	 */
	public void addLog(Player player, GroupLog log) {
		holder.add(log);
		// TODO HC 当前暂时没加日志推送
	}

	/**
	 * 刷新帮派日志的数据
	 */
	public void flush() {
		holder.flush();
	}

	/**
	 * 同步日志数据
	 * 
	 * @param player
	 * @param version
	 */
	public void synLogData(Player player, int version) {
		holder.synData(player, version);
	}
}