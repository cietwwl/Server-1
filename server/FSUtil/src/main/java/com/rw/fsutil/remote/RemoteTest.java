package com.rw.fsutil.remote;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.rw.fsutil.remote.parse.FSIntMessagePrefix;
import com.rw.fsutil.remote.parse.FSMessageDecoder;
import com.rw.fsutil.remote.parse.FSMessageEncoder;
import com.rw.fsutil.remote.parse.FSMessageExecutor;
import com.rw.fsutil.remote.parse.FSMessagePrefix;

public class RemoteTest {

	public static void main(String[] args) throws InterruptedException {

		FSMessageDecoder<String> decoder = new FSMessageDecoder<String>() {

			@Override
			public FSMessagePrefix getPrefix() {
				return new FSIntMessagePrefix();
			}

			@Override
			public String convertToMessage(byte[] array) {
				try {
					return new String(array, "utf-8");
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException();
				}
			}
		};

		FSMessageEncoder<String> encoder = new FSMessageEncoder<String>() {

			@Override
			public byte[] encode(String msg) {
				try {
					byte[] contentBytes = msg.getBytes("utf-8");
					int contentLenght = contentBytes.length;
					ByteBuffer dataBuffer = ByteBuffer.allocate(4 + contentLenght);// 创建数据包，大小为包头长度+包体长度
					// 添加包头
					int header = contentLenght;
					dataBuffer.putInt(header);
					dataBuffer.put(contentBytes);
					return dataBuffer.array();
				} catch (Exception e) {
					return new byte[0];
				}
			}
		};

		FSMessageExecutor<String> executor = new FSMessageExecutor<String>() {

			@Override
			public void execute(String message) {
				// TODO Auto-generated method stub

			}
		};

		final RemoteChannelServer<String, String> server = new RemoteChannelServer<String, String>("192.168.2.253",
				7777, 4, 4, decoder, encoder, executor);
		Thread.sleep(1000);

		for (int i = 0; i < 10; i++) {
			new Thread() {

				public void run() {
					while (true) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						server.sendMsg("你好啊你好");
					}
				}

			}.start();
		}
	}
}
