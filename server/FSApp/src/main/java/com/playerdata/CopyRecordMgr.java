package com.playerdata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.readonly.CopyCfgIF;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.playerdata.readonly.CopyRecordMgrIF;
import com.rw.service.copy.CommonTip;
import com.rw.service.copy.PvECommonHelper;
import com.rwbase.common.enu.eStoreConditionType;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.cfg.GiftCfg;
import com.rwbase.dao.copy.cfg.GiftCfgDAO;
import com.rwbase.dao.copy.cfg.MapCfg;
import com.rwbase.dao.copy.cfg.MapCfgDAO;
import com.rwbase.dao.copy.common.CopySubType;
import com.rwbase.dao.copy.pojo.CopyLevelRecord;
import com.rwbase.dao.copy.pojo.CopyMapRecord;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.gift.ComGiftCfg;
import com.rwbase.dao.gift.ComGiftCfgDAO;
import com.rwproto.CopyServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.PrivilegeProtos.CopyPrivilegeNames;

public class CopyRecordMgr implements CopyRecordMgrIF {

	private String userId;
	private CopyLevelRecordHolder copyLevelRecordHolder;
	private CopyMapRecordHolder copyMapRecordHolder;

	// // 战斗副本奖励,因为是战斗前获取的所以要做临时保存
	// private CopyRewards copyRewards;

	private Player m_pPlayer = null;

	/*
	 * 登陆时判断是否要初始化用户记录或者加载用户记录...
	 */
	public void init(Player pOwner) {

		m_pPlayer = pOwner;
		userId = pOwner.getUserId();

		copyLevelRecordHolder = new CopyLevelRecordHolder(userId);
		copyMapRecordHolder = new CopyMapRecordHolder(userId);

	}

	public void flush() {
		copyLevelRecordHolder.flush();
		copyMapRecordHolder.flush();
	}

	// public CopyRewardsIF getCopyRewards() {
	// return copyRewards;
	// }
	//
	// public void setCopyRewards(CopyRewards copyRewards) {
	// this.copyRewards = copyRewards;
	// }

	public boolean save() {
		// do nothing
		return true;
	}

	/*
	 * 重置关卡记录的次数
	 */
	public void resetAllCopyRecord() {

		copyLevelRecordHolder.resetAllCopyRecord(m_pPlayer);
		initMap();

	}

	public void initMap() {
		copyLevelRecordHolder.SynAll(m_pPlayer);
		copyMapRecordHolder.SynAll(m_pPlayer);
	}

	/*
	 * 获取当前用户的副本地图记录,以"0,0,0"记录下"是否领取1,是否领取2,是否领取3"，
	 */
	public List<String> getMapRecordList() {
		return copyMapRecordHolder.getMapRecordList();
	}

	/*
	 * 获取当前用户的副本关卡记录
	 */
	public List<CopyLevelRecord> getLevelRecordList() {
		return copyLevelRecordHolder.getLevelRecordList();
	}

	/**
	 * 添加关卡记录
	 * 
	 * @param levelID 关卡id
	 * @param nPassStar 关卡获得的星级
	 * @param times 战斗次数
	 * @return
	 */
	public String updateLevelRecord(int levelID, int nPassStar, int times) {
		return copyLevelRecordHolder.updateLevelRecord(m_pPlayer, levelID, nPassStar, times);
	}

	public String buyLevel(int levelID) {
		return copyLevelRecordHolder.buyLevel(m_pPlayer, levelID);
	}

	public boolean isCopyLevelPassed(int levelID) {
		return copyLevelRecordHolder.isCopyLevelPassed(m_pPlayer, levelID);
	}

	public CopyLevelRecordIF getLevelRecord(int levelID) {
		return copyLevelRecordHolder.getLevelRecord(m_pPlayer, levelID);
	}

	/*
	 * 将记录转换成string回传...
	 */

	private List<CopyCfgIF> GetAllCopyCfgByMap(MapCfg map) {
		List<CopyCfgIF> list = new ArrayList<CopyCfgIF>();
		int start = map.getStartLevelId();
		int end = map.getEndLevelId();
		CopyCfgDAO cfgDAO = CopyCfgDAO.getInstance();
		for (int i = start; i <= end; i++) {
			CopyCfg copyCfg = cfgDAO.getCfg(i);
			if (copyCfg != null) {
				list.add(copyCfg);
			}
		}
		return list;
	}

