package com.rw.service.guide;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.common.RefParam;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.FashionMgr;
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
				RefParam<String> outTip=new RefParam<String>();
				boolean result = giveItem(cfg, player, outTip);
				return setResponse(builder, outTip.value, result);
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
	
	public static boolean giveItem(GiveItemCfg cfg,Player player,RefParam<String> outTip){
		outTip.value="success";
		boolean result=true;
		if (cfg == null){
			outTip.value="找不到配置";
			result=false;
			return result;
		}
		int actId = cfg.getKey();
		
		GiveItemHistory history = GiveItemHistoryHolder.getInstance().getHistory(player.getUserId(),actId);
		if (history != null && history.isGiven()){
			outTip.value="只能赠送一次";
			result=false;
			return result;
		}
		
		if (history == null){
			history = GiveItemHistory.Add(null, player.getUserId(), actId);
			if (history == null){
				outTip.value="无法新增赠品";
				result=false;
				return result;
			}
		}
		
		String[] arg = new String[2];
		arg[0] = cfg.getModleId();
		arg[1] = String.valueOf(cfg.getCount());
		
		//首先判断是否为时装
		boolean isFashionId = false;
		try {
			int fashionModelId = Integer.parseInt(cfg.getModleId());
			FashionMgr fashionMgr = player.getFashionMgr();
			if (fashionMgr.GMSetFashion(fashionModelId)){
				if (fashionMgr.GMSetExpiredTime(fashionModelId, TimeUnit.DAYS.toMinutes(cfg.getCount()))){
					isFashionId = true;
				}
			}
		} catch (Exception e) {
			GameLog.info("引导", player.getUserId(), "不是赠送时装",null);
		}
		
		if (!isFashionId){
			//不是时装，考虑作为道具赠送
			if (!GMHandler.getInstance().addItem(arg , player)){
				outTip.value="赠送失败";
				result=false;
			}
		}
		
		if (!history.setGiven(null, true)){
			GameLog.error("引导", player.getUserId(), "更新赠送历史失败！");
		}
		GameLog.info("引导", player.getUserId(), "成功赠送物品,actId:"+actId, null);
		return result;
	}

}
