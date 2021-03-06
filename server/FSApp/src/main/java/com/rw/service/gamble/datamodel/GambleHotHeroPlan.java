package com.rw.service.gamble.datamodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.common.RefInt;
import com.log.GameLog;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rw.service.gamble.GambleHandler;
import com.rw.service.gamble.GambleLogicHelper;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gamble_hotheroplan")
public class GambleHotHeroPlan {
	@NonSave
	private static DateFormat dataFormat = new SimpleDateFormat("yyyy_MM_dd");
	@Id
	private String dateAsId;// 用日期作为数据存储的ID
	private GambleDropGroup hotPlan;
	private String errDefaultModelId;

	public String getDateAsId() {
		return dateAsId;
	}

	public GambleDropGroup getHotPlan() {
		return hotPlan;
	}

	public String getErrDefaultModelId() {
		return errDefaultModelId;
	}

	// set方法仅限于Json库调用，其他类不要用
	public void setDateAsId(String dateAsId) {
		this.dateAsId = dateAsId;
	}

	public void setHotPlan(GambleDropGroup hotPlan) {
		this.hotPlan = hotPlan;
	}

	public void setErrDefaultModelId(String errDefaultModelId) {
		this.errDefaultModelId = errDefaultModelId;
	}

	/**
	 * 仅仅用于hson库的序列化/反序列化
	 */
	public GambleHotHeroPlan() {
	}

	/**
	 * 生成热点外其他三个
	 * 
	 * @param r
	 * @param groupID 热点道具组id
	 * @param hotCount
	 * @param errDefaultModelId
	 */
	@JsonIgnore
	private void Init(int groupID, int hotCount, String errDefaultModelId) {
		this.errDefaultModelId = errDefaultModelId;

		List<Pair<String, Integer>> list = null;
		int[] slots = new int[hotCount];
		for (int i = 0; i < slots.length; i++) {
			slots[i] = 1;
		}
		GambleDropCfgHelper gambleDropConfig = GambleDropCfgHelper.getInstance();
		int hotGroupSize = gambleDropConfig.getDropGroupSize(groupID);
		boolean checkDuplicated = hotGroupSize > hotCount;
		if (checkDuplicated) {
			String guanrateeHero = HotGambleCfgHelper.getInstance().getTodayGuanrateeHotHero(null);
			// 生成热点不重复
			list = gambleDropConfig.getHotRandomDrop(null, groupID, hotCount, guanrateeHero);
		} else {
			RefInt weight = new RefInt();
			RefInt slotCount = new RefInt();
			list = new ArrayList<Pair<String, Integer>>(hotCount);

			for (int i = 0; i < hotCount; i++) {
				String itemModel = gambleDropConfig.getRandomDrop(null, groupID, slotCount, weight);
				if (GambleLogicHelper.isValidHeroOrItemId(itemModel)) {
					list.add(Pair.Create(itemModel, weight.value));
					// 忽略配置的数量(slotCount.value)，客户端已经写死一定只能是一个
				} else {
					GameLog.error("钓鱼台", "热点数据初始化", "无效英雄ID:" + itemModel);
					list.add(Pair.Create(errDefaultModelId, 1));
				}
			}
		}

		hotPlan = GambleDropGroup.Create(list, slots);
	}

	// @JsonIgnore
	// public String getRandomDrop(Random r, RefInt slotCount) {
	// return hotPlan.getRandomGroup(r, slotCount);
	// }

	/**
	 * 需要保证额外的容错配置(errDefaultModelId)是有效的！
	 * 
	 * @param r
	 * @param groupID 热点道具组id
	 * @param hotCount
	 * @param errDefaultModelId
	 * @return
	 */
	@JsonIgnore
	public static GambleHotHeroPlan getTodayHotHeroPlan(int groupID, int hotCount, String errDefaultModelId) {
		String date = getDateStr();
		GambleHotHeroPlanDAO DAO = GambleHotHeroPlanDAO.getInstance();
		GambleHotHeroPlan result = DAO.get(date);
		if (result == null || result.dateAsId == null) {
			GameLog.info("钓鱼台", "", "每日热点未初始化");
			// TODO 改为用一个静态的GambleHotHeroPlan做容错，避免并发修改每日热点数据
			result = new GambleHotHeroPlan();
			result.dateAsId = date;
			result.Init(groupID, hotCount, errDefaultModelId);
			if (!DAO.commit(result)) {
				GameLog.error("钓鱼台", "数据库操作,gamble_hotHeroPlan", "更新失败");
			}
		}
		return result;
	}

	@JsonIgnore
	public static void resetHotHeroList() {
		String date = getDateStr();
		GambleHotHeroPlanDAO DAO = GambleHotHeroPlanDAO.getInstance();
		GambleHotHeroPlan result = DAO.get(date);
		if (result != null && result.getDateAsId() != null) {
			// System.out.println("gamble hot hero list already set for today:"+date);
			return;
		}

		if (result == null || result.getDateAsId() == null) {
			result = new GambleHotHeroPlan();
			result.dateAsId = date;
		}

		HotGambleCfgHelper helper = HotGambleCfgHelper.getInstance();
		String defaultHero = helper.getTodayGuanrateeHotHero(null);
		if (!GambleLogicHelper.isValidHeroOrItemId(defaultHero) && StringUtils.isNotBlank(result.errDefaultModelId)) {
			defaultHero = result.errDefaultModelId;
		}
		result.Init(helper.getTodayHotPlanId(), GambleHandler.HotHeroPoolSize, defaultHero);
		if (!DAO.commit(result)) {
			GameLog.error("钓鱼台", "数据库操作,gamble_hotHeroPlan", "更新失败");
		}
	}

	@JsonIgnore
	private static String getDateStr() {
		Calendar cal = Calendar.getInstance();
		String date = dataFormat.format(cal.getTime());
		return date;
	}

	@JsonIgnore
	public static Iterable<String> getTodayHotList() {
		String guanrateeHero = HotGambleCfgHelper.getInstance().getTodayGuanrateeHotHero(null);

		GambleHotHeroPlan.InitTodayHotHeroList(guanrateeHero);
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

	@JsonIgnore
	public static boolean isTodayInited() {
		String date = getDateStr();
		GambleHotHeroPlanDAO DAO = GambleHotHeroPlanDAO.getInstance();
		GambleHotHeroPlan result = DAO.get(date);
		return result != null && result.hotPlan != null;
	}

	@JsonIgnore
	public static void InitTodayHotHeroList(String defaultItem) {
		if (GambleHotHeroPlan.isTodayInited())
			return;
		HotGambleCfgHelper hotGambleConfig = HotGambleCfgHelper.getInstance();
		RefInt slotCount = new RefInt();
		String heroId = hotGambleConfig.getTodayGuanrateeHotHero(slotCount);
		// 特殊容错处理：如果保底英雄有效就作为容错默认值，否则使用必送丹药作为默认值
		String errDefaultModelId = GambleLogicHelper.isValidHeroOrItemId(heroId) ? heroId : defaultItem;

		// 用热点组生成N个英雄
		int groupID = hotGambleConfig.getTodayHotPlanId();
		GambleHotHeroPlan.getTodayHotHeroPlan(groupID, GambleHandler.HotHeroPoolSize, errDefaultModelId);
	}
}