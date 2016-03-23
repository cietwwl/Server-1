package com.rwbase.dao.tower;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class TowerGoodsCfgDAO extends CfgCsvDao<TowerGoodsCfg> {
	public static TowerGoodsCfgDAO getInstance() {
		return SpringContextUtil.getBean(TowerGoodsCfgDAO.class);
	}


	@Override
	public Map<String, TowerGoodsCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("tower/TowerGoods.csv",TowerGoodsCfg.class);
		return cfgCacheMap;
	}

	public List<TowerGoodsCfg> getCfgsByFormatId(int id) {
		List<TowerGoodsCfg> allCfg = super.getAllCfg();
		List<TowerGoodsCfg> formatCfg = new ArrayList<TowerGoodsCfg>();
		for (int i = 0; i < allCfg.size(); i++) {
			if (allCfg.get(i).getFormatId() == id) {
				formatCfg.add(allCfg.get(i));
			}
		}
		return formatCfg;
	}
}
