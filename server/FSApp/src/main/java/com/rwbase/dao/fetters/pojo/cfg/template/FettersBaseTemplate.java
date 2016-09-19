package com.rwbase.dao.fetters.pojo.cfg.template;

import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import com.common.HPCUtil;
import com.rwbase.dao.fetters.pojo.cfg.FettersBaseCfg;

/*
 * @author HC
 * @date 2016年4月27日 上午11:30:24
 * @Description 
 */
public class FettersBaseTemplate {
	private final int fettersId;// 羁绊Id
	private final int heroModelId;// 英雄的ModelId
	private final List<Integer> fettersConditionList;// 羁绊的条件列表
	private final List<Integer> fettersHeroIdList;// 羁绊的英雄Id列表
	private final int seq; // 羁绊在面板的位置，相当于一个部位概念，用于战斗力运算

	public FettersBaseTemplate(FettersBaseCfg cfg) {
		this.fettersId = cfg.getFettersId();
		this.heroModelId = cfg.getHeroModelId();
		this.seq = cfg.getSeq();

		// ===================================条件
		String fettersConditions = cfg.getFettersConditions();
		if (StringUtils.isEmpty(fettersConditions)) {// 如果是空的
			this.fettersConditionList = Collections.emptyList();
		} else {
			this.fettersConditionList = Collections.unmodifiableList(HPCUtil.parseIntegerList(fettersConditions, ","));
		}

		// ===================================羁绊英雄Id列表
		String fettersHeroId = cfg.getFettersHeroId();
		if (StringUtils.isEmpty(fettersHeroId)) {// 如果是空的
			this.fettersHeroIdList = Collections.emptyList();
		} else {
			this.fettersHeroIdList = Collections.unmodifiableList(HPCUtil.parseIntegerList(fettersHeroId, ","));
		}
	}

	/**
	 * 获取羁绊Id
	 * 
	 * @return
	 */
	public int getFettersId() {
		return fettersId;
	}

	/**
	 * 获取英雄的ModelId
	 * 
	 * @return
	 */
	public int getHeroModelId() {
		return heroModelId;
	}

	/**
	 * 获取羁绊中的条件列表
	 * 
	 * @return
	 */
	public List<Integer> getFettersConditionList() {
		return fettersConditionList;
	}

	/**
	 * 获取羁绊中的英雄Id列表
	 * 
	 * @return
	 */
	public List<Integer> getFettersHeroIdList() {
		return fettersHeroIdList;
	}
	
	/**
	 * 
	 * 羁绊在面板的位置，相当于一个部位概念，用于战斗力运算
	 * 
	 * @return
	 */
	public int getSeq() {
		return seq;
	}
}