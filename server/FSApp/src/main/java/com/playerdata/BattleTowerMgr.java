package com.playerdata;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.readonly.BattleTowerMgrIF;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.BattleTowerRankManager;
import com.rwbase.dao.battletower.pojo.cfg.dao.BattleTowerConfigCfgDao;
import com.rwbase.dao.battletower.pojo.db.TableBattleTower;
import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerDao;
import com.rwbase.dao.battletower.pojo.readonly.TableBattleTowerRankIF;

/*
 * @author HC
 * @date 2015年9月1日 下午3:05:20
 * @Description 试练塔Mgr类
 */
public class BattleTowerMgr implements BattleTowerMgrIF {
	private Player m_pPlayer;

	public void init(Player player) {
		m_pPlayer = player;
		// 获取好友的信息
		// friendRankList = BattleTowerRankManager.getFriendBattleTowerRank(player);
	}

	/**
	 * 获取试练塔的数据
	 * 
	 * @return
	 */
	public TableBattleTower getTableBattleTower() {
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		String userId = m_pPlayer.getUserId();
		TableBattleTower tableBattleTower = dao.get(userId);
		if (tableBattleTower == null) {
			tableBattleTower = new TableBattleTower(userId);
			dao.update(tableBattleTower);// 存储到数据库
		}
		return tableBattleTower;
	}

	// private TableBattleTowerRank getBattleTowerRank() {
	// // 个人试练塔历史最高数据排行榜
	// String userId = m_pPlayer.getUserId();
	// TableBattleTowerRank tableRank = TableBattleTowerRankDao.getRankByKey(userId);
	// if (tableRank == null) {
	// tableRank = new TableBattleTowerRank(userId);
	//
	// BattleTowerRoleInfo roleInfo = new BattleTowerRoleInfo(userId);
	// tableRank.setRoleInfo(roleInfo);
	//
	// TableBattleTowerRankDao.updateValue(tableRank);
	// }
	// return tableRank;
	// }

	/**
	 * 获取好友排行试练塔历史数据排行数据
	 * 
	 * @return
	 */
	public List<TableBattleTowerRankIF> getFriendRankList() {
		return BattleTowerRankManager.getFriendBattleTowerRank(this.m_pPlayer);
	}

	/**
	 * 截取好友的试练塔排行信息
	 * 
	 * @param pageIndex 截取列表的索引
	 * @return
	 */
	public List<TableBattleTowerRankIF> getFriendRankList(int pageIndex) {
		if (pageIndex <= 0) {
			return new ArrayList<TableBattleTowerRankIF>();
		}

		List<TableBattleTowerRankIF> friendRankList = getFriendRankList();

		int size = friendRankList.size();
		int perPageSize = BattleTowerConfigCfgDao.getCfgDao().getUniqueCfg().getPerPageFriendSize();

		int eIndex = pageIndex * perPageSize;// 结束索引
		int sIndex = eIndex - perPageSize;// 开始索引

		// 开始的索引都已经超过了等级
		if (sIndex >= size) {
			return new ArrayList<TableBattleTowerRankIF>();
		}

		if (eIndex > size) {
			eIndex = size;
		}

		return new ArrayList<TableBattleTowerRankIF>(friendRankList.subList(sIndex, eIndex));
	}

	/**
	 * 重置试练塔的次数
	 * 
	 * @param now
	 */
	public void resetBattleTowerResetTimes(long now) {
		TableBattleTower tableBattleTower = getTableBattleTower();
		if (tableBattleTower == null) {
			return;
		}

		if (!DateUtils.isResetTime(5, 0, 0, tableBattleTower.getResetTime())) {
			return;
		}

		tableBattleTower.setCurBossTimes(0);
		tableBattleTower.setResetTimes(0);
		tableBattleTower.setResetTime(now);
		TableBattleTowerDao.getDao().update(tableBattleTower);
	}
}