package com.rw.service.gm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.bm.chat.ChatInteractiveType;
import com.bm.group.GroupBM;
import com.bm.group.GroupBaseDataMgr;
import com.bm.group.GroupMemberMgr;
import com.bm.randomBoss.RandomBossMgr;
import com.bm.rank.RankType;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.bm.rank.groupsecretmatch.GroupSecretMatchRankAttribute;
import com.bm.rank.groupsecretmatch.GroupSecretMatchRankComparable;
import com.bm.serverStatus.ServerStatusMgr;
import com.bm.worldBoss.WBMgr;
import com.common.HPCUtil;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.BattleTowerMgr;
import com.playerdata.CopyDataMgr;
import com.playerdata.FashionMgr;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.TowerMgr;
import com.playerdata.activityCommon.modifiedActivity.ActivityModifyMgr;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.groupFightOnline.state.GFightStateTransfer;
import com.playerdata.groupsecret.UserCreateGroupSecretDataMgr;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.readonly.CopyInfoCfgIF;
import com.rw.fsutil.cacheDao.CfgCsvReloader;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.manager.ServerSwitch;
import com.rw.netty.UserChannelMgr;
import com.rw.service.Email.EmailUtils;
import com.rw.service.PeakArena.PeakArenaBM;
import com.rw.service.PeakArena.datamodel.peakArenaBuyCostHelper;
import com.rw.service.PeakArena.datamodel.peakArenaInfoHelper;
import com.rw.service.PeakArena.datamodel.peakArenaMatchRuleHelper;
import com.rw.service.PeakArena.datamodel.peakArenaPrizeHelper;
import com.rw.service.PeakArena.datamodel.peakArenaResetCostHelper;
import com.rw.service.Privilege.datamodel.PrivilegeConfigHelper;
import com.rw.service.TaoistMagic.ITaoistMgr;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfg;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rw.service.chat.ChatHandler;
import com.rw.service.gamble.datamodel.GambleDropCfgHelper;
import com.rw.service.gamble.datamodel.GamblePlanCfgHelper;
import com.rw.service.gamble.datamodel.HotGambleCfgHelper;
import com.rw.service.gm.copy.GMCopyProcesser;
import com.rw.service.gm.fixequip.GMAddFixEquip;
import com.rw.service.gm.groupcomp.GCGMHandler;
import com.rw.service.gm.hero.GMHeroBase;
import com.rw.service.gm.hero.GMHeroProcesser;
import com.rw.service.group.helper.GroupHelper;
import com.rw.service.group.helper.GroupRankHelper;
import com.rw.service.guide.DebugNewGuideData;
import com.rw.service.guide.datamodel.GiveItemCfgDAO;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.common.enu.eStoreConditionType;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.angelarray.AngelArrayConst;
import com.rwbase.dao.angelarray.pojo.db.TableAngelArrayData;
import com.rwbase.dao.angelarray.pojo.db.dao.AngelArrayDataDao;
import com.rwbase.dao.battletower.pojo.db.TableBattleTower;
import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerDao;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.copypve.CopyType;
import com.rwbase.dao.copypve.TableCopyDataDAO;
import com.rwbase.dao.copypve.pojo.CopyData;
import com.rwbase.dao.copypve.pojo.TableCopyData;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.fashion.FashionBuyRenewCfgDao;
import com.rwbase.dao.fashion.FashionCommonCfgDao;
import com.rwbase.dao.fashion.FashionEffectCfgDao;
import com.rwbase.dao.fashion.FashionQuantityEffectCfgDao;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.db.GroupBaseData;
import com.rwbase.dao.group.pojo.db.UserGroupAttributeData;
import com.rwbase.dao.group.pojo.db.dao.UserGroupAttributeDataDAO;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceCfg;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretResourceCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.item.pojo.itembase.INewItem;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.NewItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.setting.HeadBoxCfgDAO;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.ChatServiceProtos.ChatMessageData;
import com.rwproto.ChatServiceProtos.MsgChatRequest;
import com.rwproto.ChatServiceProtos.eChatType;
import com.rwproto.GMServiceProtos.MsgGMRequest;
import com.rwproto.GMServiceProtos.MsgGMResponse;
import com.rwproto.GMServiceProtos.eGMResultType;
import com.rwproto.GroupCompetitionProto.GCRequestType;
import com.rwproto.GuidanceProgressProtos.GuidanceConfigs;
import com.rwproto.ItemBagProtos.EItemTypeDef;
import com.rwproto.MainMsgProtos.EMsgType;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RequestProtos.RequestBody;
import com.rwproto.RequestProtos.RequestHeader;

public class GMHandler {
	private HashMap<String, String> funcCallBackMap = new HashMap<String, String>();
	private static GMHandler instance = new GMHandler();
	// 是否激活gm指令
	private boolean active = false;

	protected GMHandler() {
		initMap();
	};

	public static GMHandler getInstance() {
		return instance;
	}

