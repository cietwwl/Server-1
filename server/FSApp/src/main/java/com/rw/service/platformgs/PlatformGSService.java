package com.rw.service.platformgs;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.bm.serverStatus.ServerStatus;
import com.bm.serverStatus.ServerStatusMgr;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.shutdown.IShutdownHandler;
import com.rw.fsutil.shutdown.ShutdownService;
import com.rw.manager.GameManager;
import com.rw.service.http.GSRequestAction;
import com.rw.service.http.platformResponse.ServerBaseDataResponse;
import com.rwbase.common.enu.EServerStatus;
import com.rwproto.PlatformGSMsg.UserInfoRequest;
import com.rwproto.PlatformGSMsg.ePlatformGSMsgType;
import com.rwproto.PlatformGSMsg.eServerStatusType;
import com.rwproto.RequestProtos.Request;

public class PlatformGSService implements IShutdownHandler{
	private static PlatformGSHandler platformGSHandler = PlatformGSHandler
			.getInstance();
	
	private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	public static void init(){
		executor.scheduleAtFixedRate(new TServerStatus(), 5, 5, TimeUnit.SECONDS);
		ShutdownService.registerShutdownService(new PlatformGSService());
	}

	private static class TServerStatus implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			notifyServerStatusToPlatform(false);
		}
		
	}
	
	public static void notifyServerStatusToPlatform(boolean blnShutdown) {
		try {
			int size = PlayerMgr.getInstance().getAllPlayer().size();
			ServerBaseDataResponse serverBaseDataResponse = new ServerBaseDataResponse();
			serverBaseDataResponse.setZoneId(GameManager.getZoneId());
			serverBaseDataResponse.setOnlineNum(size);
			serverBaseDataResponse.setStatus(blnShutdown ? EServerStatus.CLOSE.getStatusId() : getStatus());
			for (String strUrl : GameManager.getPlatformUrls()) {
				try {
					GSRequestAction requestAction = new GSRequestAction();
					requestAction.pushParams(ServerBaseDataResponse.class, serverBaseDataResponse);
					requestAction.remoteCall(strUrl, "com.rw.netty.http.requestHandler.ServerStatusHandler", "notifyServerData");
				} catch (Exception ex) {
					continue;
				}
			}
		} catch (Exception ex) {

		}
	}
	
	public static int getStatus(){
		ServerStatus status = ServerStatusMgr.getStatus();
		return status.ordinal();
	}

	@Override
	public void notifyShutdown() {
		// TODO Auto-generated method stub
		PlatformGSService.notifyServerStatusToPlatform(true);
	}

	public static ByteString doTask(Request request) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			UserInfoRequest userRequest = UserInfoRequest.parseFrom(request
					.getBody().getSerializedContent());
			ePlatformGSMsgType type = userRequest.getPlatformGSMsgType();
			switch (type) {
			case USER_INFO:
				result = platformGSHandler.getUserInfo(userRequest);
				break;
			case USER_STATUS:
				result = platformGSHandler.processKickPlayerOnline(userRequest);
				break;
			default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
