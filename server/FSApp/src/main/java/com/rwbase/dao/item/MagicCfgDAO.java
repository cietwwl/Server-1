package com.rwbase.dao.item;

import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.MagicCfg;

public class MagicCfgDAO extends CfgCsvDao<MagicCfg> {
	public static MagicCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicCfgDAO.class);
	}

	@Override
	public Map<String, MagicCfg> initJsonCfg() {
		Map<String, MagicCfg> readCsv2Map = CfgCsvHelper.readCsv2Map("item/Magic.csv", MagicCfg.class);

		if (readCsv2Map != null && !readCsv2Map.isEmpty()) {
			for (Entry<String, MagicCfg> e : readCsv2Map.entrySet()) {
				MagicCfg value = e.getValue();
				if (value == null) {
					continue;
				}

				value.initData();
				value.ExtraInitAfterLoad();
			}
		}

		return cfgCacheMap = readCsv2Map;
	}

	// TODO 所有表加载完成后，应该检查decomposeGoodList里面的模板ID是否存在于Consume表
}
