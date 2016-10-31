package com.rw.service.gamble;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.common.HPCUtil;
import com.common.RefInt;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.rw.fsutil.common.Pair;
import com.rw.service.gamble.datamodel.DropMissingCfg;
import com.rw.service.gamble.datamodel.DropMissingCfgHelper;
import com.rw.service.gamble.datamodel.DropMissingLogic;
import com.rw.service.gamble.datamodel.GambleAdwardItem;
import com.rw.service.gamble.datamodel.GambleDropGroup;
import com.rw.service.gamble.datamodel.GambleDropHistory;
import com.rw.service.gamble.datamodel.GambleHistoryRecord;
import com.rw.service.gamble.datamodel.GambleHotHeroPlan;
import com.rw.service.gamble.datamodel.GambleOnePlanDropData;
import com.rw.service.gamble.datamodel.GamblePlanCfg;
import com.rw.service.gamble.datamodel.GamblePlanCfgHelper;
import com.rw.service.gamble.datamodel.GambleRecord;
import com.rw.service.gamble.datamodel.GambleRecordDAO;
import com.rw.service.gamble.datamodel.HotGambleCfgHelper;
import com.rw.service.gamble.datamodel.IDropGambleItemPlan;
import com.rw.service.gamble.datamodel.ItemOrHeroGambleInfo;
import com.rwbase.dao.item.SoulStoneCfgDAO;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.SoulStoneCfg;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwproto.GambleServiceProtos.DropData;
import com.rwproto.GambleServiceProtos.EGambleRequestType;
import com.rwproto.GambleServiceProtos.EGambleResultType;
import com.rwproto.GambleServiceProtos.EGambleType;
import com.rwproto.GambleServiceProtos.ELotteryType;
import com.rwproto.GambleServiceProtos.GambleData;
import com.rwproto.GambleServiceProtos.GambleRequest;
import com.rwproto.GambleServiceProtos.GambleResponse;
import com.rwproto.GambleServiceProtos.GambleResponse.Builder;
import com.rwproto.GambleServiceProtos.ItemPreviewData;
import com.rwproto.GambleServiceProtos.PreviewData;
import com.rwproto.MsgDef.Command;

public class GambleLogicHelper {
	/** 准备推送的垂钓数据 */
	public static GambleResponse.Builder prepareGambleData(GambleRequest request, String defaultItem, Player player) {
		GambleHotHeroPlan.InitTodayHotHeroList(defaultItem);
		GambleResponse.Builder response = GambleResponse.newBuilder();
		response.setRequest(request);
		response.addAllHeroList(GambleHotHeroPlan.getTodayHotList());

		// TODO 暂时未按新协议发送数据，最大限度减少客户端的修改
		// response.addAllDropHistory(getFinshingData(uid));

		RefInt coinLeftGCount = new RefInt();
		RefInt goldLeftGCount = new RefInt();
		// 填写兼容旧协议的数据
		response.setGambleData(getFishingItemToData(player, coinLeftGCount, goldLeftGCount));

		// test 设置到达保底组的剩余次数
		ArrayList<Integer> gLeftCountLst = new ArrayList<Integer>(2);
		gLeftCountLst.add(coinLeftGCount.value);
		gLeftCountLst.add(goldLeftGCount.value);
		response.addAllGuaranteenLeftCountList(gLeftCountLst);

		// 不是所有情况都需要发送！ 设置预览数据
		if (request.hasCachePreviewListTag() && request.getCachePreviewListTag() == -1) {
			ArrayList<PreviewData> previewLst = new ArrayList<PreviewData>(4);
			int[] dropTypeList = { 1, 2, 3 };
			for (int i = 0; i < dropTypeList.length; i++) {
				int dropType = dropTypeList[i];
				Iterable<GamblePlanCfg> lst = GamblePlanCfgHelper.getInstance().getConfigList(dropType);
				for (GamblePlanCfg gamblePlanCfg : lst) {
					PreviewData.Builder pb = PreviewData.newBuilder();
					pb.setMinLevel(gamblePlanCfg.getLevelStart());
					pb.setMaxLevel(gamblePlanCfg.getLevelEnd());
					// pb.setGamblePlaId(gamblePlanCfg.getKey());
					pb.setDropType(dropType);

					IDropGambleItemPlan plan = gamblePlanCfg.getChargePlan();
					Iterable<ItemOrHeroGambleInfo> pos = plan.getGamblePosibles();
					for (ItemOrHeroGambleInfo info : pos) {
						ItemPreviewData.Builder value = ItemPreviewData.newBuilder();
						value.setIsGuaranteen(info.isGuarantee());
						value.setItemId(info.getId());
						if (info.isHero()) {
							pb.addHeroList(value);
						} else {
							pb.addItemList(value);
						}
					}

					// TODO 去重复
					if (gamblePlanCfg.getHotCount() > 0) {
						HotGambleCfgHelper hotGambleConfig = HotGambleCfgHelper.getInstance();
						RefInt slotCount = new RefInt();
						String heroId = hotGambleConfig.getTodayGuanrateeHotHero(slotCount);
						ItemPreviewData.Builder value = ItemPreviewData.newBuilder();
						value.setItemId(heroId);
						value.setIsGuaranteen(true);
						if (GambleLogicHelper.isValidHeroOrSoulId(heroId)) {
							pb.addHeroList(value);
						} else {
							pb.addItemList(value);
						}
						value = null;

						String errDefaultModelId = GambleLogicHelper.isValidHeroOrItemId(heroId) ? heroId : defaultItem;
						int hotPlanId = hotGambleConfig.getTodayHotPlanId();
						GambleHotHeroPlan hotPlan = GambleHotHeroPlan.getTodayHotHeroPlan(hotPlanId, GambleHandler.HotHeroPoolSize, errDefaultModelId);
						GambleDropGroup hot = hotPlan.getHotPlan();
						String[] hotHeroList = hot.getPlans();
						for (String hotItemId : hotHeroList) {
							boolean isHero = false;
							if (GambleLogicHelper.isValidHeroOrSoulId(hotItemId)) {
								isHero = true;
								if (GambleLogicHelper.isHeroSoul(hotItemId)) {
									hotItemId = GambleLogicHelper.ConvertSoulIdToHeroModelId(hotItemId);
									if (hotItemId == null) {
										continue;
									}
								}
							} else {
								isHero = false;
							}

							value = ItemPreviewData.newBuilder();
							value.setItemId(hotItemId);
							value.setIsGuaranteen(false);
							if (isHero) {
								pb.addHeroList(value);
							} else {
								pb.addItemList(value);
							}
						}
					}
					previewLst.add(pb.build());
				}
			}
			response.addAllPreviewDataList(previewLst);
		}

		response.setResultType(EGambleResultType.SUCCESS);
		return response;
	}

