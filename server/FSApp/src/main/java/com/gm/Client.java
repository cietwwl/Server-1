package com.gm;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.gm.util.SocketHelper;

public class Client {
	public static final String IP_ADDR = "localhost";//服务器地址 
	public static final int PORT = 12345;//服务器端口号  
	
    public static void main(String[] args) throws IOException {  
        System.out.println("客户端启动...");  
        //创建一个流套接字并将其连接到指定主机上的指定端口号
        Socket socket = new Socket(IP_ADDR, PORT);  
        
        //读取服务器端数据  
        DataInputStream input = new DataInputStream(socket.getInputStream());  
        //向服务器端发送数据  
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());  
        
        GmRequest request = new GmRequest();
        request.setOpType(20008);
        request.setAccount("gm");
        request.setPassword("123456");
        
        Map<String, Object> prams = new HashMap<String, Object>();
        prams.put("noticeId", 0);
        prams.put("title", "Hello3");
        prams.put("content", "Hello Everyone~~~~~~~~333");
        prams.put("startTime", System.currentTimeMillis()/1000);
        prams.put("endTime", System.currentTimeMillis()/1000 + 10 * 60);
        prams.put("cycleInterval", "0");
        prams.put("priority", "1");
        request.setArgs(prams);
        
        SocketHelper.write(output, request);  
        output.flush();
        
        try {
        	GmResponse response = SocketHelper.read(input, GmResponse.class);
        	if (response!=null) {  
        		System.out.println("客户端将关闭连接");  
        		System.out.println("服务器端返回过来的是: " + response.getStatus());  
        	}  
        	output.close();
        	input.close();
        	Thread.sleep(500);  
        } catch (Exception e) {
        	System.out.println("客户端异常:" + e.getMessage()); 
        } finally {
        	if (socket != null) {
        		try {
        			socket.close();
        		} catch (IOException e) {
        			socket = null; 
        			System.out.println("客户端 finally 异常:" + e.getMessage()); 
        		}
        	}
        }
    }  
}  