//package com.rwbase.common.attrdata.calc;
//
//import java.util.List;
//
//import com.playerdata.team.EquipInfo;
//import com.rwbase.common.attrdata.AttrData;
//import com.rwbase.dao.item.HeroEquipCfgDAO;
//import com.rwbase.dao.item.pojo.HeroEquipCfg;
//import com.rwbase.dao.role.EquipAttachCfgDAO;
//import com.rwbase.dao.role.pojo.EquipAttachCfg;
//
///*
// * @author HC
// * @date 2016年4月15日 下午11:42:10
// * @Description 
// */
//public class EquipAttrDataCalc implements IAttrDataCalc {
//
//	private final List<EquipInfo> equip;// 装备数据
//
//	public EquipAttrDataCalc(List<EquipInfo> equip) {
//		this.equip = equip;
//	}
//
//	@Override
//	public AttrData getAttrData() {
//		if (equip == null || equip.isEmpty()) {
//			return null;
//		}
//
//		AttrData attrData = new AttrData();
//		HeroEquipCfgDAO cfgDAO = HeroEquipCfgDAO.getInstance();
//		EquipAttachCfgDAO attachCfgDAO = EquipAttachCfgDAO.getInstance();
//
//		for (int i = 0, size = equip.size(); i < size; i++) {
//			EquipInfo equipInfo = equip.get(i);
//			if (equipInfo == null) {
//				continue;
//			}
//
//			String tmpId = equipInfo.gettId();// 模版Id
//			int attachLevel = equipInfo.geteLevel();// 附灵等级
//
//			HeroEquipCfg equipCfg = cfgDAO.getCfgById(tmpId);
//			if (equipCfg == null) {
//				continue;
//			}
//
//			AttrData equipAttrData = AttrData.fromObject(equipCfg);
//
//			EquipAttachCfg attachLevelCfg = attachCfgDAO.getCfgById(String.valueOf(attachLevel));
//			if (attachLevelCfg != null) {
//				equipAttrData.addPercent(attachLevelCfg.getAttriPercent());
//			}
//
//			attrData.plus(equipAttrData);
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