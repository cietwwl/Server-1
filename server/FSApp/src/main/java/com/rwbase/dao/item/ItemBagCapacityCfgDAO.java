package com.rwbase.dao.item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.ItemBagCapacityCfg;
import com.rwproto.ItemBagProtos.EItemTypeDef;

public class ItemBagCapacityCfgDAO extends CfgCsvDao<ItemBagCapacityCfg> {

	public static ItemBagCapacityCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(ItemBagCapacityCfgDAO.class);
	}

	private Map<Integer, ItemBagCapacityCfg> itemBagCapacityMap;

	@Override
	protected Map<String, ItemBagCapacityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/ItemBagCapacity.csv", ItemBagCapacityCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			Map<Integer, ItemBagCapacityCfg> map = new HashMap<Integer, ItemBagCapacityCfg>(cfgCacheMap.size());

			for (Entry<String, ItemBagCapacityCfg> e : cfgCacheMap.entrySet()) {
				ItemBagCapacityCfg cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				map.put(cfg.getItemType(), cfg);
			}

			if (!map.isEmpty()) {
				itemBagCapacityMap = Collections.unmodifiableMap(map);
			}
		}

		return cfgCacheMap;
	}

	/**
	 * 获取背包的上限
	 * 
	 * @param type
	 * @return
	 */
	public int getItemBagCapacity(EItemTypeDef type) {
		if (itemBagCapacityMap == null || itemBagCapacityMap.isEmpty()) {
			return 0;
		}

		ItemBagCapacityCfg cfg = itemBagCapacityMap.get(type.getNumber());
		return cfg == null ? 0 : cfg.getCapacity();
	}

	/**
	 * 获取显示的背包的名字
	 * 
	 * @param type
	 * @return
	 */
	public String getItemBagName(EItemTypeDef type) {
		if (itemBagCapacityMap == null || itemBagCapacityMap.isEmpty()) {
			return "";
		}

		ItemBagCapacityCfg cfg = itemBagCapacityMap.get(type.getNumber());
		return cfg == null ? "" : cfg.getBagName();
	}
}