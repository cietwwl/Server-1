package com.rw;

import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.common.MsgLog;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.MsgDataSynList;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RequestProtos.RequestBody;
import com.rwproto.RequestProtos.RequestHeader;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.ResponseProtos.ResponseHeader;

/*
 * 发送消息的处理
 * @author HC
 * @date 2015年12月14日 下午11:27:25
 * @Description 
 */
public abstract class ClientMsgHandler {

	private BlockingQueue<Response> resultQueue = new LinkedBlockingQueue<Response>(1);

	private MsgReciver msgReciver;

	private Response getResp() {
		Response resp = null;
		long maxTime = 30L;
		// 超过十秒拿不到认为超时。
		try {
			resp = resultQueue.poll(maxTime, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			RobotLog.fail("ServerResp[getResp] 服务器响应超时", e);
		}
		return resp;

	}

	public void setResp(Response resp) {
		if (msgReciver != null && msgReciver.getCmd() == resp.getHeader().getCommand()) {

			if (resultQueue.isEmpty()) {
				resultQueue.add(resp);
			}
		}
	}

	public void dataSyn(Response resp) {
		if (Command.MSG_DATA_SYN == resp.getHeader().getCommand()) {
			try {
				MsgDataSynList datasynList = MsgDataSynList.parseFrom(resp.getSerializedContent());
				for (MsgDataSyn msgDataSyn : datasynList.getMsgDataSynList()) {
					eSynType synType = msgDataSyn.getSynType();

					switch (synType) {
					case Store_Data:
						getClient().getStoreItemHolder().syn(msgDataSyn);
						break;
					case USER_ITEM_BAG:
						getClient().getItembagHolder().syn(msgDataSyn);
						break;
					case TASK_DATA:
						getClient().getTaskItemHolder().syn(msgDataSyn);
						break;
					// /////////////////////////////////////////////帮派数据
					case GroupBaseData:
						getClient().getGroupBaseDataHolder().syn(msgDataSyn);
						break;
					case GroupApplyMemberData:
						getClient().getApplyMemberHolder().syn(msgDataSyn);
						break;
					case GroupMemberData:
						getClient().getNormalMemberHolder().syn(msgDataSyn);
						break;
					case GroupResearchSkill:
						getClient().getResearchSkillDataHolder().syn(msgDataSyn);
						break;
					case GroupLog:
						getClient().getLogHolder().syn(msgDataSyn);
						break;
					case UserGroupAttributeData:
						getClient().getUserGroupDataHolder().syn(msgDataSyn);
						break;
					case EQUIP_ITEM:
						getClient().getHeroEquipHolder().syn(msgDataSyn);
					default:
						break;
					}
				}
			} catch (InvalidProtocolBufferException e) {
				throw (new RuntimeException("ClientMsgHandler[dataSyn] parse error", e));
			}
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param channel
	 * @param command
	 * @param userId
	 * @param token
	 * @param bytes
	 */
	public boolean sendMsg(Command command, ByteString bytes, MsgReciver msgReciverP) {
		msgReciver = msgReciverP;

		boolean success = false;
		Client client = getClient();
		RequestHeader.Builder header = RequestHeader.newBuilder();
		header.setCommand(command);
		if (client.getUserId() != null) {
			header.setUserId(client.getUserId());
		}

		if (client.getToken() != null) {
			header.setToken(client.getToken());
		}

		RequestBody.Builder body = RequestBody.newBuilder();
		if (bytes != null) {
			body.setSerializedContent(bytes);
		}

		Request.Builder request = Request.newBuilder();
		request.setHeader(header);
		request.setBody(body);

		try {

			Channel channel = ChannelServer.getInstance().getChannel(client);

			channel.writeAndFlush(request);
			MsgLog.info("发送消息 客户端Id：" + client.getAccountId() + " cmd:" + command);
			if (msgReciver != null) {
				success = handleResp(msgReciverP, client);
				msgReciver = null;
			} else {
				success = true;
			}
		} catch (Exception e) {
			RobotLog.fail("ClientMsgHandler[sendMsg] 与服务器通信异常. accountId:" + client.getAccountId(), e);
			success = false;
		}
		return success;

	}

	private boolean handleResp(MsgReciver msgReciverP, Client client) {
		boolean success = true;
		Response rsp = getResp();
		if (rsp == null) {
			RobotLog.info("ClientMsgHandler[handleResp]业务模块收到的响应超时, account:" + client.getAccountId() + " cmd:" + msgReciverP.getCmd());
			success = false;
		} else {

			ResponseHeader headerTmp = rsp.getHeader();
			if (headerTmp == null) {
				RobotLog.info("ClientMsgHandler[handleResp]业务模块收到的响应没有头, account:" + client.getAccountId());
				success = false;
			} else {
				Command commandTmp = headerTmp.getCommand();
				if (msgReciverP != null && msgReciverP.getCmd() == commandTmp) {
					success = msgReciverP.execute(client, rsp);
				}
			}

		}
		return success;
	}

	public abstract Client getClient();
}