package com.rwbase.common.attrdata.calc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.BeanOperationHelper;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.item.GemCfgDAO;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.role.InlayCfgDAO;
import com.rwbase.dao.role.pojo.InlayTemplate;

/*
 * @author HC
 * @date 2016年4月15日 下午11:50:57
 * @Description 
 */
public class GemAttrDataCalc implements IAttrDataCalc {

	private final String heroModelId;// 英雄的模版Id
	private final List<String> gemList;// 宝石数据

	public GemAttrDataCalc(List<String> gemList, String heroModelId) {
		this.gemList = gemList;
		this.heroModelId = heroModelId;
	}

	@Override
	public AttrData getAttrData() {
		if (gemList == null || gemList.isEmpty()) {
			return null;
		}

		AttrData attrData = new AttrData();
		// 宝石的分类<等级，数量>
		Map<Integer, Integer> levelNumMap = new HashMap<Integer, Integer>();

		// 基础的属性计算
		GemCfgDAO cfgDAO = GemCfgDAO.getInstance();
		for (int i = 0, size = gemList.size(); i < size; i++) {
			String modelId = gemList.get(i);
			GemCfg cfg = cfgDAO.getCfgById(modelId);
			if (cfg == null) {
				continue;
			}

			attrData.plus(AttrData.fromObject(cfg));
			int level = cfg.getLevel();// 品质

			Integer hasValue = levelNumMap.get(level);
			if (hasValue == null) {
				levelNumMap.put(level, 1);
			} else {
				levelNumMap.put(level, hasValue + 1);
			}
		}

		// 如果没有
		if (levelNumMap.isEmpty()) {
			return attrData;
		}

		// 佩戴某些品阶的宝石可以增加属性
		InlayTemplate heroInlayTmp = InlayCfgDAO.getInstance().getInlayTemplate(heroModelId);
		if (heroInlayTmp == null) {
			return attrData;
		}

		attrData.plus(getExtraAttrData(levelNumMap, heroInlayTmp.getExtraNum1(), heroInlayTmp.getExtraValue1()));
		attrData.plus(getExtraAttrData(levelNumMap, heroInlayTmp.getExtraNum2(), heroInlayTmp.getExtraValue2()));
		attrData.plus(getExtraAttrData(levelNumMap, heroInlayTmp.getExtraNum3(), heroInlayTmp.getExtraValue3()));

		return attrData;
	}

	/**
	 * 解析根据宝石等级和数量算出来宝石属性
	 * 
	 * @param levelNumMap
	 * @param extraNum
	 * @param attrMap
	 * @return
	 */
	private AttrData getExtraAttrData(Map<Integer, Integer> levelNumMap, int extraNum, Map<Integer, Map<String, String>> attrMap) {
		AttrData attrData = new AttrData();
		if (levelNumMap == null || levelNumMap.isEmpty()) {
			return attrData;
		}

		if (attrMap == null || attrMap.isEmpty()) {
			return attrData;
		}

		int maxLevel = 0;
		// 检索等级
		for (Entry<Integer, Integer> e : levelNumMap.entrySet()) {
			int level = e.getKey();
			int num = e.getValue();

			if (num >= extraNum && level > maxLevel) {
				maxLevel = level;
			}
		}

		if (maxLevel <= 0) {
			return attrData;
		}

		Map<String, String> targetMap = null;
		int fitMaxLevel = 0;
		// 检索内容
		for (Entry<Integer, Map<String, String>> e : attrMap.entrySet()) {
			int level = e.getKey();
			if (level >= maxLevel && level > fitMaxLevel) {
				fitMaxLevel = level;
				targetMap = e.getValue();
			}
		}

		if (targetMap == null || targetMap.isEmpty()) {
			return attrData;
		}

		BeanOperationHelper.plus(attrData, targetMap);
		return attrData;
	}

	@Override
	public AttrData getPrecentAttrData() {
		if (gemList == null || gemList.isEmpty()) {
			return null;
		}

		AttrData attrData = new AttrData();
		// 基础的属性计算
		GemCfgDAO cfgDAO = GemCfgDAO.getInstance();
		for (int i = 0, size = gemList.size(); i < size; i++) {
			String modelId = gemList.get(i);
			GemCfg cfg = cfgDAO.getCfgById(modelId);
			if (cfg == null) {
				continue;
			}

			attrData.plus(AttrData.fromPercentObject(cfg));
		}

		return attrData;
	}
}