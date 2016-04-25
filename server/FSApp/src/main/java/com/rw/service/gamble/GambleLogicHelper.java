package com.rw.service.gamble;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.gamble.datamodel.GambleDropHistory;
import com.rw.service.gamble.datamodel.GambleHotHeroPlan;
import com.rw.service.gamble.datamodel.GambleOnePlanDropData;
import com.rw.service.gamble.datamodel.GamblePlanCfg;
import com.rw.service.gamble.datamodel.GamblePlanCfgHelper;
import com.rw.service.gamble.datamodel.GambleRecord;
import com.rw.service.gamble.datamodel.GambleRecordDAO;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwproto.GambleServiceProtos.DropData;
import com.rwproto.GambleServiceProtos.EGambleRequestType;
import com.rwproto.GambleServiceProtos.EGambleResultType;
import com.rwproto.GambleServiceProtos.EGambleType;
import com.rwproto.GambleServiceProtos.ELotteryType;
import com.rwproto.GambleServiceProtos.GambleData;
import com.rwproto.GambleServiceProtos.GambleRequest;
import com.rwproto.GambleServiceProtos.GambleResponse;
import com.rwproto.GambleServiceProtos.GambleResponse.Builder;
import com.rwproto.GambleServiceProtos.GambleRewardData;
import com.rwproto.MsgDef.Command;

public class GambleLogicHelper {
	/** 准备推送的垂钓数据 */
	public static GambleResponse.Builder prepareGambleData(GambleRequest request,Random ranGen, String defaultItem,String uid) {
		GambleHotHeroPlan.InitTodayHotHeroList(ranGen,defaultItem);
		GambleResponse.Builder response = GambleResponse.newBuilder();
		response.setRequest(request);
		response.addAllHeroList(GambleHotHeroPlan.getTodayHotList());
		
		//TODO 暂时未按新协议发送数据，最大限度减少客户端的修改
		//response.addAllDropHistory(getFinshingData(uid));
		
		//填写兼容旧协议的数据
		response.setGambleData(getFishingItemToData(uid));
		response.setResultType(EGambleResultType.SUCCESS);
		return response;
	}
	
	public static Iterable<DropData> getFinshingData(String userId){
		GamblePlanCfgHelper helper = GamblePlanCfgHelper.getInstance();
		Iterable<GamblePlanCfg> all = helper.getIterateAllCfg();
		ArrayList<DropData> result = new ArrayList<DropData>(helper.getEntryCount());
		for (GamblePlanCfg cfg : all) {
			result.add(getFinshingData(userId,cfg.getKey()));
		}
		return result;
	}
	
	public static boolean canGambleFreely(String userId){
		GamblePlanCfgHelper helper = GamblePlanCfgHelper.getInstance();
		Iterable<GamblePlanCfg> all = helper.getIterateAllCfg();
		for (GamblePlanCfg cfg : all) {
			if (isFree(userId,cfg.getKey())){
				return true;
			}
		}
		return false;
	}
	
	//根据配置的方案，找到数据库里面的历史信息并返回
	public static DropData getFinshingData(String userId,int planId){
		GambleOnePlanDropData oneData = getOneDropData(userId,planId);
		DropData.Builder result = DropData.newBuilder();
		result.setFreeCount(oneData.getFreeCount());
		result.setLeftTime(oneData.getLeftTime());
		result.setMaxFreeCount(oneData.getMaxFreeCount());
		return result.build();
	}
	
	public static GambleOnePlanDropData getOneDropData(String userId,int planId){
		String planIdStr = String.valueOf(planId);
		GamblePlanCfg planCfg = GamblePlanCfgHelper.getInstance().getCfgById(planIdStr);
		GambleRecordDAO gambleRecords = GambleRecordDAO.getInstance();
		GambleRecord record = gambleRecords.getOrCreate(userId);
		GambleDropHistory historyRecord = record.getHistory(planId);

		GambleOnePlanDropData result = new GambleOnePlanDropData(historyRecord,planCfg);
		return result;
	}
	
	public static void pushGambleItem(Player player,Random ranGen,String defaultItem) {
		GambleRequest.Builder request = GambleRequest.newBuilder();
		request.setRequestType(EGambleRequestType.GAMBLE_DATA);
		GambleResponse.Builder response = prepareGambleData(request.build(),ranGen, defaultItem,player.getUserId());
		player.SendMsg(Command.MSG_GAMBLE, response.build().toByteString());
	}

