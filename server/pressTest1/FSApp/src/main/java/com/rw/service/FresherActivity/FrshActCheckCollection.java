package com.rw.service.FresherActivity;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;
import com.rwbase.dao.item.GemCfgDAO;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.ItemBagProtos.EItemTypeDef;
/**
 * 
 * @author lida
 *
 */
public class FrshActCheckCollection implements IFrshActCheckTask{

	private final static String CONDITION_LEVELNUM = "1";
	private final static String CONDITION_TYPENUM = "2";
	
	@Override
	public FresherActivityCheckerResult doCheck(Player player, eActivityType activityType) {
		// TODO Auto-generated method stub
		List<Integer> result = new ArrayList<Integer>();
		List<FresherActivityItemIF> fresherActivityItems = player.getFresherActivityMgrIF().getFresherActivityItems(activityType);
		
		FresherActivityCheckerResult checkResult = new FresherActivityCheckerResult();
		Map<Integer, String> map = new HashMap<Integer, String>();
		
		
		//<等级，个数>
		HashMap<Integer, Integer> lvMap = new HashMap<Integer, Integer>();
		//<类型，个数>
		HashMap<Integer, Integer> typeMap = new HashMap<Integer, Integer>();
		
		statisticsCollection(player, lvMap, typeMap);
		
		for (FresherActivityItemIF freActivityItem : fresherActivityItems){
			if(!FresherActivityChecker.checkFresherActivity(freActivityItem)){
				continue;
			}
			
			int cfgId = freActivityItem.getCfgId();
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
			String condition = fresherActivityCfg.getCondition();
			
			String[] conditions = condition.split(":");
			
			if(checkCondition(conditions, lvMap, typeMap, map, freActivityItem)){
				result.add(cfgId);
			}
		}
		checkResult.setCompleteList(result);
		checkResult.setCurrentProgress(map);
		return checkResult;
	}

	private boolean checkCondition(String[] conditions, HashMap<Integer, Integer> lvMap, HashMap<Integer, Integer> typeMap, Map<Integer, String> map, FresherActivityItemIF freActivityItem){
		
		if(CONDITION_LEVELNUM.equals(conditions[0])){
			int level = Integer.parseInt(conditions[1]);
			int count = Integer.parseInt(conditions[2]);
			int value = lvMap.containsKey(level) ? lvMap.get(level) : 0;
			if (!freActivityItem.getCurrentValue().equals(value + "/" + count)) {
				map.put(freActivityItem.getCfgId(), String.valueOf(value));
			}
			if(value >= count){
				return true;
			}
		}
		if(CONDITION_TYPENUM.equals(conditions[0])){
			int count = Integer.parseInt(conditions[1]);
			int value = typeMap.size();
			if(!freActivityItem.getCurrentValue().equals(value + "/" + count)){
				map.put(freActivityItem.getCfgId(), String.valueOf(value));
			}
			if(value >= count){
				return true;
			}
		}
		return false;
	}
	
	private void statisticsCollection(Player player, HashMap<Integer, Integer> lvMap, HashMap<Integer, Integer> typeMap){
		List<ItemData> list = player.getItemBagMgr().getItemListByType(EItemTypeDef.Gem);
		
		for (ItemData itemData : list) {
			int modelId = itemData.getModelId();
			GemCfg cfg = (GemCfg)GemCfgDAO.getInstance().getCfgById(String.valueOf(modelId));
			int level = cfg.getGemLevel();
			int gemType = cfg.getGemType();
			if(lvMap.containsKey(level)){
				lvMap.put(level, lvMap.get(level)+itemData.getCount());
			}else{
				lvMap.put(level, itemData.getCount());
			}
			if(typeMap.containsKey(gemType)){
				typeMap.put(gemType, typeMap.get(gemType) + itemData.getCount());
			}else{
				typeMap.put(gemType, itemData.getCount());
			}
		}
	}
}
