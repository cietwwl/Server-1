package com.rw.service.PeakArena;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.bm.arena.ArenaConstant;
import com.common.RefBool;
import com.common.RefParam;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.UserGameDataMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.embattle.EmbattlePositonHelper;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.exception.ReplaceTargetNotExistException;
import com.rw.fsutil.ranking.exception.ReplacerAlreadyExistException;
import com.rw.service.PeakArena.datamodel.PeakArenaExtAttribute;
import com.rw.service.PeakArena.datamodel.PeakRecordInfo;
import com.rw.service.PeakArena.datamodel.TablePeakArenaData;
import com.rw.service.PeakArena.datamodel.TablePeakArenaDataDAO;
import com.rw.service.PeakArena.datamodel.TeamData;
import com.rw.service.PeakArena.datamodel.peakArenaBuyCost;
import com.rw.service.PeakArena.datamodel.peakArenaBuyCostHelper;
import com.rw.service.PeakArena.datamodel.peakArenaInfo;
import com.rw.service.PeakArena.datamodel.peakArenaInfoHelper;
import com.rw.service.PeakArena.datamodel.peakArenaPrizeHelper;
import com.rw.service.PeakArena.datamodel.peakArenaResetCost;
import com.rw.service.PeakArena.datamodel.peakArenaResetCostHelper;
import com.rw.service.Privilege.IPrivilegeManager;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.TableSkill;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.MsgDef;
import com.rwproto.ArenaServiceProtos.ArenaEmbattleType;
import com.rwproto.BattleCommon.BattleHeroPosition;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.MsgDef.Command;
import com.rwproto.PeakArenaServiceProtos.ArenaData;
import com.rwproto.PeakArenaServiceProtos.ArenaInfo;
import com.rwproto.PeakArenaServiceProtos.ArenaRecord;
import com.rwproto.PeakArenaServiceProtos.HeroData;
import com.rwproto.PeakArenaServiceProtos.MsgArenaRequest;
import com.rwproto.PeakArenaServiceProtos.MsgArenaResponse;
import com.rwproto.PeakArenaServiceProtos.MsgArenaResponse.Builder;
import com.rwproto.PeakArenaServiceProtos.TeamInfo;
import com.rwproto.PeakArenaServiceProtos.eArenaResultType;
import com.rwproto.PeakArenaServiceProtos.eArenaType;
import com.rwproto.PrivilegeProtos.PeakArenaPrivilegeNames;
import com.rwproto.SkillServiceProtos.TagSkillData;

public class PeakArenaHandler {

	private static PeakArenaHandler instance;

	private PeakArenaHandler() {
	}

	public static PeakArenaHandler getInstance() {
		if (instance == null) {
			instance = new PeakArenaHandler();
		}
		return instance;
	}

