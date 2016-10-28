package com.rwbase.dao.spriteattach;

import java.util.List;

import com.playerdata.SpriteAttachMgr;
import com.rw.dataaccess.hero.HeroCreateParam;
import com.rw.dataaccess.hero.HeroExtPropertyCreator;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

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
		int heroModelId = params.getModelId();
		String heroId = params.getHeroId();
		String userId = params.getUserId();
		String qualityId = params.getQualityId();
		int heroLevel = params.getHeroLevel();

		return SpriteAttachMgr.getInstance().checkRoleCreate(userId, heroId, heroLevel, heroModelId, qualityId);
	}

	@Override
	public List<SpriteAttachSyn> checkAndCreate(RoleExtPropertyStore<SpriteAttachSyn> store, HeroCreateParam params) {
		// TODO Auto-generated method stub
		return null;
	}

}
