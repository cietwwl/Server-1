package com.playerdata;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.readonly.CopyDataIF;
import com.playerdata.readonly.CopyDataMgrIF;
import com.playerdata.readonly.CopyInfoCfgIF;
import com.playerdata.readonly.ItemInfoIF;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.copypve.CopyInfoCfgDAO;
import com.rwbase.dao.copypve.CopyLevelCfgDAO;
import com.rwbase.dao.copypve.CopyType;
import com.rwbase.dao.copypve.TableCopyDataDAO;
import com.rwbase.dao.copypve.pojo.CopyData;
import com.rwbase.dao.copypve.pojo.CopyInfoCfg;
import com.rwbase.dao.copypve.pojo.CopyLevelCfg;
import com.rwbase.dao.copypve.pojo.TableCopyData;
import com.rwproto.PrivilegeProtos.PvePrivilegeNames;

public class CopyDataMgr implements CopyDataMgrIF {
	public static final int PVE_RESET_TIME_HOUR = 5;// PVE模块重置的时间点

	private TableCopyDataDAO tableCopyDataDAO = TableCopyDataDAO.getInstance();
	private Player player;

	public void init(Player playerP) {
		player = playerP;
		TableCopyData pTableCopyData = tableCopyDataDAO.get(playerP.getUserId());
		List<CopyInfoCfg> cfgList = CopyInfoCfgDAO.getInstance().getAllCfg();
		// 角色第一次初始化
		List<CopyData> copyList = pTableCopyData.getCopyList();
		boolean save = false;
		for (CopyInfoCfgIF cfg : cfgList) {
			// 调整bAdd位置
			boolean bAdd = true;
			for (CopyData data : copyList) {
				if (data.getInfoId() == cfg.getId()) {
					bAdd = false;
					break;
				}
			}
			if (bAdd) {
				CopyData data = new CopyData();
				data.setCopyCount(cfg.getCount());
				// data.setResetCount(getRestCountByCopyType(cfg.getType()));
				data.setCopyType(cfg.getType());
				data.setInfoId(cfg.getId());
				data.setPassMap(getCelestialDegreeMap());
				copyList.add(data);
				save = true;
			}
		}
		if (save) {
			this.save();
		}
	}

	public boolean save() {
		tableCopyDataDAO.update(player.getUserId());
		return true;
	}

	/**
	 * 需要检查一下开启的时间是什么时候
	 * 
	 * @param copyType
	 * @param hour
	 *            开启的时间 一般都是5点
	 * @return
	 */
	public List<CopyInfoCfgIF> getTodayInfoCfg(int copyType) {
		List<CopyInfoCfgIF> list = new ArrayList<CopyInfoCfgIF>();

		List<CopyInfoCfg> listInfo = CopyInfoCfgDAO.getInstance().getAllCfg();
		for (CopyInfoCfg cfg : listInfo) {
			int type = cfg.getType();
			if (type != copyType) {
				continue;
			}

			String[] time = cfg.getTime().split(",");
			for (int i = 0; i < time.length; i++) {
				if (DateUtils.isTheSameDayOfWeekAndHour(Integer.valueOf(time[i]), PVE_RESET_TIME_HOUR)) {
					list.add(cfg);
				}
			}
		}
		return list;
	}

	public List<CopyInfoCfgIF> getTodayInfoCfgList(int copyType) {
		init(player);
		List<CopyInfoCfgIF> list = new ArrayList<CopyInfoCfgIF>();

		List<CopyInfoCfg> listInfo = CopyInfoCfgDAO.getInstance().getAllCfg();
		for (CopyInfoCfg cfg : listInfo) {
			int type = cfg.getType();
			if (type != copyType) {
				continue;
			}

			list.add(cfg);
		}

		return list;
	}

