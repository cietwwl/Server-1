package com.gm.customer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import com.gm.gmsender.GmSend;
import com.gm.gmsender.GmSender;
import com.gm.gmsender.GmSenderConfig;
import com.gm.gmsender.GmSenderFactory;
import com.gm.util.GmUtils;
import com.log.GameLog;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.manager.GameManager;

public class QuestionSender {
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream out;
	private GmSenderConfig senderConfig;
	
	public QuestionSender(GmSenderConfig senderConfig){
		this.senderConfig = senderConfig;
	}
	
	public void init() {
		
	}
	
	private void reconnect(){
		if(socket != null){
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					GameLog.error("PlayerQuestionService", "GmSender[destroy]", "socket 关闭 出错", e);
				}
			}
		}
		socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(senderConfig.getHost(), senderConfig.getPort()));
			socket.setSoTimeout(10000);
			socket.setSendBufferSize(100 * 1024);
			input = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (Exception ex) {
			GameLog.error("PlayerQuestionService", "init QuestionSender fail:" + senderConfig.getHost() + "," + senderConfig.getPort(), ex.getMessage());
		}
	}
	
	public <T> T sendQuestion(Map<String, Object> content, int opType, Class<T> clazz){
		T gmResponse = null;
		try {
			GmSend gmSend = new GmSend();
			gmSend.setAccount(GameManager.getGmAccount());
			gmSend.setPassword(GameManager.getGmPassword());
			gmSend.setOpType(opType);
			gmSend.setArgs(content);
			String jsonContent = FastJsonUtil.serialize(gmSend);
			byte[] dataFormat = GmUtils.dataFormat(senderConfig.getProtno(), jsonContent);
			
			try{
				out.write(dataFormat);
				out.flush();
				gmResponse = GmUtils.read(input, clazz);
			}catch(IOException ex){
				reconnect();
				out.write(dataFormat);
				out.flush();
				gmResponse = GmUtils.read(input, clazz); 
			}
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			GameLog.error("PlayerQuestionService", "sendQuestion fail:", ex.getMessage());
		}
		return gmResponse;
	}
}