	public static String ConvertSoulIdToHeroModelId(String soulId) {
		String result = null;
		SoulStoneCfg soulCfg = SoulStoneCfgDAO.getInstance().getCfgById(soulId);
		if (soulCfg != null) {
			result = GambleLogicHelper.ConvertSoulToHeroModelId(soulCfg);
		}
		return result;
	}

	private static String ConvertSoulToHeroModelId(SoulStoneCfg soulCfg) {
		int star = soulCfg.getStar();
		star = star > 0 ? star : 1;
		RoleCfg cfg = RoleCfgDAO.getInstance().getCfgByModeID(String.valueOf(soulCfg.getComposeTargetId()));
		if (cfg != null) {
			return cfg.getRoleId();
		}
		GameLog.error("钓鱼台", "", "魂石没有对应的RoleCfg可抽英雄配置,ID=" + soulCfg.getComposeTargetId());
		return null;
		// return soulCfg.getComposeTargetId()+"_"+star;
	}

	public static Iterable<DropData> getFinshingData(Player player) {
		GamblePlanCfgHelper helper = GamblePlanCfgHelper.getInstance();
		Iterable<GamblePlanCfg> all = helper.getIterateAllCfg();
		ArrayList<DropData> result = new ArrayList<DropData>(helper.getEntryCount());
		for (GamblePlanCfg cfg : all) {
			result.add(getFinshingData(player, cfg.getDropType()));
		}
		return result;
	}

	public static boolean canGambleFreely(Player player) {
		GamblePlanCfgHelper helper = GamblePlanCfgHelper.getInstance();
		Iterable<GamblePlanCfg> all = helper.getIterateAllCfg();
		for (GamblePlanCfg cfg : all) {
			if (isFree(player, cfg.getDropType())) {
				return true;
			}
		}
		return false;
	}

	// 根据配置的方案，找到数据库里面的历史信息并返回
	public static DropData getFinshingData(Player player, int dropType) {
		GambleOnePlanDropData oneData = getOneDropData(player, dropType);
		DropData.Builder result = DropData.newBuilder();
		result.setFreeCount(oneData.getFreeCount());
		result.setLeftTime(oneData.getLeftTime());
		result.setMaxFreeCount(oneData.getMaxFreeCount());
		return result.build();
	}

