package com.rw.dataaccess.hero;

import java.util.List;

import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.skill.pojo.SkillHelper;
import com.rwbase.dao.skill.pojo.SkillItem;

public class HeroSkillItemCreator implements HeroExtPropertyCreator<SkillItem> {

	@Override
	public eOpenLevelType getOpenLevelType() {
		return null;
	}

	@Override
	public List<SkillItem> firstCreate(HeroCreateParam params) {
		List<SkillItem> list = SkillHelper.getInstance().initSkill(params.getRolecfg(), params.getQualityId(), params.getHeroLevel());
		String heroId = params.getHeroId();
		for (int i = list.size(); --i >= 0;) {
			SkillItem item = list.get(i);
			item.setOwnerId(heroId);
			item.setId(SkillHelper.getInstance().parseSkillItemId(item.getSkillId()));
		}
		return list;
	}

	@Override
	public List<SkillItem> checkAndCreate(RoleExtPropertyStore<SkillItem> store, HeroCreateParam params) {
		return null;
	}

	@Override
	public boolean requiredToPreload(HeroCreateParam params) {
		return true;
	}

}
