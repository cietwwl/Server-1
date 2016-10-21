package com.rw.service.PlatformService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.log.PlatformLog;
import com.rw.platform.PlatformFactory;

public class PlatformServer {
	private ExecutorService executorService = Executors.newFixedThreadPool(5);
	private ExecutorService plService = Executors.newSingleThreadExecutor();
	
	PlatformServerHandler platformServerHandler;
	
	public void init(){
		platformServerHandler = new PlatformServerHandler();
		plService.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startService();
			}
		});
	}
	
	private void startService(){
		System.out.println("启动平台通信服务");
		ServerSocket serverSocket = null;
		try{
			serverSocket = new ServerSocket(PlatformFactory.getHttpPort());
			while(true){
				final Socket client = serverSocket.accept();
				executorService.submit(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							client.setSoTimeout(5000);
							platformServerHandler.handler(client);
						} catch (SocketException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				});
			}
		} catch (Exception e) {
			PlatformLog.error("platformService", "PlatformFactory.getHttpPort():" + PlatformFactory.getHttpPort(), e.getMessage());
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					PlatformLog.error("platformService", "PlatformFactory.getHttpPort():" + PlatformFactory.getHttpPort(), e.getMessage());
				}
			}
		}
	}
}
