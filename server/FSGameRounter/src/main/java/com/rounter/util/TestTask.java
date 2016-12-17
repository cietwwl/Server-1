package com.rounter.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class TestTask implements ICommonTask{

	@Scheduled(cron="0/5 * *  * * ? ")   //每5秒执行一次
	@Override
	public void doWork() {
		//logger.info("FS Game Rounter is running~~~~~~~");
		
	}

	public TestTask() {
		System.out.println("---------------------");
	}
	
	

}
