package com.gm.multipletimeshotfix;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.manager.GameManager;
import com.rwbase.common.timer.core.FSGameTimerMgr;

/**
 * @Author HC
 * @date 2016年12月21日 下午12:36:18
 * @desc
 **/

public class NewFreshActivityCheckTask implements Callable<Void> {

	private static AtomicBoolean hasInit = new AtomicBoolean();

	@Override
	public Void call() throws Exception {
		String serverId = GameManager.getServerId();
		FSUtilLogger.info("执行NewFreshActivityCheckTask--1,serverId=" + serverId);
		if (!serverId.trim().contains("9001")) {
			return null;
		}
		FSUtilLogger.info("执行NewFreshActivityCheckTask--2,serverId=" + serverId);
		if (hasInit.compareAndSet(false, true)) {
			FSGameTimerMgr.getInstance().submitSecondTask(new NewFreshActivityAction(), 10);
		} else {
			FSUtilLogger.info("执行NewFreshActivityCheckTask失败，重复执行=" + hasInit);
		}
		return null;
	}
}