	/**
	 * 用一个默认值做容错，并假设这个默认值是没有问题的！
	 * @param dropList
	 * @param slotCount
	 * @param itemModelId
	 * @param uid
	 * @param planIdStr
	 * @param defaultModelId
	 * @return
	 */
	public static boolean add2DropList(ArrayList<GambleRewardData> dropList, int slotCount, String itemModelId,String uid, String planIdStr,String defaultModelId) {
		//检查是否具有有效的佣兵配置
		if (StringUtils.isBlank(itemModelId)){
			GameLog.error("钓鱼台", uid, String.format("配置物品ID无效，配置:%s", planIdStr));
			itemModelId = defaultModelId;
			//return false;
		}
		if (slotCount <= 0){
			GameLog.error("钓鱼台", uid, String.format("配置叠加数量无效，配置:%s", planIdStr));
			slotCount = 1;
			//return false;
		}
		
		if (itemModelId.indexOf("_") != -1) {
			String[] arr = itemModelId.split("_");
			if (arr == null) {
				itemModelId = defaultModelId;
				//return false;
			}else{
				RoleCfg roleCfg = RoleCfgDAO.getInstance().getConfig(itemModelId);
				if (roleCfg == null) {
					itemModelId = defaultModelId;
					GameLog.error("钓鱼模块", uid, "钓鱼随机到了模版Id为：" + itemModelId + "的英雄，配置不存在,ID="+planIdStr);
					//return false;
				}
			}
		}

		GambleRewardData.Builder rewardData = GambleRewardData.newBuilder();
		rewardData.setItemId(itemModelId);
		rewardData.setItemNum(slotCount);
		dropList.add(rewardData.build());
		return true;
	}

	public static ByteString SetError(Builder response, Player player, String errLog, String tip) {
		GameLog.error("钓鱼台", player.getUserId(), errLog);
		if(!StringUtils.isBlank(tip)) {
			response.setTips(tip);
		}
		return response.build().toByteString();
	}

	public static boolean isValidHeroId(String itemModelId) {
		RoleCfg roleCfg = RoleCfgDAO.getInstance().getConfig(itemModelId);
		return roleCfg != null;
	}

	public static boolean isFree(String userId, int planId) {
		GambleOnePlanDropData oneData = getOneDropData(userId,planId);
		return oneData.canGambleFree();
	}
	
	public static boolean isLeftTimeOver(int leftTime){
		//让服务端比客户端快2秒
		return leftTime <= 2;
	}
	
	//为了兼容旧的协议而做的映射，写死在这个类 以后客户端做好对应修改后删除
	public static final int Primary_One = 1;
	public static final int Middle_One = 2;
	public static final int Advanced_Six = 3;
	public static final int Primary_Ten = 4;
	public static final int Middle_Ten = 5;

	// 返回旧协议的钓鱼数据
	public static GambleData getFishingItemToData(String uid) {
		GambleData.Builder data = GambleData.newBuilder();
		GambleOnePlanDropData p1 = GambleLogicHelper.getOneDropData(uid, Primary_One);
		// 当天已用普通免费次数
		data.setPrimaryCount(p1.getFreeCount());
		// 初级祈祷免费剩余时间(秒)
		data.setPrimaryTime(p1.getLeftTime());
		GambleOnePlanDropData m1 = GambleLogicHelper.getOneDropData(uid, Middle_One);
		// 中级祈祷免费剩余时间(秒)
		data.setMiddleTime(m1.getLeftTime());
		return data.build();
	}

	// 转换请求类型
	public static GambleRequest ConvertRequest(GambleRequest request) {
		EGambleType gambleType = request.getGambleType();
		ELotteryType lotteryType = request.getLotteryType();
		int planKey = 0;
		switch (gambleType) {
		case PRIMARY:
			if (lotteryType == ELotteryType.ONE) {
				planKey = Primary_One;
			} else if (lotteryType == ELotteryType.TEN) {
				planKey = Primary_Ten;
			}
			break;
		case MIDDLE:
			if (lotteryType == ELotteryType.ONE) {
				planKey = Middle_One;
			} else if (lotteryType == ELotteryType.TEN) {
				planKey = Middle_Ten;
			}
			break;
		case ADVANCED:
			if (lotteryType == ELotteryType.SIX) {
				planKey = Advanced_Six;
			}
			break;
		}
		GambleRequest.Builder result = GambleRequest.newBuilder();
		result.mergeFrom(request);
		result.setGamblePlanId(planKey);
		return result.build();
	}
}
