package com.rw.service.dropitem;

import io.netty.util.collection.IntObjectHashMap;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class DropGuaranteeCfgDAO extends CfgCsvDao<DropGuaranteeCfg> {

	public static DropGuaranteeCfgDAO getInstance() {
		return SpringContextUtil.getBean(DropGuaranteeCfgDAO.class);
	}
	
	private IntObjectHashMap<DropGuaranteeCfg> map;

	@Override
	protected Map<String, DropGuaranteeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/dropGuaranteeCfg.csv", DropGuaranteeCfg.class);
		IntObjectHashMap<DropGuaranteeCfg> copy = new IntObjectHashMap<DropGuaranteeCfg>();
		for (Map.Entry<String, DropGuaranteeCfg> entry : cfgCacheMap.entrySet()) {
			copy.put(Integer.parseInt(entry.getKey()), entry.getValue());
		}
		this.map = copy;
		return cfgCacheMap;
	}

	public DropGuaranteeCfg getDropGuarantee(int id) {
		return map.get(id);
	}
}
