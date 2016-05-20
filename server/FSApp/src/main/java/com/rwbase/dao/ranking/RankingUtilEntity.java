package com.rwbase.dao.ranking;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.RankType;
import com.bm.rank.arena.ArenaExtAttribute;
import com.playerdata.Player;
import com.playerdata.RankingMgr;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.service.ranking.ERankingType;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.hero.pojo.RoleBaseInfoDAO;
import com.rwbase.dao.ranking.pojo.CfgRanking;
import com.rwbase.dao.ranking.pojo.RankingCommonData;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwproto.RankServiceProtos;
import com.rwproto.RankServiceProtos.RankInfo;
import com.rwproto.RankServiceProtos.RankingHeroData;
import com.rwproto.RankServiceProtos.RankingMagicData;
import com.rwproto.RankServiceProtos.RankingTeamData;

public class RankingUtilEntity {
	/** 生成数据 */
	public void initCreateCommonData(Player player, RankingLevelData toData) {
		toData.setUserId(player.getUserId());
		toData.setUserName(player.getUserName());
		toData.setLevel(player.getLevel());
		toData.setExp(player.getExp());
		toData.setFightingAll(player.getHeroMgr().getFightingAll());
		toData.setFightingTeam(player.getHeroMgr().getFightingTeam());
		toData.setUserHead(player.getHeadImage());
		toData.setModelId(player.getModelId());
		toData.setJob(player.getCareer());
		toData.setSex(player.getSex());
		toData.setCareerLevel(player.getStarLevel());
	}

	/** 每日排序后生成数据 */
	public void createLevelToCommonData(RankingLevelData data, RankingCommonData toData) {
		toData.setUserId(data.getUserId());
		toData.setUserName(data.getUserName());
		toData.setLevel(data.getLevel());
		toData.setExp(data.getExp());
		toData.setFightingAll(data.getFightingAll());
		toData.setFightingTeam(data.getFightingTeam());
		toData.setUserHead(data.getUserHead());
		// toData.setModelId(data.getModelId());
		toData.setModelId(RankingUtils.getModelId(data));
		toData.setJob(data.getJob());
		toData.setSex(data.getSex());
		toData.setCareerLevel(data.getCareerLevel());
	}

	/** 每日排序后生成数据 */
	public void createCommonToLevelData(RankingCommonData data, RankingLevelData toData) {
		toData.setUserId(data.getUserId());
		toData.setUserName(data.getUserName());
		toData.setLevel(data.getLevel());
		toData.setExp(data.getExp());
		toData.setFightingAll(data.getFightingAll());
		toData.setFightingTeam(data.getFightingTeam());
		toData.setUserHead(data.getUserHead());
		toData.setModelId(data.getModelId());
		toData.setJob(data.getJob());
		toData.setSex(data.getSex());
		toData.setCareerLevel(data.getCareerLevel());
	}

	public List<RankingLevelData> subListByLevelData(List<RankingLevelData> list, ERankingType rankType) {
		CfgRanking cfg = CfgRankingDAO.getInstance().getRankingCf(rankType.getValue());
		if (list.size() >= cfg.getRankNum()) {
			return list.subList(0, cfg.getRankNum());
		} else {
			return list;
		}
	}

	/** 生成或改变一个数据 */
	public RankingCommonData createCommonData(Player pPlayer, RankingCommonData toData) {
		toData.setUserId(pPlayer.getUserId());
		toData.setLevel(pPlayer.getLevel());
		toData.setExp(pPlayer.getExp());
		toData.setUserName(pPlayer.getUserName());
		toData.setUserHead(pPlayer.getHeadImage());
		toData.setJob(pPlayer.getCareer());
		toData.setSex(pPlayer.getSex());
		toData.setCareerLevel(pPlayer.getStarLevel());
		toData.setModelId(pPlayer.getModelId());
		toData.setFightingAll(pPlayer.getHeroMgr().getFightingAll());
		toData.setFightingTeam(pPlayer.getHeroMgr().getFightingTeam());
		return toData;
	}

	/** 获得相应生成列表 */
	public List<RankServiceProtos.RankInfo> createRankList(RankType rankType) {
		List<RankingLevelData> tableRankInfoList = RankingMgr.getInstance().getRankList(rankType);
		List<RankServiceProtos.RankInfo> rankInfoList = new ArrayList<RankServiceProtos.RankInfo>();
		for (int i = 0; i < tableRankInfoList.size(); i++) {
			RankingLevelData levelData = tableRankInfoList.get(i);
			RankInfo rankInfo = createOneRankInfo(levelData, i + 1, true);
			rankInfoList.add(rankInfo);
		}
		return rankInfoList;
	}

	public RankInfo createOneRankInfo(RankingLevelData levelData, int ranking) {
		return createOneRankInfo(levelData, ranking, false);
	}

