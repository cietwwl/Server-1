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
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.guide.GuideProgressDAO;
import com.rwbase.dao.guide.pojo.UserGuideProgress;
import com.rwproto.GuidanceProgressProtos.GuidanceProgress;
import com.rwproto.GuidanceProgressProtos.GuidanceRequest;
import com.rwproto.GuidanceProgressProtos.GuidanceRequest.GuidanceRequestType;
import com.rwproto.GuidanceProgressProtos.GuidanceResponse;
import com.rwproto.RequestProtos.Request;

public class NewGuideService implements FsService<GuidanceRequest, GuidanceRequestType> {

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
		boolean isFashionId = true;
		try {
			int fashionModelId = Integer.parseInt(cfg.getModleId());
			FashionMgr fashionMgr = player.getFashionMgr();
			// 检查之前是否已经有时装，因为有可能升到VIP之后就会有时装，所以这里要避免覆盖了之前的时装时间 by PERRY @ 2016-11-03 >>>>>>>>>>
			FashionItem fashionItem = fashionMgr.getItem(fashionModelId);
			long expireMinutes;
			if (fashionItem == null) {
				isFashionId = fashionMgr.GMSetFashion(fashionModelId);
				expireMinutes = TimeUnit.DAYS.toMinutes(cfg.getCount());
			} else {
				expireMinutes = TimeUnit.DAYS.toMinutes(cfg.getCount()) + TimeUnit.MILLISECONDS.toMinutes(fashionItem.getExpiredTime() - System.currentTimeMillis());
			}
			if (isFashionId) {
				fashionMgr.GMSetExpiredTime(fashionModelId, expireMinutes);
			}
			// END <<<<<<<<<<
//			if (fashionMgr.GMSetFashion(fashionModelId)){
//				if (fashionMgr.GMSetExpiredTime(fashionModelId, TimeUnit.DAYS.toMinutes(cfg.getCount()))){
//					isFashionId = true;
//				}
//			}
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

	@Override
	public ByteString doTask(GuidanceRequest request, Player player) {
		// TODO Auto-generated method stub
		GuidanceResponse.Builder builder = GuidanceResponse.newBuilder();
		try {
			switch (request.getRequestType()) {
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
				int actId = request.getGiveActionId();
				GiveItemCfg cfg = GiveItemCfgDAO.getInstance().getCfgById(String.valueOf(actId));
				RefParam<String> outTip=new RefParam<String>();
				boolean result = giveItem(cfg, player, outTip);
				return setResponse(builder, outTip.value, result);
			default:
				return builder.build().toByteString();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return builder.build().toByteString();
		} 
	}

	@Override
	public GuidanceRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		GuidanceRequest req = GuidanceRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public GuidanceRequestType getMsgType(GuidanceRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}

}
