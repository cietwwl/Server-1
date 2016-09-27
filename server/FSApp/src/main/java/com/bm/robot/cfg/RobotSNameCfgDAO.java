package com.bm.robot.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class RobotSNameCfgDAO extends CfgCsvDao<RobotFNameCfg> {
	
	private static String[] sNameArr;
	
	
	public static RobotSNameCfgDAO getInstance() {
		return SpringContextUtil.getBean(RobotSNameCfgDAO.class);
	}
	

	@Override
	public Map<String, RobotFNameCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("robotName/sName.csv",RobotFNameCfg.class);
		toArr();
		return cfgCacheMap;
	}
	
	private void toArr(){
		sNameArr = new String[cfgCacheMap.size()];
		
		Collection<RobotFNameCfg> values = cfgCacheMap.values();
		int index = 0;
		for (RobotFNameCfg robotFNameCfg : values) {
			sNameArr[index] = robotFNameCfg.getName();
			index++;
		}
		
	}
	
	public int getSize(){
		return sNameArr.length;
	}
	
	public String get(int index){
		return sNameArr[index];
	}
	
}
