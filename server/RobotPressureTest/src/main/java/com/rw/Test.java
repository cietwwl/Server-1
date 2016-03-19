package com.rw;

public class Test {

	// private static ExecutorService executorService = Executors.newFixedThreadPool(3);

	public static void main(String[] args) throws InterruptedException {
		// Robot robot = createRobot("testallen0002");
		// long start = System.currentTimeMillis();
		// Robot robot = loginRobot("testallen0003");
		Robot robot = loginRobot("hclovehf3012");
		// robot.givePowerAll();
		robot.receivePowerAll();
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