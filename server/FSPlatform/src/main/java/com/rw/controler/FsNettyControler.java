package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import com.bm.login.AccoutBM;
import com.google.protobuf.ByteString;
import com.log.PlatformLog;
import com.rw.account.Account;
import com.rw.common.GameUtil;
import com.rw.netty.ChannelWriteMgr;
import com.rw.netty.UserChannelMgr;
import com.rw.platform.PlatformFactory;
import com.rw.platform.task.ReconnectFilterTask;
import com.rw.service.RequestService;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.MsgDef.Command;
import com.rwproto.ReConnectionProtos.ReConnectRequest;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RequestProtos.RequestHeader;
import com.rwproto.ResponseProtos;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.ResponseProtos.ResponseHeader;

public class FsNettyControler {

	public FsNettyControler(){}
	
	private Map<Command, RequestService> commandMap;
	
	public void doMyService(final Request exRequest, final ChannelHandlerContext ctx) {
		long current = System.currentTimeMillis();
		RequestHeader header = exRequest.getHeader();
		final Command command = header.getCommand();
		final int seqID = header.getSeqID();
		
		UserChannelMgr.updateSessionInfo(ctx, current, command);
		Response result = null;
		Account account;
		String accountId = UserChannelMgr.getUserId();
		if(accountId == null){
			account = new Account();
		}else{
			account = PlatformFactory.getPlatformService().getAccount(accountId);
		}
		if (command == Command.MSG_RECONNECT) {
			ReConnect(exRequest, ctx);
		} else {
			if(!commandMap.containsKey(command)){
				return;
			}
			ByteString resultContent = getSerivice(command).doTask(exRequest, account);
			System.out.println("++++++++++++++++++++++++" + command);
			Response.Builder builder = Response.newBuilder().setHeader(getResponseHeader(exRequest, command));
			if (resultContent != null) {
				builder.setSerializedContent(resultContent);
				result = builder.build();
				sendResponse(seqID, result, ctx);
			}
		}
	}

	public void sendErrorResponse(Request req, int exceptionCode, ChannelHandlerContext ctx) {
		RequestHeader header = req.getHeader();
		Command command = header.getCommand();
		String token = req.getHeader().getToken();
		ResponseHeader.Builder responseHeaderBuilder = ResponseHeader.newBuilder().setToken(token).setCommand(command).setStatusCode(exceptionCode);
		Response.Builder builder = Response.newBuilder().setHeader(responseHeaderBuilder);
		builder.setSerializedContent(ByteString.EMPTY);
		Response response = builder.build();
		sendResponse(header.getSeqID(), response, ctx);
	}
	
	public void sendResponse(int seqID, Response result, ChannelHandlerContext ctx) {
		if (result == null)
			return;
		if (!GameUtil.checkMsgSize(result))
			return;

		ResponseProtos.Response.Builder response = ResponseProtos.Response.newBuilder();
		ResponseHeader.Builder header = ResponseHeader.newBuilder();
		header.setSeqID(seqID);
		header.mergeFrom(result.getHeader());
		response.setHeader(header.build());
		response.setSerializedContent(result.getSerializedContent());
		
		ChannelWriteMgr.write(ctx.channel(), response.build());
	}
	
	public ResponseHeader getResponseHeader(Request req, Command command) {
		String token = req.getHeader().getToken();
		ResponseHeader.Builder responseHeaderBuilder = ResponseHeader.newBuilder().setToken(token).setCommand(command).setStatusCode(200);

		return responseHeaderBuilder.build();
	}
	
	private void ReConnect(Request request, ChannelHandlerContext ctx) {
		try {
			ReConnectRequest reconnectRequest = ReConnectRequest.parseFrom(request.getBody().getSerializedContent());
			String accountId = reconnectRequest.getAccountId();
			int zoneId = reconnectRequest.getZoneId();
			TableAccount userAccount = AccoutBM.getInstance().getByAccountId(accountId);
			if (userAccount == null) {
				PlatformLog.error("FsNettyControler", "#ReConnect()", "find account fail on reconnecting:" + accountId + "," + zoneId);
				ReconnectCommon.getInstance().reLoginGame(this, ctx, request);
				return;
			}
			GameWorldFactory.getGameWorld().executeAccountTask(accountId, new ReconnectFilterTask(request, reconnectRequest, ctx));
		} catch (Exception ex) {
			PlatformLog.error("PlayerReconnectTask", "#run()", "parse reconnect protocol exception:"+ex.getMessage());
			ReconnectCommon.getInstance().reLoginGame(this, ctx, request);
			return;
		}
	}


	private RequestService getSerivice(Command command) {
		return commandMap.get(command);
	}

	public void setCommandMap(Map<Command, RequestService> commandMap) {
		this.commandMap = commandMap;
	}
}
