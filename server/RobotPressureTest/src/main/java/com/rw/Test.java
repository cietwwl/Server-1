package com.rw;

import java.util.Random;

public class Test {

	// private static ExecutorService executorService = Executors.newFixedThreadPool(3);
	/** 所有运行起来的客户端 */
	public static final Random random = new Random();
	public static void main(String[] args) throws InterruptedException {
		 Robot robot = loginRobot("0527575707");
		robot.dailyActivity();

		 Robot robot = createRobot("testallen"+random.nextInt(2000));
		 if(robot == null){
		 }else{
			 boolean issucc =false;
			 
//			 issucc=robot.testCopyWarfare();
			 
			 
			 System.out.println("@@@@@@@@@@@" + issucc);
			 
		 }
		 
		 
		//5-27新增部分
//		 robot.testWorShip();//膜拜
//		 robot.testMainService();//主服务，买体力
//		 robot.testDailyActivity();
//		 robot.testCopyWarfare();//无尽战火
//		 robot.testCopyTower();//万仙阵胜利一次
		 
		 
		 
		 
		 
		 
		// long start = System.currentTimeMillis();
		// Robot robot = loginRobot("testallen0003");
		// Robot robot = loginRobot("hclovehf");
		// Client client = robot.getClient();
		// BattleTowerHandler handler = BattleTowerHandler.getHandler();
		// handler.resetData(client);
		// handler.openMainView(client);
		// handler.openChallengeView(client);
		// handler.openLuckyView(client);
		// // for (int i = 0; i < 10; i++) {
		// handler.challengeBattleStart(client);
		// handler.challengeBattleEnd(client);
		// handler.challengeBossStart(client);
		// handler.challengeBossEnd(client);
		// }

		// ///////////////////////////////////////////试练塔
		// Robot robot = loginRobot("hclovehf");
		// robot.openBattleTowerMainView();
		// robot.battleTowerResetData();
		//
		// for (int i = 0; i < 8; i++) {
		// robot.battleTowerChallengeStart();
		// robot.battleTowerChallengeEnd();
		//
		// robot.battleTowerBossChallengeStart();
		// robot.battleTowerBossChallengeEnd();
		//
		// robot.battleTowerUseLuckyKey();
		// }

		// ///////////////////////////////////////////帮派
		/**
		 * <pre>
		 * name:groupRobot1,id:100100001600 接受
		 * name:groupRobot2,id:100100001601 接受
		 * name:groupRobot3,id:100100001602 接受
		 * name:groupRobot4,id:100100001603 接受
		 * name:groupRobot5,id:100100001604 接受
		 * name:groupRobot6,id:100100001605 接受
		 * name:groupRobot7,id:100100001606 拒绝
		 * name:groupRobot8,id:100100001607 拒绝
		 * name:groupRobot9,id:100100001608 拒绝
		 * name:groupRobot10,id:100100001609 接受
		 * </pre>
		 */

		// String groupId = "100110033";
		// Robot robot = loginRobot("groupRobot9");
//		 robot.addGold(1000);
		// robot.addCoin(100000);
		// robot.createGroup("作弊捡来的");
		// for (int i = 0; i < 2; i++) {
		// robot.groupDonate();
		// }

//		 robot.refuseApplyMemberAll();
//		 robot.getGroupInfo();
		// robot.receiveApplyMemberAll();
		// robot.memberNominate();
//		 robot.memberCancelNominate();
		// robot.receiveApplyMemberOne("100100001609");
		// robot.refuseApplyMemberOne("100100001607");
		// robot.refuseApplyMemberOne("100100001606");

		// StringBuilder sb = new StringBuilder();
		// for (int i = 1; i < 11; i++) {
		// // Robot robot = createRobot("groupRobot" + i);
		// // robot.upgrade(22);
		// // robot.applyGroup(groupId);
		// String accountId = "groupRobot" + i;
		// Robot robot = loginRobot(accountId);
		// robot.applyGroup(groupId);
		
		// // sb.append("name:").append(accountId).append(",id:").append(robot.getUserId()).append("\n");
		// }
		// System.err.println(sb.toString());

		// ///////////////////////////////////////////旧服务器测试机器人
		// robot.buyRandom();
//		 robot.wearEquip();
		// robot.equipAttach();
		// robot.upgrade(35);
		// // robot.heroAdvance();
		// robot.heroUpgrade();
		// robot.magicForge();

		// handler.sweepStart(client);
		// handler.sweepEnd(client);
		// handler.useLuckyKey(client);
		// handler.getFriendRankInfo(client);
		// handler.getStrategyList(client);
		// robot.givePowerAll();
		// robot.receivePowerAll();
		// robot.givePower();
		// robot.receivePower();
		// robot.buyRandom();
		// robot.wearEquip();
		// robot.equipAttach();
		// robot.upgrade(35);
		// robot.heroAdvance();
		// robot.heroUpgrade();
		// robot.magicForge();
		// robot.doPvP();

		// long end = System.currentTimeMillis();
		// long cost = end-start;
		// System.out.println(cost);
		//
		// for (int i = 0; i < 1000; i++) {
		// Robot robot = loginRobot("testallen0001");
		// Thread.sleep(100L);
		// robot.close();
		// }

		// robot.upgrade(60);
		// robot.selectCarrer();
		// robot.doPvP();
		// robot.equipCompose();
		// robot.close();
		//
		// Robot robot2 = loginRobot("testtllen021");

		// testRemoveFriend();
		// robot2.close();
		 
		 
		 
	}

