package com.rwbase.dao.group.pojo.cfg.dao;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.group.pojo.cfg.GroupDonateCfg;

/*
 * @author HC
 * @date 2016年1月23日 下午2:28:59
 * @Description 帮派贡献配置的DAO
 */
public class GroupDonateCfgDAO extends CfgCsvDao<GroupDonateCfg> {
	// private static GroupDonateCfgDAO dao;

	public static GroupDonateCfgDAO getDAO() {
		// if (dao == null) {
		// dao = new GroupDonateCfgDAO();
		// }
		//
		// return dao;
		return SpringContextUtil.getBean(GroupDonateCfgDAO.class);
	}

	private GroupDonateCfgDAO() {
	}

	@Override
	public Map<String, GroupDonateCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Group/DonateCfg.csv", GroupDonateCfg.class);
		return cfgCacheMap;
	}
}