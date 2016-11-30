package com.rw.service.gm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.bm.chat.ChatInteractiveType;
import com.bm.group.GroupBM;
import com.bm.group.GroupBaseDataMgr;
import com.bm.group.GroupMemberMgr;
import com.bm.serverStatus.ServerStatusMgr;
import com.google.protobuf.ByteString;
import com.groupCopy.bm.GroupHelper;
import com.log.GameLog;
import com.playerdata.BattleTowerMgr;
import com.playerdata.FashionMgr;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.TowerMgr;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.groupFightOnline.state.GFightStateTransfer;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.rw.fsutil.cacheDao.CfgCsvReloader;
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
import com.rw.service.gamble.datamodel.GambleDropCfgHelper;
import com.rw.service.gamble.datamodel.GamblePlanCfgHelper;
import com.rw.service.gamble.datamodel.HotGambleCfgHelper;
import com.rw.service.gm.fixequip.GMAddFixEquip;
import com.rw.service.gm.hero.GMHeroProcesser;
import com.rw.service.guide.DebugNewGuideData;
import com.rw.service.guide.datamodel.GiveItemCfgDAO;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.common.enu.eStoreConditionType;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.anglearray.pojo.db.TableAngleArrayData;
import com.rwbase.dao.battletower.pojo.db.TableBattleTower;
import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerDao;
import com.rwbase.dao.copy.cfg.MapCfg;
import com.rwbase.dao.copy.cfg.MapCfgDAO;
import com.rwbase.dao.fashion.FashionBuyRenewCfgDao;
import com.rwbase.dao.fashion.FashionCommonCfgDao;
import com.rwbase.dao.fashion.FashionEffectCfgDao;
import com.rwbase.dao.fashion.FashionQuantityEffectCfgDao;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.item.pojo.itembase.INewItem;
import com.rwbase.dao.item.pojo.itembase.NewItem;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.setting.HeadBoxCfgDAO;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.GMServiceProtos.MsgGMRequest;
import com.rwproto.GMServiceProtos.MsgGMResponse;
import com.rwproto.GMServiceProtos.eGMResultType;
import com.rwproto.GuidanceProgressProtos.GuidanceConfigs;
import com.rwproto.MsgDef.Command;

public class GMHandler {
	private HashMap<String, String> funcCallBackMap = new HashMap<String, String>();
	private static GMHandler instance = new GMHandler();
	// 是否激活gm指令
	private boolean active = false;