	public static void testUpgarate() {
		Robot robot = loginRobot("testallen22");
		robot.upgrade(10);
	}

	// 获取所有邮件的附件
	private static void testOpenAllEmail() {

		Robot robot = loginRobot("testallen22");
		robot.openAllEmail();

	}

	// 随机购买物品
	private static void testBuyRandom() {

		Robot robot = loginRobot("testallen22");
		robot.buyRandom();

	}

	// 随机出售物品
	private static void testSellRandom() {

		Robot robot = loginRobot("testallen22");
		robot.sellRandom();

	}

	// 出售物品
	private static void testSellItem() {

		Robot robot = loginRobot("testallen22");
		robot.sellItem(803004);

	}

	// 直接获得物品
	private static void testGetItem() {

		Robot robot = loginRobot("testallen22");
		robot.gainItem(803004);

	}

	// 装备合成
	private static void testCompose() {

		Robot robot = loginRobot("testallen22");
		robot.addCoin(20000);
		robot.gainItem(703098);
		robot.gainItem(700097);
		robot.equipCompose(700098);

	}

	// 聊天
	private static void testChat() {

		Robot robot = loginRobot("testallen22");
		robot.gamble();

	}

	// 抽卡
	private static void testGamble() {

		Robot robot = loginRobot("testallen22");
		robot.chat("ok man");

	}

	// 获取第一个完成的任务奖励,如果有的话
	private static void testGetFinishTaskReward() {

		Robot robot = loginRobot("testallen22");
		robot.getFinishTaskReward();

	}

	private static void testPVE() {

		Robot robot = loginRobot("testallen22");
		robot.doPvE();

	}

	private static void testPVP() {

		Robot robot = loginRobot("testallen22");
		robot.doPvP();

	}

	// 加好友
	private static void testAddFriend() {
		Robot robotB = createRobot("testallen25");
		Robot robotA = createRobot("testallen26");
		robotA.addFriend(robotB.getUserId());
		robotB.acceptAllFriend();
		robotA.close();
		robotB.close();

	}

	// 加好友
	private static void testRemoveFriend() {
		Robot robotB = createRobot("testallen27");
		Robot robotA = createRobot("testallen28");
		robotA.addFriend(robotB.getUserId());
		robotB.acceptAllFriend();
		robotB.removeFriend(robotA.getUserId());
		robotA.close();
		robotB.close();

	}
		
	
	// 登录
	private static Robot loginRobot(String accountId) {
		Robot robot = Robot.newInstance(accountId);
		robot.loginPlatform();
		robot.loginGame();
		return robot;
	}

	// 注册创建角色
	private static Robot createRobot(String accountId) {
		Robot robot = Robot.newInstance(accountId);
		if (robot.regPlatform()) {
			if (robot.creatRole()) {
				return robot;
			}
		}
		return null;
	}
}