package com.rwbase.dao.fightinggrowth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.copy.itemPrivilege.PrivilegeDescItem;
import com.rwbase.dao.fightinggrowth.pojo.FSUserFightingGrowthTitleCfg;

public class FSUserFightingGrowthTitleCfgDAO extends CfgCsvDao<FSUserFightingGrowthTitleCfg> {
	
	private String _firstTitleKey;

	public static FSUserFightingGrowthTitleCfgDAO getInstance() {
		return SpringContextUtil.getBean(FSUserFightingGrowthTitleCfgDAO.class);
	}
	
	private Map<Integer, Integer> parseItemString(String str) {
		if(str == null || (str = str.trim()).length() == 0) {
			return Collections.emptyMap();
		}
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		String[] array = str.split(";");
		for(int i = 0; i < array.length; i++) {
			String[] single = array[i].split(",");
			map.put(Integer.parseInt(single[0]), Integer.parseInt(single[1]));
		}
		return map;
	}
	
	private FSUserFightingGrowthTitleCfg getFightingGrowthTitle(String key, boolean getNext) {
		FSUserFightingGrowthTitleCfg titleCfg;
		if (StringUtils.isEmpty(key)) {
			titleCfg = this.getCfgById(_firstTitleKey);
		} else {
			titleCfg = this.getCfgById(key);
			if (getNext) {
				titleCfg = this.getCfgById(titleCfg.getNextTitleKey());
			}
		}
		return titleCfg;
	}
	
	@Override
	protected Map<String, FSUserFightingGrowthTitleCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map("fightingGrowth/FightingGrowthTitle.csv", FSUserFightingGrowthTitleCfg.class);
		for (Iterator<String> itr = this.cfgCacheMap.keySet().iterator(); itr.hasNext();) {
			FSUserFightingGrowthTitleCfg temp = cfgCacheMap.get(itr.next());
			temp.setItemRequiredMap(this.parseItemString(temp.getItemRequired()));
			temp.setItemRewardMap(this.parseItemString(temp.getRewards()));
			if (temp.isFirst()) {
				if (this._firstTitleKey == null) {
					this._firstTitleKey = temp.getKey();
				} else {
					throw new RuntimeException("fightingGrowth/FightingGrowthTitle.csv，多于一个isFirst为true！");
				}
			}
			temp.setPrivilegeDescItem(parseStringToPrivilege(temp.getPrivilege()));
		}
		return this.cfgCacheMap;
	}
	
	/**
	 * 
	 * 获取战力称号配置，如果key为null或者""，则会返回第一个称号的配置数据
	 * 
	 * @param key
	 * @return
	 */
	public FSUserFightingGrowthTitleCfg getFightingGrowthTitleCfgSafely(String key) {
		return this.getFightingGrowthTitle(key, false);
	}

	/**
	 * 
	 * 获取currentKey的下一个战力称号配置，如果currentKey为null或者""，则会返回第一个称号的配置数据
	 * 
	 * @param currentKey
	 * @return
	 */
	public FSUserFightingGrowthTitleCfg getNextFightingGrowthTitleCfgSafely(String currentKey) {
		return this.getFightingGrowthTitle(currentKey, true);
	}
	
	private List<PrivilegeDescItem> parseStringToPrivilege(String desc){
		List<PrivilegeDescItem> itemList = new ArrayList<PrivilegeDescItem>();
		if(StringUtils.isBlank(desc)) return itemList;
		String[] privStrArr = desc.split("\\|");
		for(String singleDesc : privStrArr){
			String[] descStrArr = singleDesc.split("_");
			if(descStrArr.length < 3) continue;
			PrivilegeDescItem item = new PrivilegeDescItem();
			item.setItemID(Integer.parseInt(descStrArr[0]));
			item.setValue(Float.parseFloat(descStrArr[1]));
			item.setPersent(StringUtils.equals(descStrArr[2], "0"));
			itemList.add(item);
		}
		return itemList;
	}
}
