package com.rwbase.dao.hero.pojo;

import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.role.pojo.RoleQualityCfg;

public class RoleBaseInfoHelper {

	
	public static AttrData toAttrData(RoleBaseInfo roleBaseInfo)
	{
		RoleCfg m_HeroCfg = (RoleCfg) RoleCfgDAO.getInstance().getCfgById(roleBaseInfo.getTemplateId());
		AttrData attrData = AttrData.fromObject(m_HeroCfg);
		
		int level = roleBaseInfo.getLevel();
		attrData.setLife((int) m_HeroCfg.getLife() + (int) m_HeroCfg.getLifeGrowUp() / 100 * level);
		attrData.setAttack((int) m_HeroCfg.getAttack() + (int) m_HeroCfg.getAttackGrowUp() / 100 * level);
		attrData.setPhysiqueDef((int) m_HeroCfg.getPhysiqueDef() + (int) m_HeroCfg.getPhysicqueDefGrowUp() / 100 * level);
		attrData.setSpiritDef((int) m_HeroCfg.getSpiritDef() + (int) m_HeroCfg.getSpiritDefGrowUp() / 100 * level);
		attrData.plus(addQualityAttrData(roleBaseInfo));
		return attrData;
	}
	
	public static AttrData addQualityAttrData(RoleBaseInfo roleBaseInfo){
		RoleQualityCfg cfg = RoleQualityCfgDAO.getInstance().getConfig(roleBaseInfo.getQualityId());
		AttrData qualityAttrData = null;
		if (cfg != null){
			qualityAttrData = AttrData.fromObject(cfg);
		}
		return qualityAttrData;
	}
	

}
