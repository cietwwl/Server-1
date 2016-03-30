package com.gm.giftCenter;

import java.io.IOException;

import com.gm.gmsender.GmCallBack;
import com.gm.gmsender.GmSender;
import com.gm.gmsender.GmSenderConfig;

public class GiftCodeTest {

	public static void main(String[] args) throws IOException {
		GmSenderConfig senderConfig = null;
		GmSender gmSender = new GmSender(senderConfig);

		final String code = null;
		final String userId = null;
		GiftCodeItem giftCodeItem = new GiftCodeItem(code, userId, new GmCallBack<GiftCodeResponse>() {
			@Override
			public void doCallBack(GiftCodeResponse gmResponse) {
				// TODO Auto-generated method stub

			}
		});
		GiftCodeResponse resopnse = gmSender.send(giftCodeItem.toGmSendItemData(), GiftCodeResponse.class);
		giftCodeItem.getGmCallBack().doCallBack(resopnse);
	}
}