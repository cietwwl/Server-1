//package com.rwbase.dao.fetters.pojo.cfg.template;
//
//import java.util.Collections;
//import java.util.List;
//
//import org.springframework.util.StringUtils;
//
//import com.common.HPCUtil;
//import com.rwbase.dao.fetters.pojo.cfg.HeroFettersCfg;
//
///*
// * @author HC
// * @date 2016年4月27日 上午11:22:45
// * @Description 
// */
//public class HeroFettersTemplate {
//	private final int heroModelId;// 英雄模版Id
//	private final List<Integer> fettersIdList;// 一个不可修改的羁绊列表
//
//	public HeroFettersTemplate(HeroFettersCfg cfg) {
//		this.heroModelId = cfg.getHeroModelId();
//
//		String fettersId = cfg.getFettersId();
//		if (StringUtils.isEmpty(fettersId)) {// 如果是空的
//			this.fettersIdList = Collections.emptyList();
//		} else {
//			this.fettersIdList = Collections.unmodifiableList(HPCUtil.parseIntegerList(fettersId, ","));
//		}
//	}
//
//	/**
//	 * 获取英雄的模版Id
//	 * 
//	 * @return
//	 */
//	public int getHeroModelId() {
//		return heroModelId;
//	}
//
//	/**
//	 * <pre>
//	 * 获取一个不可修改的对应羁绊List
//	 * <b>Unmodifiable</b>
//	 * </pre>
//	 * 
//	 * @return
//	 */
//	public List<Integer> getFettersIdList() {
//		return fettersIdList;
//	}
// }