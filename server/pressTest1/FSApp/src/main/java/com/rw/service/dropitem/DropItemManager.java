package com.rw.service.dropitem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.HPCUtil;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.service.copy.CopyHandler;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.dropitem.DropAdjustmentCfg;
import com.rwbase.dao.dropitem.DropAdjustmentCfgDAO;
import com.rwbase.dao.dropitem.DropAdjustmentState;
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
		List<Integer> list = CopyHandler.convertToIntList(items);
		return pretreatDrop(player, list, copyId, firstDrop);
	}

	public List<? extends ItemInfo> pretreatDrop(Player player, List<Integer> list, int copyId) throws DataAccessTimeoutException {
		return pretreatDrop(player, list, copyId, false);
	}

	/**
	 * 预处理掉落
	 * 
	 * @return
	 * @throws DataAccessTimeoutException
	 */
	public List<? extends ItemInfo> pretreatDrop(Player player, List<Integer> list, int copyId, boolean firstDrop) throws DataAccessTimeoutException {
		ArrayList<ItemInfo> dropItemInfoList = new ArrayList<ItemInfo>();
		try {
			String userId = player.getUserId();
			DropRecordDAO dropRecordDAO = DropRecordDAO.getInstance();
			DropRecord record = dropRecordDAO.getDropRecord(userId);
			HashMap<Integer, DropAdjustmentState> adjustmentMap = null;
			int dropRuleSize = list.size();
			for (int j = 0; j < dropRuleSize; j++) {
				int dropRuleId = list.get(j);
				List<DropCfg> dropGroupList = DropCfgDAO.getInstance().getDropCfg(dropRuleId);
				if (dropGroupList == null) {
					GameLog.error("找不到掉落规则：" + dropRuleId + ",userId = " + userId);
					continue;
				}

				HashMap<Integer, Integer> tempRateMap = null;
				int size = dropGroupList.size();
				boolean minRateDrop = false;
				for (int i = 0; i < size; i++) {
					DropCfg dropCfg = dropGroupList.get(i);
					int dropRecordId = dropCfg.getId();
					int baseRate = dropCfg.getBaseRate();
					DropAdjustmentCfg adjustmentCfg = DropAdjustmentCfgDAO.getInstance().getDropAdjustment(dropRecordId);
					if (adjustmentCfg == null) {
						continue;
					}
					int additiveRate = adjustmentCfg.getAdditiveRate();
					int minRate = adjustmentCfg.getMinRate();
					if (additiveRate <= 0 && minRate <= 0) {
						continue;
					}
					if (adjustmentMap == null) {
						adjustmentMap = new HashMap<Integer, DropAdjustmentState>();
					}
					DropAdjustmentState state = adjustmentMap.get(adjustmentCfg);
					// 已经触发的不再生效
					if (state != null && state != DropAdjustmentState.FAIL) {
						continue;
					}

					int currentTimes = record.getDropRecordTimes(dropRecordId);
					int currentRate;
					// 根据随机过的次数计算当前已经达到的概率
					if (currentTimes > 0) {
						currentRate = TEN_THOUSAND / currentTimes;
					} else {
						currentRate = TEN_THOUSAND;
					}

					// 达到最小概率时必掉
					if (minRate > 0) {
						if (currentRate <= minRate) {
							addOrMerge(dropItemInfoList, dropCfg);
							adjustmentMap.put(dropRecordId, DropAdjustmentState.MIN_RATE);
							minRateDrop = true;
							break;
						} else {
							adjustmentMap.put(dropRecordId, DropAdjustmentState.FAIL);
						}
					}
					if (additiveRate > 0 && currentRate <= baseRate) {
						if (tempRateMap == null) {
							tempRateMap = new HashMap<Integer, Integer>(8);
						}
						tempRateMap.put(dropRecordId, additiveRate);
					}
				}
				// 按顺序检索，如果触发 最低几率掉落，也是以为顺序只触发最前面的那一个
				if (minRateDrop) {
					continue;
				}
				// 一个掉落方案只随机一次
				int random = HPCUtil.getRandom().nextInt(TEN_THOUSAND);
				for (int i = 0; i < size; i++) {
					DropCfg dropCfg = dropGroupList.get(i);
					int rate = dropCfg.getBaseRate();
					int dropRecordId = dropCfg.getId();
					// 检测有没有概率的增加
					boolean addRate = false;
					if (tempRateMap != null) {
						Integer value = tempRateMap.get(dropRecordId);
						if (value != null) {
							rate += value;
							addRate = true;
						}
					}
					if (random < rate) {
						addOrMerge(dropItemInfoList, dropCfg);
						if (addRate) {
							adjustmentMap.put(dropRecordId, DropAdjustmentState.ADD_RATE);
						}
						break;
					}
					random -= rate;
					if (addRate) {
						adjustmentMap.put(dropRecordId, DropAdjustmentState.FAIL);
					}
				}
			}
			List<ItemInfo> result = Collections.unmodifiableList(dropItemInfoList);
			record.putPretreatDropList(copyId, new DropResult(result, adjustmentMap, firstDrop));
			dropRecordDAO.update(record);
		} catch (Throwable t) {
			GameLog.error(t);
		}
		return dropItemInfoList;
	}

	private void addOrMerge(List<ItemInfo> list, DropCfg dropCfg) {
		int id = dropCfg.getItemCfgId();
		for (int i = list.size(); --i >= 0;) {
			ItemInfo info = list.get(i);
			if (info.getItemID() == id) {
				info.setItemNum(info.getItemNum() + dropCfg.getDropCount());
				return;
			}
		}
		ItemInfo itemInfo = new ItemInfo();
		itemInfo.setItemID(id);
		itemInfo.setItemNum(dropCfg.getDropCount());
		list.add(itemInfo);
	}

	/**
	 * 提取预先生成好的掉落列表
	 * 
	 * @param player
	 * @param copyId
	 * @return
	 * @throws DataAccessTimeoutException
	 */
	public List<? extends ItemInfo> extractDropPretreatment(Player player, int copyId) throws DataAccessTimeoutException {
		List<ItemInfo> itemInfoList = null;
		try {
			String userId = player.getUserId();
			DropRecordDAO dropRecordDAO = DropRecordDAO.getInstance();
			DropRecord record = dropRecordDAO.getDropRecord(userId);
			// 获取并删除
			DropResult dropResult = record.extract(copyId);
			if (dropResult == null) {
				GameLog.error("缺少战斗结果掉落集：" + userId);
				return Collections.EMPTY_LIST;
			}
			itemInfoList = dropResult.getItemInfos();
			Map<Integer, DropAdjustmentState> adjustmentSet = dropResult.getDropRuleMap();
			boolean needUpdate = false;
			if (adjustmentSet != null) {
				for (Map.Entry<Integer, DropAdjustmentState> entry : adjustmentSet.entrySet()) {
					Integer dropRecordId = entry.getKey();
					DropAdjustmentState state = entry.getValue();
					if (state == DropAdjustmentState.FAIL) {
						record.addDropMissTimes(dropRecordId, 1);
						needUpdate = true;
					} else if (record.clearDropMissTimes(dropRecordId) > 0) {
						needUpdate = true;
					}
				}
			}

			if (dropResult.isFirstDrop()) {
				record.addFirstDrop(copyId);
				needUpdate = true;
			}
			if (needUpdate) {
				dropRecordDAO.update(record);
			}
			return itemInfoList;
		} catch (Throwable t) {
			GameLog.error(t);
			if (itemInfoList != null) {
				return itemInfoList;
			} else {
				return Collections.EMPTY_LIST;
			}
		}
	}

}
