package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.List;

import com.log.GameLog;
import com.playerdata.Player;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.UserChannelMgr;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.ReConnectionProtos.ReConnectRequest;
import com.rwproto.ReConnectionProtos.SyncVersion;
import com.rwproto.RequestProtos.Request;

public class ReconnectSecondaryTreatment implements PlayerTask {

	private static FsNettyControler nettyControler = SpringContextUtil.getBean("fsNettyControler");
	private final Request request;
	private final ChannelHandlerContext ctx;
	private final String userId;
	private final ReConnectRequest reconnectRequest;

	public ReconnectSecondaryTreatment(Request request, ChannelHandlerContext ctx, ReConnectRequest reconnectRequest, String userId) {
		super();
		this.request = request;
		this.ctx = ctx;
		this.reconnectRequest = reconnectRequest;
		this.userId = userId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(Player player) {
		if (player == null) {
			GameLog.error("ReconnectSecondaryTreatment", "#run()", "find player fail on reconneting:" + userId);
			ReconnectCommon.getInstance().reLoginGame(nettyControler, ctx, request);
			return;
		}
		Long disconnectTime = UserChannelMgr.getDisconnectTime(userId);
		ChannelHandlerContext oldCtx = null;
		if (disconnectTime == null) {
			oldCtx = UserChannelMgr.get(userId);
			if (oldCtx != null) {
				// 在线情况不处理
				if (oldCtx == ctx) {
					GameLog.error("reconnect", userId, "repeat reconnect:"+userId);
					ReconnectCommon.getInstance().reconnectSuccess(nettyControler, ctx, request);
					return;
				}
			} else {
				// 不在线disconnectTime == null && oldCtx == null
				ReconnectCommon.getInstance().reLoginGame(nettyControler, ctx, request);
				return;
			}
		}
		if (oldCtx == null && (System.currentTimeMillis() - disconnectTime) > UserChannelMgr.RECONNECT_TIME) {
			ReconnectCommon.getInstance().reLoginGame(nettyControler, ctx, request);
			return;
		}
		if (!UserChannelMgr.bindUserID(userId, ctx, false)) {
			return;
		}
		if (oldCtx != null) {
			oldCtx.close();
			GameLog.error("reconnect", userId, "remove old session:" + UserChannelMgr.getCtxInfo(oldCtx));
		}
		UserChannelMgr.onBSBegin(userId);
		try {
			List<SyncVersion> versionList = reconnectRequest.getVersionListList();
			if (versionList != null) {
				player.synByVersion(versionList);
			} else {
				GameLog.error("ReconnectSecondaryTreatment", "#run()", "version list is null:" + userId);
				player.synByVersion(Collections.EMPTY_LIST);
			}
		} finally {
			UserChannelMgr.onBSEnd(userId);
		}
		ReconnectCommon.getInstance().reconnectSuccess(nettyControler, ctx, request);
	}

}
