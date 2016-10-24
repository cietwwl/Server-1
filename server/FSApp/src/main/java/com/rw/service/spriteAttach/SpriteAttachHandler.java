package com.rw.service.spriteAttach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.common.RefLong;
import com.google.protobuf.ByteString;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.SpriteAttachMgr;
import com.playerdata.hero.core.FSHeroThirdPartyDataMgr;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.spriteattach.SpriteAttachItem;
import com.rwbase.dao.spriteattach.SpriteAttachSyn;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachCfg;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachCfgDAO;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachLevelCostCfg;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachLevelCostCfgDAO;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachRoleCfg;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachRoleCfgDAO;
import com.rwproto.SpriteAttachProtos.SpriteAttachRequest;
import com.rwproto.SpriteAttachProtos.SpriteAttachResponse;
import com.rwproto.SpriteAttachProtos.eSpriteAttachRequestType;
import com.rwproto.SpriteAttachProtos.eSpriteAttachResultType;
import com.rwproto.SpriteAttachProtos.spriteAttachMaterial;

public class SpriteAttachHandler {
	private SpriteAttachHandler() {
	}

	private static SpriteAttachHandler instance = new SpriteAttachHandler();

	public static SpriteAttachHandler getInstance() {
		return instance;
	}
	
	/**
	 * 附灵
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString spriteAttach(Player player, SpriteAttachRequest request){
		SpriteAttachResponse.Builder res = SpriteAttachResponse.newBuilder();
		eSpriteAttachRequestType requestType = request.getRequestType();
		int heroModelId = request.getHeroModelId();
		int spriteAttachId = request.getSpriteAttachId();
		List<spriteAttachMaterial> materialsList = request.getMaterialsList();
		
		Hero hero = player.getHeroMgr().getHeroByModerId(player, heroModelId);
		if(hero == null){
			return sendFailMsg("找不到对应的英雄!", res, requestType);
		}
		
		SpriteAttachRoleCfg spriteAttachRoleCfg = SpriteAttachRoleCfgDAO.getInstance().getCfgById(String.valueOf(hero.getModeId()));
		if(spriteAttachRoleCfg == null){
			return sendFailMsg("找不到对应的英雄的灵蕴信息!", res, requestType);
		}
		SpriteAttachCfg spriteAttachCfg = SpriteAttachCfgDAO.getInstance().getCfgById(String.valueOf(spriteAttachId));
		if(spriteAttachCfg == null){
			return sendFailMsg("找不到对应的英雄的灵蕴信息!", res, requestType);
		}
		SpriteAttachMgr spriteAttachMgr = SpriteAttachMgr.getInstance();
		Map<Integer, SpriteAttachItem> itemMap = spriteAttachMgr.getSpriteAttachHolder().getSpriteAttachItemMap(hero.getUUId());
		
		SpriteAttachItem spriteAttachItem = itemMap.get(spriteAttachId);
		//查找对应的灵蕴点
		if(spriteAttachItem == null){
			return sendFailMsg("找不到对应的英雄的灵蕴信息!", res, requestType);
		}
		
		//判断灵蕴点是否激活
		if(!spriteAttachMgr.checkSpriteAttachActive(player, hero, spriteAttachCfg)){
			return sendFailMsg("英雄的灵蕴点尚未激活,附灵失败!", res, requestType);
		}
		
		int spriteAttachLevel = spriteAttachItem.getLevel();
		long currentExp = spriteAttachItem.getExp();
		int nextSpriteAttachLevel = spriteAttachLevel + 1;
		
		//执行附灵
		int levelCostPlanId = spriteAttachCfg.getLevelCostPlanId();
		
		SpriteAttachLevelCostCfg spriteAttachLevelCost = SpriteAttachLevelCostCfgDAO.getInstance().getSpriteAttachLevelCost(spriteAttachLevel, levelCostPlanId);
		
		long levelExp = spriteAttachLevelCost.getExp();
		
		SpriteAttachLevelCostCfg nextSpriteAttachLevelCost = SpriteAttachLevelCostCfgDAO.getInstance().getSpriteAttachLevelCost(nextSpriteAttachLevel, levelCostPlanId);
		if(nextSpriteAttachLevelCost == null && currentExp >= levelExp){
			return sendFailMsg("当前附灵已到最高等级!", res, requestType);
		}
		
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		
		int costType = spriteAttachLevelCost.getCostType();
		
		final eSpecialItemId currencyType = eSpecialItemId.getDef(costType);
		if (currencyType == null) {
			return sendFailMsg("附灵配置的货币类型无效,附灵失败!", res, requestType);
		}
		
		HashMap<Integer, Integer> consumeMap =new HashMap<Integer, Integer>();
		RefLong cost = new RefLong();
		List<IUseItem> useItemList = new ArrayList<IUseItem>(consumeMap.size());
		calcConsume(materialsList, spriteAttachLevel, currentExp, levelCostPlanId, spriteAttachLevelCost, consumeMap, cost, useItemList);
		
		Map<Integer, Integer> modifyMoneyMap = new HashMap<Integer, Integer>(1);
		modifyMoneyMap.put(costType, -(int)(cost.value));
		
		long costCount =cost.value;
		// 扣金币和扣材料
		long curValue = player.getReward(currencyType);
		if (costCount > curValue) {
			return sendFailMsg("货币不足,附灵失败!", res, requestType);
		}
		
		if (!itemBagMgr.useLikeBoxItem(useItemList, null, modifyMoneyMap)) {
			return sendFailMsg("法宝升级失败，消耗升级材料失败！", res, requestType);
		}
		
		spriteAttachItem.setLevel(nextSpriteAttachLevel);
		FSHeroThirdPartyDataMgr.getInstance().notifyAll();
		
		return res.build().toByteString();
	}
	

	private void calcConsume(List<spriteAttachMaterial> materialsList, int spriteAttachLevel, long currentExp, int levelCostPlanId, SpriteAttachLevelCostCfg spriteAttachLevelCost, HashMap<Integer, Integer> consumeMap, RefLong Cost, List<IUseItem> useItemList) {
		int totalCost = 0;
		int materialsExp = 0;
		SpriteAttachLevelCostCfg currentCfg = spriteAttachLevelCost;
		for (Iterator<spriteAttachMaterial> iterator = materialsList.iterator(); iterator.hasNext();) {
			spriteAttachMaterial spriteAttachMaterial = (spriteAttachMaterial) iterator.next();
			int itemModelId = spriteAttachMaterial.getItemModelId();
			int count = spriteAttachMaterial.getCount();
			ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(itemModelId);
			materialsExp += itemBaseCfg.getEnchantExp()* count;
			consumeMap.put(itemModelId, count);
		}
		
		SpriteAttachLevelCostCfg levelCostCfg = spriteAttachLevelCost;
		long tmpExp = currentExp;
		int upgradeLevel = spriteAttachLevel;
		while(materialsExp > 0){
			long exp = levelCostCfg.getExp();
			long upgradeExp = exp - tmpExp;
			if(materialsExp > upgradeExp){
				materialsExp -= upgradeExp;
				totalCost += (exp - tmpExp) * levelCostCfg.getCostCount();
				upgradeLevel++;
				levelCostCfg = SpriteAttachLevelCostCfgDAO.getInstance().getSpriteAttachLevelCost(upgradeLevel, levelCostPlanId);
				if(levelCostCfg == null){
					break;
				}
			}else{
				materialsExp = 0;
				totalCost += materialsExp * levelCostCfg.getCostCount();
			}
			
		}
		Cost.value = totalCost;
	}
	
	public ByteString sendFailMsg(String failMsg, SpriteAttachResponse.Builder res, eSpriteAttachRequestType requestType){
		res.setRequestType(requestType);
		res.setReslutType(eSpriteAttachResultType.Fail);
		res.setReslutValue(failMsg);
		
		return res.build().toByteString();
	}
}
