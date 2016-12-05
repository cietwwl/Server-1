package com.gm.multipletimeshotfix;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.timer.Timer;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.net.BenefitMsgController;
import com.bm.targetSell.net.TargetSellOpType;
import com.bm.targetSell.param.TargetSellData;
import com.bm.targetSell.param.TargetSellGetItemParam;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.common.Pair;
import com.rw.manager.ServerSwitch;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwbase.dao.targetSell.BenefitDataDAO;
import com.rwbase.dao.targetSell.BenefitItems;
import com.rwbase.dao.targetSell.TargetSellRecord;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.TargetSellProto.TargetSellReqMsg;
import com.rwproto.TargetSellProto.TargetSellRespMsg;

public class TargetSellMgrFix extends TargetSellManager{

	
	private ConcurrentHashMap<String, Pair<Integer, Long>> PreChargeMap = new ConcurrentHashMap<String, Pair<Integer, Long>>();
	
	public void getDatas(){
		try {
			Field field = TargetSellManager.class.getDeclaredField("PreChargeMap");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			ConcurrentHashMap<String, Pair<Integer, Long>> map = (ConcurrentHashMap<String, Pair<Integer, Long>>) field.get(null);
			if(map != null){
				for(Map.Entry<String, Pair<Integer, Long>> entry :map.entrySet()){
					String key = entry.getKey();
					Pair<Integer, Long> pair = map.remove(key);
					if(pair == null){
						continue;
					}
					PreChargeMap.putIfAbsent(key, pair);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public void playerCharge(Player player, float charge) {
		try {
			if (!ServerSwitch.isOpenTargetSell()) {
				return;
			}
			int score = 0;
			BenefitDataDAO dataDao = BenefitDataDAO.getDao();
			TargetSellRecord record = dataDao.get(player.getUserId());
			
			score = record.getBenefitScore();
			score += charge;
			// 检查一下是否前置充值记录
			Pair<Integer, Long> preCharge = PreChargeMap.remove(player.getUserId());
			BenefitItems items = null;
			if (preCharge != null) {
				// 存在前置记录，检查积分是否可以购买
				Map<Integer, BenefitItems> map = record.getItemMap();
				items = map.remove(preCharge.getT1());
				if (items != null && items.getRecharge() <= score && (preCharge.getT2() + Timer.ONE_MINUTE) >= System.currentTimeMillis()) {
					// 积分可以购买并且还没有超时
					String mailID = PublicDataCfgDAO.getInstance().getPublicDataStringValueById(PublicData.BENEFIT_ITEM_BUY_SUC);
					Map<Integer, Integer> mailAttach = new HashMap<Integer, Integer>();
					
					List<ItemInfo> info = tranfer2ItemInfo(items);
					for (ItemInfo item : info) {
						Integer num = mailAttach.get(item.getItemID());
						if (num == null) {
							mailAttach.put(item.getItemID(), item.getItemNum());
						} else {
							mailAttach.put(item.getItemID(), item.getItemNum() + num);
						}
					}
					boolean suc = EmailUtils.sendEmail(player.getUserId(), mailID, mailAttach);
					if (suc) {
						score -= items.getRecharge();
						// 通知精准服
						notifyBenefitServerRoleGetItem(player, preCharge.getT1());
					}
				}
			}
			record.setBenefitScore(score);
			dataDao.update(record);
			
			// 通知前端
			ClientDataSynMgr.synData(player, record, eSynType.BENEFIT_SELL_DATA, eSynOpType.UPDATE_SINGLE);
			
		} catch (Throwable e) {
			GameLog.error(LogModule.COMMON.getName(), "TargetSellHotfix[playerCharge]", "精准营销角色充值判断出现异常！", e);
		}
	}
	
	
	
	
	
	
	@Override
	public void checkAndPackHeroChanged(String userId, boolean forceRead) {
		
		super.checkAndPackHeroChanged(userId, forceRead);
		checkAndRemovePreChangeRecord(userId);
		
	
	}

	
	public void checkAndRemovePreChangeRecord(String userID){
		Pair<Integer, Long> pair = PreChargeMap.get(userID);
		if(pair != null){
			long passTime = pair.getT2() + Timer.ONE_MINUTE * 10;
			if(passTime < System.currentTimeMillis()){
				PreChargeMap.remove(userID);//清除超时记录
			}
		}
	}

	


	/**
	 * 通知精准服角色领取目标物品
	 * @param player
	 * @param itemGroupID
	 */
	public void notifyBenefitServerRoleGetItem(Player player, int itemGroupID) {
		if (player == null) {
			return;
		}
		TargetSellData data = TargetSellData.create(TargetSellOpType.OPTYPE_5007);
		TargetSellGetItemParam itemP = new TargetSellGetItemParam();
		User user = UserDataDao.getInstance().getByUserId(player.getUserId());
		if (user == null) {
			GameLog.error("TargetSell", "TargetSellManager[notifyBenefitServerRoleGetItem]", "角色领取优惠物品，通知精准服时无法找到user数据", null);
			return;
		}
		itemP = initCommonParam(itemP, user.getChannelId(), player.getUserId(), user.getAccount());
		itemP.setItemGroupId(itemGroupID);
		data.setArgs(toJsonObj(itemP));
		sendMsg(toJsonString(data));
	}
	
	private void sendMsg(final String content) {
		if (!ServerSwitch.isOpenTargetSell()) {
			return;
		}
		BenefitMsgController.getInstance().addMsg(content);
	}
	

	
	private List<ItemInfo> tranfer2ItemInfo(BenefitItems item) {
		List<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();
		String itemIds = item.getItemIds();
		String[] itemData = itemIds.split(",");
		for (String subStr : itemData) {
			String[] sb = subStr.split("\\*");
			ItemInfo i;
			if (sb.length < 2) {
				i = new ItemInfo(Integer.parseInt(sb[0].trim()), 1);
			} else {
				i = new ItemInfo(Integer.parseInt(sb[0].trim()), Integer.parseInt(sb[1].trim()));
			}
			itemInfoList.add(i);
		}
		return itemInfoList;
	}

	@Override
	public ByteString roleChargeItem(Player player, TargetSellReqMsg request) {
		TargetSellRespMsg.Builder respMsg = TargetSellRespMsg.newBuilder();
		respMsg.setReqType(request.getReqType());
		int id = request.getItemGroupId();
		// 检查一下是否存在道具组ID
		BenefitDataDAO dataDao = BenefitDataDAO.getDao();
		TargetSellRecord record = dataDao.get(player.getUserId());
		Map<Integer, BenefitItems> map = record.getItemMap();
		if (map.containsKey(id)) {
			this.PreChargeMap.put(player.getUserId(), Pair.Create(id, System.currentTimeMillis()));
			respMsg.setIsSuccess(true);
		} else {
			respMsg.setIsSuccess(false);
			respMsg.setTipsMsg("不存在此奖励记录");
		}
		return respMsg.build().toByteString();
	}
	
	
	
	
}
