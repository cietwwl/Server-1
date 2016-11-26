package com.rw.fsutil.record;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.rw.fsutil.common.SimpleThreadFactory;

/**
 * 用于周期性记录服务器状态或打印
 */
public class SimpleRecordService {

	private static ScheduledThreadPoolExecutor recrodExecutor = new ScheduledThreadPoolExecutor(1, new SimpleThreadFactory("record"));

	public static ScheduledThreadPoolExecutor getRecrodExectuor() {
		return recrodExecutor;
	}
	
}
