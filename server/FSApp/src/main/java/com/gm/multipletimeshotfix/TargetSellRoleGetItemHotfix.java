package com.gm.multipletimeshotfix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.net.BenefitMsgController;
import com.bm.targetSell.net.TargetSellOpType;
import com.bm.targetSell.param.TargetSellData;
import com.bm.targetSell.param.TargetSellGetItemParam;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.manager.ServerSwitch;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.targetSell.BenefitDataDAO;
import com.rwbase.dao.targetSell.BenefitItems;
import com.rwbase.dao.targetSell.TargetSellRecord;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.TargetSellProto.TargetSellReqMsg;
import com.rwproto.TargetSellProto.TargetSellRespMsg;

public class TargetSellRoleGetItemHotfix extends TargetSellManager{
	private Logger logger = Logger.getLogger("targetSellLogger");
	

	@Override
	public ByteString roleGetItem(Player player, TargetSellReqMsg request) {
		int itemGroupId = request.getItemGroupId();
		TargetSellRespMsg.Builder respMsg = TargetSellRespMsg.newBuilder();
		try {
			respMsg.setReqType(request.getReqType());
			BenefitDataDAO dataDao = BenefitDataDAO.getDao();
			TargetSellRecord record = dataDao.get(player.getUserId());

			if (record == null) {
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("不存在目标数据");
				return respMsg.build().toByteString();
			}
			Map<Integer, BenefitItems> itemMap = record.getItemMap();
			if (!itemMap.containsKey(itemGroupId)) {
				// 不存在此道具
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("无法找到目标道具");
				return respMsg.build().toByteString();
			}
			// 判断领取次数
			Map<Integer, Integer> recieveMap = record.getRecieveMap();

			if (recieveMap != null && recieveMap.containsKey(itemGroupId) && recieveMap.get(itemGroupId) <= 0) {
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("已经达到可领取上限，不可再领取");
				return respMsg.build().toByteString();
			}
			BenefitItems items = itemMap.get(itemGroupId);
//			BenefitItems items = itemMap.remove(itemGroupId);
			if(items == null){
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("无法找到目标道具");
				return respMsg.build().toByteString();
			}
			//增加积分检查，避免没有积分的时候也领取
			int benefitScore = record.getBenefitScore();
			if(benefitScore < items.getRecharge()){
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("积分不足，无法领取此奖励！");
				return respMsg.build().toByteString();
			}
			
			
			StringBuilder sb = new StringBuilder("玩家["+player.getUserName()+"],id:["+player.getUserId()
					+"],主动领取物品，当前积分：" + record.getBenefitScore()+",道具组id:" + itemGroupId + ",道具列表："+ items.getItemIds()
					+",消耗积分：" + items.getRecharge());
			// 添加道具
			boolean addItem = ItemBagMgr.getInstance().addItem(player, tranfer2ItemInfo(items));
			if (addItem) {
				itemMap.remove(itemGroupId);//删除目标道具数据
				// 通知精准服，玩家领取了道具,同时保存一下可领取次数
				notifyBenefitServerRoleGetItem(player, itemGroupId);
				if (recieveMap == null) {
					recieveMap = new HashMap<Integer, Integer>();
				}
				recieveMap.put(itemGroupId, items.getPushCount() - 1);
				record.setBenefitScore(benefitScore - items.getRecharge());
			}
			sb.append(",发送道具成功：").append(addItem);
			logger.info(sb.toString());
			dataDao.update(record);
			respMsg.setDataStr(items.getItemIds());
			respMsg.setIsSuccess(true);
			ClientDataSynMgr.synData(player, record, eSynType.BENEFIT_SELL_DATA, eSynOpType.UPDATE_SINGLE);
		} catch (Exception e) {
			GameLog.error("TargetSell", "TargetSellManager[roleGetItem]", "玩家领取物品时出现异常", e);
		}

		return respMsg.build().toByteString();
	}

	
	/**
	 * 将BenefitItems 转换为List<ItemInfo>
	 * 
	 * @param item
	 * @return
	 */
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
	
	
	/**
	 * 通知精准服角色领取目标物品
	 * 
	 * @param player
	 * @param itemGroupID
	 */
	private void notifyBenefitServerRoleGetItem(Player player, int itemGroupID) {
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
	
	/**
	 * 发送数据到精准服
	 * 
	 * @param content
	 */
	private void sendMsg(final String content) {
		if (!ServerSwitch.isOpenTargetSell()) {
			return;
		}
		BenefitMsgController.getInstance().addMsg(content);
	}
}
