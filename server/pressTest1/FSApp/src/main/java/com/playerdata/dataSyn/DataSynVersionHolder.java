package com.playerdata.dataSyn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.Player;
import com.rwbase.common.PlayerDataMgr;
import com.rwbase.common.RecordSynchronization;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.ReConnectionProtos.SyncVersion;

public class DataSynVersionHolder {

	// holder控制的数据同步
	private Map<eSynType, PlayerDataMgr> versionMap = new ConcurrentHashMap<eSynType, PlayerDataMgr>();

	// 发送的顺序
	private List<eSynType> orderList = new ArrayList<eSynType>();
	// 没在holder里的 数据同步版本控制
	private List<PlayerDataMgr> notInVersionControlList = new ArrayList<PlayerDataMgr>();

	public int addVersion(eSynType synType) {

		eSynType versionType = getVersionType(synType);
		PlayerDataMgr playerDataMgr = versionMap.get(versionType);
		if (playerDataMgr != null) {
			return playerDataMgr.versionIncr();
		}
		return -1;
	}

	public int getVersion(eSynType synType) {
		int version = -1;
		eSynType versionType = getVersionType(synType);
		PlayerDataMgr playerDataMgr = versionMap.get(versionType);
		if (playerDataMgr != null) {
			version = playerDataMgr.getVersion();
		}
		return version;
	}

	private eSynType getVersionType(eSynType synType) {
		eSynType versionType = synType;
		if (synType == eSynType.COPY_LEVEL_RECORD || synType == eSynType.COPY_MAP_RECORD) {
			versionType = eSynType.VERSION_COPY;
		} else if (synType == eSynType.USER_HEROS || synType == eSynType.ROLE_BASE_ITEM || synType == eSynType.SKILL_ITEM
				|| synType == eSynType.EQUIP_ITEM || synType == eSynType.INLAY_ITEM || synType == eSynType.ROLE_ATTR_ITEM) {

			versionType = eSynType.USER_HEROS;
		}
		return versionType;
	}

	public void synByVersion(Player player, List<SyncVersion> versionList) {
		Map<eSynType, Integer> clientVersionMap = new HashMap<eSynType, Integer>();
		for (SyncVersion dataSynVersionTmp : versionList) {
			clientVersionMap.put(dataSynVersionTmp.getType(), dataSynVersionTmp.getVersion());
		}

		for (eSynType synTypeTmp : orderList) {

			PlayerDataMgr playerDataMgr = versionMap.get(synTypeTmp);
			Integer synVersionTmp = clientVersionMap.get(synTypeTmp);
			int clientVersion = -1;
			if (synVersionTmp != null) {
				clientVersion = synVersionTmp;
			}
			playerDataMgr.syn(player, clientVersion);
		}

		synNotInVersion(player);
	}

	public void synAll(Player player) {

		for (eSynType synTypeTmp : orderList) {
			PlayerDataMgr playerDataMgr = versionMap.get(synTypeTmp);
			playerDataMgr.syn(player, -1);
		}

		synNotInVersion(player);
	}

	private void synNotInVersion(Player player) {
		for (PlayerDataMgr playerDataMgr : notInVersionControlList) {
			playerDataMgr.syn(player, -1);
		}
	}

	private boolean isInit = false;

	public boolean isInit() {
		return isInit;
	}

	public void init(Player player, PlayerDataMgr notInVersionControlP) {
		if (isInit) {
			return;
		}
		isInit = true;
		// 发送的顺序
		orderList = new ArrayList<eSynType>();
		// 没在holder里的 数据同步版本控制
		notInVersionControlList = new ArrayList<PlayerDataMgr>();

		versionMap.put(eSynType.USER_DATA, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				player.getUserDataMgr().syn(version);

			}
		}));
		orderList.add(eSynType.USER_DATA);

		versionMap.put(eSynType.USER_GAME_DATA, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				player.getUserGameDataMgr().syn(version);

			}
		}));
		orderList.add(eSynType.USER_GAME_DATA);

		versionMap.put(eSynType.USER_HEROS, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				player.getHeroMgr().synAllHeroToClient(version);
			}
		}));
		orderList.add(eSynType.USER_HEROS);

		// todo
		versionMap.put(eSynType.VERSION_COPY, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				player.getCopyRecordMgr().initMap();
			}
		}));
		orderList.add(eSynType.VERSION_COPY);

		versionMap.put(eSynType.USER_ITEM_BAG, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				player.getItemBagMgr().syncAllItemData();
			}
		}));
		orderList.add(eSynType.USER_ITEM_BAG);

		versionMap.put(eSynType.FASHION_ITEM, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				player.getFashionMgr().syncAll();
			}
		}));
		orderList.add(eSynType.FASHION_ITEM);

		versionMap.put(eSynType.USER_MAGIC, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				player.getMagicMgr().syncAllMagicData(version);
			}
		}));
		orderList.add(eSynType.USER_MAGIC);

		versionMap.put(eSynType.Store_Data, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				player.getStoreMgr().syncAllStore();
			}
		}));
		orderList.add(eSynType.Store_Data);

		versionMap.put(eSynType.TASK_DATA, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				player.getTaskMgr().synData(version);
			}
		}));
		orderList.add(eSynType.TASK_DATA);

		versionMap.put(eSynType.VIP_DATA, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				player.getVipMgr().syn(version);
			}
		}));
		orderList.add(eSynType.VIP_DATA);

//		versionMap.put(eSynType.FRESHER_ATIVITY_DATA, new PlayerDataMgr(new RecordSynchronization() {
//			@Override
//			public void synAllData(Player player, int version) {
//				player.getFresherActivityMgr().syn(version);
//			}
//		}));
		//orderList.add(eSynType.FRESHER_ATIVITY_DATA);

		notInVersionControlList.add(notInVersionControlP);
	}
}