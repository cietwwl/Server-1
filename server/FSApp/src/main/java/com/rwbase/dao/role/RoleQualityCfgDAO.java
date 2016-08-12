package com.rwbase.dao.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.role.pojo.RoleQualityCfg;

public class RoleQualityCfgDAO extends CfgCsvDao<RoleQualityCfg> {
	public static RoleQualityCfgDAO getInstance() {
		return SpringContextUtil.getBean(RoleQualityCfgDAO.class);
	}

	@Override
	public Map<String, RoleQualityCfg> initJsonCfg() {
		Map<String, RoleQualityCfg> readCsv2Map = CfgCsvHelper.readCsv2Map("role/RoleQualityCfg.csv", RoleQualityCfg.class);
		if (readCsv2Map != null && !readCsv2Map.isEmpty()) {
			for (Entry<String, RoleQualityCfg> e : readCsv2Map.entrySet()) {
				e.getValue().initData();
			}
		}
		return cfgCacheMap = readCsv2Map;
	}

	public RoleQualityCfg getConfig(String id) {
		return (RoleQualityCfg) getCfgById(id);
	}

	public RoleQualityCfg getConfigByNextId(String nextId) {
		List<RoleQualityCfg> list = super.getAllCfg();
		for (RoleQualityCfg cfg : list) {
			if (cfg.getNextId().equals(nextId)) {
				return cfg;
			}
		}
		return null;
	}

	/**
	 * 根据当前ID获得下一品阶ID
	 * 
	 * @return
	 */
	public RoleQualityCfg getNextConfig(String id) {
		List<RoleQualityCfg> list = super.getAllCfg();
		for (RoleQualityCfg cfg : list) {
			if (cfg.getId().equals(id)) {
				RoleQualityCfg nextcfg = getConfig(cfg.getNextId());
				if (nextcfg != null) {
					return nextcfg;
				}
				break;
			}
		}
		return null;
	}

	public ArrayList<Integer> getEquipList(String id) {
		ArrayList<Integer> list = new ArrayList<Integer>(6);
		RoleQualityCfg cfg = getConfig(id);
		if (cfg == null) {
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
	
	/**
	 * 在排除列表中的不返回
	 * @param id
	 * @param excludePositions 排除位置列表
	 * @return
	 */
	public ArrayList<Integer> getEquipList(String id,int[] excludePositions) {
		if (excludePositions == null || excludePositions.length <=0)
			return getEquipList(id);
		
		ArrayList<Integer> list = new ArrayList<Integer>(6);
		RoleQualityCfg cfg = getConfig(id);
		if (cfg == null) {
			return list;
		}
		filterAdd(list,cfg.getEquip1(),1,excludePositions);
		filterAdd(list,cfg.getEquip2(),2,excludePositions);
		filterAdd(list,cfg.getEquip3(),3,excludePositions);
		filterAdd(list,cfg.getEquip4(),4,excludePositions);
		filterAdd(list,cfg.getEquip5(),5,excludePositions);
		filterAdd(list,cfg.getEquip6(),6,excludePositions);
		return list;
	}
	
	private void filterAdd(ArrayList<Integer> list,int equipId,int index,int[] excludePositions){
		for (int i =0;i<excludePositions.length;i++){
			if (excludePositions[i] == index){
				return;
			}
		}
		list.add(equipId);
	}

	public int getQuality(String qualityId) {
		RoleQualityCfg cfg = getConfig(qualityId);
		if (cfg == null) {
			return 0;
		}
		return cfg.getQuality();
	}
}
