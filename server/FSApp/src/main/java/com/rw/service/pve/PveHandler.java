package com.rw.service.pve;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;
import com.playerdata.CopyDataMgr;
import com.playerdata.Player;
import com.playerdata.readonly.CopyDataIF;
import com.playerdata.readonly.CopyInfoCfgIF;
import com.rwbase.dao.anglearray.pojo.db.TableAngleArrayData;
import com.rwbase.dao.battletower.pojo.db.TableBattleTower;
import com.rwbase.dao.copypve.CopyEntryCfgDAO;
import com.rwbase.dao.copypve.CopyType;
import com.rwbase.dao.copypve.pojo.CopyEntryCfg;
import com.rwbase.dao.unendingwar.TableUnendingWar;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwproto.MsgDef.Command;
import com.rwproto.PveServiceProtos.PveActivity;
import com.rwproto.PveServiceProtos.PveServiceResponse;

public class PveHandler {
	private static PveHandler instance;

	public static PveHandler getInstance() {
		if (instance == null)
			instance = new PveHandler();
		return instance;
	}

	public ByteString getPveInfo(Player player) {
		PrivilegeCfg privilegeCfg = PrivilegeCfgDAO.getInstance().getCfg(player.getVip());
		PveServiceResponse.Builder reponse = PveServiceResponse.newBuilder();
		long currentTime = System.currentTimeMillis();
		PveActivity.Builder unendingActivity = PveActivity.newBuilder();
		TableUnendingWar unendingWar = player.unendingWarMgr.getTable();
		unendingActivity.setCopyType(CopyType.COPY_TYPE_WARFARE);

		// 重置次数不计算时间
		int time;
		if (player.unendingWarMgr.getTable().getResetNum() >= 1) {
			time = 0;
		} else {
			time = getRemainSeconds(unendingWar.getLastChallengeTime(), currentTime, CopyType.COPY_TYPE_WARFARE);
		}

		unendingActivity.setRemainSeconds(time);
		// unendingActivity.setRemainSeconds(getRemainSeconds(unendingWar.getLastChallengeTime(),
		// currentTime, CopyType.COPY_TYPE_WARFARE));
		// TODO 无尽战火最多挑战次数现在是客户端写死1次，服务器先写，之后统一弄成配置吧
		int unendingCount = 1 - player.unendingWarMgr.getTable().getNum();
		unendingActivity.setRemainTimes(unendingCount > 0 ? unendingCount : 0);
		reponse.addPveActivityList(unendingActivity);
		reponse.addPveActivityList(fill(CopyType.COPY_TYPE_TRIAL_LQSG, player, currentTime));
		reponse.addPveActivityList(fill(CopyType.COPY_TYPE_TRIAL_JBZD, player, currentTime));
		reponse.addPveActivityList(fill(CopyType.COPY_TYPE_CELESTIAL, player, currentTime));
		// 万仙阵
		PveActivity.Builder tower = PveActivity.newBuilder();
		TableAngleArrayData angleData = player.getTowerMgr().getAngleArrayData();
		int count = 0;
		if (angleData != null) {
			count = privilegeCfg.getExpeditionCount() - angleData.getResetTimes();
		}
		tower.setCopyType(CopyType.COPY_TYPE_TOWER);
		tower.setRemainSeconds(0);
		tower.setRemainTimes(count > 0 ? count : 0);
		reponse.addPveActivityList(tower);

		// 试练塔
		PveActivity.Builder battleTower = PveActivity.newBuilder();
		TableBattleTower tableBattleTower = player.getBattleTowerMgr().getTableBattleTower();
		int btCount = 0;
		if (tableBattleTower != null) {
			btCount = privilegeCfg.getBattleTowerResetTimes() - tableBattleTower.getResetTimes();
		}
		battleTower.setCopyType(CopyType.COPY_TYPE_BATTLETOWER);
		battleTower.setRemainSeconds(0);
		battleTower.setRemainTimes(btCount > 0 ? btCount : 0);
		reponse.addPveActivityList(battleTower);

		return reponse.build().toByteString();
	}

