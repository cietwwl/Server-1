package com.gm.giftCenter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.TimeUnit;

//import com.gm.gmsender.GmSender;
import com.gm.gmsender.GmSenderConfig;
import com.gm.gmsender.GmSenderPool;
import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.manager.GameManager;
import com.rw.manager.ServerSwitch;
import com.rw.netty.ServerConfig;

public class GiftCodeSenderBm {

	private BlockingQueue<GiftCodeItem> GiftCodeItemQueue = new LinkedBlockingQueue<GiftCodeItem>();

	// private Map<String, GiftCodeItem> userGiftMap = new
	// ConcurrentHashMap<String, GiftCodeItem>();

	private ExecutorService sendService;

	private ExecutorService submitService;

	private GmSenderPool giftSenderPool;
	
	private GiftCodeSenderTask _senderTask = new GiftCodeSenderTask();
	
	ExecutorService getSendService() {
		return sendService;
	}

	GmSenderPool getGiftSenderPool() {
		return giftSenderPool;
	}

	public static GiftCodeSenderBm getInstance() {
		// return SpringContextUtil.getBean("GmSenderBm");
		return SpringContextUtil.getBean(GiftCodeSenderBm.class);
	}
	
	BlockingQueue<GiftCodeItem> getGiftCodeItemQueue() {
		return GiftCodeItemQueue;
	}

	public void init() {
		if (!ServerSwitch.isGiftCodeOpen()) {
			return;
		}

		// GmSenderConfig senderConfig = null;
		String giftCodeServerIp = ServerConfig.getInstance().getServeZoneInfo().getGiftCodeServerIp();
		int giftCodeServerPort = ServerConfig.getInstance().getServeZoneInfo().getGiftCodeServerPort();
		GmSenderConfig senderConfig = new GmSenderConfig(giftCodeServerIp, giftCodeServerPort, GameManager.getGiftCodeTimeOut(), (short) 10354);

		giftSenderPool = new GmSenderPool(senderConfig);

		sendService = Executors.newSingleThreadExecutor();
		submitService = Executors.newFixedThreadPool(10);
		submitService.submit(new Runnable() {
//			@Override
//			public void run() {
//
//				while (true) {
//					try {
//						checkAndSubmit();
//					} catch (Throwable e) {
//						GameLog.error(LogModule.GmSender, "GiftCodeSenderBm[checkAndSubmit]", "", e);
//					}
//				}
//
//			}
//
//			private void checkAndSubmit() {
//				final GmSender borrowSender = giftSenderPool.borrowSender();
//				if (borrowSender != null) {
//					GiftCodeItem giftCodeItem = null;
//					try {
//						giftCodeItem = GiftCodeItemQueue.poll(10, TimeUnit.SECONDS);
//					} catch (InterruptedException e) {
//						// do nothing
//					}
//					if (giftCodeItem != null) {
//						addSendTask(borrowSender, giftCodeItem);
//					} else {
//						giftSenderPool.returnSender(borrowSender);
//					}
//				}
//			}
//
//			private void addSendTask(final GmSender borrowSender, final GiftCodeItem giftCodeItem) {
//				sendService.submit(new Runnable() {
//
//					@Override
//					public void run() {
//						try {
//							GiftCodeRsp resopnse = borrowSender.send(giftCodeItem.toGmSendItemData(), GiftCodeRsp.class, 20039);
//							giftCodeItem.getGmCallBack().doCallBack(resopnse == null ? null : resopnse.getResult().get(0));
//
//						} catch (Exception e) {
//							borrowSender.setAvailable(false);//return pool之后会呗销毁。
//							GameLog.error(LogModule.GmSender, "GiftCodeSenderBm[addSendTask]", "borrowSender.send error", e);
//						} finally {
//							giftSenderPool.returnSender(borrowSender);
//						}
//
//					}
//				});
//			}
			@Override
			public void run() {
				while (true) {
					try {
						_senderTask.checkAndSubmit(GiftCodeSenderBm.this);
					} catch (Throwable e) {
						GameLog.error(LogModule.GmSender, "GiftCodeSenderBm[checkAndSubmit]", "", e);
					}
				}
			}
		});

	}

	public boolean add(GiftCodeItem giftCodeItem) {
		boolean addSuccess = false;
		final int maxHold = 1000;

		if (GiftCodeItemQueue.size() < maxHold) {

			GiftCodeItemQueue.add(giftCodeItem);
			addSuccess = true;
		}
		return addSuccess;
	}
}