	/*
	 * GM命令
	 */
	public MsgCopyResponse.Builder setMapByGM(MapCfg mapCfg) {
		int nMapID = mapCfg.getId();
		MapCfgDAO mapCfgDAO = MapCfgDAO.getInstance();
		for (int i = 1001; i <= nMapID; i++) {
			MapCfg map = mapCfgDAO.getCfg(i);
			if (map != null) {
				List<CopyCfgIF> list = GetAllCopyCfgByMap(map);
				for (int j = 0, size = list.size(); j < size; j++) {
					updateLevelRecord(list.get(j).getLevelID(), 3, 1);
				}
			}
		}
		this.m_pPlayer.getStoreMgr().ProbStore(eStoreConditionType.WarCopy);
		MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder();
		copyResponse.setEResultType(EResultType.GM_SETSUCCESS);
		// copyResponse.addAllTagCopyLevelRecord(listResult);
		return copyResponse;
	}

	/**
	 * 判断某一章的是否通关
	 */
	public Boolean isMapClear(int mapID) {
		Boolean result = false;
		MapCfg map = MapCfgDAO.getInstance().getCfg(mapID);
		if (map != null) {
			CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(map.getEndLevelId());
			if (copyCfg != null) {
				CopyLevelRecordIF copyRecord = getLevelRecord(copyCfg.getLevelID());
				if (copyRecord != null && copyRecord.getPassStar() > 0)
					result = true;
			}
		}
		return result;
	}

	public Boolean isOpen(CopyCfg copyCfg) {
		boolean isOpen = false;
		if (copyCfg.getPreviousLevelID() == 0) {
			isOpen = true;
		} else {
			CopyLevelRecordIF copyRecord = getLevelRecord(copyCfg.getPreviousLevelID());
			if (copyRecord != null && copyRecord.getPassStar() > 0) {
				isOpen = true;
			}
		}
		return isOpen;
	}

	/**
	 * 
	 * @param mapID
	 * @param index 这里的index是从1开始(不要问我为什么 我也想吐槽)
	 * @return
	 */
	public String getGift(int mapID, int frontIndex) {
		String mapRecordInfo = "";
		if (frontIndex > 3 || frontIndex < 1) {
			m_pPlayer.NotifyCommonMsg("索引越界");
			return mapRecordInfo;
		}

		int giftIndex = frontIndex - 1;
		MapCfg mapCfg = MapCfgDAO.getInstance().getCfg(mapID);
		if (mapCfg == null) {
			m_pPlayer.NotifyCommonMsg("找不到mapID:" + mapID);
			return mapRecordInfo;
		}

		CopyMapRecord mapRecord = copyMapRecordHolder.getMapReord(mapID);
		if (mapRecord == null) {
			mapRecord = copyMapRecordHolder.addMapRecord(m_pPlayer, mapID);
		}

		if (isCanGet(mapRecord, giftIndex)) {
			copyMapRecordHolder.takeGift(m_pPlayer, mapRecord, giftIndex);
			mapRecord.takeGift(giftIndex);

			addGiftToItemBag(mapID, giftIndex);
			mapRecordInfo = mapRecord.toClientData();
		}

		return mapRecordInfo;
	}

	private void addGiftToItemBag(int mapID, int index) {
		MapCfg map = MapCfgDAO.getInstance().getCfg(mapID);
		List<ItemInfo> list = getGiftList(map, index);
		// for (int i = 0; i < list.size(); i++) {
		// ItemInfoIF itemInfo = list.get(i);
		// m_pPlayer.getItemBagMgr().addItem(itemInfo.getItemID(), itemInfo.getItemNum());
		// }
		ItemBagMgr.getInstance().addItem(m_pPlayer, list);
	}

	// private List<ItemInfoIF> getGiftList(MapCfg mapCfg, int index) {
	private List<ItemInfo> getGiftList(MapCfg mapCfg, int index) {
		String str = mapCfg.getRewardGain();
		String[] arr = str.split(",");
		str = arr[index];
		GiftCfg giftCfg = GiftCfgDAO.getInstance().getCfg(str);
		List<ItemInfo> list = departStringToItemInfoList(giftCfg.getReward());
		return list;
	}

