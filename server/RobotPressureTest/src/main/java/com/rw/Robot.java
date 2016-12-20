package com.rw;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.PropertyConfigurator;

import com.config.PlatformConfig;
import com.rw.common.RobotLog;
import com.rw.handler.RandomMethodIF;
import com.rw.handler.DailyActivity.DailyActivityHandler;
import com.rw.handler.GroupCopy.GroupCopyHandler;
import com.rw.handler.GroupCopy.GroupCopyMgr;
import com.rw.handler.GroupCopy.data.GroupCopyMapRecord;
import com.rw.handler.activity.ActivityCountHandler;
import com.rw.handler.activity.daily.ActivityDailyCountHandler;
import com.rw.handler.battle.PVEHandler;
import com.rw.handler.battle.PVPHandler;
import com.rw.handler.battletower.BattleTowerHandler;
import com.rw.handler.chat.ChatHandler;
import com.rw.handler.chat.GmHandler;
import com.rw.handler.copy.CopyHandler;
import com.rw.handler.copy.CopyType;
import com.rw.handler.copy.data.CopyHolder;
import com.rw.handler.daily.DailyHandler;
import com.rw.handler.email.EmailHandler;
import com.rw.handler.equip.EquipHandler;
import com.rw.handler.fashion.FashionHandler;
import com.rw.handler.fixEquip.FixEquipHandler;
import com.rw.handler.fixExpEquip.FixExpEquipHandler;
import com.rw.handler.fresheractivity.FresherActivityHandler;
import com.rw.handler.friend.FriendHandler;
import com.rw.handler.gamble.GambleHandler;
import com.rw.handler.gameLogin.GameLoginHandler;
import com.rw.handler.gameLogin.SelectCareerHandler;
import com.rw.handler.giftcode.GiftCodeHandler;
import com.rw.handler.group.GroupBaseHandler;
import com.rw.handler.group.GroupMemberHandler;
import com.rw.handler.group.GroupPersonalHandler;
import com.rw.handler.groupCompetition.service.GroupCompSameSceneHandler;
import com.rw.handler.groupCompetition.service.GroupCompetitionHandler;
import com.rw.handler.groupCompetition.service.GroupCompetitionQuizHandler;
import com.rw.handler.groupFight.service.GroupFightHandler;
import com.rw.handler.groupsecret.GroupSecretHandler;
import com.rw.handler.groupsecret.GroupSecretMatchHandler;
import com.rw.handler.groupsecret.SecretUserInfoSynData;
import com.rw.handler.hero.HeroHandler;
import com.rw.handler.itembag.ItemBagHandler;
import com.rw.handler.itembag.ItemData;
import com.rw.handler.itembag.ItembagHolder;
import com.rw.handler.magic.MagicHandler;
import com.rw.handler.magicSecret.MagicSecretHandler;
import com.rw.handler.mainService.MainHandler;
import com.rw.handler.peakArena.PeakArenaHandler;
import com.rw.handler.platform.PlatformHandler;
import com.rw.handler.sevenDayGift.DailyGiftHandler;
import com.rw.handler.sign.SignHandler;
import com.rw.handler.store.StoreHandler;
import com.rw.handler.taoist.TaoistHandler;
import com.rw.handler.task.TaskHandler;
import com.rw.handler.teamBattle.service.TeamBattleHandler;
import com.rw.handler.worShip.WorShipHandler;
import com.rwproto.CopyServiceProtos.EBattleStatus;
import com.rwproto.GroupCopyAdminProto.RequestType;
import com.rwproto.PeakArenaServiceProtos.ArenaInfo;

/*
 * 机器人入口
 * @author HC
 * @date 2015年12月14日 下午1:58:55
 * @Description 
 */
public class Robot {

	private static boolean isInit = false;

	private String accountId;

	private Client client;

	// private int zoneId;

	private int chatCount;

	private Random random = new Random();

	private static void init() {
		PropertyConfigurator.configureAndWatch("log4j.properties");
		PlatformConfig.InitPlatformConfig();

	}

	public Robot(String accountIdP) {
		this.accountId = accountIdP;
		// Client clientTmp = ClientPool.getByAccountId(accountIdP);
		// if (clientTmp != null) {
		// client = clientTmp;
		// }
	}

	public synchronized static Robot newInstance(String accountIdP) {
		if (!isInit) {
			init();
			isInit = true;
		}
		return new Robot(accountIdP);
	}

	public String getUserId() {
		return client.getUserId();
	}

	// 注册平台帐号
	public boolean regPlatform() {
		if (client == null) {
			client = PlatformHandler.instance().reg(accountId);
		}
		if (client == null) {
			return false;
		}
		boolean loadZoneListSuccess = PlatformHandler.instance().loadZoneAndRoleList(client);
		return loadZoneListSuccess;
	}

