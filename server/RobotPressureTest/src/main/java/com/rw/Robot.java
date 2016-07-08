package com.rw;

import org.apache.log4j.PropertyConfigurator;

import com.config.PlatformConfig;
import com.rw.common.RobotLog;
import com.rw.handler.DailyActivity.DailyActivityHandler;
import com.rw.handler.activity.ActivityCountHandler;
import com.rw.handler.activity.daily.ActivityDailyCountHandler;
import com.rw.handler.battle.PVEHandler;
import com.rw.handler.battle.PVPHandler;
import com.rw.handler.battletower.BattleTowerHandler;
import com.rw.handler.chat.ChatHandler;
import com.rw.handler.chat.GmHandler;
import com.rw.handler.copy.CopyHandler;
import com.rw.handler.copy.CopyType;
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
import com.rw.handler.group.GroupBaseHandler;
import com.rw.handler.group.GroupMemberHandler;
import com.rw.handler.group.GroupPersonalHandler;
import com.rw.handler.groupsecret.GroupSecretHandler;
import com.rw.handler.groupsecret.GroupSecretMatchHandler;
import com.rw.handler.hero.HeroHandler;
import com.rw.handler.itembag.ItemBagHandler;
import com.rw.handler.itembag.ItemData;
import com.rw.handler.magic.MagicHandler;
import com.rw.handler.magicSecret.MagicSecretHandler;
import com.rw.handler.mainService.MainHandler;
import com.rw.handler.peakArena.PeakArenaHandler;
import com.rw.handler.platform.PlatformHandler;
import com.rw.handler.sign.SignHandler;
import com.rw.handler.sevenDayGift.DailyGiftHandler;
import com.rw.handler.store.StoreHandler;
import com.rw.handler.taoist.TaoistHandler;
import com.rw.handler.task.TaskHandler;
import com.rw.handler.worShip.worShipHandler;
import com.rwproto.CopyServiceProtos.EBattleStatus;

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
				Thread.sleep(1000);
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
			Thread.sleep(500);
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
	/**钻石抽*/
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
	 * @param heroModelId
	 *            如果是0是主角，其他的佣兵要填入具体的模版Id，例如姜子牙就填入202001
	 * @return
	 */
	public boolean gmGainHeroEquip(int heroModelId) {
		boolean sendSuccess = GmHandler.instance().send(client, "* gainHeroEquip " + heroModelId);
		return sendSuccess;
	}

	/**
	 * 作弊穿装备
	 * 
	 * @param heroModelId
	 *            如果是0是主角，其他的佣兵要填入具体的模版Id，例如姜子牙就填入202001
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
	
	
	public void checkItemEnough(int modelId) {
		ItemData itemData = client.getItembagHolder().getByModelId(modelId);
		if (itemData.getCount() < 10) {
			gainItem(modelId, 888);
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
	
	public boolean additem(int id) {
		boolean sendSuccess = GmHandler.instance().send(client, "* additem " + id + " " + 999);
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
	 * @param userId
	 *            申请成员的Id
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
	 * @param userId
	 *            要拒绝的申请成员的Id
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
	public  boolean testWorShip(int num) {
			
		
		return worShipHandler.getHandler().ArenaWorship(client, num);
	}
	
	/**
	 * 买体
	 */
	public  boolean testMainService() {
		return MainHandler.getHandler().buyTower(client);
	}
	
	
	
	public int getChatCount() {
		return chatCount;
	}

	public void setChatCount(int chatCount) {
		this.chatCount = chatCount;
	}
	
	/**消费300钻 */
	public boolean testDailyActivity() {
		// TODO Auto-generated method stub
		return DailyActivityHandler.getHandler().Const(this);		
		
	}
	
	/**无尽战火挑战一次*/
	public boolean testCopyWarfare(){		
		boolean getitemback = CopyHandler.getHandler().battleItemsBack(client,CopyType.COPY_TYPE_WARFARE);
		if(getitemback){
			return CopyHandler.getHandler().battleClear(client,CopyType.COPY_TYPE_WARFARE,EBattleStatus.NULL);		
		}else return false;
		 
	}

	/**万仙阵胜利一次 */
	public boolean testCopyTower() {
		boolean getitemback = CopyHandler.getHandler().battleItemsBack(client,CopyType.COPY_TYPE_TOWER);
		if(getitemback){
			return CopyHandler.getHandler().battleClear(client,CopyType.COPY_TYPE_TOWER,EBattleStatus.WIN);		
		}else return false;
	}
	
	/** 聚宝胜利,根据参数决定战斗次数 */
	public boolean testCopyJbzd(int num) {
		if(!getPveInfo()){
			return false;
		}
		CopyHandler.getHandler();
		if(CopyHandler.getCopyTime().get(CopyType.COPY_TYPE_TRIAL_JBZD) <= 0){			
			return true;
		}
		
		
		if (num == 2) {
			boolean getitemback = CopyHandler.getHandler().battleItemsBack(
					client, CopyType.COPY_TYPE_TRIAL_JBZD);
			if (getitemback) {
				CopyHandler.getHandler().battleClear(client,
						CopyType.COPY_TYPE_TRIAL_JBZD, EBattleStatus.WIN);
			}
		}
		clearCd(CopyType.COPY_TYPE_TRIAL_JBZD);
		boolean getitembacksecond = CopyHandler.getHandler().battleItemsBack(
				client, CopyType.COPY_TYPE_TRIAL_JBZD);
		if (getitembacksecond) {
			return CopyHandler.getHandler().battleClear(client,
					CopyType.COPY_TYPE_TRIAL_JBZD, EBattleStatus.WIN);
		}

		return false;
	}
	
	private boolean getPveInfo(){
		boolean getPveInfo = CopyHandler.getHandler().pveInfo(client);
		return getPveInfo;		
	}
	


	/**炼息胜利两 次 */
	public boolean testCopyLxsg() {
		if(!getPveInfo()){
			return false;
		}
		CopyHandler.getHandler();
		if(CopyHandler.getCopyTime().get(CopyType.COPY_TYPE_TRIAL_LQSG) <= 0){			
			return true;
		}
		
		boolean getitemback = CopyHandler.getHandler().battleItemsBack(client,CopyType.COPY_TYPE_TRIAL_LQSG);
		if(getitemback){
			CopyHandler.getHandler().battleClear(client,CopyType.COPY_TYPE_TRIAL_LQSG,EBattleStatus.WIN);		
		}
		clearCd(CopyType.COPY_TYPE_TRIAL_LQSG);
		boolean getitembacksecond = CopyHandler.getHandler().battleItemsBack(client,CopyType.COPY_TYPE_TRIAL_LQSG);
		if(getitembacksecond){
			return CopyHandler.getHandler().battleClear(client,CopyType.COPY_TYPE_TRIAL_LQSG,EBattleStatus.WIN);		
		}
		return false;
	}
	
	/**传入关卡类型和关卡地图id*/
	public boolean clearCd(int copyTypeTrialJbzd) {
		int levelId = 0;
		CopyHandler.getHandler();
		if(copyTypeTrialJbzd == CopyType.COPY_TYPE_TRIAL_JBZD){
			
			levelId = CopyHandler.getJbzdcopyid()[0];
		}else if(copyTypeTrialJbzd == CopyType.COPY_TYPE_TRIAL_LQSG){
			levelId = CopyHandler.getLxsgcopyid()[0];
		}else if(copyTypeTrialJbzd == CopyType.COPY_TYPE_CELESTIAL){
			levelId = CopyHandler.getCelestialcopyid()[0];
		}
		
		boolean sendSuccess = GmHandler.instance().send(client, "* clearcd " + copyTypeTrialJbzd + " "+ levelId);
		return sendSuccess;		
	}
	
	/**生存幻境两 次 */
	public boolean testCopyschj() {
		if(!getPveInfo()){
			return false;
		}
		CopyHandler.getHandler();
		if(CopyHandler.getCopyTime().get(CopyType.COPY_TYPE_CELESTIAL) <= 0){			
			return true;
		}
		
		
		boolean getitemback = CopyHandler.getHandler().battleItemsBack(client,CopyType.COPY_TYPE_CELESTIAL);
		if(getitemback){
			CopyHandler.getHandler().battleClear(client,CopyType.COPY_TYPE_CELESTIAL,EBattleStatus.WIN);		
		}
		clearCd(CopyType.COPY_TYPE_CELESTIAL);
		boolean getitembacksecond = CopyHandler.getHandler().battleItemsBack(client,CopyType.COPY_TYPE_CELESTIAL);
		if(getitembacksecond){
			return CopyHandler.getHandler().battleClear(client,CopyType.COPY_TYPE_CELESTIAL,EBattleStatus.WIN);		
		}
		return false;
	}
	
	/**
	 * 通用活动一领奖all
	 */
	public  boolean testActivityCountTakeGift() {					
				
		return	ActivityCountHandler.getHandler().ActivityCountTakeGift(client);				
	}
	
	/**
	 * 通用活动二领奖all
	 */
	public  boolean testActivityDailyCountTakeGift() {		
		return ActivityDailyCountHandler.getHandler().ActivityDailyCountTakeGift(client);	
	}
	
	/**
	 * 七日礼包领取
	 */
	public  boolean testDailyGiftTake(){		
		DailyGiftHandler.getHandler().getSevenDayGiftItem(client);
		return DailyGiftHandler.getHandler().getSevenDayGift(client);
		
	}
	
	/**
	 * 封神之路普通领取
	 */
	public  boolean testFrshActAchieveRewardGiftTake(){		
		FresherActivityHandler.getInstance().testTakeFresherActivityRewards(client);
		return  false;		
	}
	

	
	
	public boolean sign(){
		return SignHandler.getInstance().processsSign(client);
	}
	
	public boolean dailyActivity(){
		return DailyHandler.getInstance().processDaily(client);
	}
	
	public boolean buyFashion(){
		return FashionHandler.getInstance().processBuyFashion(client);
	}
	
	public boolean buyWing(){
		return FashionHandler.getInstance().processBuyWing(client);
	}
	
	public boolean buyPet(){
		return FashionHandler.getInstance().processBuyPet(client);
	}

	public boolean WearFashion(boolean wear){
		return FashionHandler.getInstance().processWearFashion(client, wear);
	}
	
	public boolean WearWing(boolean wear){
		return FashionHandler.getInstance().processWearWing(client, wear);
	}
	
	public boolean WearPet(boolean wear){
		return FashionHandler.getInstance().processWearPet(client, wear);
	}
	
	public boolean BuyCoin(){
		return MainHandler.getHandler().buyCoin(client);
	}

	public boolean testPeakArena() {
		boolean issuc = upgrade(50);
		RobotLog.fail("巅峰竞技场-设置50级结果" + issuc);
		PeakArenaHandler.getHandler().changeEnemy(client, "");
		PeakArenaHandler.getHandler().fightStart(client, "");
		return PeakArenaHandler.getHandler().fightFinish(client, "");
	}
	
	public boolean createGroupSecret(){
		return GroupSecretHandler.getInstance().createGroupSecret(client);
	}
	
	public boolean searchGroupSecret(){
		return GroupSecretMatchHandler.getInstance().searchGroupSecret(client);
	}
	
	public boolean attackEnemyGroupSecret(){
		return GroupSecretMatchHandler.getInstance().attackEnemyGroupSecret(client);
	}
	
	public boolean getGroupSecretReward(){
		return GroupSecretMatchHandler.getInstance().getGroupSecretReward(client);
	}
	
	public boolean inviteMemberDefend(){
		return GroupSecretHandler.getInstance().inviteMemberDefend(client);
	}
	
	public boolean acceptMemberDefend(){
		ChatHandler.instance().sendRequestTreasure(client);
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
	public boolean testFixEquip(int type ,int heronumber,int expequipId,int servicetype){
		upgrade(50);
		additem(806511);//进化材料
		additem(806523);//升星材料
		additem(806501);//下两格经验材料
		boolean issuc = false;
		if(type == 0){
			issuc=FixEquipHandler.instance().doEquip(client,heronumber,expequipId, servicetype);
		}else{
			issuc=FixExpEquipHandler.instance().doExpEquip(client,heronumber,expequipId, servicetype);			
		}		
		return issuc;
	}
	
	/**预制升级和加金币；参数不存在则选择首项提升*/
	public boolean testTaoist(){
		upgrade(50);	
		boolean issuc = false;
//		TaoistHandler.getHandler().getTaoistData(client);
		issuc=TaoistHandler.getHandler().updateTaoist(client);
		return issuc;
	}
	
	/**
	 * 进行一次乾坤幻境并领取奖励（如果所有幻境都挑战通过，则会进行任意个幻境扫荡）
	 * @param client
	 * @return
	 */
	public boolean playerMagicSecret(){
		return MagicSecretHandler.getHandler().playMagicSecret(client);
	}
	
	/**
	 * 获取乾坤幻境的排行榜
	 * @param client
	 * @return
	 */
	public boolean getMagicSecretRank() {
		return MagicSecretHandler.getHandler().getMagicSecretRank(client);
	}
	
	public boolean sendGmCommand(String value){
		return GmHandler.instance().send(client, value);
	}

	public void checkEnoughMoney(){
		if(!client.getMajorDataholder().CheckEnoughCoin()){
			addCoin(1000000000);
		}
		if(!client.getMajorDataholder().CheckEnoughGold()){
			addGold(10000000);
		}
	}
}