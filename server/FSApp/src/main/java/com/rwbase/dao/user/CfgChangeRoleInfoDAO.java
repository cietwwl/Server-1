package com.rwbase.dao.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.user.pojo.ChangeRoleInfoCfg;

public class CfgChangeRoleInfoDAO extends CfgCsvDao<ChangeRoleInfoCfg> {

	public static CfgChangeRoleInfoDAO getInstance() {
		return SpringContextUtil.getBean(CfgChangeRoleInfoDAO.class);
	}

	private static Comparator<ChangeRoleInfoCfg> comparator = new Comparator<ChangeRoleInfoCfg>() {

		@Override
		public int compare(ChangeRoleInfoCfg o1, ChangeRoleInfoCfg o2) {
			if (o1.getTime() < o2.getTime())
				return 1;
			if (o1.getTime() > o2.getTime())
				return -1;
			return 0;
		}
	};

	/** 排序过的列表 */
	private List<ChangeRoleInfoCfg> sortCfgList = new ArrayList<ChangeRoleInfoCfg>();

	@Override
	public Map<String, ChangeRoleInfoCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PlayerCfg/ChangeRoleInfoCfg.csv", ChangeRoleInfoCfg.class);

		List<ChangeRoleInfoCfg> cfgList = new ArrayList<ChangeRoleInfoCfg>(getAllCfg());
		Collections.sort(cfgList, comparator);

		this.sortCfgList = Collections.unmodifiableList(cfgList);

		return cfgCacheMap;
	}

	public ChangeRoleInfoCfg getCfgByTime(long time) {
		if (time <= 0) {
			return null;
		}

		int index = -1;

		for (int i = 0, size = sortCfgList.size(); i < size; i++) {
			ChangeRoleInfoCfg changeRoleInfoCfg = sortCfgList.get(i);
			if (changeRoleInfoCfg == null) {
				continue;
			}

			int needTime = changeRoleInfoCfg.getTime();
			if (time > needTime) {
				index = i;
				break;
			}
		}

		if (index < 0) {
			return null;
		}

		return sortCfgList.get(index);
	}

	public ChangeRoleInfoCfg getCfg() {
		return sortCfgList.get(0);
	}
}