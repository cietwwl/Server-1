package com.rw.fsutil.remote.parse;

public class FSShortMessagePrefix implements FSMessagePrefix {

	@Override
	public int getPrefixReadSize() {
		return 2;
	}

	@Override
	public int getMaxDataReadSize() {
		return 32768;
	}

	@Override
	public int getDataSize(byte[] prefixDatas) {
		int ch1 = prefixDatas[0];
		int ch2 = prefixDatas[1];
		return ((ch1 << 8) + (ch2 << 0));
	}

}
