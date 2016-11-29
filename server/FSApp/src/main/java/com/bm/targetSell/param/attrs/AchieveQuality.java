package com.bm.targetSell.param.attrs;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.bm.targetSell.param.ERoleAttrs;
import com.bm.targetSell.param.TargetSellRoleChange;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.embattle.EmBattlePositionKey;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;
import com.rwproto.BattleCommon.eBattlePositionType;

/**
 * 检查上阵英雄品阶
 * @author Alex
 *
 * 2016年11月29日 下午4:45:00
 */
public class AchieveQuality implements AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap) {
		HeroMgr heroMgr = player.getHeroMgr();
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
		RoleQualityCfgDAO roleQualityCfgDAO = RoleQualityCfgDAO.getInstance();
		int index = 0;
		for (String heroId : list) {
			Hero hero = heroMgr.getHeroById(player, heroId);
			String qualityId = hero.getQualityId();
			RoleQualityCfg roleQualityCfg = roleQualityCfgDAO.getCfgById(qualityId);
			String attrName = attrNames[index];
			AttrMap.put(attrName, roleQualityCfg.getQuality());
			index++;
		}
	}

	@Override
	public void addHeroAttrs(String userID, String heroID, EAchieveType change, TargetSellRoleChange value) {
		EmbattlePositionInfo embattleInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(userID, eBattlePositionType.Normal_VALUE, EmBattlePositionKey.posCopy.getKey());
		if(embattleInfo == null){
			return;
		}
		
		//检查一下是否为上阵英雄
		int posIndex = -1;
		List<EmbattleHeroPosition> pos = embattleInfo.getPos();
		for (EmbattleHeroPosition embattleHeroPosition : pos) {
			if (embattleHeroPosition.getId().equals(heroID)) {
				posIndex = embattleHeroPosition.getPos();
				break;
			}
		}
		if(posIndex <= 0){
			//不上阵
			return;
		}
		//是上阵英雄，添加改变通知
		value.addChange(ERoleAttrs.r_EmbattleQuality.getId());
	}

}
