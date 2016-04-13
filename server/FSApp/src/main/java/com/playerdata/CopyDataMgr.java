package com.playerdata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.readonly.CopyDataIF;
import com.playerdata.readonly.CopyDataMgrIF;
import com.playerdata.readonly.CopyInfoCfgIF;
import com.playerdata.readonly.ItemInfoIF;
import com.rwbase.common.enu.EPrivilegeDef;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.copypve.CopyInfoCfgDAO;
import com.rwbase.dao.copypve.CopyLevelCfgDAO;
import com.rwbase.dao.copypve.CopyType;
import com.rwbase.dao.copypve.TableCopyDataDAO;
import com.rwbase.dao.copypve.pojo.CopyData;
import com.rwbase.dao.copypve.pojo.CopyInfoCfg;
import com.rwbase.dao.copypve.pojo.CopyLevelCfg;
import com.rwbase.dao.copypve.pojo.TableCopyData;

public class CopyDataMgr implements CopyDataMgrIF {

	private TableCopyDataDAO tableCopyDataDAO = TableCopyDataDAO.getInstance();
	private TableCopyData pTableCopyData;
	private Player player;

	public void init(Player playerP) {
		player = playerP;
		pTableCopyData = tableCopyDataDAO.get(playerP.getUserId());
		List<CopyInfoCfg> cfgList = CopyInfoCfgDAO.getInstance().getAllCfg();
		//角色第一次初始化
		if (pTableCopyData == null) 
		{
			pTableCopyData = new TableCopyData();
			pTableCopyData.setUserId(playerP.getUserId());
			List<CopyData> copyList = new ArrayList<CopyData>();
			for (CopyInfoCfgIF cfg : cfgList)
			{
				CopyData data = new CopyData();
				data.setCopyCount(cfg.getCount());
//				data.setResetCount(getRestCountByCopyType(cfg.getType()));
				data.setCopyType(cfg.getType());
				data.setInfoId(cfg.getId());
				data.setPassMap(getCelestialDegreeMap());
				copyList.add(data);
			}
			pTableCopyData.setCopyList(copyList);
		} 
		else
		{
			List<CopyData> copyList = pTableCopyData.getCopyList();
			for (CopyInfoCfgIF cfg : cfgList)
			{
				//调整bAdd位置
				boolean bAdd = true;
				for (CopyData data : copyList)
				{
					if (data.getInfoId() == cfg.getId()) 
					{
						bAdd = false;
						break;
					}
				}
				if (bAdd)
				{
					CopyData data = new CopyData();
					data.setCopyCount(cfg.getCount());
//					data.setResetCount(getRestCountByCopyType(cfg.getType()));
					data.setCopyType(cfg.getType());
					data.setInfoId(cfg.getId());
					data.setPassMap(getCelestialDegreeMap());
					copyList.add(data);
				}
			}
		}
		this.save();
	}

	public boolean save() {
		if (tableCopyDataDAO == null)
			return false;
		return tableCopyDataDAO.update(pTableCopyData);
	}

