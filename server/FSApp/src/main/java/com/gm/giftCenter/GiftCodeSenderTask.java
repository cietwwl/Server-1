package com.gm.giftCenter;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.gm.gmsender.GmSender;
import com.log.GameLog;
import com.log.LogModule;

public class GiftCodeSenderTask {
	
	private static Logger logger = Logger.getLogger("warningLog");

	public void checkAndSubmit(GiftCodeSenderBm senderBM) {
		final GmSender borrowSender = senderBM.getGiftSenderPool().borrowSender();
		if (borrowSender != null) {
			GiftCodeItem giftCodeItem = null;
			try {
				giftCodeItem = senderBM.getGiftCodeItemQueue().poll(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// do nothing
			}
			if (giftCodeItem != null) {
				addSendTask(borrowSender, giftCodeItem, senderBM);
			} else {
				senderBM.getGiftSenderPool().returnSender(borrowSender);
			}
		}
	}

	private void addSendTask(final GmSender borrowSender, final GiftCodeItem giftCodeItem, final GiftCodeSenderBm senderBM) {
		senderBM.getSendService().submit(new Runnable() {

			@Override
			public void run() {
				try {
					
					logger.info(String.format("发送兑换码请求，兑换码：%s，userId：%s，account：%s", giftCodeItem.getId(), giftCodeItem.getUserId(), giftCodeItem.getAccount()));
					GiftCodeRsp resopnse = borrowSender.send(giftCodeItem.toGmSendItemData(), GiftCodeRsp.class, 20039);
					giftCodeItem.getGmCallBack().doCallBack(resopnse == null ? null : resopnse.getResult().get(0));

				} catch (Exception e) {
					borrowSender.setAvailable(false);// return pool之后会呗销毁。
					GameLog.error(LogModule.GmSender, "GiftCodeSenderBm[addSendTask]", "borrowSender.send error", e);
				} finally {
					senderBM.getGiftSenderPool().returnSender(borrowSender);
				}
			}
		});
	}
}
