package com.rwbase.dao.item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.item.useeffect.IItemUseEffect;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.ItemUseEffectCfg;
import com.rwbase.dao.item.pojo.ItemUseEffectTemplate;

/*
 * @author HC
 * @date 2016年5月18日 下午2:37:34
 * @Description 
 */
public class ItemUseEffectCfgDAO extends CfgCsvDao<ItemUseEffectCfg> {

	public static ItemUseEffectCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(ItemUseEffectCfgDAO.class);
	}

	/** <对应的处理类路径,使用的效果实现类> */
	private Map<String, IItemUseEffect> useEffectClassMap = new HashMap<String, IItemUseEffect>();
	/** <道具的ModelId,ItemUseEffectTemplate> */
	private Map<Integer, ItemUseEffectTemplate> effectMap = new HashMap<Integer, ItemUseEffectTemplate>();

	@Override
	protected Map<String, ItemUseEffectCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/ItemUseEffectCfg.csv", ItemUseEffectCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {

			HashMap<String, IItemUseEffect> useEffectClassMap = new HashMap<String, IItemUseEffect>();
			HashMap<Integer, ItemUseEffectTemplate> effectMap = new HashMap<Integer, ItemUseEffectTemplate>();

			for (Entry<String, ItemUseEffectCfg> e : cfgCacheMap.entrySet()) {
				ItemUseEffectCfg value = e.getValue();
				if (value == null) {
					continue;
				}

				effectMap.put(value.getModelId(), new ItemUseEffectTemplate(value));

				// 检查
				String classPath = value.getUseEffectClass();
				if (StringUtils.isEmpty(classPath)) {
					throw new ExceptionInInitializerError(String.format("ItemUseEffectCfg配置表[%s]的物品配置的处理类路径是空的", value.getModelId()));
				}

				IItemUseEffect useEffect = useEffectClassMap.get(classPath);
				if (useEffect == null) {
					try {
						Class<?> forName = Class.forName(classPath);
						useEffect = (IItemUseEffect) forName.newInstance();
						useEffectClassMap.put(classPath, useEffect);
					} catch (ClassNotFoundException exception) {
						GameLog.error("解析ItemUseEffect表", String.valueOf(value.getModelId()), String.format("[%s]表里填写对应的处理类无法创建出来实例", classPath), exception);
					} catch (InstantiationException exception) {
						GameLog.error("解析ItemUseEffect表", String.valueOf(value.getModelId()), String.format("[%s]表里填写对应的处理类无法创建出来实例", classPath), exception);
					} catch (IllegalAccessException exception) {
						GameLog.error("解析ItemUseEffect表", String.valueOf(value.getModelId()), String.format("[%s]表里填写对应的处理类无法创建出来实例", classPath), exception);
					}
				}
			}

			this.effectMap = Collections.unmodifiableMap(effectMap);

			this.useEffectClassMap = Collections.unmodifiableMap(useEffectClassMap);
		}

		return cfgCacheMap;
	}

	/**
	 * 获取道具对应的使用效果的模版表
	 * 
	 * @param modelId
	 * @return
	 */
	public ItemUseEffectTemplate getUseEffectTemplateByModelId(int modelId) {
		return this.effectMap.get(modelId);
	}

	/**
	 * 获取道具对应的使用效果的处理类
	 * 
	 * @param modelId
	 * @return
	 */
	public IItemUseEffect getItemUseEffectByModelId(int modelId) {
		ItemUseEffectTemplate tmp = this.effectMap.get(modelId);
		if (tmp == null) {
			return null;
		}

		return this.useEffectClassMap.get(tmp.getUseEffectClass());
	}
}