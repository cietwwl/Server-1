package com.rwbase.dao.fetters;

import io.netty.util.collection.IntObjectHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.fetters.pojo.MagicEquipFetterRecord;
import com.rwbase.dao.fetters.pojo.SynMagicEquipFetterData;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;
import com.rwbase.dao.fetters.pojo.cfg.dao.FetterMagicEquipCfgDao;
import com.rwbase.dao.fetters.pojo.cfg.dao.MagicEquipConditionKey;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/**
 * 法宝神器羁绊holder
 * 
 * @author Alex
 *
 *         2016年7月18日 上午9:54:58
 */
public class MagicEquipFetterDataHolder {

	private static final eSynType syType = eSynType.MAGICEQUIP_FETTER;

	private AtomicInteger dataVersion = new AtomicInteger(0);

	private final String userID;

	public MagicEquipFetterDataHolder(String userID) {
		this.userID = userID;
		checkRecord();
	}

	/**
	 * 检查是否有记录，如果没有就新生成
	 */
	private MagicEquipFetterRecord checkRecord() {
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if (item == null) {
			item = new MagicEquipFetterRecord();
			item.setId(userID);
			item.setUserId(userID);
			getItemStore().addItem(item);
		}
		return item;
	}

	/**
	 * 同步所有法宝神器羁绊数据
	 * 
	 * @param player
	 * @param version
	 *            版本 0表示强制同步
	 */
	public void synAllData(Player player, int version) {
		if (version != 0 && version == dataVersion.get()) {
			return;
		}
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if (item == null) {
			item = checkRecord();
			return;
		}

//		 StringBuffer sb = new StringBuffer("同步羁绊数据：");
//		 for (Integer id : item.getAllFetters()) {
//		 sb.append("[").append(id).append("]");
//		 }
//		 System.out.println(sb.toString());
		SynMagicEquipFetterData synData = new SynMagicEquipFetterData(userID, item.getAllFetters());

		ClientDataSynMgr.synData(player, synData, syType, eSynOpType.UPDATE_SINGLE);

	}

	private MapItemStore<MagicEquipFetterRecord> getItemStore() {
		MapItemStoreCache<MagicEquipFetterRecord> itemStoreCache = MapItemStoreFactory.getMagicEquipFetterCache();
		return itemStoreCache.getMapItemStore(userID, MagicEquipFetterRecord.class);
	}

	/**
	 * 检查英雄羁绊数据
	 * 
	 * @param fetter
	 * @param modelId
	 */
	public void checkFixEquipFetterRecord(MagicEquipConditionCfg fetter, int modelId) {
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if (item == null) {
			item = checkRecord();
		}
		List<Integer> fetterIDs = item.getFixEquipFetters();
		if (fetter == null) {
			// 降星到0,就要把目标英雄的神器羁绊去了
			List<Integer> temp = new ArrayList<Integer>();
			temp.addAll(fetterIDs);
			boolean remove = false;
			for (Integer id : temp) {
				MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
				if (cfg.getHeroModelID() == modelId) {
					remove = true;
					fetterIDs.remove(id);
					break;
				}
			}

			if (remove) {
				item.setFixEquipFetters(fetterIDs);
				getItemStore().updateItem(item);
				dataVersion.incrementAndGet();

			}
			return;
		}

		List<Integer> clearOld = new ArrayList<Integer>();

		// 检查数据库里有没有相同类型的旧数据
		for (Integer id : fetterIDs) {
			MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			if (id == fetter.getUniqueId()) {
				// 数据库已经有记录，就不做更新了
				return;
			}
			if (fetter.getUniqueId() != id && cfg.getType() == fetter.getType() && cfg.getSubType() == fetter.getSubType()) {
				clearOld.add(id);
			}
		}

		fetterIDs.removeAll(clearOld);
		fetterIDs.add(fetter.getUniqueId());

		item.setFixEquipFetters(fetterIDs);
		getItemStore().updateItem(item);
		dataVersion.incrementAndGet();

	}
	
