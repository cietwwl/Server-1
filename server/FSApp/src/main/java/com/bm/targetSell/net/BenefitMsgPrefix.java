package com.bm.targetSell.net;

import com.rw.fsutil.remote.parse.FSIntMessagePrefix;

public class BenefitMsgPrefix extends FSIntMessagePrefix{

	@Override
	public int getDataSize(byte[] prefixDatas) {
		int size = super.getDataSize(prefixDatas);
		int bodyLen = size ^ BenefitMsgController.MSG_KEY;
		return bodyLen;
	}

	
}
