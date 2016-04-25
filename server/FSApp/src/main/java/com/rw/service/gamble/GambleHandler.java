package com.rw.service.gamble;

import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.gamble.GambleUtils;
import com.rwbase.dao.gamble.pojo.cfg.GambleCfg;
import com.rwbase.dao.gamble.pojo.cfg.GambleCfgDAO;
import com.rwproto.GambleServiceProtos.EGambleRequestType;
import com.rwproto.GambleServiceProtos.EGambleResultType;
import com.rwproto.GambleServiceProtos.EGambleType;
import com.rwproto.GambleServiceProtos.GambleRequest;
import com.rwproto.GambleServiceProtos.GambleResponse;
import com.rwproto.GambleServiceProtos.GambleRewardData;
import com.rwproto.MsgDef.Command;

public class GambleHandler {

	private static GambleHandler _instance;

	public static GambleHandler getInatance() {
		if (_instance == null) {
			_instance = new GambleHandler();
		}
		return _instance;
	}

	/** 赌博 */
	public ByteString gamble(GambleRequest request, Player player) {
		GambleResponse.Builder response = GambleResponse.newBuilder();
		response.setRequest(request);
		if (player.getGambleMgr().getCanGamble(player, request.getGambleType(), request.getLotteryType())) {
			System.err.println(player.getUserName() + "可以成功购买物品！！！！！！！");
			response.setResultType(EGambleResultType.SUCCESS);
			
			List<GambleRewardData> rewardList = player.getGambleMgr().getRewardResult(request.getGambleType(), request.getLotteryType());

			SetReward(rewardList, request.getGambleType(), player);
			response.addAllItemList(rewardList);
			pushGambleItem(player);
			UserEventMgr.getInstance().Gamble(player, request.getLotteryType().getNumber(), GambleCfgDAO.getInstance().getGambleCfg( request.getGambleType()).getMoneyType());
			
		} else {
			System.err.println(player.getUserName() + "购买物品失败了，原因不明！！！！！！！");
			response.setResultType(EGambleResultType.FAIL);
			pushGambleItem(player);
		}
		return response.build().toByteString();
	}
 
	/** 设置获得的奖励 */
	private void SetReward(final List<GambleRewardData> rewardList, EGambleType gambleType, final Player player) {
		String reward = "";
		for (int i = 0; i < rewardList.size(); i++) {
			GambleRewardData rewardData = rewardList.get(i);
			if (rewardData.getItemId().indexOf("_") != -1) {// 佣兵
				player.getHeroMgr().addHero(rewardData.getItemId());
				MainMsgHandler.getInstance().sendPmdJtYb(player, rewardData.getItemId());
			} else {
				reward += "," + rewardData.getItemId() + "~" + rewardData.getItemNum();
				MainMsgHandler.getInstance().sendPmdJtGoods(player, rewardData.getItemId());
			}
		}
		player.getItemBagMgr().addItemByPrizeStr(reward);

		GambleCfg cfg = GambleCfgDAO.getInstance().getGambleCfg(gambleType);
		if (cfg != null) {
			player.getItemBagMgr().addItem(cfg.getRewardItem(), rewardList.size());
		}
	}

	/** 请求赌博数据 */
	public ByteString gambleData(GambleRequest request, Player player) {
		GambleResponse.Builder response = GambleResponse.newBuilder();
		response.setRequest(request);
		response.setResultType(EGambleResultType.SUCCESS);
		response.addAllHeroList(GambleUtils.getRewardCfgToString(player.getGambleMgr().getDestinyHot()));
		response.setGambleData(GambleUtils.getFishintItemToData(player.getGambleMgr().getGambleItem()));
		return response.build().toByteString();
	}

	/** 推送垂钓数据 */
	public void pushGambleItem(Player player) {
		GambleResponse.Builder response = GambleResponse.newBuilder();
		GambleRequest.Builder request = GambleRequest.newBuilder();
		request.setRequestType(EGambleRequestType.GAMBLE_DATA);
		response.setRequest(request);
		response.setResultType(EGambleResultType.SUCCESS);
		response.addAllHeroList(GambleUtils.getRewardCfgToString(player.getGambleMgr().getDestinyHot()));
		response.setGambleData(GambleUtils.getFishintItemToData(player.getGambleMgr().getGambleItem()));
		player.SendMsg(Command.MSG_GAMBLE, response.build().toByteString());
	}
}
