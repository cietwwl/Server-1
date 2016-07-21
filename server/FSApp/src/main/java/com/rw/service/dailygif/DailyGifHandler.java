package com.rw.service.dailygif;

import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BilogItemInfo;
import com.rwbase.dao.business.SevenDayGifCfg;
import com.rwbase.dao.business.SevenDayGifCfgDAO;
import com.rwbase.dao.business.SevenDayGifInfo;
import com.rwproto.DailyGifProtos.DailyGifResponse;
import com.rwproto.DailyGifProtos.EType;
import com.rwproto.MsgDef;

public class DailyGifHandler {
	private static DailyGifHandler instance = new DailyGifHandler();

	private DailyGifHandler() {
	}

	public static DailyGifHandler getInstance() {
		return instance;
	}

	/*** 获取基本信息 ***/
	public ByteString getInfo(Player player) 
	{	
		DailyGifResponse.Builder res = DailyGifResponse.newBuilder();
		// res.setInfo(otherRoleAttr);
		res.setType(EType.InfoMsg);
		res.setCount(player.getDailyGifMgr().getTable().getCount());
		List<Integer> temps = player.getDailyGifMgr().getTable().getCounts();
		res.addAllGetCount(temps);
//		res.setGetCount(player.getDailyGifMgr().getTable().getCounts());
		return res.build().toByteString();
	}

	/*** 领取礼包 **/
	public ByteString getGif(Player player, int count) {
		if(count <= 0 || count > 7){
			return null;
		}
		DailyGifResponse.Builder res = DailyGifResponse.newBuilder();
		SevenDayGifInfo dailyGiftData = player.getDailyGifMgr().getTable();
		if (dailyGiftData.getCounts().size() >= dailyGiftData.getCount()) {
			return null;
		}

		List<Integer> temps = dailyGiftData.getCounts();
		temps.add(count);
		dailyGiftData.setCounts(temps);

		res.setType(EType.GetGif);
		res.setCount(dailyGiftData.getCount());
		res.addAllGetCount(dailyGiftData.getCounts());
		sendGoods(player, count);

		player.getDailyGifMgr().save();
		return res.build().toByteString();
	}

	public void sendInfo(Player player) {
		player.SendMsg(MsgDef.Command.MSG_DailyGif, getInfo(player));
	}

	/*** 给主角增加任务的奖励 **/
	private void sendGoods(Player player, int count) {
		SevenDayGifCfg dailyGif = SevenDayGifCfgDAO.getInstance().getCfg(count);
		if (dailyGif != null) {
			// 给主角增加任务的奖励
			String[] reward = dailyGif.goods.split("\\|");
			for (int i = 0; i < reward.length; i++) {
				String[] rewardItem = reward[i].split("_");
				if (rewardItem.length > 1) {
					player.getItemBagMgr().addItem(Integer.parseInt(rewardItem[0]), Integer.parseInt(rewardItem[1]));
				}
			}
			List<BilogItemInfo> rewardslist = BilogItemInfo.fromStrArr(reward);
			String rewardInfoActivity = BILogTemplateHelper.getString(rewardslist);				
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.SEVENDAYACTIVITY,0,0);
			BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.SEVENDAYACTIVITY, 0, true, 0, rewardInfoActivity,0);
		}
	}
}
