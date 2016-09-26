package com.bm.robot.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class RobotFNameCfgDAO extends CfgCsvDao<RobotFNameCfg> {
	
	private static String[] fNameArr;
	
	
	public static RobotFNameCfgDAO getInstance() {
		return SpringContextUtil.getBean(RobotFNameCfgDAO.class);
	}

	@Override
	public Map<String, RobotFNameCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("robotName/fName.csv",RobotFNameCfg.class);

		toArr();

		return cfgCacheMap;
	}
	
	private void toArr(){
		fNameArr = new String[cfgCacheMap.size()];
		
		Collection<RobotFNameCfg> values = cfgCacheMap.values();
		int index = 0;
		for (RobotFNameCfg robotFNameCfg : values) {
			fNameArr[index] = robotFNameCfg.getName();
			index++;
		}
		
	}
	
	public int getSize(){
		return fNameArr.length;
	}
	
	public String get(int index){
		return fNameArr[index];
	}
}
