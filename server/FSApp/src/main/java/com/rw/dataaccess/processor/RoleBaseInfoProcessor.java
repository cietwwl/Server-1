package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerParam;
import com.rw.dataaccess.PlayerCoreCreation;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.role.pojo.RoleCfg;

public class RoleBaseInfoProcessor implements PlayerCoreCreation<RoleBaseInfo>{

	@Override
	public RoleBaseInfo create(PlayerParam param) {
		RoleCfg heroCfg = param.getPlayerCfg();
		RoleBaseInfo roleBaseInfo = new RoleBaseInfo();
		roleBaseInfo.setId(param.getUserId());
		roleBaseInfo.setTemplateId(heroCfg.getRoleId());
		roleBaseInfo.setModeId(heroCfg.getModelId());
		roleBaseInfo.setLevel(1);
		roleBaseInfo.setCareerType(heroCfg.getCareerType());
		roleBaseInfo.setStarLevel(heroCfg.getStarLevel());
		roleBaseInfo.setQualityId(heroCfg.getQualityId());
		return roleBaseInfo;
	}

}
