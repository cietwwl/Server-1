package com.rw.service.platformService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.activityCommon.timeControl.ActivitySpecialTimeMgr;
import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.manager.GameManager;
import com.rw.service.http.request.RequestObject;
import com.rw.service.http.request.ResponseObject;

public class PlatformService {

	public static ConcurrentLinkedQueue<RequestObject> TaskQuene = new ConcurrentLinkedQueue<RequestObject>();
	private static ExecutorService plService = Executors.newFixedThreadPool(16, new SimpleThreadFactory("Platform Service"));

	public static void init() {
	}

	public static void addRequest(final RequestObject requestObject) {
		plService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendPlatformRequest(requestObject);
			}
		});
	}

	private static ResponseObject processPlatformServiceRequest(String ip, int port, RequestObject requestObject) {
		Socket socket = null;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port));

			socket.setSoTimeout(10000);
			socket.setSendBufferSize(100 * 1024);
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			String json = FastJsonUtil.serialize(requestObject);

			byte[] jsonbytes = json.getBytes("utf-8");
			short jsonLen = (short) jsonbytes.length;
			ByteBuffer dataInfo = ByteBuffer.allocate(4 + jsonLen);

			dataInfo.putInt(jsonLen);
			dataInfo.put(jsonbytes);

			byte[] array = dataInfo.array();

			out.write(array);
			out.flush();

			ResponseObject responseObject = read(input, ResponseObject.class);

			out.close();
			input.close();

			return responseObject;
		} catch (Exception ex) {
			GameLog.error("PlatformService", "processPlatformServiceRequest:"+ip+","+port, ex.getMessage());
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return null;
	}

	private static void sendPlatformRequest(RequestObject requestObject) {
		try {
			List<PlatformInfo> platformInfos = GameManager.getPlatformInfos();
			for (PlatformInfo platformInfo : platformInfos) {
				ResponseObject reponse = processPlatformServiceRequest(platformInfo.getIp(), platformInfo.getPort(), requestObject);
				if(reponse == null){
					continue;
				}else if(null != reponse.getActTimeInfo()){
					ActivitySpecialTimeMgr.getInstance().decodeActivityTimeInfo(reponse.getActTimeInfo());
				}
				if (StringUtils.isBlank(reponse.getResult()) && requestObject.isBlnNotifySingle()) {
					break;
				}
			}
		} catch (Exception e) {
		}
	}

	public static <T> T read(DataInputStream input, Class<T> clazz) throws IOException {

		T content = null;
		int len = input.readInt();

		byte[] jsonBody = new byte[len];
		input.read(jsonBody);
		String json = new String(jsonBody, "utf-8");
		content = FastJsonUtil.deserialize(json, clazz);

		return content;
	}

	public static boolean checkPlatformOpen() {
		List<PlatformInfo> platformInfos = GameManager.getPlatformInfos();
		for (PlatformInfo platformInfo : platformInfos) {
			try {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(platformInfo.getIp(), platformInfo.getPort()));
				if (socket.isConnected()) {
					return true;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return false;
	}

	public static void SendResponse(String classPath, String methodName, Object object, Class<?> classValue) {
		RequestObject requestObject = new RequestObject();
		requestObject.pushParam(classValue, object);
		requestObject.setClassName(classPath);
		requestObject.setMethodName(methodName);
		sendPlatformRequest(requestObject);
	}

	public static void main(String[] objs) {

	}
}
