package com.rwbase.dao.worship;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import com.playerdata.FashionMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.FashionMgrIF;
import com.playerdata.readonly.FashionMgrIF.ItemFilter;
import com.playerdata.readonly.PlayerIF;
import com.rwbase.dao.fashion.FashState;
import com.rwbase.dao.fashion.FashionItemIF;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.worship.pojo.CfgWorshipRandomReward;
import com.rwbase.dao.worship.pojo.CfgWorshipRankdomScheme;
import com.rwbase.dao.worship.pojo.WorshipItem;
import com.rwbase.dao.worship.pojo.WorshipItemData;
import com.rwproto.WorshipServiceProtos.WorshipInfo;
import com.rwproto.WorshipServiceProtos.WorshipRewardData;

public class WorshipUtils {
	/***/
	public static WorshipItemData getRandomRewardData(int scheme){
		CfgWorshipRankdomScheme schemeCfg = CfgWorshipRankdomSchemeHelper.getInstance().getWorshipRewardCfg(scheme);
		int weightGroup = WorshipUtils.getRandomWeightGroup(schemeCfg.getProbabilityList());
		List<CfgWorshipRandomReward> list = CfgWorshipRandomRewardHelper.getInstance().getWorshipRewardCfg(scheme, weightGroup);
		return WorshipUtils.getRandomRewardData(list);
	}
	
	/**直接获取奖励数据*/
	public static WorshipItemData getRandomRewardData(List<CfgWorshipRandomReward> list){
		CfgWorshipRandomReward cfg = getRandomRewardCfg(list);
		if(cfg == null){
			return null;
		}
		WorshipItemData rewardData = new WorshipItemData();
    	rewardData.setItemId(String.valueOf(cfg.getItemID()));
    	rewardData.setCount(new Random().nextInt(cfg.getMax()) + 1);
    	return rewardData;
	}
	
	/**获取一个随机奖励配置*/
	public static CfgWorshipRandomReward getRandomRewardCfg(List<CfgWorshipRandomReward> list){
		if(list == null){
			return null;
		}
		int weightTotal = 0;
		for(CfgWorshipRandomReward cfg : list){
			weightTotal += cfg.getWeight();
		}
		int random = new Random().nextInt(weightTotal);
		int temp = 0;
		for(CfgWorshipRandomReward cfg : list){
			temp += cfg.getWeight();
			if(random < temp){
				return cfg;
			}
		}
		return null;
	}
	
	/**获取一个随机权重组*/
	public static int getRandomWeightGroup(String[] list){
		int weightTotal = 0;
		for(String temp : list){
			String[] temp2 = temp.split("_");
			weightTotal += Integer.parseInt(temp2[1]);
		}
		int random = new Random().nextInt(weightTotal);
		int temp3 = 0;
		for(String temp : list){
			String[] temp2 = temp.split("_");
			temp3 += Integer.parseInt(temp2[1]);
			if(random < temp3){
				return Integer.valueOf(temp2[0]);
			}
		}
		return 0;
	}
	
	/**排行信息转换为膜拜信息*/
	public static WorshipInfo rankInfoToWorshipInfo(RankingLevelData rankInfo){
		if(rankInfo == null){
			return null;
		}
		WorshipInfo.Builder worshipInfo = WorshipInfo.newBuilder();
		worshipInfo.setUserId(rankInfo.getUserId());
		worshipInfo.setUserName(rankInfo.getUserName());
		worshipInfo.setCareer(rankInfo.getJob());
		worshipInfo.setSex(rankInfo.getSex());
		worshipInfo.setCareerLevel(rankInfo.getCareerLevel());
		worshipInfo.setImageId(rankInfo.getUserHead());
		worshipInfo.setFightingAll(rankInfo.getFightingAll());
		worshipInfo.setLevel(rankInfo.getLevel());
		worshipInfo.setSex(rankInfo.getSex());
		worshipInfo.setCareerLevel(rankInfo.getCareerLevel());
		worshipInfo.setModelId(rankInfo.getModelId());
		
		PlayerIF readOnlyPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(rankInfo.getUserId());
		if (readOnlyPlayer != null){
			FashionMgrIF fmgr = readOnlyPlayer.getFashionMgr();
			List<FashionItemIF> swingOn = fmgr.search(fmgr.getSwingOnItemPred());
			if (swingOn != null && swingOn.size() > 0){
				FashionItemIF item = swingOn.get(0);
				worshipInfo.setSwingID(item.getId());
			}
		}else{
			//print error log
		}
		
		return worshipInfo.build();
	}
	
	/**玩家信息转换为膜拜信息*/
	public static WorshipItem playerInfoToWorshipItem(Player player, WorshipItemData itemData){
		WorshipItem worshipItem = new WorshipItem();
		worshipItem.setUserId(player.getUserId());
		worshipItem.setUserName(player.getUserName());
		worshipItem.setCareer(player.getCareer());
		worshipItem.setSex(player.getSex());
		worshipItem.setCareerLevel(player.getStarLevel());
		worshipItem.setImageId(player.getHeadImage());
		worshipItem.setFightingAll(player.getHeroMgr().getFightingAll());
		worshipItem.setLevel(player.getLevel());
		worshipItem.setCanReceive(true);
		worshipItem.setItemData(itemData);
		worshipItem.setWorshipTime(Calendar.getInstance().getTimeInMillis());
		worshipItem.setModelId(player.getModelId());
		return worshipItem;
	}
	
	public static List<WorshipInfo> toWorshipList(List<WorshipItem> itemList){
		List<WorshipInfo> infolist = new ArrayList<WorshipInfo>();
		for(WorshipItem item : itemList){
			infolist.add(worshipItemToWorshipInfo(item));
		}
		return infolist;
	}
	
	/**膜拜数据转换*/
	public static WorshipInfo worshipItemToWorshipInfo(WorshipItem worshipItem){
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
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(worshipItem.getWorshipTime());		
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);		
		
		worshipInfo.setTime((hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute));
		WorshipRewardData rewardData = worshipRewardDataToItemData(worshipItem.getItemData());
		if(rewardData != null){
			worshipInfo.setRandomRward(rewardData);
		}
		return worshipInfo.build();
	}
	
	public static WorshipRewardData worshipRewardDataToItemData(WorshipItemData itemData){
		if(itemData == null){
			return null;
		}
		WorshipRewardData.Builder rewardData = WorshipRewardData.newBuilder();
		rewardData.setItemId(itemData.getItemId());
		rewardData.setCount(itemData.getCount());	
		return rewardData.build();
	}
}
