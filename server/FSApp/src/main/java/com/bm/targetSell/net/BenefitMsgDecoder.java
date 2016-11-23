package com.bm.targetSell.net;

import java.io.UnsupportedEncodingException;

import com.rw.fsutil.remote.parse.FSMessageDecoder;
import com.rw.fsutil.remote.parse.FSMessagePrefix;

public class BenefitMsgDecoder implements FSMessageDecoder<String>{

	@Override
	public FSMessagePrefix getPrefix() {
		return new BenefitMsgPrefix();
	}

	@Override
	public String convertToMessage(byte[] array) {
		try {
			return new String(array, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException();
		}
	}

}