	// 获取同一天的玩法
	public static List<CopyInfoCfgIF> getSameDayInfoList() {
		List<CopyInfoCfgIF> list = new ArrayList<CopyInfoCfgIF>();
		List<CopyInfoCfg> listInfo = CopyInfoCfgDAO.getInstance().getAllCfg();
		for (CopyInfoCfg cfg : listInfo) {
			String[] time = cfg.getTime().split(",");
			for (int i = 0; i < time.length; i++) {
				if (DateUtils.isTheSameDayOfWeekAndHour(Integer.valueOf(time[i]), PVE_RESET_TIME_HOUR)) {
					list.add(cfg);
				}
			}
		}
		return list;
	}

	/*
	 * public CopyData getByTrialType(int copyType) { List<CopyData> copyList =
	 * pTableCopyData.getCopyList(); CopyData data = null; for(CopyData copy :
	 * copyList){ if(copy.getCopyType() == copyType){ data = copy; break; } }
	 * return data; }
	 */

	private CopyData getByInfoWithId(int infoId) {
		List<CopyData> copyList = getTableCopyData().getCopyList();
		CopyData data = null;
		for (CopyData copy : copyList) {
			if (copy.getInfoId() == infoId) {
				data = copy;
				break;
			}
		}
		return data;
	}

	public CopyData getByInfoId(int infoId) {
		return getByInfoWithId(infoId);
	}

	public int getCopyCount(String strLevelId) {
		CopyInfoCfg infoCfg = getCopyInfoCfgByLevelID(strLevelId);
		if (infoCfg == null)
			return 0;
		CopyData data = getByInfoWithId(infoCfg.getId());
		if (data == null)
			return 0;
		return data.getCopyCount();
	}

	// 扣次数
	public void subCopyCount(String strLevelId) {
		CopyInfoCfg infoCfg = getCopyInfoCfgByLevelID(strLevelId);
		if (infoCfg == null)
			return;
		CopyData data = getByInfoWithId(infoCfg.getId());
		if (data == null)
			return;
		if (data.getCopyCount() <= 0) {
			return;
		}

		data.setCopyCount(data.getCopyCount() - 1);
		// modify@2015-12-28 增加挑战时间
		data.setLastChallengeTime(System.currentTimeMillis());
		save();
	}

	// 重置次数
	public CopyDataIF resetCopyCount(int infoId, int copyType) {
		CopyData data = getByInfoWithId(infoId);
		if (data == null)
			return null;
		int count = getRestCountByCopyType(copyType);
		if (data.getResetCount() >= count) {
			// 重置次数达到上限
			return null;
		}
		CopyInfoCfg cfg = (CopyInfoCfg) CopyInfoCfgDAO.getInstance().getCfgById(String.valueOf(infoId));

		if (player.getUserGameDataMgr().addGold(-cfg.getCost()) < 0) {
			// 重置钱不够
			System.out.println("gold not enourgh...");
			return null;
		}

		data.setCopyCount(cfg.getCount());
		data.setResetCount(data.getResetCount() + 1);
		data.setLastChallengeTime(0);
		save();
		return data;
	}

	// 新的一天重置次数
	public void resetDataInNewDay() {
		List<CopyInfoCfgIF> cfgList = getSameDayInfoList();
		List<CopyData> copyList = getTableCopyData().getCopyList();
		for (CopyData data : copyList) {
			int copyType = data.getCopyType();
			for (CopyInfoCfgIF cfg : cfgList) {
				if (copyType == cfg.getType()) {
					data.setCopyCount(cfg.getCount());
					data.setResetCount(0);
					data.setLastFreeResetTime(System.currentTimeMillis());
					break;
				}
			}
		}
	}

	private ConcurrentHashMap<String, Integer> getCelestialDegreeMap() {
		ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
		List<CopyInfoCfg> listInfo = CopyInfoCfgDAO.getInstance().getAllCfg();
		int i;
		String[] degrees;
		for (CopyInfoCfg cfg : listInfo) {
			if (cfg.getType() != CopyType.COPY_TYPE_CELESTIAL)
				continue;
			degrees = cfg.getDegreeID().split(",");
			for (i = 0; i < degrees.length; i++) {
				map.put(degrees[i], 0);
			}
		}
		return map;
	}

