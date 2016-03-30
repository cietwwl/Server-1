package com.gm.gmsender;

import java.io.DataInputStream;
import java.io.IOException;

import com.rw.fsutil.log.GmLog;
import com.rw.fsutil.util.fastjson.FastJsonUtil;

/*
 * @author HC
 * @date 2016年3月29日 上午10:08:34
 * @Description 
 */
public class GiftCodeSocketHelper {

	public static <T> T read(DataInputStream input, Class<T> clazz) throws IOException {
		T content = null;
		// int len = input.readInt();
		// short readShort = input.readShort();
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