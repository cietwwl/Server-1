package com.rwbase.dao.spriteattach;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.rw.dataaccess.hero.HeroCreateParam;
import com.rw.dataaccess.hero.HeroExtPropertyCreator;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachRoleCfg;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachRoleCfgDAO;

public class SpriteAttachSynCreator implements HeroExtPropertyCreator<SpriteAttachSyn>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return eOpenLevelType.Sprite_Attach;
	}

	@Override
	public boolean requiredToPreload(HeroCreateParam params) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<SpriteAttachSyn> firstCreate(HeroCreateParam params) {
		// TODO Auto-generated method stub
		String heroId = params.getHeroId();
		String userId = params.getUserId();
		SpriteAttachRoleCfg spriteAttachRoleCfg = SpriteAttachRoleCfgDAO.getInstance().getCfgById(heroId);
		if (spriteAttachRoleCfg != null) {
			List<SpriteAttachItem> list = new ArrayList<SpriteAttachItem>();
			int spriteItem1 = spriteAttachRoleCfg.getSpriteItem1();
			craeteSpriteAttach(spriteItem1, list);
			int spriteItem2 = spriteAttachRoleCfg.getSpriteItem2();
			craeteSpriteAttach(spriteItem2, list);
			int spriteItem3 = spriteAttachRoleCfg.getSpriteItem3();
			craeteSpriteAttach(spriteItem3, list);
			int spriteItem4 = spriteAttachRoleCfg.getSpriteItem4();
			craeteSpriteAttach(spriteItem4, list);
			int spriteItem5 = spriteAttachRoleCfg.getSpriteItem5();
			craeteSpriteAttach(spriteItem5, list);
			int spriteItem6 = spriteAttachRoleCfg.getSpriteItem6();
			craeteSpriteAttach(spriteItem6, list);

			List<SpriteAttachSyn> result = new ArrayList<SpriteAttachSyn>();
			SpriteAttachSyn syn = new SpriteAttachSyn();
			syn.setItems(list);
			syn.setOwnerId(heroId);
			result.add(syn);
			return result;

		} else {
			GameLog.error("SpriteAttach", "userId:" + userId, "找不到对应英雄的灵蕴点,英雄id：" + heroId);
			return null;
		}
	}
	
	private void craeteSpriteAttach(int spriteAttachId, List<SpriteAttachItem> list){
		SpriteAttachItem item = new SpriteAttachItem();
		item.setId(spriteAttachId);
		item.setLevel(1);
		list.add(item);
	}

	@Override
	public List<SpriteAttachSyn> checkAndCreate(PlayerExtPropertyStore<SpriteAttachSyn> store, HeroCreateParam params) {
		// TODO Auto-generated method stub
		return null;
	}

}
