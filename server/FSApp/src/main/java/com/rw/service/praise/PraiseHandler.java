package com.rw.service.praise;

import org.springframework.util.StringUtils;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwbase.dao.praise.PraiseHelper;
import com.rwbase.dao.praise.PraiseMgr;
import com.rwproto.PraiseServiceProto.GetPraiseReqMsg;
import com.rwproto.PraiseServiceProto.GetPraiseRspMsg;
import com.rwproto.PraiseServiceProto.PraiseCommonRspMsg;
import com.rwproto.PraiseServiceProto.PraiseReqType;
import com.rwproto.PraiseServiceProto.PraiseSomeoneReqMsg;

/**
 * @Author HC
 * @date 2016年10月14日 上午11:23:42
 * @desc 点赞的处理类
 **/

public class PraiseHandler {
	private static PraiseHandler handler = new PraiseHandler();

	static PraiseHandler getHandler() {
		return handler;
	}

	PraiseHandler() {
	}

	/**
	 * 获取某个人的点赞数据
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString getPraiseDataHandler(Player player, GetPraiseReqMsg req) {
		String getPraiseUserId = req.getUserId();// 要获取点赞数据的人Id

		GetPraiseRspMsg.Builder getPraiseRsp = GetPraiseRspMsg.newBuilder();
		PraiseHelper.getInstance().fillPraiseMsgByUserId(getPraiseUserId, getPraiseRsp);

		PraiseCommonRspMsg.Builder rsp = PraiseCommonRspMsg.newBuilder();
		rsp.setReqType(PraiseReqType.GET_PRAISE_TYPE);
		rsp.setGetPraiseRspMsg(getPraiseRsp);

		return fillRspMsg(rsp, true, null);
	}

	/**
	 * 给某人点赞
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString praiseToSomeoneHandler(Player player, PraiseSomeoneReqMsg req) {
		String userId = player.getUserId();
		String praiseId = req.getUserId();

		PraiseMgr mgr = PraiseMgr.getMgr();

		PraiseCommonRspMsg.Builder rsp = PraiseCommonRspMsg.newBuilder();
		rsp.setReqType(PraiseReqType.PRAISE_SOMEONE_TYPE);

		if (mgr.hasPraisedSomeOne(userId, praiseId)) {
			return fillRspMsg(rsp, false, "每天只能对同一个玩家点赞1次");
		}

		// 增加人气
		PraiseHelper.getInstance().updatePopularityByUserId(praiseId);
		// 修改已经点赞的人的列表
		mgr.addPraise(userId, praiseId);

		return fillRspMsg(rsp, true, null);
	}

	/**
	 * 填充结果
	 * 
	 * @param rsp
	 * @param isSuccess
	 * @param tipMsg
	 * @return
	 */
	private ByteString fillRspMsg(PraiseCommonRspMsg.Builder rsp, boolean isSuccess, String tipMsg) {
		rsp.setIsSuccess(isSuccess);
		if (!StringUtils.isEmpty(tipMsg)) {
			rsp.setTipMsg(tipMsg);
		}
		return rsp.build().toByteString();
	}
}