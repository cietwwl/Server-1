package com.rw.fsutil.remote.parse;

public class FSIntMessagePrefix implements FSMessagePrefix {

	@Override
	public int getPrefixReadSize() {
		return 4;
	}

	@Override
	public int getDataSize(byte[] prefixDatas) {
		int ch1 = prefixDatas[0];
		int ch2 = prefixDatas[1];
		int ch3 = prefixDatas[2];
		int ch4 = prefixDatas[3];
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}

	@Override
	public int getMaxDataReadSize() {
		return 65535;
	}

}
