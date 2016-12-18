package com.rw.service.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.bm.arena.ArenaBM;
import com.bm.arena.ArenaConstant;
import com.bm.arena.ArenaRankCfgDAO;
import com.bm.arena.ArenaRankEntity;
import com.bm.arena.ArenaScoreCfgDAO;
import com.bm.arena.ArenaScoreTemplate;
import com.bm.rank.arena.ArenaExtAttribute;
import com.common.RefParam;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.ArmyMagic;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.embattle.EmbattlePositonHelper;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.readonly.ItemDataIF;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.exception.ReplaceTargetNotExistException;
import com.rw.fsutil.ranking.exception.ReplacerAlreadyExistException;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.fashion.FashionHandle;
import com.rw.service.group.helper.GroupHelper;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BilogItemInfo;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.playerext.PlayerTempAttribute;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.arena.ArenaCostCfgDAO;
import com.rwbase.dao.arena.ArenaInfoCfgDAO;
import com.rwbase.dao.arena.TableArenaDataDAO;
import com.rwbase.dao.arena.TableArenaRecordDAO;
import com.rwbase.dao.arena.pojo.ArenaCost;
import com.rwbase.dao.arena.pojo.ArenaInfoCfg;
import com.rwbase.dao.arena.pojo.HurtValueRecord;
import com.rwbase.dao.arena.pojo.RecordInfo;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.arena.pojo.TableArenaRecord;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.hero.pojo.RoleBaseInfoIF;
import com.rwbase.dao.skill.pojo.SkillItem;
import com.rwproto.ArenaServiceProtos.ArenaData;
import com.rwproto.ArenaServiceProtos.ArenaEmbattleType;
import com.rwproto.ArenaServiceProtos.ArenaHisRewardView;
import com.rwproto.ArenaServiceProtos.ArenaHistoryResponse;
import com.rwproto.ArenaServiceProtos.ArenaHistoryRewardSum;
import com.rwproto.ArenaServiceProtos.ArenaInfo;
import com.rwproto.ArenaServiceProtos.ArenaRecord;
import com.rwproto.ArenaServiceProtos.HeroData;
import com.rwproto.ArenaServiceProtos.HistoryRankingRise;
import com.rwproto.ArenaServiceProtos.HurtValue;
//import com.rwproto.ArenaServiceProtos.HurtValueOrBuilder;;
import com.rwproto.ArenaServiceProtos.MsgArenaRequest;
import com.rwproto.ArenaServiceProtos.MsgArenaResponse;
import com.rwproto.ArenaServiceProtos.eArenaResultType;
import com.rwproto.ArenaServiceProtos.eArenaType;
import com.rwproto.BattleCommon.BattleHeroPosition;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.BattleCommon.ePlayerCamp;
import com.rwproto.FashionServiceProtos.FashionUsed;
import com.rwproto.MsgDef.Command;
import com.rwproto.PrivilegeProtos.ArenaPrivilegeNames;
import com.rwproto.SkillServiceProtos.TagSkillData;
import com.rwproto.TaskProtos.OneKeyResultType;

public class ArenaHandler {

	private static ArenaHandler instance = new ArenaHandler();
	private static int addScore = 2;

	protected ArenaHandler() {
	}

	public static ArenaHandler getInstance() {
		return instance;
	}

