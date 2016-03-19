package com.gm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.log.GameLog;
import com.log.LogModule;
import com.rw.netty.ServerConfig;


public class GmServer {
	
//	private int gmPort = 12345;
	
	private String gmAccount = "gm";
	
	private String gmPassword = "passwd";

	private GmHandler gmHandler;
	
	private ExecutorService executorService = Executors.newFixedThreadPool(5);
	
	private ExecutorService gmService = Executors.newSingleThreadExecutor();
	
	public static void main(String[] args) {
		GmServer server = new GmServer();
		server.init();
	}

	public void init() {
		
		gmHandler = new GmHandler(gmAccount, gmPassword);
		gmService.submit(new Runnable() {
			
			@Override
			public void run() {
				
				startService();
				
			}
		});
		
	}
	
	private void startService(){
		System.out.println("服务器启动...\n");
		ServerSocket serverSocket = null;
		try {
			int gmPort = Integer.valueOf(ServerConfig.getInstance().getServeZoneInfo().getGmPort());
			serverSocket = new ServerSocket(gmPort);
			while (true) {
				// 一旦有堵塞, 则表示服务器与客户端获得了连接
				final Socket client = serverSocket.accept();
				// 处理这次连接
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						gmHandler.handle(client);
						
					}
				});
			}
		} catch (Exception e) {
			GameLog.error(LogModule.GM.getName(), "gmId", "GmServer[init] error:", e);
		}finally{
			if(serverSocket!=null){
				try {					
					serverSocket.close();					
				} catch (IOException e) {	
					GameLog.error(LogModule.GM.getName(), "gmId", "GmServer[init] serverSocket 关闭异常:", e);
				}
			}
		}
	}


	public void setGmAccount(String gmAccount) {
		this.gmAccount = gmAccount;
	}

	public void setGmPassword(String gmPassword) {
		this.gmPassword = gmPassword;
	}

	public void setGmHandler(GmHandler gmHandler) {
		this.gmHandler = gmHandler;
	}
	
	
	
}