	private GMHandler() {
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
		
		// 设置帮战阶段
		funcCallBackMap.put("setgfstate", "setGFightState");
		funcCallBackMap.put("setgfauto", "setGFightAutoState");
		
		//添加帮派物资
		funcCallBackMap.put("setgp", "SetGroupSupplier");
		//添加帮派副本战斗次数    * setgbf 1000
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
	
	public boolean SetGroupSupplier(String[] comd, Player player){
		boolean result = true;
		Group group = GroupHelper.getGroup(player);
		if(group == null){
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
			result = result && reloadConfigByHelperClass(arrCommandContents[i]);
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
		return mgr.giveFashionItem(fashionId,minutes,false,true,TimeUnit.MINUTES);
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
			player.getItemBagMgr().addItem(itemId, itemNum);
			// }
			return true;
		}
		return false;
	}

	/*
	 * 设定副本地图的通关关卡
	 */
	public boolean setMap(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			player.NotifyCommonMsg("命令有误，请重新输入");
			return false;
		}
		String id = arrCommandContents[0];
		if (id == null) {
			player.NotifyCommonMsg("mapid有错");
			return false;
		}
		MapCfg map = (MapCfg) MapCfgDAO.getInstance().getCfgById(id);
		if (map == null) {
			player.NotifyCommonMsg("mapid有错");
			return false;
		}

		MsgCopyResponse.Builder copyResponse = player.getCopyRecordMgr().setMapByGM(map);// 获取要新增的关卡...
		player.SendMsg(Command.MSG_CopyService, copyResponse.build().toByteString());
		return true;
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
			player.getHeroMgr().addHero(heroId);
			return true;
		}
		return false;
	}

	public boolean addFixEquipItem(String[] arrCommandContents,Player player){
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (player != null) {
			GMAddFixEquip.addStarUp(player);
			GMAddFixEquip.addexp(player);
			GMAddFixEquip.addqualityUp(player);
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
	
	public boolean addSecretKeycount(String[] arrCommandContents, Player player){
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
	
	
	
	
	public boolean addWakenPiece(String[] arrCommandContents, Player player){
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
	
	public boolean addWakenKey(String[] arrCommandContents, Player player){
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
			ChargeMgr.getInstance().buyMonthCard(player, null);
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
				player.getItemBagMgr().removeAllItems();
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

	public boolean recharge(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		String itemId = arrCommandContents[0];
		if (player != null) {
			ChargeMgr.getInstance().charge(player, itemId);
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
			player.SetLevel(Integer.parseInt(newLevel));
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
		if (arrCommandContents == null || arrCommandContents.length < 2) {
			System.out.println(" command param not right ...");
			return false;
		}
		EmailUtils.sendEmail(player.getUserId(), arrCommandContents[0], arrCommandContents[1]);
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
				player.getHeroMgr().AddAllHeroExp(addExp);
				return true;
			}
			return false;
		}

		int heroId = Integer.parseInt(arrCommandContents[0]);
		long addExp = Long.parseLong(arrCommandContents[1]);
		if (player != null) {
			player.getHeroMgr().getHeroByModerId(heroId).addHeroExp(addExp);
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
			hero = player.getHeroMgr().getHeroByModerId(Integer.parseInt(heroId));
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

		return player.getItemBagMgr().useLikeBoxItem(null, addItemList);
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
			hero = player.getHeroMgr().getHeroByModerId(Integer.parseInt(heroId));
		}

		if (hero == null) {
			return false;
		}

		hero.getEquipMgr().orderHeroWearEquip(hero);
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
			TableAngleArrayData angleArrayData = towerMgr.getAngleArrayData();
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
			UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
			String groupId = userGroupAttributeData.getGroupId();
			if (StringUtils.isEmpty(groupId)) {
				return false;
			}

			Group group = GroupBM.get(groupId);
			if (group == null) {
				return false;
			}

			GroupMemberMgr groupMemberMgr = group.getGroupMemberMgr();
			groupMemberMgr.resetMemberDataDonateTimes(player.getUserId(), System.currentTimeMillis());
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

		Group group = GroupBM.get(groupId);
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

			int su = memberData.getContribution() + value;
			if (su < 0) {
				return false;
			}
			groupMemberMgr.updateMemberContribution(userId, value, true);
		} else if (functionName.equalsIgnoreCase("exp")) {// 增加帮派经验
			if (value <= 0) {
				return false;
			}

			groupBaseDataMgr.updateGroupDonate(player, group.getGroupLogMgr(), 0, value, 0, true);
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
		try {
			java.lang.reflect.Field fUserChannelMap = com.rw.netty.UserChannelMgr.class.getDeclaredField("userChannelMap");
			fUserChannelMap.setAccessible(true);
			@SuppressWarnings("unchecked")
			java.util.Map<String, io.netty.channel.ChannelHandlerContext> map = (java.util.Map<String, io.netty.channel.ChannelHandlerContext>) fUserChannelMap.get(null);
			fUserChannelMap.setAccessible(false);
			io.netty.channel.ChannelHandlerContext ctx = map.get(player.getUserId());
			com.rw.netty.UserSession sessionInfo = com.rw.netty.UserChannelMgr.getUserSession(ctx);
			com.rwproto.RequestProtos.Request.Builder requestBuilder = com.rwproto.RequestProtos.Request.newBuilder();
			com.rwproto.RequestProtos.RequestHeader.Builder headerBuilder = com.rwproto.RequestProtos.RequestHeader.newBuilder();
			headerBuilder.setCommand(com.rwproto.MsgDef.Command.MSG_CHAT_REQUEST_PRIVATE_CHATS);
			headerBuilder.setUserId(player.getUserId());
			requestBuilder.setHeader(headerBuilder.build());
			com.rwproto.RequestProtos.RequestBody.Builder bodyBuilder = com.rwproto.RequestProtos.RequestBody.newBuilder();
			bodyBuilder.setSerializedContent(com.rwproto.ChatServiceProtos.MsgChatRequestPrivateChats.newBuilder().setUserId(targetUserId).build().toByteString());
			requestBuilder.setBody(bodyBuilder.build());
			com.rwbase.gameworld.GameWorldFactory.getGameWorld().asyncExecute(player.getUserId(), new com.rw.controler.GameLogicTask(sessionInfo, requestBuilder.build()));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
			if(type.equals("1")) {
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
			if(type.equals("1")) {
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

	public boolean setGroupBossFightTime(String[] arrcomStrings, Player player){
		
		int count = Integer.parseInt(arrcomStrings[0]);
		if(count <= 0){
			return false;
		}
		
		player.getUserGroupCopyRecordMgr().setRoleBattleTime(count, player);
		return true;
	}
	
	public boolean shutdownServer(String[] arrCommandContents, Player player) {
		com.rw.manager.GameManager.shutdown();
		return true;
	}

	public boolean addDistCount(String[] str, Player player){
		int count = Integer.parseInt(str[0]);
		if(count <= 0){
			return false;
		}
		
		Group group = GroupHelper.getGroup(player);
		if(group != null){
			group.getGroupMemberMgr().resetAllotGroupRewardCount(player.getUserId(),count, false);
		}
		return true;
	}
	
	public boolean addServerStatusTips(String[] arrCommandContents, Player player){
		if (arrCommandContents == null || arrCommandContents.length != 1) {
			return false;
		}
		GFightStateTransfer.getInstance().setAutoCheck(Integer.valueOf(arrCommandContents[0]) == 1);
		return true;
	}
}