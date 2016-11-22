package com.rwbase.dao.ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bm.arena.ArenaBM;
import com.bm.rank.RankType;
import com.bm.rank.arena.ArenaExtAttribute;
import com.common.RefInt;
import com.playerdata.Player;
import com.playerdata.RankingMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.ranking.impl.RankingEntryData;
import com.rw.service.fashion.FashionHandle;
import com.rw.service.ranking.ERankingType;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.hero.pojo.RoleBaseInfoDAO;
import com.rwbase.dao.hero.pojo.RoleBaseInfoIF;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.praise.PraiseHelper;
import com.rwbase.dao.ranking.pojo.CfgRanking;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.setting.HeadBoxCfgDAO;
import com.rwbase.dao.setting.pojo.HeadBoxType;
import com.rwproto.FashionServiceProtos.FashionUsed;
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
		// toData.setFightingAll(player.getHeroMgr().getFightingAll());
		// toData.setFightingTeam(player.getHeroMgr().getFightingTeam());
		toData.setFightingAll(player.getHeroMgr().getFightingAll(player));
		toData.setFightingTeam(player.getHeroMgr().getFightingTeam(player));
		toData.setUserHead(player.getHeadImage());
		toData.setHeadbox(player.getHeadFrame());
		toData.setModelId(player.getModelId());
		toData.setJob(player.getCareer());
		toData.setSex(player.getSex());
		toData.setCareerLevel(player.getStarLevel());
		toData.setVip(player.getVip());
		toData.setMagicCfgId(player.getMagic().getModelId());
	}

	public List<RankingLevelData> subListByLevelData(List<RankingLevelData> list, ERankingType rankType) {
		CfgRanking cfg = CfgRankingDAO.getInstance().getRankingCf(rankType.getValue());
		if (list.size() >= cfg.getRankNum()) {
			return list.subList(0, cfg.getRankNum());
		} else {
			return list;
		}
	}

	/** 获得相应生成列表 */
	public List<RankServiceProtos.RankInfo> createRankList(RankType rankType) {
		List<RankingLevelData> tableRankInfoList = RankingMgr.getInstance().getRankList(rankType);
		List<RankServiceProtos.RankInfo> rankInfoList = new ArrayList<RankServiceProtos.RankInfo>();
		for (int i = 0; i < tableRankInfoList.size(); i++) {
			RankingLevelData levelData = tableRankInfoList.get(i);
			RankInfo rankInfo = createOneRankInfo(levelData, i + 1, true, rankType);
			rankInfoList.add(rankInfo);
		}
		return rankInfoList;
	}

	public RankInfo createOneRankInfo(RankingLevelData levelData, int ranking) {
		return createOneRankInfo(levelData, ranking, false, null);
	}

	/** 写入一条数据 */
	public RankInfo createOneRankInfo(RankingLevelData levelData, int ranking, boolean realTime, RankType type) {
		RankInfo.Builder rankInfo;
		rankInfo = RankInfo.newBuilder();
		if (levelData != null) {
			String userId = levelData.getUserId();
			rankInfo.setHeroUUID(userId);
			int rankLevel = levelData.getRankLevel();
			if (realTime) {
				rankInfo.setRankingLevel(ranking);
			} else {
				rankInfo.setRankingLevel(rankLevel > 0 ? rankLevel : ranking);
			}
			rankInfo.setVip(levelData.getVip());
			rankInfo.setLevel(levelData.getLevel());
			rankInfo.setHeroName(levelData.getUserName());
			rankInfo.setImageId(levelData.getUserHead());
			rankInfo.setJob(levelData.getJob());
			rankInfo.setModelId(RankingUtils.getModelId(levelData));
			rankInfo.setFightingAll(levelData.getFightingAll());
			rankInfo.setFightingTeam(levelData.getFightingTeam());
			if (levelData.getMagicCfgId() > 0) {
				rankInfo.setMagicId(levelData.getMagicCfgId());
			} else {
				rankInfo.setMagicId(603601);
			}
			boolean setFromLastRank = false;
			if (type != null) {
				RankType dailyType = RankingMgr.getInstance().getDailyRankType(type);
				if (dailyType != null) {
					Ranking dailyRanking = RankingFactory.getRanking(dailyType);
					int lastRank = dailyRanking.getRanking(userId);
					if (lastRank > 0) {
						rankInfo.setRankCount(lastRank - ranking);
						setFromLastRank = true;
					}
				}

				// 人气值
				if (type == RankType.POPULARITY_RANK) {
					rankInfo.setPopularity(PraiseHelper.getInstance().getPopularityByUserId(userId));
				}
			}
			if (!setFromLastRank) {
				rankInfo.setRankCount(levelData.getRankCount());
			}
			if (levelData.getHeadbox() == null) {
				List<String> defaultHeadBoxList = HeadBoxCfgDAO.getInstance().getHeadBoxByType(HeadBoxType.HEADBOX_DEFAULT);
				levelData.setHeadbox(defaultHeadBoxList.get(0));
			}
			rankInfo.setHeadbox(levelData.getHeadbox());
			// 设置时装数据
			rankInfo.setSex(levelData.getSex());
			FashionUsed.Builder fashionUsing = FashionHandle.getInstance().getFashionUsedProto(levelData.getUserId());
			if (fashionUsing != null) {
				rankInfo.setFashionUsage(fashionUsing);
			}
		}
		return rankInfo.build();
	}
	
	/** 写入一条数据 */
	public RankInfo createOneRankInfo(ListRankingEntry<String, ArenaExtAttribute> entry, int ranking, boolean realTime, RankType type) {
		RankInfo.Builder rankInfo;
		rankInfo = RankInfo.newBuilder();
		if (entry != null) {
			String userId = entry.getKey();
			ArenaExtAttribute attr = entry.getExtension();
			rankInfo.setHeroUUID(userId);
			int rankLevel = attr.getRankLevel();
			if (realTime) {
				rankInfo.setRankingLevel(ranking);
			} else {
				rankInfo.setRankingLevel(rankLevel > 0 ? rankLevel : ranking);
			}
			rankInfo.setVip(attr.getVip());
			rankInfo.setLevel(attr.getLevel());
			rankInfo.setHeroName(attr.getName());
			rankInfo.setImageId(attr.getHeadImage());
			rankInfo.setJob(attr.getCareer());
			rankInfo.setModelId(attr.getModelId());
			rankInfo.setFightingAll(attr.getFighting());
			rankInfo.setFightingTeam(attr.getFightingTeam());
			if (attr.getMagicCfgId() > 0) {
				rankInfo.setMagicId(attr.getMagicCfgId());
			} else {
				rankInfo.setMagicId(603601);
			}
			boolean setFromLastRank = false;
			if (type != null) {
				RankType dailyType = RankingMgr.getInstance().getDailyRankType(type);
				if (dailyType != null) {
					Ranking dailyRanking = RankingFactory.getRanking(dailyType);
					int lastRank = dailyRanking.getRanking(userId);
					if (lastRank > 0) {
						rankInfo.setRankCount(lastRank - ranking);
						setFromLastRank = true;
					}
				}
				// 人气值
				if (type == RankType.POPULARITY_RANK) {
					rankInfo.setPopularity(PraiseHelper.getInstance().getPopularityByUserId(userId));
				}
			}
			if (!setFromLastRank) {
				rankInfo.setRankCount(attr.getRankCount());
			}
			if(null != attr.getHeadbox()){
				rankInfo.setHeadbox(attr.getHeadbox());
			}
			// 设置时装数据
			rankInfo.setSex(attr.getSex());
			FashionUsed.Builder fashionUsing = FashionHandle.getInstance().getFashionUsedProto(userId);
			if (fashionUsing != null) {
				rankInfo.setFashionUsage(fashionUsing);
			}
		}
		return rankInfo.build();
	}

	@SuppressWarnings("unchecked")
	public List<RankingTeamData> createTeamData(ERankingType rankType, String userId, RefInt refInt) {
		TableArenaData arenaData = ArenaBM.getInstance().getArenaData(userId);
		if (arenaData == null) {
			return Collections.EMPTY_LIST;
		}
		RankingTeamData.Builder rankingTeamData = RankingTeamData.newBuilder();
		List<RankingHeroData> listHeros = new ArrayList<RankingHeroData>();
		ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(arenaData.getUserId(), arenaData.getHeroIdList());
		int fighting = 0;
		for (ArmyHero tableHeroData : armyInfo.getHeroList()) {
			RoleBaseInfoIF roleBaseInfo = tableHeroData.getRoleBaseInfo();
			RoleCfg heroCfg = RoleCfgDAO.getInstance().getConfig(roleBaseInfo.getTemplateId());
			RankingHeroData.Builder rankingHeroData = RankingHeroData.newBuilder();
			rankingHeroData.setHeroHead(heroCfg.getBattleIcon());
			rankingHeroData.setStarLevel(roleBaseInfo.getStarLevel());
			rankingHeroData.setHeroId(roleBaseInfo.getTemplateId());
			rankingHeroData.setLevel(roleBaseInfo.getLevel());
			rankingHeroData.setQuality(roleBaseInfo.getQualityId());
			listHeros.add(rankingHeroData.build());
			fighting += tableHeroData.getFighting();
		}
		fighting += armyInfo.getPlayer().getFighting();
		if (refInt != null) {
			refInt.value = fighting;
		}
		RankingMagicData.Builder magicData = RankingMagicData.newBuilder();
		MagicCfg cfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(arenaData.getMagicId() + "");
		// TODO 按原逻辑，没有法宝不返回队伍信息?
		if (cfg == null) {
			return Collections.EMPTY_LIST;
		}
		magicData.setMagicImage(cfg.getIcon());
		magicData.setMagicQuality(cfg.getQuality());
		magicData.setMagicAttackType(cfg.getAttackType());
		magicData.setMagicLevel(arenaData.getMagicLevel());
		rankingTeamData.addAllHeroList(listHeros);
		rankingTeamData.setMagicData(magicData.build());
		ArrayList<RankingTeamData> result = new ArrayList<RankingTeamData>(1);
		result.add(rankingTeamData.build());
		return result;
	}

	public int getModelId(RankingLevelData data) {
		int model = data.getModelId();
		if (model != 0) {
			return model;
		}
		RoleBaseInfoIF roleBaseInfo = RoleBaseInfoDAO.getInstance().get(data.getUserId());
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
			RoleBaseInfoIF roleBaseInfo = RoleBaseInfoDAO.getInstance().get(userId);
			if (roleBaseInfo != null) {
				modelId = roleBaseInfo.getModeId();
				areanExt.setModelId(modelId);
			}
		}
		levelData.setModelId(modelId);
		levelData.setSex(areanExt.getSex());
		levelData.setVip(areanExt.getVip());
		levelData.setUserHead(areanExt.getHeadImage());
		levelData.setHeadbox(areanExt.getHeadbox());
		levelData.setUserName(areanExt.getName());
		levelData.setArenaPlace(entry.getRanking());
		int last = areanExt.getRankLevel();
		levelData.setRankLevel(last == 0 ? entry.getRanking() : last);
		levelData.setMagicCfgId(areanExt.getMagicCfgId());
		return levelData;
	}
	
	/* 通过竞技场记录创建一个排行榜实体 */
	public RankingLevelData createRankingLevelDataByEntryData(RankingEntryData entry) {
		RankingLevelData levelData = new RankingLevelData();
		String userId = entry.getKey();
		levelData.setUserId(userId);
		levelData.setLevel(Integer.parseInt(entry.getCondition()));
		
		return levelData;
	}

}
