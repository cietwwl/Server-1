package com.rw.account;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.log.PlatformLog;
import com.rw.common.GameUtil;
import com.rw.netty.MsgSaveVO;
import com.rw.netty.UserChannelMgr;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.dao.whiteList.TableWhiteList;
import com.rwbase.dao.whiteList.TableWhiteListHolder;
import com.rwproto.CommonMsgProtos.CommonMsgResponse;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.MsgDef;
import com.rwproto.MsgDef.Command;
import com.rwproto.MsgRsProtos.EMsgType;
import com.rwproto.MsgRsProtos.MsgMsgRsResponse;
import com.rwproto.ResponseProtos;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.ResponseProtos.ResponseHeader;

public class Account {
	private String accountId;
	private Map<Integer, MsgSaveVO> msgHashtable = new HashMap<Integer, MsgSaveVO>();// 保存未发送成功的信息
	private int msgIndexId = 0;// 保存未发送成功的信息Id
	private TableAccount tableAccount;
	private TableWhiteListHolder whiteList;
	
	public Account(){
		
	}
	
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public TableAccount getTableAccount() {
		return tableAccount;
	}

	public void setTableAccount(TableAccount tableAccount) {
		this.tableAccount = tableAccount;
		init();
	}

	public boolean isWhiteList() {
		if(whiteList == null){
			return false;
		}
		TableWhiteList tableWhiteList = whiteList.getTableWhiteList();
		return tableWhiteList == null ? false : !tableWhiteList.isClose();
	}
	
	private void init(){
		whiteList = new TableWhiteListHolder(this.tableAccount.getAccountId());
	}

	public void NotifyCommonMsg(ECommonMsgTypeDef type, String message) {
		if (message == null || message.equals("")) {
			return;
		}
		CommonMsgResponse.Builder response = CommonMsgResponse.newBuilder();
		response.setType(type.getValue());
		response.setMessage(message);
		response.setError(ErrorType.SUCCESS);
		PlatformLog.debug(message);
		// DevelopLogger.info("common message", "player", "", message, "");
		SendMsg(Command.MSG_COMMON_MESSAGE, response.build().toByteString());
	}
	
	public void SendMsg(MsgDef.Command Cmd, ByteString pBuffer){
		try {
			ChannelHandlerContext ctx = UserChannelMgr.get(getAccountId());
			if (ctx == null) {
				return;
			}
			Response.Builder builder = Response.newBuilder().setHeader(ResponseHeader.newBuilder().setCommand(Cmd).setToken("").setStatusCode(200));
			if (pBuffer != null) {
				builder.setSerializedContent(pBuffer);
			}

			ResponseProtos.Response.Builder response = ResponseProtos.Response.newBuilder();
			ResponseHeader.Builder header = ResponseHeader.newBuilder();
			header.mergeFrom(builder.getHeader());
			header.setStatusCode(200);
			response.setHeader(header.build());
			response.setSerializedContent(builder.getSerializedContent());
			if (!GameUtil.checkMsgSize(response, this)) {
				UserChannelMgr.removeThreadLocalCTX();
				return;
			}
			addMsgMap(Cmd, pBuffer, response);
			PlatformLog.debug("发送消息" + "  " + response.getHeader().getCommand().toString() + "  Size:" + response.getSerializedContent().size());
			ctx.channel().write(response.build());
			ctx.channel().flush();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void addMsgMap(MsgDef.Command Cmd, ByteString pBuffer, ResponseProtos.Response.Builder response) {
		if (MsgDef.Command.MSG_Rs_DATA == Cmd) {
			return;
		}
		msgIndexId++;
		MsgMsgRsResponse.Builder res = MsgMsgRsResponse.newBuilder();
		res.setId(msgIndexId);
		res.setType(EMsgType.ServerMsg);

		msgHashtable.put(res.getId(), new MsgSaveVO(Cmd, pBuffer));
		response.setNum(msgIndexId);

	}
}
