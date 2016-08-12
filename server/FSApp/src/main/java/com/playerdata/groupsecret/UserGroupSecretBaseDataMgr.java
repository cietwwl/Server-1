package com.playerdata.groupsecret;

import java.util.concurrent.TimeUnit;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.groupsecret.pojo.UserGroupSecretDataHolder;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretBaseTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretBaseCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/*
 * @author HC
 * @date 2016年5月26日 下午4:53:33
 * @Description 
 */
public class UserGroupSecretBaseDataMgr {
	private static UserGroupSecretBaseDataMgr mgr = new UserGroupSecretBaseDataMgr();

	public static UserGroupSecretBaseDataMgr getMgr() {
		return mgr;
	}

	private UserGroupSecretBaseDataMgr() {
	}

	/**
	 * 获取秘境的数据
	 * 
	 * @param userId
	 * @return
	 */
	public UserGroupSecretBaseData get(String userId) {
		return UserGroupSecretDataHolder.getHolder().get(userId);
	}

	/**
	 * 更新数据
	 * 
	 * @param userId
	 */
	public void update(String userId) {
		UserGroupSecretDataHolder.getHolder().updateData(userId);
	}

	/**
	 * 增加防守的秘境Id
	 * 
	 * @param userId
	 * @param id
	 */
	public void addDefendSecretId(String userId, String id) {
		UserGroupSecretBaseData userGroupSecretBaseData = get(userId);
		if (userGroupSecretBaseData == null) {
			return;
		}

		userGroupSecretBaseData.addDefendSecretId(id);
		update(userId);
	}

	/**
	 * 删除防守秘境的Id
	 * 
	 * @param id
	 * @param userId
	 */
	public void removeDefendSecretId(String userId, String id) {
		UserGroupSecretBaseData userGroupSecretBaseData = get(userId);
		if (userGroupSecretBaseData == null) {
			return;
		}

		userGroupSecretBaseData.removeDefendSecretId(id);
		update(userId);
	}

	/**
	 * 删除匹配到的敌人秘境Id
	 * 
	 * @param player 角色
	 * @param id 匹配到的秘境id
	 */
	public void updateMatchSecretId(Player player, String id) {
		String userId = player.getUserId();
		UserGroupSecretBaseData userGroupSecretBaseData = get(userId);
		if (userGroupSecretBaseData == null) {
			return;
		}

		userGroupSecretBaseData.setMatchSecretId(id);
		update(userId);

		// 同步数据到前端
		synData(player);
	}

	// /**
	// * 更新匹配的次数
	// *
	// * @param player 角色
	// */
	// public void updateMatchTimes(Player player) {
	// String userId = player.getUserId();
	// UserGroupSecretBaseData userGroupSecretBaseData = get(userId);
	// if (userGroupSecretBaseData == null) {
	// return;
	// }
	//
	// int matchTimes = userGroupSecretBaseData.getMatchTimes();
	// userGroupSecretBaseData.setMatchTimes(matchTimes + 1);
	// update(userId);
	//
	// // 同步数据到前端
	// synData(player);
	// }

	/**
	 * 更新领取的钥石的数量
	 * 
	 * @param player
	 * @param keyCount
	 */
	public void updateReceiveKeyCount(Player player, int keyCount, boolean updateMatchTimes) {
		String userId = player.getUserId();
		UserGroupSecretBaseData userGroupSecretBaseData = get(userId);
		if (userGroupSecretBaseData == null) {
			return;
		}

		if (updateMatchTimes) {
			int matchTimes = userGroupSecretBaseData.getMatchTimes();
			userGroupSecretBaseData.setMatchTimes(matchTimes + 1);
		}

		int allKey = userGroupSecretBaseData.getKeyCount() + keyCount;
		userGroupSecretBaseData.setKeyCount(allKey < 0 ? 0 : allKey);

		if (keyCount > 0) {
			userGroupSecretBaseData.setReceiveKeyCount(userGroupSecretBaseData.getReceiveKeyCount() + keyCount);
		}
		update(userId);

		// 同步数据到前端
		synData(player);
	}

