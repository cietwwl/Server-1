package com.playerdata.dataSyn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bm.randomBoss.RandomBossMgr;
import com.bm.targetSell.TargetSellManager;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
import com.playerdata.activity.retrieve.ActivityRetrieveTypeMgr;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeMgr;
import com.playerdata.activityCommon.ActivityMgrHelper;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.fightinggrowth.FSuserFightingGrowthMgr;
import com.playerdata.groupFightOnline.data.UserGFightOnlineHolder;
import com.playerdata.groupcompetition.quiz.GCompUserQuizItemHolder;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
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
		} else if (synType == eSynType.USER_HEROS || synType == eSynType.ROLE_BASE_ITEM || synType == eSynType.SKILL_ITEM || synType == eSynType.EQUIP_ITEM || synType == eSynType.FIX_EXP_EQUIP || synType == eSynType.FIX_NORM_EQUIP || synType == eSynType.INLAY_ITEM
				|| synType == eSynType.ROLE_ATTR_ITEM) {

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
			if(null != playerDataMgr){
				playerDataMgr.syn(player, clientVersion);
			}
		}
		synNotInVersion(player);
	}

	public void synByClientVersion(Player player, eSynType synTypeTmp, int clientVerion) {

		PlayerDataMgr playerDataMgr = versionMap.get(synTypeTmp);

		playerDataMgr.syn(player, clientVerion);

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
		player.getUserTmpGameDataFlag().setSynAll(true);
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
				// player.getHeroMgr().synAllHeroToClient(version);
				player.getHeroMgr().synAllHeroToClient(player, version);
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
				ItemBagMgr.getInstance().syncAllItemData(player);
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

		versionMap.put(eSynType.FRESHER_ATIVITY_DATA, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				player.getFresherActivityMgr().syn(version);
			}
		}));
		orderList.add(eSynType.FRESHER_ATIVITY_DATA);

		versionMap.put(eSynType.Charge, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				ChargeMgr.getInstance().syn(player, version);
			}
		}));
		orderList.add(eSynType.Charge);

		versionMap.put(eSynType.ActivityTimeCardType, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				ActivityTimeCardTypeMgr.getInstance().synCountTypeData(player);
			}
		}));
		orderList.add(eSynType.ActivityTimeCardType);

		versionMap.put(eSynType.ActivityRateType, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				ActivityRateTypeMgr.getInstance().synData(player);
			}
		}));
		orderList.add(eSynType.ActivityRateType);

		versionMap.put(eSynType.ActivityTimeCountType, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				ActivityTimeCountTypeMgr.getInstance().synTimeCountTypeData(player);
			}
		}));
		orderList.add(eSynType.ActivityTimeCountType);

		versionMap.put(eSynType.ActivityExchangeType, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				ActivityExchangeTypeMgr.getInstance().synCountTypeData(player);
			}
		}));
		orderList.add(eSynType.ActivityExchangeType);

		versionMap.put(eSynType.ActivityDailyRechargeType, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				ActivityMgrHelper.getInstance().synActivityData(player, null);
			}
		}));
		orderList.add(eSynType.ActivityDailyRechargeType);

		versionMap.put(eSynType.MagicSecretData, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				MagicSecretMgr.getInstance().synUserMSData(player);
			}
		}));
		orderList.add(eSynType.MagicSecretData);

		versionMap.put(eSynType.MagicChapterData, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				MagicSecretMgr.getInstance().synMagicChapterData(player);
			}
		}));
		orderList.add(eSynType.MagicChapterData);

		versionMap.put(eSynType.GCompSelfGuess, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				GCompUserQuizItemHolder.getInstance().synAllData(player);
			}
		}));
		orderList.add(eSynType.GCompSelfGuess);


		versionMap.put(eSynType.ActivityRedEnvelopeType, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				ActivityRedEnvelopeTypeMgr.getInstance().synRedEnvelopeTypeData(player);
			}
		}));
		orderList.add(eSynType.ActivityRedEnvelopeType);

		versionMap.put(eSynType.ActivityLimitHeroType, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				ActivityLimitHeroTypeMgr.getInstance().synCountTypeData(player);
			}
		}));
		orderList.add(eSynType.ActivityLimitHeroType);

		versionMap.put(eSynType.ActivityRetrieveType, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				ActivityRetrieveTypeMgr.getInstance().synCountTypeData(player);
			}
		}));
		orderList.add(eSynType.ActivityRetrieveType);

		versionMap.put(eSynType.GFightOnlinePersonalData, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				UserGFightOnlineHolder.getInstance().synData(player);
			}
		}));
		orderList.add(eSynType.GFightOnlinePersonalData);

		versionMap.put(eSynType.MAGICEQUIP_FETTER, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				player.getMe_FetterMgr().loginNotify(player);
			}
		}));
		orderList.add(eSynType.MAGICEQUIP_FETTER);

		versionMap.put(eSynType.RANDOM_BOSS_DATA, new PlayerDataMgr(new RecordSynchronization() {

			@Override
			public void synAllData(Player player, int version) {
				RandomBossMgr.getInstance().checkAndSynRandomBossData(player);
			}
		}));
		orderList.add(eSynType.RANDOM_BOSS_DATA);

		versionMap.put(eSynType.BENEFIT_SELL_DATA, new PlayerDataMgr(new RecordSynchronization() {

			@Override
			public void synAllData(Player player, int version) {
				TargetSellManager.getInstance().checkBenefitScoreAndSynData(player);
			}
		}));
		orderList.add(eSynType.BENEFIT_SELL_DATA);

		//
		// versionMap.put(eSynType.GFDefendArmyData, new PlayerDataMgr(new RecordSynchronization() {
		// @Override
		// public void synAllData(Player player, int version) {
		// GFDefendArmyItemHolder.getInstance().synSelfData(player);
		// }
		// }));
		// orderList.add(eSynType.GFDefendArmyData);
		//
		// versionMap.put(eSynType.GFBiddingData, new PlayerDataMgr(new RecordSynchronization() {
		// @Override
		// public void synAllData(Player player, int version) {
		// GFBiddingItemHolder.getInstance().synAllData(player);
		// }
		// }));
		// orderList.add(eSynType.GFBiddingData);
		//
		// versionMap.put(eSynType.GFightOnlineResourceData, new PlayerDataMgr(new RecordSynchronization() {
		// @Override
		// public void synAllData(Player player, int version) {
		// GFightOnlineResourceHolder.getInstance().synData(player);
		// }
		// }));
		// orderList.add(eSynType.GFightOnlineResourceData);

		//
		// versionMap.put(eSynType.GFDefendArmyData, new PlayerDataMgr(new RecordSynchronization() {
		// @Override
		// public void synAllData(Player player, int version) {
		// GFDefendArmyItemHolder.getInstance().synSelfData(player);
		// }
		// }));
		// orderList.add(eSynType.GFDefendArmyData);
		//
		// versionMap.put(eSynType.GFBiddingData, new PlayerDataMgr(new RecordSynchronization() {
		// @Override
		// public void synAllData(Player player, int version) {
		// GFBiddingItemHolder.getInstance().synAllData(player);
		// }
		// }));
		// orderList.add(eSynType.GFBiddingData);
		//
		// versionMap.put(eSynType.GFightOnlineResourceData, new PlayerDataMgr(new RecordSynchronization() {
		// @Override
		// public void synAllData(Player player, int version) {
		// GFightOnlineResourceHolder.getInstance().synData(player);
		// }
		// }));
		// orderList.add(eSynType.GFightOnlineResourceData);

		notInVersionControlList.add(notInVersionControlP);

		versionMap.put(eSynType.QuestionList, new PlayerDataMgr(new RecordSynchronization() {

			@Override
			public void synAllData(Player player, int version) {
				// TODO Auto-generated method stub
				// player.getPlayerQuestionMgr().sync(version);
			}
		}));
		orderList.add(eSynType.QuestionList);

		versionMap.put(eSynType.EmbattleInfo, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				EmbattleInfoMgr.getMgr().syn(player);
			}
		}));
		orderList.add(eSynType.EmbattleInfo);

		versionMap.put(eSynType.FIGHTING_GROWTH_DATA, new PlayerDataMgr(new RecordSynchronization() {
			@Override
			public void synAllData(Player player, int version) {
				FSuserFightingGrowthMgr.getInstance().synFightingTitleData(player);
			}
		}));
		orderList.add(eSynType.FIGHTING_GROWTH_DATA);

		// versionMap.put(eSynType.CommonSoulConfig, new PlayerDataMgr(new RecordSynchronization() {
		// @Override
		// public void synAllData(Player player, int version) {
		// CommonSoulConfigHolder.getInstance().synConfig(player);
		// }
		// }));
		// orderList.add(eSynType.CommonSoulConfig);
	}
}