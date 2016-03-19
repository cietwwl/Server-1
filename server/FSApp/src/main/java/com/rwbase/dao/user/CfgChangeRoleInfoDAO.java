package com.rwbase.dao.user;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.user.pojo.ChangeRoleInfoCfg;

public class CfgChangeRoleInfoDAO extends  CfgCsvDao<ChangeRoleInfoCfg> {

		private CfgChangeRoleInfoDAO()
		{
			
		}
		private static CfgChangeRoleInfoDAO instance = new CfgChangeRoleInfoDAO();
		public static CfgChangeRoleInfoDAO getInstance() {
			
			return instance;
		}
		
		@Override
		public Map<String, ChangeRoleInfoCfg> initJsonCfg() {
			cfgCacheMap = CfgCsvHelper.readCsv2Map("PlayerCfg/ChangeRoleInfoCfg.csv",ChangeRoleInfoCfg.class);
			return cfgCacheMap;
		}
		
		public ChangeRoleInfoCfg getCfgByTime(long time)
		{
			List<ChangeRoleInfoCfg> allCfgs = sortCfg();
			if(allCfgs != null && allCfgs.size() > 0){
				for (int i = 0; i < allCfgs.size(); i++) {
					if(time > allCfgs.get(i).getTime())
						return allCfgs.get(i - 1);
				}
			}
			return null;
		}
		
		public  ChangeRoleInfoCfg getCfg()
		{
			List<ChangeRoleInfoCfg> allCfgs = sortCfg();
			if(allCfgs != null && allCfgs.size() > 0)
				return allCfgs.get(0);
			return null;
		}

		private List<ChangeRoleInfoCfg> sortCfg() {
			List<ChangeRoleInfoCfg> allCfgs = super.getAllCfg();
			Collections.sort(allCfgs,new Comparator<ChangeRoleInfoCfg>()
			{
				public int compare(ChangeRoleInfoCfg o1, ChangeRoleInfoCfg o2) {
					if(o1.getTime() < o2.getTime()) return 1;
					if(o1.getTime() > o2.getTime()) return -1;
					return 0;
				}});
			return allCfgs;
		}
}
