package com.gm.multipletimeshotfix;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.BattleTowerMgr;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.service.battletower.BattleTowerHandler;
import com.rwbase.dao.battletower.pojo.BossInfo;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerFloorCfg;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerRewardCfg;
import com.rwbase.dao.battletower.pojo.cfg.dao.BattleTowerFloorCfgDao;
import com.rwbase.dao.battletower.pojo.cfg.dao.BattleTowerRewardCfgDao;
import com.rwbase.dao.battletower.pojo.db.TableBattleTower;
import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerDao;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.BattleTowerServiceProtos.BattleTowerCommonRspMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeBossStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeStartRspMsg;
import com.rwproto.BattleTowerServiceProtos.ERequestType;
import com.rwproto.BattleTowerServiceProtos.EResponseState;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class BattleTowerHandlerHF extends BattleTowerHandler {

	private static void synHeroInfo(Player player) {
		EmbattlePositionInfo positionInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.TowerBattle_VALUE, "");
		if (positionInfo != null) {
			List<EmbattleHeroPosition> posList = positionInfo.getPos();
			List<String> heroIds = new ArrayList<String>(posList.size());
			for (EmbattleHeroPosition heroPos : posList) {
				heroIds.add(heroPos.getId());
			}
			List<Hero> heros = FSHeroMgr.getInstance().getHeros(player, heroIds);
			for (Hero h : heros) {
				ClientDataSynMgr.synData(player, h.getAttrMgr().getRoleAttrData(), eSynType.ROLE_ATTR_ITEM, eSynOpType.UPDATE_SINGLE, -1);
			}
		} else {
			List<Hero> allHeros = FSHeroMgr.getInstance().getAllHeros(player, null);
			for (Hero h : allHeros) {
				ClientDataSynMgr.synData(player, h.getAttrMgr().getRoleAttrData(), eSynType.ROLE_ATTR_ITEM, eSynOpType.UPDATE_SINGLE, -1);
			}
		}
	}
	
	private static void SetFail(BattleTowerCommonRspMsg.Builder commonRsp, String module, String userId, String reason, String tipMsg) {
		GameLog.error(module, userId, reason);
		commonRsp.setRspState(EResponseState.RSP_FAIL);
		if (!StringUtils.isBlank(tipMsg))
			commonRsp.setTips(tipMsg);
	}
	
	/**
	 * 挑战某层开始
	 * 
	 * @param player
	 * @param req
	 * @param commonRsp
	 */
	public static void battleTowerChallengeStart(Player player, ChallengeStartReqMsg req, BattleTowerCommonRspMsg.Builder commonRsp) {
		String userId = player.getUserId();
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			SetFail(commonRsp, "试练塔模块-战斗开始", userId, "没有找到角色对应的试练塔数据信息", "数据异常");
			return;
		}

		// 检测当前层是否是被中断的
		boolean isBreak = tableBattleTower.isBreak();// 是否被中断
		if (isBreak) {
			SetFail(commonRsp, "试练塔模块-战斗开始", userId, "当前某层挑战失败，不能继续进行挑战", "挑战中断");
			return;
		}

		final int floor = req.getFloor();// 要挑战的层数
		int curFloor = tableBattleTower.getCurFloor();// 当前层
		if (!tableBattleTower.getResult() && curFloor > 0) {// 还没有任何结果
			curFloor -= 1;
			tableBattleTower.setCurFloor(curFloor);
			tableBattleTower.setResult(false);
			// 更新数据
			dao.update(tableBattleTower);
		}

		curFloor = curFloor == 0 ? 1 : curFloor;

		BattleTowerFloorCfgDao cfgDao = BattleTowerFloorCfgDao.getCfgDao();
		BattleTowerFloorCfg floorCfg = cfgDao.getCfgById(String.valueOf(curFloor));
		if (floorCfg == null) {
			SetFail(commonRsp, "试练塔模块-战斗开始", userId, "没有找到对应的" + curFloor + "层的配置表", "数据异常");
			return;
		}

		int groupId = floorCfg.getGroupId();
		List<Integer> floorList = cfgDao.getContainFloorList(String.valueOf(groupId));
		if (floorList.isEmpty()) {
			SetFail(commonRsp, "试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，包含的层数列表是空的", curFloor, groupId), "数据异常");
			return;
		}

		// 检查一下是不是最后一层
		int lastFloor = floorList.get(floorList.size() - 1);
		if (curFloor == lastFloor) {
			curFloor++;// 下一层加一
			floorCfg = cfgDao.getCfgById(String.valueOf(curFloor));
			if (floorCfg == null) {
				SetFail(commonRsp, "试练塔模块-战斗开始", userId, "挑战的新层，没有找到对应的" + curFloor + "层的配置表", "数据异常");
				return;
			}

			groupId = floorCfg.getGroupId();
			floorList = cfgDao.getContainFloorList(String.valueOf(groupId));
			if (floorList.isEmpty()) {
				SetFail(commonRsp, "试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，包含的层数列表是空的", curFloor, groupId), "数据异常");
				return;
			}
		}

		if (floor != curFloor) {
			SetFail(commonRsp, "试练塔模块-战斗开始", userId, String.format("请求层和应该打的层数不一致，请求%s层,应该打第%s层", floor, curFloor), "数据异常");
			return;
		}

		if (floor != floorList.get(0)) {
			SetFail(commonRsp, "试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，请求打的不是该组第一层数据", curFloor, floorCfg.getGroupId()), "请求数据异常");
			return;
		}

		// TODO 验证客户端的copyID
		if (req.hasCopyId()) {// 为兼容旧的客户端，没有设置为required字段
			int clientCopyId = req.getCopyId();
			BattleTowerRewardCfg rewardCfg = BattleTowerRewardCfgDao.getCfgDao().getCfgById(String.valueOf(groupId));
			if (rewardCfg != null && rewardCfg.getCopyId() != clientCopyId) {
				ChallengeStartRspMsg.Builder rsp = ChallengeStartRspMsg.newBuilder();
				rsp.setCopyId(rewardCfg.getCopyId());
				commonRsp.setRspBody(rsp.build().toByteString());
				SetFail(commonRsp, "试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，请求打的不是该组的copyID，客户端的是:%s,服务器计算的是:%s", curFloor, floorCfg.getGroupId(), clientCopyId, rewardCfg.getCopyId()), "请求数据异常，请重试");
				return;
			}
		}

		tableBattleTower.setCurFloor(floor);// 设置当前要打的层
		tableBattleTower.setResult(false);// 当前还没有结果
		// 更新数据
		dao.update(tableBattleTower);

		commonRsp.setReqType(ERequestType.CHALLENGE_START);
		commonRsp.setRspState(EResponseState.RSP_SUCESS);
		synHeroInfo(player);
	}
	
	/**
	 * 挑战Boss开始
	 * 
	 * @param player
	 * @param req
	 * @param commonRsp
	 */
	public static void challengeBossStart(Player player, ChallengeBossStartReqMsg req, BattleTowerCommonRspMsg.Builder commonRsp) {
		String userId = player.getUserId();
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			SetFail(commonRsp, "挑战Boss开始", userId, "获取个人的试练塔数据为null", "数据异常");
			return;
		}

		int bossId = req.getBossId();
		BossInfo bossInfo = tableBattleTower.getBoss(bossId);
		if (bossInfo == null) {
			SetFail(commonRsp, "挑战Boss开始", userId, String.format("挑战Boss的Id为%s，没有找到对应数据", bossId), "Boss不存在");
			return;
		}

		tableBattleTower.setChallengeBossId(bossId);
		dao.update(tableBattleTower);

		commonRsp.setRspState(EResponseState.RSP_SUCESS);
		
		synHeroInfo(player);
	}
}
