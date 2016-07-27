package com.rwbase.dao.store.wakenlotterydraw;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.common.HPCUtil;
import com.playerdata.Player;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.enu.eStoreType;
import com.rwbase.dao.item.SpecialItemCfgDAO;
import com.rwbase.dao.store.WakenLotteryDrawCfgDAO;
import com.rwbase.dao.store.WakenLotteryRewardPoolCfgDAO;
import com.rwbase.dao.store.pojo.StoreData;
import com.rwbase.dao.store.pojo.StoreDataHolder;
import com.rwbase.dao.store.pojo.TableStore;
import com.rwbase.dao.store.pojo.WakenLotteryDrawCfg;
import com.rwbase.dao.store.pojo.WakenLotteryRewardPoolCfg;
import com.rwproto.StoreProtos.StoreResponse;
import com.rwproto.StoreProtos.eStoreRequestType;
import com.rwproto.StoreProtos.eStoreResultType;
import com.rwproto.StoreProtos.eWakenRewardDrawType;
import com.rwproto.StoreProtos.tagCommodity;
import com.rwproto.StoreProtos.tagReward;

public class WakenLotteryProcesser {
	
	public final static int TYPE_FRIST_FREE_DRAW = 1;  	//免费首抽
	public final static int TYPE_FRIST_PAY_DRAW = 2;   	//付费首抽
	public final static int TYPE_FREE_DRAW = 3;			//免费抽
	public final static int TYPE_PAY_DRAW = 4;			//付费抽
	public final static int TYPE_GUARANTEE_DRAW = 5;	//保底抽
	public final static int TYPE_TEN_DRAW = 6;          //十连抽
	
	private final static HashMap<Integer, IWakenLotteryDraw> WakenLotteryProcesserMap = new HashMap<Integer, IWakenLotteryDraw>();
	
	static{
		WakenLotteryProcesserMap.put(TYPE_FRIST_FREE_DRAW, new FirstFreeWakenLotteryDraw());
		WakenLotteryProcesserMap.put(TYPE_FRIST_PAY_DRAW, new FirstPayWakenLotteryDraw());
		WakenLotteryProcesserMap.put(TYPE_FREE_DRAW, new FreeWakenLotteryDraw());
		WakenLotteryProcesserMap.put(TYPE_PAY_DRAW, new PayWakenLotteryDraw());
		WakenLotteryProcesserMap.put(TYPE_GUARANTEE_DRAW, new GuaranteeLotteryDraw());
		WakenLotteryProcesserMap.put(TYPE_TEN_DRAW, new TenWakenLotteryDraw());
	}
	
	private static WakenLotteryProcesser instance = new WakenLotteryProcesser();
	
	public static WakenLotteryProcesser getInstantce(){
		return instance;
	}
	
	/**
	 * 处理觉醒宝箱抽取
	 * @param player
	 * @param holder
	 * @param type
	 * @param resp
	 */
	public void processWakenLottery(Player player, StoreDataHolder holder, eWakenRewardDrawType type, StoreResponse.Builder resp, int consumeType) {
		int lotteryDrawType = type.getNumber();
		int level = player.getLevel();
		WakenLotteryDrawCfg cfg = WakenLotteryDrawCfgDAO.getInstance().getCfgByType(lotteryDrawType, level);
		
		TableStore tableStore = holder.get();
		ConcurrentHashMap<Integer,StoreData> storeDataMap = tableStore.getStoreDataMap();
		StoreData storeData = storeDataMap.get(eStoreType.Waken.getOrder());
		checkDrawReset(player, storeData, cfg);
		
		
		//判断是否金钱充足
		if(storeData.getDrawTime() >= cfg.getFreeTime() && !checkEnoughConsumeAndUse(player, cfg, consumeType)){
			resp.setReslutType(eStoreResultType.FAIL);
			resp.setReslutValue(SpecialItemCfgDAO.getDAO().getCfgById(String.valueOf(consumeType)).getName() + "不足");
			return;
		}
		
		int processType = getProcessType(storeData, cfg, type);
		IWakenLotteryDraw handler = WakenLotteryProcesserMap.get(processType);
		HashMap<Integer, Integer> map = handler.lotteryDraw(player, holder, cfg);
		int guaranteeTime = storeData.getRecordGuaranteeTime();
		
		
		if (processType != TYPE_TEN_DRAW) {
			if (processType != TYPE_GUARANTEE_DRAW) {
				guaranteeTime++;
				storeData.setRecordGuaranteeTime(guaranteeTime);
			}
			if (guaranteeTime == cfg.getGuaranteeeTime()) {
				storeData.setRecordGuaranteeTime(0);
			}

		}
		for (Iterator<Entry<Integer, Integer>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, Integer> entry = iterator.next();
			Integer modelId = entry.getKey();
			Integer count = entry.getValue();
			player.getItemBagMgr().addItem(modelId, count);
			if (modelId != eSpecialItemId.WAKEN_PIECE.getValue()) {
				tagReward.Builder reward = tagReward.newBuilder();
				reward.setModelId(modelId);
				reward.setCount(count);
				resp.addRewards(reward);
			}
		}
		storeData.setLastDrawTime(System.currentTimeMillis());
		holder.update(player, eStoreType.Waken.getOrder());
		resp.setReslutType(eStoreResultType.SUCCESS);
	}
	
