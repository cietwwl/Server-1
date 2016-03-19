package com.dx.gods.common.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.concurrent.ScheduledExecutorTask;

import com.dx.gods.common.utils.GlobalValue;

public class TimerManger implements Runnable{
	
	public static ScheduledExecutorService scheudlExecutorService = Executors.newScheduledThreadPool(2);
	
	public static void initTimer(){
		TimerManger timer = new TimerManger();
		scheudlExecutorService.scheduleAtFixedRate(timer, 1, 10, TimeUnit.SECONDS);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		GlobalValue.initConfig(false);
	}
}
