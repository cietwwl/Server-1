package com.playerdata.activity.timeCardType.cfg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.ChargeCfgDao;
import com.playerdata.charge.cfg.ChargeTypeEnum;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityTimeCardTypeSubCfgDAO extends CfgCsvDao<ActivityTimeCardTypeSubCfg> {


	public static ActivityTimeCardTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityTimeCardTypeSubCfgDAO.class);
	}
	
	private HashMap<String, List<ActivityTimeCardTypeSubCfg>> subCfgListMap;
	
	@Override
	public Map<String, ActivityTimeCardTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityTimeCardTypeSubCfg.csv", ActivityTimeCardTypeSubCfg.class);
		HashMap<String, List<ActivityTimeCardTypeSubCfg>> subCfgListMapTmp = new HashMap<String, List<ActivityTimeCardTypeSubCfg>>();
		for(ActivityTimeCardTypeSubCfg subCfg : cfgCacheMap.values()){
			ChargeCfg cfg = ChargeCfgDao.getInstance().getCfgById(subCfg.getChargeId());
			subCfg.setChargeType(cfg.getChargeType());
			ActivityTypeHelper.add(subCfg, subCfg.getParentCfgId(), subCfgListMapTmp);
		}
		
		this.subCfgListMap = subCfgListMapTmp;
		return cfgCacheMap;
	}
	
	

	public List<ActivityTimeCardTypeSubCfg> getByParentCfgId(String parentCfgId){
		return subCfgListMap.get(parentCfgId);
	}
	
	public ActivityTimeCardTypeSubCfg getById(String subId){
		ActivityTimeCardTypeSubCfg target = new ActivityTimeCardTypeSubCfg();
		List<ActivityTimeCardTypeSubCfg> allCfg = getAllCfg();
		for (ActivityTimeCardTypeSubCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getId(), subId)){
				target = tmpItem;
			}
		}
		return target;		
	}
	
	public ActivityTimeCardTypeSubCfg getBynume(ChargeTypeEnum cardenum){
		ActivityTimeCardTypeSubCfg target = new ActivityTimeCardTypeSubCfg();
		List<ActivityTimeCardTypeSubCfg> allCfg = getAllCfg();
		for (ActivityTimeCardTypeSubCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getChargeType().getCfgId(), cardenum.getCfgId())){
				target = tmpItem;
			}
		}
		return target;		
	}
}