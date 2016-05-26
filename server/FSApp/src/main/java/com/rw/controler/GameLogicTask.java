package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bm.login.ZoneBM;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.UserDataMgr;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.SessionInfo;
import com.rw.netty.UserChannelMgr;
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
	private final SessionInfo session;

	public GameLogicTask(SessionInfo session, Request request) {
		this.request = request;
		this.session = session;
	}

	@Override
	public void run(Player player) {
		ByteString resultContent;
		String userId = null;
		RequestHeader header = request.getHeader();
		int seqID = header.getSeqID();
		long sessionId = session.getSessionId();
		try {
			Command command = header.getCommand();
			System.err.println("开始处理：" + new Date() + "," + command + "," + seqID);
			// plyaer为null不敢做过滤
			if (player != null) {
				UserDataMgr userDataMgr = player.getUserDataMgr();
				userDataMgr.setEntranceId(request.getHeader().getEntranceId());
				userId = player.getUserId();
				ChannelHandlerContext ctx = UserChannelMgr.get(userId);
				if (ctx == null) {
					return;
				}
				if (sessionId != UserChannelMgr.getSessionId(ctx)) {
					return;
				}
				TableZoneInfo zone = ZoneBM.getInstance().getTableZoneInfo(player.getUserDataMgr().getZoneId());
				if (zone == null || (zone.getEnabled() != 1)) {
					nettyControler.sendResponse(userId, request.getHeader(), null, 600, ctx);
					return;
				}
				Response response = nettyControler.getResponse(userId, seqID);
				if (response != null) {
					System.err.println("发送重连信息：" + header.getCommand() + ",seqId=" + seqID + sessionId + "," + UserChannelMgr.getCurrentSessionId(userId));
					nettyControler.sendResponse(request.getHeader(), response.getSerializedContent(), UserChannelMgr.get(userId));
					return;
				}
				handleGuildance(header, userId);
			}
//			try {
//				Thread.sleep(10100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			try {
				player.onBSStart();
				resultContent = nettyControler.getSerivice(command).doTask(request, player);
				player.getAssistantMgr().doCheck();
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++" + command);
			} finally {
				player.onBSEnd();
			}
		} catch (Throwable t) {
			GameLog.error("GameLogicTask", "#run()", "run business service exception:", t);
			nettyControler.sendErrorResponse(userId, request.getHeader(), 500);
			return;
		}
		if (resultContent != null) {
			nettyControler.sendResponse(userId, header, resultContent, sessionId);
		}
		int redPointVersion = header.getRedpointVersion();
		if (redPointVersion >= 0) {
			RedPointManager.getRedPointManager().checkRedPointVersion(player, redPointVersion);
		}
		System.err.println("结束处理：" + new Date());
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
