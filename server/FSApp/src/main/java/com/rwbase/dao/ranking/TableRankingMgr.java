package com.rwbase.dao.ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.readonly.HeroIF;
import com.playerdata.readonly.ItemDataIF;
import com.playerdata.readonly.PlayerIF;
import com.rw.service.ranking.ERankingType;
import com.rwbase.dao.arena.TableArenaDataDAO;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.ranking.pojo.RankingArenaTeamData;
import com.rwbase.dao.ranking.pojo.RankingHeroData;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.ranking.pojo.RankingMagicData;
import com.rwbase.dao.ranking.pojo.RankingTeamData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;

public class TableRankingMgr {
	private static TableRankingMgr _instance;

	private String RANKING_KEY = "ranking_key";
	private TableRankingDAO rankingDao = TableRankingDAO.getInstance();
	private TableRanking tableRanking;

	public static TableRankingMgr getInstance() {
		if (_instance == null) {
			_instance = new TableRankingMgr();
			_instance.tableRanking = _instance.rankingDao.get(_instance.RANKING_KEY);
			if (_instance.tableRanking == null) {
				_instance.tableRanking = new TableRanking();
				_instance.tableRanking.setKey(_instance.RANKING_KEY);
			}
		}
		return _instance;
	}

	/** 排序时把队伍数据 写入数据库(5人小队排行数据) */
	public void saveTeamTopList(List<RankingLevelData> list) {
		// System.out.println("开始计算五人小队：   " + Calendar.getInstance().getTime().getTime());
//		Map<String, RankingTeamData> teamList = new HashMap<String, RankingTeamData>();
//		RankingTeamData teamData;
//		RankingHeroData heroData;
//		RankingMagicData magicData;
		for (RankingLevelData levelData : list) {
			RankingTeamData teamData = new RankingTeamData();
			PlayerIF player = PlayerMgr.getInstance().getReadOnlyPlayer(levelData.getUserId());
			if (player == null) {
				continue;
			}
			List<? extends HeroIF> heroList = player.getHeroMgr().getMaxFightingHeros();
			List<RankingHeroData> listHeros = new ArrayList<RankingHeroData>();

			for (HeroIF hero : heroList) {
				RankingHeroData heroData = new RankingHeroData();
				heroData.setHeroHead(hero.getHeroCfg().getBattleIcon());
				heroData.setStarLevel(hero.getHeroData().getStarLevel());
				heroData.setHeroId(hero.getHeroData().getTemplateId());
				heroData.setLevel(hero.GetHeroLevel());
				heroData.setQuality(hero.getHeroCfg().getQualityId());
				listHeros.add(heroData);
			}
			RankingMagicData magicData = new RankingMagicData();
			ItemDataIF itemData = player.getMagic();
			if (itemData != null) {
				MagicCfg cfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(itemData.getModelId() + "");
				magicData.setMagicImage(cfg.getIcon());
				magicData.setMagicQuality(cfg.getQuality());
				magicData.setMagicAttackType(cfg.getAttackType());
			}
			magicData.setMagicImage("");
			magicData.setMagicQuality(0);
			teamData.setHeroList(listHeros);
			teamData.setMagicData(magicData);
//			teamList.put(levelData.getUserId(), teamData);
		}
//		tableRanking.setFiveTeamList(teamList);
		save();
		// System.out.println("结束计算五人小队：   " + Calendar.getInstance().getTime().getTime());
	}
	
	/** 获取5人小队数据 */
	public List<RankingTeamData> getFiveTeamData(String userId) {
		RankingTeamData teamData = new RankingTeamData();
		PlayerIF player = PlayerMgr.getInstance().getReadOnlyPlayer(userId);
		if (player == null) {
			return Collections.EMPTY_LIST;
		}
		List<? extends HeroIF> heroList = player.getHeroMgr().getMaxFightingHeros();
		List<RankingHeroData> listHeros = new ArrayList<RankingHeroData>();

		for (HeroIF hero : heroList) {
			RankingHeroData heroData = new RankingHeroData();
			heroData.setHeroHead(hero.getHeroCfg().getBattleIcon());
			heroData.setStarLevel(hero.getHeroData().getStarLevel());
			heroData.setHeroId(hero.getHeroData().getTemplateId());
			heroData.setLevel(hero.GetHeroLevel());
			heroData.setQuality(hero.getHeroCfg().getQualityId());
			listHeros.add(heroData);
		}
		RankingMagicData magicData = new RankingMagicData();
		ItemDataIF itemData = player.getMagic();
		if (itemData != null) {
			MagicCfg cfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(itemData.getModelId() + "");
			magicData.setMagicImage(cfg.getIcon());
			magicData.setMagicQuality(cfg.getQuality());
			magicData.setMagicAttackType(cfg.getAttackType());
		}
		magicData.setMagicImage("");
		magicData.setMagicQuality(0);
		teamData.setHeroList(listHeros);
		teamData.setMagicData(magicData);
		
		
		List<RankingTeamData> result = new ArrayList<RankingTeamData>();
		if (tableRanking.getFiveTeamList().containsKey(userId)) {
			result.add(tableRanking.getFiveTeamList().get(userId));
		}
		return result;
	}

