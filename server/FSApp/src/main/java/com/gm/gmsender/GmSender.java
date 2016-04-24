package com.gm.gmsender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;

import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.manager.GameManager;

public class GmSender {

	private Socket socket;
	private DataOutputStream output;
	private DataInputStream input;
	private short protno;
	final GmSenderConfig gmSenderConfig;
	
	private boolean available = true;

	public GmSender(GmSenderConfig senderConfig) throws IOException {
		gmSenderConfig = senderConfig;
		this.protno = senderConfig.getProtno();
		connect(senderConfig);		
	}	
	private void connect(GmSenderConfig senderConfig) throws IOException{
		this.socket = new Socket(senderConfig.getHost(), senderConfig.getPort());
		this.socket.setSoTimeout(senderConfig.getTimeoutMillis());
		this.output = new DataOutputStream(socket.getOutputStream());
		this.input = new DataInputStream(socket.getInputStream());
	}

	public boolean isAvailable() {
		return available && socket.isConnected() && !socket.isClosed();
	}	
	
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public <T> T send(Map<String, Object> content, Class<T> clazz) throws IOException {

		GmSend gmSend = new GmSend();
		gmSend.account = GameManager.getGmAccount();
		gmSend.password = GameManager.getGmPassword();
		gmSend.opType = 20039;
		gmSend.args = content;
		String jsonContent = FastJsonUtil.serialize(gmSend);
		byte[] dataFormat = dataFormat(protno, jsonContent);
		T gmResponse = null;
		try {
			output.write(dataFormat);
			output.flush();
			gmResponse = GiftCodeSocketHelper.read(input, clazz);
		} catch (IOException e) {	
			GameLog.error(LogModule.GmSender, "GmSender[send]", "第一次发送出现异常,重发", e);
			//出错重连，再出错则直接抛出异常.
			reconect();
			output.write(dataFormat);
			output.flush();
			gmResponse = GiftCodeSocketHelper.read(input, clazz); 
		}

		return gmResponse;
	}
	
	private void reconect() throws IOException{
		destroy();
		connect(gmSenderConfig);		
	}
	

	public class GmSend {
		private String account;
		private Map<String, Object> args;
		private int opType;
		private String password;

		public String getAccount() {
			return account;
		}

		public void setAccount(String account) {
			this.account = account;
		}

		public Map<String, Object> getArgs() {
			return args;
		}

		public void setArgs(Map<String, Object> args) {
			this.args = args;
		}

		public int getOpType() {
			return opType;
		}

		public void setOpType(int opType) {
			this.opType = opType;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	private byte[] dataFormat(short protno, String json) throws UnsupportedEncodingException {
		// 包头计算：包头(short,4个字节) = 包体长度%255(1个字节) + 包体长度&0x00ffffff(3个字节)
		byte[] jsonbytes = json.getBytes("utf-8");
		short jsonLen = (short) jsonbytes.length;
		int bodyLength = 2 + 2 + jsonLen;
		bodyLength = 1;

		int header = ((bodyLength % 255) << 24) | (bodyLength & 0x00ffffff);
		ByteBuffer dataInfo = ByteBuffer.allocate(8 + jsonLen);
		// 转换为小端模式，默认为大端。
		// dataInfo.order(ByteOrder.LITTLE_ENDIAN);
		// 设置包头
		dataInfo.putInt(header);
		// System.out.println("header:" + header);
		// 包体计算：包体=协议号(2个字节) + json字符串长度(2字节) + json字符串内容
		dataInfo.putShort(protno);
		dataInfo.putShort(jsonLen);
		// System.out.println("jsonLen:" + jsonLen);
		dataInfo.put(jsonbytes);
		return dataInfo.array();
	}

	public void destroy() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				GameLog.error(LogModule.GmSender, "GmSender[destroy]", "socket 关闭 出错", e);
			}
		}
	}

}