	public static GambleOnePlanDropData getOneDropData(Player player, int dropType) {
		GamblePlanCfg planCfg = GamblePlanCfgHelper.getInstance().getConfig(dropType, player.getLevel());
		GambleRecordDAO gambleRecords = GambleRecordDAO.getInstance();
		GambleRecord record = gambleRecords.getOrCreate(player.getUserId());
		GambleDropHistory historyRecord = record.getHistory(dropType);
		GambleHistoryRecord historyByGroup = record.getByGroup(planCfg);

		GambleOnePlanDropData result = new GambleOnePlanDropData(historyRecord, planCfg, historyByGroup);
		return result;
	}

	public static void pushGambleItem(Player player, String defaultItem) {
		GambleRequest.Builder request = GambleRequest.newBuilder();
		request.setRequestType(EGambleRequestType.GAMBLE_DATA);
		GambleResponse.Builder response = prepareGambleData(request.build(), defaultItem, player);
		player.SendMsg(Command.MSG_GAMBLE, response.build().toByteString());
	}

	/**
	 * 用一个默认值做容错，并假设这个默认值是没有问题的！
	 * 
	 * @param dropList
	 * @param slotCount
	 * @param itemModelId
	 * @param uid
	 * @param planIdStr
	 * @param defaultModelId
	 * @return
	 */
	public static boolean add2DropList(ArrayList<GambleAdwardItem> dropList, int slotCount, String itemModelId, String uid, String planIdStr, String defaultModelId) {
		// 检查是否具有有效的佣兵配置
		if (StringUtils.isBlank(itemModelId)) {
			GameLog.error("钓鱼台", uid, String.format("配置物品ID无效=%s，配置id:%s,", itemModelId, planIdStr));
			// itemModelId = defaultModelId;
			return false;
		}
		if (slotCount <= 0) {
			GameLog.error("钓鱼台", uid, String.format("配置叠加数量无效，配置:%s", planIdStr));
			slotCount = 1;
			// return false;
		}

		if (itemModelId.indexOf("_") != -1) {
			String[] arr = itemModelId.split("_");
			if (arr == null) {
				// itemModelId = defaultModelId;
				return false;
			} else {
				RoleCfg roleCfg = RoleCfgDAO.getInstance().getConfig(itemModelId);
				if (roleCfg == null) {
					// itemModelId = defaultModelId;
					GameLog.error("钓鱼模块", uid, "钓鱼随机到了模版Id为：" + itemModelId + "的英雄，配置不存在,ID=" + planIdStr);
					return false;
				}
			}
		}

		// GambleRewardData.Builder rewardData = GambleRewardData.newBuilder();
		GambleAdwardItem rewardData = new GambleAdwardItem();
		rewardData.setItemId(itemModelId);
		rewardData.setItemNum(slotCount);
		// dropList.add(rewardData.build());
		dropList.add(rewardData);
		return true;
	}

	public static ByteString SetError(Builder response, Player player, String errLog, String tip) {
		GameLog.error("钓鱼台", player.getUserId(), errLog);
		response.setResultType(EGambleResultType.FAIL);
		if (!StringUtils.isBlank(tip)) {
			response.setTips(tip);
		}
		response.setResultType(EGambleResultType.FAIL);
		return response.build().toByteString();
	}

	public static boolean isValidHeroId(String heroModelId) {
		if (StringUtils.isNotBlank(heroModelId)) {
			if (heroModelId.indexOf("_") != -1) {
				RoleCfg roleCfg = RoleCfgDAO.getInstance().getConfig(heroModelId);
				if (roleCfg == null) {
					System.out.println(heroModelId + " not found");
				}
				return roleCfg != null;
			}
		}
		return false;
	}

	public static boolean isValidHeroOrItemId(String itemModelId) {
		if (StringUtils.isNotBlank(itemModelId)) {
			if (itemModelId.indexOf("_") != -1) {
				RoleCfg roleCfg = RoleCfgDAO.getInstance().getConfig(itemModelId);
				return roleCfg != null;
			}
			try {
				int modelId = Integer.parseInt(itemModelId);
				ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(modelId);// 检查物品的基础模版
				return itemBaseCfg != null;
			} catch (Exception e) {
				GameLog.error("钓鱼台", itemModelId, "无效物品／英雄ID=" + itemModelId);
			}
		}
		return false;
	}

	public static boolean isValidItemId(String itemModelId) {
		if (StringUtils.isNotBlank(itemModelId)) {
			try {
				int modelId = Integer.parseInt(itemModelId);
				ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(modelId);// 检查物品的基础模版
				return itemBaseCfg != null;
			} catch (Exception e) {
				GameLog.error("钓鱼台", itemModelId, "无效物品／英雄ID=" + itemModelId);
			}
		}
		return false;
	}

