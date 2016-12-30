package com.rw.controler;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.bm.login.ZoneBM;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.ProtocolMessageEnum;
import com.log.FSTraceLogger;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.UserDataMgr;
import com.rw.fsutil.common.PairKey;
import com.rw.fsutil.dao.cache.trace.DataEventRecorder;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.MsgResultType;
import com.rw.netty.ServerHandler;
import com.rw.netty.UserChannelMgr;
import com.rw.service.FsService;
import com.rw.service.common.FunctionOpenLogic;
import com.rw.service.log.behavior.GameBehaviorMgr;
import com.rw.service.redpoint.RedPointManager;
import com.rw.service.yaowanlog.YaoWanLogHandler;
import com.rwbase.dao.guide.GuideProgressDAO;
import com.rwbase.dao.guide.PlotProgressDAO;
import com.rwbase.dao.guide.pojo.UserGuideProgress;
import com.rwbase.dao.guide.pojo.UserPlotProgress;
import com.rwbase.dao.zone.TableZoneInfo;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.ClientViewProtos.ClientViewData;
import com.rwproto.GuidanceProgressProtos.GuidanceProgress;
import com.rwproto.MsgDef.Command;
import com.rwproto.PlotViewProtos.PlotProgress;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RequestProtos.RequestHeader;

public class GameLogicTask implements PlayerTask {

	private static FsNettyControler nettyControler = SpringContextUtil.getBean("fsNettyControler");
	private final Request request;
	private final Long sessionId;
	private final long submitTime;
	private final Command command;

	public GameLogicTask(Long sessionId, Request request, Command command) {
		this.request = request;
		this.sessionId = sessionId;
		this.submitTime = System.currentTimeMillis();
		this.command = command;
	}

	@Override
	public void run(Player player) {
		ByteString resultContent;
		String userId = null;
		RequestHeader header = request.getHeader();
		final int seqID = header.getSeqID();
		final long executeTime = System.currentTimeMillis();
		ProtocolMessageEnum msgType = null;
		ByteString synData = null;// 同步数据
		try {
			if (!ServerHandler.isConnecting(sessionId)) {
				FSTraceLogger.logger("disconnect_", executeTime - submitTime, command, null, seqID, player != null ? player.getUserId() : null);
				return;
			}
			FSTraceLogger.logger("run", executeTime - submitTime, command, null, seqID, player != null ? player.getUserId() : null);
			// plyaer为null不敢做过滤
			if (player != null) {
				// 设置一下客户端的IP
				userId = player.getUserId();
				String ip = player.getTempAttribute().getIp();
				if (StringUtils.isEmpty(ip)) {
					player.getTempAttribute().setIp(YaoWanLogHandler.getHandler().getClientIp(userId));
				}

				UserDataMgr userDataMgr = player.getUserDataMgr();
				userDataMgr.setEntranceId(request.getHeader().getEntranceId());
				int zoneId = player.getUserDataMgr().getZoneId();
				TableZoneInfo zone = ZoneBM.getInstance().getTableZoneInfo(zoneId);
				if (zone == null) {
					UserChannelMgr.sendSyncResponse(userId, request.getHeader(), MsgResultType.ZONE_NOT_OPEN.getPreDesc(zoneId), null, 600, sessionId, null);
					return;
				}
				ByteString response = UserChannelMgr.getResponse(userId, seqID);
				if (response != null) {
					System.err.println("send reconnect:" + ServerHandler.getCtxInfo(sessionId));
					UserChannelMgr.sendSyncResponse(request.getHeader(), MsgResultType.RECONNECT, response, sessionId);
					return;
				}
				handleGuildance(header, userId);
			}
			try {
				// 收集逻辑产生的数据变化
				UserChannelMgr.onBSBegin(userId);
				FsService<GeneratedMessage, ProtocolMessageEnum> serivice = nettyControler.getSerivice(command);
				if (serivice == null) {
					proceeMsgRequestException(player, userId, "command获取不到对应的service,cmd=" + command, command, "no service", executeTime, seqID);
					return;
				}

				GeneratedMessage msg = serivice.parseMsg(request);
				if (msg == null) {
					proceeMsgRequestException(player, userId, "command对应的request解析消息出错,,cmd=" + command, command, "no type", executeTime, seqID);
					return;
				}

				msgType = serivice.getMsgType(msg);
				registerBehavior(userId, serivice, command, msgType, msg, header.getViewId());

				if (FunctionOpenLogic.getInstance().isOpen(msgType, request, player)) {
					resultContent = serivice.doTask(msg, player);
					player.getAssistantMgr().doCheck();
					FSTraceLogger.logger("run end(" + (System.currentTimeMillis() - executeTime) + "," + command + "," + seqID + ")[" + player.getUserId() + "]");
				} else {
					nettyControler.functionNotOpen(userId, request.getHeader());
					return;
				}

			} finally {
				// 把逻辑产生的数据变化先同步到客户端
				synData = UserChannelMgr.getDataOnBSEnd(userId, new PairKey<Command, ProtocolMessageEnum>(command, msgType));
				DataEventRecorder.endAndPollCollections();
				// UserChannelMgr.synDataOnBSEnd(userId);
			}
		} catch (Throwable t) {
			GameLog.error("GameLogicTask", "#run()", "run business service exception:", t);
			UserChannelMgr.sendErrorResponse(userId, request.getHeader(), MsgResultType.EXCEPTION.getPreDesc(msgType), 500);
			FSTraceLogger.logger("run exception", System.currentTimeMillis() - executeTime, command, null, seqID, userId, null);
			return;
		}
		Object subCmd;
		if (msgType == null) {
			subCmd = MsgResultType.NOT_PARSE;
		} else {
			subCmd = msgType;
		}
		ChannelFuture future = UserChannelMgr.sendSyncResponse(userId, header, subCmd, resultContent, sessionId, synData);
		if (future == null) {
			FSTraceLogger.logger("send fail", 0, command, msgType, seqID, player != null ? player.getUserId() : null);
		} else {

			final String userId_ = userId;
			final ProtocolMessageEnum type_ = msgType;
			future.addListener(new GenericFutureListener<Future<? super Void>>() {

				@Override
				public void operationComplete(Future<? super Void> future) throws Exception {
					long current = System.currentTimeMillis();
					FSTraceLogger.loggerSendAndSubmit("send", current - submitTime, current - executeTime, command, type_, seqID, userId_, null);
				}
			});
		}
		int redPointVersion = header.getRedpointVersion();
		if (redPointVersion >= 0) {
			RedPointManager.getRedPointManager().checkRedPointVersion(player, redPointVersion);
		}
	}

