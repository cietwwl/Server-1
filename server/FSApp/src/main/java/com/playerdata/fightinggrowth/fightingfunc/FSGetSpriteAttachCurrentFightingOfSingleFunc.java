package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.SpriteAttachMgr;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.SpriteAttachFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.SpriteAttachFightingCfg;
import com.rwbase.dao.spriteattach.SpriteAttachItem;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachRoleCfg;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachRoleCfgDAO;

public class FSGetSpriteAttachCurrentFightingOfSingleFunc implements IFunction<Hero, Integer>{

	private static final FSGetSpriteAttachCurrentFightingOfSingleFunc _instance = new FSGetSpriteAttachCurrentFightingOfSingleFunc();
	private SpriteAttachFightingCfgDAO spriteAttachFightingCfgDAO;
	
	protected FSGetSpriteAttachCurrentFightingOfSingleFunc(){
		spriteAttachFightingCfgDAO = SpriteAttachFightingCfgDAO.getInstance();
	}
	
	public static final FSGetSpriteAttachCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}
	
	@Override
	public Integer apply(Hero hero) {
		int fighting = 0;
		SpriteAttachRoleCfg spriteAttachRoleCfg = SpriteAttachRoleCfgDAO.getInstance().getCfgById(String.valueOf(hero.getModeId()));
		List<SpriteAttachItem> spriteAttachItemList = SpriteAttachMgr.getInstance().getSpriteAttachHolder().getSpriteAttachItemList(hero.getUUId());
		for (SpriteAttachItem spriteAttachItem : spriteAttachItemList) {
			int id = spriteAttachItem.getId();
			int level = spriteAttachItem.getLevel();
			if (spriteAttachRoleCfg != null) {
				int index = spriteAttachRoleCfg.getIndex(id);
				SpriteAttachFightingCfg cfg = spriteAttachFightingCfgDAO.getByLevel(level);
				fighting += cfg.getFightingOfIndex(index);
			}
		}
		return fighting;
	}

}
