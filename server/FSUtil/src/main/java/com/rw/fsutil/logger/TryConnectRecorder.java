package com.rw.fsutil.logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 尝试连接记录器
 * 
 * @author Jamaz
 *
 */
public class TryConnectRecorder {

	private final long recordTimeMillis;
	private AtomicInteger successTimes = new AtomicInteger();
	private AtomicInteger failTimes = new AtomicInteger();

	public TryConnectRecorder(long recordTimeMillis) {
		this.recordTimeMillis = recordTimeMillis;
	}

	/**
	 * 获取连接成功次数
	 * @return
	 */
	public AtomicInteger getSuccessTimes() {
		return successTimes;
	}

	/**
	 * 获取失败失败次数
	 * @return
	 */
	public AtomicInteger getFailTimes() {
		return failTimes;
	}

	/**
	 * 获取时间记录器
	 * @return
	 */
	public long getRecordTimeMillis() {
		return recordTimeMillis;
	}

}
