package com.rwbase.dao.dropitem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class DropCfgDAO extends CfgCsvDao<DropCfg> {

	public static DropCfgDAO getInstance() {
		return SpringContextUtil.getBean(DropCfgDAO.class);
	}
	

	private HashMap<Integer, List<Integer>> allDropListMap;
	private HashMap<Integer, List<DropCfg>> dropCfgMap;


	@Override
	public Map<String, DropCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/DropRecord.csv", DropCfg.class);
		HashMap<Integer, ArrayList<Integer>> allDropListMap_ = new HashMap<Integer, ArrayList<Integer>>();
		HashMap<Integer, ArrayList<DropCfg>> dropCfgMap_ = new HashMap<Integer, ArrayList<DropCfg>>();
		for (Object value : cfgCacheMap.values()) {
			DropCfg dropCfg = (DropCfg) value;
			Integer dropRuleId = dropCfg.getItemsFormula();
			Integer recordId = dropCfg.getId();
			ArrayList<Integer> allDropList = allDropListMap_.get(dropRuleId);
			if (allDropList == null) {
				allDropList = new ArrayList<Integer>();
				allDropListMap_.put(dropRuleId, allDropList);
			}
			allDropList.add(recordId);
			ArrayList<DropCfg> dropCfgList = dropCfgMap_.get(dropRuleId);
			if (dropCfgList == null) {
				dropCfgList = new ArrayList<DropCfg>();
				dropCfgMap_.put(dropRuleId, dropCfgList);
			}
			dropCfgList.add(dropCfg);
		}
		HashMap<Integer, List<Integer>> allDropListMap = new HashMap<Integer, List<Integer>>(allDropListMap_.size());
		for (Map.Entry<Integer, ArrayList<Integer>> entry : allDropListMap_.entrySet()) {
			allDropListMap.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
		}

		HashMap<Integer, List<DropCfg>> dropCfgMap = new HashMap<Integer, List<DropCfg>>(allDropListMap_.size());
		for (Map.Entry<Integer, ArrayList<DropCfg>> entry : dropCfgMap_.entrySet()) {
			dropCfgMap.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
		}
		this.dropCfgMap = dropCfgMap;
		this.allDropListMap = allDropListMap;
		return cfgCacheMap;
	}

	public List<Integer> getAllDropIds(int dropRuleId) {
		if (allDropListMap == null) {
			initJsonCfg();
		}
		return this.allDropListMap.get(dropRuleId);
	}

	public List<DropCfg> getDropCfg(int dropRuleId) {
		if (dropCfgMap == null) {
			initJsonCfg();
		}
		return this.dropCfgMap.get(dropRuleId);
	}

}
