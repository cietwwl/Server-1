package com.rwbase.dao.worship;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.rw.service.fashion.FashionHandle;
import com.rwbase.dao.ranking.RankingUtils;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.worship.pojo.WorshipItem;
import com.rwbase.dao.worship.pojo.WorshipItemData;
import com.rwproto.FashionServiceProtos.FashionUsed;
import com.rwproto.WorshipServiceProtos.WorshipInfo;
import com.rwproto.WorshipServiceProtos.WorshipRewardData;

public class WorshipUtils {
	
	public static final int UpperWorshipNum = 20;//发送给前端膜拜者数量
	
	
	
	/**
	 * 根据奖励字符串获取奖励数据
	 * @param rewardStr
	 * @return
	 */
	public static WorshipItemData getWorshipDataFromStr(String rewardStr){
		WorshipItemData data = new WorshipItemData();
		String[] str = rewardStr.split("~");
		data.setItemId(str[0].toString().trim());
		data.setCount(Integer.parseInt(str[1]));
		return data;
	}



	

	/** 排行信息转换为膜拜信息 */
	public static WorshipInfo rankInfoToWorshipInfo(RankingLevelData rankInfo) {
		if (rankInfo == null) {
			return null;
		}
		WorshipInfo.Builder worshipInfo = WorshipInfo.newBuilder();
		worshipInfo.setUserId(rankInfo.getUserId());
		worshipInfo.setUserName(rankInfo.getUserName());
		worshipInfo.setCareer(rankInfo.getJob());
		worshipInfo.setSex(rankInfo.getSex());
		worshipInfo.setCareerLevel(rankInfo.getCareerLevel());
		worshipInfo.setImageId(rankInfo.getUserHead());
		worshipInfo.setHeadFrame(rankInfo.getHeadbox());
		worshipInfo.setFightingAll(rankInfo.getFightingAll());
		worshipInfo.setLevel(rankInfo.getLevel());
		// worshipInfo.setModelId(rankInfo.getModelId());
		worshipInfo.setModelId(RankingUtils.getModelId(rankInfo));
		worshipInfo.setVip(rankInfo.getVip());
		//by Franky:
		FashionUsed.Builder fashionUsing = FashionHandle.getInstance().getFashionUsedProto(rankInfo.getUserId());
		if (fashionUsing != null){
			worshipInfo.setFashionUsage(fashionUsing);
		}
		
		return worshipInfo.build();
	}

	/** 玩家信息转换为膜拜信息 */
	public static WorshipItem playerInfoToWorshipItem(Player player, WorshipItemData itemData) {
		WorshipItem worshipItem = new WorshipItem();
		worshipItem.setUserId(player.getUserId());
		worshipItem.setUserName(player.getUserName());
		worshipItem.setCareer(player.getCareer());
		worshipItem.setSex(player.getSex());
		worshipItem.setCareerLevel(player.getStarLevel());
		worshipItem.setImageId(player.getHeadImage());
		worshipItem.setHeadFrame(player.getHeadFrame());
//		worshipItem.setFightingAll(player.getHeroMgr().getFightingAll());
		worshipItem.setFightingAll(player.getHeroMgr().getFightingAll(player));
		worshipItem.setLevel(player.getLevel());
		worshipItem.setCanReceive(true);
		worshipItem.setItemData(itemData);
		worshipItem.setWorshipTime(Calendar.getInstance().getTimeInMillis());
		worshipItem.setModelId(player.getModelId());
		worshipItem.setVip(player.getVip());
		return worshipItem;
	}

	public static Comparator<WorshipItem> comparator = new Comparator<WorshipItem>() {

		@Override
		public int compare(WorshipItem o1, WorshipItem o2) {
			if (o1.getWorshipTime() - o2.getWorshipTime() > 0) {
				return -1;
			} else {
				return 1;
			}
		}
	};

	public static List<WorshipInfo> toWorshipList(List<WorshipItem> itemList) {
		Collections.sort(itemList,comparator);
		List<WorshipInfo> infolist = new ArrayList<WorshipInfo>();
		for (WorshipItem item : itemList) {
			infolist.add(worshipItemToWorshipInfo(item));
		}
		return infolist;
	}

	/** 膜拜数据转换 */
	public static WorshipInfo worshipItemToWorshipInfo(WorshipItem worshipItem) {
		WorshipInfo.Builder worshipInfo = WorshipInfo.newBuilder();
		worshipInfo.setUserId(worshipItem.getUserId());
		worshipInfo.setUserName(worshipItem.getUserName());
		worshipInfo.setCareer(worshipItem.getCareer());
		worshipInfo.setSex(worshipItem.getSex());
		worshipInfo.setCareerLevel(worshipItem.getCareerLevel());
		worshipInfo.setImageId(worshipItem.getImageId());
		worshipInfo.setFightingAll(worshipItem.getFightingAll());
		worshipInfo.setLevel(worshipItem.getLevel());
		worshipInfo.setCanReceive(worshipItem.isCanReceive());
		worshipInfo.setModelId(worshipItem.getModelId());
		worshipInfo.setVip(worshipItem.getVip());
		
		String headFrame = worshipItem.getHeadFrame();
		if (StringUtils.isNotBlank(headFrame)) {//兼容旧数据
			worshipInfo.setHeadFrame(headFrame);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(worshipItem.getWorshipTime());
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		worshipInfo.setTime((hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute));
		WorshipRewardData rewardData = worshipRewardDataToItemData(worshipItem.getItemData());
		if (rewardData != null) {
			worshipInfo.setRandomRward(rewardData);
		}
		return worshipInfo.build();
	}

	public static WorshipRewardData worshipRewardDataToItemData(WorshipItemData itemData) {
		if (itemData == null) {
			return null;
		}
		WorshipRewardData.Builder rewardData = WorshipRewardData.newBuilder();
		rewardData.setItemId(itemData.getItemId());
		rewardData.setCount(itemData.getCount());
		return rewardData.build();
	}
}
