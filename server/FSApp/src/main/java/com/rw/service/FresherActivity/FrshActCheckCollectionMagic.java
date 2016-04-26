package com.rw.service.FresherActivity;

import java.util.ArrayList;
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
 * 收集法宝活动
 * @author lida
 *
 */
public class FrshActCheckCollectionMagic implements IFrshActCheckTask{

	@Override
	public FresherActivityCheckerResult doCheck(Player player,
			eActivityType activityType) {
		// TODO Auto-generated method stub
				List<Integer> result = new ArrayList<Integer>();
				List<FresherActivityItemIF> fresherActivityItems = player.getFresherActivityMgrIF().getFresherActivityItems(activityType);
				
				FresherActivityCheckerResult checkResult = new FresherActivityCheckerResult();
				Map<Integer, String> map = new HashMap<Integer, String>();
				
				List<ItemData> list = player.getItemBagMgr().getItemListByType(EItemTypeDef.Magic);
				int size = list.size();
				for (FresherActivityItemIF freActivityItem : fresherActivityItems){
					if(!FresherActivityChecker.checkFresherActivity(freActivityItem)){
						continue;
					}
					
					int cfgId = freActivityItem.getCfgId();
					FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
					String condition = fresherActivityCfg.getCondition();
					
					int conditionValue = Integer.parseInt(condition);
					map.put(cfgId, String.valueOf(size));
					if(size >= conditionValue){
						result.add(cfgId);
					}
					
				}
				checkResult.setCompleteList(result);
				checkResult.setCurrentProgress(map);
				return checkResult;
	}

}
