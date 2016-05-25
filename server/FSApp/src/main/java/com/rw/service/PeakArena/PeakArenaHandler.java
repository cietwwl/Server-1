package com.rw.service.PeakArena;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.bm.arena.ArenaConstant;
import com.bm.arena.PeakArenaBM;
import com.bm.arena.PeakArenaScoreLevel;
import com.bm.rank.RankType;
import com.bm.rank.peakArena.PeakArenaExtAttribute;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.service.PeakArena.datamodel.PeakRecordInfo;
import com.rw.service.PeakArena.datamodel.TablePeakArenaData;
import com.rw.service.PeakArena.datamodel.TablePeakArenaDataDAO;
import com.rw.service.PeakArena.datamodel.TeamData;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.TableAttr;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.dao.arena.ArenaInfoCfgDAO;
import com.rwbase.dao.arena.pojo.ArenaInfoCfg;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.skill.pojo.TableSkill;
import com.rwproto.MsgDef.Command;
import com.rwproto.PeakArenaServiceProtos.ArenaData;
import com.rwproto.PeakArenaServiceProtos.ArenaInfo;
import com.rwproto.PeakArenaServiceProtos.ArenaRecord;
import com.rwproto.PeakArenaServiceProtos.HeroData;
import com.rwproto.PeakArenaServiceProtos.MsgArenaRequest;
import com.rwproto.PeakArenaServiceProtos.MsgArenaResponse;
import com.rwproto.PeakArenaServiceProtos.TeamInfo;
import com.rwproto.PeakArenaServiceProtos.eArenaResultType;
import com.rwproto.PeakArenaServiceProtos.eArenaType;
import com.rwproto.SkillServiceProtos.TagSkillData;
import com.rwproto.SyncAttriProtos.TagAttriData;

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

		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (arenaData == null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "数据错误");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		response.setArenaData(getPeakArenaData(arenaData, player));
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString gainScore(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TablePeakArenaData arenaData = TablePeakArenaDataDAO.getInstance().get(player.getUserId());
		PeakArenaBM.getInstance().gainCurrency(player,arenaData);
		response.setArenaData(getPeakArenaData(arenaData, player));
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString clearCD(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		TablePeakArenaData m_MyArenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (m_MyArenaData == null) {
			// 这种属于异常情况
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		ArenaInfoCfg arenaInfoCfg = ArenaInfoCfgDAO.getInstance().getPeakArenaInfo();
		if (player.getUserGameDataMgr().getGold() < arenaInfoCfg.getCost()) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "钻石不足");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		player.getUserGameDataMgr().addGold(-arenaInfoCfg.getCost());
		m_MyArenaData.setNextFightTime(0);
		TablePeakArenaDataDAO.getInstance().update(m_MyArenaData);

		response.setArenaData(getPeakArenaData(m_MyArenaData, player));

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString getEnemys(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		TablePeakArenaData m_MyArenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (m_MyArenaData == null) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		List<MomentRankingEntry<Integer, PeakArenaExtAttribute>> listInfo = PeakArenaBM.getInstance().SelectPeakArenaInfos(m_MyArenaData,player);
		for (MomentRankingEntry<Integer, PeakArenaExtAttribute> info : listInfo) {
			ArenaInfo.Builder result = ArenaInfo.newBuilder();
			RankingEntry<Integer, PeakArenaExtAttribute> entry = info.getEntry();
			String key = entry.getKey();
			TablePeakArenaData otherArenaData = PeakArenaBM.getInstance().getPeakArenaData(key);
			int score = entry.getComparable();
			result.setUserId(key);
			result.setScore(score);
			result.setScoreLv(PeakArenaScoreLevel.getSocre(score).getLevel());
			result.setWinCount(otherArenaData.getWinCount());
			result.setFighting(otherArenaData.getFighting());
			result.setHeadImage(otherArenaData.getHeadImage());
			result.setLevel(otherArenaData.getLevel());
			result.setName(otherArenaData.getName());
			result.setPlace(RankingFactory.getRanking(RankType.PEAK_ARENA).getRanking(key));
			response.addListInfo(result.build());
		}

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString getEnemyInfo(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getPeakArenaData(request.getUserId());
		if (arenaData == null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "数据错误");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		response.setArenaData(getPeakArenaData(arenaData, player));
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

		TablePeakArenaData m_MyArenaData = PeakArenaBM.getInstance().switchTeam(player);
		if (m_MyArenaData != null) {
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
		TablePeakArenaData m_MyArenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		Map<Integer, TeamData> teamMap = m_MyArenaData.getTeamMap();
		List<TeamInfo> teamInfoList = request.getTeamsList();
		for (TeamInfo teamInfo : teamInfoList) {
			TeamData team = teamMap.get(teamInfo.getTeamId());
			List<RoleBaseInfo> newHeroList = new ArrayList<RoleBaseInfo>();
			List<TableSkill> heroSkillList = new ArrayList<TableSkill>();
			List<TableAttr> heroAttrList = new ArrayList<TableAttr>();
			for (String id : teamInfo.getHeroIdsList()) {
				RoleBaseInfo data = player.getHeroMgr().getHeroById(id).getHeroData();
				newHeroList.add(data);
				TableSkill skill = player.getHeroMgr().getHeroById(id).getSkillMgr().getTableSkill(); 
				heroSkillList.add(skill);
				
				AttrData heroTotalAttrData = player.getHeroMgr().getHeroById(id).getAttrMgr().getTotalAttrData();
				TableAttr attr = new TableAttr(id, heroTotalAttrData);
				heroAttrList.add(attr);
			}
			team.setMagicId(teamInfo.getMagicId());
			team.setMagicLevel(teamInfo.getMagicLevel());
			team.setHeros(newHeroList);
			team.setHeroSkills(heroSkillList);
			team.setHeroAtrrs(heroAttrList);
		}
		TablePeakArenaDataDAO.getInstance().update(m_MyArenaData);
		response.setArenaData(getPeakArenaData(m_MyArenaData, player));

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString initFightInfo(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (arenaData.getNextFightTime() > System.currentTimeMillis()) {
			return sendFailRespon(player, response, ArenaConstant.COOL_DOWN);
		}
		if (arenaData.getRemainCount() <= 0) {
			return sendFailRespon(player, response, ArenaConstant.TIMES_NOT_ENOUGH);
		}
		TablePeakArenaData enemyArenaData = PeakArenaBM.getInstance().getPeakArenaData(request.getUserId());
		if (enemyArenaData == null) {
			return sendFailRespon(player, response, ArenaConstant.ENEMY_NOT_EXIST);
		}
		String enemyId = request.getUserId();
		int enemyPlace = RankingFactory.getRanking(RankType.PEAK_ARENA).getRanking(enemyId);
		if (enemyPlace <= 0) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, ArenaConstant.ENEMY_PLACE_CHANGED);
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		response.setArenaData(getPeakArenaData(enemyArenaData, enemyPlace));
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString arenaFightStart(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (arenaData == null) {
			return sendFailRespon(player, response, ArenaConstant.UNKOWN_EXCEPTION);
		}
		String userId = player.getUserId();
		Ranking<Integer, PeakArenaExtAttribute> ranking = RankingFactory.getRanking(RankType.PEAK_ARENA);
		RankingEntry<Integer, PeakArenaExtAttribute> entry = ranking.getRankingEntry(userId);
		// TODO 超出排行榜容量的容错处理，让他打，赢了重新尝试加入排行榜
		if (entry == null) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		String enemyId = request.getUserId();
		RankingEntry<Integer, PeakArenaExtAttribute> enemyEntry = ranking.getRankingEntry(enemyId);
		if (enemyEntry == null) {
			return sendFailRespon(player, response, ArenaConstant.ENEMY_NOT_EXIST);
		}
		if (!enemyEntry.getExtendedAttribute().setFighting()) {
			return sendFailRespon(player, response, ArenaConstant.ENEMY_IS_FIGHTING);
		}
		// 设置自己的战斗状态
		entry.getExtendedAttribute().forceSetFighting();
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	public ByteString arenaFightFinish(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		boolean win = request.getWin();
		String enemyUserId = request.getUserId();
		String userId = player.getUserId();

		// 从db加载数据的容错处理
		TablePeakArenaData playerArenaData = PeakArenaBM.getInstance().getPeakArenaData(userId);
		if (playerArenaData == null) {
			return SetError(response,player,"结算时找不到用户","");
		}

		Ranking<Integer, PeakArenaExtAttribute> ranking = RankingFactory.getRanking(RankType.PEAK_ARENA);
		// 巅峰排行榜超出上限的容错处理
		RankingEntry<Integer, PeakArenaExtAttribute> entry = ranking.getRankingEntry(userId);
		if (entry == null) {
			// 如果赢了在加上最低分做重新加入排行榜的尝试
			return SetError(response,player,"结算时找不到排行","");
		}
		PeakArenaExtAttribute areanExtAttribute = entry.getExtendedAttribute();
		TablePeakArenaData enemyArenaData = PeakArenaBM.getInstance().getPeakArenaData(enemyUserId);
		if (enemyArenaData == null) {
			areanExtAttribute.setNotFighting();
			return SetError(response,player,"结算时找不到对手",":"+enemyUserId);
		}

		// 计算积分
		RankingEntry<Integer, PeakArenaExtAttribute> enemyEntry = ranking.getRankingEntry(enemyUserId);
		int score = entry.getComparable();
		int enemyScore;
		if (enemyEntry != null) {
			enemyScore = enemyEntry.getComparable();
		} else {
			enemyScore = 0;
		}
		try {
			long currentTimeMillis = System.currentTimeMillis();
			int fightTime = (int) (currentTimeMillis - entry.getExtendedAttribute().getLastFightTime()) / 1000;
			int addScore;
			if (win) {
				addScore = (int) (Math.min((int) (60 + fightTime * 0.5) + (int) ((enemyScore - score) * 0.05), 180));
				PeakArenaBM.getInstance().addScore(player, addScore);
			} else {
				addScore = (int) (Math.max((int) (60 - fightTime * 0.3) - (int) ((enemyScore - score) * 0.05), 0));
				PeakArenaBM.getInstance().addScore(player, -addScore);
			}
			PeakRecordInfo record = new PeakRecordInfo();
			record.setUserId(enemyUserId);
			record.setWin(win?1:0);
			record.setName(enemyArenaData.getName());
			record.setHeadImage(enemyArenaData.getHeadImage());
			record.setLevel(enemyArenaData.getLevel());
			record.setTime(currentTimeMillis);
			record.setChallenge(1);
			// TODO 需要了解双方录像的差异
			PeakArenaBM.getInstance().addOthersRecord(playerArenaData, record);
			PeakRecordInfo recordForEnemy = new PeakRecordInfo();
			recordForEnemy.setUserId(userId);
			recordForEnemy.setWin(win?0:1);//取反
			recordForEnemy.setName(playerArenaData.getName());
			recordForEnemy.setHeadImage(playerArenaData.getHeadImage());
			recordForEnemy.setLevel(playerArenaData.getLevel());
			recordForEnemy.setTime(currentTimeMillis);
			recordForEnemy.setChallenge(0);
			PeakArenaBM.getInstance().addOthersRecord(enemyArenaData, recordForEnemy);
			ArenaRecord ar = getPeakArenaRecord(record);
			ArenaInfoCfg arenaInfoCfg = ArenaInfoCfgDAO.getInstance().getPeakArenaInfo();
			playerArenaData.setNextFightTime(System.currentTimeMillis() + arenaInfoCfg.getCdTime() * 1000);
			int remainCount = playerArenaData.getRemainCount() - 1;
			playerArenaData.setRemainCount(remainCount > 0 ? remainCount : 0);
			TablePeakArenaDataDAO.getInstance().update(playerArenaData);
		
			MsgArenaResponse.Builder recordResponse = MsgArenaResponse.newBuilder();
			recordResponse.setArenaType(eArenaType.SYNC_RECORD);
			recordResponse.addListRecord(ar);
			recordResponse.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			player.SendMsg(Command.MSG_PEAK_ARENA, recordResponse.build().toByteString());

			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			return response.build().toByteString();
		} finally {
			areanExtAttribute.setNotFighting();
			if (enemyEntry != null) {
				enemyEntry.getExtendedAttribute().setNotFighting();
			}
		}
	}
	
	private ByteString SetError(MsgArenaResponse.Builder response,Player player,String userTip,String logError){
		GameLog.error("巅峰竞技场", player.getUserId(), logError+userTip);
		response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		//if(StringUtils.isNotBlank(userTip)) response.setResultTip(userTip);
		return response.build().toByteString();
	}

	public HeroData getHeroData(RoleBaseInfo tableHeroData, int teamId) {
//		@不再使用该方法，统一用amryInfo返回队伍信息 有问题联系allen
//		HeroData.Builder result = HeroData.newBuilder();
//		result.setHeroId(tableHeroData.getId());
//		result.setTempleteId(tableHeroData.getTemplateId());
//		result.setLevel(tableHeroData.getLevel());
//		result.setStarLevel(tableHeroData.getStarLevel());
//		//fighting 要直接从hero获取
////		result.setFighting(tableHeroData.getFighting());
//		result.setQualityId(tableHeroData.getQualityId());
//		result.setExp(tableHeroData.getExp());
//		result.setTeamId(teamId);
//		Hero hero = new Hero(null);
//		hero.getRoleBaseInfoMgr().initRoleBase(tableHeroData);
//		
//		List<TagSkillData> skills = hero.getSkillMgr().getSkillProtoList();
//		for (TagSkillData skill : skills) {
//			result.addSkills(skill);
//		}
//		List<TagAttriData> attrs = hero.getAttrMgr().getAttrList();
//		for (TagAttriData attr : attrs) {
//			result.addAttrs(attr);
//		}
//
//		return result.build();
		return null;
	}

	public ArenaData getPeakArenaData(TablePeakArenaData arenaData, Player player) {
		return getPeakArenaData(arenaData, PeakArenaBM.getInstance().getPlace(player));
	}

	public ArenaData getPeakArenaData(TablePeakArenaData arenaData, int place) {
		PeakArenaBM peakArenaBM = PeakArenaBM.getInstance();
		String userId = arenaData.getUserId();
		int socre = peakArenaBM.getScore(userId);
		ArenaData.Builder data = ArenaData.newBuilder();
		data.setUserId(userId);
		data.setScore(socre);
		data.setGainScore(peakArenaBM.gainExpectCurrency(arenaData));
		PeakArenaScoreLevel scoreLevel = PeakArenaScoreLevel.getSocre(socre);
		data.setScoreLv(scoreLevel.getLevel());
		data.setGainCurrencyPerHour(scoreLevel.getGainCurrency());
		data.setPlace(place);
		data.setMaxPlace(arenaData.getMaxPlace());
		data.setWinningStreak(arenaData.getWinningStreak());
		data.setWinCount(arenaData.getWinCount());
		//TODO 改为发送 challengeCount maxChallengeCount
		//data.setRemainCount(arenaData.getRemainCount());
		
		long nextFightTime = arenaData.getNextFightTime();
		// 这里不为0的时候才设置冷却时间
		if (nextFightTime > 0) {
			long currentTime = System.currentTimeMillis();
			if (nextFightTime > currentTime) {
				int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(nextFightTime - currentTime);
				data.setCdTime(seconds);
			}
		}
		data.setCareer(arenaData.getCareer());
		data.setHeadImage(arenaData.getHeadImage());
		data.setLevel(arenaData.getLevel());
		data.setFighting(arenaData.getFighting());
		data.setName(arenaData.getName());

		PlayerIF role = PlayerMgr.getInstance().getReadOnlyPlayer(arenaData.getUserId());

		List<TagSkillData> skills = role.getSkillMgr().getSkillProtoList();
		for (TagSkillData skill : skills) {
			data.addRoleSkill(skill);
		}
		List<TagAttriData> attrs = role.getAttrMgr().getAttrList();
		for (TagAttriData attr : attrs) {
			if (attr.getAttValue() == 0) {
				System.out.println("attrId:" + attr.getAttrId() + ",attrValue:" + attr.getAttValue());
			}
			data.addRoleAttr(attr);
		}
		data.setTempleteId(arenaData.getTempleteId());

		for (int i = 1; i <= 3; i++) {
			TeamInfo.Builder teamBuilder = TeamInfo.newBuilder();
			teamBuilder.setTeamId(i);
			TeamData team = arenaData.getTeamMap().get(i);
			teamBuilder.setMagicId(team.getMagicId());
			teamBuilder.setMagicLevel(team.getMagicLevel());
			List<RoleBaseInfo> heros = team.getHeros();
			for (RoleBaseInfo hero : heros) {
				teamBuilder.addHeros(getHeroData(hero, i));
			}
			data.addTeams(teamBuilder);
		}
		return data.build();
	}

	public ArenaRecord getPeakArenaRecord(PeakRecordInfo record) {
		ArenaRecord.Builder result = ArenaRecord.newBuilder();
		result.setUserId(record.getUserId());
		result.setWin(record.getWin()==1);
		result.setPlaceUp(record.getPlaceUp());
		result.setName(record.getName());
		result.setHeadImage(record.getHeadImage());
		result.setLevel(record.getLevel());
		result.setTime(record.getTime());
		result.setChallenge(record.getChallenge());
		return result.build();
	}

	private ByteString sendFailRespon(Player player, MsgArenaResponse.Builder response, String tips) {
		player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, tips);
		response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		return response.build().toByteString();
	}
	
}


