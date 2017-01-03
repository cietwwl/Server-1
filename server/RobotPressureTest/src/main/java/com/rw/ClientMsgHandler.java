package com.rw;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.ChatServiceProtos.MsgChatResponse;
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

	private LinkedBlockingQueue<Response> resultQueue = new LinkedBlockingQueue<Response>(1);

	private MsgReciver msgReciver;

	private volatile long lastExecuteTime;
	private AtomicBoolean isOffLine = new AtomicBoolean(false); // 是否下线

	private static AtomicInteger generator = new AtomicInteger();
	private final int id;
	private final String name;

	public ClientMsgHandler() {
		this.id = generator.incrementAndGet();
		this.name = "机器人[" + id + "]";
		RobotLog.testInfo("创建机器人：" + name + getClient().getAccountId() + "," + getClient());
	}

	public String getName() {
		return name + " accountId=" + getClient().getAccountId();
	}

	private Response getResp(int seqId, MsgReciver msgReceiverP) {
		Response resp = null;
		long maxTime = 20L;
		// 超过十秒拿不到认为超时。
		long start = System.currentTimeMillis();
		if (lastExecuteTime > 0) {
			RobotLog.testInfo(getName() + " 间隔时间：" + (start - lastExecuteTime));
		}
		try {
			resp = resultQueue.poll(maxTime, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			RobotLog.testException("ServerResp[getResp] 接收线程interrupted", e);
		}
		long current = System.currentTimeMillis();
		lastExecuteTime = current;
		long cost = current - start;
		// if (cost > 1000) {
		RobotLog.testInfo(getName() + " 处理耗时=" + cost + "cmd=," + msgReceiverP.getCmd() + ",seqId=" + seqId + "," + getClient());
		// }
		return resp;

	}

	public void setResp(Response resp) {
		if (msgReciver != null && msgReciver.getCmd() == resp.getHeader().getCommand()) {

			if (resultQueue.isEmpty()) {
				resultQueue.add(resp);
			}
		}
	}

	/**
	 * 处理顶号的情况
	 * 
	 * @param resp
	 */
	public void processKickOff(Response resp) {
		if (resp.getHeader().getCommand() == Command.MSG_PLAYER_OFF_LINE) {
			isOffLine.getAndSet(true);
		}
		if (resp.getHeader().getCommand() == Command.MSG_LOGIN_PLATFORM || resp.getHeader().getCommand() == Command.MSG_LOGIN_PLATFORM) {
			isOffLine.getAndSet(false);
		}
	}

	public void dataSyn(Response resp) {

		ByteString synData = resp.getHeader().getSynData();
		MsgDataSynList datasynList = null;
		try {

			if (Command.MSG_DATA_SYN == resp.getHeader().getCommand()) {
				datasynList = MsgDataSynList.parseFrom(resp.getSerializedContent());
			} else if (synData != null) {
				datasynList = MsgDataSynList.parseFrom(synData);
			}

			if (datasynList != null) {
				for (MsgDataSyn msgDataSyn : datasynList.getMsgDataSynList()) {
					eSynType synType = msgDataSyn.getSynType();
					// RobotLog.fail("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ +++synType= "
					// + synType.getNumber() + " i =" + i);
					// i++;
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
						break;
					case ActivityCountType:
						getClient().getActivityCountHolder().syn(msgDataSyn);
						break;
					case ActivityDailyType:
						getClient().getActivityDailyCountHolder().syn(msgDataSyn);
						break;
					case FRESHER_ATIVITY_DATA:
						getClient().getFresherActivityHolder().syn(msgDataSyn);
						break;
					case SECRETAREA_BASE_INFO:
						getClient().getGroupSecretBaseInfoSynDataHolder().syn(msgDataSyn);
						break;
					case SECRETAREA_TEAM_INFO:
						getClient().getGroupSecretTeamDataHolder().syn(msgDataSyn);
						break;
					case SECRETAREA_USER_INFO:
						getClient().getGroupSecretUserInfoSynDataHolder().syn(msgDataSyn);
						break;
					case USER_HEROS:
						getClient().getUserHerosDataHolder().syn(msgDataSyn);
						break;
					case FIX_NORM_EQUIP:
						getClient().getFixNormEquipDataItemHolder().syn(msgDataSyn);
						break;
					case FIX_EXP_EQUIP:
						getClient().getFixExpEquipDataItemHolder().syn(msgDataSyn);
						break;
					case MagicSecretData:
						getClient().getMagicSecretHolder().syn(msgDataSyn);
						break;
					case MagicChapterData:
						getClient().getMagicChapterInfoHolder().syn(msgDataSyn);
						break;
					case MajorData:
						getClient().getMajorDataholder().syn(msgDataSyn);
						break;
					case COPY_LEVEL_RECORD:
						getClient().getCopyHolder().syn(msgDataSyn);
						break;
					case USER_GAME_DATA:
						getClient().getUserGameDataHolder().syn(msgDataSyn);
						break;
					case ROLE_BASE_ITEM:
						getClient().getRoleBaseInfoHolder().syn(getClient(), msgDataSyn);
						break;
					case GFightOnlinePersonalData:
						getClient().getUserGFightOnlineHolder().syn(msgDataSyn);
						break;
					case GFightOnlineResourceData:
						getClient().getGFightOnlineResourceHolder().syn(msgDataSyn);
						break;
					case GFightOnlineGroupData:
						getClient().getGFightOnlineGroupHolder().syn(msgDataSyn);
						break;
					case TEAM_BATTLE_TEAM:
						getClient().getTBTeamItemHolder().syn(msgDataSyn);
						break;
					case USER_TEAM_BATTLE:
						getClient().getUserTeamBattleDataHolder().syn(msgDataSyn);
						break;
					// --------------------------帮派副本数据-------------------------------//
					case GROUP_COPY_LEVEL:
						getClient().getGroupCopyHolder().syn(msgDataSyn);
						break;
					case GROUP_COPY_REWARD:
						getClient().getGroupCopyHolder().syn(msgDataSyn);
						break;
					case GROUP_COPY_MAP:
						getClient().getGroupCopyHolder().syn(msgDataSyn);
						break;
					case GROUP_ITEM_DROP_APPLY:
						getClient().getGroupCopyHolder().syn(msgDataSyn);
						break;
					case USE_GROUP_COPY_DATA:
						getClient().getGroupCopyUserData().syn(msgDataSyn);
						break;
					case GCompSelfGuess:
						getClient().getUserQuizItemHolder().syn(msgDataSyn);
						break;
					case GCompSelfGuessDetail:
						getClient().getQuizEventItemHolder().syn(getClient(), msgDataSyn, true);
						break;
					case GCompCanGuessItem:
						getClient().getQuizEventItemHolder().syn(getClient(), msgDataSyn, false);
						break;
					case GC_PREPARE_POSITION:
						getClient().getSameSceneSynDataHolder().syn(msgDataSyn);
						break;
					case GCompBase:
						getClient().getGCompBaseInfoHolder().syn(msgDataSyn);
						break;
					case GCompTeamHolder:
						getClient().getGCompTeamHolder().syn(msgDataSyn);
						break;
					case GCompMatch:
						getClient().getGCompEventsDataHolder().syn(msgDataSyn);
						break;
					case GCompOnlineMember:
						getClient().getGCompOnlinememberHolder().syn(msgDataSyn);
						break;
					case GCompMatchEnemy:
						Client client = getClient();
						client.getgCompMatchBattleSynDataHolder().sendGCOmpMatchBattleReq(client);
						break;
					case WB_DATA:
						getClient().getWbDataHolder().syn(msgDataSyn);
						break;
					default:
						break;
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			RobotLog.fail("!!!!!!!!!!!!!!!!!!!!!------------------------------------------------", e);
			throw (new RuntimeException("ClientMsgHandler[dataSyn] parse error", e));
		}
	}

	public void receiveRsp(Response resp) {
		if (Command.MSG_CHAT == resp.getHeader().getCommand()) {
			try {
				MsgChatResponse rsp = MsgChatResponse.parseFrom(resp.getSerializedContent());
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
				throw (new RuntimeException("ClientMsgHandler[dataSyn] parse error", e));
			}
		}
	}

	private static AtomicInteger seqGenerator = new AtomicInteger();

	/**
	 * 发送消息
	 * 
	 * @param channel
	 * @param command
	 * @param userId
	 * @param token
	 * @param bytes
	 */
	public boolean sendMsg(final Command command, ByteString bytes, MsgReciver msgReciverP) {
		msgReciver = msgReciverP;

		boolean success = false;
		final Client client = getClient();
		RequestHeader.Builder header = RequestHeader.newBuilder();
		header.setCommand(command);
		if (client.getUserId() != null) {
			header.setUserId(client.getUserId());
		}

		if (client.getToken() != null) {
			header.setToken(client.getToken());
		}
		final int seqId = seqGenerator.incrementAndGet();
		header.setSeqID(seqId);
		header.setToken(getName());
		RequestBody.Builder body = RequestBody.newBuilder();
		if (bytes != null) {
			body.setSerializedContent(bytes);
		}
		Request.Builder request = Request.newBuilder();
		request.setHeader(header);
		request.setBody(body);
		final long sendTime = System.currentTimeMillis();
		try {
			final Channel channel = ChannelServer.getInstance().getChannel(client);
			if (channel == null) {
				RobotLog.testException("channel is null:" + client.getAccountId(), new NullPointerException());
				return false;
			}
			client.setCommandInfo(new CommandInfo(command, seqId));
			RobotLog.testInfo("发送消息 客户端Id：" + client.getAccountId() + ",command=" + command + ",seqId=" + seqId);

			ChannelFuture f = channel.writeAndFlush(request);
			f.addListener(new GenericFutureListener<ChannelFuture>() {
				public void operationComplete(ChannelFuture future) throws Exception {
					if (!future.isSuccess()) {
						RobotLog.testError("send msg fail:" + client.getAccountId() + ",command=" + command + ",seqId=" + seqId + ",active=" + channel.isActive() + ",write=" + channel.isWritable() + ",open=" + channel.isOpen());
					} else {
						long cost = System.currentTimeMillis() - sendTime;
						if (cost > 1000) {
							RobotLog.testError("send cost:" + client.getAccountId() + ",command=" + command + ",seqId=" + seqId + ",cost=" + cost);
						}
					}
				}
			});
			Thread.sleep(300);
			if (!f.channel().isActive()) {
				RobotLog.info("--------------channel is close");
				return true;
			}
			try {
				f.get(25, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				if (isOffLine.get()) {
					RobotLog.testError("ClientMsgHandler[sendMsg] 被顶号. accountId:" + client.getAccountId() + ",command=" + command + ",seqId=" + seqId);
					return true;
				} else {
					RobotLog.testException("ClientMsgHandler[sendMsg] 与服务器通信异常. accountId:" + client.getAccountId() + ",command=" + command + ",seqId=" + seqId, e);
					return false;
				}
			}
			if (!f.isSuccess()) {
				return true;
			}
			// 当前离线发送的消息表示成功
			if (msgReciverP != null && !isOffLine.get()) {
				success = handleResp(msgReciverP, client, seqId);
				msgReciver = null;
			} else {
				RobotLog.info("--------------can not find msgReciver or be kick off line");
				success = true;
			}
		} catch (Exception e) {
			long cost = System.currentTimeMillis() - sendTime;
			RobotLog.fail("ClientMsgHandler[sendMsg] 与服务器通信异常. accountId:" + client.getAccountId() + ",command=" + command + ",seqId=" + seqId + ",cost=" + cost, e);
			RobotLog.testException("ClientMsgHandler[sendMsg] 与服务器通信异常. accountId:" + client.getAccountId() + ",command=" + command + ",seqId=" + seqId, e);
			success = false;
		}
		return success;

	}

	private boolean handleResp(MsgReciver msgReciverP, Client client, int seqId) {
		boolean success = true;
		Response rsp = getResp(seqId, msgReciverP);
		if (rsp == null) {
			RobotLog.fail("ClientMsgHandler[handleResp]业务模块收到的响应超时, cmd:" + msgReciverP.getCmd() + "account :" + client.getAccountId());
			success = false;
		} else {
			ResponseHeader headerTmp = rsp.getHeader();
			if (headerTmp == null) {
				RobotLog.fail(String.format("ClientMsgHandler[%s]业务模块收到的响应没有头, account:%s", msgReciverP.getCmd(), client.getAccountId()));
				success = false;
			} else if(403 == headerTmp.getStatusCode()){
				RobotLog.fail(String.format("ClientMsgHandler[%s]业务模块没有到达开放等级, account:%s", msgReciverP.getCmd(), client.getAccountId()));
			} else{
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