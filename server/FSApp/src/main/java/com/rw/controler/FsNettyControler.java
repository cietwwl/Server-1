package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bm.login.AccoutBM;
import com.bm.login.ZoneBM;
import com.common.GameUtil;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.netty.UserChannelMgr;
import com.rw.service.FsService;
import com.rw.service.login.game.GameLoginHandler;
import com.rw.service.numericAnalysis.NumericAnalysisService;
import com.rw.service.platformgs.PlatformGSService;
import com.rw.service.redpoint.RedPointManager;
import com.rwbase.common.threadLocal.UserThreadLocal;
import com.rwbase.dao.guide.GuideProgressDAO;
import com.rwbase.dao.guide.PlotProgressDAO;
import com.rwbase.dao.guide.pojo.UserGuideProgress;
import com.rwbase.dao.guide.pojo.UserPlotProgress;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.dao.zone.TableZoneInfo;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwbase.gameworld.TaskExceptionHandler;
import com.rwproto.ClientViewProtos.ClientViewData;
import com.rwproto.GameLoginProtos.GameLoginRequest;
import com.rwproto.GuidanceProgressProtos.GuidanceProgress;
import com.rwproto.MsgDef;
import com.rwproto.MsgDef.Command;
import com.rwproto.MsgRsProtos.EMsgType;
import com.rwproto.MsgRsProtos.MsgMsgRsResponse;
import com.rwproto.PlotViewProtos.PlotProgress;
import com.rwproto.ReConnectionProtos.ReConnectRequest;
import com.rwproto.ReConnectionProtos.ReConnectResponse;
import com.rwproto.ReConnectionProtos.ReConnectResultType;
import com.rwproto.ReConnectionProtos.SyncVersion;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RequestProtos.RequestHeader;
import com.rwproto.ResponseProtos;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.ResponseProtos.ResponseHeader;

public class FsNettyControler {

	private Map<Command, FsService> commandMap;

	private GameLoginHandler gameLoginHandler = GameLoginHandler.getInstance();