	public ByteString getMyPlace(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		if (player.getCareer() <= 0) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		int place = ArenaBM.getInstance().getArenaPlace(player);
		if (place < 0) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		} else {
			response.setPlace(place);
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		}
		return response.build().toByteString();
	}

	public ByteString getHurtValue(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		int recordId = request.getRecordId();
		RefParam<String> enemyId = new RefParam<String>();
		List<HurtValueRecord> listRecord = ArenaBM.getInstance().getRecordHurtValue(player.getUserId(), recordId, enemyId);
		if (listRecord == null) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		int size = listRecord.size();
		if (size <= 0) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		} else {
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			ArrayList<HurtValue> hurtList = new ArrayList<HurtValue>(size);
			for (int i = 0; i < size; i++) {
				HurtValueRecord record = listRecord.get(i);
				HurtValue.Builder builder = HurtValue.newBuilder();

				if (StringUtils.isNotBlank(record.getQualityId())) {
					builder.setQuality(record.getQualityId());
				}

				builder.setHeroId(record.getHeroId());
				builder.setCamp(record.getCamp());
				builder.setIcon(record.getIcon());
				builder.setLevel(record.getLevel());
				builder.setIsDead(record.isDead());
				builder.setPlayerType(record.getPlayerType());
				builder.setHp(record.getHp());
				builder.setSp(record.getSp());
				builder.setStartlevel(record.getStartlevel());
				builder.setValue(record.getValue());
				hurtList.add(builder.build());
			}
			response.addAllHurtValue(hurtList);
		}
		return response.build().toByteString();
	}

	public ByteString getInfo(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		String userId = player.getUserId();
		int career = player.getCareer();
		response.setArenaType(request.getArenaType());
		if (career <= 0) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "进行职业选择后才可以进入竞技场");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}

		TableArenaData arenaData = ArenaBM.getInstance().getArenaData(userId);
		if (arenaData == null) {
			arenaData = ArenaBM.getInstance().addArenaData(player);
			if (arenaData == null) {
				player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "数据错误");
				response.setArenaResultType(eArenaResultType.ARENA_FAIL);
				return response.build().toByteString();
			}
			setArenaHero(player, arenaData, null);
		}

		List<ListRankingEntry<String, ArenaExtAttribute>> listInfo = ArenaBM.getInstance().selectArenaInfos(player);
		for (ListRankingEntry<String, ArenaExtAttribute> info : listInfo) {
			response.addListInfo(getArenaInfo(info));
		}
		response.setArenaData(getArenaData(arenaData, player));
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);

		int resetTimes = arenaData.getResetTimes();
		ArenaCost resetArenaCfg = ArenaCostCfgDAO.getInstance().get(resetTimes + 1);
		if (resetArenaCfg == null) {
			GameLog.error("arena", "getInfo", "获取重置配置失败：" + (resetTimes + 1));
		} else {
			response.setResetCost(resetArenaCfg.getResetCost());
		}
		int buyTimes = arenaData.getBuyTimes();
		ArenaCost buyTimesCfg = ArenaCostCfgDAO.getInstance().get(buyTimes + 1);
		if (buyTimesCfg == null) {
			GameLog.error("arena", "getInfo", "获取购买挑战次数配置失败：" + (buyTimes + 1));
		} else {
			response.setBuyTimesCost(buyTimesCfg.getBuyTimesCost());
		}
		response.setBuyTimes(buyTimes);
		return response.build().toByteString();
	}

	public ByteString changeEnemys(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(eArenaType.CHANGE_ENEMY);
		TableArenaData m_MyArenaData = ArenaBM.getInstance().getArenaData(player.getUserId());
		if (m_MyArenaData == null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "数据错误");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}

		List<ListRankingEntry<String, ArenaExtAttribute>> listInfo = ArenaBM.getInstance().selectArenaInfos(player);
		for (ListRankingEntry<String, ArenaExtAttribute> info : listInfo) {
			response.addListInfo(getArenaInfo(info));
		}

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString changeHeros(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TableArenaData m_MyArenaData = ArenaBM.getInstance().getArenaData(player.getUserId());
		if (m_MyArenaData == null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "数据错误");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}

		setArenaHero(player, m_MyArenaData, request.getHeroIdsList());
		response.setArenaData(getArenaData(m_MyArenaData, player));
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public void setArenaHero(Player player, TableArenaData arenaData, List<BattleHeroPosition> heroPosList) {
		String userId = player.getUserId();
		arenaData.setUserId(userId);

		HeroMgr heroMgr = player.getHeroMgr();
		if (heroPosList != null) {
			int size = heroPosList.size();
			List<String> heroIds = new ArrayList<String>(size);
			for (int i = size; --i >= 0;) {
				String uuid = heroPosList.get(i).getHeroId();
				// if (heroMgr.getHeroById(uuid) != null) {
				if (heroMgr.getHeroById(player, uuid) != null) {
					heroIds.add(uuid);
				}
			}
			arenaData.setHeroIdList(heroIds);
		}

		int fighting = player.getMainRoleHero().getFighting();
		arenaData.setFighting(fighting);
		arenaData.setVip(player.getVip());
		arenaData.setSex(player.getSex());
		TableArenaDataDAO.getInstance().update(arenaData);
		ListRanking<String, ArenaExtAttribute> ranking = ArenaBM.getInstance().getRanking();
		if (ranking != null) {
			ListRankingEntry<String, ArenaExtAttribute> entry = ranking.getRankingEntry(userId);
			if (entry != null) {
				entry.getExtension().setFighting(ArenaBM.getInstance().getAllFighting(arenaData));
				ranking.subimitUpdatedTask(userId);
			}
		}

		if (!player.isRobot()) {
			// 存储到阵容中
			EmbattleInfoMgr.getMgr().updateOrAddEmbattleInfo(player, eBattlePositionType.ArenaPos_VALUE, String.valueOf(ArenaEmbattleType.ARENA_DEFEND_VALUE), EmbattlePositonHelper.parseMsgHeroPos2Memery(heroPosList));
		}
	}

	public ByteString getEnemyInfoData(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		TableArenaData arenaData = ArenaBM.getInstance().getArenaData(request.getUserId());
		if (arenaData == null) {
			return sendFailResponse(response, "找不到该玩家数据", player);
		}
		response.setArenaData(getArenaData(arenaData, null));

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString getArenaRecordInfo(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		String userId = player.getUserId();
		TableArenaRecordDAO arenaRecordDAO = TableArenaRecordDAO.getInstance();
		TableArenaRecord arenaTable = arenaRecordDAO.get(userId);
		if (arenaTable == null) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		} else {
			List<RecordInfo> listRecord = arenaTable.getRecordList();
			for (RecordInfo record : listRecord) {
				response.addListRecord(getArenaRecord(record));
			}
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			PlayerTempAttribute tempAttribute = player.getTempAttribute();
			tempAttribute.setRecordChanged(false);
		}
		return response.build().toByteString();
	}

	public ByteString initFightInfo(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		TableArenaData m_MyArenaData = ArenaBM.getInstance().getArenaData(player.getUserId());
		if (m_MyArenaData == null) {
			return sendFailResponse(response, "数据错误", player);
		}
		if (getNextFightTime(m_MyArenaData, player) > System.currentTimeMillis()) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, ArenaConstant.COOL_DOWN);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
			// return sendFailResponse(response, ArenaConstant.COOL_DOWN,
			// player);
		}

		if (m_MyArenaData.getRemainCount() <= 0) {
			return sendFailResponse(response, "挑战次数已用完", player);
		}
		String enemyUserId = request.getUserId();
		TableArenaData enemyArenaData = ArenaBM.getInstance().getArenaData(enemyUserId);
		if (enemyArenaData == null) {
			return sendFailResponse(response, "找不到对手玩家数据", player);
		}
		ListRankingEntry<String, ArenaExtAttribute> enmeyMgr = ArenaBM.getInstance().getEntry(enemyUserId, enemyArenaData.getCareer());
		if (enmeyMgr == null) {
			return sendFailResponse(response, "该对手暂时无法挑战，请重新选择对手", player);
		}

		if (enmeyMgr.getExtension().adjustTimeOutState()) {
			return sendFailResponse(response, "对手正在战斗中，请重新选择对手", player);
		}

		response.setArenaData(getArenaData(enemyArenaData, null));
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		response.addAllAtkList(ArenaBM.getInstance().getAtkHeroList(player.getUserId()));
		return response.build().toByteString();
	}

	public ByteString arenaFightStart(MsgArenaRequest request, Player player) {
		String userId = player.getUserId();

		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		int career = player.getCareer();
		if (career <= 0) {
			return sendFailResponse(response, "数据错误", player);
		}
		response.setArenaType(request.getArenaType());
		TableArenaData m_MyArenaData = ArenaBM.getInstance().getArenaData(userId);
		if (m_MyArenaData == null) {
			return sendFailResponse(response, "数据错误", player);
		}

		// 检查挑战次数
		if (m_MyArenaData.getRemainCount() <= 0) {
			return sendFailResponse(response, "挑战次数已用完", player);
		}

		String enemyUserId = request.getUserId();
		TableArenaData enemyArenaData = ArenaBM.getInstance().getArenaData(enemyUserId);
		if (enemyArenaData == null) {
			return sendFailResponse(response, ArenaConstant.ENEMY_NOT_EXIST, player);
		}
		ArenaBM arenaBM = ArenaBM.getInstance();
		ListRanking<String, ArenaExtAttribute> ranking = arenaBM.getRanking();
		ListRankingEntry<String, ArenaExtAttribute> entry = ranking.getRankingEntry(userId);
		if (entry == null && !ranking.isFull()) {
			arenaBM.addArenaData(player);
			entry = ranking.getRankingEntry(userId);
			if (entry == null) {
				return sendFailResponse(response, ArenaConstant.UNKOWN_EXCEPTION, player);
			}
		}

		// 转职的多线程保护，是木有的=。=
		ListRankingEntry<String, ArenaExtAttribute> enmeyEntry = ranking.getRankingEntry(enemyUserId);
		if (enmeyEntry == null) {
			return sendFailResponse(response, "无法挑战该名对手，请重新选择对手", player);
		}
		if (!enmeyEntry.getExtension().setFighting()) {
			return sendFailResponse(response, "对手正在战斗中，请重新选择对手", player);
		}
		// 强行设置挑战者
		entry.getExtension().forceSetFighting();
		// 设置后挑战者掉线，可怜的被挑战者只能等待超时时间(可以监听挑战者断线事件)
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.ARENA, 0, 0);

		List<BattleHeroPosition> heroPosList = request.getAtkIdListList();

		int size = heroPosList.size();
		List<String> heroIds = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			heroIds.add(heroPosList.get(i).getHeroId());
		}

		// 存储到阵容中
		EmbattleInfoMgr.getMgr().updateOrAddEmbattleInfo(player, eBattlePositionType.ArenaPos_VALUE, String.valueOf(ArenaEmbattleType.ARENA_ATK_VALUE), EmbattlePositonHelper.parseMsgHeroPos2Memery(heroPosList));

		arenaBM.updateAtkHeroList(heroIds, player);
		return response.build().toByteString();
	}

	private ByteString sendFailResponse(MsgArenaResponse.Builder response, String failTips, Player player) {
		player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, failTips);
		response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		return response.build().toByteString();
	}

	public ByteString arenaFightFinish(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		ArenaBM arenaBM = ArenaBM.getInstance();
		String userId = player.getUserId();
		TableArenaData m_MyArenaData = arenaBM.getArenaData(userId);
		if (m_MyArenaData == null) {
			return sendFailResponse(response, "数据错误", player);
		}
		int win = request.getWin();
		String enemyUserId = request.getUserId();
		int career = player.getCareer();
		if (career <= 0) {
			return sendFailResponse(response, "数据错误", player);
		}
		ListRanking<String, ArenaExtAttribute> ranking = arenaBM.getRanking();
		ListRankingEntry<String, ArenaExtAttribute> entry = ranking.getRankingEntry(userId);
		int oldPlace;
		ArenaExtAttribute arenaExt = null;
		if (entry == null) {
			if (!ranking.isFull()) {
				arenaBM.addArenaData(player);
				entry = ranking.getRankingEntry(userId);
				if (entry == null) {
					return sendFailResponse(response, ArenaConstant.ENEMY_PLACE_CHANGED, player);
				}
				oldPlace = entry.getRanking();
			} else {
				oldPlace = ranking.getMaxCapacity();
			}
		} else {
			arenaExt = entry.getExtension();
			oldPlace = entry.getRanking();
		}

		TableArenaData enemyArenaData = ArenaBM.getInstance().getArenaData(enemyUserId);
		if (enemyArenaData == null) {
			// 对手战斗状态未设置，采用等待超时的容错机制
			if (arenaExt != null) {
				arenaExt.setNotFighting();
			}
			return sendFailResponse(response, ArenaConstant.ENEMY_PLACE_CHANGED, player);
		}

		ListRankingEntry<String, ArenaExtAttribute> enemyEntry = ranking.getRankingEntry(enemyUserId);
		if (enemyEntry == null) {
			if (arenaExt != null) {
				arenaExt.setNotFighting();
			}
			// TODO 需要与策划商量极端情况，当战斗后该对手已经转职
			return sendFailResponse(response, ArenaConstant.ENEMY_PLACE_CHANGED, player);
		}

		boolean isWin = win == 1;
		ArenaExtAttribute enemyExt = enemyEntry.getExtension();
		// 必须交换完双方位置才能设置战斗状态
		try {
			// 挑战者(我)胜利的情况
			if (isWin) {
				// 这里多线程情况会导致交换出错
				if (entry != null) {
					if (entry.getRanking() > enemyEntry.getRanking() && !ranking.swap(userId, enemyUserId)) {
						if (ranking.contains(userId)) {
							return sendFailResponse(response, ArenaConstant.ENEMY_PLACE_CHANGED, player);
						}
						arenaExt = ArenaBM.getInstance().createArenaExt(player);
						try {
							ranking.replace(userId, arenaExt, enemyUserId);
						} catch (ReplacerAlreadyExistException e) {
							GameLog.error("竞技场", player.getUserId(), "严重错误@竞技场#replace失败：" + userId);
							return sendFailResponse(response, ArenaConstant.UNKOWN_EXCEPTION, player);
						} catch (ReplaceTargetNotExistException e) {
							return sendFailResponse(response, ArenaConstant.ENEMY_PLACE_CHANGED, player);
						}
					}
					// 排名比他高，就不做处理啦
				} else {
					arenaExt = ArenaBM.getInstance().createArenaExt(player);
					try {
						entry = ranking.replace(userId, arenaExt, enemyUserId);
					} catch (ReplacerAlreadyExistException e) {
						GameLog.error("竞技场", player.getUserId(), "严重错误@竞技场#replace失败：" + userId);
						return sendFailResponse(response, ArenaConstant.UNKOWN_EXCEPTION, player);
					} catch (ReplaceTargetNotExistException e) {
						// 对手也被打下来了
						return sendFailResponse(response, ArenaConstant.ENEMY_PLACE_CHANGED, player);
					}
				}
				TableArenaDataDAO.getInstance().update(enemyArenaData);
			}

			ListRankingEntry<String, ArenaExtAttribute> newEntry = ranking.getRankingEntry(userId);
			int newPlace = newEntry.getRanking();
			long currentTime = System.currentTimeMillis();
			List<com.rwproto.ArenaServiceProtos.HurtValue> hurtValueList = request.getHurtValueList();
			int size = hurtValueList.size();
			ArrayList<HurtValueRecord> hurtValueList_ = new ArrayList<HurtValueRecord>(size);
			ArrayList<HurtValueRecord> enemyHurtList = new ArrayList<HurtValueRecord>(size);
			for (int i = 0; i < size; i++) {
				com.rwproto.ArenaServiceProtos.HurtValue value = hurtValueList.get(i);
				hurtValueList_.add(createHurtRecord(value, false));
				enemyHurtList.add(createHurtRecord(value, true));
			}
			RecordInfo record = new RecordInfo();
			int maxPlace = ArenaBM.getInstance().getMaxPlace(m_MyArenaData);
			// int maxPlace = m_MyArenaData.getMaxPlace();
			record.setHurtList(hurtValueList_);
			record.setUserId(enemyUserId);
			record.setWin(win);
			HistoryRankingRise.Builder builder = null;
			if (win == 0) {
				record.setPlaceUp(0);
			} else {
				// TODO 这里排名变化准确需要ListRanking支持，迟点实现
				if (newPlace < oldPlace) {
					builder = HistoryRankingRise.newBuilder();
					builder.setHistoryRanking(maxPlace);
					builder.setCurrentRanking(newPlace);
					int rankingUp = oldPlace - newPlace;
					builder.setRankingUp(rankingUp);
					record.setPlaceUp(rankingUp);

					player.getFresherActivityMgr().doCheck(eActivityType.A_ArenaRank);
				}
			}
			record.setName(enemyArenaData.getName());
			record.setHeadImage(enemyArenaData.getHeadImage());
			record.setLevel(enemyArenaData.getLevel());
			record.setTime(currentTime);
			record.setChallenge(1);
			record.setVip(enemyArenaData.getVip());
			record.setSex(enemyArenaData.getSex());
			record.setFighting(enemyArenaData.getFighting());
			if (newPlace < maxPlace) {
				m_MyArenaData.setMaxPlace(newPlace);
			}
			m_MyArenaData.setWinCount(m_MyArenaData.getWinCount() + 1);
			ArenaBM.getInstance().addRecord(userId, record, false);

			RecordInfo recordForEnemy = new RecordInfo();
			recordForEnemy.setHurtList(enemyHurtList);
			recordForEnemy.setUserId(m_MyArenaData.getUserId());
			recordForEnemy.setWin(1 - win);
			if (!isWin) {
				recordForEnemy.setPlaceUp(0);
			} else {
				// TODO 这里排名变化准确需要ListRanking支持，迟点实现
				if (newPlace < oldPlace) {
					recordForEnemy.setPlaceUp(newPlace - oldPlace);
				}
			}
			recordForEnemy.setName(m_MyArenaData.getName());
			recordForEnemy.setHeadImage(m_MyArenaData.getHeadImage());
			recordForEnemy.setLevel(m_MyArenaData.getLevel());
			recordForEnemy.setTime(currentTime);
			recordForEnemy.setChallenge(0);
			recordForEnemy.setVip(m_MyArenaData.getVip());
			recordForEnemy.setSex(m_MyArenaData.getSex());
			recordForEnemy.setFighting(m_MyArenaData.getFighting());
			ArenaBM.getInstance().addRecord(enemyUserId, recordForEnemy, true);
			ArenaRecord ar = getArenaRecord(record);

			m_MyArenaData.setLastFightTime(System.currentTimeMillis());
			m_MyArenaData.setLastChallengeVictory(isWin);
			m_MyArenaData.setRemainCount(m_MyArenaData.getRemainCount() - 1);
			// 胜利时增加的积分
			int score = addScore;
			m_MyArenaData.setScore(m_MyArenaData.getScore() + score);
			UserEventMgr.getInstance().ArenaDaily(player, score);
			TableArenaDataDAO.getInstance().update(m_MyArenaData);
			if (isWin) {
				Player enemyPlayer = PlayerMgr.getInstance().find(enemyUserId);
				if (enemyPlayer != null) {
					enemyPlayer.getTempAttribute().setRecordChanged(true);
				}
			}

			// 增加挑战次数 2016-04-14 edit by lida
			m_MyArenaData.setChallengeTime(m_MyArenaData.getChallengeTime() + 1);

			MsgArenaResponse.Builder recordBuilder = MsgArenaResponse.newBuilder();
			recordBuilder.setArenaType(eArenaType.SYNC_RECORD);
			recordBuilder.addListRecord(ar);
			recordBuilder.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			player.SendMsg(Command.MSG_ARENA, recordBuilder.build().toByteString());
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Arena, 1);
			if (builder != null) {
				System.out.println(builder.getCurrentRanking() + "," + builder.getGoldAward() + "," + builder.getHistoryRanking() + "," + builder.getRankingUp());
				response.setHistory(builder.build());
			}
			// 开服活动通知 竞技场挑战次数
			player.getFresherActivityMgr().doCheck(eActivityType.A_ArenaChallengeTime);

			return response.build().toByteString();
		} finally {
			arenaExt.setNotFighting();
			enemyExt.setNotFighting();
		}
	}

	private HurtValueRecord createHurtRecord(com.rwproto.ArenaServiceProtos.HurtValue value, boolean reverse) {
		HurtValueRecord valueRecord = new HurtValueRecord();
		if (reverse) {
			ePlayerCamp camp = value.getCamp();
			valueRecord.setCamp(camp == ePlayerCamp.Me ? ePlayerCamp.Enemy : ePlayerCamp.Me);
		} else {
			valueRecord.setCamp(value.getCamp());
		}
		valueRecord.setDead(value.getIsDead());
		valueRecord.setHeroId(value.getHeroId());
		valueRecord.setHp(value.getHp());
		valueRecord.setSp(value.getSp());
		valueRecord.setLevel(value.getLevel());
		valueRecord.setStartlevel(value.getStartlevel());
		valueRecord.setPlayerType(value.getPlayerType());
		valueRecord.setIcon(value.getIcon());
		valueRecord.setValue(value.getValue());
		valueRecord.setQualityId(value.getQuality());
		return valueRecord;
	}

	public ByteString buyTimes(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TableArenaData m_MyArenaData = ArenaBM.getInstance().getArenaData(player.getUserId());
		if (m_MyArenaData == null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "找不到该玩家数据");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		int remainCount = m_MyArenaData.getRemainCount();
		if (remainCount > 0) {
			response.setArenaData(getArenaData(m_MyArenaData, player));
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			return response.build().toByteString();
		}

		int buyTimes = m_MyArenaData.getBuyTimes();
		// by franky
		int allowMaxBuyCount = player.getPrivilegeMgr().getIntPrivilege(ArenaPrivilegeNames.arenaMaxCount);
		if (allowMaxBuyCount < buyTimes) {
			GameLog.info("arena", player.getUserId(), "buyTimes:购买次数已达上限,buyTimes=" + buyTimes + ",allowBuyMaxCount=" + allowMaxBuyCount, null);
			// player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox,
			// ArenaConstant.VIP_LEVEL_NOT_ENOUGHT);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}

		/*
		 * // 检查vip等级 int vipLevel = player.getVip(); PrivilegeCfg privilegeCfg = PrivilegeCfgDAO.getInstance().getCfg(vipLevel); if (privilegeCfg == null) { GameLog.error("arena", "buyTimes", player
		 * + "获取特权配置失败,vipLevle = " + vipLevel); player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, ArenaConstant.VIP_CONFIG_IS_NULL); response.setArenaResultType(eArenaResultType.ARENA_FAIL); return
		 * response.build().toByteString(); } if (privilegeCfg.getSportBuyCount() <= buyTimes) { GameLog.error("arena", "buyTimes", player + "当前vip购买次数已达上限," + buyTimes + "," +
		 * privilegeCfg.getSportBuyCount()); player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, ArenaConstant.VIP_LEVEL_NOT_ENOUGHT); response.setArenaResultType(eArenaResultType.ARENA_FAIL); return
		 * response.build().toByteString(); }
		 */

		int nextBuyTimes = buyTimes + 1;
		ArenaCost arenaCostCfg = ArenaCostCfgDAO.getInstance().get(nextBuyTimes);
		if (arenaCostCfg == null) {
			GameLog.error("arena", "buyTimes", player + "获取竞技场购买配置失败：" + nextBuyTimes);
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, ArenaConstant.VIP_CONFIG_IS_NULL);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		int costGold = arenaCostCfg.getBuyTimesCost();
		if (player.getUserGameDataMgr().getGold() < costGold) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "钻石不足");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		player.getUserGameDataMgr().addGold(-costGold);
		m_MyArenaData.setBuyTimes(nextBuyTimes);
		m_MyArenaData.setRemainCount(m_MyArenaData.getRemainCount() + 1);
		m_MyArenaData.setLastFightTime(0);
		TableArenaDataDAO.getInstance().update(m_MyArenaData);
		response.setArenaData(getArenaData(m_MyArenaData, player));
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString clearCD(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TableArenaData m_MyArenaData = ArenaBM.getInstance().getArenaData(player.getUserId());
		if (m_MyArenaData == null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "找不到该玩家数据");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}

		// by franky
		boolean isOpen = player.getPrivilegeMgr().getBoolPrivilege(ArenaPrivilegeNames.isAllowResetArena);
		if (!isOpen) {
			GameLog.info("arena", player.getUserId(), "clearCD:未开启重置竞技场CD", null);
			// player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox,
			// ArenaConstant.VIP_LEVEL_NOT_ENOUGHT);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}

		/*
		 * // 检查vip等级 int vipLevel = player.getVip(); PrivilegeCfg privilegeCfg = PrivilegeCfgDAO.getInstance().getCfg(vipLevel); if (privilegeCfg == null) {
		 * player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, ArenaConstant.VIP_CONFIG_IS_NULL); response.setArenaResultType(eArenaResultType.ARENA_FAIL); return response.build().toByteString(); } //
		 * 检查vip等级是否开启该功能 if (privilegeCfg.getArenaResetCDOpen() == 0) { player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, ArenaConstant.VIP_LEVEL_NOT_ENOUGHT);
		 * response.setArenaResultType(eArenaResultType.ARENA_FAIL); return response.build().toByteString(); }
		 */

		long nextFightingTime = m_MyArenaData.getLastFightTime();
		if (nextFightingTime <= 0) {
			response.setArenaData(getArenaData(m_MyArenaData, player));
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			return response.build().toByteString();
		}
		// 根据次数获取配置
		int clearCDTimes = m_MyArenaData.getResetTimes() + 1;
		ArenaCost arenaCostCfg = ArenaCostCfgDAO.getInstance().get(clearCDTimes);
		if (arenaCostCfg == null) {
			GameLog.error("arena", "clearCD", player + "获取重置次数配置失败：" + clearCDTimes);
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, ArenaConstant.VIP_CONFIG_IS_NULL);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		int costGold = arenaCostCfg.getResetCost();
		if (player.getUserGameDataMgr().getGold() < costGold) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "钻石不足");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		player.getUserGameDataMgr().addGold(-costGold);
		m_MyArenaData.setLastFightTime(0);
		m_MyArenaData.setResetTimes(clearCDTimes);
		TableArenaDataDAO.getInstance().update(m_MyArenaData);
		response.setArenaData(getArenaData(m_MyArenaData, player));
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ArenaInfo getArenaInfo(ListRankingEntry<String, ArenaExtAttribute> info) {
		ArenaInfo.Builder result = ArenaInfo.newBuilder();
		ArenaExtAttribute arenaExtAttr = info.getExtension();
		String key = info.getKey();
		result.setUserId(key);
		result.setCareer(arenaExtAttr.getCareer());
		PlayerIF player = PlayerMgr.getInstance().getReadOnlyPlayer(key);
		TableArenaData arenaData = TableArenaDataDAO.getInstance().get(key);
		List<String> heroIds = arenaData.getHeroIdList();
		int size = heroIds.size();
		ArrayList<String> heroImages = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			String heroId = heroIds.get(i);
			// HeroIF hero = player.getHeroMgr().getHeroById(heroId);
			Hero hero = player.getHeroMgr().getHeroById(player, heroId);
			if (hero != null) {
				// heroImages.add(hero.getHeroCfg().getImageId());
				heroImages.add(FSHeroMgr.getInstance().getHeroCfg(hero).getImageId());
			}
		}

		// result.setHeadImage(arenaExtAttr.getHeadImage());
		result.setFighting(ArenaBM.getInstance().getAllFighting(arenaData));
		result.setHeadImage(player.getTableUser().getHeadImageWithDefault());
		result.setLevel(arenaExtAttr.getLevel());
		result.setName(arenaExtAttr.getName());
		result.setState(arenaExtAttr.adjustTimeOutState() ? 1 : 0);
		result.setPlace(info.getRanking());
		result.addAllHeroImages(heroImages);
		result.setModelId(player.getModelId());
		// 设置时装数据
		result.setSex(player.getSex());
		FashionUsed.Builder fashionUsing = FashionHandle.getInstance().getFashionUsedProto(key);
		if (fashionUsing != null) {
			result.setFashionUsage(fashionUsing);
		}
		ItemDataIF magic = player.getMagic();
		if (magic != null) {
			result.setMagicModelId(magic.getModelId());
		} else {
			result.setMagicModelId(0);
		}
		return result.build();
	}

	private long getNextFightTime(TableArenaData arenaData, Player player) {
		int decCount = player.getPrivilegeMgr().getIntPrivilege(ArenaPrivilegeNames.arenaChallengeDec);
		ArenaInfoCfg arenaInfoCfg = ArenaInfoCfgDAO.getInstance().getArenaInfo();
		int configCdTime;
		if (arenaData.isLastChallengeVictory()) {
			configCdTime = arenaInfoCfg.getWinCdTime();
		} else {
			configCdTime = arenaInfoCfg.getLoseCdTime();
		}
		int cdTime = configCdTime - decCount;
		if (cdTime < 0) {
			cdTime = 0;
		}
		return arenaData.getLastFightTime() + cdTime * 1000L;
	}

	public ArenaData getArenaData(TableArenaData arenaData, Player player) {
		ArenaData.Builder data = ArenaData.newBuilder();
		String enemyId = arenaData.getUserId();
		int career = arenaData.getCareer();
		data.setUserId(enemyId);
		data.setCareer(career);
		data.setPlace(ArenaBM.getInstance().getOtherArenaPlace(enemyId, career));
		data.setMaxPlace(ArenaBM.getInstance().getMaxPlace(arenaData));
		int remainCount = arenaData.getRemainCount();
		data.setRemainCount(remainCount);
		long lastFightTime = arenaData.getLastFightTime();
		// 这里不为0的时候才设置冷却时间
		if (player != null && lastFightTime > 0 && remainCount > 0) {
			// by franky
			long currentTime = System.currentTimeMillis();
			long nextFightTime = getNextFightTime(arenaData, player);
			if (nextFightTime > currentTime) {
				data.setCdTime(TimeUnit.MILLISECONDS.toSeconds(nextFightTime - currentTime));
			}
		}

		data.setHeadImage(arenaData.getHeadImage());
		data.setLevel(arenaData.getLevel());
		data.setName(arenaData.getName());
		int fighting = 0;
		List<String> heroIdList = arenaData.getHeroIdList();
		if (heroIdList != null)
			heroIdList.remove(enemyId);
		ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(enemyId, heroIdList);
		List<ArmyHero> armyList = armyInfo.getHeroList();
		int armySize = armyList.size();

		// 填充站位
		EmbattlePositionInfo posInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(enemyId, eBattlePositionType.ArenaPos_VALUE, String.valueOf(ArenaEmbattleType.ARENA_DEFEND_VALUE));

		for (int i = 0; i < armySize; i++) {
			ArmyHero hero = armyList.get(i);
			if (hero == null) {
				continue;
			}

			hero.setPosition(posInfo == null ? i + 1 : posInfo.getHeroPos(hero.getRoleBaseInfo().getId()));
			data.addHeros(getHeroData(hero));
			fighting += hero.getFighting();
		}

		fighting += armyInfo.getPlayer().getFighting();
		data.setFighting(fighting);
		data.setTempleteId(arenaData.getTempleteId());
		data.setWinCount(arenaData.getWinCount());
		try {
			data.setArmyInfo(armyInfo.toJson());
		} catch (Exception e) {
			GameLog.error("竞技场", enemyId, "职业竞技场转换armyInfo异常", e);
		}

		ArmyMagic magic = armyInfo.getArmyMagic();
		data.setMagicId(magic.getModelId());
		data.setMagicLevel(magic.getLevel());
		List<SkillItem> skills = armyInfo.getPlayer().getSkillList();
		for (SkillItem skill : skills) {
			data.addRoleSkill(transfrom(skill));
		}
		String gName = GroupHelper.getInstance().getGroupName(enemyId);
		if (StringUtils.isNotBlank(gName)) {
			data.setGroupName(gName);
		}
		return data.build();
	}

	private TagSkillData transfrom(SkillItem skill) {
		TagSkillData.Builder builder = TagSkillData.newBuilder();
		builder.setId(skill.strId());
		builder.setOwnerId(skill.getOwnerId());
		builder.setSkillId(skill.getSkillId());
		// builder.addAllBuffId(skill.getBuffId());
		builder.setOrder(skill.getOrder());
		builder.setSkillRate(skill.getSkillRate());
		builder.setExtraDamage(skill.getExtraDamage());
		return builder.build();
	}

	public HeroData getHeroData(ArmyHero tableHeroData) {
		HeroData.Builder result = HeroData.newBuilder();
		RoleBaseInfoIF baseInfo = tableHeroData.getRoleBaseInfo();
		result.setHeroId(baseInfo.getId());
		result.setTempleteId(baseInfo.getTemplateId());
		result.setLevel(baseInfo.getLevel());
		result.setStarLevel(baseInfo.getStarLevel());
		result.setFighting(tableHeroData.getFighting());
		result.setQualityId(baseInfo.getQualityId());
		result.setExp(baseInfo.getExp());

		for (SkillItem skill : tableHeroData.getSkillList()) {
			result.addSkills(transfrom(skill));
		}
		return result.build();
	}

	public ArenaRecord getArenaRecord(RecordInfo record) {
		ArenaRecord.Builder result = ArenaRecord.newBuilder();
		result.setRecordId(record.getRecordId());
		result.setUserId(record.getUserId());
		result.setWin(record.getWin());
		result.setPlaceUp(record.getPlaceUp());
		result.setName(record.getName());
		result.setHeadImage(record.getHeadImage());
		result.setLevel(record.getLevel());
		result.setTime(record.getTime());
		result.setChallenge(record.getChallenge());
		result.setFighting(record.getFighting());
		result.setVip(record.getVip());
		result.setSex(record.getSex());
		FashionUsed.Builder usingFashion = FashionHandle.getInstance().getFashionUsedProto(record.getUserId());
		if (null != usingFashion) {
			result.setFashionUsed(usingFashion);
		}
		return result.build();
	}

	public ByteString getScoreInfo(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TableArenaData arenaData = ArenaBM.getInstance().getArenaData(player.getUserId());
		if (arenaData == null) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return fillArenaScore(arenaData, response);
	}

	/**
	 * 获取积分奖励
	 * 
	 * @param request
	 * @param player
	 * @return
	 */
	public ByteString getScoreReward(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TableArenaData arenaData = ArenaBM.getInstance().getArenaData(player.getUserId());
		if (arenaData == null) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		int id = request.getRewardId();
		List<Integer> rewardList = arenaData.getRewardList();
		if (rewardList.contains(id)) {
			GameLog.error("ArenaHandler", "#getScoreReward()", "重复领取积分奖励：" + id);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return fillArenaScore(arenaData, response);
		}
		ArenaScoreTemplate template = ArenaScoreCfgDAO.getInstance().getScoreTemplate(id);
		if (template == null) {
			GameLog.error("ArenaHandler", "#getScoreReward()", "领取不存在的积分奖励：" + id);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return fillArenaScore(arenaData, response);
		}
		int score = arenaData.getScore();
		if (template.getScore() > score) {
			GameLog.error("ArenaHandler", "#getScoreReward()", "领取奖励的积分不够:id = " + id + ",score = " + score);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return fillArenaScore(arenaData, response);
		}
		rewardList.add(id);
		Map<Integer, Integer> rewards = template.getRewards();
		List<ItemInfo> itemInfoList = new ArrayList<ItemInfo>(rewards.size());
		// ItemBagMgr itemBagMgr = player.getItemBagMgr();
		for (Map.Entry<Integer, Integer> entry : rewards.entrySet()) {
			// itemBagMgr.addItem(entry.getKey(), entry.getValue());
			itemInfoList.add(new ItemInfo(entry.getKey(), entry.getValue()));
		}
		ItemBagMgr.getInstance().addItem(player, itemInfoList);

		List<BilogItemInfo> list = BilogItemInfo.fromMap(rewards);
		String rewardInfoActivity = BILogTemplateHelper.getString(list);

		BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.ARENA_INTEGRAL_REWARDS, 0, 0);
		BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.ARENA_INTEGRAL_REWARDS, 0, true, 0, rewardInfoActivity, 0);
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return fillArenaScore(arenaData, response);
	}

	/**
	 * 获取积分奖励
	 * 
	 * @param request
	 * @param player
	 * @return
	 */
	public OneKeyResultType getAllScoreReward(Player player, HashMap<Integer, Integer> rewardMap) {
		TableArenaData arenaData = ArenaBM.getInstance().getArenaData(player.getUserId());
		if (arenaData == null) {
			return OneKeyResultType.DATA_ERROR;
		}
		List<Integer> scoreRewardKeys = ArenaScoreCfgDAO.getInstance().getKeys();
		if (null == scoreRewardKeys || scoreRewardKeys.isEmpty()) {
			return OneKeyResultType.NO_REWARD;
		}
		boolean isGet = false;
		for (Integer id : scoreRewardKeys) {
			List<Integer> rewardList = arenaData.getRewardList();
			if (rewardList.contains(id)) {
				// 已经领取过
				continue;
			}
			ArenaScoreTemplate template = ArenaScoreCfgDAO.getInstance().getScoreTemplate(id);
			int score = arenaData.getScore();
			if (template.getScore() > score) {
				// 积分不够
				continue;
			}
			rewardList.add(id);
			Map<Integer, Integer> rewards = template.getRewards();
			for (Map.Entry<Integer, Integer> entry : rewards.entrySet()) {
				Integer haveCount = rewardMap.get(entry.getKey());
				if (null == haveCount)
					haveCount = entry.getValue();
				else
					haveCount += entry.getValue();
				rewardMap.put(entry.getKey(), haveCount);
			}
			isGet = true;
			// 日志
			List<BilogItemInfo> list = BilogItemInfo.fromMap(rewards);
			String rewardInfoActivity = BILogTemplateHelper.getString(list);
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.ARENA_INTEGRAL_REWARDS, 0, 0);
			BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.ARENA_INTEGRAL_REWARDS, 0, true, 0, rewardInfoActivity, 0);
		}
		if (isGet)
			return OneKeyResultType.OneKey_SUCCESS;
		else
			return OneKeyResultType.NO_REWARD;
	}

	private ByteString fillArenaScore(TableArenaData arenaData, MsgArenaResponse.Builder response) {
		response.setCurrentScore(arenaData.getScore());
		response.addAllGetCount(arenaData.getRewardList());
		return response.build().toByteString();

	}

	/**
	 * 获取历史奖励
	 * 
	 * @param request
	 * @param player
	 * @return
	 */
	public ByteString getHistoryView(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		String userId = player.getUserId();
		TableArenaData arenaData = ArenaBM.getInstance().getArenaData(userId);
		if (arenaData == null) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		List<Integer> rewardRecord = arenaData.getHistoryRewards();
		ArenaRankCfgDAO rankCfgDAO = ArenaRankCfgDAO.getInstance();
		List<Integer> list = rankCfgDAO.getAllRankIds();
		int size = list.size();
		ArrayList<ArenaHisRewardView> viewList = new ArrayList<ArenaHisRewardView>(size);
		for (int i = 0; i < size; i++) {
			int id = list.get(i);
			ArenaHisRewardView.Builder view = ArenaHisRewardView.newBuilder();
			view.setRewardId(id);
			view.setGainReward(rewardRecord.contains(id));
			viewList.add(view.build());
		}
		ArenaHistoryResponse.Builder historyBuilder = ArenaHistoryResponse.newBuilder();
		historyBuilder.addAllRewardView(viewList);
		// 整合奖励和对应的总数
		HashMap<Integer, Integer> totalRewardMap = new HashMap<Integer, Integer>();
		for (Integer rewardId : rewardRecord) {
			ArenaRankEntity entity = rankCfgDAO.getArenaRankEntity(rewardId);
			List<ItemInfo> rewardList = entity.getRewardList();
			for (int i = rewardList.size(); --i >= 0;) {
				ItemInfo item = rewardList.get(i);
				Integer itemId = item.getItemID();
				int num = item.getItemNum();
				Integer old = totalRewardMap.get(itemId);
				if (old == null) {
					totalRewardMap.put(itemId, num);
				} else {
					totalRewardMap.put(itemId, num + old);
				}
			}
		}
		ArrayList<ArenaHistoryRewardSum> rewardSum = new ArrayList<ArenaHistoryRewardSum>(totalRewardMap.size());
		for (Map.Entry<Integer, Integer> entry : totalRewardMap.entrySet()) {
			ArenaHistoryRewardSum.Builder builder = ArenaHistoryRewardSum.newBuilder();
			builder.setItemId(entry.getKey());
			builder.setNum(entry.getValue());
			rewardSum.add(builder.build());
		}
		historyBuilder.addAllRewardSum(rewardSum);
		response.setHistoryReward(historyBuilder.build());
		return response.build().toByteString();
	}

	/**
	 * 获取竞技场历史排名奖励
	 * 
	 * @param request
	 * @param player
	 * @return
	 */
	public ByteString getHistoryReward(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		String userId = player.getUserId();
		TableArenaData arenaData = ArenaBM.getInstance().getArenaData(userId);
		if (arenaData == null) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return getHistoryView(request, player);
		}
		int id = request.getHistoryRewardId();
		List<Integer> historyRewards = arenaData.getHistoryRewards();
		if (historyRewards.contains(id)) {
			GameLog.error("ArenaHandler", "#getHistoryReward()", "重复历史排名奖励：" + id);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return getHistoryView(request, player);
		}
		ArenaRankEntity rankEntity = ArenaRankCfgDAO.getInstance().getArenaRankEntity(id);
		if (rankEntity == null) {
			GameLog.error("ArenaHandler", "#getHistoryReward()", "领取不存在的历史排名奖励：" + id);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return getHistoryView(request, player);
		}
		int maxPlace = ArenaBM.getInstance().getMaxPlace(arenaData);
		int rankRequire = rankEntity.getRank();
		if (rankRequire < maxPlace) {
			GameLog.error("ArenaHandler", "#getHistoryReward()", "领取历史排名奖励的名次不够:id = " + id + ",rank=" + rankRequire + ",maxPlace=" + maxPlace);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return getHistoryView(request, player);
		}
		historyRewards.add(id);
		List<ItemInfo> rewards = rankEntity.getRewardList();
		// ItemBagMgr itemBagMgr = player.getItemBagMgr();
		// for (ItemInfo item : rewards) {
		// itemBagMgr.addItem(item.getItemID(), item.getItemNum());
		// }
		ItemBagMgr.getInstance().addItem(player, rewards);
		TableArenaDataDAO.getInstance().update(userId);
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);

		BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.ARENA_REWARDS_HISTORY, 0, 0);

		List<BilogItemInfo> list = BilogItemInfo.fromItemList(rewards);
		String rewardInfoActivity = BILogTemplateHelper.getString(list);

		BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.ARENA_REWARDS_HISTORY, 0, true, 0, rewardInfoActivity, 0);
		return getHistoryView(request, player);
	}

}
