package com.fy.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fy.lua.LuaDao;
import com.fy.version.VersionDao;

public class Timer {

	private ScheduledExecutorService service = Executors
			.newSingleThreadScheduledExecutor();

	public void init() {
		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					VersionDao.getInstance().load();
					LuaDao.getInstance().load();
				} catch (Throwable e) {
					e.printStackTrace();
				}

			}
		}, 0, 10, TimeUnit.SECONDS);
	}

}