	// private List<ItemInfoIF> departStringToItemInfoList(String rewards) {
	private List<ItemInfo> departStringToItemInfoList(String rewards) {
		List<ItemInfo> list = new ArrayList<ItemInfo>();
		String[] rewardArray = rewards.split(",");
		for (int i = 0; i < rewardArray.length; i++) {
			String strItem = rewardArray[i];
			String[] arrItem = strItem.split(":");
			ItemInfo itemInfo = new ItemInfo();
			itemInfo.setItemID(Integer.valueOf(arrItem[0]));
			itemInfo.setItemNum(Integer.valueOf(arrItem[1]));
			list.add(itemInfo);
		}
		return list;
	}

	private boolean isCanGet(CopyMapRecord mapRecord, int index) {
		if (!mapRecord.isGiftCanTake(index)) {
			m_pPlayer.NotifyCommonMsg("领过了");
			return false;// 领过了
		}
		int mapId = mapRecord.getMapId();
		MapCfg map = MapCfgDAO.getInstance().getCfg(mapId);
		String[] rewardStars = map.getRewardStar().split(",");
		List<CopyCfgIF> listCopyCfg = GetAllCopyCfgByMap(map);
		int star = 0;
		CopyLevelRecordIF copyRecord;
		for (CopyCfgIF copyCfg : listCopyCfg) {
			if (ShouldCountStar(copyCfg)) {
				copyRecord = getLevelRecord(copyCfg.getLevelID());
				if (copyRecord != null) {
					star += copyRecord.getPassStar();
				}
			}
		}
		return star >= Integer.parseInt(rewardStars[index]);
	}

	// ------------------------------------------------
	public int getMapCurrentStar(int mapID) {
		MapCfg map = MapCfgDAO.getInstance().getCfg(mapID);
		if (map == null) {
			GameLog.error("副本", "mapID:" + mapID, "获取副本地图配置失败", null);
			return 0;
		}
		List<CopyCfgIF> listCopyCfg = GetAllCopyCfgByMap(map);
		int star = 0;
		for (CopyCfgIF copyCfg : listCopyCfg) {
			if (ShouldCountStar(copyCfg)) {
				CopyLevelRecordIF copyRecord = getLevelRecord(copyCfg.getLevelID());
				if (copyRecord != null) {
					star += copyRecord.getPassStar();
				}
			}
		}
		return star;
	}

	private boolean ShouldCountStar(CopyCfgIF copyCfg) {
		if (copyCfg.getSubtype() == CopySubType.BOSS_BIG || copyCfg.getSubtype() == CopySubType.BOSS_SMALL) {
			return true;
		}
		return false;
	}

	public boolean IsCanSweep(CopyLevelRecordIF copyRecord, CopyCfg copyCfg, int times, ERequestType requestType) {
		if (IsLoop(copyCfg) == false) {
			m_pPlayer.NotifyCommonMsg(CommonTip.STAGE_NOT_SWEEP);
			return false;
		}
		if (copyRecord.getPassStar() < 3) {
			m_pPlayer.NotifyCommonMsg(CommonTip.STAGE_NOT_3_STAR_PASS);
			return false;
		}
		if (m_pPlayer.getUserGameDataMgr().getPower() < copyCfg.getSuccSubPower() * times) {
			m_pPlayer.NotifyCommonMsg(CommonTip.POWER_NOT_ENOUGH);
			return false;
		}
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		int ticketCount = itemBagMgr.getItemCountByModelId(userId, PvECommonHelper.SweepTicketID);
		if (requestType == ERequestType.SWEEP_LEVEL_DIAMOND) { // 钻石扫荡
			if (m_pPlayer.getUserGameDataMgr().getGold() < times - ticketCount) {
				m_pPlayer.NotifyCommonMsg(CommonTip.GOLD_NOT_ENOUGH);
				return false;
			}
		} else if (requestType == ERequestType.SWEEP_LEVEL_TICKET) { // 扫荡券扫荡
			if (ticketCount < times) {
				m_pPlayer.NotifyCommonMsg(CommonTip.TICKET_NOT_ENOUGH);
				return false;
			}
		}

		if (times > 1) {
			boolean isAllow = m_pPlayer.getPrivilegeMgr().getBoolPrivilege(CopyPrivilegeNames.isAllowTenSweep);
			if (!isAllow) {
				m_pPlayer.NotifyCommonMsg(CommonTip.VIP_NOT_ENOUGH);
				return false;
			}
			// PrivilegeCfg pPrivilege = PrivilegeCfgDAO.getInstance().getCfg(m_pPlayer.getVip());
			// if (pPrivilege.getCopyCount() <= 0) {
			// m_pPlayer.NotifyCommonMsg(CommonTip.VIP_NOT_ENOUGH);
			// return false;
			// }
		}

		return true;
	}

