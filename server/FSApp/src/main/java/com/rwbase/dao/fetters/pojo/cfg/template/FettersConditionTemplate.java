package com.rwbase.dao.fetters.pojo.cfg.template;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.util.StringUtils;

import com.common.HPCUtil;
import com.rwbase.dao.fetters.pojo.cfg.FettersConditionCfg;

/*
 * @author HC
 * @date 2016年4月27日 下午12:14:42
 * @Description 羁绊条件
 */
public class FettersConditionTemplate {
	private final int uniqueId;// 唯一模版Id
	private final int conditionId;// 条件Id
	private final int conditionLevel;// 条件的等级
	private final List<Integer> subConditionIdList;// 羁绊的子条件Id
	private final Map<Integer, Float> fettersAttrDataMap;// 羁绊增加属性
	private final Map<Integer, Float> fettersPrecentAttrDataMap;// 羁绊增加的百分比属性

	public FettersConditionTemplate(FettersConditionCfg cfg) {
		this.uniqueId = cfg.getUniqueId();// 唯一Id
		this.conditionId = cfg.getConditionId();// 条件Id
		this.conditionLevel = cfg.getConditionLevel();// 条件等级

		// ===============================子条件Id列表
		String subConditionId = cfg.getSubConditionId();
		if (StringUtils.isEmpty(subConditionId)) {
			this.subConditionIdList = Collections.emptyList();
		} else {
			this.subConditionIdList = Collections.unmodifiableList(HPCUtil.parseIntegerList(subConditionId, ","));
		}

		// ===============================增加的固定属性
		String fettersAttrData = cfg.getFettersAttrData();
		if (StringUtils.isEmpty(fettersAttrData)) {
			this.fettersAttrDataMap = Collections.emptyMap();
		} else {
			HashMap<Integer, Float> map = new HashMap<Integer, Float>();
			StringTokenizer token = new StringTokenizer(fettersAttrData, ";");
			while (token.hasMoreTokens()) {
				StringTokenizer token1 = new StringTokenizer(token.nextToken(), "_");
				if (token1.countTokens() != 2) {
					continue;
				}

				map.put(Integer.valueOf(token.nextToken()), Float.valueOf(token.nextToken()));
			}

			this.fettersAttrDataMap = Collections.unmodifiableMap(map);
		}
		// ===============================增加的百分比属性
		String fettersPrecentAttrData = cfg.getFettersPrecentAttrData();
		if (StringUtils.isEmpty(fettersPrecentAttrData)) {
			this.fettersPrecentAttrDataMap = Collections.emptyMap();
		} else {
			HashMap<Integer, Float> map = new HashMap<Integer, Float>();
			StringTokenizer token = new StringTokenizer(fettersPrecentAttrData, ";");
			while (token.hasMoreTokens()) {
				StringTokenizer token1 = new StringTokenizer(token.nextToken(), "_");
				if (token1.countTokens() != 2) {
					continue;
				}

				map.put(Integer.valueOf(token.nextToken()), Float.valueOf(token.nextToken()));
			}

			this.fettersPrecentAttrDataMap = Collections.unmodifiableMap(map);
		}
	}

	/**
	 * 获取唯一Id
	 * 
	 * @return
	 */
	public int getUniqueId() {
		return uniqueId;
	}

	/**
	 * 获取条件的Id
	 * 
	 * @return
	 */
	public int getConditionId() {
		return conditionId;
	}

	/**
	 * 条件等级
	 * 
	 * @return
	 */
	public int getConditionLevel() {
		return conditionLevel;
	}

	/**
	 * 获取条件的子条件列表
	 * 
	 * @return
	 */
	public List<Integer> getSubConditionIdList() {
		return subConditionIdList;
	}

	/**
	 * 获取条件增加的固定属性
	 * 
	 * @return
	 */
	public Map<Integer, Float> getFettersAttrDataMap() {
		return fettersAttrDataMap;
	}

	/**
	 * 获取条件增加的百分比属性
	 * 
	 * @return
	 */
	public Map<Integer, Float> getFettersPrecentAttrDataMap() {
		return fettersPrecentAttrDataMap;
	}
}