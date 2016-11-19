package com.gm.giftCenter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

import com.gm.gmsender.GmSender;
import com.gm.gmsender.GmSenderConfig;
import com.gm.gmsender.GmSenderPool;
import com.log.GameLog;
import com.log.LogModule;
import com.rw.manager.GameManager;
import com.rw.manager.ServerSwitch;
import com.rw.netty.ServerConfig;

public class GiftCodeSenderBm {

	private static Logger logger = Logger.getLogger("warningLog");

	protected ExecutorService sendService; // 提交的线程池

	protected GmSenderPool giftSenderPool; // http发送器的对象池
	
	private static GiftCodeSenderBm _instance = new GiftCodeSenderBm();
	
	public static GiftCodeSenderBm getInstance() {
		return _instance;
	}

	public void init() throws Exception {
		if (!ServerSwitch.isGiftCodeOpen()) {
			return;
		}

		String giftCodeServerIp = ServerConfig.getInstance().getServeZoneInfo().getGiftCodeServerIp();
		int giftCodeServerPort = ServerConfig.getInstance().getServeZoneInfo().getGiftCodeServerPort();
		GmSenderConfig senderConfig = new GmSenderConfig(giftCodeServerIp, giftCodeServerPort, GameManager.getGiftCodeTimeOut(), (short) 10354);

		giftSenderPool = new GmSenderPool(senderConfig);
		
		sendService = Executors.newFixedThreadPool(16);

	}

	public boolean add(final GiftCodeItem giftCodeItem) {
		if (giftCodeItem != null) {
			sendService.submit(new GiftCodeTask(giftCodeItem));
			return true;
		}
		return false;
	}
	
	protected static class GiftCodeTask implements Runnable {

		protected GiftCodeItem targetItem;

		protected GiftCodeTask(GiftCodeItem pTargetItem) {
			this.targetItem = pTargetItem;
		}

		@Override
		public void run() {
			GmSenderPool senderPool = GiftCodeSenderBm.getInstance().giftSenderPool;
			GmSender borrowSender = senderPool.borrowSender();
			if (borrowSender != null) {
				try {

					logger.info(String.format("发送兑换码请求，兑换码：%s，userId：%s，account：%s", targetItem.getId(), targetItem.getUserId(), targetItem.getAccount()));
					GiftCodeRsp resopnse = borrowSender.send(targetItem.toGmSendItemData(), GiftCodeRsp.class, 20039);
					targetItem.getGmCallBack().doCallBack(resopnse == null ? null : resopnse.getResult().get(0));

				} catch (Exception e) {
					borrowSender.setAvailable(false); // return pool之后会被销毁。
					GameLog.error(LogModule.GmSender, "GiftCodeSenderBm[addSendTask]", "borrowSender.send error", e);
				} finally {
					senderPool.returnSender(borrowSender);
				}
			} else {
				// 如果对象池没有空闲对象
				GiftCodeSenderBm.getInstance().add(targetItem); // 再次提交
			}
		}
	}
}
