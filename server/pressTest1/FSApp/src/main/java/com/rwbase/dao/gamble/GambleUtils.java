package com.rwbase.dao.gamble;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.log.GameLog;
import com.rwbase.dao.gamble.pojo.EGambleWeight;
import com.rwbase.dao.gamble.pojo.TableGamble;
import com.rwbase.dao.gamble.pojo.cfg.GambleRewardCfg;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwproto.GambleServiceProtos.GambleData;
import com.rwproto.GambleServiceProtos.GambleRewardData;

public class GambleUtils {

	/** 直接获取奖励数据 */
	public static GambleRewardData getRandomRewardData(List<GambleRewardCfg> list) {
		if (list == null || list.size() == 0) {
			return null;
		}
		GambleRewardCfg cfg = getRandomRewardCfg(list);
		if (cfg == null) {
			return null;
		}
		GambleRewardData.Builder rewardData = GambleRewardData.newBuilder();
		// TODO HC @Modify HC 2015-12-19
		/**
		 * <pre>
		 * 如果是(modelId_等级)的组合英雄，要先检查下模版存不存在，如果模版不存在就直接返回个Null
		 *  对于玩家来说，我保底的规则只能我服务器看到，玩家是不会看到的，玩家看到的就是我物品是不是有。
		 *  宁可把保底规则舍弃都不能让玩家因为异常获取不到物品
		 * </pre>
		 */
		String itemID = cfg.getItemID();
		if (itemID.indexOf("_") != -1) {
			String[] arr = itemID.split("_");
			if (arr == null) {
				return null;
			}

			// String modelId = arr[0];
			RoleCfg roleCfg = RoleCfgDAO.getInstance().getConfig(itemID);
			if (roleCfg == null) {
				GameLog.error("钓鱼模块", "玩家", "钓鱼随机到了模版Id为：" + itemID + "的英雄，配置不存在", null);
				// System.err.println("钓鱼随机到了模版Id为：" + itemID + "的英雄，配置不存在");
				return null;
			}
		}

		rewardData.setItemId(itemID);
		rewardData.setItemNum(new Random().nextInt(cfg.getMax()) + 1);
		return rewardData.build();
	}

	/** 获取一个随机奖励配置 */
	public static GambleRewardCfg getRandomRewardCfg(List<GambleRewardCfg> list) {
		int weightTotal = 0;
		for (GambleRewardCfg cfg : list) {
			weightTotal += cfg.getWeight();
		}
		int random = new Random().nextInt(weightTotal);
		int temp = 0;
		for (GambleRewardCfg cfg : list) {
			temp += cfg.getWeight();
			if (random < temp) {
				return cfg;
			}
		}
		return null;
	}

	/** 获取一个随机权重组 */
	public static EGambleWeight getRandomWeightGroup(String[] list) {
		int weightTotal = 0;
		for (String temp : list) {
			String[] temp2 = temp.split("_");
			weightTotal += Integer.parseInt(temp2[1]);
		}
		int random = new Random().nextInt(weightTotal);
		int temp3 = 0;
		for (String temp : list) {
			String[] temp2 = temp.split("_");
			temp3 += Integer.parseInt(temp2[1]);
			if (random < temp3) {
				int weightId = Integer.parseInt(temp2[0]);
				// System.err.println("随机出来的权重组是：" + weightId);
				return EGambleWeight.valueOf(weightId);
			}
		}
		return EGambleWeight.ITEM_WRITE;
	}

	/** 生成热点英雄数组 */
	public static List<String> getRewardCfgToString(List<GambleRewardCfg> cfgList) {
		List<String> heroList = new ArrayList<String>();
		Iterator<GambleRewardCfg> it = cfgList.iterator();
		while (it.hasNext()) {
			heroList.add(it.next().getItemID());
		}
		return heroList;
	}

	public static GambleData getFishintItemToData(TableGamble item) {
		GambleData.Builder data = GambleData.newBuilder();
		data.setPrimaryCount(item.getGambleItem().getSurplusOrdinaryCount());
		data.setPrimaryTime(item.ordinaryTime());
		data.setMiddleTime(item.prayTime());
		return data.build();
	}
}
