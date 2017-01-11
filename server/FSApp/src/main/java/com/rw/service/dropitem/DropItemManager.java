package com.rw.service.dropitem;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.common.HPCUtil;
import com.common.RefInt;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.fightinggrowth.FSuserFightingGrowthMgr;
import com.playerdata.readonly.ItemInfoIF;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.fsutil.common.IntTuple;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.itemPrivilege.ItemPrivilegeFactory;
import com.rwbase.dao.copy.itemPrivilege.PrivilegeDescItem;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.dropitem.DropCfg;
import com.rwbase.dao.dropitem.DropCfgDAO;
import com.rwbase.dao.dropitem.DropRecord;
import com.rwbase.dao.dropitem.DropRecordDAO;

public class DropItemManager {

	private static final int TEN_THOUSAND = 10000;
	private static DropItemManager instance = new DropItemManager();

	public static DropItemManager getInstance() {
		return instance;
	}

	/**
	 * 封神台 掉落并记录必要信息
	 * 
	 * @param player
	 * @param copyCfg
	 * @return
	 */
	public List<ItemInfo> dropAndRecord(String[] dropIdArr, Player player) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < dropIdArr.length; i++) {
			list.add(Integer.parseInt(dropIdArr[i]));
		}
		try {
			return pretreatDrop(player, list, Collections.<Integer> emptyList(), -1, false);
		} catch (DataAccessTimeoutException e) {
			e.printStackTrace();
			return Collections.EMPTY_LIST;
		}
	}

	/**
	 * 获取掉落的物品信息
	 * 
	 * @param player
	 * @param copyCfg
	 * @return
	 * @throws DataAccessTimeoutException
	 */
	public List<? extends ItemInfo> getPretreatDrop(Player player, CopyCfg copyCfg) {
		String userId = player.getUserId();
		DropRecordDAO dropRecordDAO = DropRecordDAO.getInstance();
		DropRecord record;
		try {
			record = dropRecordDAO.getDropRecord(userId);
			DropResult result = record.getPretreatDropList(copyCfg.getLevelID());
			if (result == null) {
				return null;
			}
			return result.getItemInfos();
		} catch (DataAccessTimeoutException e) {
			return null;
		}
	}

	/** 聚宝之地 ！炼息山谷！生存幻境,无尽战火；普通本精英本,扫荡，道具预计掉落 */
	public List<? extends ItemInfo> pretreatDrop(Player player, CopyCfg copyCfg) throws DataAccessTimeoutException {
		String userId = player.getUserId();
		DropRecordDAO dropRecordDAO = DropRecordDAO.getInstance();
		DropRecord record = dropRecordDAO.getDropRecord(userId);
		int copyId = copyCfg.getLevelID();
		boolean firstDrop = false;
		String items = copyCfg.getFirstDropItems();
		if (items != null && items.length() > 0) {
			if (!record.hasDropFirst(copyId)) {
				firstDrop = true;
			}
		}
		if (!firstDrop) {
			items = copyCfg.getItems();
		}
		List<Integer> list = convertToIntList(items);
		List<Integer> guaranteeList = convertToIntList(copyCfg.getDropGuarantee());
		return pretreatDrop(player, list, guaranteeList, copyId, firstDrop);
	}

	private List<Integer> convertToIntList(String str) {
		if (str == null || str.isEmpty()) {
			return Collections.emptyList();
		}
		String[] pItemsID = str.split(",");
		int length = pItemsID.length;
		ArrayList<Integer> result = new ArrayList<Integer>(length);
		for (int i = 0; i < length; i++) {
			try {
				result.add(Integer.parseInt(pItemsID[i]));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public List<ItemInfo> pretreatDrop(Player player, List<Integer> dropRuleList, int copyId, boolean firstDrop) throws DataAccessTimeoutException {
		return pretreatDrop(player, dropRuleList, Collections.<Integer> emptyList(), copyId, firstDrop);
	}

	/**
	 * 预处理掉落
	 * 
	 * @return
	 * @throws DataAccessTimeoutException
	 */
	public List<ItemInfo> pretreatDrop(Player player, List<Integer> dropRuleList, List<Integer> guaranteeList, int copyId, boolean firstDrop) throws DataAccessTimeoutException {
		String userId = player.getUserId();
		IntObjectHashMap<RefInt> dropItemInfoMap = new IntObjectHashMap<RefInt>();
		ArrayList<ItemInfo> dropItemInfoList;
		try {
			DropRecordDAO dropRecordDAO = DropRecordDAO.getInstance();
			DropRecord record = dropRecordDAO.getDropRecord(userId);
			int dropRuleSize = dropRuleList.size();
			for (int j = 0; j < dropRuleSize; j++) {
				int dropRuleId = dropRuleList.get(j);
				List<DropCfg> dropGroupList = DropCfgDAO.getInstance().getDropCfg(dropRuleId);
				if (dropGroupList == null) {
					GameLog.error("DropItemManager", "#pretreatDrop", "找不到掉落规则：" + dropRuleId + ",copyId = " + copyId + ",userId = " + userId);
					continue;
				}

				int size = dropGroupList.size();
				// 一个掉落方案只随机一次
				int random = HPCUtil.getRandom().nextInt(TEN_THOUSAND);
				for (int i = 0; i < size; i++) {
					DropCfg dropCfg = dropGroupList.get(i);
					int rate = dropCfg.getBaseRate();
					if (random < rate) {
						int id = dropCfg.getItemCfgId();
						int dropCount = dropCfg.getDropCount();
						RefInt count = dropItemInfoMap.get(id);
						if (count == null) {
							dropItemInfoMap.put(id, new RefInt(dropCount));
						} else {
							count.value += dropCount;
						}
						break;
					}
					random -= rate;
				}
			}
			ArrayList<IntTuple<Integer, DropGuaranteeState>> stateList = null;
			// 检查掉落保底与不掉落保底
			int guaranteeSize = guaranteeList.size();
			if (guaranteeSize > 0) {
				DropGuaranteeData guaranteeData = DropGuaranteeDAO.getInstance().get(userId);
				if (guaranteeData == null) {
					GameLog.error("DropItemManager", userId, "获取保底掉落数据失败");
				} else {
					DropGuaranteeCfgDAO guaranteeCfgDAO = DropGuaranteeCfgDAO.getInstance();
					stateList = new ArrayList<IntTuple<Integer, DropGuaranteeState>>(guaranteeSize);
					for (int i = guaranteeSize; --i >= 0;) {
						Integer guaranteeId = guaranteeList.get(i);
						DropGuaranteeCfg cfg = guaranteeCfgDAO.getDropGuarantee(guaranteeId);
						if (cfg == null) {
							continue;
						}
						int itemTemplateId = cfg.getItemTemplateId();
						if (itemTemplateId <= 0) {
							continue;
						}
						// 检查本次是否掉落
						boolean drop = dropItemInfoMap.containsKey(itemTemplateId);
						DropGuaranteeRecord guaranteeRecord = guaranteeData.getRecord(guaranteeId);
						DropGuaranteeState state;
						if (guaranteeRecord != null) {
							int times;
							// 记录的道具ID不一致，清0
							int itemId = guaranteeRecord.getId();
							if (itemId != cfg.getItemTemplateId()) {
								GameLog.error("DropGuarantee", userId, "清空不一致掉落ID,old=" + itemId + ",new=" + cfg.getItemTemplateId());
								times = 0;
								guaranteeRecord.setT(times);
							} else {
								times = guaranteeRecord.getT();
							}
							// 之前掉落次数n>=0 次(即连续掉落n次)
							if (times >= 0) {
								if (!drop) {
									// 如果这次不掉，记录不掉落1次(不检查保底)
									state = DropGuaranteeState.MISS_RESET;
								} else {
									// 如果这次掉落，检查是否到底不掉落阀值，检查方式 之前掉落次数>=不掉落阀值，触发不掉落，并记录不掉落1次；否则继续增加掉落次数
									int maxCount = cfg.getMaxDropCount();
									if (times < maxCount) {
										state = DropGuaranteeState.ADD_DROP;
									} else {
										state = DropGuaranteeState.MISS_RESET;
										dropItemInfoMap.remove(itemId);
									}
								}
							} else {
								// 之前掉落次数n< 0次(即连续不掉落n次)
								if (drop) {
									//如果这次掉落，记录掉落1次(不检查保底)
									state = DropGuaranteeState.DROP_RESET;
								} else {
									//如果这次不掉落，检查是否到底掉落阀值，检查方式 之前不掉落次数 >= 掉落阀值，触发掉落，并记录掉落1次；否则继续     增加不掉落次数
									int maxCount = cfg.getMaxMisspCount();
									if (Math.abs(times) < maxCount) {
										state = DropGuaranteeState.ADD_MISS;
									} else {
										state = DropGuaranteeState.DROP_RESET;
										dropItemInfoMap.put(itemId, new RefInt(1));
									}
								}
							}
						} else {
							//第一次生成记录，按掉落情况来记录，不检查保底
							if (drop) {
								state = DropGuaranteeState.DROP_RESET;
							} else {
								state = DropGuaranteeState.MISS_RESET;
							}
						}
						IntTuple<Integer, DropGuaranteeState> tuple = new IntTuple<Integer, DropGuaranteeState>(itemTemplateId, guaranteeId, state);
						stateList.add(tuple);
					}
				}
			}

			// 生成掉落列表
			dropItemInfoList = new ArrayList<ItemInfo>(dropItemInfoMap.size());
			for (Iterator<IntObjectMap.Entry<RefInt>> it = dropItemInfoMap.iterator(); it.hasNext();) {
				IntObjectMap.Entry<RefInt> entry = it.next();
				ItemInfo itemInfo = new ItemInfo(entry.key(), entry.value().value);
				dropItemInfoList.add(itemInfo);
			}

			// TODO 下面这段硬编码需要整理
			CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(copyId);
			if (!firstDrop && copyCfg != null) {
				Map<Integer, Integer> map = ActivityRateTypeMgr.getInstance().getEspecialItemtypeAndEspecialWithTime(player, copyCfg.getLevelType());
				int multipleItem = ActivityRateTypeMgr.getInstance().getMultiple(map, eSpecialItemId.item.getValue());

				List<PrivilegeDescItem> totalPriv = new ArrayList<PrivilegeDescItem>();
				if (multipleItem >= 0.001f || multipleItem <= -0.001f) {
					PrivilegeDescItem privDescItem = new PrivilegeDescItem(0, multipleItem);
					totalPriv.add(privDescItem);
				}
				List<? extends PrivilegeDescItem> privList = FSuserFightingGrowthMgr.getInstance().getPrivilegeDescItem(player);
				if (null != privList && !privList.isEmpty()) {
					totalPriv.addAll(privList);
				}
				if (!totalPriv.isEmpty()) {
					ArrayList<ItemInfo> privDropItemInfoList = new ArrayList<ItemInfo>();
					for (ItemInfo iteminfo : dropItemInfoList) {
						ItemInfoIF newItemIF = ItemPrivilegeFactory.createPrivilegeItem(iteminfo, totalPriv);
						privDropItemInfoList.add(ItemPrivilegeFactory.getItemInfo(newItemIF));
					}
					dropItemInfoList = privDropItemInfoList;
				}
				// 上边为通用活动3的多倍奖励，下边为通用活动9的活动掉落--------------------------------------------------
				ActivityExchangeTypeMgr.getInstance().AddItemOfExchangeActivityBefore(player, copyCfg, dropItemInfoList);
			}

			if (copyId > 0) {
				List<ItemInfo> result = Collections.unmodifiableList(dropItemInfoList);
				record.putPretreatDropList(copyId, new DropResult(result, firstDrop, stateList));
			} else {
				recordDrop(record, false, copyId);
			}
		} catch (Throwable t) {
			GameLog.error("DropItemManager", userId, "掉落异常:", t);
			return Collections.emptyList();
		}
		return dropItemInfoList;
	}

	/**
	 * 提取预先生成好的掉落列表
	 * 
	 * @param player
	 * @param copyId
	 * @return
	 * @throws DataAccessTimeoutException
	 */
	public List<? extends ItemInfo> extractDropPretreatment(Player player, int copyId, boolean isWin) throws DataAccessTimeoutException {
		List<ItemInfo> itemInfoList = null;
		try {
			String userId = player.getUserId();
			DropRecordDAO dropRecordDAO = DropRecordDAO.getInstance();
			DropRecord record = dropRecordDAO.getDropRecord(userId);
			// 获取并删除
			DropResult dropResult = record.extract(copyId);
			if (dropResult == null) {
				GameLog.error("DropItemManager", userId, "缺少战斗结果掉落集");
				return Collections.emptyList();
			}
			itemInfoList = dropResult.getItemInfos();
			List<IntTuple<Integer, DropGuaranteeState>> list = dropResult.getGuaranteeList();
			if (isWin && list != null && !list.isEmpty()) {
				DropGuaranteeDAO guaranteeDAO = DropGuaranteeDAO.getInstance();
				DropGuaranteeData guaranteeData = guaranteeDAO.get(userId);
				if (guaranteeData != null) {
					for (int i = list.size(); --i >= 0;) {
						IntTuple<Integer, DropGuaranteeState> tuple = list.get(i);
						Integer guaranteeId = tuple.getT1();
						DropGuaranteeRecord guaranteeRecord = guaranteeData.getRecord(guaranteeId);
						if (guaranteeRecord == null) {
							guaranteeRecord = new DropGuaranteeRecord();
							guaranteeData.addRecord(guaranteeId, guaranteeRecord);
						}
						guaranteeRecord.setId(tuple.getValue());
						DropGuaranteeState state = tuple.getT2();
						int times = guaranteeRecord.getT();
						if (state == DropGuaranteeState.DROP_RESET) {
							guaranteeRecord.setT(1);
						} else if (state == DropGuaranteeState.MISS_RESET) {
							guaranteeRecord.setT(-1);
						} else if (state == DropGuaranteeState.ADD_DROP) {
							if (times < 0) {
								GameLog.error("DropItemManager", userId, "ADD_DROP state is error:" + times + "," + guaranteeId);
								guaranteeRecord.setT(1);
							} else {
								guaranteeRecord.setT(times + 1);
							}
						} else {
							if (times > 0) {
								GameLog.error("DropItemManager", userId, "ADD_MISS state is error:" + times + "," + guaranteeId);
								guaranteeRecord.setT(-1);
							} else {
								guaranteeRecord.setT(times - 1);
							}
						}
					}
					guaranteeDAO.update(guaranteeData);
				} else {
					GameLog.error("DropItemManager", userId, "记录副本保底掉落失败：" + list);
				}
			}
			recordDrop(record, dropResult.isFirstDrop(), copyId);
			return itemInfoList;
		} catch (Throwable t) {
			GameLog.error("DropItemManager", player == null ? "null" : player.getUserId(), "", t);
			if (itemInfoList != null) {
				return itemInfoList;
			} else {
				return Collections.emptyList();
			}
		}
	}

	private void recordDrop(DropRecord record, boolean isFirstDrop, int copyId) {
		if (isFirstDrop && copyId > 0) {
			record.addFirstDrop(copyId);
			DropRecordDAO.getInstance().update(record);
			GameLog.error("DropItemManaer", "#trace", "记录首掉：" + record.getUserId() + "," + copyId);
		}
	}

}
