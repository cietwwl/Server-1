package com.rwbase.dao.worship;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.worship.pojo.CfgWorshipReward;

public class CfgWorshipRewardHelper extends CfgCsvDao<CfgWorshipReward> {

	private final int TYPE_WORSHIP = 3;//膜拜者类型
	
	private final int TYPE_BYWORSHIP = 1;;//被膜拜者类型
	
	private CfgWorshipReward worshipReward = null;
	/**被膜拜者的奖励配置*/
	private List<CfgWorshipReward> dataList  = new ArrayList<CfgWorshipReward>();
	
	public static CfgWorshipRewardHelper getInstance() {
		return SpringContextUtil.getBean(CfgWorshipRewardHelper.class);
	}
	
	public Map<String, CfgWorshipReward> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worship/worshipReward.csv",CfgWorshipReward.class);
		return cfgCacheMap;
	}
	
	@Override
	public void CheckConfig() {
	
		try {
			
			List<CfgWorshipReward> allCfg = getAllCfg();
			for (CfgWorshipReward record : allCfg) {
				if(record.getRewardType() == TYPE_WORSHIP){
					worshipReward = record;//此记录在配置表内应该只有一个
				}
				if(record.getRewardType() == TYPE_BYWORSHIP){
					record.format();
					dataList.add(record);
				}
				
			}
		} catch (Exception e) {
			GameLog.error(LogModule.COMMON, "膜拜", "检查膜拜配置表，发现存在问题数据", e);
		}
		
	}

	/**获取膜拜者奖励配置*/
	public CfgWorshipReward getWorshipRewardCfg(){
		return worshipReward;
	}
	
	/**
	 * 根据膜拜者人数获取被膜拜者奖励配置
	 * @param count
	 * @return
	 */
	public CfgWorshipReward getByWorshipRewardCfgByCount(int count){
		CfgWorshipReward cfg = null;
		for (CfgWorshipReward record : dataList) {
			if(record.getUpper() >= count && record.getLowwer() <= count){
				cfg = record;
				break;
			}
		}
		return cfg;
	}
	
}
