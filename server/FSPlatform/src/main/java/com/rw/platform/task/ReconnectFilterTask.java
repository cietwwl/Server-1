package com.rw.platform.task;

import io.netty.channel.ChannelHandlerContext;

import com.log.PlatformLog;
import com.rw.controler.FsNettyControler;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.ReconnectSecondaryTreatment;
import com.rw.netty.UserChannelMgr;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.ReConnectionProtos.ReConnectRequest;
import com.rwproto.ReConnectionProtos.ReConnectResponse;
import com.rwproto.ReConnectionProtos.ReConnectResultType;
import com.rwproto.RequestProtos.Request;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User;

public class ReconnectFilterTask implements Runnable {

	private static FsNettyControler nettyControler = SpringContextUtil.getBean("fsNettyControler");
	private Request request;
	private ReConnectRequest reconnectRequest;
	private ChannelHandlerContext ctx;

	public ReconnectFilterTask(Request request, ReConnectRequest reconnectRequest, ChannelHandlerContext ctx) {
		super();
		this.request = request;
		this.reconnectRequest = reconnectRequest;
		this.ctx = ctx;
	}

	@Override
	public void run() {
		String accountId = reconnectRequest.getAccountId();
		// 真正处理逻辑
		GameWorldFactory.getGameWorld().executeAccountTask(accountId, new ReconnectSecondaryTreatment(request, ctx, reconnectRequest, accountId));
	}
}