	/**
	 * 检查数据库内法宝羁绊记录是否与当前集合一致，如果没有则进行添加 modify by Jamaz @2016-10-13
	 * 
	 * @param curCfgs
	 * @param modelID
	 *            TODO 英雄modelID
	 */
	public boolean compareMagicFetterRcord(Map<MagicEquipConditionKey, MagicEquipConditionCfg> curCfgs, int modelID) {
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if (item == null) {
			item = checkRecord();
		}
		// 检查当前拥有法宝羁绊与通过配置与法宝计算出来的羁绊是否一致
		int cfgSize = curCfgs.size();
		List<Integer> fetterIDs = item.getMagicFetters();
		int size = fetterIDs.size();
		if (cfgSize == 0 && size == 0) {
			return false;
		}
		IntObjectHashMap<MagicEquipConditionCfg> curCfgsMap;
		if (cfgSize > 0) {
			curCfgsMap = new IntObjectHashMap<MagicEquipConditionCfg>(cfgSize << 1);
			for (MagicEquipConditionCfg cfg : curCfgs.values()) {
				curCfgsMap.put(cfg.getUniqueId(), cfg);
			}
		}else{
			curCfgsMap = new IntObjectHashMap<MagicEquipConditionCfg>(5);
		}
		boolean equals = true;
		if (size == cfgSize) {
			for (int i = size; --i >= 0;) {
				Integer id = fetterIDs.get(i);
				if (id == null) {
					equals = false;
					break;
				}
				if (!curCfgsMap.containsKey(id)) {
					equals = false;
					break;
				}
			}
		}else{
			equals = false;
		}
		// 配置没有变化
		if (equals) {
			return false;
		}

		FetterMagicEquipCfgDao fetterMagicEquipCfgDao = FetterMagicEquipCfgDao.getInstance();
		// 先找出数据库里多出来的记录，判断是否要保留
		for (Integer id : fetterIDs) {
			if (id == null) {
				continue;
			}
			if (curCfgsMap.containsKey(id)) {
				continue;
			}
			MagicEquipConditionCfg cfg = fetterMagicEquipCfgDao.get(id);
			// 增加判空
			if (cfg == null) {
				continue;
			}

			MagicEquipConditionCfg sameTypeCfg = curCfgs.get(cfg.getCompositeKey());
			// TODO 如果没有此类型，不保留，需要和策划确认(原逻辑是这样跑)
			if (sameTypeCfg == null) {
				continue;
			}

			// 表示数据库有此记录，但现在不属于当前应该拥有的记录
			// 检查是否要保留
			if (cfg.recordOldData() && cfg.getHeroModelID() == modelID) {
				// 对比同类型，不同id的配置
				if (sameTypeCfg.getConditionLevel() <= cfg.getConditionLevel()) {
					// 替换同类型配置
					curCfgsMap.remove(sameTypeCfg.getUniqueId());
					curCfgsMap.put(cfg.getUniqueId(), cfg);
				}
			}
		}
		// 更新羁绊集
		int[] keys = curCfgsMap.keys();
		int newSize = keys.length;
		ArrayList<Integer> newList = new ArrayList<Integer>(newSize);
		for (int i = 0; i < newSize; i++) {
			newList.add(keys[i]);
		}

		item.setMagicFetters(newList);
		getItemStore().updateItem(item);
		dataVersion.incrementAndGet();
		return true;
	}

	public int getVersion() {
		return dataVersion.get();
	}

	/**
	 * 获取英雄的神器羁绊列表
	 * 
	 * @param modelId
	 * @return
	 */
	public List<Integer> getFixEquipFetterByModelID(int modelId) {
		List<Integer> temp = new ArrayList<Integer>();
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		List<Integer> fetterIDs = item.getFixEquipFetters();
		for (Integer id : fetterIDs) {
			MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			if (cfg.getHeroModelID() == modelId) {
				temp.add(id);
			}
		}
		return temp;
	}

	/**
	 * 获取法宝的
	 * 
	 * @return
	 */
	public List<Integer> getMagicFetters() {
		List<Integer> temp = new ArrayList<Integer>();
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		List<Integer> fetterIDs = item.getMagicFetters();
		temp.addAll(fetterIDs);
		return temp;
	}

}