	public void doMyService(final Request exRequest, final ChannelHandlerContext ctx) {
		RequestHeader header = exRequest.getHeader();
		final int redpointVersion = header.getRedpointVersion();
		final Command command = header.getCommand();
		final int seqID = header.getSeqID();
		GameLog.debug("@@接收消息" + "  " + exRequest.getHeader().getCommand().toString());

		Response result = null;
		if (command == Command.MSG_HeartBeat) {
			result = doHeartBeat(exRequest);
			sendResponse(seqID, result, ctx);
		} else if (command == Command.MSG_LOGIN_GAME) {
			result = doGameLogin(exRequest);
			sendResponse(seqID, result, ctx);
		} else if (command == Command.MSG_RECONNECT) {
			result = ReConnect(exRequest);
			sendResponse(seqID, result, ctx);
		} else if (command == Command.MSG_NUMERIC_ANALYSIS) {
			result = NumericAnalysisService.doTask(exRequest);
			sendResponse(seqID, result, ctx);
		} else if(command == Command.MSG_PLATFORMGS){
			result = doPlatformGSMsg(exRequest);
			sendResponse(seqID, result, ctx);
		}else {
			TaskExceptionHandler errorHandler = new TaskExceptionHandler() {

				@Override
				public void handle(Throwable t) {
					Response errorResult = getErrorResponse(command);
					sendResponse(seqID, errorResult, ctx);
				}
			};
			String userId = UserChannelMgr.getUserId();
			if(userId == null){
				return;
			}
			handleGuildance(header);
			GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerTask() {

				@Override
				public void run(Player player) {
					if (player == null) {
						return;
					}
					if (exRequest.getNum() > 0) {
						MsgMsgRsResponse.Builder res = MsgMsgRsResponse.newBuilder();
						res.setType(EMsgType.ClientMsg);
						res.setId(exRequest.getNum());
						player.SendMsg(MsgDef.Command.MSG_Rs_DATA, res.build().toByteString());
					}
					Response result = doBusinessService(player, exRequest, command);
					if (result == null)
						return;
					sendResponse(seqID, result, ctx);
					if (exRequest.getNum() > 0 && redpointVersion >= 0) {
						RedPointManager.getRedPointManager().checkRedPointVersion(player, redpointVersion);
					}

				}
			}, errorHandler);
		}
	}

	private void handleGuildance(RequestHeader header) {
		try {
			ClientViewData clientView = header.getClientGenerated();
			if (clientView == null) {
				return;
			}
			String userId = UserChannelMgr.getUserId();
			if (userId == null) {
				GameLog.error("处理新手引导找不到UserId:" + userId);
				return;
			}
			List<GuidanceProgress> guidanceList = clientView.getGuideProgList();
			List<PlotProgress> plotList = clientView.getPlotProgList();
			int guideSize = guidanceList.size();
			int plotSize = plotList.size();
			if (guideSize <= 0 && plotSize <= 0) {
				// GameLog.error("新手引导的List为空 UserId:" + userId);
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
			GameLog.error(t);
		}
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
		ctx.channel().writeAndFlush(response.build());
		GameLog.debug("##发送消息" + "  " + response.getHeader().getCommand().toString() + "  Size:" + response.getSerializedContent().size());
	}

	private Response doHeartBeat(Request exRequest) {
		heartBeatCheck();
		Response result;
		Response.Builder builder = Response.newBuilder().setHeader(getSimpleResponseHeader(exRequest, Command.MSG_HeartBeat));
		result = builder.build();
		return result;
	}

	/**
	 * heartBeat 的时候检查用户需要按时更新的功能
	 */
	private void heartBeatCheck() {
		String userId = UserChannelMgr.getUserId();
		if (StringUtils.isBlank(userId)) {
			return;
		}
		// modify@2015-8-29 by Jamaz 通过异步角色任务的方式处理逻辑消息
		GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerTask() {

			@Override
			public void run(Player player) {

				player.heartBeatCheck();

			}
		});
	}
	
	private Response doPlatformGSMsg(Request exRequest){
		Response result;
		ByteString resultContent = PlatformGSService.doTask(exRequest);

		Response.Builder builder = Response.newBuilder().setHeader(getSimpleResponseHeader(exRequest, Command.MSG_PLATFORMGS));
		if (resultContent != null) {
			builder.setSerializedContent(resultContent);
		}
		result = builder.build();
		return result;
	}

	private Response ReConnect(Request exRequest) {
		ReConnectResponse.Builder b = ReConnectResponse.newBuilder();
		try {
			ReConnectRequest reconnectRequest = ReConnectRequest.parseFrom(exRequest.getBody().getSerializedContent());
			String accountId = reconnectRequest.getAccountId();
			int zoneId = reconnectRequest.getZoneId();
			TableAccount userAccount = AccoutBM.getInstance().getByAccountId(accountId);
			if (userAccount == null) {
				GameLog.error("ReConnect找不到account：" + accountId);
				return returnReconnectRequest(b, exRequest, ReConnectResultType.RETURN_GAME_LOGIN);
			} else {
				User user = UserDataDao.getInstance().getByAccoutAndZoneId(accountId, zoneId);
				if (user == null) {
					GameLog.error("ReConnect找不到user：" + accountId + "," + zoneId);
					return returnReconnectRequest(b, exRequest, ReConnectResultType.RETURN_GAME_LOGIN);
				}
				if( user.isBlocked()){
					GameLog.error("ReConnect 用户封号中 ，userId:" + user.getUserId() + "," + zoneId);
					return returnReconnectRequest(b, exRequest, ReConnectResultType.RETURN_GAME_LOGIN);
				}
				if( user.isInKickOffCoolTime()){
					GameLog.error("ReConnect 用户踢出冷却中 ，userId:" + user.getUserId() + "," + zoneId);
					return returnReconnectRequest(b, exRequest, ReConnectResultType.RETURN_GAME_LOGIN);
				}
				
				String userId = user.getUserId();
				Player player = PlayerMgr.getInstance().findPlayerFromMemory(userId);
				if (player == null) {
					return returnReconnectRequest(b, exRequest, ReConnectResultType.RETURN_GAME_LOGIN);
				}
				//如果不符合5分钟以内
				if(!UserChannelMgr.clearAndCheckReconnect(user.getUserId())){
					return returnReconnectRequest(b, exRequest, ReConnectResultType.RETURN_GAME_LOGIN);
				}
				UserChannelMgr.bindUserID(userId);
				List<SyncVersion> versionList = reconnectRequest.getVersionListList();
				if (versionList != null) {
					player.synByVersion(versionList);
				}else{
					GameLog.error("客户端版本同步列表为null");
				}
				return returnReconnectRequest(b, exRequest, ReConnectResultType.RECONNECT_SUCCESS);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return returnReconnectRequest(b, exRequest, ReConnectResultType.RETURN_GAME_LOGIN);
		}
	}

	private Response returnReconnectRequest(ReConnectResponse.Builder b, Request exRequest, ReConnectResultType resultType) {
		b.setResultType(resultType);
		Response.Builder builder = Response.newBuilder().setHeader(getSimpleResponseHeader(exRequest, Command.MSG_RECONNECT));
		builder.setSerializedContent(b.build().toByteString());
		return builder.build();
	}

	private Response doGameLogin(Request exRequest) {
		Response result;
		ByteString resultContent = null;
		try {
			GameLoginRequest loginRequest = GameLoginRequest.parseFrom(exRequest.getBody().getSerializedContent());
			switch (loginRequest.getLoginType()) {
			case GAME_LOGIN:
				resultContent = gameLoginHandler.gameServerLogin(loginRequest);
				if (resultContent == null) {
					return null;
				}
				break;
			case CREATE_ROLE:
				resultContent = gameLoginHandler.createRoleAndLogin(loginRequest);
				break;
			case LOAD_MAINCITY:
				return null;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

		Response.Builder builder = Response.newBuilder().setHeader(getSimpleResponseHeader(exRequest, Command.MSG_LOGIN_GAME));
		if (resultContent != null) {
			builder.setSerializedContent(resultContent);
		}
		result = builder.build();
		return result;
	}

	private Response doBusinessService(Player pPlayer, Request exRequest, Command command) {

		Response result;

		boolean isZoneOpen = validateZone(pPlayer);

		if (!isZoneOpen) {
			result = getZoneClosedResponse(command);
		} else {
			ByteString resultContent = null;
			UserThreadLocal.set(pPlayer);
			try {
				pPlayer.onBSStart();
				resultContent = getSerivice(command).doTask(exRequest, pPlayer);
				if (command != Command.MSG_Rs_DATA) {
					pPlayer.getAssistantMgr().doCheck();
				}

				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++" + command);
			} finally {
				pPlayer.onBSEnd();
			}
			Response.Builder builder = Response.newBuilder().setHeader(getResponseHeader(exRequest, command, pPlayer));
			if (resultContent != null) {
				builder.setSerializedContent(resultContent);
			}
			result = builder.build();
			// int size = result.getSerializedSize();
			// System.out.println("serializedContent size:" + size);

		}

		return result;
	}

	public ResponseHeader getResponseHeader(Request req, Command command, Player user) {
		String token = req.getHeader().getToken();
		ResponseHeader.Builder responseHeaderBuilder = ResponseHeader.newBuilder().setToken(token).setCommand(command).setStatusCode(200);

		return responseHeaderBuilder.build();
	}

	private boolean validateZone(Player user) {
		boolean isOpen = false;
		// ZoneCfg zone = ZoneBLL.getInstance().getMainZone(user.GetZoneId());
		TableZoneInfo zone = ZoneBM.getInstance().getTableZoneInfo(user.getUserDataMgr().getZoneId());
		if (zone != null && (zone.getEnabled() == 1)) {
			isOpen = true;
		}
		return isOpen;
	}

	private ResponseHeader getSimpleResponseHeader(Request req, Command command) {
		String token = req.getHeader().getToken();
		return ResponseHeader.newBuilder().setToken(token).setCommand(command).setStatusCode(200).build();
	}

	private Response getUnAuthrizedResponse(Command command) {
		int unAuthorized = 404;
		return Response.newBuilder().setHeader(ResponseHeader.newBuilder().setStatusCode(unAuthorized).setCommand(command).setToken("").build()).build();
	}

	private Response getZoneClosedResponse(Command command) {
		int zoneClosed = 600;
		return Response.newBuilder().setHeader(ResponseHeader.newBuilder().setStatusCode(zoneClosed).setCommand(command).setToken("").build()).build();
	}

	private Response getErrorResponse(Command command) {
		int systemError = 500;
		return Response.newBuilder().setHeader(ResponseHeader.newBuilder().setStatusCode(systemError).setCommand(command).setToken("").build()).build();
	}

	private FsService getSerivice(Command command) {
		return commandMap.get(command);
	}

	public void setCommandMap(Map<Command, FsService> commandMap) {
		this.commandMap = commandMap;
	}

}
