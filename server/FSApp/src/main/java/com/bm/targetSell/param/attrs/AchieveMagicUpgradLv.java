package com.bm.targetSell.param.attrs;

import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;
import com.rwproto.ItemBagProtos.EItemTypeDef;


/**
 * 角色某一类法宝的最高进化等级
 * 参数：法宝类型
 * @author Alex
 *
 * 2016年11月17日 下午7:19:20
 */
public class AchieveMagicUpgradLv extends AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg,
			Map<String, Object> AttrMap) {
		int magicType = Integer.parseInt(cfg.getParam());
		int currLv = 0;
		List<ItemData> list = player.getItemBagMgr().getItemListByType(EItemTypeDef.Magic);
		for (ItemData data : list) {
			MagicCfg magicCfg = MagicCfgDAO.getInstance().getCfgById(data.getId());
			if(magicCfg.getMagicType() == magicType && data.getMagicAdvanceLevel() > currLv){
				currLv = data.getMagicAdvanceLevel();
			}
		}
		
		AttrMap.put(cfg.getAttrName(), currLv);
	}

}
