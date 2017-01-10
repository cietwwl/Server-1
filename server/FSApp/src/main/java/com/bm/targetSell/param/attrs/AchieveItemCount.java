package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.bm.targetSell.param.TargetSellRoleChange;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;

/**
 * 监测某类道具数量  参数：道具modelId
 * @author Alex
 * 2017年1月6日 上午11:38:03
 */
public class AchieveItemCount implements AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap) {
		int itemModelId = Integer.parseInt(cfg.getParam());
		int count = ItemBagMgr.getInstance().getItemCountByModelId(player.getUserId(), itemModelId);
		AttrMap.put(cfg.getAttrName(), count);
	}

	@Override
	public void addHeroAttrs(String userID, String heroID, EAchieveType achieveType, TargetSellRoleChange value) {
		
	}

}
