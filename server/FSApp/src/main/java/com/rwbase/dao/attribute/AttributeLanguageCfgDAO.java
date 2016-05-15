package com.rwbase.dao.attribute;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.attribute.cfg.AttributeLanguageCfg;

/*
 * @author HC
 * @date 2016年5月13日 下午4:48:15
 * @Description 属性类名字类型对应值
 */
public class AttributeLanguageCfgDAO extends CfgCsvDao<AttributeLanguageCfg> {

	public static AttributeLanguageCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(AttributeLanguageCfgDAO.class);
	}

	/** <中文名字,AttributeType> */
	private Map<String, AttributeType> mapping = new HashMap<String, AttributeType>();

	@Override
	protected Map<String, AttributeLanguageCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("AttributeLanguage/AttributeLanguageCfg.csv", AttributeLanguageCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {

			Map<String, AttributeType> mapping = new HashMap<String, AttributeType>(cfgCacheMap.size());

			for (Entry<String, AttributeLanguageCfg> e : cfgCacheMap.entrySet()) {
				AttributeLanguageCfg value = e.getValue();
				if (value == null) {
					continue;
				}

				if (value.getAttrType() <= 0) {
					continue;
				}

				AttributeType attrType = AttributeType.getAttributeType(value.getAttrType());
				if (attrType == null) {
					continue;
				}

				mapping.put(value.getChinese(), attrType);
			}

			this.mapping = Collections.unmodifiableMap(mapping);

			System.err.println(JsonUtil.writeValue(mapping));
		}

		return cfgCacheMap;
	}

	/**
	 * 获取属性中文名字跟{@link AttributeType}枚举的映射关系
	 * 
	 * @param attrName
	 * @return
	 */
	public AttributeType getAttributeType(String attrName) {
		return this.mapping.get(attrName);
	}
}