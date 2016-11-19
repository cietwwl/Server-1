package com.playerdata.fightinggrowth.fightingfunc;

import java.util.HashMap;
import java.util.List;

import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.SpriteAttachMgr;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.SpriteAttachFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.SpriteAttachFightingCfg;
import com.rwbase.dao.spriteattach.SpriteAttachItem;
import com.rwbase.dao.spriteattach.SpriteAttachRoleCfgDAO;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachRoleCfg;

public class FSGetSpriteAttachCurrentFightingOfSingleFunc implements IFunction<Hero, Integer>{

	private static FSGetSpriteAttachCurrentFightingOfSingleFunc _instance = new FSGetSpriteAttachCurrentFightingOfSingleFunc();
	private SpriteAttachFightingCfgDAO spriteAttachFightingCfgDAO;
	
	protected FSGetSpriteAttachCurrentFightingOfSingleFunc(){
		spriteAttachFightingCfgDAO = SpriteAttachFightingCfgDAO.getInstance();
	}
	
	public static FSGetSpriteAttachCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}
	
	@Override
	public Integer apply(Hero hero) {
		int fighting = 0;
		SpriteAttachRoleCfg spriteAttachRoleCfg = SpriteAttachRoleCfgDAO.getInstance().getCfgById(String.valueOf(hero.getModeId()));
		HashMap<Integer, Integer> indexMap = spriteAttachRoleCfg.getIndexMap();
		List<SpriteAttachItem> spriteAttachItemList = SpriteAttachMgr.getInstance().getSpriteAttachHolder().getSpriteAttachItemList(hero.getUUId());
		for (SpriteAttachItem spriteAttachItem : spriteAttachItemList) {
			int id = spriteAttachItem.getSpriteAttachId();
			int level = spriteAttachItem.getLevel();
			if (spriteAttachRoleCfg != null) {
				Integer iIndex = indexMap.get(id);
				if (iIndex != null) {
					int index = iIndex.intValue();
					SpriteAttachFightingCfg cfg = spriteAttachFightingCfgDAO.getByLevel(level);
					fighting += cfg.getFightingOfIndex(index);
				} else {
					GameLog.error("FSGetSpriteAttachCurrentFightingOfSingleFunc", hero.getId(), "找不到附灵id对应的索引，附灵id：" + id + "，map=" + indexMap);
				}
			}
		}
		return fighting;
	}

}
