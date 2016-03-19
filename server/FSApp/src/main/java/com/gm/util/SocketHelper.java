package com.gm.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.rw.fsutil.log.GmLog;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.fsutil.util.jackson.JsonUtil;

public class SocketHelper {

	final static short PROTO_NO = 11;

	public static <T> T read(DataInputStream input, Class<T> clazz) throws IOException {

		T content = null;
		int len = input.readInt();
		int firstLen = len >>> 24;
		int realLength = len & 0x00ffffff;
		if (firstLen == (realLength % 255)) {
			short readShort = input.readShort();
			if (PROTO_NO == readShort) {
				short jsonLength = input.readShort();

				byte[] jsonBody = new byte[jsonLength];
				input.read(jsonBody);
				String json = new String(jsonBody, "utf-8");
				content = FastJsonUtil.deserialize(json, clazz);
				GmLog.info("SocketHelper[read] 处理gm请求：" + json);
			}

		}

		return content;
	}

	public static void write(DataOutputStream output, Object target) throws IOException {
		String json = FastJsonUtil.serialize(target);
		GmLog.info("SocketHelper[write] 返回gm响应：" + json);
		byte[] bytes = dataFormat(PROTO_NO, json);
		output.write(bytes);
	}

	private static byte[] dataFormat(short protno, String json) throws UnsupportedEncodingException {
		// 包头计算：包头(short,4个字节) = 包体长度%255(1个字节) + 包体长度&0x00ffffff(3个字节)
		short jsonLen = (short) json.getBytes("utf-8").length;
		int bodyLength = 2 + 2 + jsonLen;
		int header = ((bodyLength % 255) << 24) | (bodyLength & 0xffffff);
		ByteBuffer dataInfo = ByteBuffer.allocate(8 + jsonLen);
		// 转换为小端模式，默认为大端。
		// dataInfo.order(ByteOrder.LITTLE_ENDIAN);
		// 设置包头
		dataInfo.putInt(header);

		// 包体计算：包体=协议号(2个字节) + json字符串长度(2字节) + json字符串内容
		dataInfo.putShort(protno);
		dataInfo.putShort(jsonLen);
		dataInfo.put(json.getBytes("utf-8"));
		return dataInfo.array();
	}

	public static void processException(Exception ex, GmResponse response) {
		int status;
		try {
			status = Integer.getInteger(ex.getMessage());
		} catch (Exception ex1) {
			status = GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus();
		}
		response.setStatus(status);
		response.setCount(1);
	}
}
