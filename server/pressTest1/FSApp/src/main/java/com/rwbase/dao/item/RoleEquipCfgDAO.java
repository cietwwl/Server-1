package com.rwbase.dao.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.RoleEquipCfg;

public class RoleEquipCfgDAO  extends CfgCsvDao<RoleEquipCfg>{
	private static RoleEquipCfgDAO instance = new RoleEquipCfgDAO();
	private RoleEquipCfgDAO(){}
	public static RoleEquipCfgDAO getInstance(){
		return instance;
	}

	@Override
	public Map<String, RoleEquipCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/RoleEquip.csv",RoleEquipCfg.class);
		return cfgCacheMap;
	}
	
	public RoleEquipCfg GetCfg(int newSlot,int newCareer)
	{
		List<RoleEquipCfg> arr = getAllCfg();
		for (RoleEquipCfg Cfg : arr) {
			if(Cfg.getEquipType() == newSlot && Cfg.getCareerType() == newCareer)
				return Cfg;
		}
		return null;
	}
	
	public List<RoleEquipCfg> GetCfgList(int newCareer){
		List<RoleEquipCfg> allArr = getAllCfg();
		List<RoleEquipCfg> arr = new ArrayList<RoleEquipCfg>(); 
		for (RoleEquipCfg Cfg : allArr) {
			if(Cfg.getCareerType() == newCareer)
			{
				arr.add(Cfg);
			}
		}
		return arr;
	}
	
}
