package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.List;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.ServerHandler;
import com.rw.netty.UserChannelMgr;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.ReConnectionProtos.ReConnectRequest;
import com.rwproto.ReConnectionProtos.SyncVersion;
import com.rwproto.RequestProtos.Request;

public class ReconnectSecondaryTreatment implements PlayerTask {

	private static FsNettyControler nettyControler = SpringContextUtil.getBean("fsNettyControler");
	private final Request request;
	private final Long sessionId;
	private final String userId;
	private final ReConnectRequest reconnectRequest;

	public ReconnectSecondaryTreatment(Request request, Long sessionId, ReConnectRequest reconnectRequest, String userId) {
		super();
		this.request = request;
		this.sessionId = sessionId;
		this.reconnectRequest = reconnectRequest;
		this.userId = userId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(Player player) {
		ChannelHandlerContext ctx = ServerHandler.getChannelHandlerContext(sessionId);
		if (ctx == null) {
			return;
		}
		if (player == null) {
			GameLog.error("ReconnectSecondaryTreatment", "#run()", "find player fail on reconneting:" + userId);
			ReconnectCommon.getInstance().reLoginGame(nettyControler, ctx, request);
			return;
		}
		Long disconnectTime = UserChannelMgr.getDisconnectTime(userId);
		// ChannelHandlerContext oldCtx = null;
		Long oldSessionId = null;
		if (disconnectTime == null) {
			// oldCtx = UserChannelMgr.get(userId);
			oldSessionId = UserChannelMgr.getSessionId(userId);
			if (oldSessionId != null) {
				// 在线情况不处理
				if (oldSessionId.longValue() == sessionId.longValue()) {
					GameLog.error("reconnect", userId, "repeat reconnect:" + userId);
					ReconnectCommon.getInstance().reconnectSuccess(nettyControler, ctx, request, null);
					return;
				}
			} else {
				// 不在线disconnectTime == null && oldCtx == null
				ReconnectCommon.getInstance().reLoginGame(nettyControler, ctx, request);
				return;
			}
		}
		if (oldSessionId == null && (System.currentTimeMillis() - disconnectTime) > UserChannelMgr.RECONNECT_TIME) {
			ReconnectCommon.getInstance().reLoginGame(nettyControler, ctx, request);
			return;
		}
		if (!UserChannelMgr.bindUserId(userId, sessionId, false)) {
			return;
		}
		if (oldSessionId != null) {
			// oldCtx.close();
			UserChannelMgr.KickOffPlayer(sessionId, nettyControler, userId);
			GameLog.error("reconnect", userId, "remove old session:" + ServerHandler.getCtxInfo(oldSessionId));
		}
		UserChannelMgr.onBSBegin(userId);
		ByteString synData = null;
		try {
			List<SyncVersion> versionList = reconnectRequest.getVersionListList();
			if (versionList != null) {
				player.synByVersion(versionList);
			} else {
				GameLog.error("ReconnectSecondaryTreatment", "#run()", "version list is null:" + userId);
				player.synByVersion(Collections.EMPTY_LIST);
			}
		} finally {
			synData = UserChannelMgr.getDataOnBSEnd(userId);
		}
		ReconnectCommon.getInstance().reconnectSuccess(nettyControler, ctx, request, synData);
	}

}