	/**
	 * 更新购买钥石的次数和数量
	 * 
	 * @param player
	 * @param buyKeyNum
	 */
	public void updateBuyKeyData(Player player, int buyKeyNum) {
		String userId = player.getUserId();
		UserGroupSecretBaseData userGroupSecretBaseData = get(userId);
		if (userGroupSecretBaseData == null) {
			return;
		}

		userGroupSecretBaseData.setKeyCount(userGroupSecretBaseData.getKeyCount() + buyKeyNum);
		userGroupSecretBaseData.setBuyKeyTimes(userGroupSecretBaseData.getBuyKeyTimes() + 1);
		update(userId);

		// 同步数据到前端
		synData(player);
	}

	/**
	 * 检查钥石的恢复
	 * 
	 * @param player
	 */
	public void checkAndUpdateKeyData(Player player) {
		GroupSecretBaseTemplate uniqueCfg = GroupSecretBaseCfgDAO.getCfgDAO().getUniqueCfg();
		int recoveryLimit = uniqueCfg == null ? -1 : uniqueCfg.getKeyRecoveryLimit();

		String userId = player.getUserId();
		UserGroupSecretBaseData userGroupSecretBaseData = get(userId);

		int keyCount = userGroupSecretBaseData.getKeyCount();
		long lastTime = userGroupSecretBaseData.getLastRecoveryTime();
		if (recoveryLimit > 0 && keyCount >= recoveryLimit) {// 有上限，并且已经满了，不检查
			if (lastTime != 0) {
				userGroupSecretBaseData.setLastRecoveryTime(0);
				update(userId);
				synData(player);
			}
			return;
		}

		int revertTime = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.SECRET_REVERT_TIME);
		long needTimeMillis = TimeUnit.MINUTES.toMillis(revertTime);// 恢复时间

		boolean needUpdateAndSyn = false;
		long now = System.currentTimeMillis();
		if (lastTime <= 0) {
			needUpdateAndSyn = true;
			userGroupSecretBaseData.setLastRecoveryTime(now);
			lastTime = now;
		}

		// 检查当前时间总共可以恢复多少个
		long passTimeMillis = now - lastTime;
		int count = (int) (passTimeMillis / needTimeMillis);// 可以产出多少个钥石
		if (count <= 0) {
			if (needUpdateAndSyn) {
				update(userId);
				synData(player);
			}
			return;
		}

		if (count >= recoveryLimit) {
			userGroupSecretBaseData.setKeyCount(recoveryLimit);
			userGroupSecretBaseData.setLastRecoveryTime(0);
		} else {
			userGroupSecretBaseData.setKeyCount(keyCount + count);
			userGroupSecretBaseData.setLastRecoveryTime(userGroupSecretBaseData.getLastRecoveryTime() + (needTimeMillis * count));
		}

		// 更新并同步数据到客户端
		update(userId);
		synData(player);
	}

	/**
	 * 检查是否可以重置数据
	 * 
	 * @param player
	 * @param now
	 */
	public void checkCanReset(Player player, long now) {
		String userId = player.getUserId();
		UserGroupSecretBaseData userGroupSecretBaseData = get(userId);
		if (userGroupSecretBaseData == null) {
			return;
		}

		if (DateUtils.isResetTime(5, 0, 0, userGroupSecretBaseData.getLastResetTime())) {
			resetGroupSecretData(player, now);
		}
	}

	/**
	 * 重置数据
	 * 
	 * @param player
	 * @param now
	 */
	public void resetGroupSecretData(Player player, long now) {
		String userId = player.getUserId();
		UserGroupSecretBaseData userGroupSecretBaseData = get(userId);
		if (userGroupSecretBaseData == null) {
			return;
		}

		userGroupSecretBaseData.setBuyKeyTimes(0);
		userGroupSecretBaseData.setMatchTimes(0);
		userGroupSecretBaseData.setReceiveKeyCount(0);
		userGroupSecretBaseData.setLastResetTime(now);

		update(userId);
		synData(player);
	}

	private eSynType synType = eSynType.SECRETAREA_USER_INFO;

	/**
	 * 推送个人的帮派秘境数据
	 * 
	 * @param player
	 */
	public void synData(Player player) {
		ClientDataSynMgr.synData(player, get(player.getUserId()), synType, eSynOpType.UPDATE_SINGLE);
	}
}