	private void initMap() {

		funcCallBackMap.put("additem", "addItem");
		funcCallBackMap.put("addpower", "addPower");
		funcCallBackMap.put("addcoin", "addCoin");
		funcCallBackMap.put("addgold", "addGold");
		funcCallBackMap.put("addhero", "addHero");
		funcCallBackMap.put("addfixequipitem", "addFixEquipItem");
		funcCallBackMap.put("clrbag", "clrBag");
		funcCallBackMap.put("setlevel", "setLevel");
		funcCallBackMap.put("ranksort", "rankSort");
		funcCallBackMap.put("addexp", "addExp");
		funcCallBackMap.put("sendemail", "sendEmail");
		funcCallBackMap.put("addheroexp", "addHeroExp");
		funcCallBackMap.put("setattack", "setAttack");
		funcCallBackMap.put("addattack", "addAttack");
		funcCallBackMap.put("setmap", "setMap");
		funcCallBackMap.put("commonmsg", "commonMsg");// --test
		funcCallBackMap.put("setvip", "setVip");
		funcCallBackMap.put("setskillpointcount", "setskillpointcount");
		funcCallBackMap.put("recharge", "recharge");
		funcCallBackMap.put("reloadarena", "reloadArena");// --test
		funcCallBackMap.put("servertime", "serverTime");
		funcCallBackMap.put("createarena", "createArena");// --test

		funcCallBackMap.put("addguildnum", "addguildNum");// --test
		funcCallBackMap.put("sendtestemail", "sendTestEmail");// --test
		funcCallBackMap.put("addtower", "addTowerNum");// --test
		funcCallBackMap.put("setwjzh", "setWjzh");//
		funcCallBackMap.put("resetwjzh", "resetWjzh");//
		funcCallBackMap.put("probstore", "probstore");
		funcCallBackMap.put("sendpmd", "sendPmd");//
		funcCallBackMap.put("addarenacoin", "addArenaCoin");
		funcCallBackMap.put("getallsecret", "getAllSecret");
		funcCallBackMap.put("teambringit", "teamBringit");
		funcCallBackMap.put("teambringitsigle", "teamBringitSigle");
		funcCallBackMap.put("addhero", "addHero1");
		funcCallBackMap.put("setteam1", "setTeam1");
		funcCallBackMap.put("setteam2", "setTeam2");
		funcCallBackMap.put("gainheroequip", "gainHeroEquip");
		funcCallBackMap.put("wearequip", "wearEquip");
		funcCallBackMap.put("reset", "resetTimes");

		// 重新加载某些配置文件
		funcCallBackMap.put("reloadconfig", "reloadConfig");

		// 引导
		funcCallBackMap.put("updatenewguideconfig", "UpdateNewGuideConfig");
		funcCallBackMap.put("readnewguideconfig", "ReadNewGuideConfig");
		funcCallBackMap.put("reloadnewguidecfg", "ReloadNewGuideCfg");

		// 帮派作弊
		funcCallBackMap.put("group", "groupChange");

		// 获取vip道具，调试用
		funcCallBackMap.put("getgift", "getgift");

		// 时装
		funcCallBackMap.put("setfashionexpiredtime", "setFashionExpiredTime");
		funcCallBackMap.put("setfashion", "setFashion");
		funcCallBackMap.put("reloadfashionconfig", "reloadFashionConfig");
		funcCallBackMap.put("reloadunlockfashionicon", "reloadUnlockFashionIconCfg");

		// 钓鱼台配置更新并重新生成热点数据
		funcCallBackMap.put("reloadgambleconfig", "reloadGambleConfig");

		// 特权系统重新加载所有特权相关配置
		funcCallBackMap.put("reloadprivilegeconfig", "reloadPrivilegeConfig");

		// 设置充值开关,0开启1关闭
		funcCallBackMap.put("setchargeon", "setChargeOn");

		// 巅峰竞技场，重新加载配置
		funcCallBackMap.put("reloadpeakarenaconfig", "reloadPeakArenaConfig");
		funcCallBackMap.put("resetpeakarenachallenge", "resetPeakArenaChallenge");

		// 封神台，设置当前层数
		funcCallBackMap.put("setbattletowerfloor", "setBattleTowerFloor");
		funcCallBackMap.put("endbtsweep", "endBTsweep");
		funcCallBackMap.put("btreset", "clearBattleTowerResetTimes");
		funcCallBackMap.put("setbtkey", "setBattleTowerKey");
		funcCallBackMap.put("setbtlefttime", "setBattleTowerLeftTime");

		// 道术
		funcCallBackMap.put("setalltaoist", "setAllTaoist");
		funcCallBackMap.put("resetTaoistLevelByTag".toLowerCase(), "resetTaoistLevelByTag");
		funcCallBackMap.put("resetTaoistLevelById".toLowerCase(), "resetTaoistLevelById");

		// 设置帮战阶段
		funcCallBackMap.put("setgfstate", "setGFightState");
		funcCallBackMap.put("setgfauto", "setGFightAutoState");

		// 添加帮派物资
		funcCallBackMap.put("setgp", "SetGroupSupplier");
		// 添加帮派副本战斗次数 * setgbf 1000
		funcCallBackMap.put("setgbf", "setGroupBossFightTime");

		// 聊天消息测试
		funcCallBackMap.put("getprivatechatlist", "getPrivateChatList");
		funcCallBackMap.put("sendinteractivedata", "sendInteractiveData");
		funcCallBackMap.put("receiveinteractivedata", "receiveInteractiveData");

		funcCallBackMap.put("addwakenpiece", "addWakenPiece");
		funcCallBackMap.put("addwakenkey", "addWakenKey");

		funcCallBackMap.put("shutdown", "shutdownServer");

		funcCallBackMap.put("addserverstatustips", "addServerStatusTips");
		funcCallBackMap.put("addsecretkeycount", "addSecretKeycount");

		funcCallBackMap.put("adddist", "addDistCount");

		funcCallBackMap.put("speedUpSecret".toLowerCase(), "speedUpSecret");
		funcCallBackMap.put("finishsecret", "finishSecret");

		funcCallBackMap.put("requestfightinggrowthdata", "requestFightingGrowthData");
		funcCallBackMap.put("requestfightinggrowthupgrade", "requestFightingGrowthUpgrade");
		funcCallBackMap.put("requestgcompselectiondata", "requestGCompSelectionData");
		funcCallBackMap.put("requestGCompMatchData".toLowerCase(), "requestGCompMatchData");
		funcCallBackMap.put("requestGroupScoreRank".toLowerCase(), "requestGroupScoreRank");
		funcCallBackMap.put("requestGroupNewestScore".toLowerCase(), "requestGroupNewestScore");
		funcCallBackMap.put("MGCS".toLowerCase(), "moveGroupCompStage");
		funcCallBackMap.put("enterPrepareArea".toLowerCase(), "enterPrepareArea");
		funcCallBackMap.put("createGCompTeam".toLowerCase(), "requestCreateGCompTeam");
		funcCallBackMap.put("gCompTeamAction".toLowerCase(), "GCompTeamAction");
		funcCallBackMap.put("sendGroupPmd".toLowerCase(), "sendGroupPmd");
		funcCallBackMap.put("refreshGroupFightingRank".toLowerCase(), "refreshGroupFightingRank");
		funcCallBackMap.put("refreshGCompFighting".toLowerCase(), "refreshGCompFighting");
		funcCallBackMap.put("gCompGroupAction".toLowerCase(), "gCompGroupAction"); // * gcompgroupaction groupName
		funcCallBackMap.put("gCompCheckIfLeader".toLowerCase(), "gCompCheckIfLeader");
		funcCallBackMap.put("gCompCheckTimes".toLowerCase(), "gCompCheckTimes");

		// 批量添加物品
		funcCallBackMap.put("addbatchitem", "addBatchItem");
		funcCallBackMap.put("addAllMagicPieces".toLowerCase(), "addAllMagicPieces");

		funcCallBackMap.put("emptybag", "emptyBag");
		funcCallBackMap.put("emptyAccount".toLowerCase(), "emptyAccount");

		funcCallBackMap.put("addequiptorole", "addEquipToRole");

		funcCallBackMap.put("upgradetaoist", "upgradeTaoist");
		funcCallBackMap.put("fixequiplevelup", "fixEquipLevelUp");
		funcCallBackMap.put("fixequipstarup", "fixEquipStarUp");
		funcCallBackMap.put("upgrademagic", "upgradeMagic");
		funcCallBackMap.put("growthFund".toLowerCase(), "growthFund");
		funcCallBackMap.put("growthFundBoughtCount".toLowerCase(), "setGrowthFundBoughtCount");

		// * callrb 1 生成随机boss,如果角色已经达到生成boss上限，这个指令会无效
		funcCallBackMap.put("callrb", "callRb");
		funcCallBackMap.put("testcharge", "testCharge");

		funcCallBackMap.put("addsaexp", "addSaExp");
		funcCallBackMap.put("exchangeSoul".toLowerCase(), "exchangeSoul");
		funcCallBackMap.put("resetJBZD".toLowerCase(), "resetJBZD");
		funcCallBackMap.put("resetLQSG".toLowerCase(), "resetLQSG");
		funcCallBackMap.put("resetQKHJ".toLowerCase(), "resetQKHJ");
		funcCallBackMap.put("resetJBZDCD".toLowerCase(), "resetJBZDCD");
		funcCallBackMap.put("resetLQSGCD".toLowerCase(), "resetLQSGCD");
		funcCallBackMap.put("resetQKHJCD".toLowerCase(), "resetQKHJCD");
		funcCallBackMap.put("resetSCLJ".toLowerCase(), "resetSCLJ");
		funcCallBackMap.put("resetSCLJCD".toLowerCase(), "resetSCLJCD");
		funcCallBackMap.put("resetWXZ".toLowerCase(), "resetWXZ");
		funcCallBackMap.put("resetWXZCD".toLowerCase(), "resetWXZCD");
		funcCallBackMap.put("resetFST".toLowerCase(), "resetFST");
		funcCallBackMap.put("resetFSTCD".toLowerCase(), "resetFSTCD");
		funcCallBackMap.put("sendWorldChat".toLowerCase(), "sendWorldChat");

		funcCallBackMap.put("sendOneHundredEmails".toLowerCase(), "sendOneHundredEmails");

		// 修改活动配置的时间和奖励
		funcCallBackMap.put("setcfgtime", "setCfgTime");
		funcCallBackMap.put("setcfgreward", "setCfgReward");

		// 世界boss召唤指令,这个指令会把旧的怪物结束，并且召唤一个新的出来
		funcCallBackMap.put("callwb", "callWorldBoss");
		funcCallBackMap.put("wbstate", "changeWBState");
		funcCallBackMap.put("openwb", "openWorldBoss");// 开启/关闭世界boss状态 openwb num(0=关闭，1=开启)
		funcCallBackMap.put("addGroupDonateAndExp".toLowerCase(), "addGroupDonateAndExp");
		funcCallBackMap.put("addPersonalContribute".toLowerCase(), "addPersonalContribute");
		funcCallBackMap.put("recal", "reCal");
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private boolean reloadConfigByHelperClass(String clname) {
		try {
			CfgCsvReloader.reloadByClassName(clname);
			GameLog.info("GM", "reload config", "reloadByClassName:" + clname + " success", null);
			return true;
		} catch (Exception e) {
			GameLog.error("GM", "reload config", "reloadByClassName:" + clname + " failed", e);
			return false;
		}
	}

	private boolean assumeSendRequest(Player player, com.rwproto.RequestProtos.Request request) {
		try {
			Long sessionId = UserChannelMgr.getSessionId(player.getUserId());
			if (sessionId == null) {
				return false;
			}
			com.rwbase.gameworld.GameWorldFactory.getGameWorld().asyncExecute(player.getUserId(), new com.rw.controler.GameLogicTask(sessionId, request));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean SetGroupSupplier(String[] comd, Player player) {
		boolean result = true;
		Group group = GroupHelper.getInstance().getGroup(player);
		if (group == null) {
			return false;
		}
		group.getGroupBaseDataMgr().setGroupSupplier(100000);
		group.getGroupBaseDataMgr().updateAndSynGroupData(player);
		return result;
	}

	/** GM命令 */
	public boolean reloadConfig(String[] arrCommandContents, Player player) {
		GameLog.info("GM", "reloadConfig", "start", null);
		boolean result = true;
		for (int i = 0; i < arrCommandContents.length; i++) {
			String clname = arrCommandContents[i];
			clname = CfgCsvReloader.findClassName(clname);
			result = result && reloadConfigByHelperClass(clname);
		}
		GameLog.info("GM", "reloadConfig", "finished", null);
		return result;
	}

	public boolean endBTsweep(String[] arrCommandContents, Player player) {
		GameLog.info("GM", "endBTsweep", "start", null);
		boolean result = true;
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		int highestFloor = tableBattleTower.getHighestFloor();
		// 更新数据
		tableBattleTower.setSweepStartTime(0);
		// by franky 扫荡结束时需要重置每层扫荡时间，下次开始扫荡就按照新的特权值进行设置
		tableBattleTower.setSweepTimePerFloor(0);
		tableBattleTower.setSweepState(false);
		tableBattleTower.setSweepStartFloor(0);
		tableBattleTower.setCurFloor(highestFloor);
		tableBattleTower.setResult(true);
		UserEventMgr.getInstance().BattleTower(player, highestFloor);
		dao.update(tableBattleTower);
		GameLog.info("GM", "endBTsweep ", "finished", null);
		return result;
	}

	public boolean setBattleTowerKey(String[] arrCommandContents, Player player) {
		GameLog.info("GM", "setBattleTowerKey", "start", null);
		boolean result = true;
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		int count = Integer.parseInt(arrCommandContents[0]);
		// 更新数据
		tableBattleTower.setCopper_key(count);
		tableBattleTower.setGold_key(count);
		tableBattleTower.setSilver_key(count);
		dao.update(tableBattleTower);
		GameLog.info("GM", "setBattleTowerKey ", "finished", null);
		return result;
	}

	public boolean setAllTaoist(String[] arrCommandContents, Player player) {
		GameLog.info("GM", "setAllTaoist", "start", null);
		boolean result = true;
		ITaoistMgr mgr = player.getTaoistMgr();
		Iterable<TaoistMagicCfg> cfglst = TaoistMagicCfgHelper.getInstance().getIterateAllCfg();
		for (TaoistMagicCfg cfg : cfglst) {
			mgr.setLevel(cfg.getKey(), 50);
		}
		GameLog.info("GM", "setAllTaoist ", "finished", null);
		return result;
	}

	public boolean resetTaoistLevelByTag(String[] arrCommandContents, Player player) {
		int tag = Integer.parseInt(arrCommandContents[0]);
		GameLog.info("GM", "resetTaoistLevel", "start", null);
		boolean result = true;
		ITaoistMgr mgr = player.getTaoistMgr();
		Iterable<TaoistMagicCfg> cfglst = TaoistMagicCfgHelper.getInstance().getIterateAllCfg();
		for (TaoistMagicCfg cfg : cfglst) {
			if (cfg.getTagNum() == tag) {
				mgr.setLevel(cfg.getKey(), 1);
			}
		}
		GameLog.info("GM", "resetTaoistLevel ", "finished", null);
		return result;
	}

	public boolean resetTaoistLevelById(String[] arrCommandContents, Player player) {
		int id = Integer.parseInt(arrCommandContents[0]);
		GameLog.info("GM", "resetTaoistLevel", "start", null);
		boolean result = true;
		ITaoistMgr mgr = player.getTaoistMgr();
		Iterable<TaoistMagicCfg> cfglst = TaoistMagicCfgHelper.getInstance().getIterateAllCfg();
		for (TaoistMagicCfg cfg : cfglst) {
			if (cfg.getKey() == id) {
				mgr.setLevel(cfg.getKey(), 1);
			}
		}
		GameLog.info("GM", "resetTaoistLevel ", "finished", null);
		return result;
	}

	public boolean setBattleTowerLeftTime(String[] arrCommandContents, Player player) {
		GameLog.info("GM", "setBattleTowerLeftTime", "start", null);
		boolean result = true;
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();// 试练塔数据管理
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();// 试练塔的存储数据

		final int highestFloor = tableBattleTower.getHighestFloor();
		int sweepStartFloor = highestFloor - 2;
		long now = System.currentTimeMillis();

		tableBattleTower.setSweepStartTime(now);
		tableBattleTower.setSweepStartFloor(sweepStartFloor);
		TableBattleTowerDao.getDao().update(tableBattleTower);
		GameLog.info("GM", "setBattleTowerLeftTime " + "finished", null);
		return result;
	}

	public boolean setBattleTowerFloor(String[] arrCommandContents, Player player) {
		GameLog.info("GM", "setBattleTowerFloor", "start", null);
		boolean result = true;
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();// 试练塔数据管理
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();// 试练塔的存储数据
		int curFloor = Integer.parseInt(arrCommandContents[0]);
		tableBattleTower.setCurFloor(curFloor);
		tableBattleTower.setHighestFloor(curFloor);
		TableBattleTowerDao.getDao().update(tableBattleTower);
		GameLog.info("GM", "setBattleTowerFloor " + curFloor, "finished", null);
		return result;
	}

	public boolean resetPeakArenaChallenge(String[] arrCommandContents, Player player) {
		GameLog.info("GM", "resetPeakArenaChallenge", "start", null);
		PeakArenaBM.getInstance().resetDataInNewDay(player);
		GameLog.info("GM", "resetPeakArenaChallenge", "finished", null);
		return true;
	}

	public boolean reloadPeakArenaConfig(String[] arrCommandContents, Player player) {
		GameLog.info("GM", "reloadPeakArenaConfig", "start", null);
		boolean result = true;
		result = result && reloadConfigByHelperClass(peakArenaInfoHelper.class.getName());
		result = result && reloadConfigByHelperClass(peakArenaBuyCostHelper.class.getName());
		result = result && reloadConfigByHelperClass(peakArenaResetCostHelper.class.getName());
		result = result && reloadConfigByHelperClass(peakArenaMatchRuleHelper.class.getName());
		result = result && reloadConfigByHelperClass(peakArenaPrizeHelper.class.getName());
		GameLog.info("GM", "reloadPeakArenaConfig", "finished", null);
		return result;
	}

	public boolean reloadPrivilegeConfig(String[] arrCommandContents, Player player) {
		GameLog.info("GM", "reloadPrivilegeConfig", "start", null);
		PrivilegeConfigHelper.getInstance().reloadAllPrivilegeConfigs();
		GameLog.info("GM", "reloadPrivilegeConfig", "finished", null);
		return true;
	}

	public boolean reloadFashionConfig(String[] arrCommandContents, Player player) {
		boolean result = true;
		result = result && reloadConfigByHelperClass(FashionBuyRenewCfgDao.class.getName());
		result = result && reloadConfigByHelperClass(FashionEffectCfgDao.class.getName());
		result = result && reloadConfigByHelperClass(FashionQuantityEffectCfgDao.class.getName());
		result = result && reloadConfigByHelperClass(FashionCommonCfgDao.class.getName());
		return result;
	}

	public boolean reloadUnlockFashionIconCfg(String[] arrCommandContents, Player player) {
		boolean result = true;
		result = result && reloadConfigByHelperClass(HeadBoxCfgDAO.class.getName());
		result = result && reloadConfigByHelperClass(FashionCommonCfgDao.class.getName());
		return result;
	}

	public boolean setFashionExpiredTime(String[] arrCommandContents, Player player) {
		GameLog.info("时装", player.getUserId(), "调用设置过期时间命令", null);
		if (arrCommandContents == null || arrCommandContents.length < 2) {
			GameLog.info("时装", player.getUserId(), "调用设置过期时间命令,参数不足", null);
			return false;
		}
		int fashionId = Integer.parseInt(arrCommandContents[0]);
		int minutes = Integer.parseInt(arrCommandContents[1]);
		FashionMgr mgr = player.getFashionMgr();
		return mgr.GMSetExpiredTime(fashionId, minutes);
	}

	public boolean setFashion(String[] arrCommandContents, Player player) {
		GameLog.info("时装", player.getUserId(), "设置时装命令", null);
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			GameLog.info("时装", player.getUserId(), "设置时装命令", null);
			return false;
		}
		int fashionId = Integer.parseInt(arrCommandContents[0]);
		int minutes = Integer.parseInt(arrCommandContents[1]);
		FashionMgr mgr = player.getFashionMgr();
		return mgr.giveFashionItem(fashionId, minutes, false, true, TimeUnit.MINUTES);
	}

	// 钓鱼台配置更新并重新生成热点数据
	public boolean reloadGambleConfig(String[] arrCommandContents, Player player) {
		boolean result = true;
		result = result && reloadConfigByHelperClass(HotGambleCfgHelper.class.getName());
		result = result && reloadConfigByHelperClass(GamblePlanCfgHelper.class.getName());
		result = result && reloadConfigByHelperClass(GambleDropCfgHelper.class.getName());
		if (result) {
			player.getGambleMgr().resetHotHeroList();
		}
		return result;
	}

	public boolean ReloadNewGuideCfg(String[] arrCommandContents, Player player) {
		return reloadConfigByHelperClass(GiveItemCfgDAO.class.getName());
	}

	public boolean ReadNewGuideConfig(String[] arrCommandContents, Player player) {
		System.out.println("ReadNewGuideConfig command");
		DebugNewGuideData debugSupport = DebugNewGuideData.getInstance();
		debugSupport.ClearData();
		boolean result = debugSupport.RefreshConfig();
		return result;
	}

	public boolean UpdateNewGuideConfig(String[] arrCommandContents, Player player) {
		System.out.println("UpdateNewGuideConfig command");
		DebugNewGuideData debugSupport = DebugNewGuideData.getInstance();
		boolean result = debugSupport.RefreshConfig();
		if (result) {
			GuidanceConfigs.Builder configfiles = GuidanceConfigs.newBuilder();
			configfiles.setGuidanceData(debugSupport.getGuidanceData());
			configfiles.setActionsData(debugSupport.getActionsData());
			configfiles.setConditionalsData(debugSupport.getConditionalsData());
			configfiles.setConductressData(debugSupport.getConductressData());
			player.SendMsg(Command.MSG_NEW_GUIDE, configfiles.build().toByteString());
		}
		return result;
	}

	public boolean rankSort(String[] arrCommandContents, Player player) {
		// if (arrCommandContents == null || arrCommandContents.length < 1) {
		// System.out.println(" command param not right ...");
		// return false;
		// }
		// String rankType = arrCommandContents[0];
		// if (player != null) {
		// RankingMgr.getInstance().onUpdateSort(ERankingType.valueOf(Integer.parseInt(rankType)));
		// return true;
		// }
		return false;
	}

	// 函数名与funcCallBackMap里面的value相同
	public boolean addItem(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 2) {
			System.out.println(" command param not right ...");
			return false;
		}
		int itemId = Integer.parseInt(arrCommandContents[0]);
		int itemNum = Integer.parseInt(arrCommandContents[1]);
		if (player != null) {
			// TODO @fixing HC 减少作弊的循环次数，提高效率，如果是可叠加物品就直接把全部数量放过去就好了
			// for (int i = 0; i < itemNum; i++) {
			// player.getItemBagMgr().addItem(itemId, itemNum);
			ItemBagMgr.getInstance().addItem(player, itemId, itemNum);
			// }
			return true;
		}
		return false;
	}

	/*
	 * 设定副本地图的通关关卡
	 */
	public boolean setMap(String[] arrCommandContents, Player player) {
		// if (arrCommandContents == null || arrCommandContents.length < 1) {
		// player.NotifyCommonMsg("命令有误，请重新输入");
		// return false;
		// }
		// String id = arrCommandContents[0];
		// if (id == null) {
		// player.NotifyCommonMsg("mapid有错");
		// return false;
		// }
		// MapCfg map = (MapCfg) MapCfgDAO.getInstance().getCfgById(id);
		// if (map == null) {
		// player.NotifyCommonMsg("mapid有错");
		// return false;
		// }
		//
		// int nMapID = map.getId();
		// MapCfgDAO mapCfgDAO = MapCfgDAO.getInstance();
		// CopyCfgDAO cfgDAO = CopyCfgDAO.getInstance();
		// List<CopyCfgIF> list = new ArrayList<CopyCfgIF>();
		// for (int i = 1001; i <= nMapID; i++) {
		// MapCfg mapCfg = mapCfgDAO.getCfg(i);
		// if (map != null) {
		// int start = mapCfg.getStartLevelId();
		// int end = mapCfg.getEndLevelId();
		// for (int levelId = start; levelId <= end; i++) {
		// CopyCfg copyCfg = cfgDAO.getCfg(levelId);
		// if (copyCfg != null) {
		// list.add(copyCfg);
		// }
		// }
		// }
		// }
		// MsgCopyResponse.Builder copyResponse = player.getCopyRecordMgr().setMapByGM(map);// 获取要新增的关卡...
		// player.SendMsg(Command.MSG_CopyService, copyResponse.build().toByteString());
		// return true;
		return GMCopyProcesser.processSetMap(arrCommandContents, player);
	}

	public boolean addPower(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int nAddPower = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.addPower(nAddPower);
			return true;
		}
		return false;
	}

