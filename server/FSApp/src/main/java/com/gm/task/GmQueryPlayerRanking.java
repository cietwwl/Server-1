package com.gm.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.rank.RankType;
import com.bm.rank.magicsecret.MagicSecretComparable;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.playerdata.mgcsecret.data.MSScoreDataItem;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.dao.ranking.RankingUtils;
import com.rwproto.RankServiceProtos.RankInfo;

public class GmQueryPlayerRanking implements IGmTask {

	static HashMap<Integer, RankType> RankTypeMap = new HashMap<Integer, RankType>();

	static {
		RankTypeMap.put(1, RankType.WARRIOR_ARENA);
		RankTypeMap.put(2, RankType.SWORDMAN_ARENA);
		RankTypeMap.put(3, RankType.MAGICAN_ARENA);
		RankTypeMap.put(4, RankType.PRIEST_ARENA);
		RankTypeMap.put(5, RankType.FIGHTING_ALL);
		RankTypeMap.put(6, RankType.TEAM_FIGHTING);
		RankTypeMap.put(7, RankType.LEVEL_ALL);
		RankTypeMap.put(8, RankType.MAGIC_SECRET_SCORE_RANK);
	}

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try {
			Map<String, Object> args = request.getArgs();
			int type = GmUtils.parseInt(args, "type");
			RankType rankType = RankTypeMap.get(type);

			switch (rankType) {
			case WARRIOR_ARENA:
			case SWORDMAN_ARENA:
			case MAGICAN_ARENA:
			case PRIEST_ARENA:
			case LEVEL_ALL:
			case FIGHTING_ALL:
			case TEAM_FIGHTING:
				processCommonRank(rankType, response);
				break;
			case MAGIC_SECRET_SCORE_RANK:
				processMagicSecretScoreRank(rankType, response);
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}
	
	private void processCommonRank(RankType rankType, GmResponse response){
		List<RankInfo> rankList = RankingUtils.createRankList(rankType);
		for (RankInfo rankInfo : rankList) {

			int rank = rankInfo.getRankingLevel();
			String userId = rankInfo.getHeroUUID();
			String userName = rankInfo.getHeroName();
			int value = rankInfo.getRankingLevel();

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("rank", rank);
			map.put("roleId", userId);
			map.put("roleName", userName);
			map.put("value", value);

			response.addResult(map);
		}
	}

	private void processMagicSecretScoreRank(RankType rankType, GmResponse response) {
		Ranking<MagicSecretComparable, MSScoreDataItem> ranking = RankingFactory.getRanking(RankType.MAGIC_SECRET_SCORE_RANK);

		EnumerateList<? extends MomentRankingEntry<MagicSecretComparable, MSScoreDataItem>> it = ranking.getEntriesEnumeration(1, MagicSecretMgr.MS_RANK_FETCH_COUNT);
		int rank = 1;
		while (it.hasMoreElements()) {
			MomentRankingEntry<MagicSecretComparable, MSScoreDataItem> nextElement = it.nextElement();
			RankingEntry<MagicSecretComparable, MSScoreDataItem> entry = nextElement.getEntry();
			MSScoreDataItem extendedAttribute = entry.getExtendedAttribute();
			String userId = extendedAttribute.getUserId();
			String userName = extendedAttribute.getUserName();
			int value = extendedAttribute.getTotalScore();

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("rank", rank);
			map.put("roleId", userId);
			map.put("roleName", userName);
			map.put("value", value);

			response.addResult(map);
			rank++;
		}

		response.setStatus(0);
		response.setCount(rank - 1);
	}
}