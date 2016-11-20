package com.bm.targetSell.param.attrs;

import java.util.List;
import java.util.Map;

import com.bm.targetSell.param.TargetSellRoleChange;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.InlayMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.embattle.EmBattlePositionKey;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.hero.core.FSHero;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;
import com.rwbase.dao.user.User;
import com.rwproto.BattleCommon.eBattlePositionType;

/**
 * 英雄宝石等级和类型，这里会检查所有位置宝石的等级
 * 参数：英雄modelID
 * @author Alex
 *
 * 2016年11月17日 下午6:09:44
 */
public class AchieveStoneAttrValue implements AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap) {

		HeroMgr heroMgr = player.getHeroMgr();
		String[] attrNames = cfg.getAttrName().split(",");
		int pos = Integer.parseInt(cfg.getParam());
		
		EmbattlePositionInfo embattleInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.Normal_VALUE, EmBattlePositionKey.posCopy.getKey());
		if(embattleInfo == null){
			return;
		}
		String heroId = "";
		List<EmbattleHeroPosition> p = embattleInfo.getPos();
		for (EmbattleHeroPosition embattleHeroPosition : p) {
			if (embattleHeroPosition.getPos() == pos) {
				heroId = embattleHeroPosition.getId();
				break;
			} 
		}
		
		Hero hero = heroMgr.getHeroById(player, heroId);
		if(hero != null){
			List<InlayItem> list = InlayMgr.getInstance().getInlayGenList(heroId);
			for (int i = 0; i < list.size(); i++) {
				InlayItem item = list.get(i);
				if(item == null){
					continue;
				}
				if(item.getSlotId() >= attrNames.length){//这里做个容错
					GameLog.error(LogModule.COMMON.getName(), "AchieveStoneLevel", "精准营销获取英雄的宝石等级时出现问题，宝石id:"
							+item.getId()+"位置slot值为"+item.getSlotId()+"，参数长度为"+ attrNames.length, null);
					continue;
				}
				GemCfg gemCfg = ItemCfgHelper.getGemCfg(item.getModelId());
				if(gemCfg != null){
					EAchieveType achieveType = EAchieveType.getAchieveType(cfg.getProcessType());
					int value = achieveType == EAchieveType.AchieveStoneLevel ? gemCfg.getLevel() : gemCfg.getGemType();
					AttrMap.put(attrNames[item.getSlotId()], value);
				}
			}
			
		}
		
	}

	@Override
	public void addHeroAttrs(String userID, String heroID, EAchieveType achieveType, TargetSellRoleChange value) {
		//因为宝石数据没有保存角色的数据，所以要在这里补加进去
		FSHero hero = FSHeroMgr.getInstance().getHeroById(userID, heroID);
		if(hero == null){
			return;
		}
		EmbattlePositionInfo embattleInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(userID, eBattlePositionType.Normal_VALUE, EmBattlePositionKey.posCopy.getKey());
		if(embattleInfo == null){
			return;
		}
		int pos = -10;
		List<EmbattleHeroPosition> p = embattleInfo.getPos();
		for (EmbattleHeroPosition embattleHeroPosition : p) {
			if (embattleHeroPosition.getId().equals(heroID)) {
				pos = embattleHeroPosition.getPos();
				break;
			} 
		}
		
		if(pos == -10){
			return;
		}
		
		BenefitAttrCfg cfg = BenefitAttrCfgDAO.getInstance().getCfgByHeroModelIdAndProcessType(pos, achieveType.getId());
		if(cfg != null){
			value.addChange(cfg.getId());
		}
		
	}

}