	/** 保存竞技队伍数据 */
	public void saveArenaTeamData(List<RankingLevelData> list, ERankingType rankType) {
		// System.out.println("开始计算竞技小队：" + rankType + "   " + Calendar.getInstance().getTime().getTime());
		Map<String, RankingArenaTeamData> teamList = new HashMap<String, RankingArenaTeamData>();
		for (RankingLevelData levelData : list) {
			TableArenaData arenaData = TableArenaDataDAO.getInstance().get(levelData.getUserId());
			if (arenaData == null) {
				continue;
			}
			RankingArenaTeamData teamData = new RankingArenaTeamData();
			teamData.setTeamData(new RankingTeamData());
			teamData.setWinCount(arenaData.getWinCount());
			List<RankingHeroData> listHeros = new ArrayList<RankingHeroData>();
			ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(arenaData.getUserId(), arenaData.getHeroIdList());

			for (ArmyHero tableHeroData : armyInfo.getHeroList()) {
				RoleBaseInfo roleBaseInfo = tableHeroData.getRoleBaseInfo();
				RoleCfg heroCfg = RoleCfgDAO.getInstance().getConfig(roleBaseInfo.getTemplateId());
				RankingHeroData rankingHeroData = new RankingHeroData();
				rankingHeroData.setHeroHead(heroCfg.getBattleIcon());
				rankingHeroData.setStarLevel(roleBaseInfo.getStarLevel());
				rankingHeroData.setHeroId(roleBaseInfo.getTemplateId());
				rankingHeroData.setLevel(roleBaseInfo.getLevel());
				rankingHeroData.setQuality(roleBaseInfo.getQualityId());
				listHeros.add(rankingHeroData);
			}
			RankingMagicData magicData = new RankingMagicData();
			MagicCfg cfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(arenaData.getMagicId() + "");
			if (cfg == null) {
				return;
			}

			magicData.setMagicImage(cfg.getIcon());
			magicData.setMagicQuality(cfg.getQuality());
			magicData.setMagicAttackType(cfg.getAttackType());

			teamData.getTeamData().setHeroList(listHeros);
			teamData.getTeamData().setMagicData(magicData);
			teamList.put(levelData.getUserId(), teamData);
		}
		tableRanking.getArenaTeamList().put(String.valueOf(rankType.getValue()), teamList);
		save();
		// System.out.println("结束计算竞技小队：" + rankType + "   " + Calendar.getInstance().getTime().getTime());
	}

	/** 获取竞技数据 */
	public List<RankingTeamData> getArenaTeamData(String userId, ERankingType rankType) {
		TableArenaData arenaData = TableArenaDataDAO.getInstance().get(userId);
		if (arenaData == null) {
			return Collections.EMPTY_LIST;
		}
		RankingArenaTeamData teamData = new RankingArenaTeamData();
		teamData.setTeamData(new RankingTeamData());
		teamData.setWinCount(arenaData.getWinCount());
		List<RankingHeroData> listHeros = new ArrayList<RankingHeroData>();
		ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(arenaData.getUserId(), arenaData.getHeroIdList());

		for (ArmyHero tableHeroData : armyInfo.getHeroList()) {
			RoleBaseInfo roleBaseInfo = tableHeroData.getRoleBaseInfo();
			RoleCfg heroCfg = RoleCfgDAO.getInstance().getConfig(roleBaseInfo.getTemplateId());
			RankingHeroData rankingHeroData = new RankingHeroData();
			rankingHeroData.setHeroHead(heroCfg.getBattleIcon());
			rankingHeroData.setStarLevel(roleBaseInfo.getStarLevel());
			rankingHeroData.setHeroId(roleBaseInfo.getTemplateId());
			rankingHeroData.setLevel(roleBaseInfo.getLevel());
			rankingHeroData.setQuality(roleBaseInfo.getQualityId());
			listHeros.add(rankingHeroData);
		}
		RankingMagicData magicData = new RankingMagicData();
		MagicCfg cfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(arenaData.getMagicId() + "");
		if (cfg == null) {
			return Collections.EMPTY_LIST;
		}

		magicData.setMagicImage(cfg.getIcon());
		magicData.setMagicQuality(cfg.getQuality());
		magicData.setMagicAttackType(cfg.getAttackType());
		teamData.getTeamData().setHeroList(listHeros);
		teamData.getTeamData().setMagicData(magicData);
		
		List<RankingTeamData> result = new ArrayList<RankingTeamData>();
		result.add(teamData.getTeamData());
		return result;
	}

	/** 获取竞技胜场次数 */
	public int getArenaTeamWinCount(String userId, ERankingType rankType) {
		String type = String.valueOf(rankType.getValue());
		if (tableRanking.getArenaTeamList().containsKey(type)) {
			if (tableRanking.getArenaTeamList().get(type).containsKey(userId)) {
				return tableRanking.getArenaTeamList().get(type).get(userId).getWinCount();
			}
		}
		return 0;
	}

	public void save() {
		rankingDao.update(tableRanking);
	}
}