	private boolean IsLoop(CopyCfg copyCfg) {
		if (copyCfg.getSubtype() == CopySubType.ONCE) {
			return false;
		}
		return true;
	}

	/**
	 * 领取关卡宝箱
	 * 
	 * @param mapId
	 * @param copy
	 * @return
	 */
	public boolean getCopyBox(int mapId, int copy) {
		CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(copy);
		if (copyCfg == null) {
			m_pPlayer.NotifyCommonMsg("找不到目标关卡，id:" + copy);
			return false;
		}
		if (!isOpen(copyCfg)) {
			m_pPlayer.NotifyCommonMsg("目标关卡还没有通关");
			return false;
		}
		if (!StringUtils.isNotBlank(copyCfg.getRewardInfo())) {
			m_pPlayer.NotifyCommonMsg("目标关卡没有可领取的宝箱");
			return false;
		}
		CopyMapRecord mapReord = copyMapRecordHolder.getMapReord(mapId);
		if (mapReord == null) {
			mapReord = copyMapRecordHolder.addMapRecord(m_pPlayer, mapId);
		}
		List<Integer> rewardLvList = mapReord.getRewardLvList();
		if (rewardLvList.contains(copy)) {
			m_pPlayer.NotifyCommonMsg("目标关卡宝箱已经被领取");
			return false;
		}

		ComGiftCfg config = ComGiftCfgDAO.getInstance().getConfig(copyCfg.getRewardInfo());
		if (config == null) {
			m_pPlayer.NotifyCommonMsg("找不到奖励数据");
			return false;
		}
		List<ItemInfo> list = getItemListFromMap(config.getGiftMap());

		boolean take = ItemBagMgr.getInstance().addItem(m_pPlayer, list);
		if (take) {
			take = copyMapRecordHolder.takeCopyBox(m_pPlayer, mapReord, copy);

		}
		return take;

	}

	private List<ItemInfo> getItemListFromMap(Map<String, Integer> map) {
		List<ItemInfo> list = new ArrayList<ItemInfo>();
		for (Iterator<Entry<String, Integer>> itr = map.entrySet().iterator(); itr.hasNext();) {
			Entry<String, Integer> entry = itr.next();
			ItemInfo info = new ItemInfo();
			info.setItemID(Integer.parseInt(entry.getKey()));
			info.setItemNum(entry.getValue());
			list.add(info);
		}

		return list;
	}

	/**
	 * 领取天尊锦囊
	 * 
	 * @param mapID
	 * @return
	 */
	public boolean getGodGiftBox(int mapID) {
		MapCfg cfg = MapCfgDAO.getInstance().getCfg(mapID);
		if (cfg == null) {
			m_pPlayer.NotifyCommonMsg("找不到目标章节地图");
			return false;
		}
		if (!isMapClear(mapID)) {
			m_pPlayer.NotifyCommonMsg("当前章节还没有通关");
			return false;
		}
		if (cfg.getGodBoxID() == 0) {
			m_pPlayer.NotifyCommonMsg("当前章节没有奖励可以领取");
			return false;
		}
		CopyMapRecord mapReord = copyMapRecordHolder.getMapReord(mapID);
		if (mapReord == null) {
			mapReord = copyMapRecordHolder.addMapRecord(m_pPlayer, mapID);
		}
		if (mapReord.isReceiveChapterReward()) {
			m_pPlayer.NotifyCommonMsg("当前章节的奖励已经领取");
			return false;
		}
		ComGiftCfg config = ComGiftCfgDAO.getInstance().getConfig(String.valueOf(cfg.getGodBoxID()));
		if (config == null) {
			m_pPlayer.NotifyCommonMsg("找不到奖励数据");
			return false;
		}
		List<ItemInfo> list = getItemListFromMap(config.getGiftMap());

		boolean take = ItemBagMgr.getInstance().addItem(m_pPlayer, list);
		if (take) {
			take = copyMapRecordHolder.takeGodBox(m_pPlayer, mapReord, mapID);
		}
		return take;
	}

}
