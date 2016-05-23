package com.rw.service.TaoistMagic.datamodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.common.RandomSeqGenerator;
import com.common.RefInt;
import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.attribute.AttributeLanguageCfgDAO;

//	<bean class="com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper"  init-method="init" />

public class TaoistMagicCfgHelper extends CfgCsvDao<TaoistMagicCfg> {
	public static TaoistMagicCfgHelper getInstance() {
		return SpringContextUtil.getBean(TaoistMagicCfgHelper.class);
	}

	/*
	 * public AttrDataIF getEffect(int skillId, int level) { TaoistMagicCfg cfg = cfgCacheMap.get(String.valueOf(skillId)); AttrData attr = new
	 * AttrData(); Field field = attrMap.get(cfg.getAttribute()); try { field.set(attr, cfg.getMagicValue(level)); } catch (IllegalArgumentException
	 * e) { e.printStackTrace(); } catch (IllegalAccessException e) { e.printStackTrace(); } return attr; }
	 */
	// private Map<String, Field> attrMap;

	@Override
	public Map<String, TaoistMagicCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("TaoistMagic/TaoistMagicCfg.csv", TaoistMagicCfg.class);
		// attrMap = CfgCsvHelper.getFieldMap(AttrData.class);
		Collection<TaoistMagicCfg> vals = cfgCacheMap.values();
		HashMap<Integer, Integer> tagMap = new HashMap<Integer, Integer>();
		HashMap<Integer, HashSet<Integer>> orderMap = new HashMap<Integer, HashSet<Integer>>();

		for (TaoistMagicCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
			// 检查属性是否存在, 每个分页的开放等级必须一样,序号应该连续且没有重复
			// String attribute = cfg.getAttribute();
			// if (!attrMap.containsKey(attribute)) {
			// throw new RuntimeException("无效属性名:" + attribute);
			// }
			int tagNum = cfg.getTagNum();
			int openLevel = cfg.getOpenLevel();
			Integer oldTagCfg = tagMap.get(tagNum);
			if (oldTagCfg == null) {
				tagMap.put(tagNum, openLevel);
			} else {
				if (oldTagCfg != openLevel) {
					throw new RuntimeException("同一个分页的道术技能应该是同一个开放等级！" + "key=" + cfg.getKey());
				}
			}

			HashSet<Integer> orderSet = orderMap.get(tagNum);
			if (orderSet == null) {
				orderSet = new HashSet<Integer>();
				orderMap.put(tagNum, orderSet);
			}
			if (!orderSet.add(cfg.getOrder())) {
				throw new RuntimeException("重复的道术排列序号" + "key=" + cfg.getKey());
			}
		}

