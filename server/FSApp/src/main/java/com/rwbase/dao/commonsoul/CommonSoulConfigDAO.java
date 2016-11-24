package com.rwbase.dao.commonsoul;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.common.enu.eConsumeTypeDef;
import com.rwbase.dao.commonsoul.pojo.CommonSoulConfig;
import com.rwbase.dao.item.ConsumeCfgDAO;
import com.rwbase.dao.item.pojo.ConsumeCfg;

public class CommonSoulConfigDAO extends CfgCsvDao<CommonSoulConfig> {

	public static CommonSoulConfigDAO getInstance() {
		return SpringContextUtil.getBean(CommonSoulConfigDAO.class);
	}

	private CommonSoulConfig config;

	@Override
	protected Map<String, CommonSoulConfig> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map("CommonSoul/CommonSoulConfig.csv", CommonSoulConfig.class);
		for (Iterator<String> keyItr = cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			config = cfgCacheMap.get(keyItr.next());
			break;
		}
		return cfgCacheMap;
	}

	public CommonSoulConfig getConfig() {
		return config;
	}
	
	@Override
	public void CheckConfig() {
		List<ConsumeCfg> allCfg = ConsumeCfgDAO.getInstance().getAllCfg();
		int commonSoulStoneType = eConsumeTypeDef.CommonSoulStone.getOrder();
		for (int i = 0, size = allCfg.size(); i < size; i++) {
			ConsumeCfg tempCfg = allCfg.get(i);
			if (tempCfg.getConsumeType() == commonSoulStoneType) {
				config.setCommonSoulStoneCfgId(tempCfg.getId());
				config.setCommonSoulStoneName(tempCfg.getName());
				break;
			}
		}
		if(config.getCommonSoulStoneCfgId() == 0) {
//			throw new IllegalArgumentException("万能魂石不存在！");
		}
	}

}
