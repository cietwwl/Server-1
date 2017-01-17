package com.rw.service.gamble.datamodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import com.common.RefInt;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.gamble.GambleLogicHelper;
import com.rwbase.common.config.CfgCsvHelper;

/*
 <bean class="com.rw.service.gamble.datamodel.HotGambleCfgHelper"  init-method="init" />
 */

public class HotGambleCfgHelper extends CfgCsvDao<HotGambleCfg> {
	public static HotGambleCfgHelper getInstance() {
		return SpringContextUtil.getBean(HotGambleCfgHelper.class);
	}

	private ArrayList<HotGambleCfg> sortCfg;
	private String defaultHeroId = null;

	@Override
	public Map<String, HotGambleCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("gamble/HotGambleCfg.csv", HotGambleCfg.class);
		Collection<HotGambleCfg> vals = cfgCacheMap.values();
		sortCfg = new ArrayList<HotGambleCfg>(cfgCacheMap.size());
		for (HotGambleCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
			sortCfg.add(cfg);
		}
		if (cfgCacheMap.size() <= 0)
			throw new RuntimeException("钓鱼台高级抽卡热点英雄配置(HotGambleCfg.csv)至少需要一行!");

		Collections.sort(sortCfg, SorterByKey);

		// 按照日期重新索引
		cfgCacheMap.clear();
		for (HotGambleCfg cfg : sortCfg) {
			cfgCacheMap.put(getMonthDay(cfg.getMonth(), cfg.getDay()), cfg);
		}
		return cfgCacheMap;
	}

	@Override
	public void CheckConfig() {
		// 跨表检查英雄是否存在，然后寻找合适的默认保底容错英雄
		defaultHeroId = null;
		for (HotGambleCfg cfg : sortCfg) {
			if (!GambleLogicHelper.isValidHeroId(cfg.getHeroModelId())) {
				throw new RuntimeException("无效英雄ID:" + cfg.getHeroModelId());
			}
			if (defaultHeroId == null) {
				defaultHeroId = cfg.getHeroModelId();
			}
		}
	}

	public String getDefaultHeroId() {
		return defaultHeroId;
	}

	private String getMonthDay(int month, int dayOfMonth) {
		return String.format("%02d_%02d", month, dayOfMonth);
	}

	private static Comparator<HotGambleCfg> SorterByKey = new Comparator<HotGambleCfg>() {
		@Override
		public int compare(HotGambleCfg o1, HotGambleCfg o2) {
			return o1.getKey() - o2.getKey();
		}
	};

	public String getTodayGuanrateeHotHero(RefInt slotCount) {
		HotGambleCfg cfg = calculateCache();
		if (slotCount != null) {
			slotCount.value = 1;// 目前定死是一个，但由配置类决定，方便以后扩展
		}
		return cfg.getHeroModelId();
	}

	private static DateFormat dataFormat = new SimpleDateFormat("MM_dd");
	// 缓存今天的配置，需要通过日期判断缓存是否失效
	private HotGambleCfg cacheCfg;

	private HotGambleCfg calculateCache() {
		if (cacheCfg == null) {
			cacheCfg = getTodayHotCfg();
			return cacheCfg;
		}

		Calendar cal = Calendar.getInstance();
		
		String date = dataFormat.format(cal.getTime());
		GambleHotHeroPlanDAO DAO = GambleHotHeroPlanDAO.getInstance();
		GambleHotHeroPlan result = DAO.get(date);
		if (result != null && result.getDateAsId() != null) {
			// System.out.println("gamble hot hero list already set for today:"+date);
			return cacheCfg;
		}
		
		int nowMonth = cal.get(Calendar.MONTH) + 1;// JDK的月份从0开始计数
		int nowDay = cal.get(Calendar.DAY_OF_MONTH);// 从下一日开始搜索
		//if (cacheCfg.getMonth() < nowMonth || cacheCfg.getMonth() == nowMonth && cacheCfg.getDay() < nowDay) {----不可以这样判断，否则跨年就无法刷新    by Alex 12.14.2016
		if (cacheCfg.getMonth() != nowMonth || (cacheCfg.getMonth() == nowMonth && cacheCfg.getDay() < nowDay)) {
			// 正常情况下，隔了一天以上没有刷新缓存的时候，当前日期应该在缓存日期之后，这就需要重新计算
			// 但如果出现某一次计算缓存的时候对应日期没有配置，当时会搜索下一个可用日期的配置作为替代
			// 那就可能产生当前日期在缓存日期之前的情况，这时候不需要再进行计算
			cacheCfg = getTodayHotCfg();
		}

		return cacheCfg;
	}

	private HotGambleCfg getTodayHotCfg() {
		Calendar cal = Calendar.getInstance();
		String todayStr = dataFormat.format(cal.getTime());
		HotGambleCfg cfg = cfgCacheMap.get(todayStr);

		if (cfg == null) {
			// 需要特殊容错手段：当前日期如果没有配置则按日期搜索下一条
			int month = cal.get(Calendar.MONTH) + 1;// JDK的月份从0开始计数
			int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH) + 1;// 从下一日开始搜索
			for (int i = month; i <= 12; i++) {
				for (int j = dayOfMonth; j <= 31; j++) {
					todayStr = getMonthDay(i, j);
					cfg = cfgCacheMap.get(todayStr);
					if (cfg != null) {
						break;
					}
				}

				if (cfg != null) {
					break;
				}
				dayOfMonth = 1;
			}
		}

		// 如果搜索到年末还没有找到配置，则使用第一条
		if (cfg == null) {
			cfg = sortCfg.get(0);
		}
		// 保存缓存
		cacheCfg = cfg;
		return cfg;
	}

	public int getTodayHotPlanId() {
		HotGambleCfg cfg = calculateCache();
		return cfg.getGroupId();
	}
}