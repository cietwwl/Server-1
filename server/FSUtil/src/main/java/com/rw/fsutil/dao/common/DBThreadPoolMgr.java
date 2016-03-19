package com.rw.fsutil.dao.common;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.rw.fsutil.common.SimpleThreadFactory;

/**
 * 获取DB时效任务线程管理器
 * 线程数最好做成配置
 * @author Jamaz
 *
 */
public class DBThreadPoolMgr {

	private static ScheduledThreadPoolExecutor executor;
	
	static{
		executor = new ScheduledThreadPoolExecutor(20, new SimpleThreadFactory("db"));
	}
	
	public static ScheduledThreadPoolExecutor getExecutor() {
		return executor;
	}
	
}
