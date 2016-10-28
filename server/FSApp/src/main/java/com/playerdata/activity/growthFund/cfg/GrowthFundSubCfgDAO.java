package com.playerdata.activity.growthFund.cfg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.common.BaseConfig;
import com.log.GameLog;
import com.playerdata.ItemCfgHelper;
import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.pojo.ItemInfo;

//	<bean class="com.playerdata.activity.growthFund.cfg.GrowthFundSubCfgDAO"  init-method="init" />

public class GrowthFundSubCfgDAO extends CfgCsvDao<ActivitySubCfgIF> {
	
	public static GrowthFundSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(GrowthFundSubCfgDAO.class);
	}

	private void putToCfgCacheMap(Map<String, ? extends ActivitySubCfgIF> map) {
		if (null != map) {
			for (Iterator<String> keyItr = map.keySet().iterator(); keyItr.hasNext();) {
				ActivitySubCfgIF cfg = map.get(keyItr.next());
				cfgCacheMap.put(String.valueOf(cfg.getId()), cfg);
				((BaseConfig) cfg).ExtraInitAfterLoad();
			}
		}
	}

	@Override
	public Map<String, ActivitySubCfgIF> initJsonCfg() {
		cfgCacheMap = new HashMap<String, ActivitySubCfgIF>();
		Map<String, GrowthFundGiftCfg> giftCfgMap = CfgCsvHelper.readCsv2Map("growthFund/GrowthFundGift.csv", GrowthFundGiftCfg.class);
		Map<String, GrowthFundRewardCfg> rewardCfgMap = CfgCsvHelper.readCsv2Map("growthFund/GrowthFundReward.csv", GrowthFundRewardCfg.class);
		putToCfgCacheMap(giftCfgMap);
		putToCfgCacheMap(rewardCfgMap);
		return cfgCacheMap;
	}
	
	@Override
	public void CheckConfig() {
		int exCount = 0;
		int endValue = eSpecialItemId.eSpecial_End.getValue();
		for (Iterator<String> keyItr = cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			GrowthFundRewardAbsCfg cfg = (GrowthFundRewardAbsCfg) cfgCacheMap.get(keyItr.next());
			List<ItemInfo> itemInfos = cfg.getRewardItemInfos();
			if (itemInfos.isEmpty()) {
				GameLog.error("GrowthFundSubCfgDAO", "CheckConfig", "奖励为空，奖励id：" + cfg.getId());
				exCount++;
			}
			for (ItemInfo itemInfo : itemInfos) {
				if (itemInfo.getItemID() > endValue) {
					if (ItemCfgHelper.GetConfig(itemInfo.getItemID()) == null) {
						GameLog.error("GrowthFundSubCfgDAO", "CheckConfig", "道具不存在！奖励id：" + cfg.getId() + "，奖励id：" + itemInfo.getItemID());
						exCount++;
					}
				}
			}
		}
		if (exCount > 0) {
			throw new IllegalArgumentException("成长基金数据验证不通过！");
		}
	}
}