	private boolean checkEnoughConsumeAndUse(Player player, WakenLotteryDrawCfg cfg, int consumeType){
		HashMap<Integer,Integer> consumeMap = cfg.getConsumeMap();
		for (Iterator<Entry<Integer, Integer>> iterator = consumeMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, Integer> entry = iterator.next();
			int cfgId = entry.getKey();
			int value = entry.getValue();
			if (consumeType == cfgId) {
				if (player.getItemBagMgr().checkEnoughItem(cfgId, value)) {
					player.getItemBagMgr().addItem(cfgId, -1 * value);
					return true;
				}
			}
			
		}
		return false;
	}
	
	/**
	 * 检测是否需要重置次数
	 * @param player
	 * @param holder
	 * @param cfg
	 */
	public void checkDrawReset(Player player, StoreData storeData, WakenLotteryDrawCfg cfg){
		long lastDrawTime = storeData.getLastDrawTime();
		int resetTime = Integer.parseInt(cfg.getResetTime());
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.HOUR_OF_DAY, resetTime);
		long time = instance.getTimeInMillis();
		if(lastDrawTime < time){
			storeData.setDrawTime(0);
		}
	}
	
	/**
	 * 获取抽取的类型
	 * @param tableStore
	 * @param cfg
	 * @return
	 */
	private int getProcessType(StoreData storeData, WakenLotteryDrawCfg cfg, eWakenRewardDrawType type){
		
		if(type == eWakenRewardDrawType.tenDraw)
			return TYPE_TEN_DRAW;
		int processType = TYPE_PAY_DRAW;
		if(storeData.getDrawTime() < cfg.getFreeTime()){
			if(storeData.isFirstFreeLottery()){
				processType = TYPE_FRIST_FREE_DRAW;
			}else{
				processType = TYPE_FREE_DRAW;
			}
		}else{
			if(storeData.isFirstPayLottery()){
				processType = TYPE_FRIST_PAY_DRAW;
			}else{
				processType = TYPE_PAY_DRAW;
			}
		}
		
		if(storeData.getRecordGuaranteeTime() + 1 >= cfg.getGuaranteeeTime()){
			processType = TYPE_GUARANTEE_DRAW;
		}
		
		return processType;
	}
	
	/**
	 * 获取随机的奖品
	 * @param poolIds
	 * @return
	 */
	public WakenLotteryResult processLottery(List<Integer> poolIds){
		HashMap<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
		WakenLotteryResult wakenLotteryResult = new WakenLotteryResult();
		boolean isGuarantee = false;
		for (Integer poolId : poolIds) {
			List<WakenLotteryRewardPoolCfg> list = WakenLotteryRewardPoolCfgDAO.getInstance().getWakenLotteryRewardPoolCfgByPoolId(poolId);
			int sumWeight = WakenLotteryRewardPoolCfgDAO.getInstance().getSumWeightByPoolId(poolId);
			int result = HPCUtil.getRandom().nextInt(sumWeight);
			WakenLotteryRewardPoolCfg randomResult = getRandomResult(list, result);
			rewardMap.put(randomResult.getRewardItemId(), randomResult.getCount());
			if (randomResult.getIsGuarantee() == 1) {
				isGuarantee = true;
			}

		}
		wakenLotteryResult.setRewardMap(rewardMap);
		wakenLotteryResult.setGuarantee(isGuarantee);
		
		return wakenLotteryResult;
	} 
	
	private WakenLotteryRewardPoolCfg getRandomResult(List<WakenLotteryRewardPoolCfg> list, int randomResult) {
		int value = 0;
		for (WakenLotteryRewardPoolCfg wakenLotteryRewardPoolCfg : list) {
			value += wakenLotteryRewardPoolCfg.getWeight();
			if (randomResult < value) {
				return wakenLotteryRewardPoolCfg;
			}
		}
		return list.size() > 0 ? list.get(0) : null;
	}
}
