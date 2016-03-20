package com.gm.giftCenter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.gm.gmsender.GmSender;
import com.gm.gmsender.GmSenderConfig;
import com.gm.gmsender.GmSenderPool;
import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.util.SpringContextUtil;

public class GiftCodeSenderBm {
	
	

	private BlockingQueue<GiftCodeItem> GiftCodeItemQueue = new LinkedBlockingQueue<GiftCodeItem>();

	// private Map<String, GiftCodeItem> userGiftMap = new
	// ConcurrentHashMap<String, GiftCodeItem>();

	private ExecutorService sendService;

	private ExecutorService submitService;

	private GmSenderPool giftSenderPool;

	public static GiftCodeSenderBm getInstance() {
		return SpringContextUtil.getBean("GmSenderBm");

	}

	public void init() {
		
		GmSenderConfig senderConfig = null;
		
		giftSenderPool = new GmSenderPool(senderConfig);
		
		sendService = Executors.newSingleThreadExecutor();
		submitService = Executors.newFixedThreadPool(10);
		submitService.submit(new Runnable() {
			@Override
			public void run() {

				while (true) {
					try {
						checkAndSubmit();						
					} catch (Throwable e) {
						GameLog.error(LogModule.GmSender, "GiftCodeSenderBm[checkAndSubmit]", "", e);
					}
				}

			}

			private void checkAndSubmit() {
				final GmSender borrowSender = giftSenderPool.borrowSender();
				if (borrowSender != null) {
					GiftCodeItem giftCodeItem = null;
					try {
						giftCodeItem = GiftCodeItemQueue.poll(10, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						//do nothing
					}
					if (giftCodeItem != null) {
						addSendTask(borrowSender, giftCodeItem);
					} else {
						giftSenderPool.returnSender(borrowSender);
					}
				}
			}

			private void addSendTask(final GmSender borrowSender, final GiftCodeItem giftCodeItem) {
				sendService.submit(new Runnable() {

					@Override
					public void run() {
						try {
							GiftCodeResponse resopnse = borrowSender.send(giftCodeItem.toGmSendItemData(), GiftCodeResponse.class);
							giftCodeItem.getGmCallBack().doCallBack(resopnse);
							
						} catch (Exception e) {
							GameLog.error(LogModule.GmSender, "GiftCodeSenderBm[addSendTask]", "borrowSender.send error", e);
						} finally {
							giftSenderPool.returnSender(borrowSender);
						}

					}
				});
			}
		});

	}

	public boolean add(GiftCodeItem giftCodeItem) {
		boolean addSuccess = false;
		final int maxHold = 1000;
		
		if(GiftCodeItemQueue.size() < maxHold){	
			
			GiftCodeItemQueue.add(giftCodeItem);
			addSuccess = true;
		}
		return addSuccess;
	}
}