	public CopyInfoCfg getCopyInfoCfgByLevelID(String levelId) {
		CopyLevelCfg levelCfg = getCopyLevelCfgByLevelID(levelId);
		if (levelCfg == null)
			return null;
		CopyInfoCfg infoCfg = getCopyInfoCfgByDegreeID(levelCfg.getDegreeID());
		if (infoCfg == null)
			return null;
		return infoCfg;
	}

	private CopyLevelCfg getCopyLevelCfgByLevelID(String levelId) {
		List<CopyLevelCfg> listCfg = CopyLevelCfgDAO.getInstance().getAllCfg();
		for (CopyLevelCfg cfg : listCfg) {
			if (cfg.getLevelID().equals(levelId)) {
				return cfg;
			}
		}
		return null;
	}

	private CopyInfoCfg getCopyInfoCfgByDegreeID(String degreeId) {
		List<CopyInfoCfg> listInfo = CopyInfoCfgDAO.getInstance().getAllCfg();
		int i;
		String[] degrees;
		for (CopyInfoCfg cfg : listInfo) {
			degrees = cfg.getDegreeID().split(",");
			for (i = 0; i < degrees.length; i++) {
				if (degreeId.equals(degrees[i])) {
					return cfg;
				}
			}
		}
		return null;
	}

	// 击杀奖励
	public List<? extends ItemInfoIF> addKillPrize(String levelId) {
		CopyLevelCfg levelCfg = getCopyLevelCfgByLevelID(levelId);
		if (levelCfg == null)
			return null;
		String[] arrPrizes = levelCfg.getKillPrize().split(",");
		String[] arrItem;
		List<ItemInfo> addList = new ArrayList<ItemInfo>(arrPrizes.length);
		ItemInfo item;
		int itemId, itemCount;
		for (int i = 0; i < arrPrizes.length; i++) {
			arrItem = arrPrizes[i].split("~");
			itemId = Integer.valueOf(arrItem[0]);
			itemCount = Integer.valueOf(arrItem[1]);
//			player.getItemBagMgr().addItem(itemId, itemCount);
			item = new ItemInfo();
			item.setItemID(itemId);
			item.setItemNum(itemCount);
			addList.add(item);
		}
		player.getItemBagMgr().addItem(addList);
		return addList;
	}

	public int getRestCountByCopyType(int copyType) {
		// EPrivilegeDef vipType;
		// int count;
		switch (copyType) {
		case CopyType.COPY_TYPE_TRIAL_LQSG:// 练气山谷
			// vipType = EPrivilegeDef.TRIAL2_COPY_RESET_TIMES;
			return player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.expResetCnt);
		case CopyType.COPY_TYPE_TRIAL_JBZD:// 聚宝之地
			// vipType = EPrivilegeDef.TRIAL1_COPY_RESET_TIMES;
			return player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.treasureResetCnt);
		case CopyType.COPY_TYPE_CELESTIAL:// 生存幻境
			// vipType = EPrivilegeDef.COPY_CELESTAL;
			return player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.survivalResetCnt);
		case CopyType.COPY_TYPE_WARFARE:// 无尽战火
			// vipType = EPrivilegeDef.WARFARE_COPY_RESET_TIMES;
			return player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.warfareResetCnt);
		case CopyType.COPY_TYPE_TOWER:// 万仙阵
			// vipType = EPrivilegeDef.TOWER_RESET_TIMES;
			return player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.arrayMaxResetCnt);
		case CopyType.COPY_TYPE_BATTLETOWER:// 封神台
			// vipType = EPrivilegeDef.BATTLE_TOWER_TIMES;
			return player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.maxResetCount);
		}

		return 0;
		// count = player.getVipMgr().GetMaxPrivilege(vipType);
		// return count;
	}

	private TableCopyData getTableCopyData() {
		return TableCopyDataDAO.getInstance().get(player.getUserId());
	}
}
