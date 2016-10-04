package com.rw.dataaccess.hero;

import java.util.List;

import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.skill.pojo.SkillItem;

public class HeroSkillItemCreator implements HeroExtPropertyCreator<SkillItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateOpenTime(long currentTimeMillis) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<SkillItem> firstCreate(HeroCreateParam params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SkillItem> checkAndCreate(
			PlayerExtPropertyStore<SkillItem> store, HeroCreateParam params) {
		// TODO Auto-generated method stub
		return null;
	}
	



	

}
