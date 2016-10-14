package com.rwbase.dao.praise;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.praise.db.PraiseDAO;
import com.rwbase.dao.praise.db.PraiseData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/**
 * @Author HC
 * @date 2016年10月14日 上午11:33:26
 * @desc 点赞数据的Mgr
 **/

public class PraiseMgr {
	private static PraiseMgr mgr = new PraiseMgr();

	public static PraiseMgr getMgr() {
		return mgr;
	}

	/**
	 * 是否给某人点过赞
	 * 
	 * @param userId
	 * @return
	 */
	public boolean hasPraisedSomeOne(String userId, String praiseId) {
		PraiseData praiseData = PraiseDAO.getDAO().get(userId);
		if (praiseData == null) {
			return false;
		}

		// 检查是否需要重置数据
		checkOrResetData(praiseData, System.currentTimeMillis());

		return praiseData.hasPraisedSomeone(praiseId);
	}

	/**
	 * 添加一下我给那个人点了赞
	 * 
	 * @param userId
	 * @return
	 */
	public boolean addPraise(String userId, String praiseId) {
		PraiseData praiseData = PraiseDAO.getDAO().get(userId);
		if (praiseData == null) {
			return false;
		}

		// 检查是否需要重置数据
		checkOrResetData(praiseData, System.currentTimeMillis());

		if (praiseData.hasPraisedSomeone(praiseId)) {
			return false;
		}

		praiseData.addPraise(praiseId);
		return true;
	}

	/**
	 * 检查或者更新数据
	 * 
	 * @param now
	 * @param userId
	 */
	public void checkOrResetData(long now, String userId) {
		PraiseData praiseData = PraiseDAO.getDAO().get(userId);
		if (praiseData == null) {
			return;
		}

		checkOrResetData(praiseData, now);
	}

	/**
	 * 检查或者更新数据
	 * 
	 * @param praiseData
	 * @param now
	 */
	private void checkOrResetData(PraiseData praiseData, long now) {
		// 检查是否需要重置数据
		if (praiseData.checkOrResetData(now)) {
			synData(praiseData.getUserId());
		}
	}

	/**
	 * 同步数据
	 * 
	 * @param userId
	 */
	private void synData(String userId) {
		synData(PlayerMgr.getInstance().find(userId));
	}

	/**
	 * 同步数据
	 * 
	 * @param player
	 */
	public void synData(Player player) {
		PraiseData praiseData = PraiseDAO.getDAO().get(player.getUserId());
		if (praiseData == null) {
			return;
		}

		ClientDataSynMgr.synData(player, praiseData, eSynType.ACHIEVEMENT_DATA, eSynOpType.UPDATE_SINGLE);
	}
}