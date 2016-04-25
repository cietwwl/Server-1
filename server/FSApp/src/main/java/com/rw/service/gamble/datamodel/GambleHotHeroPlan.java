package com.rw.service.gamble.datamodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.common.RefInt;
import com.log.GameLog;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rw.service.gamble.GambleLogic;
import com.rw.service.gamble.GambleLogicHelper;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gamble_hotHeroPlan")
public class GambleHotHeroPlan {
	@NonSave
	private static DateFormat dataFormat = new SimpleDateFormat("yyyy_MM_dd");
	@Id
	private String dateAsId;// 用日期作为数据存储的ID
	private GambleDropGroup hotPlan;
	private String errDefaultModelId;

	private GambleHotHeroPlan() {
	}

	private void Init(Random r, int hotPlanId, int hotCount, String errDefaultModelId) {
		this.errDefaultModelId = errDefaultModelId;

		GambleDropCfgHelper gambleDropConfig = GambleDropCfgHelper.getInstance();
		RefInt weight = new RefInt();
		RefInt slotCount = new RefInt();
		List<Pair<String, Integer>> list = new ArrayList<Pair<String, Integer>>(hotCount);
		int[] slots = new int[hotCount];
		for (int i = 0; i < hotCount; i++) {
			String itemModel = gambleDropConfig.getRandomDrop(r, hotPlanId, slotCount, weight);
			if (GambleLogicHelper.isValidHeroId(itemModel)) {
				list.add(Pair.Create(itemModel, weight.value));
				// 忽略配置的数量(slotCount.value)，客户端已经写死一定只能是一个
				slots[i] = 1;
			} else {
				GameLog.error("钓鱼台", "热点数据初始化", "无效英雄ID:" + itemModel);
				list.add(Pair.Create(errDefaultModelId, 1));
				slots[i] = 1;
			}
		}

		hotPlan = new GambleDropGroup(list, slots);
	}

	public String getRandomDrop(Random r, RefInt slotCount) {
		return hotPlan.getRandomGroup(r, slotCount);
	}

	/**
	 * 需要保证额外的容错配置(errDefaultModelId)是有效的！
	 * 
	 * @param r
	 * @param planId
	 * @param hotCount
	 * @param errDefaultModelId
	 * @return
	 */
	public static GambleHotHeroPlan getTodayHotHeroPlan(Random r, int planId, int hotCount, String errDefaultModelId) {
		String date = getDateStr();
		GambleHotHeroPlanDAO DAO = GambleHotHeroPlanDAO.getInstance();
		GambleHotHeroPlan result = DAO.get(date);
		if (result == null || result.dateAsId == null) {
			result = new GambleHotHeroPlan();
			result.dateAsId = date;
			result.Init(r, planId, hotCount, errDefaultModelId);
			if (!DAO.commit(result)) {
				GameLog.error("钓鱼台", "数据库操作,gamble_hotHeroPlan", "更新失败");
			}
		}
		return result;
	}

	public static void resetHotHeroList(Random r) {
		String date = getDateStr();
		GambleHotHeroPlanDAO DAO = GambleHotHeroPlanDAO.getInstance();
		GambleHotHeroPlan result = DAO.get(date);
		if (result == null) {
			result = new GambleHotHeroPlan();
			result.dateAsId = date;
		}

		HotGambleCfgHelper helper = HotGambleCfgHelper.getInstance();
		String defaultHero = helper.getTodayGuanrateeHotHero(null);
		if (!GambleLogicHelper.isValidHeroId(defaultHero) && StringUtils.isNotBlank(result.errDefaultModelId)) {
			defaultHero = result.errDefaultModelId;
		}
		result.Init(r, helper.getTodayHotPlanId(), GambleLogic.HotHeroPoolSize, defaultHero);
		if (!DAO.commit(result)) {
			GameLog.error("钓鱼台", "数据库操作,gamble_hotHeroPlan", "更新失败");
		}
	}

	private static String getDateStr() {
		Calendar cal = Calendar.getInstance();
		String date = dataFormat.format(cal.getTime());
		return date;
	}

	public static Iterable<String> getTodayHotList() {
		String guanrateeHero = HotGambleCfgHelper.getInstance().getTodayGuanrateeHotHero(null);

		GambleHotHeroPlan.InitTodayHotHeroList(GambleLogic.getInstance().getRandom(), guanrateeHero);
		GambleHotHeroPlanDAO DAO = GambleHotHeroPlanDAO.getInstance();
		String date = getDateStr();
		GambleHotHeroPlan generatedPlan = DAO.get(date);
		if (generatedPlan != null && generatedPlan.hotPlan != null) {
			return generatedPlan.hotPlan.getHeroIdListWith(guanrateeHero);
		}
		GameLog.error("钓鱼台", date, "初始化热点数据失败");
		ArrayList<String> result = new ArrayList<String>();
		result.add(guanrateeHero);
		return result;
	}

	public static boolean isTodayInited() {
		String date = getDateStr();
		GambleHotHeroPlanDAO DAO = GambleHotHeroPlanDAO.getInstance();
		GambleHotHeroPlan result = DAO.get(date);
		return result != null && result.hotPlan != null;
	}

	public static void InitTodayHotHeroList(Random ranGen, String defaultItem) {
		if (GambleHotHeroPlan.isTodayInited())
			return;
		HotGambleCfgHelper hotGambleConfig = HotGambleCfgHelper.getInstance();
		RefInt slotCount = new RefInt();
		String heroId = hotGambleConfig.getTodayGuanrateeHotHero(slotCount);
		// 特殊容错处理：如果保底英雄有效就作为容错默认值，否则使用必送丹药作为默认值
		String errDefaultModelId = GambleLogicHelper.isValidHeroId(heroId) ? heroId : defaultItem;

		// 用热点组生成N个英雄
		int hotPlanId = hotGambleConfig.getTodayHotPlanId();
		GambleHotHeroPlan.getTodayHotHeroPlan(ranGen, hotPlanId, GambleLogic.HotHeroPoolSize, errDefaultModelId);
	}

}
