package com.rwbase.dao.item.pojo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.common.HPCUtil;

/*
 * @author HC
 * @date 2016年5月18日 下午2:27:19
 * @Description 
 */
public class ItemUseEffectTemplate {
	private final int modelId;// 物品的ModelId
	private final Map<Integer, Integer> combineUseMap;
	private final String useEffectClass;// 对应的处理类路径
	private final int directResult;// 直接使用的数值
	private final List<Integer> dropList;// 掉落物品的方案Id列表

	public ItemUseEffectTemplate(ItemUseEffectCfg cfg) {
		this.modelId = cfg.getModelId();
		this.directResult = cfg.getDirectResult();
		this.useEffectClass = cfg.getUseEffectClass();

		String combineUse = cfg.getCombineUse();
		if (StringUtils.isEmpty(combineUse)) {
			this.combineUseMap = Collections.emptyMap();
		} else {
			this.combineUseMap = Collections.unmodifiableMap(HPCUtil.parseIntegerMap(combineUse, ";", "_"));
		}

		String dropResult = cfg.getDropResult();
		if (StringUtils.isEmpty(dropResult)) {
			this.dropList = Collections.emptyList();
		} else {
			this.dropList = Collections.unmodifiableList(HPCUtil.parseIntegerList(dropResult, ";"));
		}
	}

	public int getModelId() {
		return modelId;
	}

	public Map<Integer, Integer> getCombineUseMap() {
		return combineUseMap;
	}

	public String getUseEffectClass() {
		return useEffectClass;
	}

	public int getDirectResult() {
		return directResult;
	}

	public List<Integer> getDropList() {
		return dropList;
	}
}