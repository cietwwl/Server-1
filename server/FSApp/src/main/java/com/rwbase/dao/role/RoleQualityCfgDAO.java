package com.rwbase.dao.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.role.pojo.RoleQualityCfg;

public class RoleQualityCfgDAO extends CfgCsvDao<RoleQualityCfg>{
	public static RoleQualityCfgDAO getInstance() {
		return SpringContextUtil.getBean(RoleQualityCfgDAO.class);
	}
	@Override
	public Map<String, RoleQualityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("role/RoleQualityCfg.csv",RoleQualityCfg.class);
		return cfgCacheMap;
	}

	public RoleQualityCfg getConfig(String id){
		return (RoleQualityCfg)getCfgById(id);
	}
	
	public RoleQualityCfg getConfigByNextId(String nextId){
		List<RoleQualityCfg> list = super.getAllCfg();
		for (RoleQualityCfg cfg : list) {
			if(cfg.getNextId().equals(nextId)){
				return cfg;
			}
		}
		return null;
	}
	
	/**
	 * 根据当前ID获得下一品阶ID
	 * @return
	 */
	public RoleQualityCfg getNextConfig(String id){
		List<RoleQualityCfg> list = super.getAllCfg();
		for (RoleQualityCfg cfg : list) {
			if(cfg.getId().equals(id)){
				RoleQualityCfg nextcfg = getConfig(cfg.getNextId());
				if(nextcfg != null){
					return nextcfg;
				}
				break;
			}
		}
		return null;
	}
	
	public List<Integer> getEquipList(String id){
		List<Integer> list = new ArrayList<Integer>();
		RoleQualityCfg cfg = getConfig(id);
		if(cfg == null){
			return list;
		}
		list.add(cfg.getEquip1());
		list.add(cfg.getEquip2());
		list.add(cfg.getEquip3());
		list.add(cfg.getEquip4());
		list.add(cfg.getEquip5());
		list.add(cfg.getEquip6());
		return list;
	}
	public int getQuality(String qualityId) {
		RoleQualityCfg cfg = getConfig(qualityId);
		if(cfg == null){
			return 0;
		}
		return cfg.getQuality();
	}
}