	// 登录平台帐号
	public boolean loginPlatform() {
		boolean loadZoneListSuccess = false;
		try {
			if (client == null) {
				client = PlatformHandler.instance().login(accountId);
				try {
					Thread.sleep(1000);
				} catch (Exception ex) {

				}
				if (client != null) {
					loadZoneListSuccess = PlatformHandler.instance().loadZoneAndRoleList(client);
				}
			}
			if (client == null) {
				return false;
			}

			loadZoneListSuccess = client.getServerList() != null && !client.getServerList().isEmpty();

		} catch (Throwable e) {
			RobotLog.fail("loginPlatform error", e);
		}
		return loadZoneListSuccess;
	}

	// 创建游戏角色
	public boolean creatRole() {
		if (client == null) {
			return false;
		}
		int zoneId = getTargetZoneId();
		boolean createSuccess = GameLoginHandler.instance().createRole(client, zoneId);

		return createSuccess;
	}

	// 登录游戏角色
	public boolean loginGame() {
		if (client == null) {
			return false;
		}
		client.closeConnect();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int zoneId = getTargetZoneId();
		boolean createSuccess = false;
		try {

			createSuccess = GameLoginHandler.instance().loginGame(client, zoneId);
		} catch (Exception e) {
			RobotLog.fail("loginGame error", e);
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return createSuccess;
	}

	/**
	 * 聊天
	 * 
	 * @param content
	 * @return
	 */
	public boolean chat(String content) {
		return ChatHandler.instance().send(client, content);
	}

	/**
	 * 抽卡
	 * 
	 * @return
	 */

	public boolean gamble() {
		return GambleHandler.instance().buy(client);

	}

	/** 钻石抽 */
	public boolean gambleByGold() {
		return GambleHandler.instance().buyByGold(client);

	}

	public boolean buyRandom() {
		if (client == null) {
			return false;
		}
		return StoreHandler.instance().buyRandom(client);
	}

	public boolean sellRandom() {
		if (client == null) {
			return false;
		}
		return ItemBagHandler.instance().sellRandom(client);
	}

	public boolean sellItem(int modelId) {
		if (client == null) {
			return false;
		}
		return ItemBagHandler.instance().sellItem(client, modelId);
	}

	public boolean gainItem(int modelId) {
		// 803004
		int count;
		if (client.getItembagHolder().getItemCountByModelId(modelId) <= 0) {
			count = 1 + random.nextInt(10);
		} else {
			count = 1;
		}
		boolean sendSuccess = GmHandler.instance().send(client, "* additem " + modelId + " " + String.valueOf(count));
		return sendSuccess;
	}

	public boolean gainItem(int modelId, int count) {
		// 803004
		boolean sendSuccess = GmHandler.instance().send(client, "* additem " + modelId + " " + count);
		return sendSuccess;
	}

	/**
	 * 作弊添加装备
	 * 
	 * @param heroModelId 如果是0是主角，其他的佣兵要填入具体的模版Id，例如姜子牙就填入202001
	 * @return
	 */
	public boolean gmGainHeroEquip(int heroModelId) {
		boolean sendSuccess = GmHandler.instance().send(client, "* gainHeroEquip " + heroModelId);
		return sendSuccess;
	}

	/**
	 * 作弊穿装备
	 * 
	 * @param heroModelId 如果是0是主角，其他的佣兵要填入具体的模版Id，例如姜子牙就填入202001
	 * @return
	 */
	public boolean gmWearEquip(int heroModelId) {
		boolean sendSuccess = GmHandler.instance().send(client, "* wearEquip " + heroModelId);
		return sendSuccess;
	}

	public boolean equipCompose(int modelId) {
		// 703098和700097合成700098
		// gainItem(703098);
		// gainItem(700097);
		// equipCompose(700098);
		boolean sendSuccess = EquipHandler.instance().compose(client, modelId);
		return sendSuccess;
	}

	/**
	 * new test by equipCompose
	 * 
	 * @return
	 */
	public boolean equipCompose() {
		return compose(700098, new int[] { 703098, 700097 });
	}

	private boolean compose(int composeId, int[] consumeList) {
		ItembagHolder itemBagHolder = client.getItembagHolder();
		ArrayList<Integer> needList = null;
		for (int i = consumeList.length; --i >= 0;) {
			int id = consumeList[i];
			if (itemBagHolder.getItemCountByModelId(id) > 0) {
				continue;
			}
			if (needList == null) {
				needList = new ArrayList<Integer>(consumeList.length);
			}
			needList.add(id);
		}
		if (needList != null) {
			for (int id : needList) {
				gainItem(id, 99);
			}
		}
		return EquipHandler.instance().compose(client, composeId);
	}

	public void checkItemEnough(int modelId) {
		ItemData itemData = client.getItembagHolder().getByModelId(modelId);
		if (itemData == null || itemData.getCount() < 50) {
			gainItem(modelId, 9999);
		}
	}

	/**
	 * 穿装备
	 * 
	 * @return
	 */
	public boolean wearEquip() {
		// 穿装备
		return EquipHandler.instance().wearEquip(client);
	}

	/**
	 * 装备附灵
	 * 
	 * @return
	 */
	public boolean equipAttach() {
		return EquipHandler.instance().equipAttach(client);
	}

	/**
	 * 英雄进阶
	 * 
	 * @return
	 */
	public boolean heroAdvance() {
		// 全部穿上装备
		// gmWearEquip(0);
		return EquipHandler.instance().heroAdvance(client);
	}

	/**
	 * 英雄升星
	 * 
	 * @return
	 */
	public boolean heroUpgrade() {
		return HeroHandler.getHandler().heroUpgrade(client);
	}

	/**
	 * 法宝强化
	 * 
	 * @return
	 */
	public boolean magicForge() {
		return MagicHandler.getHandler().magicForge(client);
	}

	public boolean givePowerAll() {
		return FriendHandler.instance().givePowerAll(client);
	}

	public boolean receivePowerAll() {
		return FriendHandler.instance().receivePowerAll(client);
	}

	// 100100001542-(3013) 100100000309-(HC) 100100001561-(3012)
	// private static final String friendUserId = "100100000309";

	public boolean givePower(String friendUserId) {
		return FriendHandler.instance().givePowerOne(client, friendUserId);
	}

	public boolean receivePower(String friendUserId) {
		return FriendHandler.instance().receivePowerOne(client, friendUserId);
	}

	/** 神器操作需要涉及的材料，创建角色是单独调用 */
	public boolean addFixEquip() {

		boolean sendSuccess = GmHandler.instance().send(client, "* addfixequipitem " + 1);

		return sendSuccess;

		// checkItemEnough(806511);// 普通进化材料
		// checkItemEnough(806512);
		// checkItemEnough(806513);
		// checkItemEnough(806514);
		// checkItemEnough(806515);
		// checkItemEnough(806516);
		// checkItemEnough(806517);
		// checkItemEnough(806518);
		// checkItemEnough(806519);
		// checkItemEnough(806520);
		// checkItemEnough(806521);
		// checkItemEnough(806522);
		// checkItemEnough(806523);
		// checkItemEnough(806524);
		// checkItemEnough(806525);
		// checkItemEnough(806526);
		// checkItemEnough(806527);
		// checkItemEnough(806528);
		//
		// checkItemEnough(806551);// 特殊←进阶别材料
		// checkItemEnough(806552);// 特殊右进阶
		//
		// checkItemEnough(806553);// 升星材料
		// checkItemEnough(806554);// 升星材料
		// checkItemEnough(806555);// 升星材料
		// checkItemEnough(806556);// 升星材料
		// checkItemEnough(806557);// 升星材料
		// checkItemEnough(806558);// 升星材料
		// checkItemEnough(806559);// 升星材料
		// checkItemEnough(806560);// 升星材料
		// checkItemEnough(806561);// 升星材料
		// checkItemEnough(806562);// 升星材料
		// checkItemEnough(806563);// 升星材料
		// checkItemEnough(806564);// 升星材料
		// checkItemEnough(806565);// 升星材料
		// checkItemEnough(806566);// 升星材料
		// checkItemEnough(806567);// 升星材料
		// checkItemEnough(806568);// 升星材料
		// checkItemEnough(806569);// 升星材料
		// checkItemEnough(806570);// 升星材料
		// checkItemEnough(806571);// 升星材料
		// checkItemEnough(806572);// 升星材料
		// checkItemEnough(806573);// 升星材料
		// checkItemEnough(806574);// 升星材料
		// checkItemEnough(806575);// 升星材料
		// checkItemEnough(806576);// 升星材料
		// checkItemEnough(806577);// 升星材料
		// checkItemEnough(806578);// 升星材料
		// checkItemEnough(806579);// 升星材料
		// checkItemEnough(806580);// 升星材料
		// checkItemEnough(806581);// 升星材料
		// checkItemEnough(806582);// 升星材料
		// checkItemEnough(806583);// 升星材料
		// checkItemEnough(806584);// 升星材料
		// checkItemEnough(806585);// 升星材料
		// checkItemEnough(806586);// 升星材料
		// //
		// checkItemEnough(806505);// 下←格经验材料
		// checkItemEnough(806510);// 下右格经验材料

		// return true;

	}

	public boolean addCoin(int coin) {
		boolean sendSuccess = GmHandler.instance().send(client, "* addCoin " + coin);
		return sendSuccess;
	}

	public boolean addGroupCopyFight(int count) {
		return GmHandler.instance().send(client, "* setgbf " + count);
	}

	public boolean addPower(int power) {
		boolean sendSuccess = GmHandler.instance().send(client, "* addPower " + power);
		return sendSuccess;
	}

	public boolean addGold(int gold) {
		boolean sendSuccess = GmHandler.instance().send(client, "* addGold " + gold);
		return sendSuccess;
	}

	/** 只加英雄和等级 */
	public boolean addHero(int i) {
		boolean sendSuccess = GmHandler.instance().send(client, "* teambringitsigle " + i);
		return sendSuccess;
	}

	public boolean additem(int id) {
		boolean sendSuccess = GmHandler.instance().send(client, "* additem " + id + " " + 999);
		return sendSuccess;
	}

	public boolean addSecretKeycount() {
		boolean sendSuccess = GmHandler.instance().send(client, "* addsecretkeycount " + 20);
		return sendSuccess;
	}

	/**
	 * 添加帮派令牌（帮战竞标）
	 * 
	 * @param token
	 * @return
	 */
	public boolean addGroupToken(int token) {
		boolean sendSuccess = GmHandler.instance().send(client, "* group token " + token);
		return sendSuccess;
	}

	/**
	 * 设置资源点2 竞标状态
	 * 
	 * @return
	 */
	public boolean setGFResBid() {
		boolean sendSuccess = GmHandler.instance().send(client, "* setgfstate 2 2");
		return sendSuccess;
	}

	/**
	 * 设置资源点2 备战状态
	 * 
	 * @return
	 */
	public boolean setGFResPrepare() {
		boolean sendSuccess = GmHandler.instance().send(client, "* setgfstate 2 3");
		return sendSuccess;
	}

	/**
	 * 设置资源点2 开战状态
	 * 
	 * @return
	 */
	public boolean setGFResFight() {
		boolean sendSuccess = GmHandler.instance().send(client, "* setgfstate 2 4");
		return sendSuccess;
	}

	/**
	 * 设置资源点2 休战状态
	 * 
	 * @return
	 */
	public boolean setGFResRest() {
		boolean sendSuccess = GmHandler.instance().send(client, "* setgfstate 2 1");
		return sendSuccess;
	}

	/**
	 * 增加帮派经验
	 * 
	 * @return
	 */
	public boolean addGroupExp() {
		boolean sendSuccess = GmHandler.instance().send(client, "* group exp 100");
		return sendSuccess;
	}

	public boolean addGroupSpplis() {
		return GmHandler.instance().send(client, "* group gs 100");
	}

	public boolean getFinishTaskReward() {
		return TaskHandler.instance().getReward(client);
	}

	// 选取角色
	public boolean selectCarrer() {

		boolean selectSuccess = SelectCareerHandler.instance().select(client);
		RobotLog.info("start carrer client userId:" + client.getUserId());
		return selectSuccess;
	}

	// 升级
	public boolean upgrade(int levelUP) {
		return GmHandler.instance().send(client, "* setLevel " + levelUP);
	}

	// 获取测试邮件，包含道具金钱等
	public boolean getTestGift() {
		boolean sendSuccess = GmHandler.instance().send(client, "* sendtestemail 1");
		boolean takeSuccess = EmailHandler.instance().openEmailList(client);
		return sendSuccess && takeSuccess;
	}

	/**
	 * 发加朋友请求
	 * 
	 * @return
	 */
	public boolean addFriend(String friendUserId) {
		boolean sendSuccess = FriendHandler.instance().add(client, friendUserId);
		return sendSuccess;
	}

	/**
	 * 发加朋友请求
	 * 
	 * @return
	 */
	public boolean removeFriend(String friendUserId) {
		boolean sendSuccess = FriendHandler.instance().remove(client, friendUserId);
		return sendSuccess;
	}

	/**
	 * 接受所有朋友请求
	 * 
	 * @return
	 */
	public boolean acceptAllFriend() {
		boolean sendSuccess = FriendHandler.instance().acceptAll(client);
		return sendSuccess;
	}

	// 获取测试邮件，包含道具金钱等
	public boolean openAllEmail() {
		boolean takeSuccess = EmailHandler.instance().openEmailList(client);
		return takeSuccess;
	}

	public boolean doPvE() {
		return PVEHandler.instance().executeMethod(client);
	}

	public boolean doPvP() {
		boolean success = PVPHandler.instance().doPvP(client);
		return success;
	}

	// private static Random random = new Random();
	private static Integer getTargetZoneId() {
		// Integer serverId = null;
		// List<Integer> serverIdList = PlatformConfig.getServerIdList();
		//
		// if(serverIdList.size()>0){
		// int nextInt = random.nextInt(serverIdList.size());
		// serverId = serverIdList.get(nextInt);
		// }else{
		// throw(new
		// RuntimeException("PlatformConfig.properties 需要配置 serverIds"));
		// }

		return PlatformConfig.getZoneId();
	}

	public void close() {
		if (client != null) {
			// ClientPool.remove(client.getAccountId());
			try {
				client.closeConnect();
			} catch (Exception e) {
				// donothing
			}
		}
	}

	public void quitPlatForm() {
		if (client != null) {
			// ClientPool.remove(client.getAccountId());
			try {
				client.closeConnect();
			} catch (Exception e) {
				// donothing
			}
			client = null;
		}
	}

	// ////////////////////////////////////////////////////////////////////////试练塔
	private BattleTowerHandler battleTowerHandler = BattleTowerHandler.getHandler();

	/**
	 * 打开试练塔主界面，这个是试练塔的第一步
	 */
	public boolean openBattleTowerMainView() {
		return battleTowerHandler.openMainView(client);
	}

	/**
	 * 通知服务器挑战试练塔开始
	 */
	public boolean battleTowerChallengeStart() {
		return battleTowerHandler.challengeBattleStart(client);
	}

	/**
	 * 通知服务器挑战试练塔层结束
	 */
	public boolean battleTowerChallengeEnd() {
		return battleTowerHandler.challengeBattleEnd(client);
	}

	/**
	 * 通知服务器挑战试练塔Boss开始
	 */
	public boolean battleTowerBossChallengeStart() {
		return battleTowerHandler.challengeBossStart(client);
	}

	/**
	 * 通知服务器挑战试练塔Boss结束
	 */
	public boolean battleTowerBossChallengeEnd() {
		return battleTowerHandler.challengeBossEnd(client);
	}

	/**
	 * 使用幸运钥匙
	 */
	public boolean battleTowerUseLuckyKey() {
		return battleTowerHandler.useLuckyKey(client);
	}

	/**
	 * 试练塔数据重置
	 */
	public boolean battleTowerResetData() {
		return battleTowerHandler.resetData(client);
	}

	// ////////////////////////////////////////////////////////////////////////帮派
	private GroupBaseHandler groupBaseHandler = GroupBaseHandler.getHandler();

	/**
	 * <pre>
	 * 创建帮派，帮派名字
	 * 名字要唯一，如果不唯一服务器会创建不成功
	 * </pre>
	 * 
	 * @param groupName
	 */
	public boolean createGroup(String groupName) {
		return groupBaseHandler.createGorup(client, groupName);
	}

	// ////////////////////////////////////////////////////////////////////////帮派个人
	private GroupPersonalHandler groupPersonalHandler = GroupPersonalHandler.getHandler();

	/**
	 * <pre>
	 * 如果想申请加入某个帮派，又不想输入帮派ID的话，最好调一下这个方法先
	 * 如果已经有了帮派的用户调用这个方法，会返回失败
	 * </pre>
	 */
	public boolean recommendGroup() {
		return groupPersonalHandler.getRecommendGroup(client);
	}

	/**
	 * <pre>
	 * 申请加入帮派，如果不知道帮派Id，就填入个null，<b>然后需要先调用{@link Robot#recommendGroup()}</b>
	 * </pre>
	 * 
	 * @param groupId
	 */
	public boolean applyGroup(String groupId) {
		return groupPersonalHandler.applyJoinGroup(client, groupId);
	}

	/**
	 * 获取个人的帮派数据，如果没有帮派会返回失败
	 * 
	 * @param groupId
	 */
	public boolean getGroupInfo() {
		return groupPersonalHandler.getGroupInfo(client);
	}

	/**
	 * 帮派贡献，如果次数已经用完，或者没有帮派会返回失败
	 */
	public boolean groupDonate() {
		return groupPersonalHandler.groupDonate(client);
	}

	// ////////////////////////////////////////////////////////////////////////帮派成员管理
	private GroupMemberHandler groupMemberHandler = GroupMemberHandler.getHandler();

	/**
	 * <pre>
	 * 接受一个申请成员，都是从已经申请的列表中随机移除一个人
	 * 如果传入的userId是null，就会从申请列表中随机通过一个
	 * </pre>
	 * 
	 * @param userId 申请成员的Id
	 */
	public boolean receiveApplyMemberOne(String userId) {
		return groupMemberHandler.memberReceive(client, userId);
	}

	/**
	 * <pre>
	 * 接受一个申请成员，都是从已经申请的列表中随机移除一个人
	 * 如果传入的userId是null，就会从申请列表中随机拒绝一个
	 * </pre>
	 * 
	 * @param userId 要拒绝的申请成员的Id
	 */
	public boolean refuseApplyMemberOne(String userId) {
		return groupMemberHandler.memberRefuse(client, userId);
	}

	/**
	 * 接受所有的申请成员
	 */
	public boolean receiveApplyMemberAll() {
		return groupMemberHandler.memberReceiveAll(client);
	}

	/**
	 * 拒绝所有的申请成员
	 */
	public boolean refuseApplyMemberAll() {
		return groupMemberHandler.memberRefuseAll(client);
	}

	/**
	 * 成员任命
	 */
	public boolean memberNominate() {
		return groupMemberHandler.memberNominate(client);
	}

	/**
	 * 取消成员任命
	 */
	public boolean memberCancelNominate() {
		return groupMemberHandler.memberCancelNominate(client);

	}

	/**
	 * 膜拜
	 */
	public boolean testWorShip(int num) {

		return WorShipHandler.getHandler().ArenaWorship(client, num);
	}

	/**
	 * 买体
	 */
	public boolean testMainService() {
		return MainHandler.getHandler().buyPower(client);
	}

	public int getChatCount() {
		return chatCount;
	}

	public void setChatCount(int chatCount) {
		this.chatCount = chatCount;
	}

	/** 消费300钻 */
	public boolean testDailyActivity() {
		return DailyActivityHandler.getHandler().Const(this);

	}

	/** 无尽战火挑战一次 */
	public boolean testCopyWarfare() {
		boolean getitemback = CopyHandler.getHandler().battleItemsBack(client, CopyType.COPY_TYPE_WARFARE);
		if (getitemback) {
			return CopyHandler.getHandler().battleClear(client, CopyType.COPY_TYPE_WARFARE, EBattleStatus.NULL);
		} else
			return false;

	}

	/** 万仙阵胜利一次 */
	public boolean testCopyTower() {
		boolean getitemback = CopyHandler.getHandler().battleItemsBack(client, CopyType.COPY_TYPE_TOWER);
		if (getitemback) {
			return CopyHandler.getHandler().battleClear(client, CopyType.COPY_TYPE_TOWER, EBattleStatus.WIN);
		} else
			return false;
	}

	/** 聚宝胜利,根据参数决定战斗次数 */
	public boolean testCopyJbzd() {
		if (!getPveInfo()) {
			RobotLog.fail("获取副本信息失败");
			return true;
		}
		CopyHolder copyHolder = client.getCopyHolder();

		if (copyHolder.getCopyTime().get(CopyType.COPY_TYPE_TRIAL_JBZD) <= 0) {
			return true;
		}

		boolean clearCd = clearCd(CopyType.COPY_TYPE_TRIAL_JBZD);
		if (!clearCd) {
			return true;
		}
		boolean result;
		result = CopyHandler.getHandler().battleItemsBack(client, CopyType.COPY_TYPE_TRIAL_JBZD);
		if (result) {
			result = CopyHandler.getHandler().battleClear(client, CopyType.COPY_TYPE_TRIAL_JBZD, EBattleStatus.WIN);
		}

		return result;
	}

	private boolean getPveInfo() {
		boolean getPveInfo = CopyHandler.getHandler().pveInfo(client);
		return getPveInfo;
	}

	/** 炼息胜利两 次 */
	public boolean testCopyLxsg() {
		if (!getPveInfo()) {
			RobotLog.fail("获取副本信息失败");
			return true;
		}
		CopyHolder copyHolder = client.getCopyHolder();
		if (copyHolder.getCopyTime().get(CopyType.COPY_TYPE_TRIAL_LQSG) <= 0) {
			return true;
		}

		boolean getitemback = CopyHandler.getHandler().battleItemsBack(client, CopyType.COPY_TYPE_TRIAL_LQSG);
		if (getitemback) {
			CopyHandler.getHandler().battleClear(client, CopyType.COPY_TYPE_TRIAL_LQSG, EBattleStatus.WIN);
		}
		clearCd(CopyType.COPY_TYPE_TRIAL_LQSG);
		boolean getitembacksecond = CopyHandler.getHandler().battleItemsBack(client, CopyType.COPY_TYPE_TRIAL_LQSG);
		if (getitembacksecond) {
			return CopyHandler.getHandler().battleClear(client, CopyType.COPY_TYPE_TRIAL_LQSG, EBattleStatus.WIN);
		}
		return false;
	}

	/** 传入关卡类型和关卡地图id */
	public boolean clearCd(int copyTypeTrialJbzd) {
		int levelId = 0;
		CopyHandler.getHandler();
		if (copyTypeTrialJbzd == CopyType.COPY_TYPE_TRIAL_JBZD) {

			levelId = CopyHandler.getJbzdcopyid()[0];
		} else if (copyTypeTrialJbzd == CopyType.COPY_TYPE_TRIAL_LQSG) {
			levelId = CopyHandler.getLxsgcopyid()[0];
		} else if (copyTypeTrialJbzd == CopyType.COPY_TYPE_CELESTIAL) {
			levelId = CopyHandler.getCelestialcopyid()[0];
		}

		boolean sendSuccess = GmHandler.instance().send(client, "* clearcd " + copyTypeTrialJbzd + " " + levelId);
		return sendSuccess;
	}

	/** 生存幻境两 次 */
	public boolean testCopyschj() {
		if (!getPveInfo()) {
			RobotLog.fail("获取副本信息失败");
			return true;
		}
		CopyHolder copyHolder = client.getCopyHolder();
		if (copyHolder.getCopyTime().get(CopyType.COPY_TYPE_CELESTIAL) <= 0) {
			return true;
		}

		boolean getitemback = CopyHandler.getHandler().battleItemsBack(client, CopyType.COPY_TYPE_CELESTIAL);
		if (getitemback) {
			CopyHandler.getHandler().battleClear(client, CopyType.COPY_TYPE_CELESTIAL, EBattleStatus.WIN);
		}
		clearCd(CopyType.COPY_TYPE_CELESTIAL);
		boolean getitembacksecond = CopyHandler.getHandler().battleItemsBack(client, CopyType.COPY_TYPE_CELESTIAL);
		if (getitembacksecond) {
			return CopyHandler.getHandler().battleClear(client, CopyType.COPY_TYPE_CELESTIAL, EBattleStatus.WIN);
		}
		return false;
	}

	/**
	 * 通用活动一领奖all
	 */
	public boolean testActivityCountTakeGift() {
		return ActivityCountHandler.getHandler().ActivityCountTakeGift(client);
	}

	/**
	 * 通用活动二领奖all
	 */
	public boolean testActivityDailyCountTakeGift() {
		return ActivityDailyCountHandler.getHandler().ActivityDailyCountTakeGift(client);
	}

	/**
	 * 七日礼包领取
	 */
	public boolean testDailyGiftTake() {
		DailyGiftHandler.getHandler().getSevenDayGiftItem(client);
		return DailyGiftHandler.getHandler().getSevenDayGift(client);

	}

	/**
	 * 封神之路普通领取
	 */
	public boolean testFrshActAchieveRewardGiftTake() {
		FresherActivityHandler.getInstance().testTakeFresherActivityRewards(client);
		return false;
	}

	public boolean sign() {
		return SignHandler.getInstance().processsSign(client);
	}

	public boolean dailyActivity() {
		return DailyHandler.getInstance().processDaily(client);
	}

	public boolean buyFashion() {
		return FashionHandler.getInstance().processBuyFashion(client);
	}

	public boolean buyWing() {
		return FashionHandler.getInstance().processBuyWing(client);
	}

	public boolean buyPet() {
		return FashionHandler.getInstance().processBuyPet(client);
	}

	public boolean WearFashion(boolean wear) {
		return FashionHandler.getInstance().processWearFashion(client, wear);
	}

	public boolean WearWing(boolean wear) {
		return FashionHandler.getInstance().processWearWing(client, wear);
	}

	public boolean WearPet(boolean wear) {
		return FashionHandler.getInstance().processWearPet(client, wear);
	}

	public boolean BuyCoin() {
		return MainHandler.getHandler().buyCoin(client);
	}

	public boolean testPeakArena() {
		PeakArenaHandler.getHandler().changeEnemy(client);
		List<ArenaInfo> listInfoList = client.getPeakArenaDataHolder().getListInfoList();
		if (listInfoList == null || listInfoList.size() <= 0) {
			return true;
		}
		PeakArenaHandler.getHandler().fightStart(client);
		return PeakArenaHandler.getHandler().fightFinish(client);
	}

	public void openGroupSecretMainView() {
		GroupSecretHandler.getInstance().openMainView(client);
	}

	public boolean createGroupSecret() {
		GroupSecretHandler.getInstance().openMainView(client);
		GroupSecretHandler.getInstance().getGroupSecretReward(client);
		// return true;
		return GroupSecretHandler.getInstance().createGroupSecret(client);
	}

	public boolean searchGroupSecret() {
		GroupSecretMatchHandler.getInstance().getGroupSecretReward(client);
		return GroupSecretMatchHandler.getInstance().searchGroupSecret(client);
	}

	public boolean attackEnemyGroupSecret() {
		GroupSecretMatchHandler.getInstance().getGroupSecretReward(client);
		boolean isCanSeach = GroupSecretMatchHandler.getInstance().searchGroupSecret(client);
		if (!isCanSeach) {
			RobotLog.fail("搜索敌对秘境时失败，请确认是否未提前生成被掠夺的秘境");
			return true;
		}
		checkEnoughSecretKeyCount();
		GroupSecretMatchHandler.getInstance().attackEnemyGroupSecret(client);
		return GroupSecretMatchHandler.getInstance().attackEndEnemyGroupSecret(client);

	}

	public boolean getGroupMatchSecretReward() {
		return GroupSecretMatchHandler.getInstance().getGroupSecretReward(client);
	}

	public boolean inviteMemberDefend() {
		return GroupSecretHandler.getInstance().inviteMemberDefend(client);
	}

	public boolean acceptMemberDefend() {
		return GroupSecretHandler.getInstance().acceptMemberDefend(client);
	}

	/**
	 * 
	 * @param type 类型，支持0-1；0为普通装备，1为特殊装备
	 * @param heronumber 英雄位置，0为玩家，1234依次为英雄
	 * @param expequipId 装备位置普通装备0123；特殊装备01；
	 * @param servicetype 操作类型；普通装备支持15234；特殊装备支持6789
	 * @return
	 */
	public boolean testFixEquip(int type, int heronumber, int expequipId, int servicetype) {

		boolean issuc = false;
		if (type == 0) {
			issuc = FixEquipHandler.instance().doEquip(client, heronumber, expequipId, servicetype);
		} else {
			issuc = FixExpEquipHandler.instance().doExpEquip(client, heronumber, expequipId, servicetype);
		}
		return issuc;
	}

	/** 预制升级和加金币；参数不存在则选择首项提升 */
	public boolean testTaoist() {
		boolean issuc = false;
		upgrade(50);
		// TaoistHandler.getHandler().getTaoistData(client);
		issuc = TaoistHandler.getHandler().updateTaoist(client);
		return issuc;
	}

	/**
	 * 进行一次乾坤幻境并领取奖励（如果所有幻境都挑战通过，则会进行任意个幻境扫荡）
	 * 
	 * @param client
	 * @return
	 */
	public boolean playerMagicSecret() {
		return MagicSecretHandler.getHandler().playMagicSecret(client);
	}

	/**
	 * 获取乾坤幻境的排行榜
	 * 
	 * @param client
	 * @return
	 */
	public boolean getMagicSecretRank() {
		return MagicSecretHandler.getHandler().getMagicSecretRank(client);
	}

	/**
	 * 帮派战
	 * 
	 * @return
	 */
	public boolean playerGroupFight() {
		return GroupFightHandler.getHandler().playGroupFight(client);
	}

	/**
	 * 申请开启帮派副本
	 * 
	 * @return
	 */
	public boolean applyOpenGroupCopy() {
		GroupCopyHandler.getInstance().applyCopyInfo(client);
		List<GroupCopyMapRecord> list = GroupCopyMgr.getInstance().getAllOnGoingChaters(client);
		if (list.isEmpty()) {
			RobotLog.info("发现角色无已开启帮派副本，执行开启请求!");
			// 增加一下帮派经验
			addGroupExp();
			addGroupSpplis();
			for (GroupCopyMapRecord record : list) {
				GroupCopyHandler.getInstance().openLevel(client, record.getChaterID(), RequestType.OPEN_COPY);
			}
		}
		return true;
	}

	/**
	 * 请求同步帮派关卡数据
	 * 
	 * @return
	 */
	public boolean applySynGroupCopyData() {
		// 同步地图关卡数据
		GroupCopyHandler.getInstance().applyCopyInfo(client);
		return true;
	}

	/**
	 * 获取随机一个帮派地图
	 * 
	 * @return
	 */
	public int getRandomGroupCopyID() {
		GroupCopyMapRecord record = GroupCopyMgr.getInstance().getRandomOpenChater(client);
		if (record == null) {
			RobotLog.info("当前机器人没有可进入的帮派副本");
			return 0;
		}
		return Integer.parseInt(record.getCurLevelID());
	}

	/**
	 * 帮派副本战斗 跑这个方法前，要依次调用 {@link Robot#applyOpenGroupCopy()},{@link Robot#getRandomGroupCopyID()},{@link Robot#applySynGroupCopyData()}
	 * 
	 * @return
	 */
	public boolean playerGroupCopy(int copyID) {

		return GroupCopyMgr.getInstance().playGroupCopy(client, String.valueOf(copyID));
	}

	/**
	 * 组队战开始战斗
	 * 
	 * @return
	 */
	public boolean startTBFight() {
		return TeamBattleHandler.getInstance().startTBFight(client);
	}

	/**
	 * 争霸赛的竞猜
	 * 
	 * @return
	 */
	public boolean groupCompQuiz() {
		return GroupCompetitionQuizHandler.getHandler().groupCompQuiz(client);
	}

	/**
	 * 争霸赛备战区内的走动
	 * 
	 * @return
	 */
	public boolean groupCompSameScene() {
		return GroupCompSameSceneHandler.getHandler().informPreparePosition(client);
	}

	public boolean sendGmCommand(String value) {
		return GmHandler.instance().send(client, value);
	}

	public void checkEnoughMoney() {
		if (!client.getMajorDataholder().CheckEnoughCoin()) {
			addCoin(1000000000);
		}
		if (!client.getMajorDataholder().CheckEnoughGold()) {
			addGold(10000000);
		}
	}

	private void checkEnoughSecretKeyCount() {
		SecretUserInfoSynData userInfoData = client.getGroupSecretUserInfoSynDataHolder().getUserInfoSynData();
		if (userInfoData != null) {
			if (userInfoData.getKeyCount() < 21) {
				addSecretKeycount();
			}
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~" + client.getGroupSecretUserInfoSynDataHolder().getUserInfoSynData().getKeyCount());
		}

	}

	public boolean donateGroupCopy() {
		return GroupCopyMgr.getInstance().donateCopy(client);
	}

	public boolean applyDistRewardLog() {
		return GroupCopyHandler.getInstance().clientApplyDistRewardLog(client);
	}

	public boolean applyGroupDamageRank() {
		return GroupCopyHandler.getInstance().clientApplyGroupDamageRank(client);
	}

	public boolean applyAllRewardApplyInfo() {
		return GroupCopyHandler.getInstance().getAllRewardApplyInfo(client);
	}

	public boolean testGroupCompetition() {
		client.executeAsynResp();
		return GroupCompetitionHandler.getHandler().testGroupCompetition(client);
	}

	/*
	 * 随机执行方法
	 */
	public boolean executeRandomMethod() {
		RandomMethodIF randomHandler = client.getNextModuleHandler();
		if (null != randomHandler) {
			return randomHandler.executeMethod(client);
		}
		return true;
	}

	/**
	 * 使用兑换码
	 * 
	 * @param code
	 * @return
	 */
	public boolean useGiftCode(String code) {
		return GiftCodeHandler.getHandler().useGiftCodeHandler(client, code);
	}
}