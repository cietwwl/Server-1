package com.rwbase.dao.power;

import java.util.concurrent.TimeUnit;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.power.pojo.PowerInfo;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;
import com.rwbase.dao.user.readonly.TableUserOtherIF;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/*
 * @author HC
 * @date 2016年4月25日 上午10:54:41
 * @Description 
 */
public class PowerInfoDataHolder {

	private static final eSynType synType = eSynType.POWER_INFO;

	/**
	 * 同步体力信息到前台
	 * 
	 * @param player
	 */
	public static void synPowerInfo(Player player) {
		PowerInfo powerInfo = player.getPowerInfo();

		RoleUpgradeCfg cfg = (RoleUpgradeCfg) RoleUpgradeCfgDAO.getInstance().getCfgById(String.valueOf(player.getLevel()));
		int maxPower = cfg.getMaxPower();
		int recoverTime = powerInfo.getSpeed();// 恢复速度（秒）

		TableUserOtherIF readOnly = player.getUserGameDataMgr().getReadOnly();
		int curPower = readOnly.getPower();
		if (curPower >= maxPower) {
			powerInfo.setnTime(-1);
			powerInfo.settTime(-1);
		} else {
			long now = System.currentTimeMillis();
			long lastAddPowerTime = readOnly.getLastAddPowerTime();

			long leftTime = now - lastAddPowerTime;
			int leftPower = maxPower - curPower;
			int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(leftTime);
			int oneNeedTime = recoverTime - seconds;
			int totalNeedTime = leftPower * recoverTime - seconds;

			powerInfo.setnTime(oneNeedTime);
			powerInfo.settTime(totalNeedTime);
		}

		powerInfo.setBuyCount(readOnly.getBuyPowerTimes());

		ClientDataSynMgr.synData(player, powerInfo, synType, eSynOpType.UPDATE_SINGLE);
	}
}