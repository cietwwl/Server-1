package com.rw.service.arena;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bm.arena.ArenaBM;
import com.bm.arena.ArenaConstant;
import com.bm.rank.arena.ArenaExtAttribute;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.HotPointMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.ArmyMagic;
import com.playerdata.readonly.HeroIF;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.exception.ReplaceTargetNotExistException;
import com.rw.fsutil.ranking.exception.ReplacerAlreadyExistException;
import com.rw.service.Email.EmailUtils;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.arena.ArenaCostCfgDAO;
import com.rwbase.dao.arena.ArenaInfoCfgDAO;
import com.rwbase.dao.arena.ArenaUpPrizeCfgDAO;
import com.rwbase.dao.arena.TableArenaDataDAO;
import com.rwbase.dao.arena.pojo.ArenaCost;
import com.rwbase.dao.arena.pojo.ArenaInfoCfg;
import com.rwbase.dao.arena.pojo.HurtValueRecord;
import com.rwbase.dao.arena.pojo.PrizeInfo;
import com.rwbase.dao.arena.pojo.RecordInfo;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailCfg;
import com.rwbase.dao.email.EmailCfgDAO;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.hotPoint.EHotPointType;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwproto.ArenaServiceProtos.ArenaData;
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
import com.rwproto.MsgDef.Command;
import com.rwproto.SkillServiceProtos.TagSkillData;

public class ArenaHandler {

	private static ArenaHandler instance;

	private ArenaHandler() {
	}

	public static ArenaHandler getInstance() {
		if (instance == null) {
			instance = new ArenaHandler();
		}
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
		List<HurtValueRecord> listRecord = ArenaBM.getInstance().getRecordHurtValue(player.getUserId(), recordId);
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
			setArenaHero(player, arenaData, request.getHeroIdsList());
		}

		List<ListRankingEntry<String, ArenaExtAttribute>> listInfo = ArenaBM.getInstance().selectArenaInfos(player);
		for (ListRankingEntry<String, ArenaExtAttribute> info : listInfo) {
			response.addListInfo(getArenaInfo(info));
		}
		response.setArenaData(getArenaData(arenaData));
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
		response.setArenaData(getArenaData(m_MyArenaData));
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public void setArenaHero(Player player, TableArenaData arenaData, List<String> heroIds) {
		String userId = player.getUserId();
		int fighting = player.getMainRoleHero().getFighting();
		arenaData.setUserId(userId);
		arenaData.setHeroIdList(heroIds);

		arenaData.setFighting(fighting);
		TableArenaDataDAO.getInstance().update(arenaData);
		ListRanking<String, ArenaExtAttribute> ranking = ArenaBM.getInstance().getRanking(player.getCareer());
		if (ranking != null) {
			ListRankingEntry<String, ArenaExtAttribute> entry = ranking.getRankingEntry(userId);
			if (entry != null) {
				entry.getExtension().setFighting(ArenaBM.getInstance().getAllFighting(arenaData));
				ranking.subimitUpdatedTask(userId);
			}
		}
	}

