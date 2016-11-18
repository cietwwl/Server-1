package com.rwbase.dao.fightinggrowth;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.fightinggrowth.FSFightingGrowthWayType;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.copy.itemPrivilege.PrivilegeDescItem;
import com.rwbase.dao.copy.pojo.ItemInfo;
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
			if (single.length > 1 && single[0].trim().length() > 0) {
				map.put(Integer.parseInt(single[0]), Integer.parseInt(single[1]));
			}
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
	
	private List<Field> getExpectedFightingFields() {
		Field[] allFields = FSUserFightingGrowthTitleCfg.class.getDeclaredFields();
		List<Field> list = new ArrayList<Field>();
		for (int i = 0, size = allFields.length; i < size; i++) {
			Field tempField = allFields[i];
			if (tempField.isAnnotationPresent(FightingGrowthTypeTarget.class)) {
				tempField.setAccessible(true);
				list.add(tempField);
			}
		}
		return list;
	}
	
	private void generateExpectedFightingMap(FSUserFightingGrowthTitleCfg cfg, List<Field> targetFields) throws IllegalAccessException {
		Map<FSFightingGrowthWayType, Integer> map = new HashMap<FSFightingGrowthWayType, Integer>(EnumSet.allOf(FSFightingGrowthWayType.class).size(), 1.2f);
		FightingGrowthTypeTarget temp;
		Field tempField;
		FSFightingGrowthWayType tempType;
		for (int i = 0, size = targetFields.size(); i < size; i++) {
			tempField = targetFields.get(i);
			temp = tempField.getAnnotation(FightingGrowthTypeTarget.class);
			tempType = temp.wayType();
			Integer now = map.get(tempType);
			int value = tempField.getInt(cfg);
			if (now == null) {
				now = value;
			} else {
				now += value;
			}
			map.put(tempType, now);
		}
		cfg.setExpectedFightingMap(map);
	}
	
	@Override
	protected Map<String, FSUserFightingGrowthTitleCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map("fightingGrowth/FightingGrowthTitle.csv", FSUserFightingGrowthTitleCfg.class);
		List<Field> expectedFightingFields = getExpectedFightingFields();
		String firstTitleKey = null;
		for (Iterator<String> itr = this.cfgCacheMap.keySet().iterator(); itr.hasNext();) {
			FSUserFightingGrowthTitleCfg temp = cfgCacheMap.get(itr.next());
			Map<Integer, Integer> itemRewardMap = this.parseItemString(temp.getRewards());
			temp.setItemRequiredMap(this.parseItemString(temp.getItemRequired()));
			temp.setItemRewardMap(itemRewardMap);
			List<ItemInfo> itemRewardList = new ArrayList<ItemInfo>(itemRewardMap.size());
			for (Iterator<Integer> keyItr = itemRewardMap.keySet().iterator(); keyItr.hasNext();) {
				Integer itemCfgId = keyItr.next();
				itemRewardList.add(new ItemInfo(itemCfgId, itemRewardMap.get(itemCfgId)));
			}
			temp.setItemRewardList(itemRewardList);
			if (temp.isFirst()) {
				if (firstTitleKey == null) {
					firstTitleKey = temp.getKey();
				} else {
					throw new RuntimeException("fightingGrowth/FightingGrowthTitle.csv，多于一个isFirst为true！");
				}
			}
			temp.setPrivilegeDescItem(parseStringToPrivilege(temp.getPrivilege()));
			try {
				generateExpectedFightingMap(temp, expectedFightingFields);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(firstTitleKey == null) {
			throw new RuntimeException("fightingGrowth/FightingGrowthTitle.csv，没有第一个战力称号！");
		}
		this._firstTitleKey = firstTitleKey;
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
	
	@Override
	public void CheckConfig() {
		int exCount = 0;
		FSUserFightingGrowthTitleCfg cfg;
		for (Iterator<String> keyItr = cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			cfg = cfgCacheMap.get(keyItr.next());
			if (cfg.getExpectedFightingMap() == null || cfg.getExpectedFightingMap().isEmpty()) {
				exCount++;
				GameLog.error("FSUserFightingGrowthTitleCfgDAO", "CheckConfig", "cfg：" + cfg.getKey() + "，不存在期望战力！");
			}
		}
		if(exCount > 0) {
			throw new IllegalStateException("战力成长系统，配置校验不通过！");
		}
	}
}
