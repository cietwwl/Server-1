//package com.rwbase.common.attrdata.calc;
//
//import java.util.List;
//
//import com.playerdata.team.SkillInfo;
//import com.rwbase.common.attrdata.AttrData;
//import com.rwbase.dao.skill.SkillCfgDAO;
//import com.rwbase.dao.skill.pojo.SkillCfg;
//
///*
// * @author HC
// * @date 2016年4月15日 下午11:51:30
// * @Description 
// */
//public class SkillAttrDataCalc implements IAttrDataCalc {
//
//	private final List<SkillInfo> skillList;// 技能列表
//
//	public SkillAttrDataCalc(List<SkillInfo> skillList) {
//		this.skillList = skillList;
//	}
//
//	@Override
//	public AttrData getAttrData() {
//		if (skillList == null || skillList.isEmpty()) {
//			return null;
//		}
//
//		AttrData attrData = new AttrData();
//		SkillCfgDAO skillCfgDAO = SkillCfgDAO.getInstance();
//
//		for (int i = 0, size = skillList.size(); i < size; i++) {
//			SkillInfo skillInfo = skillList.get(i);
//			if (skillInfo == null) {
//				continue;
//			}
//
//			String skillId = skillInfo.getSkillId();
//			int skillLevel = skillInfo.getSkillLevel();
//			if (skillLevel <= 0) {
//				continue;
//			}
//
//			SkillCfg skillCfg = skillCfgDAO.getCfgById(skillId);
//			if (skillCfg == null) {
//				continue;
//			}
//
//			attrData.plus(AttrData.fromObject(skillCfg));
//		}
//
//		return attrData;
//	}
//
//	@Override
//	public AttrData getPrecentAttrData() {
//		return null;
//	}
// }