	private void proceeMsgRequestException(Player player, String userId, String msg, Command command, Object reason, long executeTime, int seqID) {
		GameLog.error("GameLogicTask", "run business service exception:", msg);
		UserChannelMgr.sendErrorResponse(userId, request.getHeader(), reason, 503);
		FSTraceLogger.logger("run exception", System.currentTimeMillis() - executeTime, command, null, seqID, userId, null);
	}

	private void registerBehavior(String userId, FsService serivice, Command command, ProtocolMessageEnum msgType, GeneratedMessage msg, int viewId) {
		// if (msgType != null) {
		// String value = String.valueOf(msgType.getNumber());
		// GameBehaviorMgr.getInstance().registerBehavior(player, command,
		// msgType, value, viewId);
		// } else {
		// GameBehaviorMgr.getInstance().registerBehavior(player, command,
		// msgType, "-1", viewId);
		// }
		GameBehaviorMgr.getInstance().registerBehavior(userId, command, msgType, viewId);
	}

	private void handleGuildance(RequestHeader header, String userId) {
		try {
			ClientViewData clientView = header.getClientGenerated();
			if (clientView == null) {
				return;
			}
			if (userId == null) {
				GameLog.error("GameLogicTask", "#handleGuildance()", "处理新手引导找不到UserId:" + userId);
				return;
			}
			List<GuidanceProgress> guidanceList = clientView.getGuideProgList();
			List<PlotProgress> plotList = clientView.getPlotProgList();
			int guideSize = guidanceList.size();
			int plotSize = plotList.size();
			if (guideSize <= 0 && plotSize <= 0) {
				return;
			}
			if (guideSize > 0) {
				GuideProgressDAO guideDAO = GuideProgressDAO.getInstance();
				UserGuideProgress userGuideProgress = guideDAO.get(userId);
				if (userGuideProgress == null) {
					userGuideProgress = new UserGuideProgress();
					userGuideProgress.setUserId(userId);
				}

				Map<Integer, Integer> map = userGuideProgress.getProgressMap();
				for (int i = guideSize; --i >= 0;) {
					GuidanceProgress p = guidanceList.get(i);
					map.put(p.getGuideID(), p.getProgress());
				}
				guideDAO.update(userGuideProgress);
			}
			if (plotSize > 0) {
				PlotProgressDAO plotProgressDAO = PlotProgressDAO.getInstance();
				UserPlotProgress userPlotProgress = plotProgressDAO.get(userId);
				if (userPlotProgress == null) {
					userPlotProgress = new UserPlotProgress();
					userPlotProgress.setUserId(userId);
				}
				Map<String, Integer> map = userPlotProgress.getProgressMap();
				for (int i = plotSize; --i >= 0;) {
					PlotProgress p = plotList.get(i);
					map.put(p.getPlotID(), p.getProgress());
				}
				plotProgressDAO.update(userPlotProgress);
			}
		} catch (Throwable t) {
			GameLog.error("GameLogicTask", "#handleGuildance()", "处理新手引导找不到UserId:" + userId, t);
		}
	}

}
