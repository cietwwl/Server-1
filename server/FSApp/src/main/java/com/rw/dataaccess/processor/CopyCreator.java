package com.rw.dataaccess.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.readonly.CopyInfoCfgIF;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.copypve.CopyInfoCfgDAO;
import com.rwbase.dao.copypve.CopyType;
import com.rwbase.dao.copypve.pojo.CopyData;
import com.rwbase.dao.copypve.pojo.CopyInfoCfg;
import com.rwbase.dao.copypve.pojo.TableCopyData;

public class CopyCreator implements DataExtensionCreator<TableCopyData>{

	@Override
	public TableCopyData create(String userId) {
		List<CopyInfoCfg> cfgList = CopyInfoCfgDAO.getInstance().getAllCfg();
		TableCopyData pTableCopyData = new TableCopyData();
		pTableCopyData.setUserId(userId);
		List<CopyData> copyList = new ArrayList<CopyData>();
		for (CopyInfoCfgIF cfg : cfgList)
		{
			CopyData data = new CopyData();
			data.setCopyCount(cfg.getCount());
//			data.setResetCount(getRestCountByCopyType(cfg.getType()));
			data.setCopyType(cfg.getType());
			data.setInfoId(cfg.getId());
			data.setPassMap(getCelestialDegreeMap());
			copyList.add(data);
		}
		pTableCopyData.setCopyList(copyList);
		return pTableCopyData;
	}
	
	private ConcurrentHashMap<String, Integer> getCelestialDegreeMap() {
		ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
		List<CopyInfoCfg> listInfo = CopyInfoCfgDAO.getInstance().getAllCfg();
		int i;
		String[] degrees;
		for (CopyInfoCfg cfg : listInfo) {
			if (cfg.getType() != CopyType.COPY_TYPE_CELESTIAL)
				continue;
			degrees = cfg.getDegreeID().split(",");
			for (i = 0; i < degrees.length; i++) {
				map.put(degrees[i], 0);
			}
		}
		return map;
	}

}
