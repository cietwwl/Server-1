package com.rwbase.dao.group.pojo.cfg.dao;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.group.pojo.cfg.GroupBaseConfigTemplate;
import com.rwbase.dao.group.pojo.cfg.GroupConfigCfg;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class GroupConfigCfgDAO extends CfgCsvDao<GroupConfigCfg> {
	// private static GroupConfigCfgDAO dao;

	private GroupBaseConfigTemplate template;// 唯一的模版配置

	/**
	 * 获取GroupConfigCfgDao表的唯一Dao
	 * 
	 * @return
	 */
	public static GroupConfigCfgDAO getDAO() {
		return SpringContextUtil.getBean(GroupConfigCfgDAO.class);
	}

	private GroupConfigCfgDAO() {
	}

	@Override
	public Map<String, GroupConfigCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Group/GuildBaseConfigCfg.csv", GroupConfigCfg.class);
		// 初始化数据
		List<GroupConfigCfg> allCfg = getAllCfg();
		if (allCfg != null && !allCfg.isEmpty()) {
			GroupConfigCfg cfg = allCfg.get(0);
			template = new GroupBaseConfigTemplate(cfg);
		}
		return cfgCacheMap;
	}

	/**
	 * 返回帮派唯一的基础配置表
	 * 
	 * @return
	 */
	public GroupBaseConfigTemplate getUniqueCfg() {
		return template;
	}

	/**
	 * 获取帮派日志记录的最大条数
	 * 
	 * @return
	 */
	public int getGroupLogMaxCacheSize() {
		return template == null ? 12 : template.getGroupLogCacheSize();
	}
}