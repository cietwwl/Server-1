package com.rw.service.pve;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;
import com.playerdata.CopyDataMgr;
import com.playerdata.Player;
import com.playerdata.readonly.CopyDataIF;
import com.playerdata.readonly.CopyInfoCfgIF;
import com.rwbase.dao.angelarray.pojo.db.TableAngelArrayData;
import com.rwbase.dao.battletower.pojo.db.TableBattleTower;
import com.rwbase.dao.copypve.CopyEntryCfgDAO;
import com.rwbase.dao.copypve.CopyType;
import com.rwbase.dao.copypve.pojo.CopyEntryCfg;
import com.rwbase.dao.unendingwar.TableUnendingWar;
import com.rwproto.MsgDef.Command;
import com.rwproto.PrivilegeProtos.PvePrivilegeNames;
import com.rwproto.PveServiceProtos.PveActivity;
import com.rwproto.PveServiceProtos.PveServiceResponse;

public class PveHandler {
	private static PveHandler instance = new PveHandler();

	public static PveHandler getInstance() {
		return instance;
	}

	public ByteString getPveInfo(Player player) {
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
			time = getRemainSeconds(player, unendingWar.getLastChallengeTime(), currentTime, CopyType.COPY_TYPE_WARFARE);
		}

		unendingActivity.setRemainSeconds(time);
		// 无尽战火最多挑战次数现在是客户端写死1次，服务器先写，之后统一弄成配置吧
		// by franky
		int unendingCount = player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.warfareResetCnt);
		unendingCount = unendingCount > 0 ? unendingCount : 1;
		unendingCount = unendingCount - player.unendingWarMgr.getTable().getNum();
		unendingActivity.setRemainTimes(unendingCount > 0 ? unendingCount : 0);
		reponse.addPveActivityList(unendingActivity);
		// TODO 需要按照特权系统统一管理
		reponse.addPveActivityList(fill(CopyType.COPY_TYPE_TRIAL_LQSG, player, currentTime));
		reponse.addPveActivityList(fill(CopyType.COPY_TYPE_TRIAL_JBZD, player, currentTime));
		reponse.addPveActivityList(fill(CopyType.COPY_TYPE_CELESTIAL, player, currentTime));
		// 万仙阵
		PveActivity.Builder tower = PveActivity.newBuilder();
		TableAngelArrayData angleData = player.getTowerMgr().getAngleArrayData();
		int count = 0;

		if (angleData != null) {
			// by franky
			int resetCount = player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.arrayMaxResetCnt);
			count = resetCount - angleData.getResetTimes();
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
			int battleTowerResetTimes = player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.maxResetCount);
			btCount = battleTowerResetTimes - tableBattleTower.getResetTimes();
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
			time = getRemainSeconds(player, unendingWar.getLastChallengeTime(), currentTime, CopyType.COPY_TYPE_WARFARE);
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
		TableAngelArrayData angleData = player.getTowerMgr().getAngleArrayData();

		// by franky
		int resetCount = player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.arrayMaxResetCnt);
		int count = resetCount - angleData.getResetTimes();

		tower.setCopyType(CopyType.COPY_TYPE_TOWER);
		tower.setRemainSeconds(0);
		tower.setRemainTimes(count > 0 ? count : 0);
		reponse.addPveActivityList(tower);

		player.SendMsg(Command.MSG_PVE_INFO, reponse.build().toByteString());
	}

	public PveActivity.Builder fill(int type, Player player, long currentTime) {
		PveActivity.Builder activity = PveActivity.newBuilder();
		CopyDataMgr copyDataMgr = player.getCopyDataMgr();
		List<CopyInfoCfgIF> infoCfgList = copyDataMgr.getTodayInfoCfg(type);

		// int minCount = -1;// 最小次数
		int totalCount = 0;
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
			// if (minCount <= 0 || (copyCount > 0 && copyCount < minCount)) {
			// minCount = copyCount;
			// }
			if (copyCount > 0) {
				totalCount += copyCount;
			}
			int time = getRemainSeconds(player, data.getLastChallengeTime(), currentTime, type);

			if (time > maxTime) {
				maxTime = time;
			}
		}

		if (totalCount <= 0) {
			maxTime = 0;
		}

		activity.setCopyType(type);
		activity.setRemainSeconds(maxTime);
		activity.setRemainTimes(totalCount);
		return activity;
	}

	/**
	 * 获取剩余的时间，这里确保已经是处理过特权减少时间的了
	 * 
	 * @param player
	 * @param lastTime
	 * @param currentTime
	 * @param copyType
	 * @return
	 */
	public int getRemainSeconds(Player player, long lastTime, long currentTime, int copyType) {
		CopyEntryCfg entry = CopyEntryCfgDAO.getInstance().getCfgById(String.valueOf(copyType));
		if (entry == null) {
			return 0;
		}

		if (lastTime <= 0) {
			return 0;
		}

		int seconds = entry.getCdSeconds() - getPveReduceTime(player, copyType);
		long remain = TimeUnit.MILLISECONDS.toSeconds(currentTime - lastTime);
		if (remain < seconds) {
			return (int) (seconds - remain);
		} else {
			return 0;
		}
	}

	/**
	 * 获取减少时间
	 * 
	 * @param player
	 * @param type
	 * @return
	 */
	private int getPveReduceTime(Player player, int type) {
		if (player == null) {
			return 0;
		}

		PvePrivilegeNames names = null;
		if (type == CopyType.COPY_TYPE_TRIAL_JBZD) {// 聚宝之地
			names = PvePrivilegeNames.treasureTimeDec;
		} else if (type == CopyType.COPY_TYPE_CELESTIAL) {// 生存环境
			names = PvePrivilegeNames.survivalTimeDec;
		} else if (type == CopyType.COPY_TYPE_TRIAL_LQSG) {// 炼气山谷
			names = PvePrivilegeNames.expTimeDec;
		}

		if (names == null) {
			return 0;
		}

		return player.getPrivilegeMgr().getIntPrivilege(names);
	}
}