	public ByteString getPlaceByteString(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		int place = PeakArenaBM.getInstance().getPlace(player);
		if (place < 0) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		} else {
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			response.setPlace(place);
		}
		return response.build().toByteString();
	}

	public ByteString getPeakArenaData(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		PeakArenaBM peakBM = PeakArenaBM.getInstance();
		TablePeakArenaData arenaData = peakBM.getOrAddPeakArenaData(player);
		if (arenaData == null) {
			GameLog.error("巅峰竞技场", player.getUserId(), "找不到玩家竞技场数据");
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "数据错误");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		// 触发领奖
		peakBM.addPeakArenaCoin(player, arenaData, peakBM.getPlace(player), System.currentTimeMillis());
		response.setArenaData(getPeakArenaData(arenaData, player));
		setOtherInfo(response, player, arenaData);
		setSuccess(response, arenaData);
		return response.build().toByteString();
	}

	private void setOtherInfo(Builder response, Player player, TablePeakArenaData arenaData) {
		int playerPlace = PeakArenaBM.getInstance().getPlace(player);
		int gainPerHour = peakArenaPrizeHelper.getInstance().getBestMatchPrizeCount(playerPlace);
		response.setGainCurrencyPerHour(gainPerHour);
		response.setChallengeCount(arenaData.getChallengeCount());
		response.setCdTime(computeCdTime(arenaData));
	}

	private int computeCdTime(TablePeakArenaData arenaData) {
		long fightStartTime = arenaData.getFightStartTime();
		if (fightStartTime <= 0) {
			return 0;
		}
		// 特殊规则：如果剩余挑战次数为0，则返回给客户端的CD时间为0
		peakArenaInfo cfg = peakArenaInfoHelper.getInstance().getUniqueCfg();
		int challengeCount = arenaData.getChallengeCount();
		if (challengeCount >= cfg.getCount() + arenaData.getBuyCount()) {
			return 0;
		}
		long nextFightTime = fightStartTime + cfg.getCdTimeInMillSecond();
		int seconds = 0;
		long currentTime = System.currentTimeMillis();
		if (nextFightTime > 0 && nextFightTime > currentTime) {
			seconds = (int) TimeUnit.MILLISECONDS.toSeconds(nextFightTime - currentTime);
		}
		return seconds;
	}

	public void setSuccess(MsgArenaResponse.Builder response, TablePeakArenaData arenaData) {
		response.setMaxChallengeCount(peakArenaInfoHelper.getInstance().getUniqueCfg().getCount());
		int buyCount = arenaData.getBuyCount();
		response.setBuyCount(buyCount);
		peakArenaBuyCost cfg = peakArenaBuyCostHelper.getInstance().getCfgByCount(buyCount + 1);
		if (cfg != null) {
			response.setCurrentBuyCost(cfg.getCost());
		}

		peakArenaResetCost resetCostCfg = peakArenaResetCostHelper.getInstance().getCfgByResetCount(arenaData.getResetCount() + 1);
		if (resetCostCfg != null) {
			response.setResetCost(resetCostCfg.getCost());
		}
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
	}

	public ByteString clearCD(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		TablePeakArenaData arenData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (arenData == null) {
			// 这种属于异常情况
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}

		// 重置费用改为从peakArenaCost读取，需要保存重置次数
		// 是否可重置由特权配置决定
		IPrivilegeManager pri = player.getPrivilegeMgr();
		boolean isOpen = pri.getBoolPrivilege(PeakArenaPrivilegeNames.isAllowResetPeak);
		if (!isOpen) {
			// player.NotifyCommonMsg(CommonTip.VIP_NOT_ENOUGH);//是否需要通知客户端？
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		int nextCount = arenData.getResetCount() + 1;

		peakArenaResetCost cfg = peakArenaResetCostHelper.getInstance().getCfgByResetCount(nextCount);
		UserGameDataMgr userMgr = player.getUserGameDataMgr();
		if (!userMgr.isEnoughCurrency(cfg.getCoinType(), cfg.getCost())) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "钻石不足");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		// 扣费，记录重置次数，重置开始时间
		if (!userMgr.deductCurrency(cfg.getCoinType(), cfg.getCost())) {
			return SetError(response, player, "钻石不足", "扣钻石失败:" + cfg.getCost());
		}
		arenData.setResetCount(nextCount);
		arenData.setFightStartTime(0);
		TablePeakArenaDataDAO.getInstance().update(arenData);

		// response.setArenaData(getPeakArenaData(arenData, player));
		response.setCdTime(0);
		cfg = peakArenaResetCostHelper.getInstance().getCfgByResetCount(nextCount + 1);
		if (cfg != null) {
			response.setResetCost(cfg.getCost());
		}
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString selectEnemys(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		TablePeakArenaData m_MyArenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (m_MyArenaData == null) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		List<ListRankingEntry<String, PeakArenaExtAttribute>> listInfo = PeakArenaBM.getInstance().SelectPeakArenaInfos(m_MyArenaData, player);
		for (ListRankingEntry<String, PeakArenaExtAttribute> entry : listInfo) {
			ArenaInfo.Builder info = ArenaInfo.newBuilder();
			String key = entry.getKey();
			Player enemy = PlayerMgr.getInstance().find(key);
			TablePeakArenaData otherArenaData = PeakArenaBM.getInstance().getPeakArenaData(key);
			info.setUserId(key);
			info.setWinCount(otherArenaData.getWinCount());
			info.setFighting(enemy.getMainRoleHero().getFighting());
			info.setHeadImage(enemy.getHeadImage());
			info.setLevel(enemy.getLevel());
			info.setName(enemy.getUserName());
			info.setHeadFrame(enemy.getHeadFrame());
			info.setPlace(entry.getRanking());

			Player enymyPlayer = PlayerMgr.getInstance().find(key);
			info.setStarLevel(enymyPlayer.getStarLevel());

			info.setCareer(enymyPlayer.getCareer());
			info.setQualityId(enymyPlayer.getMainRoleHero().getQualityId());

			response.addListInfo(info.build());
		}

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString getEnemyInfo(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		String enemyId = request.getUserId();
		Player enemy = PlayerMgr.getInstance().find(enemyId);
		if (enemy == null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "找不到对手");
			return SetError(response, player, "找不到对手", "enemyId=" + enemyId);
		}

		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getPeakArenaData(enemyId);
		if (arenaData == null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "找不到对手的巅峰竞技场信息");
			return SetError(response, player, "找不到对手的巅峰竞技场信息", "enemyId=" + enemyId);
		}

		response.setArenaData(getPeakArenaData(arenaData, enemy));
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString getRecords(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		List<PeakRecordInfo> listRecord = PeakArenaBM.getInstance().getArenaRecordList(player.getUserId());
		for (PeakRecordInfo record : listRecord) {
			response.addListRecord(getPeakArenaRecord(record));
		}

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString switchTeam(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		if (request.getReorderCount() < 3) {
			return SetError(response, player, "参数错误，不足三支队伍", ",count=" + request.getReorderCount());
		}

		if (PeakArenaBM.getInstance().switchTeam(player, request.getReorderList())) {
			TablePeakArenaData m_MyArenaData = PeakArenaBM.getInstance().getPeakArenaData(player.getUserId());
			response.setArenaData(getPeakArenaData(m_MyArenaData, player));
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		} else {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		}
		return response.build().toByteString();
	}

	public ByteString changeHeros(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		PeakArenaBM peakBM = PeakArenaBM.getInstance();
		TablePeakArenaData peakData = peakBM.getOrAddPeakArenaData(player);
		List<TeamInfo> teamInfoList = request.getTeamsList();
		String playerId = player.getUserId();
		HeroMgr heroMgr = player.getHeroMgr();
		RefParam<List<String>> checkedHeroIDList = new RefParam<List<String>>();
		for (TeamInfo teamInfo : teamInfoList) {
			TeamData team = peakData.search(teamInfo.getTeamId());
			List<String> heroIdsList = teamInfo.getHeroIdsList();
			List<TableSkill> heroSkillList = peakBM.getHeroInfoList(heroIdsList, heroMgr, playerId, checkedHeroIDList);
			team.setMagicId(teamInfo.getMagicId());
			team.setHeros(checkedHeroIDList.value);
			team.setHeroSkills(heroSkillList);
			String teamKey = ""+teamInfo.getTeamId();
			List<BattleHeroPosition> heroPosList = teamInfo.getHeroPositionsList();
			//TODO 保存站位信息
			EmbattleInfoMgr.getMgr().updateOrAddEmbattleInfo(player, 
					eBattlePositionType.PeakArenaPos_VALUE,teamKey,
				EmbattlePositonHelper.parseMsgHeroPos2Memery(heroPosList));
		}
		TablePeakArenaDataDAO.getInstance().update(peakData);
		response.setArenaData(getPeakArenaData(peakData, player));

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	// 准备挑战
	public ByteString initFightInfo(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		peakArenaInfo cfg = peakArenaInfoHelper.getInstance().getUniqueCfg();
		if (computeCdTime(arenaData) > 0) {
			return sendFailRespon(player, response, ArenaConstant.COOL_DOWN);
		}

		// 检查挑战次数：最大次数由配置的固定值+特权附加的购买次数!
		int challengeCount = arenaData.getChallengeCount();
		if (challengeCount >= cfg.getCount() + arenaData.getBuyCount()) {
			return sendFailRespon(player, response, ArenaConstant.TIMES_NOT_ENOUGH);
		}

		TablePeakArenaData enemyArenaData = PeakArenaBM.getInstance().getPeakArenaData(request.getUserId());
		if (enemyArenaData == null) {
			return sendFailRespon(player, response, ArenaConstant.ENEMY_NOT_EXIST);
		}
		String enemyId = request.getUserId();

		int enemyPlace = PeakArenaBM.getInstance().getEnemyPlace(enemyId);
		if (enemyPlace <= 0) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, ArenaConstant.ENEMY_PLACE_CHANGED);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		response.setArenaData(getPeakArenaData(enemyArenaData, enemyPlace));
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	// 第一场战斗开始的时候发送
	public ByteString fightStart(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (arenaData == null) {
			return sendFailRespon(player, response, ArenaConstant.UNKOWN_EXCEPTION);
		}
		
		String enemyId = request.getUserId();
		ListRankingEntry<String, PeakArenaExtAttribute> enemyEntry = PeakArenaBM.getInstance().getEnemyEntry(enemyId);
		if (enemyEntry == null) {
			return sendFailRespon(player, response, ArenaConstant.ENEMY_NOT_EXIST);
		}
		ListRankingEntry<String, PeakArenaExtAttribute> entry = PeakArenaBM.getInstance().getPlayerRankEntry(player, arenaData);
		// TODO 这次不管 超出排行榜容量的容错处理，让他打，赢了重新尝试加入排行榜
		if (entry == null) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}

		// combined transaction
		if (!enemyEntry.getExtension().setFighting()) {
			return sendFailRespon(player, response, ArenaConstant.ENEMY_IS_FIGHTING);
		}

		// TODO 同宇超商量不对挑战者加锁
		entry.getExtension().forceSetFighting();

		arenaData.setLastFightEnemy(enemyId);
		final long currentTimeMillis = System.currentTimeMillis();
		arenaData.setFightStartTime(currentTimeMillis);

		int challengeCount = arenaData.getChallengeCount();
		TablePeakArenaDataDAO.getInstance().update(arenaData);
		response.setChallengeCount(challengeCount);

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString fightContinue(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (arenaData == null) {
			return sendFailRespon(player, response, ArenaConstant.UNKOWN_EXCEPTION);
		}

		String enemyId = request.getUserId();
		ListRankingEntry<String, PeakArenaExtAttribute> enemyEntry = PeakArenaBM.getInstance().getEnemyEntry(enemyId);
		if (enemyEntry == null) {
			return sendFailRespon(player, response, ArenaConstant.ENEMY_NOT_EXIST);
		}
		ListRankingEntry<String, PeakArenaExtAttribute> playerEntry = PeakArenaBM.getInstance().getPlayerRankEntry(player, arenaData);
		// TODO 这次不管 超出排行榜容量的容错处理，让他打，赢了重新尝试加入排行榜
		if (playerEntry == null) {
			GameLog.error("巅峰竞技场", player.getUserId(), "玩家未入榜");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}

		String lastEnemy = arenaData.getLastFightEnemy();
		if (enemyId.equals(lastEnemy)) {
			// 延长超时时间
			enemyEntry.getExtension().extendTimeOut();
			final long currentTimeMillis = System.currentTimeMillis();
			arenaData.setFightStartTime(currentTimeMillis);
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		} else {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		}

		return response.build().toByteString();
	}

	// 最后一场战斗结束才接收这个消息
	public ByteString fightFinish(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		boolean win = request.getWin();
		String enemyUserId = request.getUserId();
		String userId = player.getUserId();

		// 从db加载数据的容错处理
		PeakArenaBM peakBM = PeakArenaBM.getInstance();
		TablePeakArenaData playerArenaData = peakBM.getPeakArenaData(userId);
		if (playerArenaData == null) {
			return SetError(response, player, "结算时找不到用户", "");
		}

		// 重置对手
		playerArenaData.setLastFightEnemy("");
		// 结束时再减少挑战次数
		playerArenaData.setChallengeCount(playerArenaData.getChallengeCount() + 1);

		// 巅峰排行榜超出上限的容错处理
		ListRankingEntry<String, PeakArenaExtAttribute> playerEntry = peakBM.getPlayerRankEntry(player, playerArenaData);
		if (playerEntry == null) {
			// TODO 这次不做 如果赢了在加上最低分做重新加入排行榜的尝试
			return SetError(response, player, "结算时找不到排行", "");
		}

		TablePeakArenaData enemyArenaData = peakBM.getPeakArenaData(enemyUserId);
		if (enemyArenaData == null) {
			// TODO 同宇超商量不对挑战者加锁
			playerEntry.getExtension().setNotFighting();
			return SetError(response, player, "结算时找不到对手", ":" + enemyUserId);
		}

		ListRankingEntry<String, PeakArenaExtAttribute> enemyEntry = peakBM.getEnemyEntry(enemyUserId);
		if (enemyEntry == null) {
			return SetError(response, player, "结算时找不到对手排行榜信息", ":" + enemyUserId);
		}

		Player enemyUser = PlayerMgr.getInstance().find(enemyUserId);

		try {
			final long currentTimeMillis = System.currentTimeMillis();
			if (win) {
				playerArenaData.setWinCount(playerArenaData.getWinCount() + 1);
			} else {
				enemyArenaData.setWinCount(enemyArenaData.getWinCount() + 1);
			}

			RefBool hasSwap = new RefBool();
			RefParam<String> errorTip = new RefParam<String>();
			final int enemyPlace = enemyEntry.getRanking();
			final int playerPlace = playerEntry.getRanking();
			if (win && !swapPlace(player, playerEntry, userId, enemyEntry, enemyUserId, hasSwap, errorTip)) {
				return sendFailRespon(player, response, errorTip.value);
			}
			if (win && hasSwap.value) {
				// 设置最高历史排名
				int newRank = playerEntry.getRanking();
				if (playerArenaData.getMaxPlace() < newRank) {
					playerArenaData.setMaxPlace(newRank);
				}
				// 如果交换了位置则需要按照旧的排名计算奖励
				peakBM.addPeakArenaCoin(player, playerArenaData, playerPlace, currentTimeMillis);
				// 通知对手需要强制兑换奖励
				if (!enemyUser.isRobot()) {
					GameWorldFactory.getGameWorld().asyncExecute(enemyUserId, new PlayerTask() {
						@Override
						public void run(Player enemy) {
							// 对手需要强制兑换奖励
							int tmp = enemyPlace;
							long replaceTime = currentTimeMillis;
							PeakArenaBM peakBmHelper = PeakArenaBM.getInstance();
							String enemyUserId = enemy.getUserId();
							TablePeakArenaData enemyArenaData = peakBmHelper.getPeakArenaData(enemyUserId);
							peakBmHelper.addPeakArenaCoin(enemy, enemyArenaData, tmp, replaceTime);
						}
					});
				}
			}

			// 排名上升
			PeakRecordInfo recordForPlayer = new PeakRecordInfo();
			recordForPlayer.setUserId(enemyUserId);
			recordForPlayer.setWin(win ? 1 : 0);
			recordForPlayer.setName(enemyUser.getUserName());
			recordForPlayer.setHeadImage(enemyUser.getHeadImage());
			recordForPlayer.setLevel(enemyUser.getLevel());
			recordForPlayer.setTime(currentTimeMillis);
			recordForPlayer.setChallenge(1);

			int placeUp = playerPlace - enemyPlace;
			if (win && placeUp > 0) {
				recordForPlayer.setPlaceUp(placeUp);
				response.setPlaceUp(placeUp);
				response.setPlace(enemyPlace);// 排名对调了
			}
			peakBM.addOthersRecord(playerArenaData, recordForPlayer);

			PeakRecordInfo recordForEnemy = new PeakRecordInfo();
			recordForEnemy.setUserId(userId);
			recordForEnemy.setWin(win ? 0 : 1);// 取反
			recordForEnemy.setName(player.getUserName());
			recordForEnemy.setHeadImage(player.getHeadImage());
			recordForEnemy.setLevel(player.getLevel());
			recordForEnemy.setTime(currentTimeMillis);
			recordForEnemy.setChallenge(0);
			peakBM.addOthersRecord(enemyArenaData, recordForEnemy);

			playerArenaData.setFightStartTime(currentTimeMillis);

			// 向双方发送战报
			MsgArenaResponse.Builder recordResponse = MsgArenaResponse.newBuilder();
			recordResponse.setArenaType(eArenaType.SYNC_RECORD);
			ArenaRecord ar = getPeakArenaRecord(recordForPlayer);
			recordResponse.addListRecord(ar);
			recordResponse.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			player.SendMsg(Command.MSG_PEAK_ARENA, recordResponse.build().toByteString());
			if (win) {
				if (enemyUser != null) {
					enemyUser.getTempAttribute().setRecordChanged(true);
				}
			}
			
			//通知角色日常任务 by Alex
			player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.PEAKARENA_BATTLE, 1);

			response.setCdTime(computeCdTime(playerArenaData));
			response.setChallengeCount(playerArenaData.getChallengeCount());
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			
			pushMainViewData(player);
			
			return response.build().toByteString();
		} finally {
			// TODO 同宇超商量不对挑战者加锁
			playerEntry.getExtension().setNotFighting();

			if (enemyEntry != null) {
				enemyEntry.getExtension().setNotFighting();
			}
		}
	}

	private void pushMainViewData(Player player) {
		MsgArenaRequest.Builder req = MsgArenaRequest.newBuilder();
		req.setArenaType(eArenaType.GET_DATA);
		ByteString data = getPeakArenaData(req.build(),player);
		player.SendMsg(MsgDef.Command.MSG_PEAK_ARENA, data);
	}

	/**
	 * 判断是否需要交换位置 返回是否操作成功
	 * 
	 * @param win
	 * @param playerEntry
	 * @param playerId
	 * @param enemyEntry
	 * @param enemyId
	 * @param hasSwap
	 * @return
	 */
	private boolean swapPlace(Player player, ListRankingEntry<String, PeakArenaExtAttribute> playerEntry, String playerId, ListRankingEntry<String, PeakArenaExtAttribute> enemyEntry, String enemyId,
			RefBool hasSwap, RefParam<String> errorTip) {
		PeakArenaBM peakBM = PeakArenaBM.getInstance();
		ListRanking<String, PeakArenaExtAttribute> ranking = peakBM.getRanks();
		if (playerEntry == null) {
			return replaceRank(player, playerId, ranking, enemyId, hasSwap, errorTip);
		}
		if (playerEntry.getRanking() > enemyEntry.getRanking() && !ranking.swap(playerId, enemyId)) {
			if (ranking.contains(playerId)) {
				// 玩家排名比对手低，交换排名失败，排行榜依然包含玩家，报告错误
				errorTip.value = ArenaConstant.ENEMY_PLACE_CHANGED;
				return false;
			}
			// 交换失败，但其实玩家还没有入榜，则重新加入排行榜，并替换对手的位置
			return replaceRank(player, playerId, ranking, enemyId, hasSwap, errorTip);
		}
		hasSwap.value = playerEntry.getRanking() < enemyEntry.getRanking();
		return true;
	}

	// 创建玩家信息并从未入榜替换未对手的位置
	private boolean replaceRank(Player player, String playerId, ListRanking<String, PeakArenaExtAttribute> ranking, String enemyId, RefBool hasSwap, RefParam<String> errorTip) {
		PeakArenaBM peakBM = PeakArenaBM.getInstance();

		PeakArenaExtAttribute ext = peakBM.createExtData(player);
		try {
			ranking.replace(playerId, ext, enemyId);
		} catch (ReplacerAlreadyExistException e) {
			GameLog.error("巅峰竞技场", playerId, "严重错误@巅峰竞技场#replace失败,对手Id:" + playerId, e);
			errorTip.value = ArenaConstant.UNKOWN_EXCEPTION;
			return false;
		} catch (ReplaceTargetNotExistException e) {
			errorTip.value = ArenaConstant.ENEMY_PLACE_CHANGED;
			return false;
		}
		hasSwap.value = true;
		return true;
	}

	private ByteString SetError(MsgArenaResponse.Builder response, Player player, String userTip, String logError) {
		GameLog.info("巅峰竞技场", player.getUserId(), logError + userTip);
		response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		if (StringUtils.isNotBlank(userTip))
			response.setResultTip(userTip);
		return response.build().toByteString();
	}

	public HeroData getHeroData(ArmyHero tableHeroData, int teamId) {
		HeroData.Builder result = HeroData.newBuilder();
		RoleBaseInfo baseInfo = tableHeroData.getRoleBaseInfo();
		result.setHeroId(baseInfo.getId());
		result.setTempleteId(baseInfo.getTemplateId());
		result.setLevel(baseInfo.getLevel());
		result.setStarLevel(baseInfo.getStarLevel());
		result.setFighting(tableHeroData.getFighting());
		result.setQualityId(baseInfo.getQualityId());
		result.setExp(baseInfo.getExp());
		result.setTeamId(teamId);

		for (Skill skill : tableHeroData.getSkillList()) {
			result.addSkills(transfrom(skill));
		}
		return result.build();
	}

	public HeroData getHeroData(Player player) {
		HeroData.Builder result = HeroData.newBuilder();
		result.setExp(player.getExp());

		Hero baseInfo = player.getMainRoleHero();
		result.setHeroId(baseInfo.getUUId());
		result.setTempleteId(baseInfo.getTemplateId());
		result.setLevel(baseInfo.getLevel());
		result.setStarLevel(baseInfo.getStarLevel());
		result.setFighting(baseInfo.getFighting());
		result.setQualityId(baseInfo.getQualityId());

		List<Skill> lst = baseInfo.getSkillMgr().getSkillList();
		for (Skill skill : lst) {
			result.addSkills(transfrom(skill));
		}
		return result.build();
	}

	private TagSkillData transfrom(Skill skill) {
		TagSkillData.Builder builder = TagSkillData.newBuilder();
		builder.setId(skill.getId());
		builder.setOwnerId(skill.getOwnerId());
		builder.setSkillId(skill.getSkillId());
		builder.addAllBuffId(skill.getBuffId());
		builder.setOrder(skill.getOrder());
		builder.setSkillRate(skill.getSkillRate());
		builder.setExtraDamage(skill.getExtraDamage());
		return builder.build();
	}

	public ArenaData getPeakArenaData(TablePeakArenaData arenaData, Player player) {
		return getPeakArenaData(arenaData, PeakArenaBM.getInstance().getPlace(player));
	}

	public ArenaData getPeakArenaData(TablePeakArenaData arenaData, int place) {
		PeakArenaBM peakArenaBM = PeakArenaBM.getInstance();
		String userId = arenaData.getUserId();
		Player player = PlayerMgr.getInstance().find(userId);
		ArenaData.Builder data = ArenaData.newBuilder();
		data.setUserId(userId);
		int gainPerHour = peakArenaPrizeHelper.getInstance().getBestMatchPrizeCount(place);
		peakArenaBM.gainExpectCurrency(arenaData, gainPerHour);
		data.setPlace(place);
		data.setMaxPlace(arenaData.getMaxPlace());
		data.setWinCount(arenaData.getWinCount());

		data.setCareer(player.getCareer());
		data.setHeadImage(player.getHeadImage());
		data.setLevel(player.getLevel());
		data.setFighting(player.getMainRoleHero().getFighting());
		data.setName(player.getUserName());
		data.setHeadFrame(player.getHeadFrame());
		String groupName = GroupHelper.getGroupName(userId);
		if (StringUtils.isNotBlank(groupName))
			data.setGroupName(groupName);

		PlayerIF role = PlayerMgr.getInstance().getReadOnlyPlayer(arenaData.getUserId());

		List<TagSkillData> skills = role.getSkillMgr().getSkillProtoList();
		for (TagSkillData skill : skills) {
			data.addRoleSkill(skill);
		}

		data.setTempleteId(player.getTemplateId());

		HeroData teamMainRole = getHeroData(player);
		ItemData magic = player.getMagicMgr().getMagic();
		ItemBagMgr bagMgr = player.getItemBagMgr();

		for (int i = 0; i < arenaData.getTeamCount(); i++) {
			TeamInfo.Builder teamBuilder = TeamInfo.newBuilder();
			TeamData team = arenaData.getTeam(i);
			teamBuilder.setTeamId(team.getTeamId());
			String magicId = team.getMagicId();
			ItemData selectedMagic = null;
			// 如果背包找不到法宝，可能是法宝被分解了！这时自动切换为玩家的法宝
			selectedMagic = bagMgr.findBySlotId(magicId);
			if (selectedMagic == null) {
				selectedMagic = magic;
			}

			if (selectedMagic != null) {
				teamBuilder.setMagicId(selectedMagic.getId());
				teamBuilder.setMagicLevel(selectedMagic.getMagicLevel());
				teamBuilder.setEnemyMagicModelId(selectedMagic.getModelId());
			} else {
				GameLog.error("巅峰竞技场", userId, "找不到法宝,ID=" + magicId);
			}

			//TODO 获取站位信息
			EmbattlePositionInfo heroPositionInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(userId, 
					eBattlePositionType.PeakArenaPos_VALUE, ""+team.getTeamId());
			if (heroPositionInfo != null){
				List<EmbattleHeroPosition> lst = heroPositionInfo.getPos();
				if (lst.size() > 0){
					for (EmbattleHeroPosition embattleHeroPosition : lst) {
						BattleHeroPosition.Builder hposProto = BattleHeroPosition.newBuilder();
						hposProto.setHeroId(embattleHeroPosition.getId());
						hposProto.setPos(embattleHeroPosition.getPos());
						teamBuilder.addHeroPositions(hposProto);
					}
				}
			}
			
			List<String> heroIdList = team.getHeros();
			if (heroIdList != null)
				heroIdList.remove(arenaData.getUserId());
			ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(arenaData.getUserId(), heroIdList);
			List<ArmyHero> armyList = armyInfo.getHeroList();
			int[] usedPos = new int[heroIdList.size()];
			int posIndex = 0;
			for (ArmyHero hero : armyList) {
				int position = 0;
				if (heroPositionInfo != null){
					//TODO 写入站位，可能找不到，这时返回0，需要兼容旧数据
					position = heroPositionInfo.getHeroPos(hero.getRoleBaseInfo().getId());
				}
				
				if (position == 0){
					position = getNextPosition(usedPos);
				}
				usedPos[posIndex] = position;
				posIndex++;
				hero.setPosition(position);
				teamBuilder.addHeros(getHeroData(hero,i));
			}
			teamBuilder.setPlayer(teamMainRole);
			teamBuilder.addAllHeroIds(heroIdList);
			try {
				teamBuilder.setArmyInfo(armyInfo.toJson());
			} catch (Exception e) {
				GameLog.error("巅峰竞技场", userId, "无法获取队伍信息JSON", e);
			}
			data.addTeams(teamBuilder);
		}
		return data.build();
	}
	
	public int getNextPosition(int[] usedPosition){
		int maxPosValue = 0;
		for (int tmpIndex = 0; tmpIndex < usedPosition.length;++tmpIndex){
			if (usedPosition[tmpIndex] > maxPosValue){
				maxPosValue = usedPosition[tmpIndex];
			}
		}
		int newPos;
		for (newPos = 1; newPos<=maxPosValue;++newPos){
			boolean isContained = false;
			for(int j = 0; j<usedPosition.length;++j){
				if (newPos == usedPosition[j]){
					isContained = true;
					break;
				}
			}
			if (!isContained){
				break;
			}
		}
		return newPos;
	}

	public ArenaRecord getPeakArenaRecord(PeakRecordInfo record) {
		ArenaRecord.Builder result = ArenaRecord.newBuilder();
		result.setUserId(record.getUserId());
		result.setWin(record.getWin() == 1);
		result.setPlaceUp(record.getPlaceUp());
		result.setName(record.getName());
		result.setHeadImage(record.getHeadImage());
		result.setLevel(record.getLevel());
		result.setTime(record.getTime());
		result.setChallenge(record.getChallenge());
		return result.build();
	}

	public ByteString buyChallengeCount(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (arenaData == null) {
			// 这种属于异常情况
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		IPrivilegeManager pri = player.getPrivilegeMgr();
		int maxBuyCount = pri.getIntPrivilege(PeakArenaPrivilegeNames.peakMaxCount);
		int buyCount = arenaData.getBuyCount();
		if (buyCount > maxBuyCount) {
			return SetError(response, player, "超过最大购买次数", ":" + maxBuyCount);
		}

		// 扣钱
		peakArenaBuyCost cfg = peakArenaBuyCostHelper.getInstance().getCfgByCount(buyCount + 1);
		UserGameDataMgr userMgr = player.getUserGameDataMgr();
		if (!userMgr.isEnoughCurrency(cfg.getCoinType(), cfg.getCost())) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "钻石不足");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		// 扣费，记录重置次数，重置开始时间
		if (!userMgr.deductCurrency(cfg.getCoinType(), cfg.getCost())) {
			return SetError(response, player, "钻石不足", "购买挑战次数时扣钻石失败:" + cfg.getCost());
		}

		// 购买挑战次数则直接设置倒计时为0
		arenaData.setFightStartTime(0);
		response.setCdTime(0);

		// 保存购买次数
		arenaData.setBuyCount(buyCount + 1);
		TablePeakArenaDataDAO.getInstance().update(arenaData);

		// 不需要返回全部数据
		// response.setArenaData(getPeakArenaData(arenaData, player));

		setSuccess(response, arenaData);
		return response.build().toByteString();
	}

	private ByteString sendFailRespon(Player player, MsgArenaResponse.Builder response, String tips) {
		player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, tips);
		response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		return response.build().toByteString();
	}

}