	public ByteString getEnemyInfoData(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		TableArenaData arenaData = ArenaBM.getInstance().getArenaData(request.getUserId());
		if (arenaData == null) {
			return sendFailResponse(response, "找不到该玩家数据", player);
		}
		response.setArenaData(getArenaData(arenaData));

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString getArenaRecordInfo(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		List<RecordInfo> listRecord = ArenaBM.getInstance().getArenaRecordList(player.getUserId());
		for (RecordInfo record : listRecord) {
			response.addListRecord(getArenaRecord(record));
		}

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString initFightInfo(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		TableArenaData m_MyArenaData = ArenaBM.getInstance().getArenaData(player.getUserId());
		if (m_MyArenaData == null) {
			return sendFailResponse(response, "数据错误", player);
		}
		if (m_MyArenaData.getNextFightTime() > System.currentTimeMillis()) {
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

		response.setArenaData(getArenaData(enemyArenaData));
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
		String enemyUserId = request.getUserId();
		TableArenaData enemyArenaData = ArenaBM.getInstance().getArenaData(enemyUserId);
		if (enemyArenaData == null) {
			return sendFailResponse(response, ArenaConstant.ENEMY_NOT_EXIST, player);
		}
		int enemyCareer = enemyArenaData.getCareer();
		// 不是同一个职业
		if (career != enemyCareer) {
			return sendFailResponse(response, "无法挑战该名对手，请重新选择对手", player);
		}
		ArenaBM arenaBM = ArenaBM.getInstance();
		ListRanking<String, ArenaExtAttribute> ranking = arenaBM.getRanking(career);
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
		List<String> idList = request.getAtkIdListList();
		arenaBM.updateAtkHeroList(idList, userId);
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
		ListRanking<String, ArenaExtAttribute> ranking = arenaBM.getRanking(career);
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

		int enemyCareer = enemyArenaData.getCareer();
		if (career != enemyCareer) {
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

		ArenaExtAttribute enemyExt = enemyEntry.getExtension();
		// 必须交换完双方位置才能设置战斗状态
		try {
			// 挑战者(我)胜利的情况
			if (win == 1) {
				// 这里多线程情况会导致交换出错
				if (entry != null) {
					if (entry.getRanking() > enemyEntry.getRanking()) {
						if (!ranking.swap(userId, enemyUserId)) {
							if (ranking.contains(userId)) {
								return sendFailResponse(response, ArenaConstant.ENEMY_PLACE_CHANGED, player);
							}
							arenaExt = ArenaBM.getInstance().createArenaExt(player);
							try {
								ranking.replace(userId, arenaExt, enemyUserId);
							} catch (ReplacerAlreadyExistException e) {
								GameLog.error("严重错误@竞技场#replace失败：" + userId);
								return sendFailResponse(response, ArenaConstant.UNKOWN_EXCEPTION, player);
							} catch (ReplaceTargetNotExistException e) {
								return sendFailResponse(response, ArenaConstant.ENEMY_PLACE_CHANGED, player);
							}
						}
					}
					// 排名比他高，就不做处理啦
				} else {
					arenaExt = ArenaBM.getInstance().createArenaExt(player);
					try {
						entry = ranking.replace(userId, arenaExt, enemyUserId);
					} catch (ReplacerAlreadyExistException e) {
						GameLog.error("严重错误@竞技场#replace失败：" + userId);
						return sendFailResponse(response, ArenaConstant.UNKOWN_EXCEPTION, player);
					} catch (ReplaceTargetNotExistException e) {
						// 对手也被打下来了
						return sendFailResponse(response, ArenaConstant.ENEMY_PLACE_CHANGED, player);
					}
				}
				TableArenaDataDAO.getInstance().update(enemyArenaData);
				HotPointMgr.changeHotPointState(enemyUserId, EHotPointType.Arena, true);
			}

			ListRankingEntry<String, ArenaExtAttribute> newEntry = ranking.getRankingEntry(userId);
			int newPlace = newEntry.getRanking();
			long currentTime = System.currentTimeMillis();
			List<com.rwproto.ArenaServiceProtos.HurtValue> hurtValueList = request.getHurtValueList();
			int size = hurtValueList.size();
			ArrayList<HurtValueRecord> hurtValueList_ = new ArrayList<HurtValueRecord>(size);
			for (int i = 0; i < size; i++) {
				com.rwproto.ArenaServiceProtos.HurtValue value = hurtValueList.get(i);
				HurtValueRecord valueRecord = new HurtValueRecord();
				valueRecord.setCamp(value.getCamp());
				valueRecord.setDead(value.getIsDead());
				valueRecord.setHeroId(value.getHeroId());
				valueRecord.setHp(value.getHp());
				valueRecord.setSp(value.getSp());
				valueRecord.setLevel(value.getLevel());
				valueRecord.setStartlevel(value.getStartlevel());
				valueRecord.setPlayerType(value.getPlayerType());
				valueRecord.setIcon(value.getIcon());
				valueRecord.setValue(value.getValue());
				hurtValueList_.add(valueRecord);
			}
			RecordInfo record = new RecordInfo();

			int maxPlace = m_MyArenaData.getMaxPlace();
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

					//player.getFresherActivityMgr().doCheck(eActivityType.A_ArenaRank);
				}
			}
			record.setName(enemyArenaData.getName());
			record.setHeadImage(enemyArenaData.getHeadImage());
			record.setLevel(enemyArenaData.getLevel());
			record.setTime(currentTime);
			record.setChallenge(1);
			if (newPlace < maxPlace) {
				m_MyArenaData.setMaxPlace(newPlace);
				// 奖励列表
				EnumMap<eSpecialItemId, PrizeInfo> prizeList = new EnumMap<eSpecialItemId, PrizeInfo>(eSpecialItemId.class);
				ArenaUpPrizeCfgDAO prizeDAO = ArenaUpPrizeCfgDAO.getInstance();
				// TODO 迟点优化
				for (int i = maxPlace; --i >= newPlace;) {
					List<PrizeInfo> list = prizeDAO.getPrizeByRanking(i);
					for (PrizeInfo info : list) {
						eSpecialItemId type = info.getType();
						PrizeInfo oldInfo = prizeList.get(type);
						if (oldInfo == null) {
							PrizeInfo newInfo = new PrizeInfo();
							newInfo.setCount(info.getCount());
							newInfo.setType(info.getType());
							prizeList.put(type, newInfo);
						} else {
							oldInfo.setCount(info.getCount() + oldInfo.getCount());
							oldInfo.setType(info.getType());
						}
					}
				}
				StringBuilder sb = new StringBuilder();

				PrizeInfo goldPrize = prizeList.get(eSpecialItemId.Gold);
				if (goldPrize != null) {
					if (builder == null) {
						GameLog.error("竞技场排名错误：newPlace = " + newPlace + ",oldPlace = " + oldPlace + ",maxPlace = " + maxPlace);
						builder = HistoryRankingRise.newBuilder();
						builder.setCurrentRanking(newPlace);
						builder.setHistoryRanking(maxPlace);
						builder.setRankingUp(maxPlace - newPlace);
					}
					builder.setGoldAward(Math.round(goldPrize.getCount()));
				} else {
					GameLog.error("竞技场历史排名上升奖励异常,userId = " + userId + "(" + maxPlace + "-" + newPlace + ")  " + prizeList);
				}
				Collection<PrizeInfo> prizeCollection = prizeList.values();
				int prizeSize = prizeCollection.size();
				for (PrizeInfo info : prizeCollection) {
					sb.append(info.getType().getValue());
					sb.append("~");
					sb.append(Math.round(info.getCount()));
					if (--prizeSize > 0) {
						sb.append(",");
					}
				}
				EmailData emailData = new EmailData();
				EmailCfg cfg = EmailCfgDAO.getInstance().getEmailCfg(ArenaConstant.ARENA_UP_MAIL_ID);
				if (cfg != null) {
					emailData.setEmailAttachment(sb.toString());
					emailData.setTitle(cfg.getTitle());
					emailData.setContent(cfg.getContent().replace(ArenaConstant.MAIL_FIRST_PARAM, String.valueOf(maxPlace - newPlace)));
					emailData.setSender(cfg.getSender());
					emailData.setCheckIcon(cfg.getCheckIcon());
					emailData.setSubjectIcon(cfg.getSubjectIcon());
					emailData.setDeleteType(EEmailDeleteType.valueOf(cfg.getDeleteType()));
					emailData.setDelayTime(cfg.getDelayTime());
					emailData.setDeadlineTime(cfg.getDeadlineTime());
					EmailUtils.sendEmail(userId, emailData);
				} else {
					GameLog.error("竞技场上升排名获取邮件内容失败：" + userId + "," + (maxPlace - newPlace));
				}

			}
			m_MyArenaData.setWinCount(m_MyArenaData.getWinCount() + 1);
			ArenaBM.getInstance().addRecord(m_MyArenaData, record);

			RecordInfo recordForEnemy = new RecordInfo();
			recordForEnemy.setHurtList(hurtValueList_);
			recordForEnemy.setUserId(m_MyArenaData.getUserId());
			recordForEnemy.setWin(1 - win);
			if (win == 0) {
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
			ArenaBM.getInstance().addRecord(enemyArenaData, recordForEnemy);
			ArenaRecord ar = getArenaRecord(record);

			ArenaInfoCfg arenaInfoCfg = ArenaInfoCfgDAO.getInstance().getArenaInfo();
			m_MyArenaData.setNextFightTime(System.currentTimeMillis() + arenaInfoCfg.getCdTime() * 1000);
			m_MyArenaData.setRemainCount(m_MyArenaData.getRemainCount() - 1);
			TableArenaDataDAO.getInstance().update(m_MyArenaData);

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
			return response.build().toByteString();
		} finally {
			arenaExt.setNotFighting();
			enemyExt.setNotFighting();
		}
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
		if(remainCount > 0){
			response.setArenaData(getArenaData(m_MyArenaData));
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			return response.build().toByteString();
		}
		// 检查vip等级
		int vipLevel = player.getVip();
		PrivilegeCfg privilegeCfg = PrivilegeCfgDAO.getInstance().getCfg(vipLevel);
		if (privilegeCfg == null) {
			GameLog.error("arena", "buyTimes", player + "获取特权配置失败,vipLevle = " + vipLevel);
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, ArenaConstant.VIP_CONFIG_IS_NULL);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		int buyTimes = m_MyArenaData.getBuyTimes();
		if (privilegeCfg.getSportBuyCount() <= buyTimes) {
			GameLog.error("arena", "buyTimes", player + "当前vip购买次数已达上限," + buyTimes + "," + privilegeCfg.getSportBuyCount());
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, ArenaConstant.VIP_LEVEL_NOT_ENOUGHT);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		int nextBuyTimes = buyTimes + 1;
		ArenaCost arenaCostCfg = ArenaCostCfgDAO.getInstance().get(nextBuyTimes);
		if (arenaCostCfg == null) {
			GameLog.error("arena", "buyTimes", player + "获取购买次数配置失败：" + nextBuyTimes);
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
		m_MyArenaData.setNextFightTime(0);
		TableArenaDataDAO.getInstance().update(m_MyArenaData);
		response.setArenaData(getArenaData(m_MyArenaData));
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
		// 检查vip等级
		int vipLevel = player.getVip();
		PrivilegeCfg privilegeCfg = PrivilegeCfgDAO.getInstance().getCfg(vipLevel);
		if (privilegeCfg == null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, ArenaConstant.VIP_CONFIG_IS_NULL);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		// 检查vip等级是否开启该功能
		if (privilegeCfg.getArenaResetCDOpen() == 0) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, ArenaConstant.VIP_LEVEL_NOT_ENOUGHT);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		long nextFightingTime = m_MyArenaData.getNextFightTime();
		if(nextFightingTime <= 0){
			response.setArenaData(getArenaData(m_MyArenaData));
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
		m_MyArenaData.setNextFightTime(0);
		m_MyArenaData.setResetTimes(clearCDTimes);
		TableArenaDataDAO.getInstance().update(m_MyArenaData);
		response.setArenaData(getArenaData(m_MyArenaData));
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
			HeroIF hero = player.getHeroMgr().getHeroById(heroId);
			if (hero != null) {
				heroImages.add(hero.getHeroCfg().getImageId());
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
		// player.getAttrMgr()
		return result.build();
	}

	public ArenaData getArenaData(TableArenaData arenaData) {
		ArenaData.Builder data = ArenaData.newBuilder();
		String enemyId = arenaData.getUserId();
		int career = arenaData.getCareer();
		data.setUserId(enemyId);
		data.setCareer(career);
		data.setPlace(ArenaBM.getInstance().getOtherArenaPlace(enemyId, career));
		data.setMaxPlace(arenaData.getMaxPlace());
		int remainCount = arenaData.getRemainCount();
		data.setRemainCount(remainCount);
		long nextFightTime = arenaData.getNextFightTime();
		// 这里不为0的时候才设置冷却时间
		if (nextFightTime > 0 && remainCount > 0) {
			long currentTime = System.currentTimeMillis();
			if (nextFightTime > currentTime) {
				data.setCdTime(TimeUnit.MILLISECONDS.toSeconds(nextFightTime - currentTime));
			}
		}

		data.setHeadImage(arenaData.getHeadImage());
		data.setLevel(arenaData.getLevel());
		data.setName(arenaData.getName());
		int fighting = 0;
		arenaData.getHeroIdList().remove(arenaData.getUserId());
		ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(arenaData.getUserId(), arenaData.getHeroIdList());
		List<ArmyHero> armyList = armyInfo.getHeroList();
		int armySize = armyList.size();
		for (int i = 0; i < armySize; i++) {
			ArmyHero hero = armyList.get(i);
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
			GameLog.error("职业竞技场转换armyInfo异常", e);
		}

		ArmyMagic magic = armyInfo.getArmyMagic();
		data.setMagicId(magic.getModelId());
		data.setMagicLevel(magic.getLevel());

		data.setFighting(fighting);
		List<Skill> skills = armyInfo.getPlayer().getSkillList();
		for (Skill skill : skills) {
			data.addRoleSkill(transfrom(skill));
		}
		return data.build();
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

	public HeroData getHeroData(ArmyHero tableHeroData) {
		HeroData.Builder result = HeroData.newBuilder();
		RoleBaseInfo baseInfo = tableHeroData.getRoleBaseInfo();
		result.setHeroId(baseInfo.getId());
		result.setTempleteId(baseInfo.getTemplateId());
		result.setLevel(baseInfo.getLevel());
		result.setStarLevel(baseInfo.getStarLevel());
		result.setFighting(tableHeroData.getFighting());
		result.setQualityId(baseInfo.getQualityId());
		result.setExp(baseInfo.getExp());

		for (Skill skill : tableHeroData.getSkillList()) {
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
		return result.build();
	}

}
