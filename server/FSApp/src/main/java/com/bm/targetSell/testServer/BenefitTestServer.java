package com.bm.targetSell.testServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.lang3.StringUtils;

public class BenefitTestServer {

	
	
	public void init(){
		try {
			
			System.out.println("Benefit test server start~~");
			ServerSocket server = new ServerSocket(4700);
			while (true) {
				Socket client = server.accept();
						
				System.out.println("get remote client, ip:" + client.getInetAddress() + ",port:" + client.getPort());
				new Handler(client);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
private class Handler implements Runnable{
		
		private Socket socket;
		private DataOutputStream output;

		public Handler(Socket socket) {
			super();
			this.socket = socket;
			new Thread(this).start();
		}

		@Override
		public void run() {
			try {
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				this.output = new DataOutputStream(socket.getOutputStream());
				while (true) {
					String content = reader.readLine();
					System.out.println(content);
					String data = RecieverData.getInstance().tranformData(content);
					if(StringUtils.isNotBlank(data)){
						sendMsg(data);
					}
				}
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		private void sendMsg(String content){
			try {
				
				content += "\n";
				output.write(content.getBytes("UTF-8"));
				output.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
		
	}
}