		Set<Entry<Integer, HashSet<Integer>>> orderEntrySet = orderMap.entrySet();
		for (Entry<Integer, HashSet<Integer>> entry : orderEntrySet) {
			HashSet<Integer> orderSet = entry.getValue();
			int size = orderSet.size();
			for (int i = 1; i <= size; i++) {
				if (!orderSet.contains(i)) {
					throw new RuntimeException("分页:" + entry.getKey() + ",缺少道术排列序号" + i);
				}
			}
		}
		return cfgCacheMap;
	}

	@Override
	public void CheckConfig() {
		// 跨表检查，consumeId是否在TaoistConsumeCfg有定义
		TaoistConsumeCfgHelper helper = TaoistConsumeCfgHelper.getInstance();
		AttributeLanguageCfgDAO cfgDAO = AttributeLanguageCfgDAO.getCfgDAO();

		Collection<TaoistMagicCfg> vals = cfgCacheMap.values();

		for (TaoistMagicCfg cfg : vals) {
			int consumeId = cfg.getConsumeId();
			TaoistConsumeCfg consumeCfg = helper.getCfgById(String.valueOf(consumeId));
			if (consumeCfg == null) {
				throw new RuntimeException("无效技能消耗ID=" + consumeId);
			}
			int maxLvl = helper.getMaxLevel(consumeId);

			Map<String, TaoistMagicFormula> attrDataMap = cfg.getAttrDataMap();
			for (Entry<String, TaoistMagicFormula> e : attrDataMap.entrySet()) {
				String attrType = e.getKey();

				TaoistMagicFormula formula = e.getValue();
				if (formula == null) {
					GameLog.error("道术", attrType, "没有找到对应的公式");
					continue;
				}

				AttributeType attributeType = cfgDAO.getAttributeType(attrType);
				if (attributeType == null) {
					GameLog.error("道术", attrType, "无效属性名");
					continue;
				}

				formula.cacheToLevel(maxLvl);
			}
		}
	}

	/**
	 * 生成暴击方案，每次点击对应的升级数量放在返回的数组，总数存放在outTotal 产生的总数不会超过maxUpgradeCount
	 * 
	 * @param seed
	 * @param seedRange
	 * @param magicId 道术技能ID
	 * @param upgradeCount 升级次数
	 * @param maxUpgradeCount 最大升级总数
	 * @param outTotal 总数升级数，包含暴击
	 * @return 暴击方案
	 */
	public int[] generateCriticalPlan(int seed, int seedRange, int magicId, int currentLevel, int upgradeCount, int maxUpgradeCount, RefInt outTotal) {
		if (upgradeCount > maxUpgradeCount) {
			return null;
		}
		if (seed < 0 || seedRange <= 0) {
			return null;
		}

		TaoistMagicCfg mcfg = cfgCacheMap.get(String.valueOf(magicId));
		if (mcfg == null) {
			return null;
		}

		int level = currentLevel;
		TaoistConsumeCfgHelper helper = TaoistConsumeCfgHelper.getInstance();
		int[] seqPlanIdList = helper.getCriticalPlanIdList(mcfg.getConsumeId(), level);
		if (seqPlanIdList == null) {
			GameLog.error("道术", "找不到道术技能消耗配置", "consumeId=" + mcfg.getConsumeId() + ",level=" + level);
			return null;
		}

		RandomSeqGenerator seqg = new RandomSeqGenerator(seed, seqPlanIdList, TaoistCriticalPlanCfgHelper.getInstance(), seedRange);
		int count = 0;
		int[] result = new int[upgradeCount];
		outTotal.value = 0;
		while (count < upgradeCount) {
			int num = seqg.nextNum();
			if (num > 0) {
				if (outTotal.value + num > maxUpgradeCount) {
					int maxAdd = maxUpgradeCount - outTotal.value;
					result[count] = maxAdd;
					outTotal.value += maxAdd;
					break;
				}
				result[count] = num;
				outTotal.value += num;
				level += num;
			} else {
				if (outTotal.value + 1 > maxUpgradeCount) {
					break;
				}
				result[count] = 1;
				outTotal.value++;
				level++;
			}

			seqPlanIdList = helper.getCriticalPlanIdList(mcfg.getConsumeId(), level);
			if (seqPlanIdList == null) {
				GameLog.error("道术", "找不到道术技能消耗配置", "consumeId=" + mcfg.getConsumeId() + ",level=" + level);
				return null;
			}
			seqg.ChangeSeqPlanIdList(seqPlanIdList);
			count++;
		}
		return result;
	}

	// public IEffectCfg getEffect(Iterable<Entry<Integer, Integer>> lst) {
	// AttrData addedPercentages = new AttrData();
	// AttrData addedValues = new AttrData();
	// for (Entry<Integer, Integer> entry : lst) {
	// String taoistMagicId = String.valueOf(entry.getKey());
	// TaoistMagicCfg cfg = cfgCacheMap.get(taoistMagicId);
	// if (cfg == null) {
	// GameLog.error("道术", taoistMagicId, "无效道术技能ID");
	// continue;
	// }
	// Field field = attrMap.get(cfg.getAttribute());
	// if (field == null) {
	// GameLog.error("道术", cfg.getAttribute(), "无效属性名");
	// continue;
	// }
	// AttrData attr = new AttrData();
	// try {
	// field.set(attr, cfg.getMagicValue(entry.getValue()));
	// } catch (Exception e) {
	// GameLog.error("道术", cfg.getAttribute(), "无法设置属性值", e);
	// }
	// if (cfg.getAttrValueType() == AttrValueType.Value) {
	// addedValues.plus(attr);
	// } else {
	// addedPercentages.plus(attr);
	// }
	// }
	// IEffectCfg result = new BattleAddedEffects(addedValues, addedPercentages);
	// return result;
	// }

	/**
	 * 获取道术的属性
	 * 
	 * @param lst
	 * @return
	 */
	public Map<Integer, AttributeItem> getEffectAttr(Iterable<Entry<Integer, Integer>> lst) {
		Map<Integer, AttributeItem> attrMap = new HashMap<Integer, AttributeItem>();

		AttributeLanguageCfgDAO cfgDAO = AttributeLanguageCfgDAO.getCfgDAO();

		for (Entry<Integer, Integer> entry : lst) {
			String taoistMagicId = String.valueOf(entry.getKey());
			TaoistMagicCfg cfg = cfgCacheMap.get(taoistMagicId);
			if (cfg == null) {
				GameLog.error("道术", taoistMagicId, "无效道术技能ID");
				continue;
			}

			Map<String, TaoistMagicFormula> attrDataMap = cfg.getAttrDataMap();
			for (Entry<String, TaoistMagicFormula> e : attrDataMap.entrySet()) {
				String attrType = e.getKey();

				TaoistMagicFormula formula = e.getValue();
				if (formula == null) {
					GameLog.error("道术", attrType, "没有找到对应的公式");
					continue;
				}

				AttributeType attributeType = cfgDAO.getAttributeType(attrType);
				if (attributeType == null) {
					GameLog.error("道术", attrType, "无效属性名");
					continue;
				}

				AttributeItem attributeItem = attrMap.get(attributeType.getTypeValue());
				int attrDataValue = 0;
				int precentAttrDataValue = 0;
				if (attributeItem != null) {
					attrDataValue = attributeItem.getIncreaseValue();
					precentAttrDataValue = attributeItem.getIncPerTenthousand();
				}

				attributeItem = new AttributeItem(attributeType, attrDataValue + formula.getValue(entry.getValue()), precentAttrDataValue);
				attrMap.put(attributeType.getTypeValue(), attributeItem);
			}

			// Field field = attrMap.get(attrType);
			// if (field == null) {
			// GameLog.error("道术", attrType, "无效属性名");
			// continue;
			// }
			// AttrData attr = new AttrData();
			// try {
			// field.set(attr, cfg.getMagicValue(entry.getValue()));
			// } catch (Exception e) {
			// GameLog.error("道术", attrType, "无法设置属性值", e);
			// }
		}

		return attrMap;
	}
}
