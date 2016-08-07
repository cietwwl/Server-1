package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import com.bm.login.AccoutBM;
import com.google.protobuf.ByteString;
import com.rw.account.Account;
import com.rw.common.GameUtil;
import com.rw.netty.ChannelWriteMgr;
import com.rw.netty.UserChannelMgr;
import com.rw.platform.PlatformFactory;
import com.rw.service.RequestService;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RequestProtos.RequestHeader;
import com.rwproto.ResponseProtos;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.ResponseProtos.ResponseHeader;

public class FsNettyControler {

	public FsNettyControler(){}
	
	private Map<Command, RequestService> commandMap;
	
	public void doMyService(final Request exRequest, final ChannelHandlerContext ctx) {
		RequestHeader header = exRequest.getHeader();
		final Command command = header.getCommand();
		final int seqID = header.getSeqID();
		
		Response result = null;
		Account account;
		String accountId = UserChannelMgr.getUserId();
		if(accountId == null){
			account = new Account();
		}else{
			account = PlatformFactory.getPlatformService().getAccount(accountId);
		}
		
		if(!commandMap.containsKey(command)){
			return;
		}
		
		ByteString resultContent = getSerivice(command).doTask(exRequest, account);
		System.out.println("++++++++++++++++++++++++"+command);
		Response.Builder builder = Response.newBuilder().setHeader(getResponseHeader(exRequest, command));
		if (resultContent != null) {
			builder.setSerializedContent(resultContent);
			result = builder.build();
			sendResponse(seqID, result, ctx);
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


	private RequestService getSerivice(Command command) {
		return commandMap.get(command);
	}

	public void setCommandMap(Map<Command, RequestService> commandMap) {
		this.commandMap = commandMap;
	}
}
