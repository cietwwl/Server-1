package com.rw.service.sign;

import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.HotPointMgr;
import com.playerdata.Player;
import com.rwbase.dao.hotPoint.EHotPointType;
import com.rwproto.SignServiceProtos.ERequestType;
import com.rwproto.SignServiceProtos.EResultType;
import com.rwproto.SignServiceProtos.MsgSignRequest;
import com.rwproto.SignServiceProtos.MsgSignResponse;

public class SignHandler 
{
	private static SignHandler instance = new SignHandler();
	private SignHandler() {}
	public static SignHandler getInstance() 
	{
		return instance;
	}
	
	/*
	 * 返回签到次数
	 */
	public ByteString returnSignData(Player player) 
	{
		MsgSignResponse.Builder response = MsgSignResponse.newBuilder().setRequestType(ERequestType.SIGNDATA_BACK);
		if(player.getSignMgr().checkRefreshTime())
		{
			player.getSignMgr().refreshData();
		}
		else 
		{
			player.getSignMgr().disablePrevious();
		}
		List<String> signDataList = player.getSignMgr().getAllSignRecord();
		response.setMonth(player.getSignMgr().getCurrentMonth());
		response.setYear(player.getSignMgr().getCurrentYear());
		response.setReSignCount(player.getSignMgr().getResignCount());
		response.addAllTagSignData(signDataList);
		response.setResultype(EResultType.NEED_REFRESH);
		return response.build().toByteString();
	}
	
	/*
	 * 所有签到
	 */
	public ByteString sign(Player player, MsgSignRequest request) 
	{
		MsgSignResponse.Builder response = MsgSignResponse.newBuilder().setRequestType(ERequestType.SIGNDATA_BACK);
		
		if(player.getSignMgr().checkRefreshTime())	//如果月份超过了当月需要刷新数据的话
		{
			player.getSignMgr().refreshData();
			List<String> signDataList = player.getSignMgr().getAllSignRecord();
			response.setMonth(player.getSignMgr().getCurrentMonth());
			response.setYear(player.getSignMgr().getCurrentYear());
			response.setReSignCount(player.getSignMgr().getResignCount());
			response.addAllTagSignData(signDataList);
			response.setResultype(EResultType.NEED_REFRESH);
			return response.build().toByteString();
		}
		else 
		{
			boolean isNeedFresh = player.getSignMgr().disablePrevious();
			boolean isSignedToday = player.getSignMgr().getLastData().getLastSignDate() != null;
			if(isNeedFresh)
			{
				if(isSignedToday)
				{
					List<String> signDataList = player.getSignMgr().getAllSignRecord();
					response.setMonth(player.getSignMgr().getCurrentMonth());
					response.setYear(player.getSignMgr().getCurrentYear());
					response.setReSignCount(player.getSignMgr().getResignCount());
					response.addAllTagSignData(signDataList);
					response.setResultype(EResultType.NEED_REFRESH);
					return response.build().toByteString();
				}
			}
		}
		String signId = request.getSignId();
		if(player.getSignMgr().Checklegal(signId))
		{
			player.getSignMgr().changeSignData(signId, player, response);
		}
		else
		{
			response.setResultype(EResultType.NULL);
		}
		HotPointMgr.changeHotPointState(player.getUserId(), EHotPointType.Sign, false);
		response.setMonth(player.getSignMgr().getCurrentMonth());	//以上一次更新的月份为准...
		response.setYear(player.getSignMgr().getCurrentYear());
		List<String> signDataList = player.getSignMgr().getAllSignRecord();
		response.addAllTagSignData(signDataList);
		response.setReSignCount(player.getSignMgr().getResignCount());
		return response.build().toByteString();
	}
}
