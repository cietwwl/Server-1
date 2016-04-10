package com.gm.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.group.GroupBM;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.log.GameLog;
import com.rw.fsutil.json.JSONArray;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class GmGetRankList implements IGmTask {

	public enum RankInfoType {
		FIGHTING, LEVEL, PRIEST_ARENA, SWORDMAN_ARENA, WARRIOR_ARENA, MAGICAN_ARENA, GROUP;
	}

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();

			String rankInfoTypeStr = GmUtils.parseString(request.getArgs(), "rankInfoType");
			int offset = GmUtils.parseInt(request.getArgs(), "offset");
			int limit = GmUtils.parseInt(request.getArgs(), "limit");

			RankInfoType rankInfoType = Enum.valueOf(RankInfoType.class, rankInfoTypeStr);
			List<String> idList = new ArrayList<String>();

			switch (rankInfoType) {
			case FIGHTING:
				idList = getFightingList(offset, limit);
				break;
			case PRIEST_ARENA:
				idList = getPrestArenaList(offset, limit);
				break;
			case WARRIOR_ARENA:
				idList = getWarriorArenaList(offset, limit);
				break;
			case MAGICAN_ARENA:
				idList = getMagicanArenaList(offset, limit);
				break;
			case SWORDMAN_ARENA:
				idList = getSwordmanArenaList(offset, limit);
				break;
			case GROUP:
				idList = getGroupList(offset, limit);
				break;
			case LEVEL:
				idList = getLevelList(offset, limit);
				break;
			default:
				break;
			}

			response.setStatus(0);
			response.setCount(1);

			resultMap.put("idList", idList);
			response.addResult(resultMap);
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

	private List<String> getSwordmanArenaList(int offset, int limit) {
		return getRankList(GameWorldKey.SWORDMAN_ARENA, offset, limit);
	}

	private List<String> getMagicanArenaList(int offset, int limit) {
		return getRankList(GameWorldKey.MAGICAN_ARENA, offset, limit);
	}

	private List<String> getWarriorArenaList(int offset, int limit) {
		return getRankList(GameWorldKey.WARRIOR_ARENA, offset, limit);
	}

	private List<String> getLevelList(int offset, int limit) {
		return getRankList(GameWorldKey.LEVEL, offset, limit);
	}

	private List<String> getGroupList(int offset, int limit) {
		List<String> leaderIdList = new ArrayList<String>();
		List<String> groupList = getRankList(GameWorldKey.GROUP, offset, limit);

		for (String groupId : groupList) {
			Group group = GroupBM.get(groupId);
			if (group != null) {
				GroupMemberDataIF groupLeader = group.getGroupMemberMgr().getGroupLeader();
				if (groupLeader != null) {
					leaderIdList.add(groupLeader.getUserId());
				}
			}
		}
		return leaderIdList;
	}

	private List<String> getPrestArenaList(int offset, int limit) {
		return getRankList(GameWorldKey.PRIEST_ARENA, offset, limit);
	}

	private List<String> getFightingList(int offset, int limit) {

		return getRankList(GameWorldKey.FIGHTING, offset, limit);
	}

	private List<String> getRankList(GameWorldKey rankType, int offset, int limit) {
		String dbString = GameWorldFactory.getGameWorld().getAttribute(rankType);
		ArrayList<String> list = new ArrayList<String>();
		try {
			JSONArray array = new JSONArray(dbString);
			int len = array.length();
			if(offset > len){
				GameLog.error("GmGetRankingList", "#getRankList()", "获取排行榜活动记录异常：offset="+offset+",limit="+limit+",len="+len+",rankType="+rankType);
				return list;
			}
			len = Math.min(limit, len);
			for (int i = offset; i < len; i++) {
				list.add(array.getString(i));
			}
		} catch (Exception e) {
			GameLog.error("GmGetRankingList", "#getRankList()", "获取排行榜活动记录异常",e);
		}
		return list;
	}

}