	public void sendPveInfo(Player player) {
		PveServiceResponse.Builder reponse = PveServiceResponse.newBuilder();
		long currentTime = System.currentTimeMillis();
		PveActivity.Builder unendingActivity = PveActivity.newBuilder();
		TableUnendingWar unendingWar = player.unendingWarMgr.getTable();
		unendingActivity.setCopyType(CopyType.COPY_TYPE_WARFARE);
		// 重置次数不计算时间
		int time;
		if (player.unendingWarMgr.getTable().getResetNum() >= 1) {
			time = 0;
		} else {
			time = getRemainSeconds(unendingWar.getLastChallengeTime(), currentTime, CopyType.COPY_TYPE_WARFARE);
		}

		unendingActivity.setRemainSeconds(time);
		// TODO 无尽战火最多挑战次数现在是客户端写死1次，服务器先写，之后统一弄成配置吧
		int unendingCount = 1 - player.unendingWarMgr.getTable().getNum();
		unendingActivity.setRemainTimes(unendingCount > 0 ? unendingCount : 0);
		reponse.addPveActivityList(unendingActivity);
		reponse.addPveActivityList(fill(CopyType.COPY_TYPE_TRIAL_LQSG, player, currentTime));
		reponse.addPveActivityList(fill(CopyType.COPY_TYPE_TRIAL_JBZD, player, currentTime));
		reponse.addPveActivityList(fill(CopyType.COPY_TYPE_CELESTIAL, player, currentTime));
		PveActivity.Builder tower = PveActivity.newBuilder();
		TableAngleArrayData angleData = player.getTowerMgr().getAngleArrayData();
		PrivilegeCfg privilegeCfg = PrivilegeCfgDAO.getInstance().getCfg(player.getVip());
		int count = privilegeCfg.getExpeditionCount() - angleData.getResetTimes();
		tower.setCopyType(CopyType.COPY_TYPE_TOWER);
		tower.setRemainSeconds(0);
		tower.setRemainTimes(count > 0 ? count : 0);
		reponse.addPveActivityList(tower);

		player.SendMsg(Command.MSG_PVE_INFO, reponse.build().toByteString());
	}

	private PveActivity.Builder fill(int type, Player player, long currentTime) {
		PveActivity.Builder activity = PveActivity.newBuilder();
		CopyDataMgr copyDataMgr = player.getCopyDataMgr();
		List<CopyInfoCfgIF> infoCfgList = copyDataMgr.getTodayInfoCfg(type);

		// int resetCount = copyDataMgr.getRestCountByCopyType(type);

		int minCount = -1;// 最小次数
		int maxTime = 0;// 需要的时间
		for (int i = infoCfgList.size(); --i >= 0;) {
			CopyInfoCfgIF cfg = infoCfgList.get(i);
			if (cfg == null) {
				continue;
			}

			CopyDataIF data = copyDataMgr.getByInfoId(cfg.getId());
			if (data == null) {
				continue;
			}

			int copyType = data.getCopyType();// 类型
			if (copyType != type) {
				continue;
			}

			int copyCount = data.getCopyCount();// 剩余次数

			// 如果还没被赋值，上次数量是0，当前次数<上次次数
			if (minCount <= 0 || (copyCount > 0 && copyCount < minCount)) {
				minCount = copyCount;
			}

			int time = getRemainSeconds(data.getLastChallengeTime(), currentTime, type);

			if (time > maxTime) {
				maxTime = time;
			}
		}

		if (minCount <= 0) {
			maxTime = 0;
		}

		activity.setCopyType(type);
		activity.setRemainSeconds(maxTime);
		activity.setRemainTimes(minCount);
		return activity;
	}

	public int getRemainSeconds(long lastTime, long currentTime, int copyType) {
		CopyEntryCfg entry = (CopyEntryCfg) CopyEntryCfgDAO.getInstance().getCfgById(String.valueOf(copyType));
		if (entry == null) {
			return 0;
		}

		if (lastTime <= 0) {
			return 0;
		}

		int seconds = entry.getCdSeconds();
		long remain = TimeUnit.MILLISECONDS.toSeconds(currentTime - lastTime);
		if (remain < seconds) {
			return (int) (seconds - remain);
		} else {
			return 0;
		}
	}
}