package com.bm.targetSell.param.attrs;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.bm.targetSell.param.ERoleAttrs;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.embattle.EmBattlePositionKey;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;
import com.rwbase.dao.user.User;
import com.rwproto.BattleCommon.eBattlePositionType;

public class AchieveCarrer extends AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, ERoleAttrs roleType, Object param, Map<String, Object> AttrMap, BenefitAttrCfgDAO benefitAttrCfgDAO) {
		HeroMgr heroMgr = player.getHeroMgr();
		BenefitAttrCfg cfg = benefitAttrCfgDAO.getCfgById(roleType.getIdStr());
		String[] attrNames = cfg.getAttrName().split(",");
		
		EmbattlePositionInfo embattleInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.Normal_VALUE, EmBattlePositionKey.posCopy.getKey());
		if(embattleInfo == null){
			return;
		}
		LinkedList<String> list =new LinkedList<String>();
		List<EmbattleHeroPosition> pos = embattleInfo.getPos();
		for (EmbattleHeroPosition embattleHeroPosition : pos) {
			if (embattleHeroPosition.getPos() == 0) {
				list.addFirst(embattleHeroPosition.getId());
			} else {
				list.add(embattleHeroPosition.getId());
			}
		}
		
		int index = 0;
		for (String heroId : list) {
			Hero hero = heroMgr.getHeroById(player, heroId);
			int careerType = hero.getCareerType();
			String attrName = attrNames[index];
			AttrMap.put(attrName, careerType);
			index++;
		}
		
		
	}

}