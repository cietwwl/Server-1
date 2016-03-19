package com.rw.service.FresherActivity;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.ItemBagProtos.EItemTypeDef;

/**
 * 开服活动：法宝等级
 * @author lida
 *
 */
public class FrshActCheckMagic implements IFrshActCheckTask{

	@Override
	public FresherActivityCheckerResult doCheck(Player player, eActivityType activityType) {
		// TODO Auto-generated method stub
		List<Integer> result = new ArrayList<Integer>();
		List<FresherActivityItemIF> fresherActivityItems = player.getFresherActivityMgrIF().getFresherActivityItems(activityType);
		int maxMagicLevel = getMaxMagicLevel(player);
		
		FresherActivityCheckerResult checkResult = new FresherActivityCheckerResult();
		Map<Integer, String> map = new HashMap<Integer, String>();
		
		for (FresherActivityItemIF freActivityItem : fresherActivityItems) {
			if(!FresherActivityChecker.checkFresherActivity(freActivityItem)){
				continue;
			}
			int cfgId = freActivityItem.getCfgId();
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
			String condition = fresherActivityCfg.getCondition();
			
			int magicLvCondition = Integer.parseInt(condition);
			
			if(maxMagicLevel >= magicLvCondition){
				result.add(cfgId);
			}else{
				map.put(cfgId, String.valueOf(maxMagicLevel));
			}
		}
		checkResult.setCompleteList(result);
		checkResult.setCurrentProgress(map);
		return checkResult;
	}
	
	private int getMaxMagicLevel(Player player){
		int maxMagicLevel = 0;
		ItemData magic = player.getMagic();
		if(magic !=null){
			maxMagicLevel = magic.getMagicLevel();
		}
		
		List<ItemData> itemListByType = player.getItemBagMgr().getItemListByType(EItemTypeDef.Magic);
		for (ItemData itemData : itemListByType) {
			if(itemData.getMagicLevel() > maxMagicLevel){
				maxMagicLevel = itemData.getMagicLevel();
			}
		}
		return maxMagicLevel;
	}

}
