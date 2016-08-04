package com.rw.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.playerdata.FriendMgr;
import com.playerdata.Player;
import com.rwbase.dao.battletower.pojo.BattleTowerRoleInfo;
import com.rwbase.dao.battletower.pojo.db.TableBattleTowerRank;
import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerRankDao;
import com.rwbase.dao.battletower.pojo.readonly.TableBattleTowerRankIF;
import com.rwproto.FriendServiceProtos.FriendInfo;

/*
 * @author HC
 * @date 2015年9月4日 上午10:16:10
 * @Description 试练塔排行信息:从这个管理里获取到的全部都只是接口数据
 */
public class BattleTowerRankManager {

	private static Comparator<TableBattleTowerRankIF> comparator = new Comparator<TableBattleTowerRankIF>() {

		@Override
		public int compare(TableBattleTowerRankIF o1, TableBattleTowerRankIF o2) {
			return o2.getRoleInfo().getFloor() - o1.getRoleInfo().getFloor();
		}
	};

	/**
	 * 获取自己对应好友的对应的试练塔历史最高数据信息列表
	 * 
	 * @param player
	 * @return
	 */
	public static List<TableBattleTowerRankIF> getFriendBattleTowerRank(Player player) {
		FriendMgr friendMgr = player.getFriendMgr();// 好友管理
		List<FriendInfo> friendList = friendMgr.getFriendList();// 好友列表

		int size = friendList.size();// 长度
		List<TableBattleTowerRankIF> list = new ArrayList<TableBattleTowerRankIF>(size);

		for (int i = 0; i < size; i++) {
			FriendInfo friendInfo = friendList.get(i);
			TableBattleTowerRank tableBattleTowerRank = TableBattleTowerRankDao.getRankByKey(friendInfo.getUserId());
			if (tableBattleTowerRank != null) {
				BattleTowerRoleInfo roleInfo = tableBattleTowerRank.getRoleInfo();
				roleInfo.setHeadFrame(friendInfo.getHeadbox());
				roleInfo.setHeadIcon(friendInfo.getHeadImage());
				list.add(tableBattleTowerRank);
			}
		}

		TableBattleTowerRank myBattleTowerRank = TableBattleTowerRankDao.getRankByKey(player.getUserId());
		if (myBattleTowerRank != null) {
			list.add(myBattleTowerRank);
		}

		Collections.sort(list, comparator);// 获取好友的排序

		return list;
	}
}