	/** 写入一条数据 */
	public RankInfo createOneRankInfo(RankingLevelData levelData, int ranking, boolean realTime) {
		RankInfo.Builder rankInfo;
		rankInfo = RankInfo.newBuilder();
		if (levelData != null) {
			rankInfo.setHeroUUID(levelData.getUserId());
			int rankLevel = levelData.getRankLevel();
			if (realTime) {
				rankInfo.setRankingLevel(ranking);
			} else {
				rankInfo.setRankingLevel(rankLevel > 0 ? rankLevel : ranking);
			}
			rankInfo.setLevel(levelData.getLevel());
			rankInfo.setHeroName(levelData.getUserName());
			rankInfo.setImageId(levelData.getUserHead());
			rankInfo.setJob(levelData.getJob());
			rankInfo.setModelId(RankingUtils.getModelId(levelData));
			rankInfo.setFightingAll(levelData.getFightingAll());
			rankInfo.setFightingTeam(levelData.getFightingTeam());
			rankInfo.setRankCount(levelData.getRankCount());
		}
		return rankInfo.build();
	}

	/** 转换通迅数据 */
	public List<RankingTeamData> createTeamData(List<com.rwbase.dao.ranking.pojo.RankingTeamData> list) {
		RankingTeamData.Builder serviceTeamData;
		List<RankingTeamData> result = new ArrayList<RankServiceProtos.RankingTeamData>();
		for (com.rwbase.dao.ranking.pojo.RankingTeamData teamData : list) {
			serviceTeamData = RankingTeamData.newBuilder();
			serviceTeamData.addAllHeroList(createHeroDatas(teamData.getHeroList()));
			serviceTeamData.setMagicData(createMagicData(teamData.getMagicData()));
			result.add(serviceTeamData.build());
		}
		return result;
	}

	private List<RankingHeroData> createHeroDatas(List<com.rwbase.dao.ranking.pojo.RankingHeroData> list) {
		List<RankingHeroData> result = new ArrayList<RankServiceProtos.RankingHeroData>();
		for (com.rwbase.dao.ranking.pojo.RankingHeroData heroData : list) {
			result.add(createHeroData(heroData));
		}
		return result;
	}

	private RankingHeroData createHeroData(com.rwbase.dao.ranking.pojo.RankingHeroData heroData) {
		RankingHeroData.Builder serviceHeroData = RankingHeroData.newBuilder();
		serviceHeroData.setHeroId(heroData.getHeroId());
		serviceHeroData.setHeroHead(heroData.getHeroHead());
		serviceHeroData.setQuality(heroData.getQuality());
		serviceHeroData.setLevel(heroData.getLevel());
		serviceHeroData.setStarLevel(heroData.getStarLevel());
		return serviceHeroData.build();
	}

	private RankingMagicData createMagicData(com.rwbase.dao.ranking.pojo.RankingMagicData magicData) {
		RankingMagicData.Builder serviceMagicData = RankingMagicData.newBuilder();
		serviceMagicData.setMagicImage(magicData.getMagicImage());
		serviceMagicData.setMagicQuality(magicData.getMagicQuality());
		serviceMagicData.setMagicAttackType(magicData.getMagicAttackType());
		return serviceMagicData.build();
	}

	public int getModelId(RankingLevelData data) {
		int model = data.getModelId();
		if (model != 0) {
			return model;
		}
		RoleBaseInfo roleBaseInfo = RoleBaseInfoDAO.getInstance().get(data.getUserId());
		if (roleBaseInfo != null) {
			model = roleBaseInfo.getModeId();
			data.setModelId(model);
		}
		return model;
	}

	/* 通过竞技场记录创建一个排行榜实体 */
	public RankingLevelData createRankingLevelData(ListRankingEntry<String, ArenaExtAttribute> entry) {
		ArenaExtAttribute areanExt = entry.getExtension();
		RankingLevelData levelData = new RankingLevelData();
		String userId = entry.getKey();
		levelData.setUserId(userId);
		levelData.setLevel(areanExt.getLevel());
		levelData.setJob(areanExt.getCareer());
		levelData.setFightingAll(areanExt.getFighting());
		levelData.setFightingTeam(areanExt.getFightingTeam());
		int modelId = areanExt.getModelId();
		if (modelId == 0) {
			RoleBaseInfo roleBaseInfo = RoleBaseInfoDAO.getInstance().get(userId);
			if (roleBaseInfo != null) {
				modelId = roleBaseInfo.getModeId();
				areanExt.setModelId(modelId);
			}
		}
		levelData.setModelId(modelId);
		levelData.setSex(areanExt.getSex());
		levelData.setUserHead(areanExt.getHeadImage());
		levelData.setUserName(areanExt.getName());
		levelData.setArenaPlace(entry.getRanking());
		int last = areanExt.getRankLevel();
		levelData.setRankLevel(last == 0 ? entry.getRanking() : last);
		return levelData;
	}

}
