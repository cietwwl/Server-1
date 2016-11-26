package com.rw.fsutil.remote;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.List;

import com.rw.fsutil.remote.parse.FSIntMessagePrefix;
import com.rw.fsutil.remote.parse.FSMessageDecoder;
import com.rw.fsutil.remote.parse.FSMessageEncoder;
import com.rw.fsutil.remote.parse.FSMessageExecutor;
import com.rw.fsutil.remote.parse.FSMessagePrefix;

public class RemoteTest {

	static volatile long aa;

	public static void main(String[] args) throws InterruptedException {
		System.setProperty("io.netty.recycler.maxCapacity.default", "512");
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

		final RemoteMessageService<String, String> server = RemoteMessageServiceFactory.createService(1, "192.168.2.253",
				7777, 1, 1, decoder, encoder, executor);
		Thread.sleep(1000);

		server.sendMsg("你好啊你好");

		new Thread() {

			public void run() {
				while (true) {
					for (int i = 0; i < 2; i++) {
						try {
							if (i == 0) {
								Thread.sleep(10);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (server.sendMsg("你好啊你好你好啊你好你好啊你好你好啊你好你好啊你好你好啊你好你好啊你好你好啊你好")) {
							aa++;
						}
					}
				}
			}

		}.start();

		new Thread() {

			public void run() {
				while (true) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<RemoteServiceSender<String, String>> senders = server.getAllSenders();
					try {
						Class<?> c = Class.forName("java.nio.Bits");
						Field f1 = c.getDeclaredField("reservedMemory");
						f1.setAccessible(true);
						Field f2 = c.getDeclaredField("maxMemory");
						f2.setAccessible(true);
						RemoteServiceSender<String, String> sender = senders.get(0);
						System.out.println("reservedMemory:" + f1.get(null) + "," + "maxMemory:" + f2.get(null)
							+",addSuccess="+aa	+ ",count=" + sender.getCount() + ",success=" + sender.getSendSuccessStatCount() + ",fail=" + sender.getSendFailStatCount() + ",reject=" + sender.getSendRejectStatCount());

					} catch (Exception e) {

					}
				}
			}

		}.start();
	}

}
