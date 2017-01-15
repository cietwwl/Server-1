package com.rounter.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.rounter.client.node.ServerChannelManager;


@Component
public class Timer{

	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

	public void init() {
		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					ServerChannelManager.getInstance().refreshPlatformChannel();
				} catch (Throwable e) {
					e.printStackTrace();
				}

			}
			
		}, 0, 60, TimeUnit.SECONDS);
	}
}
