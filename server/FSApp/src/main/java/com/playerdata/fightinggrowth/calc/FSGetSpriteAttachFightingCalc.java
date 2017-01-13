package com.playerdata.fightinggrowth.calc;

import java.util.HashMap;
import java.util.List;

import com.log.GameLog;
import com.rwbase.common.attribute.param.SpriteAttachParam;
import com.rwbase.dao.fighting.SpriteAttachFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.SpriteAttachFightingCfg;
import com.rwbase.dao.spriteattach.SpriteAttachItem;
import com.rwbase.dao.spriteattach.SpriteAttachRoleCfgDAO;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachRoleCfg;

/**
 * @Author HC
 * @date 2016年12月9日 下午12:19:10
 * @desc
 **/

public class FSGetSpriteAttachFightingCalc implements IFightingCalc {

	private SpriteAttachFightingCfgDAO spriteAttachFightingCfgDAO;

	protected FSGetSpriteAttachFightingCalc() {
		spriteAttachFightingCfgDAO = SpriteAttachFightingCfgDAO.getInstance();
	}

	@Override
	public int calc(Object param) {
		SpriteAttachParam saParam = (SpriteAttachParam) param;
		if (saParam == null) {
			return 0;
		}

		int fighting = 0;
		List<SpriteAttachItem> spriteAttachItemList = saParam.getItems();
		if (spriteAttachItemList == null || spriteAttachItemList.isEmpty()) {
			return 0;
		}

		String heroId = saParam.getHeroId();
		SpriteAttachRoleCfg spriteAttachRoleCfg = SpriteAttachRoleCfgDAO.getInstance().getCfgById(String.valueOf(heroId));
		if (spriteAttachRoleCfg == null) {
			return 0;
		}

		HashMap<Integer, Integer> indexMap = spriteAttachRoleCfg.getIndexMap();
		if (indexMap == null || indexMap.isEmpty()) {
			return 0;
		}

		for (int i = 0, size = spriteAttachItemList.size(); i < size; i++) {
			SpriteAttachItem spriteAttachItem = spriteAttachItemList.get(i);
			int id = spriteAttachItem.getSpriteAttachId();
			int level = spriteAttachItem.getLevel();
			Integer iIndex = indexMap.get(id);

			if (iIndex == null) {
				GameLog.error("FSGetSpriteAttachCurrentFightingOfSingleFunc", heroId, "找不到附灵id对应的索引，附灵id：" + id + "，map=" + indexMap);
				continue;
			}

			int index = iIndex.intValue();
			SpriteAttachFightingCfg cfg = spriteAttachFightingCfgDAO.getByLevel(level);
			if (cfg == null) {
				continue;
			}

			fighting += cfg.getFightingOfIndex(index);
		}

		// System.err.println("附灵----------" + fighting);
		return fighting;
	}
}