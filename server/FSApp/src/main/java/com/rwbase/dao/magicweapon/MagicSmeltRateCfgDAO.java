package com.rwbase.dao.magicweapon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.Weight;
import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.pojo.AptitudeGroup;
import com.rwbase.dao.magicweapon.pojo.MagicSmeltRateCfg;

public class MagicSmeltRateCfgDAO  extends CfgCsvDao<MagicSmeltRateCfg>{

	public static MagicSmeltRateCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicSmeltRateCfgDAO.class);
	}
	
	List<AptitudeGroup> AptitudeGroupList; 
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Map<String, MagicSmeltRateCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("MagicWeapon/MagicSmeltRate.csv", MagicSmeltRateCfg.class);
		AptitudeGroupList = new ArrayList<AptitudeGroup>();
		for (Iterator<Entry<String, MagicSmeltRateCfg> > iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, MagicSmeltRateCfg> entry = iterator.next();
			MagicSmeltRateCfg cfg = entry.getValue();
			String aptitude = cfg.getAptitudeGroup();
			String[] split = aptitude.split("~");
			int min = Integer.parseInt(split[0]);
			int max = Integer.parseInt(split[1]);
			if(min > max){
				GameLog.error("MagicSmeltRateCfgDAO", "MagicSmeltRateCfgDAO", "MagicSmeltRateCfg 资质段填写错误!Aptitude:" + cfg.getAptitudeGroup());
				continue;
			}
			cfg.setMinAptitude(min);
			cfg.setMaxAptitude(max);
			
			AptitudeGroup aptitudeGroup = checkExistAptitudeGroup(min, max);
			if(aptitudeGroup == null){
				aptitudeGroup = new AptitudeGroup();
				aptitudeGroup.setMinAptitude(min);
				aptitudeGroup.setMaxAptitude(max);
				AptitudeGroupList.add(aptitudeGroup);
			}
			aptitudeGroup.addProMap(cfg);
		}
		
		for (AptitudeGroup aptitudeGroup : AptitudeGroupList) {
			Weight weight = new Weight<MagicSmeltRateCfg>(aptitudeGroup.getProMap());
			aptitudeGroup.setWeight(weight);
		}
		
		return cfgCacheMap;
	}
	
	private AptitudeGroup checkExistAptitudeGroup(int min, int max){
		for (AptitudeGroup aptitudeGroup : AptitudeGroupList) {
			if(aptitudeGroup.getMaxAptitude() == max && aptitudeGroup.getMinAptitude() == min){
				return aptitudeGroup;
			}
		}
		return null;
	}
	
	public MagicSmeltRateCfg magicSmelt(int aptitude){
		for (AptitudeGroup aptitudeGroup : AptitudeGroupList) {
			if(aptitudeGroup.getMinAptitude() <= aptitude && aptitudeGroup.getMaxAptitude()>= aptitude){
				return aptitudeGroup.getSmeltResult();
			}
		}
		return null;
	}
}
