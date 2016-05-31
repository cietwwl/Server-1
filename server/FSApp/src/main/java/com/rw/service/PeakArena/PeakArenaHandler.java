package com.rw.service.PeakArena;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bm.arena.ArenaConstant;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.UserGameDataMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.service.PeakArena.datamodel.PeakArenaExtAttribute;
import com.rw.service.PeakArena.datamodel.PeakRecordInfo;
import com.rw.service.PeakArena.datamodel.TablePeakArenaData;
import com.rw.service.PeakArena.datamodel.TablePeakArenaDataDAO;
import com.rw.service.PeakArena.datamodel.TeamData;
import com.rw.service.PeakArena.datamodel.peakArenaBuyCost;
import com.rw.service.PeakArena.datamodel.peakArenaBuyCostHelper;
import com.rw.service.PeakArena.datamodel.peakArenaInfo;
import com.rw.service.PeakArena.datamodel.peakArenaInfoHelper;
import com.rw.service.PeakArena.datamodel.peakArenaResetCost;
import com.rw.service.PeakArena.datamodel.peakArenaResetCostHelper;
import com.rw.service.Privilege.IPrivilegeManager;
import com.rw.service.copy.CommonTip;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.TableAttr;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.skill.pojo.Skill;
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

		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (arenaData == null) {
			GameLog.error("巅峰竞技场", player.getUserId(), "找不到玩家竞技场数据");
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

		TablePeakArenaData arenData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (arenData == null) {
			// 这种属于异常情况
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		/*
		ArenaInfoCfg arenaInfoCfg = ArenaInfoCfgDAO.getInstance().getPeakArenaInfo();
		if (player.getUserGameDataMgr().getGold() < arenaInfoCfg.getCost()) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "钻石不足");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		player.getUserGameDataMgr().addGold(-arenaInfoCfg.getCost());*/
		
		// 重置费用改为从peakArenaCost读取，需要保存重置次数
		// 是否可重置由特权配置决定
		IPrivilegeManager pri = player.getPrivilegeMgr();
		boolean isOpen = pri.getBoolPrivilege(PeakArenaPrivilegeNames.isAllowResetPeak);
		if (!isOpen){
			player.NotifyCommonMsg(CommonTip.VIP_NOT_ENOUGH);//TODO 是否需要？
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		int nextCount = arenData.getResetCount() + 1;
		
		peakArenaResetCost cfg = peakArenaResetCostHelper.getInstance().getCfgByResetCount(nextCount);
		UserGameDataMgr userMgr = player.getUserGameDataMgr();
		if (!userMgr.isEnoughCurrency(cfg.getCoinType(), cfg.getCost())){
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "钻石不足");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		//扣费，记录重置次数，重置开始时间
		if (!userMgr.deductCurrency(cfg.getCoinType(), cfg.getCost())){
			return SetError(response, player, "钻石不足", "扣钻石失败:"+cfg.getCost());
		}
		arenData.setResetCount(nextCount);
		arenData.setFightStartTime(0);
		TablePeakArenaDataDAO.getInstance().update(arenData);

		response.setArenaData(getPeakArenaData(arenData, player));

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
		 List<ListRankingEntry<String, PeakArenaExtAttribute>> listInfo = PeakArenaBM.getInstance().SelectPeakArenaInfos(m_MyArenaData,player);
		for (ListRankingEntry<String, PeakArenaExtAttribute> entry : listInfo) {
			ArenaInfo.Builder info = ArenaInfo.newBuilder();
			String key = entry.getKey();
			TablePeakArenaData otherArenaData = PeakArenaBM.getInstance().getPeakArenaData(key);
			info.setUserId(key);
			info.setWinCount(otherArenaData.getWinCount());
			info.setFighting(otherArenaData.getFighting());
			info.setHeadImage(otherArenaData.getHeadImage());
			info.setLevel(otherArenaData.getLevel());
			info.setName(otherArenaData.getName());
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

		if (request.getReorderCount() < 3){
			return SetError(response, player, "参数错误，不足三支队伍", ",count="+request.getReorderCount());
		}
		
		if (PeakArenaBM.getInstance().switchTeam(player,request.getReorderList())) {
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
		TablePeakArenaData peakData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		List<TeamInfo> teamInfoList = request.getTeamsList();
		HeroMgr heroMgr = player.getHeroMgr();
		for (TeamInfo teamInfo : teamInfoList) {
			TeamData team = peakData.search(teamInfo.getTeamId());
			List<String> newHeroList = new ArrayList<String>();
			List<TableSkill> heroSkillList = new ArrayList<TableSkill>();
			List<TableAttr> heroAttrList = new ArrayList<TableAttr>();
			List<String> heroIdsList = teamInfo.getHeroIdsList();
			for (String id : heroIdsList) {
				Hero heroData = heroMgr.getHeroById(id);
				if (heroData == null){
					GameLog.error("巅峰竞技场", player.getUserId(), "无效佣兵ID="+id);
					continue;
				}
				newHeroList.add(id);
				TableSkill skill = heroData.getSkillMgr().getTableSkill(); 
				heroSkillList.add(skill);
				
				AttrData heroTotalAttrData = heroData.getAttrMgr().getTotalAttrData();
				TableAttr attr = new TableAttr(id, heroTotalAttrData);
				heroAttrList.add(attr);
			}
			team.setMagicId(teamInfo.getMagicId());
			team.setMagicLevel(teamInfo.getMagicLevel());
			team.setHeros(newHeroList);
			team.setHeroSkills(heroSkillList);
			team.setHeroAtrrs(heroAttrList);
		}
		TablePeakArenaDataDAO.getInstance().update(peakData);
		response.setArenaData(getPeakArenaData(peakData, player));

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	//准备挑战
	public ByteString initFightInfo(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		peakArenaInfo cfg = peakArenaInfoHelper.getInstance().getUniqueCfg();
		if (arenaData.getFightStartTime() + cfg.getCdTimeInMillSecond() > System.currentTimeMillis()){
			return sendFailRespon(player, response, ArenaConstant.COOL_DOWN);
		}
		
		//检查挑战次数：最大次数由配置的固定值+特权附加的购买次数!
		int challengeCount = arenaData.getChallengeCount();
		if (challengeCount >= cfg.getCount()+arenaData.getBuyCount()){
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

	//第一场战斗开始的时候发送
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
		ListRankingEntry<String, PeakArenaExtAttribute> entry = PeakArenaBM.getInstance().getPlayerRankEntry(player,arenaData);
		// TODO 超出排行榜容量的容错处理，让他打，赢了重新尝试加入排行榜
		if (entry == null) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		
		arenaData.setLastFightEnemy(enemyId);
		// combined transaction
		if (!enemyEntry.getExtension().setFighting()) {
			return sendFailRespon(player, response, ArenaConstant.ENEMY_IS_FIGHTING);
		}
		// 不需要设置自己的战斗状态，允许另一个玩家在我挑战别人的时候挑战我！
		//entry.getExtension().forceSetFighting();
		
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
		ListRankingEntry<String, PeakArenaExtAttribute> playerEntry = PeakArenaBM.getInstance().getPlayerRankEntry(player,arenaData);
		// TODO 超出排行榜容量的容错处理，让他打，赢了重新尝试加入排行榜
		if (playerEntry == null) {
			GameLog.error("巅峰竞技场", player.getUserId(), "玩家未入榜");
			//response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			//return response.build().toByteString();
		}
		
		String lastEnemy = arenaData.getLastFightEnemy();
		if (enemyId.equals(lastEnemy)){
			// 延长超时时间
			enemyEntry.getExtension().extendTimeOut();
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		}else{
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		}

		return response.build().toByteString();
	}
	
	//最后一场战斗结束才接收这个消息
	public ByteString fightFinish(MsgArenaRequest request, Player player) {
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

		// 重置对手
		playerArenaData.setLastFightEnemy("");
		
		// 巅峰排行榜超出上限的容错处理
		 ListRankingEntry<String, PeakArenaExtAttribute> playerEntry = PeakArenaBM.getInstance().getPlayerRankEntry(player, playerArenaData);
		if (playerEntry == null) {
			// 如果赢了在加上最低分做重新加入排行榜的尝试
			return SetError(response,player,"结算时找不到排行","");
		}
		//PeakArenaExtAttribute playerExtAttribute = playerEntry.getExtension();
		TablePeakArenaData enemyArenaData = PeakArenaBM.getInstance().getPeakArenaData(enemyUserId);
		if (enemyArenaData == null) {
			// 玩家自己的状态不用修改，而是由挑战者修改锁的状态
			//playerExtAttribute.setNotFighting();
			return SetError(response,player,"结算时找不到对手",":"+enemyUserId);
		}

		// TODO 用新的方式计算奖励
		RankingEntry<Integer, PeakArenaExtAttribute> enemyEntry = null;//ranking.getRankingEntry(enemyUserId);
		try {
			long currentTimeMillis = System.currentTimeMillis();
			int fightTime = (int) (currentTimeMillis - playerEntry.getExtension().getLastFightTime()) / 1000;
			if (win) {
				playerArenaData.setWinCount(playerArenaData.getWinCount()+1);
				//PeakArenaBM.getInstance().addScore(player, addScore);
			} else {
				enemyArenaData.setWinCount(enemyArenaData.getWinCount()+1);
				//PeakArenaBM.getInstance().addScore(player, -addScore);
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
			playerArenaData.setFightStartTime(currentTimeMillis);
			
			int challengeCount = playerArenaData.getChallengeCount() + 1;
			playerArenaData.setChallengeCount(challengeCount);
			TablePeakArenaDataDAO.getInstance().update(playerArenaData);
		
			MsgArenaResponse.Builder recordResponse = MsgArenaResponse.newBuilder();
			recordResponse.setArenaType(eArenaType.SYNC_RECORD);
			recordResponse.addListRecord(ar);
			recordResponse.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			player.SendMsg(Command.MSG_PEAK_ARENA, recordResponse.build().toByteString());

			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
			return response.build().toByteString();
		} finally {
			// 玩家自己的状态不用修改，而是由挑战者修改锁的状态
			//playerExtAttribute.setNotFighting();
			
			if (enemyEntry != null) {
				enemyEntry.getExtendedAttribute().setNotFighting();
			}
		}
	}
	
	private ByteString SetError(MsgArenaResponse.Builder response,Player player,String userTip,String logError){
		GameLog.info("巅峰竞技场", player.getUserId(), logError+userTip);
		response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		//if(StringUtils.isNotBlank(userTip)) response.setResultTip(userTip);
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
		int socre = 0;//peakArenaBM.getScore(userId);
		ArenaData.Builder data = ArenaData.newBuilder();
		data.setUserId(userId);
		data.setScore(socre);
		data.setGainScore(peakArenaBM.gainExpectCurrency(arenaData));
		PeakArenaScoreLevel scoreLevel = PeakArenaScoreLevel.getSocre(socre);
		data.setScoreLv(scoreLevel.getLevel());
		data.setGainCurrencyPerHour(scoreLevel.getGainCurrency());
		data.setPlace(place);
		data.setMaxPlace(arenaData.getMaxPlace());
		data.setWinCount(arenaData.getWinCount());
		//TODO 改为发送 challengeCount maxChallengeCount
		//data.setRemainCount(arenaData.getRemainCount());
		
		peakArenaInfo cfg = peakArenaInfoHelper.getInstance().getUniqueCfg();
		long currentTime = System.currentTimeMillis();
		long nextFightTime = arenaData.getFightStartTime()+cfg.getCdTimeInMillSecond();
		if (nextFightTime>currentTime){
			int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(nextFightTime - currentTime);
			data.setCdTime(seconds);
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
		
		data.setTempleteId(arenaData.getTempleteId());

		Player user = PlayerMgr.getInstance().find(userId);
		HeroData teamMainRole = getHeroData(user);
		// get player from userId and then set TeamInfo.player
		for (int i = 0; i < arenaData.getTeamCount(); i++) {
			TeamInfo.Builder teamBuilder = TeamInfo.newBuilder();
			TeamData team = arenaData.getTeam(i);
			teamBuilder.setTeamId(team.getTeamId());
			teamBuilder.setMagicId(team.getMagicId());
			teamBuilder.setMagicLevel(team.getMagicLevel());
			
			List<String> heroIdList = team.getHeros();
			if (heroIdList != null)
				heroIdList.remove(arenaData.getUserId());
			ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(arenaData.getUserId(), heroIdList);
			List<ArmyHero> armyList = armyInfo.getHeroList();
			for (ArmyHero hero : armyList) {
				teamBuilder.addHeros(getHeroData(hero, i));
			}
			data.addTeams(teamBuilder);
			teamBuilder.setPlayer(teamMainRole);
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

	public ByteString buyChallengeCount(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TablePeakArenaData arenData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (arenData == null) {
			// 这种属于异常情况
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		IPrivilegeManager pri = player.getPrivilegeMgr();
		int maxBuyCount = pri.getIntPrivilege(PeakArenaPrivilegeNames.peakMaxCount);
		int buyCount = arenData.getBuyCount();
		if (buyCount>maxBuyCount){
			return SetError(response, player, "超过最大购买次数", ":"+maxBuyCount);
		}
		
		//扣钱
		peakArenaBuyCost cfg = peakArenaBuyCostHelper.getInstance().getCfgByCount(buyCount+1);
		UserGameDataMgr userMgr = player.getUserGameDataMgr();
		if (!userMgr.isEnoughCurrency(cfg.getCoinType(), cfg.getCost())){
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "钻石不足");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}
		//扣费，记录重置次数，重置开始时间
		if (!userMgr.deductCurrency(cfg.getCoinType(), cfg.getCost())){
			return SetError(response, player, "钻石不足", "购买挑战次数时扣钻石失败:"+cfg.getCost());
		}
		
		//保存购买次数
		arenData.setBuyCount(buyCount+1);
		TablePeakArenaDataDAO.getInstance().update(arenData);

		response.setArenaData(getPeakArenaData(arenData, player));

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		return response.build().toByteString();
	}

	private ByteString sendFailRespon(Player player, MsgArenaResponse.Builder response, String tips) {
		player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, tips);
		response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		return response.build().toByteString();
	}

}


