package com.rwbase.dao.fetters.pojo.impl.checkforceuse;

import com.rwbase.dao.fetters.FettersBM;
import com.rwbase.dao.fetters.pojo.IFettersCheckForceUseHeroId;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersSubConditionTemplate;

/*
 * @author HC
 * @date 2016年4月28日 下午3:26:52
 * @Description 
 */
public class FettersForceDirectHeroIdImpl implements IFettersCheckForceUseHeroId {

	@Override
	public int checkForceUseHeroId(FettersSubConditionTemplate fettersSubCondition) {
		if (fettersSubCondition == null) {
			return 0;
		}

		return fettersSubCondition.getSubConditionRestrictValue();
	}

	@Override
	public int getCheckConditionType() {
		return FettersBM.SubConditionRestrictType.FORCE_DIRECT_HERO_MODEL_ID.type;
	}
}