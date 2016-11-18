package com.bm.targetSell.param.attrs;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;
import com.rwproto.BattleCommon.eBattlePositionType;


/**
 * 上阵英雄宝石类型,参数：宝石孔位置
 * @author Alex
 *
 * 2016年11月17日 下午5:36:04
 */
public class AchieveStoneType extends AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg,
			Map<String, Object> AttrMap) {
		
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
			List<InlayItem> list = InlayMgr.getInstance().getInlayGenList(cfg.getParam());
			for (int i = 0; i < list.size(); i++) {
				InlayItem item = list.get(i);
				if(item == null){
					continue;
				}
				if(item.getSlotId() >= attrNames.length){//这里做个容错
					GameLog.error(LogModule.COMMON.getName(), "AchieveStoneType", "精准营销获取英雄的宝石类型时出现问题，宝石id:"
							+item.getId()+"位置slot值为"+item.getSlotId()+"，参数长度为"+ attrNames.length, null);
					continue;
				}
				GemCfg gemCfg = ItemCfgHelper.getGemCfg(item.getModelId());
				if(gemCfg != null){
					AttrMap.put(attrNames[item.getSlotId()], gemCfg.getGemType());
				}
			}
			
		}
		
		
	}

}
