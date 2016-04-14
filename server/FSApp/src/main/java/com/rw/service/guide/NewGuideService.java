package com.rw.service.guide;

import java.util.ArrayList;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rw.service.gm.GMHandler;
import com.rw.service.guide.datamodel.GiveItemCfg;
import com.rw.service.guide.datamodel.GiveItemCfgDAO;
import com.rw.service.guide.datamodel.GiveItemHistory;
import com.rw.service.guide.datamodel.GiveItemHistoryHolder;
import com.rwbase.dao.guide.GuideProgressDAO;
import com.rwbase.dao.guide.pojo.UserGuideProgress;
import com.rwproto.GuidanceProgressProtos.GuidanceProgress;
import com.rwproto.GuidanceProgressProtos.GuidanceRequest;
import com.rwproto.GuidanceProgressProtos.GuidanceResponse;
import com.rwproto.RequestProtos.Request;

public class NewGuideService implements FsService {

	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(Request request, Player player) {
		GuidanceResponse.Builder builder = GuidanceResponse.newBuilder();
		try {
			GuidanceRequest req = GuidanceRequest.parseFrom(request.getBody().getSerializedContent());
			switch (req.getRequestType()) {
			case LoadProgress:
				UserGuideProgress guide = GuideProgressDAO.getInstance().get(player.getUserId());
				ArrayList<GuidanceProgress> list = new ArrayList<GuidanceProgress>();
				if (guide == null) {
					builder.addAllSavedProgress(list);
					return builder.build().toByteString();
				}
				for (Map.Entry<Integer, Integer> entry : guide.getProgressMap().entrySet()) {
					GuidanceProgress.Builder b = GuidanceProgress.newBuilder();
					b.setGuideID(entry.getKey());
					b.setProgress(entry.getValue());
					list.add(b.build());
				}
				builder.addAllSavedProgress(list);
				return builder.build().toByteString();
			case GiveItem:
				int actId = req.getGiveActionId();
				GiveItemCfg cfg = GiveItemCfgDAO.getInstance().getCfgById(String.valueOf(actId));
				String tip="success";
				boolean result=true;
				if (cfg == null){
					tip="找不到配置";
					result=false;
					return setResponse(builder, tip, result);
				}
				
				GiveItemHistory history = GiveItemHistoryHolder.getInstance().getHistory(player.getUserId(),actId);
				if (history != null && history.isGiven()){
					tip="只能赠送一次";
					result=false;
					return setResponse(builder, tip, result);
				}
				
				if (history == null){
					history = GiveItemHistory.Add(null, player.getUserId(), actId);
					if (history == null){
						tip="无法新增赠品";
						result=false;
						return setResponse(builder, tip, result);
					}
				}
				
				String[] arg = new String[2];
				arg[0] = cfg.getModleId();
				arg[1] = String.valueOf(cfg.getCount());
				if (!GMHandler.getInstance().addItem(arg , player)){
					tip="赠送失败";
					result=false;
				}
				if (!history.setGiven(null, true)){
					GameLog.error("引导", player.getUserId(), "更新赠送历史失败！");
				}
				GameLog.info("引导", player.getUserId(), "成功赠送物品,actId:"+actId, null);
				return setResponse(builder, tip, result);
			default:
				break;
			}

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		} finally {
			return null;
		}

	}

	private ByteString setResponse(GuidanceResponse.Builder builder, String tip, boolean result) {
		builder.setTip(tip);
		builder.setIsSuccess(result);
		return builder.build().toByteString();
	}

}
