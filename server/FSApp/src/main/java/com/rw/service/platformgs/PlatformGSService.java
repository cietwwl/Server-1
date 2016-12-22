package com.rw.service.platformgs;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.bm.serverStatus.ServerStatus;
import com.bm.serverStatus.ServerStatusMgr;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.PlayerMgr;
import com.playerdata.activityCommon.timeControl.ActivitySpecialTimeMgr;
import com.rw.fsutil.shutdown.IShutdownHandler;
import com.rw.fsutil.shutdown.ShutdownService;
import com.rw.manager.GameManager;
import com.rw.netty.UserChannelMgr;
import com.rw.service.http.platformResponse.ServerBaseDataResponse;
import com.rw.service.platformService.PlatformService;
import com.rwbase.common.enu.EServerStatus;
import com.rwproto.PlatformGSMsg.UserInfoRequest;
import com.rwproto.PlatformGSMsg.ePlatformGSMsgType;
import com.rwproto.RequestProtos.Request;

public class PlatformGSService implements IShutdownHandler {
	
	//private static boolean IS_FIRST_INFORM = true;
	
	private static PlatformGSHandler platformGSHandler = PlatformGSHandler
			.getInstance();

	private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	public static void init() {
		executor.scheduleAtFixedRate(new TServerStatus(), 5, 5, TimeUnit.SECONDS);
		ShutdownService.registerShutdownService(new PlatformGSService());
	}

	private static class TServerStatus implements Runnable {

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
			serverBaseDataResponse.setActivityTimeVersion(ActivitySpecialTimeMgr.VERSION.get());
			serverBaseDataResponse.setStatus(blnShutdown ? EServerStatus.CLOSE.getStatusId() : getStatus());
			PlatformService.SendResponse("com.rw.netty.http.requestHandler.ServerStatusHandler", "notifyServerData", serverBaseDataResponse, ServerBaseDataResponse.class);
		} catch (Exception ex) {

		}
	}

	public static int getStatus() {
		ServerStatus status = ServerStatusMgr.getStatus();
		return status.ordinal();
	}

	@Override
	public void notifyShutdown() {
		// TODO Auto-generated method stub
		PlatformGSService.notifyServerStatusToPlatform(true);
	}

	public static void doTask(Request request, ChannelHandlerContext ctx) {
		ByteString result = null;
		ePlatformGSMsgType type = null;
		try {
			UserInfoRequest userRequest = UserInfoRequest.parseFrom(request.getBody().getSerializedContent());
			type = userRequest.getPlatformGSMsgType();
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
		UserChannelMgr.sendResponse(null, request.getHeader(), type, result, 200, ctx, null);
	}
}
