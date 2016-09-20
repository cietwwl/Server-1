package com.rw.controler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.List;
import java.util.Map;

import com.bm.login.ZoneBM;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.ProtocolMessageEnum;
import com.log.FSTraceLogger;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.UserDataMgr;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.UserChannelMgr;
import com.rw.netty.UserSession;
import com.rw.service.FsService;
import com.rw.service.common.FunctionOpenLogic;
import com.rw.service.log.behavior.GameBehaviorMgr;
import com.rw.service.redpoint.RedPointManager;
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
import com.rwproto.ResponseProtos.Response;

public class GameLogicTask implements PlayerTask {

	private static FsNettyControler nettyControler = SpringContextUtil.getBean("fsNettyControler");
	private final Request request;
	private final UserSession session;
	private final long submitTime;

	public GameLogicTask(UserSession session, Request request) {
		this.request = request;
		this.session = session;
		this.submitTime = System.currentTimeMillis();
	}

	@Override
	public void run(Player player) {
		ByteString resultContent;
		String userId = null;
		RequestHeader header = request.getHeader();
		final int seqID = header.getSeqID();
		long sessionId = session.getSessionId();
		final Command command = header.getCommand();
		final long executeTime = System.currentTimeMillis();
		ProtocolMessageEnum msgType = null;
		ByteString synData = null;// 同步数据
		try {
			FSTraceLogger.logger("run", executeTime - submitTime, command, seqID, player != null ? player.getUserId() : null);
			// plyaer为null不敢做过滤
			if (player != null) {
				UserDataMgr userDataMgr = player.getUserDataMgr();
				userDataMgr.setEntranceId(request.getHeader().getEntranceId());
				userId = player.getUserId();
				ChannelHandlerContext ctx = UserChannelMgr.get(userId);
				if (ctx == null) {
					return;
				}
				if (sessionId != UserChannelMgr.getUserSessionId(ctx)) {
					return;
				}
				TableZoneInfo zone = ZoneBM.getInstance().getTableZoneInfo(player.getUserDataMgr().getZoneId());
				if (zone == null || (zone.getEnabled() != 1)) {
					nettyControler.sendResponse(userId, request.getHeader(), null, 600, ctx);
					return;
				}
				Response response = nettyControler.getResponse(userId, seqID);
				if (response != null) {
					System.err.println("send reconnect:" + UserChannelMgr.getCtxInfo(ctx));
					nettyControler.sendResponse(request.getHeader(), response.getSerializedContent(), UserChannelMgr.get(userId));
					return;
				}
				handleGuildance(header, userId);
			}
			try {
				// 收集逻辑产生的数据变化
				UserChannelMgr.onBSBegin(userId);
				FsService<GeneratedMessage, ProtocolMessageEnum> serivice = nettyControler.getSerivice(command);
				if (serivice == null) {
					proceeMsgRequestException(player, userId, "command获取不到对应的service. command:" + command, command, executeTime, seqID);
					return;
				}
				
				GeneratedMessage msg = serivice.parseMsg(request);
				if (msg == null) {
					proceeMsgRequestException(player, userId, "command对应的request解析消息出错。 command:" + command, command,
							executeTime, seqID);
					return;
				}

				msgType = serivice.getMsgType(msg);
				registerBehavior(player, serivice, command, msgType, msg, header.getViewId());

				if (FunctionOpenLogic.getInstance().isOpen(msgType, request, player)) {
					resultContent = serivice.doTask(msg, player);
					player.getAssistantMgr().doCheck();
					FSTraceLogger.logger("run end(" + (System.currentTimeMillis() - executeTime) + "," + command + ","
							+ seqID + ")[" + player.getUserId() + "]");
				} else {
					nettyControler.functionNotOpen(userId, request.getHeader());
					return;
				}

			} finally {
				// 把逻辑产生的数据变化先同步到客户端
				synData = UserChannelMgr.getDataOnBSEnd(userId);
				// UserChannelMgr.synDataOnBSEnd(userId);
			}
		} catch (Throwable t) {
			GameLog.error("GameLogicTask", "#run()", "run business service exception:", t);
			nettyControler.sendErrorResponse(userId, request.getHeader(), 500);
			FSTraceLogger.logger("run exception", System.currentTimeMillis() - executeTime, command, null, seqID, userId, null);
			return;
		}
		ChannelFuture future = nettyControler.sendResponse(userId, header, resultContent, sessionId, synData);
		if (future == null) {
			FSTraceLogger.logger("send fail", 0, command, null, seqID, player != null ? player.getUserId() : null);
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

	private void proceeMsgRequestException(Player player, String userId, String msg, Command command, long executeTime, int seqID) {
		GameLog.error("GameLogicTask", "run business service exception:", msg);
		nettyControler.sendErrorResponse(userId, request.getHeader(), 503);
		FSTraceLogger.logger("run exception", System.currentTimeMillis() - executeTime, command, null, seqID, userId, null);
	}

	@SuppressWarnings({ "rawtypes" })
	private void registerBehavior(Player player, FsService serivice, Command command, ProtocolMessageEnum msgType, GeneratedMessage msg, int viewId) {
		if (msgType != null) {
			String value = String.valueOf(msgType.getNumber());
			GameBehaviorMgr.getInstance().registerBehavior(player, command, msgType, value, viewId);
		}
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
