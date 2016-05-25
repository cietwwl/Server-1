package com.rwbase.dao.battle.pojo;

import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.battle.pojo.cfg.BufferCfg;

/*
 * @author HC
 * @date 2016年5月14日 下午4:41:38
 * @Description 
 */
public class BufferCfgDAO extends CfgCsvDao<BufferCfg> {

	public static BufferCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(BufferCfgDAO.class);
	}

	@Override
	protected Map<String, BufferCfg> initJsonCfg() {
		Map<String, BufferCfg> readCsv2Map = CfgCsvHelper.readCsv2Map("battle/BufferCfg.csv", BufferCfg.class);

		if (readCsv2Map != null && !readCsv2Map.isEmpty()) {
			for (Entry<String, BufferCfg> e : readCsv2Map.entrySet()) {
				e.getValue().initData();
			}
		}

		return cfgCacheMap = readCsv2Map;
	}
}