	// 魂石作为英雄看待！
	public static boolean isValidHeroOrSoulId(String heroModelId) {
		if (StringUtils.isNotBlank(heroModelId)) {
			if (heroModelId.indexOf("_") != -1) {
				RoleCfg roleCfg = RoleCfgDAO.getInstance().getConfig(heroModelId);
				return roleCfg != null;
			}
			if (isHeroSoul(heroModelId)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isHeroSoul(String configId) {
		return StringUtils.isNotBlank(configId) && configId.startsWith("704");
	}

	public static boolean isFree(Player player, int dropType) {
		GambleOnePlanDropData oneData = getOneDropData(player, dropType);
		return oneData.canGambleFree();
	}

	public static boolean isLeftTimeOver(int leftTime) {
		// 让服务端比客户端快2秒
		return leftTime <= 2;
	}

	// 为了兼容旧的协议而做的映射，写死在这个类 以后客户端做好对应修改后删除
	public static final int Primary_One = 1;
	public static final int Middle_One = 2;
	public static final int Advanced_Six = 3;
	public static final int Primary_Ten = 4;
	public static final int Middle_Ten = 5;

	// 返回旧协议的钓鱼数据
	public static GambleData getFishingItemToData(Player player, RefInt coinLeftGCount, RefInt goldLeftGCount) {
		GambleData.Builder data = GambleData.newBuilder();
		GambleOnePlanDropData p1 = GambleLogicHelper.getOneDropData(player, Primary_One);
		// 当天已用普通免费次数
		data.setPrimaryCount(p1.getFreeCount());
		// 初级祈祷免费剩余时间(秒)
		data.setPrimaryTime(p1.getLeftTime());
		if (coinLeftGCount != null) {
			coinLeftGCount.value = p1.getGuaranteeLeftCount();
		}

		GambleOnePlanDropData m1 = GambleLogicHelper.getOneDropData(player, Middle_One);
		// 中级祈祷免费剩余时间(秒)
		data.setMiddleTime(m1.getLeftTime());
		if (goldLeftGCount != null) {
			goldLeftGCount.value = m1.getGuaranteeLeftCount();
		}

		return data.build();
	}

	// 转换请求类型
	public static GambleRequest ConvertRequest(GambleRequest request) {
		EGambleType gambleType = request.getGambleType();
		ELotteryType lotteryType = request.getLotteryType();
		int planKey = 0;
		switch (gambleType) {
		case PRIMARY:
			if (lotteryType == ELotteryType.ONE) {
				planKey = Primary_One;
			} else if (lotteryType == ELotteryType.TEN) {
				planKey = Primary_Ten;
			}
			break;
		case MIDDLE:
			if (lotteryType == ELotteryType.ONE) {
				planKey = Middle_One;
			} else if (lotteryType == ELotteryType.TEN) {
				planKey = Middle_Ten;
			}
			break;
		case ADVANCED:
			if (lotteryType == ELotteryType.SIX) {
				planKey = Advanced_Six;
			}
			break;
		}
		GambleRequest.Builder result = GambleRequest.newBuilder();
		result.mergeFrom(request);
		result.setGamblePlanId(planKey);
		return result.build();
	}

	public static void logTrace(StringBuilder trace, String log, GambleDropGroup tmpGroup) {
		if (trace != null) {
			trace.append(log).append("\n");
			if (tmpGroup != null) {
				// TODO
				String[] plans = tmpGroup.getPlans();
				int[] dis = tmpGroup.getDistributions();
				if (plans != null) {
					if (dis == null || dis.length != plans.length) {
						trace.append("error: distibution is not consistent with plans\n");
					}
					for (int i = 0; i < plans.length; i++) {
						trace.append(plans[i]).append(":");
						if (i < dis.length) {
							trace.append(dis[i]);
						}
						trace.append(",");
					}
					trace.append("\n");
				} else {
					trace.append("error:plans is empty\n");
				}
			} else {
				trace.append("error:tempGroup is null\n");
			}
		}
	}

	public static void logTrace(StringBuilder trace, String log, List<String> checkHistory) {
		if (trace != null) {
			trace.append(log);
			for (String his : checkHistory) {
				trace.append(his).append(",");
			}
			trace.append("\n");
		}
	}

	public static void logTrace(StringBuilder trace, String log) {
		if (trace != null) {
			trace.append(log).append("\n");
		}
	}

	public static boolean testHasHero(ArrayList<GambleAdwardItem> dropList, StringBuilder trace, int gamblePlanId, String uid) {
		if (gamblePlanId == 5) {// 钻石十连抽
			boolean hasHero = false;
			for (GambleAdwardItem item : dropList) {
				String heroId = item.getItemId();
				if (GambleLogicHelper.isValidHeroId(heroId)) {
					hasHero = true;
					break;
				}
			}
			if (!hasHero) {
				String log = "";
				if (trace != null) {
					log = trace.toString();
				}
				GameLog.error("钓鱼台", uid, "钻石十连抽没有抽到英雄\n" + log);
				String hero = GamblePlanCfgHelper.getInstance().getSpecialGuaranteeHero();
				if (isValidHeroId(hero)) {
					// 特殊容错：从配置选一个有效的保底英雄替换了最后一个英雄
					dropList.remove(dropList.size() - 1);
					// GambleRewardData.Builder b = GambleRewardData.newBuilder();
					GambleAdwardItem b = new GambleAdwardItem();
					b.setItemId(hero);
					b.setItemNum(1);
					// dropList.add(b.build());
					dropList.add(b);
				}
			}
			return !hasHero;
		}
		return true;
	}

	public static void logTrace(StringBuilder trace, GambleDropHistory historyRecord, GambleHistoryRecord groupRec) {
		if (trace != null) {
			trace.append("historyRecord:").append(historyRecord.toDebugString(groupRec));
		}
	}

	/**
	 * 获取掉落的物品
	 * 
	 * @param player
	 * @param drop
	 * @param slotCount
	 * @param weight
	 * @return
	 */
	public static String getRandomGroup(Player player, GambleDropGroup drop, RefInt slotCount) {
		return getRandomGroup(player, drop, slotCount, null);
	}

	/**
	 * 获取掉落的物品
	 * 
	 * @param player
	 * @param drop
	 * @param slotCount
	 * @param weight
	 * @return
	 */
	public static String getRandomGroup(Player player, GambleDropGroup drop, RefInt slotCount, RefInt weight) {
		RefInt planIndex = new RefInt();
		Random r = HPCUtil.getRandom();
		String result = drop.getRandomGroup(r, planIndex, weight);// 先获取索引

		slotCount.value = drop.getSlotCountArr()[planIndex.value];// 获取到数量

		// 这个是根据人身上装备的数据来获取掉落
		if (StringUtils.isNotBlank(result)) {
			DropMissingCfg cfg = DropMissingCfgHelper.getInstance().getCfgById(result);// 如果读取到的是DropMissing这类型的掉落
			if (cfg == null) {// 去检查人身上的装备，进行精准掉落
				return result;
			}

			if (player == null) {
				ArrayList<String> tmp = new ArrayList<String>(1);
				tmp.add(result);
				GambleDropGroup again = drop.removeHistory(tmp);
				if (again == null || again.size() <= 0) {
					return null;
				}
				return getRandomGroup(player, again, slotCount, weight);
			}

			result = DropMissingLogic.getInstance().searchMissingItem(player, cfg);

			if (result == null) {
				ArrayList<String> tmp = new ArrayList<String>(1);
				tmp.add(cfg.getKey());

				GambleDropGroup again = drop.removeHistory(tmp);
				if (again == null || again.size() <= 0) {
					return null;
				}
				return getRandomGroup(player, again, slotCount, weight);
			}
		}

		return result;
	}

	/**
	 * 连续生成N个热点 避免重复，如果热点组人数不够才允许重复
	 * 
	 * @param r
	 * @param hotCount
	 * @param guanrateeHero
	 * @return
	 */
	@JsonIgnore
	public static List<Pair<String, Integer>> getHotRandomGroup(Player player, GambleDropGroup drop, int hotCount, String guanrateeHero) {
		List<String> historyRecord = new ArrayList<String>(1);
		List<Pair<String, Integer>> result = new ArrayList<Pair<String, Integer>>(hotCount);
		RefInt slotCount = new RefInt();
		RefInt weight = new RefInt();
		GambleDropGroup tmpGroup = drop;
		String heroId = guanrateeHero;

		while (result.size() < hotCount) {
			if (historyRecord.size() <= 0) {
				historyRecord.add(heroId);
			} else {
				historyRecord.set(0, heroId);
			}
			tmpGroup = tmpGroup.removeHistory(historyRecord);
			if (tmpGroup != null && tmpGroup.size() > 0) {
				heroId = getRandomGroup(player, drop, slotCount, weight);
			} else {
				heroId = guanrateeHero;
			}
			if (heroId == null)
				heroId = guanrateeHero;
			result.add(Pair.Create(heroId, weight.value));
		}

		return result;
	}
}