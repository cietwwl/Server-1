//package com.rwbase.common.attrdata.calc;
//
//import com.playerdata.team.HeroBaseInfo;
//import com.rwbase.common.attrdata.AttrData;
//import com.rwbase.dao.role.RoleCfgDAO;
//import com.rwbase.dao.role.RoleQualityCfgDAO;
//import com.rwbase.dao.role.pojo.RoleCfg;
//import com.rwbase.dao.role.pojo.RoleQualityCfg;
//
///*
// * @author HC
// * @date 2016年4月15日 下午9:26:33
// * @Description 获取角色基础属性的计算结果
// */
//public class RoleBaseAttrDataCalc implements IAttrDataCalc {
//
//	private final HeroBaseInfo baseInfo;
//
//	public RoleBaseAttrDataCalc(HeroBaseInfo baseInfo) {
//		this.baseInfo = baseInfo;
//	}
//
//	@Override
//	public AttrData getAttrData() {
//		if (baseInfo == null) {
//			return null;
//		}
//
//		RoleCfg heroCfg = (RoleCfg) RoleCfgDAO.getInstance().getCfgById(baseInfo.getTmpId());
//		if (heroCfg == null) {
//			return null;
//		}
//
//		// 基础属性
//		AttrData attrData = AttrData.fromObject(heroCfg);
//
//		int level = baseInfo.getLevel();
//		attrData.setLife((int) heroCfg.getLife() + (int) heroCfg.getLifeGrowUp() / 100 * level);
//		attrData.setAttack((int) heroCfg.getAttack() + (int) heroCfg.getAttackGrowUp() / 100 * level);
//		attrData.setPhysiqueDef((int) heroCfg.getPhysiqueDef() + (int) heroCfg.getPhysicqueDefGrowUp() / 100 * level);
//		attrData.setSpiritDef((int) heroCfg.getSpiritDef() + (int) heroCfg.getSpiritDefGrowUp() / 100 * level);
//
//		// 品质属性
//		RoleQualityCfg cfg = RoleQualityCfgDAO.getInstance().getConfig(String.valueOf(baseInfo.getQuality()));
//		if (cfg == null) {
//			return attrData;
//		}
//
//		attrData.plus(AttrData.fromObject(cfg));
//		return attrData;
//	}
//
//	@Override
//	public AttrData getPrecentAttrData() {
//		return null;
//	}
// }