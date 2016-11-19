package com.rw.fsutil.remote.parse;

public interface FSMessagePrefix {

	/**
	 * 获取前置长度
	 * @return
	 */
	public int getPrefixReadSize();

	/**
	 * 获取最大读取数量上限，超过此上限，会当做错误处理
	 * @return
	 */
	public int getMaxDataReadSize();

	/**
	 * 获取数据长度
	 * @param msg
	 * @return
	 */
	public int getDataSize(byte[] prefixDatas);

}
