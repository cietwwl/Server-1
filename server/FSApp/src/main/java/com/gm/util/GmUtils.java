package com.gm.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;

import com.gm.GmRequest;
import com.rw.fsutil.log.GmLog;
import com.rw.fsutil.util.fastjson.FastJsonUtil;

public class GmUtils {

	
	public static String parseString(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		if(object == null){
			return "";
		}else{
			return object.toString();
		}
	}
	
	public static int parseInt(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		if(object == null){
			return 0;
		}else{
			int value = 0;
			try {
				value = Integer.parseInt(object.toString());
			} catch (Exception ex) {
				value = 0;
			}
			return value;
		}
	}
	
	public static long parseLong(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		if(object == null){
			return 0;
		}else{
			long value = 0;
			try {
				value = Long.parseLong(object.toString());
			} catch (Exception ex) {
				value = 0;
			}
			return value;
		}
	}
	
	public static byte[] dataFormat(short protno, String json) throws UnsupportedEncodingException {
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

		// 包体计算：包体=协议号(2个字节) + json字符串长度(2字节) + json字符串内容
		dataInfo.putShort(protno);
		dataInfo.putShort(jsonLen);

		dataInfo.put(jsonbytes);
		return dataInfo.array();
	}
	
	public static <T> T read(DataInputStream input, Class<T> clazz) throws IOException {
		T content = null;
		input.readInt();
		input.readShort();
		short jsonLength = input.readShort();

		byte[] jsonBody = new byte[jsonLength];
		input.read(jsonBody);
		String json = new String(jsonBody, "utf-8");
		content = FastJsonUtil.deserialize(json, clazz);
		GmLog.info("SocketHelper[read] 处理gm请求：" + json);
		return content;
	}
}