	public boolean addHero(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		String heroId = arrCommandContents[0];
		if (player != null) {
			// player.getHeroMgr().addHero(heroId);
			player.getHeroMgr().addHero(player, heroId);
			return true;
		}
		return false;
	}

	public boolean addFixEquipItem(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (player != null) {
			ArrayList<ItemInfo> list = new ArrayList<ItemInfo>();
			GMAddFixEquip.addStarUp(player, list);
			GMAddFixEquip.addexp(player, list);
			GMAddFixEquip.addqualityUp(player, list);
			ItemBagMgr.getInstance().addItem(player, list);
			return true;
		}
		return false;
	}

	public boolean addCoin(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int addNum = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.getUserGameDataMgr().addCoin(addNum);
			return true;
		}
		// String [] strs = {"901","90001"};
		// ActivityExchangeTypeHandler.getInstance();
		// ActivityExchangeTypeHandler.GmTakeGift(player, strs);
		//
		return false;
	}

	public boolean addSecretKeycount(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int addNum = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {

			UserGroupSecretBaseDataMgr baseDataMgr = UserGroupSecretBaseDataMgr.getMgr();
			baseDataMgr.updateBuyKeyData(player, addNum);

			return true;
		}
		return false;
	}

	public boolean addWakenPiece(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int addNum = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.getUserGameDataMgr().addWakenPiece(addNum);
			return true;
		}
		return false;
	}

	public boolean addWakenKey(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int addNum = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.getUserGameDataMgr().addWakenKey(addNum);
			return true;
		}
		return false;
	}

	public boolean getgift(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (player != null) {
			// ChargeMgr.getInstance().buyMonthCard(player, null);
			return true;
		}
		return false;
	}

	public boolean setChargeOn(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (player != null) {
			int isChargeOn = Integer.parseInt(arrCommandContents[0]);
			boolean ChargeOn = false;
			if (isChargeOn == 0) {
				ChargeOn = true;
			}
			ServerStatusMgr.setChargeOn(ChargeOn);
			return true;
		}
		return false;
	}

	public boolean addGold(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int addNum = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.getUserGameDataMgr().addGoldByGm(addNum);
			return true;
		}
		return false;
	}

	public boolean clrBag(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (player != null) {
			if ("1".equals(arrCommandContents[0])) {
				ItemBagMgr.getInstance().removeAllItems();
				return true;
			}
		}
		return false;
	}

	public boolean setVip(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int num = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.setVip(num);
			// 设置界面更新vip
			player.getSettingMgr().checkOpen();
			return true;
		}
		return false;
	}

	public boolean setskillpointcount(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int num = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.getUserGameDataMgr().setSkillPointCount(num);
			return true;
		}
		return false;
	}

	public boolean upgradeTaoist(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int upgradelevel = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			GMHeroBase.gmUpgradeTaoist(player, upgradelevel);
			return true;
		}
		return false;
	}

	public boolean fixEquipLevelUp(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int upgradelevel = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			GMHeroBase.gmFixEquipLevelUp(player, upgradelevel);
			return true;
		}
		return false;
	}

	public boolean fixEquipStarUp(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int starLevel = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			GMHeroBase.gmFixEquipStarUp(player, starLevel);
			return true;
		}
		return false;
	}

	public boolean upgradeMagic(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int upgradeLevel = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			GMHeroBase.gmUpgradeMagic(player, upgradeLevel);
			return true;
		}
		return false;
	}

	public boolean recharge(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		String itemId = arrCommandContents[0];
		if (player != null) {
			ChargeMgr.getInstance().testCharge(player, itemId);
			return true;
		}
		return false;
	}

	public boolean setLevel(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		String newLevel = arrCommandContents[0];
		if (player != null) {
			player.setLevelByGM(Integer.parseInt(newLevel));
			return true;
		}
		return false;
	}

	public boolean addExp(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		long addExp = Long.parseLong(arrCommandContents[0]);
		if (player != null) {
			player.addUserExp(addExp);
			return true;
		}
		return false;
	}

	public boolean sendEmail(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null /* || arrCommandContents.length < 2 */) {
			System.out.println(" command param not right ...");
			return false;
		}
		EmailUtils.sendEmail(player.getUserId(), arrCommandContents[0], arrCommandContents.length > 2 ? arrCommandContents[1] : null);
		return true;
	}

	public boolean sendTestEmail(String[] arrCommandContents, Player player) {

		List<Integer> mailList = new ArrayList<Integer>();

		for (int i = 10004; i <= 10009; i++) {
			mailList.add(i);
		}

		for (int i = 10031; i <= 10035; i++) {
			mailList.add(i);
		}

		for (Integer iTemp : mailList) {
			EmailUtils.sendEmail(player.getUserId(), String.valueOf(iTemp));
		}
		return true;
	}

	public boolean addTowerNum(String[] arrCommandContents, Player player) {
		int num = Integer.parseInt(arrCommandContents[0]);
		player.getTowerMgr().addTowerNum(num);
		;
		return true;
	}

	public boolean callRb(String[] commands, Player player) {
		RandomBossMgr.getInstance().findBossBorn(player, false);
		return true;
	}

	public boolean callWorldBoss(String[] args, Player player) {
		WBMgr.getInstance().reCallNewBoss();
		return true;
	}

	public boolean openWorldBoss(String[] args, Player player) {
		int state = Integer.parseInt(args[0]);
		WBMgr.getInstance().changeWorldBossState(state);
		return true;
	}

	public boolean changeWBState(String[] args, Player player) {
		WBMgr.getInstance().change2NextState();
		return true;
	}

	public boolean setWjzh(String[] arrCommandContents, Player player) {
		player.unendingWarMgr.getTable().setNum(Integer.parseInt(arrCommandContents[0]) - 1);
		player.unendingWarMgr.save();
		return true;
	}

	public boolean resetWjzh(String[] arrCommandContents, Player player) {
		player.unendingWarMgr.getTable().setLastChallengeTime(0);
		player.unendingWarMgr.save();
		return true;
	}

	public boolean addHeroExp(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (arrCommandContents.length == 1) {
			long addExp = Long.parseLong(arrCommandContents[0]);
			if (player != null) {
				// player.getHeroMgr().AddAllHeroExp(addExp);
				player.getHeroMgr().AddAllHeroExp(player, addExp);
				return true;
			}
			return false;
		}

		int heroId = Integer.parseInt(arrCommandContents[0]);
		long addExp = Long.parseLong(arrCommandContents[1]);
		if (player != null) {
			// player.getHeroMgr().getHeroByModerId(heroId).addHeroExp(addExp);
			Hero h = player.getHeroMgr().getHeroByModerId(player, heroId);
			player.getHeroMgr().addHeroExp(player, h, addExp);
			return true;
		}
		return false;
	}

	public boolean setAttack(String[] arrCommandContents, Player player) {
		// 此方法不再支持
		System.out.println(" command param not support any more ...");
		return false;
	}

	public boolean addAttack(String[] arrCommandContents, Player player) {
		// 此方法不再支持
		System.out.println(" command param not support any more ...");
		return false;
	}

	public boolean commonMsg(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (player != null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, arrCommandContents[0]);
			return true;
		}
		return false;
	}

	public boolean reloadArena(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (player != null) {
			if ("1".equals(arrCommandContents[0])) {
				// ArenaBM.getInstance().initArenaInfoList();
				return true;
			}
		}
		return false;
	}

	public boolean serverTime(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (player != null) {
			if ("1".equals(arrCommandContents[0])) {
				Calendar c = Calendar.getInstance();
				;
				player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, c.getTime().toString());
				return true;
			}
		}
		return false;
	}

	public boolean probstore(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}
		if (player != null) {
			player.getStoreMgr().ProbStore(eStoreConditionType.WarCopy);
			return true;
		}
		return false;
	}

	public boolean sendPmd(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}
		if (player != null) {
			MainMsgHandler.getInstance().sendPmdGm(player, arrCommandContents);
			return true;
		}
		return false;
	}

	public boolean addArenaCoin(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}
		if (player != null) {
			int addNum = Integer.parseInt(arrCommandContents[0]);
			player.getUserGameDataMgr().addArenaCoin(addNum);
			return true;
		}
		return false;
	}

	public ByteString executeGMCommand(Player player, MsgGMRequest msgGMRequest) {
		MsgGMResponse.Builder msgGMResponse = MsgGMResponse.newBuilder().setMsgGMRequest(msgGMRequest);

		boolean bFuncCallBackState = false;
		String content = msgGMRequest.getContent();
		String[] arrCommands = content.split(";");
		String command;
		String[] arrCommandContents;
		int argsNum;
		for (int i = 0; i < arrCommands.length; i++) {
			command = arrCommands[i];
			// command = command.replace("* ","");
			arrCommandContents = command.split(" ");
			argsNum = arrCommandContents.length;
			if (argsNum < 3 || !arrCommandContents[0].equals("*")) {
				System.out.println(" command param not right or no * ...");
				bFuncCallBackState = false;
				break;
			}
			if (funcCallBackMap.containsKey(arrCommandContents[1].toLowerCase())) {
				String methodName = funcCallBackMap.get(arrCommandContents[1].toLowerCase());
				Method declaredMethod;
				try {
					declaredMethod = this.getClass().getDeclaredMethod(methodName, new Class[] { String[].class, Player.class });
					String[] param = new String[argsNum - 2];
					for (int k = 0; k < argsNum - 2; k++) {
						param[k] = arrCommandContents[k + 2];
					}
					bFuncCallBackState = (Boolean) declaredMethod.invoke(this, new Object[] { param, player });
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println(" no related method ...");
				bFuncCallBackState = false;
			}
		}
		// System.out.println(bFuncCallBackState);
		if (bFuncCallBackState) {
			msgGMResponse.setEGMResultType(eGMResultType.SUCCESS);
			return msgGMResponse.build().toByteString();
		} else {
			msgGMResponse.setEGMResultType(eGMResultType.FAIL);
			return msgGMResponse.build().toByteString();
		}
	}

	public boolean executeGMCommand(Player player, String content) {
		boolean bFuncCallBackState = false;
		String[] arrCommands = content.split(";");
		String command;
		String[] arrCommandContents;
		int argsNum;
		for (int i = 0; i < arrCommands.length; i++) {
			command = arrCommands[i];
			arrCommandContents = command.split(" ");
			argsNum = arrCommandContents.length;
			if (argsNum < 3 || !arrCommandContents[0].equals("*")) {
				System.out.println(" command param not right or no * ...");
				bFuncCallBackState = false;
				break;
			}
			if (funcCallBackMap.containsKey(arrCommandContents[1].toLowerCase())) {
				String methodName = funcCallBackMap.get(arrCommandContents[1].toLowerCase());
				Method declaredMethod;
				try {
					declaredMethod = this.getClass().getDeclaredMethod(methodName, new Class[] { String[].class, Player.class });
					String[] param = new String[argsNum - 2];
					for (int k = 0; k < argsNum - 2; k++) {
						param[k] = arrCommandContents[k + 2];
					}
					bFuncCallBackState = (Boolean) declaredMethod.invoke(this, new Object[] { param, player });
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println(" no related method ...");
				bFuncCallBackState = false;
			}
		}
		if (bFuncCallBackState) {
			return true;
		} else {
			return false;
		}
	}

	public boolean teamBringit(String[] arrCommandContents, Player player) {
		// if(arrCommandContents == null){
		// return false;
		// }
		if (player != null) {
			GMHeroProcesser.processTeamBringit(arrCommandContents, player);
			return true;
		}
		return false;
	}

	public boolean teamBringitSigle(String[] arrCommandContents, Player player) {
		// if(arrCommandContents == null){
		// return false;
		// }
		if (player != null) {
			GMHeroProcesser.processTeamBringitSigle(arrCommandContents, player);
			return true;
		}
		return false;
	}

	public boolean addHero1(String[] arrCommandContents, Player player) {
		// if(arrCommandContents == null){
		// return false;
		// }
		if (player != null) {
			GMHeroProcesser.processAddhero(arrCommandContents, player);
			return true;
		}
		return false;
	}

	public boolean setTeam1(String[] arrCommandContents, Player player) {

		if (arrCommandContents == null) {
			return false;
		}
		if (player != null) {
			GMHeroProcesser.processSetteam1(arrCommandContents, player);
			return true;
		}
		return false;
	}

	public boolean setTeam2(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null) {
			return false;
		}
		if (player != null) {
			GMHeroProcesser.processSetteam2(arrCommandContents, player);
			return true;
		}
		return false;
	}

	public boolean clearBattleTowerResetTimes(String[] arrCommandContents, Player player) {
		if (player == null) {
			return false;
		}

		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			return false;
		}

		tableBattleTower.setResetTimes(0);
		tableBattleTower.setCurBossTimes(0);
		long now = System.currentTimeMillis();
		tableBattleTower.setResetTime(now);
		TableBattleTowerDao.getDao().update(tableBattleTower);
		return true;
	}

	/**
	 * 获取当前等级可以穿戴的装备
	 * 
	 * @param arrCommandContents
	 * @param player
	 * @return
	 */
	public boolean gainHeroEquip(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}

		if (player == null) {
			return false;
		}

		Hero hero;
		String heroId = arrCommandContents[0];
		if ("0".equalsIgnoreCase(heroId)) {
			hero = player.getMainRoleHero();
		} else {
			// hero =
			// player.getHeroMgr().getHeroByModerId(Integer.parseInt(heroId));
			hero = player.getHeroMgr().getHeroByModerId(player, Integer.parseInt(heroId));
		}

		if (hero == null) {
			return false;
		}

		String qualityId = hero.getQualityId();
		List<Integer> equipList = RoleQualityCfgDAO.getInstance().getEquipList(qualityId);
		if (equipList.isEmpty()) {
			return false;
		}

		List<INewItem> addItemList = new ArrayList<INewItem>();
		for (int i = 0, size = equipList.size(); i < size; i++) {
			int equipId = equipList.get(i);
			// 新装备
			INewItem newItem = new NewItem(equipId, 1, null);
			addItemList.add(newItem);
		}

		return ItemBagMgr.getInstance().useLikeBoxItem(player, null, addItemList);
	}

	/**
	 * 穿戴装备
	 * 
	 * @param arrCommandContents
	 * @param player
	 * @return
	 */
	public boolean wearEquip(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}

		if (player == null) {
			return false;
		}

		Hero hero;
		String heroId = arrCommandContents[0];
		if ("0".equalsIgnoreCase(heroId)) {
			hero = player.getMainRoleHero();
		} else {
			// hero =
			// player.getHeroMgr().getHeroByModerId(Integer.parseInt(heroId));
			hero = player.getHeroMgr().getHeroByModerId(player, Integer.parseInt(heroId));
		}

		if (hero == null) {
			return false;
		}

		hero.getEquipMgr().orderHeroWearEquip(player, hero);
		return true;
	}

	public boolean resetTimes(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}

		if (player == null) {
			return false;
		}

		String functionName = arrCommandContents[0];
		if (functionName.equalsIgnoreCase("wx")) {
			TowerMgr towerMgr = player.getTowerMgr();
			TableAngelArrayData angleArrayData = towerMgr.getAngleArrayData();
			if (angleArrayData == null) {
				return false;
			}

			angleArrayData.setResetTimes(0);
			towerMgr.saveAngleArrayData();
		} else if (functionName.equalsIgnoreCase("bt")) {
			BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
			TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
			if (tableBattleTower == null) {
				return false;
			}

			tableBattleTower.setResetTimes(0);
			TableBattleTowerDao.getDao().update(tableBattleTower);
		} else if (functionName.equalsIgnoreCase("dt")) {
			UserGroupAttributeDataMgr userGroupAttributeDataMgr = player.getUserGroupAttributeDataMgr();
			UserGroupAttributeData userGroupAttributeData = userGroupAttributeDataMgr.getUserGroupAttributeData();
			String groupId = userGroupAttributeData.getGroupId();
			if (StringUtils.isEmpty(groupId)) {
				return false;
			}

			userGroupAttributeDataMgr.resetMemberDataDonateTimes(player.getUserId(), System.currentTimeMillis());

			// Group group = GroupBM.get(groupId);
			// if (group == null) {
			// return false;
			// }
			//
			// GroupMemberMgr groupMemberMgr = group.getGroupMemberMgr();
			// groupMemberMgr.resetMemberDataDonateTimes(player.getUserId(), System.currentTimeMillis());
		} else if (functionName.equalsIgnoreCase("wxf")) {
			resetWxFighting(player);
		}

		return true;
	}

	public boolean groupChange(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}

		if (player == null) {
			return false;
		}

		UserGroupAttributeDataMgr userGroupAttributeDataMgr = player.getUserGroupAttributeDataMgr();
		UserGroupAttributeDataIF baseGroupData = userGroupAttributeDataMgr.getUserGroupAttributeData();

		String functionName = arrCommandContents[0];
		if (functionName.equalsIgnoreCase("cet")) {// 清除发送邮件冷却时间
			userGroupAttributeDataMgr.updateSendEmailTime(player, 0);
			return true;
		} else if (functionName.equalsIgnoreCase("cqt")) {// 清除个人退去帮派时间
			userGroupAttributeDataMgr.updateDataWhenQuitGroup(player, 0);
			return true;
		}

		String groupId = baseGroupData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return false;
		}

		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			return false;
		}

		GroupBaseDataMgr groupBaseDataMgr = group.getGroupBaseDataMgr();
		GroupBaseDataIF groupData = groupBaseDataMgr.getGroupData();
		if (groupData == null) {
			return false;
		}

		String userId = player.getUserId();
		GroupMemberMgr groupMemberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = groupMemberMgr.getMemberData(userId, false);
		if (memberData == null) {
			return false;
		}

		int value = arrCommandContents.length > 1 ? Integer.parseInt(arrCommandContents[1]) : 0;

		if (functionName.equalsIgnoreCase("gs")) {// 改变帮派物资
			if (value == 0) {
				return false;
			}

			int su = groupData.getSupplies() + value;
			if (su < 0) {
				return false;
			}

			groupBaseDataMgr.updateGroupDonate(player, group.getGroupLogMgr(), value, 0, 0, true);
		} else if (functionName.equalsIgnoreCase("pc")) {// 改变个人贡献
			if (value == 0) {
				return false;
			}

			int su = baseGroupData.getContribution() + value;
			if (su < 0) {
				return false;
			}
			groupMemberMgr.updateMemberContribution(userId, value, true);
		} else if (functionName.equalsIgnoreCase("exp")) {// 增加帮派经验
			if (value <= 0) {
				return false;
			}

			if (groupData instanceof GroupBaseData) {
				groupBaseDataMgr.addGroupExp(player, (GroupBaseData) groupData, group.getGroupLogMgr(), value);
				groupBaseDataMgr.updateAndSynGroupData(player);
			} else {
				return false;
			}
		} else if (functionName.equalsIgnoreCase("token")) {// 增加帮派令牌
			if (value == 0) {
				return false;
			}

			groupBaseDataMgr.updateGroupDonate(player, group.getGroupLogMgr(), 0, 0, value, true);
		}

		return true;
	}

	public boolean setGFightState(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 2) {
			return false;
		}
		GFightStateTransfer.getInstance().transferToState(Integer.valueOf(arrCommandContents[0]), Integer.valueOf(arrCommandContents[1]));
		return true;
	}

	public boolean setGFightAutoState(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length != 1) {
			return false;
		}
		GFightStateTransfer.getInstance().setAutoCheck(Integer.valueOf(arrCommandContents[0]) == 1);
		return true;
	}

	public boolean getPrivateChatList(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}
		String targetUserId = arrCommandContents[0];
		com.rwproto.RequestProtos.Request.Builder requestBuilder = com.rwproto.RequestProtos.Request.newBuilder();
		com.rwproto.RequestProtos.RequestHeader.Builder headerBuilder = com.rwproto.RequestProtos.RequestHeader.newBuilder();
		headerBuilder.setCommand(com.rwproto.MsgDef.Command.MSG_CHAT_REQUEST_PRIVATE_CHATS);
		headerBuilder.setUserId(player.getUserId());
		requestBuilder.setHeader(headerBuilder.build());
		com.rwproto.RequestProtos.RequestBody.Builder bodyBuilder = com.rwproto.RequestProtos.RequestBody.newBuilder();
		bodyBuilder.setSerializedContent(com.rwproto.ChatServiceProtos.MsgChatRequestPrivateChats.newBuilder().setUserId(targetUserId).build().toByteString());
		requestBuilder.setBody(bodyBuilder.build());
		return this.assumeSendRequest(player, requestBuilder.build());
	}

	public boolean sendInteractiveData(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}
		String targetUserId = arrCommandContents[0];
		String type = arrCommandContents[1];
		try {
			Calendar now = Calendar.getInstance();
			int second = now.get(Calendar.SECOND);
			int minute = now.get(Calendar.MINUTE);
			int hour = now.get(Calendar.HOUR_OF_DAY);
			String time = (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
			if (type.equals("1")) {
				com.bm.chat.ChatBM.getInstance().sendInteractiveMsg(player, ChatInteractiveType.TREASURE, time + " : " + "幫派秘境：發給幫會", "1", "1;2;3;4", Arrays.asList(targetUserId));
				com.bm.chat.ChatBM.getInstance().sendInteractiveMsgToSomeone(player, targetUserId, ChatInteractiveType.TREASURE, time + " : " + "幫派秘境：發給個人", "4", "01;02;03;04");
				com.bm.chat.ChatBM.getInstance().sendInteractiveMsgToWorld(player, ChatInteractiveType.TREASURE, time + " : " + "幫派秘境：發給世界", "3", "TO;THE;WORLD;HAHA");
			} else {
				com.bm.chat.ChatBM.getInstance().sendInteractiveMsg(player, ChatInteractiveType.TEAM, time + " : " + "組隊邀請：發給幫會", "1", "1;2;3;4", Arrays.asList(targetUserId));
				com.bm.chat.ChatBM.getInstance().sendInteractiveMsgToSomeone(player, targetUserId, ChatInteractiveType.TEAM, time + " : " + "組隊邀請：發給個人", "4", "01;02;03;04");
				com.bm.chat.ChatBM.getInstance().sendInteractiveMsgToWorld(player, ChatInteractiveType.TEAM, time + " : " + "組隊邀請：發給世界", "3", "TO;THE;WORLD;HAHA");
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean receiveInteractiveData(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}
		String targetUserId = arrCommandContents[0];
		String type = arrCommandContents[1];
		try {
			Calendar now = Calendar.getInstance();
			int second = now.get(Calendar.SECOND);
			int minute = now.get(Calendar.MINUTE);
			int hour = now.get(Calendar.HOUR_OF_DAY);
			String time = (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
			Player sender = PlayerMgr.getInstance().find(targetUserId);
			if (type.equals("1")) {
				com.bm.chat.ChatBM.getInstance().sendInteractiveMsg(sender, ChatInteractiveType.TREASURE, time + " : " + "幫派秘境：發給幫會", "1", "1;2;3;4", Arrays.asList(player.getUserId()));
				com.bm.chat.ChatBM.getInstance().sendInteractiveMsgToSomeone(sender, player.getUserId(), ChatInteractiveType.TREASURE, time + " : " + "幫派秘境：發給個人", "4", "01;02;03;04");
				com.bm.chat.ChatBM.getInstance().sendInteractiveMsgToWorld(sender, ChatInteractiveType.TREASURE, time + " : " + "幫派秘境：發給世界", "3", "TO;THE;WORLD;HAHA");
			} else {
				com.bm.chat.ChatBM.getInstance().sendInteractiveMsg(sender, ChatInteractiveType.TEAM, time + " : " + "組隊邀請：發給幫會", "1", "1;2;3;4", Arrays.asList(player.getUserId()));
				com.bm.chat.ChatBM.getInstance().sendInteractiveMsgToSomeone(sender, player.getUserId(), ChatInteractiveType.TEAM, time + " : " + "組隊邀請：發給個人", "4", "01;02;03;04");
				com.bm.chat.ChatBM.getInstance().sendInteractiveMsgToWorld(sender, ChatInteractiveType.TEAM, time + " : " + "組隊邀請：發給世界", "3", "TO;THE;WORLD;HAHA");
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean setGroupBossFightTime(String[] arrcomStrings, Player player) {

		int count = Integer.parseInt(arrcomStrings[0]);
		if (count <= 0) {
			return false;
		}

		player.getUserGroupCopyRecordMgr().setRoleBattleTime(count, player);
		return true;
	}

	public boolean shutdownServer(String[] arrCommandContents, Player player) {
		System.exit(0);
		return true;
	}

	public boolean addDistCount(String[] str, Player player) {
		int count = Integer.parseInt(str[0]);
		if (count <= 0) {
			return false;
		}

		Group group = GroupHelper.getInstance().getGroup(player);
		if (group != null) {
			group.getGroupMemberMgr().resetAllotGroupRewardCount(player.getUserId(), count, false);
		}
		return true;
	}

	public boolean addServerStatusTips(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length != 1) {
			return false;
		}
		GFightStateTransfer.getInstance().setAutoCheck(Integer.valueOf(arrCommandContents[0]) == 1);
		return true;
	}

	public boolean speedUpSecret(String[] arrCommandContents, Player player) {
		String targetUserId;
		String arg = arrCommandContents[0];
		if (!arg.equals("1")) {
			targetUserId = arg;
		} else {
			targetUserId = player.getUserId();
		}
		int second = 1800;
		if (arrCommandContents.length > 1) {
			second = Integer.parseInt(arrCommandContents[1]);
		}
		if (second > 0) {
			try {
				Field fCreateTime = GroupSecretMatchRankAttribute.class.getDeclaredField("createTime");
				fCreateTime.setAccessible(true);
				UserCreateGroupSecretDataMgr mgr = UserCreateGroupSecretDataMgr.getMgr();
				UserCreateGroupSecretData data = mgr.get(targetUserId);

				List<GroupSecretData> list = data.getCreateList();
				Ranking<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> ranking = RankingFactory.getRanking(RankType.GROUP_SECRET_MATCH_RANK);
				for (GroupSecretData tempData : list) {
					long createTime = tempData.getCreateTime() - TimeUnit.SECONDS.toMillis(second);
					tempData.setCreateTime(createTime);

					RankingEntry<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> entry = ranking.getRankingEntry(player.getUserId() + "_" + tempData.getId());
					fCreateTime.set(entry.getExtendedAttribute(), createTime);

					// 更新排行信息
					ranking.subimitUpdatedTask(entry);
				}

				mgr.updateData(targetUserId);
				fCreateTime.setAccessible(false);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public boolean finishSecret(String[] arrCommandContents, Player player) {
		try {
			Field fCreateTime = GroupSecretMatchRankAttribute.class.getDeclaredField("createTime");
			fCreateTime.setAccessible(true);
			String userId = player.getUserId();
			UserCreateGroupSecretDataMgr mgr = UserCreateGroupSecretDataMgr.getMgr();

			UserCreateGroupSecretData data = mgr.get(userId);
			List<GroupSecretData> list = data.getCreateList();
			Ranking<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> ranking = RankingFactory.getRanking(RankType.GROUP_SECRET_MATCH_RANK);
			for (GroupSecretData tempData : list) {
				GroupSecretResourceCfg cfg = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(tempData.getSecretId());
				long millis = java.util.concurrent.TimeUnit.MINUTES.toMillis(cfg.getNeedTime());
				long suppose = tempData.getCreateTime() + millis;
				if (suppose > System.currentTimeMillis()) {
					long createTime = tempData.getCreateTime() - (suppose - System.currentTimeMillis());
					tempData.setCreateTime(createTime);
					RankingEntry<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> entry = ranking.getRankingEntry(userId + "_" + tempData.getId());
					fCreateTime.set(entry.getExtendedAttribute(), createTime);

					ranking.subimitUpdatedTask(entry);
				}
			}

			mgr.updateData(userId);
			fCreateTime.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean requestFightingGrowthData(String[] arrCommandContents, Player player) {
		com.rwproto.RequestProtos.Request.Builder requestBuilder = com.rwproto.RequestProtos.Request.newBuilder();
		com.rwproto.RequestProtos.RequestHeader.Builder headerBuilder = com.rwproto.RequestProtos.RequestHeader.newBuilder();
		headerBuilder.setCommand(com.rwproto.MsgDef.Command.MSG_FIGHTING_GROWTH_REQUEST_UI_DATA);
		headerBuilder.setUserId(player.getUserId());
		requestBuilder.setHeader(headerBuilder.build());
		com.rwproto.RequestProtos.RequestBody.Builder bodyBuilder = com.rwproto.RequestProtos.RequestBody.newBuilder();
		bodyBuilder.setSerializedContent(com.google.protobuf.ByteString.EMPTY);
		requestBuilder.setBody(bodyBuilder.build());
		return this.assumeSendRequest(player, requestBuilder.build());
	}

	public boolean requestFightingGrowthUpgrade(String[] arrCommandContents, Player player) {
		com.rwproto.RequestProtos.Request.Builder requestBuilder = com.rwproto.RequestProtos.Request.newBuilder();
		com.rwproto.RequestProtos.RequestHeader.Builder headerBuilder = com.rwproto.RequestProtos.RequestHeader.newBuilder();
		headerBuilder.setCommand(com.rwproto.MsgDef.Command.MSG_FIGHTING_GROWTH_REQUEST_UPGRADE);
		headerBuilder.setUserId(player.getUserId());
		requestBuilder.setHeader(headerBuilder.build());
		com.rwproto.RequestProtos.RequestBody.Builder bodyBuilder = com.rwproto.RequestProtos.RequestBody.newBuilder();
		bodyBuilder.setSerializedContent(com.google.protobuf.ByteString.EMPTY);
		requestBuilder.setBody(bodyBuilder.build());
		return this.assumeSendRequest(player, requestBuilder.build());
	}

	public boolean requestGCompSelectionData(String[] arrCommandContents, Player player) {
		com.rwproto.RequestProtos.Request.Builder requestBuilder = com.rwproto.RequestProtos.Request.newBuilder();
		com.rwproto.RequestProtos.RequestHeader.Builder headerBuilder = com.rwproto.RequestProtos.RequestHeader.newBuilder();
		headerBuilder.setCommand(com.rwproto.MsgDef.Command.MSG_GROUP_COMPETITION_GET_DATA);
		headerBuilder.setUserId(player.getUserId());
		requestBuilder.setHeader(headerBuilder.build());
		com.rwproto.RequestProtos.RequestBody.Builder bodyBuilder = com.rwproto.RequestProtos.RequestBody.newBuilder();
		bodyBuilder.setSerializedContent(com.rwproto.GroupCompetitionProto.CommonGetDataReqMsg.newBuilder().setReqType(GCRequestType.GetSelectionData).build().toByteString());
		requestBuilder.setBody(bodyBuilder.build());
		return this.assumeSendRequest(player, requestBuilder.build());
	}

	public boolean requestGCompMatchData(String[] arrCommandContents, Player player) {
		com.rwproto.RequestProtos.Request.Builder requestBuilder = com.rwproto.RequestProtos.Request.newBuilder();
		com.rwproto.RequestProtos.RequestHeader.Builder headerBuilder = com.rwproto.RequestProtos.RequestHeader.newBuilder();
		headerBuilder.setCommand(com.rwproto.MsgDef.Command.MSG_GROUP_COMPETITION_GET_DATA);
		headerBuilder.setUserId(player.getUserId());
		requestBuilder.setHeader(headerBuilder.build());
		com.rwproto.RequestProtos.RequestBody.Builder bodyBuilder = com.rwproto.RequestProtos.RequestBody.newBuilder();
		bodyBuilder.setSerializedContent(com.rwproto.GroupCompetitionProto.CommonGetDataReqMsg.newBuilder().setReqType(GCRequestType.GetMatchView).build().toByteString());
		requestBuilder.setBody(bodyBuilder.build());
		return this.assumeSendRequest(player, requestBuilder.build());
	}

	public boolean requestGroupScoreRank(String[] arrCommandContents, Player player) {
		com.rwproto.RequestProtos.Request.Builder requestBuilder = com.rwproto.RequestProtos.Request.newBuilder();
		com.rwproto.RequestProtos.RequestHeader.Builder headerBuilder = com.rwproto.RequestProtos.RequestHeader.newBuilder();
		headerBuilder.setCommand(com.rwproto.MsgDef.Command.MSG_GROUP_COMPETITION_GET_DATA);
		headerBuilder.setUserId(player.getUserId());
		requestBuilder.setHeader(headerBuilder.build());
		com.rwproto.RequestProtos.RequestBody.Builder bodyBuilder = com.rwproto.RequestProtos.RequestBody.newBuilder();
		bodyBuilder.setSerializedContent(com.rwproto.GroupCompetitionProto.CommonGetDataReqMsg.newBuilder().setReqType(GCRequestType.GetGroupScoreRank).build().toByteString());
		requestBuilder.setBody(bodyBuilder.build());
		return this.assumeSendRequest(player, requestBuilder.build());
	}

	public boolean requestGroupNewestScore(String[] arrCommandContents, Player player) {
		int matchId = Integer.parseInt(arrCommandContents[0]);
		com.rwproto.RequestProtos.Request.Builder requestBuilder = com.rwproto.RequestProtos.Request.newBuilder();
		com.rwproto.RequestProtos.RequestHeader.Builder headerBuilder = com.rwproto.RequestProtos.RequestHeader.newBuilder();
		headerBuilder.setCommand(com.rwproto.MsgDef.Command.MSG_GROUP_COMPETITION_GET_DATA);
		headerBuilder.setUserId(player.getUserId());
		requestBuilder.setHeader(headerBuilder.build());
		com.rwproto.RequestProtos.RequestBody.Builder bodyBuilder = com.rwproto.RequestProtos.RequestBody.newBuilder();
		bodyBuilder.setSerializedContent(com.rwproto.GroupCompetitionProto.CommonGetDataReqMsg.newBuilder().setReqType(GCRequestType.GetNewestScore).setMatchId(matchId).build().toByteString());
		requestBuilder.setBody(bodyBuilder.build());
		return this.assumeSendRequest(player, requestBuilder.build());
	}

	public boolean enterPrepareArea(String[] arrCommandContents, Player player) {
		com.rwproto.RequestProtos.Request.Builder requestBuilder = com.rwproto.RequestProtos.Request.newBuilder();
		com.rwproto.RequestProtos.RequestHeader.Builder headerBuilder = com.rwproto.RequestProtos.RequestHeader.newBuilder();
		headerBuilder.setCommand(com.rwproto.MsgDef.Command.MSG_GROUP_COMPETITION);
		headerBuilder.setUserId(player.getUserId());
		requestBuilder.setHeader(headerBuilder.build());
		com.rwproto.RequestProtos.RequestBody.Builder bodyBuilder = com.rwproto.RequestProtos.RequestBody.newBuilder();
		bodyBuilder.setSerializedContent(com.rwproto.GroupCompetitionProto.CommonReqMsg.newBuilder().setReqType(GCRequestType.EnterPrepareArea).build().toByteString());
		requestBuilder.setBody(bodyBuilder.build());
		return this.assumeSendRequest(player, requestBuilder.build());
	}

	public boolean requestCreateGCompTeam(String[] arrCommandContents, Player player) {
		List<String> heroIds = player.getHeroMgr().getHeroIdList(player);
		if (heroIds.isEmpty()) {
			return false;
		}
		GCRequestType reqType;
		if (arrCommandContents[0].equals("1")) {
			reqType = GCRequestType.CreateTeam;
		} else {
			reqType = GCRequestType.AdjustTeamMember;
		}
		int size = heroIds.size();
		heroIds = new ArrayList<String>(heroIds.subList(0, size > 4 ? 4 : size));
		heroIds.add(player.getUserId());
		com.rwproto.RequestProtos.Request.Builder requestBuilder = com.rwproto.RequestProtos.Request.newBuilder();
		com.rwproto.RequestProtos.RequestHeader.Builder headerBuilder = com.rwproto.RequestProtos.RequestHeader.newBuilder();
		headerBuilder.setCommand(com.rwproto.MsgDef.Command.MSG_GROUP_COMPETITION_TEAM_REQ);
		headerBuilder.setUserId(player.getUserId());
		requestBuilder.setHeader(headerBuilder.build());
		com.rwproto.RequestProtos.RequestBody.Builder bodyBuilder = com.rwproto.RequestProtos.RequestBody.newBuilder();
		bodyBuilder.setSerializedContent(com.rwproto.GroupCompetitionProto.TeamRequest.newBuilder().setReqType(reqType).addAllHeroId(heroIds).build().toByteString());
		requestBuilder.setBody(bodyBuilder.build());
		return this.assumeSendRequest(player, requestBuilder.build());
	}

	public boolean GCompTeamAction(String[] arrCommandContents, Player player) {
		List<String> heroIds = player.getHeroMgr().getHeroIdList(player);
		if (heroIds.isEmpty()) {
			return false;
		}
		int type = Integer.parseInt(arrCommandContents[0]);
		GCRequestType reqType;
		switch (type) {
		case 1:
			reqType = GCRequestType.SetTeamReady;
			break;
		case 2:
			reqType = GCRequestType.CancelTeamReady;
			break;
		default:
		case 3:
			reqType = GCRequestType.StartMatching;
			break;
		}
		int size = heroIds.size();
		heroIds = new ArrayList<String>(heroIds.subList(0, size > 4 ? 4 : size));
		heroIds.add(player.getUserId());
		com.rwproto.RequestProtos.Request.Builder requestBuilder = com.rwproto.RequestProtos.Request.newBuilder();
		com.rwproto.RequestProtos.RequestHeader.Builder headerBuilder = com.rwproto.RequestProtos.RequestHeader.newBuilder();
		headerBuilder.setCommand(com.rwproto.MsgDef.Command.MSG_GROUP_COMPETITION_TEAM_STATUS_REQ);
		headerBuilder.setUserId(player.getUserId());
		requestBuilder.setHeader(headerBuilder.build());
		com.rwproto.RequestProtos.RequestBody.Builder bodyBuilder = com.rwproto.RequestProtos.RequestBody.newBuilder();
		bodyBuilder.setSerializedContent(com.rwproto.GroupCompetitionProto.TeamStatusRequest.newBuilder().setReqType(reqType).build().toByteString());
		requestBuilder.setBody(bodyBuilder.build());
		return this.assumeSendRequest(player, requestBuilder.build());
	}

	/**
	 * 批量添加物品
	 * 
	 * @param arrCommandContents
	 * @param player
	 * @return
	 */
	public boolean addBatchItem(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}

		Map<Integer, Integer> map = HPCUtil.parseIntegerMap(arrCommandContents[0], ",", "_");
		if (map == null || map.isEmpty()) {
			return false;
		}

		List<ItemInfo> itemInfoList = new ArrayList<ItemInfo>(map.size());
		for (Entry<Integer, Integer> e : map.entrySet()) {
			int value = e.getValue().intValue();
			if (value <= 0) {
				continue;
			}

			ItemInfo itemInfo = new ItemInfo();
			itemInfo.setItemID(e.getKey().intValue());
			itemInfo.setItemNum(value);
			itemInfoList.add(itemInfo);
		}

		if (itemInfoList.isEmpty()) {
			return true;
		}

		return ItemBagMgr.getInstance().addItem(player, itemInfoList);
	}

	public boolean emptyBag(String[] arrCommandContents, Player player) {
		List<ItemData> list = new ArrayList<ItemData>();
		String userId = player.getUserId();
		list.addAll(ItemBagMgr.getInstance().getItemListByType(userId, EItemTypeDef.Consume));
		list.addAll(ItemBagMgr.getInstance().getItemListByType(userId, EItemTypeDef.HeroEquip));
		list.addAll(ItemBagMgr.getInstance().getItemListByType(userId, EItemTypeDef.RoleEquip));
		list.addAll(ItemBagMgr.getInstance().getItemListByType(userId, EItemTypeDef.SoulStone));
		list.addAll(ItemBagMgr.getInstance().getItemListByType(userId, EItemTypeDef.Gem));
		List<IUseItem> items = new ArrayList<IUseItem>(list.size());
		List<INewItem> newItems = new ArrayList<INewItem>();
		for (ItemData itemData : list) {
			items.add(new UseItem(itemData.getId(), itemData.getCount()));
		}
		if (items.size() > 0) {
			try {
				ItemBagMgr.getInstance().updateItemBag(player, items, newItems);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return true;
	}

	public boolean addEquipToRole(String[] arrCommandContents, Player player) {
		Map<Integer, int[]> map = new HashMap<Integer, int[]>();
		map.put(0, new int[] { 700112, 700037, 700061, 700148, 700174, 700091 });
		map.put(1, new int[] { 700113, 700038, 700062, 700149, 700174, 700091 });
		map.put(2, new int[] { 700114, 700039, 700063, 700149, 700175, 700091 });
		map.put(3, new int[] { 700115, 700040, 700064, 700150, 700175, 700092 });
		map.put(4, new int[] { 700116, 700041, 700065, 700151, 700176, 700092 });
		map.put(5, new int[] { 700117, 700042, 700065, 700151, 700176, 700092 });
		map.put(6, new int[] { 700117, 700042, 700066, 700153, 700176, 700093 });
		map.put(7, new int[] { 700118, 700043, 700066, 700153, 700177, 700093 });
		map.put(8, new int[] { 700118, 700043, 700067, 700153, 700177, 700093 });
		map.put(9, new int[] { 700119, 700044, 700067, 700153, 700177, 700093 });
		map.put(10, new int[] { 700119, 700044, 700068, 700154, 700177, 700094 });
		map.put(11, new int[] { 700120, 700045, 700068, 700154, 700178, 700094 });
		map.put(12, new int[] { 700120, 700045, 700069, 700154, 700178, 700094 });
		map.put(13, new int[] { 700121, 700046, 700069, 700155, 700178, 700094 });
		map.put(14, new int[] { 700121, 700046, 700070, 700155, 700178, 700095 });
		map.put(15, new int[] { 700122, 700047, 700070, 700155, 700179, 700095 });
		map.put(16, new int[] { 700122, 700047, 700071, 700156, 700179, 700095 });
		map.put(17, new int[] { 700123, 700048, 700071, 700156, 700180, 700095 });
		map.put(18, new int[] { 700123, 700048, 700072, 700156, 700180, 700095 });
		try {
			int quality = Integer.parseInt(arrCommandContents[0]);
			int[] equips = map.get(quality);
			List<ItemInfo> list = new ArrayList<ItemInfo>(equips.length);
			for (int i = 0; i < equips.length; i++) {
				ItemInfo itemInfo = new ItemInfo();
				itemInfo.setItemID(equips[i]);
				itemInfo.setItemNum(1);
				list.add(itemInfo);
			}
			ItemBagMgr.getInstance().addItem(player, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean moveGroupCompStage(String[] arrCommandContents, Player player) {
		GameWorldFactory.getGameWorld().asynExecute(new Runnable() {

			@Override
			public void run() {
				try {
					Field wheelField = com.rwbase.common.timer.core.FSGameTimer.class.getDeclaredField("_wheel");
					Field taskField = com.rwbase.common.timer.core.FSGameTimeSignal.class.getDeclaredField("_task");
					Field timerInstanceField = com.rwbase.common.timer.core.FSGameTimerMgr.class.getDeclaredField("_timerInstance");
					Field consumerField = com.playerdata.groupcompetition.util.GCompCommonTask.class.getDeclaredField("_task");
					wheelField.setAccessible(true);
					taskField.setAccessible(true);
					consumerField.setAccessible(true);
					timerInstanceField.setAccessible(true);
					com.playerdata.groupcompetition.util.GCompStageType stageType = com.playerdata.groupcompetition.GroupCompetitionMgr.getInstance().getCurrentStageType();
					com.playerdata.groupcompetition.util.GCompEventsStatus eventStatus = com.playerdata.groupcompetition.GroupCompetitionMgr.getInstance().getCurrentEventsStatus();
					boolean isEvents = stageType == com.playerdata.groupcompetition.util.GCompStageType.EVENTS;
					boolean isNoneStatus = (eventStatus == com.playerdata.groupcompetition.util.GCompEventsStatus.NONE || eventStatus == com.playerdata.groupcompetition.util.GCompEventsStatus.FINISH);
					@SuppressWarnings("unchecked")
					Set<com.rwbase.common.timer.core.FSGameTimeSignal>[] wheel = (Set<com.rwbase.common.timer.core.FSGameTimeSignal>[]) wheelField.get(timerInstanceField.get(com.rwbase.common.timer.core.FSGameTimerMgr.getInstance()));
					List<com.rwbase.common.timer.core.FSGameTimeSignal> list = new ArrayList<com.rwbase.common.timer.core.FSGameTimeSignal>();
					Class<?> taskClazz = com.playerdata.groupcompetition.util.GCompCommonTask.class;
					List<com.rwbase.common.timer.core.FSGameTimeSignal> stageList = new ArrayList<com.rwbase.common.timer.core.FSGameTimeSignal>();
					List<Set<com.rwbase.common.timer.core.FSGameTimeSignal>> stageSet = new ArrayList<Set<com.rwbase.common.timer.core.FSGameTimeSignal>>();
					outter: for (int i = 0, length = wheel.length; i < length; i++) {
						Set<com.rwbase.common.timer.core.FSGameTimeSignal> set = wheel[i];
						for (Iterator<com.rwbase.common.timer.core.FSGameTimeSignal> itr = set.iterator(); itr.hasNext();) {
							com.rwbase.common.timer.core.FSGameTimeSignal timeSignal = itr.next();
							Object obj = taskField.get(timeSignal);
							if (obj.getClass().equals(taskClazz)) {
								Object consumerObj = consumerField.get(obj);
								String consumerName = consumerObj.getClass().getName();
								if (isEvents) {
									if (consumerName.contains("EventStatusSwitcher")) {
										// 具体赛事状态的切换器
										list.add(timeSignal);
										itr.remove();
										break outter;
									} else if (consumerName.contains("EventsTypeSwitcher")) {
										// 赛事类型切换
										list.add(timeSignal);
										itr.remove();
										break outter;
									} else if (isNoneStatus && consumerName.contains("StageEndMonitorConsumer")) {
										stageList.add(timeSignal);
										stageSet.add(set);
									} else if (isNoneStatus && consumerName.contains("StageStartConsumer")) {
										stageList.add(timeSignal);
										stageSet.add(set);
									}
								} else {
									if (consumerName.contains("StageStartConsumer")) {
										list.add(timeSignal);
										itr.remove();
									} else if (consumerName.contains("StageEndMonitorConsumer")) {
										list.add(0, timeSignal);
										itr.remove();
									}
									if (list.size() == 2) {
										break outter;
									}
								}
							}
						}
					}
					if (list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							com.rwbase.common.timer.core.FSGameTimeSignal timeSignal = list.get(i);
							timeSignal.getTask().onTimeSignal(timeSignal);
						}
					} else if (stageList.size() > 0) {
						for (int i = 0; i < stageList.size(); i++) {
							com.rwbase.common.timer.core.FSGameTimeSignal timeSignal = stageList.get(i);
							timeSignal.getTask().onTimeSignal(timeSignal);
							stageSet.get(i).remove(timeSignal);
						}
					}
					wheelField.setAccessible(false);
					taskField.setAccessible(false);
					consumerField.setAccessible(false);
					timerInstanceField.setAccessible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return true;
	}

	public boolean sendGroupPmd(String[] arrCommandContents, Player player) {
		String index = arrCommandContents[0];
		if (index.equals("1")) {
			MainMsgHandler.getInstance().sendMainCityMsg(16, EMsgType.GroupCompetitionMsg, Arrays.asList("歐盟", "荷蘭", "100"));
		}
		if (index.equals("2")) {
			MainMsgHandler.getInstance().sendMainCityMsg(24, EMsgType.PmdMsg, Arrays.asList("Fisher", "3", "随机boss"));
		}
		if (index.equals("3")) {
			MainMsgHandler.getInstance().sendMainCityMsg(2, EMsgType.PmdMsg, Arrays.asList("Fisher", "203007_5"));
		}
		return true;
	}

	public boolean refreshGroupFightingRank(String[] arrCommandContents, Player player) {
		GCompFightingRankMgr.refreshGroupFightingRank();
		return true;
	}

	public boolean refreshGCompFighting(String[] arrCommandContents, Player player) {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					com.playerdata.groupcompetition.util.GCompUpdateFightingTask temp = new com.playerdata.groupcompetition.util.GCompUpdateFightingTask();
					Method m = temp.getClass().getDeclaredMethod("refreshGroupFighting");
					m.setAccessible(true);
					m.invoke(temp);
					m.setAccessible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		GameWorldFactory.getGameWorld().asynExecute(r);
		return true;
	}

	public boolean gCompGroupAction(String[] arrCommandContents, Player player) {
		UserGroupAttributeData userGroupData = UserGroupAttributeDataDAO.getDAO().getUserGroupAttributeData(player.getUserId());
		if (userGroupData.getGroupId() != null && userGroupData.getGroupId().length() > 0) {
			return true;
		}
		String groupName = arrCommandContents[0];
		String groupId = GroupBM.getInstance().getGroupId(groupName);
		if (groupId != null) {
			return GCGMHandler.getHandler().joinGroup(arrCommandContents, player);
		} else {
			return GCGMHandler.getHandler().createGroup(arrCommandContents, player);
		}
	}

	public boolean gCompCheckIfLeader(String[] arrCommandContents, Player player) {
		return GCGMHandler.getHandler().checkIfLeader(arrCommandContents, player);
	}

	public boolean gCompCheckTimes(String[] arrCommandContents, Player player) {
		return GCGMHandler.getHandler().isCheckTimesMatch(arrCommandContents, player);
	}

	public boolean testCharge(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length != 1) {
			return false;
		}
		Integer status = Integer.valueOf(arrCommandContents[0]);
		ServerSwitch.setTestCharge(status == 1);
		return true;
	}

	public boolean emptyAccount(String[] arrCommandContents, Player player) {
		long coin = player.getUserGameDataMgr().getCoin();
		int max = Integer.MAX_VALUE;
		if (coin < max) {
			player.getUserGameDataMgr().addCoin(-(int) coin);
		} else {
			while (coin > 0) {
				int value = coin > max ? max : (int) coin;
				player.getUserGameDataMgr().addCoin(-value);
				coin -= max;
			}
		}
		long gold = player.getUserGameDataMgr().getGold();
		if (gold < max) {
			player.getUserGameDataMgr().addGold(-(int) gold);
		} else {
			while (gold > 0) {
				int value = coin > max ? max : (int) gold;
				player.getUserGameDataMgr().addGold(-value);
				gold -= max;
			}
		}
		return true;
	}

	public boolean addAllMagicPieces(String[] arrCommandContents, Player player) {
		// List<Integer> allPieceIds = Arrays.asList(604901, 604902, 604903, 604904, 604905, 604906, 604907, 604908, 604909, 604910, 604911, 604912, 604913, 604914, 604915, 604916, 604917, 604918,
		// 604919, 604920, 604921, 604922, 604923, 604924, 604925, 604926, 604927);
		List<MagicCfg> magicCfgList = MagicCfgDAO.getInstance().getAllCfg();
		List<Integer> newMagicCfgIds = new ArrayList<Integer>();
		for (MagicCfg cfg : magicCfgList) { // 这里包含了新旧法宝
			if (cfg.getFirstAptitude() > 0) {
				// 新版的才会 > 0
				newMagicCfgIds.add(cfg.getId());
			}
		}
		List<Integer> piecesIds = new ArrayList<Integer>(newMagicCfgIds.size());
		for (MagicCfg cfg : magicCfgList) {
			if (newMagicCfgIds.contains(cfg.getComposeItemID())) {
				piecesIds.add(cfg.getId());
			}
		}
		List<ItemInfo> itemInfos = new ArrayList<ItemInfo>(piecesIds.size());
		for (Integer pieceId : piecesIds) {
			itemInfos.add(new ItemInfo(pieceId, 999));
		}
		ItemBagMgr.getInstance().addItem(player, itemInfos);
		return true;
	}

	public boolean growthFund(String[] arrCommandContents, Player player) {
		int iReqType = Integer.parseInt(arrCommandContents[0]);
		com.rwproto.GrowthFundServiceProto.EGrowthFundRequestType reqType;
		com.rwproto.GrowthFundServiceProto.GrowthFundRequest.Builder reqBuilder = com.rwproto.GrowthFundServiceProto.GrowthFundRequest.newBuilder();
		switch (iReqType) {
		default:
		case com.rwproto.GrowthFundServiceProto.EGrowthFundRequestType.BUY_GROWTH_FUND_VALUE:
			reqType = com.rwproto.GrowthFundServiceProto.EGrowthFundRequestType.BUY_GROWTH_FUND;
			break;
		case com.rwproto.GrowthFundServiceProto.EGrowthFundRequestType.GET_GROWTH_FUND_GIFT_VALUE:
			reqType = com.rwproto.GrowthFundServiceProto.EGrowthFundRequestType.GET_GROWTH_FUND_GIFT;
			reqBuilder.setRequestId(Integer.parseInt(arrCommandContents[1]));
			break;
		case com.rwproto.GrowthFundServiceProto.EGrowthFundRequestType.GET_GROWTH_FUND_REWARD_VALUE:
			reqType = com.rwproto.GrowthFundServiceProto.EGrowthFundRequestType.GET_GROWTH_FUND_REWARD;
			reqBuilder.setRequestId(Integer.parseInt(arrCommandContents[1]));
			break;
		}
		reqBuilder.setReqType(reqType);
		RequestHeader.Builder headerBuilder = RequestHeader.newBuilder().setCommand(Command.MSG_BUY_GROWTH_FUND);
		RequestBody.Builder bodyBuilder = RequestBody.newBuilder().setSerializedContent(reqBuilder.build().toByteString());
		Request request = Request.newBuilder().setHeader(headerBuilder).setBody(bodyBuilder).build();
		this.assumeSendRequest(player, request);
		return true;
	}

	public boolean setGrowthFundBoughtCount(String[] arrCommandContents, Player player) {
		com.playerdata.activity.growthFund.GrowthFundGlobalData data = com.playerdata.activity.growthFund.data.ActivityGrowthFundItemHolder.getInstance().getGlobalData();
		data.setAlreadyBoughtCount(Integer.parseInt(arrCommandContents[0]));
		return true;
	}

	public boolean exchangeSoul(String[] arrCommandContents, Player player) {
		com.rwproto.RequestProtos.Request.Builder requestBuilder = com.rwproto.RequestProtos.Request.newBuilder();
		com.rwproto.RequestProtos.RequestHeader.Builder headerBuilder = com.rwproto.RequestProtos.RequestHeader.newBuilder();
		headerBuilder.setCommand(com.rwproto.MsgDef.Command.MSG_COMMON_SOUL);
		headerBuilder.setUserId(player.getUserId());
		requestBuilder.setHeader(headerBuilder.build());
		com.rwproto.RequestProtos.RequestBody.Builder bodyBuilder = com.rwproto.RequestProtos.RequestBody.newBuilder();
		bodyBuilder.setSerializedContent(com.rwproto.CommonSoulServiceProto.CommonSoulRequest.newBuilder().setRequestType(com.rwproto.CommonSoulServiceProto.RequestType.exchange).setSoulItemId(Integer.parseInt(arrCommandContents[0])).setExchangeCount(Integer.parseInt(arrCommandContents[1])).build()
				.toByteString());
		requestBuilder.setBody(bodyBuilder.build());
		return this.assumeSendRequest(player, requestBuilder.build());
	}

	private boolean resetCopy(Player player, int type) {
		TableCopyData tableCopyData = TableCopyDataDAO.getInstance().get(player.getUserId());
		List<CopyData> copyList = tableCopyData.getCopyList();
		List<CopyInfoCfgIF> cfgList = CopyDataMgr.getSameDayInfoList();
		for (CopyData cd : copyList) {
			if (cd.getCopyType() == type) {
				int copyType = cd.getCopyType();
				for (CopyInfoCfgIF cfg : cfgList) {
					if (copyType == cfg.getType()) {
						cd.setCopyCount(cfg.getCount());
						break;
					}
				}
			}
		}
		return true;
	}

	private boolean resetCopyCd(Player player, int type) {
		TableCopyData tableCopyData = TableCopyDataDAO.getInstance().get(player.getUserId());
		List<CopyData> copyList = tableCopyData.getCopyList();
		for (CopyData cd : copyList) {
			if (cd.getCopyType() == type) {
				cd.setLastChallengeTime(0);
			}
		}
		return true;
	}

	public boolean resetJBZD(String[] arrCommandContents, Player player) {
		return this.resetCopy(player, CopyType.COPY_TYPE_TRIAL_JBZD);
	}

	public boolean resetJBZDCD(String[] arrCommandContents, Player player) {
		return this.resetCopyCd(player, CopyType.COPY_TYPE_TRIAL_JBZD);
	}

	public boolean resetLQSG(String[] arrCommandContents, Player player) {
		return this.resetCopy(player, CopyType.COPY_TYPE_TRIAL_LQSG);
	}

	public boolean resetLQSGCD(String[] arrCommandContents, Player player) {
		return this.resetCopyCd(player, CopyType.COPY_TYPE_TRIAL_LQSG);
	}

	public boolean resetQKHJ(String[] arrCommandContents, Player player) {
		return this.resetCopy(player, CopyType.COPY_TYPE_WARFARE);
	}

	public boolean resetQKHJCD(String[] arrCommandContents, Player player) {
		return this.resetCopyCd(player, CopyType.COPY_TYPE_WARFARE);
	}

	public boolean resetSCLJ(String[] arrCommandContents, Player player) {
		return this.resetCopy(player, CopyType.COPY_TYPE_CELESTIAL);
	}

	public boolean resetSCLJCD(String[] arrCommandContents, Player player) {
		return this.resetCopyCd(player, CopyType.COPY_TYPE_CELESTIAL);
	}

	public boolean resetWXZ(String[] arrCommandContents, Player player) {
		return this.resetCopy(player, CopyType.COPY_TYPE_TOWER);
	}

	public boolean resetWXZCD(String[] arrCommandContents, Player player) {
		return this.resetCopyCd(player, CopyType.COPY_TYPE_TOWER);
	}

	public boolean resetFST(String[] arrCommandContents, Player player) {
		return this.resetCopy(player, CopyType.COPY_TYPE_BATTLETOWER);
	}

	public boolean resetFSTCD(String[] arrCommandContents, Player player) {
		return this.resetCopyCd(player, CopyType.COPY_TYPE_BATTLETOWER);
	}

	public boolean sendWorldChat(String[] arrCommandContents, Player player) {
		String targetUserId = arrCommandContents[0];
		Player target = PlayerMgr.getInstance().find(targetUserId);
		ChatMessageData.Builder chatMsgBuilder = ChatMessageData.newBuilder();
		String str = String.valueOf(System.currentTimeMillis());
		chatMsgBuilder.setMessage(target.getUserName() + str.substring(str.length() - 6));
		MsgChatRequest.Builder chatRequestBuilder = MsgChatRequest.newBuilder();
		chatRequestBuilder.setChatMessageData(chatMsgBuilder.build());
		chatRequestBuilder.setChatType(eChatType.CHANNEL_WORLD);
		ChatHandler.getInstance().chatWorld(target, chatRequestBuilder.build());
		return true;
	}

	public boolean sendOneHundredEmails(String[] arrCommandContents, Player player) {
		GameWorldFactory.getGameWorld().asyncExecute(player.getUserId(), new PlayerTask() {

			@Override
			public void run(Player e) {
				Calendar instance = Calendar.getInstance();
				instance.add(Calendar.DAY_OF_YEAR, 7);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				String deadline = sdf.format(instance.getTime());
				for (int i = 0; i < 100; i++) {
					EmailData emailData = new EmailData();
					emailData.setTitle("标题_" + (1000 + i));
					emailData.setContent("邮件内容：" + (1000 + i));
					emailData.setSender("神探夏洛克");
					emailData.setCheckIcon("btn_YouJian_h");
					emailData.setSubjectIcon("btn_YouJian_n");
					emailData.setDeleteType(EEmailDeleteType.GET_DELETE);
					emailData.setDeadlineTime(deadline);
					emailData.setCfgid("10001");
					emailData.setEmailAttachment("1~100,2~100,5~100,3~100");
					EmailUtils.sendEmail(e.getUserId(), emailData);
					try {
						TimeUnit.MILLISECONDS.sleep(5);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		return true;
	}

	/**
	 * 修改活动配置的时间
	 * 
	 * @param arrCommandContents
	 * @param player
	 * @return
	 */
	public boolean setCfgTime(String[] arrCommandContents, Player player) {
		if (arrCommandContents.length < 4) {
			return false;
		}
		try {
			int cfgId = Integer.parseInt(arrCommandContents[0]);
			String startTime = arrCommandContents[1];
			String endTime = arrCommandContents[2];
			int version = Integer.parseInt(arrCommandContents[3]);
			ActivityModifyMgr.getInstance().gmSetCfgTime(cfgId, startTime, endTime, version);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 修改活动配置的奖励
	 * 
	 * @param arrCommandContents
	 * @param player
	 * @return
	 */
	public boolean setCfgReward(String[] arrCommandContents, Player player) {
		if (arrCommandContents.length < 4) {
			return false;
		}
		try {
			int cfgId = Integer.parseInt(arrCommandContents[0]);
			int subCfgId = Integer.parseInt(arrCommandContents[1]);
			String reward = arrCommandContents[2];
			int version = Integer.parseInt(arrCommandContents[3]);
			ActivityModifyMgr.getInstance().gmSetSubCfgReward(cfgId, subCfgId, reward, version);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean addGroupDonateAndExp(String[] arrCommandContents, Player player) {
		int groupSupply = Integer.parseInt(arrCommandContents[0]);
		int groupExp = 0;
		if (arrCommandContents.length > 1) {
			groupExp = Integer.parseInt(arrCommandContents[1]);
		}
		Group group = GroupBM.getInstance().get(GroupHelper.getInstance().getGroupId(player));
		if (group != null) {
			GroupBaseData groupBaseData = (GroupBaseData) group.getGroupBaseDataMgr().getGroupData();
			groupBaseData.setSupplies(groupBaseData.getSupplies() + groupSupply);
			groupBaseData.setDaySupplies(groupBaseData.getDaySupplies() + groupSupply);
			if (groupExp > 0) {
				groupBaseData.setGroupExp(groupBaseData.getGroupExp() + groupExp);
				groupBaseData.setDayExp(groupBaseData.getDayExp() + groupExp);
			}
			group.getGroupBaseDataMgr().updateAndSynGroupData(player);
			// 更新帮派排行榜属性
			GroupRankHelper.getInstance().addOrUpdateGroup2BaseRank(group);
			return true;
		} else {
			return false;
		}
	}

	public boolean addPersonalContribute(String[] arrCommandContents, Player player) {
		Group group = GroupBM.getInstance().get(GroupHelper.getInstance().getGroupId(player));
		if (group != null) {
			int contribute = Integer.parseInt(arrCommandContents[0]);
			UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
			group.getGroupMemberMgr().updateMemberDataWhenDonate(player.getUserId(), baseData.getDonateTimes() + 1, System.currentTimeMillis(), contribute, true);
		}
		return true;
	}

	public boolean reCal(String[] arrCommandContents, Player player) {
		List<Hero> heros = FSHeroMgr.getInstance().getAllHeros(player, null);
		for (Hero h : heros) {
			h.getAttrMgr().reCal();
		}
		return true;
	}

	/**
	 * 重置万仙阵战力
	 * 
	 * @param arrCommandContents
	 * @param player
	 * @return
	 */
	private boolean resetWxFighting(Player player) {
		TableAngelArrayData angleArrayData = player.getTowerMgr().getAngleArrayData();

		angleArrayData.setResetTime(System.currentTimeMillis());// 设置重置个人数据的时间
		// 从昨日竞技排行榜拿数据
		angleArrayData.setResetLevel(player.getLevel());// 竞技阵容出现的最高等级
		angleArrayData.setResetRankIndex(0);// 竞技排名
		// 从昨日榜中获取自己的战力
		Ranking rank = RankingFactory.getRanking(RankType.TEAM_FIGHTING_DAILY);
		// 获取到佣兵要用于匹配的总战力
		int totalFighting = -1;
		if (rank != null) {
			RankingEntry rankingEntry = rank.getRankingEntry(player.getUserId());
			if (rankingEntry != null) {
				RankingLevelData att = (RankingLevelData) rankingEntry.getExtendedAttribute();
				if (att != null) {
					totalFighting = att.getFightingTeam();
				}
			}
		}

		if (totalFighting == -1) {
			List<Hero> allHeros = player.getHeroMgr().getAllHeros(player, comparator);

			// 要看一下总共要获取多少个佣兵的战力
			int maxSize = AngelArrayConst.MAX_HERO_FIGHTING_SIZE + 1;// 包括主要角色在内的佣兵数据
			maxSize = maxSize > allHeros.size() ? allHeros.size() : maxSize;

			// 获取到佣兵要用于匹配的总战力
			for (int i = 0; i < maxSize; i++) {
				totalFighting += allHeros.get(i).getFighting();
			}
		}

		angleArrayData.setResetFighting(totalFighting);
		AngelArrayDataDao.getDao().update(player.getUserId());

		return true;
	}

	/**
	 * 获取英雄的排序方法
	 */
	private static final Comparator<Hero> comparator = new Comparator<Hero>() {

		@Override
		public int compare(Hero h1, Hero h2) {
			// 主角始终是排在最前边的
			int rType1 = h1.getRoleType().ordinal();
			int rType2 = h2.getRoleType().ordinal();
			if (rType1 < rType2) {
				return -1;
			} else if (rType1 > rType2) {
				return 1;
			}
			// 佣兵的个人战力，谁大谁在前
			int f1 = h1.getFighting();
			int f2 = h2.getFighting();
			return f2 - f1;
		}
	};
}