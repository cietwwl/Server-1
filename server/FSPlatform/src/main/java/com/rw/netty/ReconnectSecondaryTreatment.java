package com.rw.netty;

import io.netty.channel.ChannelHandlerContext;

import com.log.PlatformLog;
import com.rw.controler.FsNettyControler;
import com.rw.controler.ReconnectCommon;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.ReConnectionProtos.ReConnectRequest;
import com.rwproto.RequestProtos.Request;

public class ReconnectSecondaryTreatment implements Runnable{
	private static FsNettyControler nettyControler = SpringContextUtil.getBean("fsNettyControler");
	private final Request request;
	private final ChannelHandlerContext ctx;
	private final String accountId;
	private final ReConnectRequest reconnectRequest;
	
	public ReconnectSecondaryTreatment(Request request, ChannelHandlerContext ctx, ReConnectRequest reconnectRequest, String accountId) {
		super();
		this.request = request;
		this.ctx = ctx;
		this.reconnectRequest = reconnectRequest;
		this.accountId = accountId;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		ChannelHandlerContext oldCtx = null;
		oldCtx = UserChannelMgr.get(accountId);
		if (oldCtx != null) {
			// 在线情况不处理
			if (oldCtx == ctx) {
				PlatformLog.error("reconnect", accountId, "repeat reconnect:"+accountId);
				ReconnectCommon.getInstance().reconnectSuccess(nettyControler, ctx, request, null);
				return;
			}
		} else {
			// 不在线disconnectTime == null && oldCtx == null
			ReconnectCommon.getInstance().reLoginGame(nettyControler, ctx, request);
			return;
		}
		if (!UserChannelMgr.bindUserID(accountId, ctx)) {
			return;
		}
		ReconnectCommon.getInstance().reconnectSuccess(nettyControler, ctx, request, null);
	}
}
