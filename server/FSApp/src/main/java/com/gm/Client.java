package com.gm;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
        request.setOpType(1001);
        request.setAccount("account");
        request.setPassword("password");
//        request.setArgs("{'value':0}");
        
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