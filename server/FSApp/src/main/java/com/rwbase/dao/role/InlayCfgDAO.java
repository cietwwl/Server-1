package com.rwbase.dao.role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.role.pojo.InlayCfg;
import com.rwbase.dao.role.pojo.InlayTemplate;

public class InlayCfgDAO extends CfgCsvDao<InlayCfg> {

	public static InlayCfgDAO getInstance() {
		return SpringContextUtil.getBean(InlayCfgDAO.class);
	}

	private Map<String, InlayTemplate> inlayTmpMap = new HashMap<String, InlayTemplate>();

	@Override
	public Map<String, InlayCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Inlay/InlayCfg.csv", InlayCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {

			Map<String, InlayTemplate> inlayTmpMap = new HashMap<String, InlayTemplate>();
			for (Entry<String, InlayCfg> e : cfgCacheMap.entrySet()) {
				InlayTemplate tmp = new InlayTemplate(e.getValue());
				inlayTmpMap.put(e.getKey(), tmp);
			}

			this.inlayTmpMap = inlayTmpMap;
		}

		return cfgCacheMap;
	}

	public InlayCfg getConfig(String roleId) {

		roleId = roleId.split("_")[0];

		List<InlayCfg> list = super.getAllCfg();
		for (InlayCfg cfg : list) {
			if (cfg.getRoleId().equals(roleId)) {
				return cfg;
			}
		}
		return null;
	}

	/**
	 * 获取角色镶嵌的宝石个数对应的属性
	 * 
	 * @param heroModelId
	 * @return
	 */
	public InlayTemplate getInlayTemplate(String heroModelId) {
		return inlayTmpMap.get(heroModelId);
	}
}