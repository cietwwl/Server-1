package com.playerdata.embattle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rwproto.BattleCommon.BattleHeroPosition;

/*
 * @author HC
 * @date 2016年7月16日 上午11:37:56
 * @Description 
 */
public class EmbattlePositonHelper {

	/**
	 * 转换协议传递来的英雄站位到存储结构
	 * 
	 * @param heroPosList
	 * @return
	 */
	public static List<EmbattleHeroPosition> parseMsgHeroPos2Memery(List<BattleHeroPosition> heroPosList) {
		if (heroPosList == null || heroPosList.isEmpty()) {
			return Collections.emptyList();
		}

		int size = heroPosList.size();

		List<EmbattleHeroPosition> emHeroList = new ArrayList<EmbattleHeroPosition>(size);
		for (int i = 0; i < size; i++) {
			BattleHeroPosition heroPos = heroPosList.get(i);

			EmbattleHeroPosition pos = new EmbattleHeroPosition();
			pos.setId(heroPos.getHeroId());
			pos.setPos(heroPos.getPos());
			emHeroList.add(pos);
		}

		return emHeroList;
	}

	/**
	 * 转换Id列表为传输的站位列表
	 * 
	 * @param userId
	 * @param type
	 * @param recordKey
	 * @param heroIdList
	 * @return
	 */
	public static List<BattleHeroPosition> parseId2MsgList(String userId, int type, String recordKey, List<String> heroIdList) {
		int size = heroIdList.size();

		EmbattlePositionInfo posInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(userId, type, recordKey);

		List<BattleHeroPosition> infoList = new ArrayList<BattleHeroPosition>(size);

		int mainRoleIndex = -1;
		for (int i = 0; i < size; i++) {
			String id = heroIdList.get(i);

			int heroPos = 0;
			if (id.equals(userId)) {// 是否是主角
				mainRoleIndex = i;
			} else if (posInfo != null) {
				heroPos = posInfo.getHeroPos(id);
			} else {
				heroPos = mainRoleIndex == -1 ? i + 1 : i;
			}

			BattleHeroPosition.Builder pos = BattleHeroPosition.newBuilder();
			pos.setHeroId(id);
			pos.setPos(heroPos);

			infoList.add(pos.build());
		}

		return infoList;
	}
}