	public List<CopyInfoCfgIF> getTodayInfoCfg(int copyType) {
		List<CopyInfoCfgIF> list = new ArrayList<CopyInfoCfgIF>();
		int day = getDayOfWeek();
		List<CopyInfoCfg> listInfo = CopyInfoCfgDAO.getInstance().getAllCfg();
		String[] time;
		int i;
		for (CopyInfoCfg cfg : listInfo) {
			if (cfg.getType() != copyType)
				continue;
			time = cfg.getTime().split(",");
			for (i = 0; i < time.length; i++) {
				if (day == Integer.valueOf(time[i])) {
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
			if (cfg.getType() != copyType)
				continue;
			list.add(cfg);
		}
		return list;
	}

	// 获取同一天的玩法
	public static List<CopyInfoCfgIF> getSameDayInfoList() {
		List<CopyInfoCfgIF> list = new ArrayList<CopyInfoCfgIF>();
		int day = getDayOfWeek();
		List<CopyInfoCfg> listInfo = CopyInfoCfgDAO.getInstance().getAllCfg();
		for (CopyInfoCfg cfg : listInfo) {
			String[] time = cfg.getTime().split(",");
			for (int i = 0; i < time.length; i++) {
				if (day == Integer.valueOf(time[i])) {
					list.add(cfg);
				}
			}
		}
		return list;
	}

	public static int getDayOfWeek() {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		if (day <= 1) {
			day = 7;
		} else {
			day = day - 1;
		}
		// System.out.println("day of week :"+day);
		return day;
	}

	/*
	 * public CopyData getByTrialType(int copyType) { List<CopyData> copyList =
	 * pTableCopyData.getCopyList(); CopyData data = null; for(CopyData copy :
	 * copyList){ if(copy.getCopyType() == copyType){ data = copy; break; } }
	 * return data; }
	 */

	private CopyData getByInfoWithId(int infoId) {
		List<CopyData> copyList = pTableCopyData.getCopyList();
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
		//modify@2015-12-28 增加挑战时间
		data.setLastChallengeTime(System.currentTimeMillis());
		save();
	}

	// 重置次数
	public CopyDataIF resetCopyCount(int infoId,int copyType)
	{
		CopyData data = getByInfoWithId(infoId);
		if (data == null)
			return null;
		int count = getRestCountByCopyType(copyType);
		if (data.getResetCount() >= count)
		{
			//重置次数达到上限
			return null;
		}
		CopyInfoCfg cfg = (CopyInfoCfg) CopyInfoCfgDAO.getInstance().getCfgById(String.valueOf(infoId));
		
		if (player.getUserGameDataMgr().addGold(-cfg.getCost()) < 0)
		{
			//重置钱不够
			System.out.println("gold not enourgh...");
			return null;
		}
		
		data.setCopyCount(cfg.getCount());
		data.setResetCount(data.getResetCount() + 1);
		save();
		return data;
	}

	// 新的一天重置次数
	public void resetDataInNewDay() {
		if (pTableCopyData == null) {
			return;
		}
		List<CopyInfoCfgIF> cfgList = getSameDayInfoList();
		List<CopyData> copyList = pTableCopyData.getCopyList();
		for (CopyData data : copyList)
		{
			for (CopyInfoCfgIF cfg : cfgList)
			{
				if (data.getCopyType() == cfg.getType()) 
				{
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

	// 首次通关奖励
	public List<ItemInfoIF> checkFirstPrize(int copyType, String levelId) {
		CopyLevelCfg levelCfg = getCopyLevelCfgByLevelID(levelId);
		if (levelCfg == null)
			return null;
		CopyInfoCfg infoCfg = getCopyInfoCfgByDegreeID(levelCfg.getDegreeID());
		if (infoCfg == null)
			return null;
		CopyData data = getByInfoWithId(infoCfg.getId());
		if (data == null)
			return null;
		Enumeration<String> keys = data.getPassMapKeysEnumeration();
		if (keys == null) {
			data.setPassMap(getCelestialDegreeMap());
		}
		int passValue = data.getPassMap(levelCfg.getDegreeID());
		if (passValue == 0) {
			String[] arrPrizes = levelCfg.getFirstPrize().split(",");
			String[] arrItem;
			List<ItemInfoIF> addList = new ArrayList<ItemInfoIF>();
			ItemInfo item;
			int itemId, itemCount;
			for (int i = 0; i < arrPrizes.length; i++) {
				arrItem = arrPrizes[i].split("~");
				itemId = Integer.valueOf(arrItem[0]);
				itemCount = Integer.valueOf(arrItem[1]);
				player.getItemBagMgr().addItem(itemId, itemCount);
				item = new ItemInfo();
				item.setItemID(itemId);
				item.setItemNum(itemCount);
				addList.add(item);
			}
			data.addPassMap(levelCfg.getDegreeID(), 1);
			return addList;
		}
		return null;
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
	public List<ItemInfoIF> addKillPrize(String levelId) {
		CopyLevelCfg levelCfg = getCopyLevelCfgByLevelID(levelId);
		if (levelCfg == null)
			return null;
		String[] arrPrizes = levelCfg.getKillPrize().split(",");
		String[] arrItem;
		List<ItemInfoIF> addList = new ArrayList<ItemInfoIF>();
		ItemInfo item;
		int itemId, itemCount;
		for (int i = 0; i < arrPrizes.length; i++) {
			arrItem = arrPrizes[i].split("~");
			itemId = Integer.valueOf(arrItem[0]);
			itemCount = Integer.valueOf(arrItem[1]);
			player.getItemBagMgr().addItem(itemId, itemCount);
			item = new ItemInfo();
			item.setItemID(itemId);
			item.setItemNum(itemCount);
			addList.add(item);
		}
		return addList;
	}
	
	private int getRestCountByCopyType(int copyType)
	{
		EPrivilegeDef vipType;
		int count;
		switch (copyType) 
		{
		case CopyType.COPY_TYPE_TRIAL_LQSG://练气山谷
			vipType = EPrivilegeDef.TRIAL2_COPY_RESET_TIMES;
			break;
		case CopyType.COPY_TYPE_TRIAL_JBZD://聚宝之地
			vipType = EPrivilegeDef.TRIAL1_COPY_RESET_TIMES;
			break;
		case CopyType.COPY_TYPE_CELESTIAL://生存幻境
			vipType = EPrivilegeDef.COPY_CELESTAL;
			break;
		case CopyType.COPY_TYPE_WARFARE://无尽战火
			vipType = EPrivilegeDef.WARFARE_COPY_RESET_TIMES;
			break;
		case CopyType.COPY_TYPE_TOWER://万仙阵
			vipType = EPrivilegeDef.TOWER_RESET_TIMES;
			break;
		case CopyType.COPY_TYPE_BATTLETOWER://封神台
			vipType = EPrivilegeDef.BATTLE_TOWER_TIMES;
			break;
		default:
			vipType = EPrivilegeDef.TRIAL2_COPY_RESET_TIMES;
			break;
		}
		
		count = player.getVipMgr().GetMaxPrivilege(vipType);
		return count;
	}
}
