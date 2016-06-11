package com.rw;

import org.apache.log4j.PropertyConfigurator;

import com.config.PlatformConfig;
import com.rw.common.RobotLog;
import com.rw.handler.battle.PVEHandler;
import com.rw.handler.battle.PVPHandler;
import com.rw.handler.chat.ChatHandler;
import com.rw.handler.chat.GmHandler;
import com.rw.handler.email.EmailHandler;
import com.rw.handler.equip.EquipHandler;
import com.rw.handler.friend.FriendHandler;
import com.rw.handler.gamble.GambleHandler;
import com.rw.handler.gameLogin.GameLoginHandler;
import com.rw.handler.gameLogin.SelectCareerHandler;
import com.rw.handler.hero.HeroHandler;
import com.rw.handler.itembag.ItemBagHandler;
import com.rw.handler.magic.MagicHandler;
import com.rw.handler.platform.PlatformHandler;
import com.rw.handler.store.StoreHandler;
import com.rw.handler.task.TaskHandler;

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

	private static void init() {
		PropertyConfigurator.configureAndWatch("log4j.properties");
		PlatformConfig.InitPlatformConfig();

	}

	public Robot(String accountIdP) {
		this.accountId = accountIdP;
		Client clientTmp = ClientPool.getByAccountId(accountIdP);
		if (clientTmp != null) {
			client = clientTmp;
		}
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
		int zoneId = getTargetZoneId();
		boolean createSuccess = false;
		try {

			createSuccess = GameLoginHandler.instance().loginGame(client, zoneId);
		} catch (Exception e) {
			RobotLog.fail("loginGame error", e);
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

	public boolean buyRandom() {
		if (client == null) {
			return false;
		}
		addCoin(100000);
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
		boolean sendSuccess = GmHandler.instance().send(client, "* additem " + modelId + " 1");
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
	 * 穿装备
	 * 
	 * @return
	 */
	public boolean wearEquip() {
		// 作弊添加装备
		gmGainHeroEquip(0);
		// 穿装备
		return EquipHandler.instance().wearEquip(client);
	}

	/**
	 * 装备附灵
	 * 
	 * @return
	 */
	public boolean equipAttach() {
		gainItem(804001, 30);
		return EquipHandler.instance().equipAttach(client);
	}

	/**
	 * 英雄进阶
	 * 
	 * @return
	 */
	public boolean heroAdvance() {
		// 全部穿上装备
		gmWearEquip(0);
		return EquipHandler.instance().heroAdvance(client);
	}

	/**
	 * 英雄升星
	 * 
	 * @return
	 */
	public boolean heroUpgrade() {
		// 获取魂石
		gainItem(708001, 10);
		return HeroHandler.getHandler().heroUpgrade(client);
	}

	/**
	 * 法宝强化
	 * 
	 * @return
	 */
	public boolean magicForge() {
		// 添加一个法宝
		// gainItem(602003);
		// 添加材料
		// gainItem(801001, 1000);
		return MagicHandler.getHandler().magicForge(client);
	}

	public boolean givePowerAll() {
		return FriendHandler.instance().givePowerAll(client);
	}

	public boolean receivePowerAll() {
		return FriendHandler.instance().receivePowerAll(client);
	}

	// 100100001542-(3013) 100100000309-(HC) 100100001561-(3012)
	private static final String friendUserId = "100100000309";

	public boolean givePower() {
		return FriendHandler.instance().givePowerOne(client, friendUserId);
	}

	public boolean receivePower() {
		return FriendHandler.instance().receivePowerOne(client, friendUserId);
	}

	public boolean addCoin(int coin) {
		boolean sendSuccess = GmHandler.instance().send(client, "* addCoin " + coin);
		return sendSuccess;
	}

	public boolean addPower(int power) {
		boolean sendSuccess = GmHandler.instance().send(client, "* addPower " + power);
		return sendSuccess;
	}

	public boolean addGold(int gold) {
		boolean sendSuccess = GmHandler.instance().send(client, "* addGold " + gold);
		return sendSuccess;
	}

	public boolean getFinishTaskReward() {
		return TaskHandler.instance().getReward(client);
	}

	// 选取角色
	public boolean selectCarrer() {

		boolean selectSuccess = SelectCareerHandler.instance().select(client);
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
		boolean before = PVEHandler.instance().before(client);
		boolean after = false;
		if (before) {
			after = PVEHandler.instance().after(client);
		}
		return after;
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
			ClientPool.remove(client.getAccountId());
			try {
				client.closeConnect();
			} catch (Exception e) {
				// donothing
			}
			client = null;
		}
	}

	public Client getClient() {
		return client;
	}
}