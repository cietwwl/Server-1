package com.rw.service.friend.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class RecommandConditionCfgDAO extends CfgCsvDao<RecommandConditionCfg> {

	private List<RecommandConditionCfg> orderConditions;

	public static RecommandConditionCfgDAO getInstance() {
		return SpringContextUtil.getBean(RecommandConditionCfgDAO.class);
	}

	@Override
	protected Map<String, RecommandConditionCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("friend/recommandCondtion.csv", RecommandConditionCfg.class);
		ArrayList<RecommandConditionCfg> list = new ArrayList<RecommandConditionCfg>();
		for (RecommandConditionCfg cfg : cfgCacheMap.values()) {
			if (cfg.getRandomCount() <= cfg.getCount()) {
				throw new IllegalArgumentException("随机人数少于推荐人数：randomCount=" + cfg.getRandomCount() + ",count=" + cfg.getCount());
			}
			list.add(cfg);
		}
		Collections.sort(list, new Comparator<RecommandConditionCfg>() {

			@Override
			public int compare(RecommandConditionCfg cfg1, RecommandConditionCfg cfg2) {
				return cfg1.getSeqId() - cfg2.getSeqId();
			}
		});
		this.orderConditions = Collections.unmodifiableList(list);
		return cfgCacheMap;
	}

	public List<RecommandConditionCfg> getOrderConditions() {
		return orderConditions;
	}

}
