package com.bm.targetSell.net;

import java.nio.ByteBuffer;

import com.rw.fsutil.remote.parse.FSMessageEncoder;

public class BenefitMsgEncoder implements FSMessageEncoder<String>{

	@Override
	public byte[] encode(String msg) {
		try {
			byte[] contentBytes = msg.getBytes("utf-8");
			int contentLenght = contentBytes.length;
			ByteBuffer dataBuffer = ByteBuffer.allocate(4 + contentLenght);// 创建数据包，大小为包头长度+包体长度
			// 添加包头
			int header = contentLenght ^ BenefitMsgController.MSG_KEY;
			dataBuffer.putInt(header);
			dataBuffer.put(contentBytes);
			return dataBuffer.array();
		} catch (Exception e) {
			return new byte[0];
		}
	}

}
