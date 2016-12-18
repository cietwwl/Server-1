package com.playerdata.groupFightOnline.bm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.group.GroupBM;
import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.cfg.GFightBiddingCfg;
import com.playerdata.groupFightOnline.cfg.GFightBiddingCfgDAO;
import com.playerdata.groupFightOnline.data.GFBiddingItem;
import com.playerdata.groupFightOnline.data.GFBiddingItemHolder;
import com.playerdata.groupFightOnline.data.GFDefendArmyItem;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.version.GFightDataVersion;
import com.playerdata.groupFightOnline.dataException.GFArmyDataException;
import com.playerdata.groupFightOnline.dataForClient.DefendArmyHerosInfo;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.playerdata.groupFightOnline.enums.GFArmyState;
import com.playerdata.groupFightOnline.manager.GFDefendArmyMgr;
import com.playerdata.groupFightOnline.manager.GFightOnlineGroupMgr;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.group.pojo.Group;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

/**
 * 在线帮战，准备阶段管理类
 * 
 * @author aken
 *
 */
public class GFightPrepareBM {

	private static class InstanceHolder {
		private static GFightPrepareBM instance = new GFightPrepareBM();
	}

	public static GFightPrepareBM getInstance() {
		return InstanceHolder.instance;
	}

	/**
	 * 备战阶段开始时，要处理的事件
	 * 
	 * @param resourceID
	 */
	public void prepareStart(int resourceID) {
		// 归还没进前四帮派的令牌数
		giveBackToken(resourceID);
	}

	// /**
	// * 查看某个帮派所有防守队伍的简要信息
	// * @param player
	// * @param gfRsp
	// * @param groupID
	// * @param version
	// */
	// public void getDefenderTeams(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID, int version) {
	// List<GFDefendArmyItem> defenders = GFDefendArmyMgr.getInstance().getGroupItemList(player, groupID, version);
	// for(GFDefendArmyItem defender : defenders) {
	// GFDefendArmySimpleLeader leader = defender.getSimpleLeader();
	// if(leader == null) continue;
	// gfRsp.addDefendArmySimpleLeader(ClientDataSynMgr.toClientData(leader));
	// }
	// gfRsp.setRstType(GFResultType.SUCCESS);
	// }

	/**
	 * 备战阶段个人的压标
	 * 
	 * @param player
	 * @param gfRsp
	 * @param resourceID
	 * @param groupID
	 * @param rateID
	 */
	public void personalBidForGroup(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID, String groupID, int rateID) {
		if (!GFightConditionJudge.getInstance().isPreparePeriod(resourceID)) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在备战阶段，不能进行此项操作");
			return;
		}
		GFightBiddingCfg bidCgf = GFightBiddingCfgDAO.getInstance().getCfgById(String.valueOf(rateID));
		if (bidCgf == null) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("压标类型数据有误");
			return;
		}
		String selfGroupID = GroupHelper.getUserGroupId(player.getUserId());
		if (StringUtils.isNotBlank(selfGroupID)) {
			int selfGroupRank = GFGroupBiddingRankMgr.getRankIndex(resourceID, selfGroupID);
			if (selfGroupRank >= 1 && selfGroupRank <= GFightConst.IN_FIGHT_MAX_GROUP) {
				gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
				gfRsp.setTipMsg("参与备战的帮派成员不能参与压标");
				return;
			}
		}
		int targetRank = GFGroupBiddingRankMgr.getRankIndex(resourceID, groupID);
		if (targetRank < 1 || targetRank > GFightConst.IN_FIGHT_MAX_GROUP) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("选择压标的帮派并没有参与该资源点的备战");
			return;
		}
		if (player.getVip() < bidCgf.getVip()) {
			gfRsp.setRstType(GFResultType.BID_VIP_UNREACH);
			gfRsp.setTipMsg("玩家VIP等级不足");
			return;
		}

		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		GFBiddingItem bidItem = GFBiddingItemHolder.getInstance().getItem(player, resourceID);
		if (bidItem != null) {
			// 已经压过标
			if (!StringUtils.equals(bidItem.getBidGroup(), groupID)) {
				gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
				gfRsp.setTipMsg("最多压标一个帮派");
				return;
			} else if (bidItem.getRateID() >= rateID) {
				gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
				gfRsp.setTipMsg("不能降低压标倍率");
				return;
			}
			// 计算两次压标所需要的资源差
			List<ItemInfo> hadCost = GFightBiddingCfgDAO.getInstance().getCfgById(String.valueOf(bidItem.getRateID())).getBidCost();
			List<ItemInfo> cost = getDistanceBetweenTwo(bidCgf.getBidCost(), hadCost);
			if (cost == null) {
				gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
				gfRsp.setTipMsg("压标资源消耗数据有误");
				return;
			}
			// 扣除压标资源
			for (ItemInfo item : cost) {
				// TODO 只处理了压标资源是金币的情况，如果添加其它资源，需要额外处理
				if (item.getItemID() == eSpecialItemId.Coin.getValue() && !player.getUserGameDataMgr().isCoinEnough(-item.getItemNum())) {
					gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
					gfRsp.setTipMsg("压标所需金币不够");
					return;
				}
				itemBagMgr.addItem(player, item.getItemID(), -item.getItemNum());
			}
			bidItem.setRateID(rateID);
			GFBiddingItemHolder.getInstance().updateItem(player, bidItem);
		} else {
			// 还没有压标
			for (ItemInfo item : bidCgf.getBidCost()) {
				// TODO 只处理了压标资源是金币的情况，如果添加其它资源，需要额外处理
				if (item.getItemID() == eSpecialItemId.Coin.getValue() && !player.getUserGameDataMgr().isCoinEnough(-item.getItemNum())) {
					gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
					gfRsp.setTipMsg("压标所需金币不够");
					return;
				}
				itemBagMgr.addItem(player, item.getItemID(), -item.getItemNum());
			}
			bidItem = new GFBiddingItem();
			bidItem.setBiddingID(player.getUserId() + "_" + resourceID);
			bidItem.setBidGroup(groupID);
			bidItem.setResourceID(String.valueOf(resourceID));
			bidItem.setUserID(player.getUserId());
			bidItem.setRateID(rateID);
			GFBiddingItemHolder.getInstance().addItem(player, bidItem);
		}
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	/**
	 * 请求同步有更新的帮派信息
	 * 
	 * @param player
	 * @param gfRsp
	 * @param resourceID
	 * @param dataVersion
	 */
	@Deprecated
	public void synGroupData(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID, GFightDataVersion dataVersion) {
		// GFightOnlineGroupMgr.getInstance().synAllData(player, resourceID, dataVersion.getOnlineGroupData());
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	/**
	 * 查看某个防守的队伍的详情（前端的公会防守页面） 或是查看自己已经锁定的，准备挑战的队伍（前端的进战斗前的页面）
	 * 
	 * @param player
	 * @param gfRsp
	 * @param groupID
	 * @param viewArmyID
	 */
	public void viewDefenderTeam(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID, String viewArmyID) {
		GFDefendArmyItem defendTeam = GFDefendArmyMgr.getInstance().getItem(groupID, viewArmyID);
		if (defendTeam == null || GFArmyState.EMPTY.equals(defendTeam.getState())) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("防守队伍数据不存在或者防守方已撤离");
			return;
		}
		defendTeam.getSimpleArmy().setGroupName(GroupHelper.getGroupName(defendTeam.getUserID()));
		gfRsp.setEnimyDefenderDetails(ClientDataSynMgr.toClientData(defendTeam));
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	/**
	 * 修改自己的防守队伍 修改之后的队伍结果会同步 修改之后的个人所属公会的队伍数量信息，也同步
	 * 
	 * @param player
	 * @param gfRsp
	 * @param items
	 * @param dataVersion
	 */
	public void modifySelfDefender(Player player, GroupFightOnlineRspMsg.Builder gfRsp, List<DefendArmyHerosInfo> items, GFightDataVersion dataVersion) {
		String groupID = GroupHelper.getUserGroupId(player.getUserId());
		if (groupID.isEmpty()) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("没有帮派，不能进行此项操作");
			return;
		}
		GFightOnlineGroupData gfGroupData = GFightOnlineGroupMgr.getInstance().get(groupID);
		if (gfGroupData == null || gfGroupData.getResourceID() == 0) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("帮派数据异常");
			return;
		}
		int resourceID = gfGroupData.getResourceID();
		if (!GFightConditionJudge.getInstance().isPreparePeriod(resourceID)) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在备战阶段，不能进行此项操作");
			return;
		}
		try {
			GFDefendArmyMgr.getInstance().resetItems(player, items);
			// 同步公会数据
			// GFightOnlineGroupMgr.getInstance().synAllData(player, resourceID, dataVersion.getOnlineGroupData());
			gfRsp.setRstType(GFResultType.SUCCESS);
		} catch (GFArmyDataException e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg(e.getMessage());
			GameLog.error(LogModule.GroupFightOnline.getName(), player.getUserId(), String.format("modifySelfDefender，修改个人防守队伍信息时，数据异常"), e);
		}
	}

	/**
	 * 计算两个资源数量的差
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	private List<ItemInfo> getDistanceBetweenTwo(List<ItemInfo> left, List<ItemInfo> right) {
		if (left == null || right == null || left.size() != right.size())
			return null;
		List<ItemInfo> result = new ArrayList<ItemInfo>();
		for (int i = 0; i < left.size(); i++) {
			if (left.get(i).getItemID() != right.get(i).getItemID())
				return null;
			ItemInfo item = new ItemInfo();
			item.setItemID(left.get(i).getItemID());
			item.setItemNum(left.get(i).getItemNum() - right.get(i).getItemNum());
			result.add(item);
		}
		return result;
	}

	/**
	 * 退还没进前四的帮派的令牌数量
	 * 
	 * @param resourceID
	 */
	private void giveBackToken(int resourceID) {
		List<GFGroupBiddingItem> groupBidRank = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		if (groupBidRank.size() <= GFightConst.IN_FIGHT_MAX_GROUP)
			return;
		for (int i = GFightConst.IN_FIGHT_MAX_GROUP; i < groupBidRank.size(); i++) {
			GFGroupBiddingItem gBidItem = groupBidRank.get(i);
			Group group = GroupBM.get(gBidItem.getGroupID());
			if (group == null)
				continue;
			// 加回令牌数
			group.getGroupBaseDataMgr().updateGroupDonate(null, null, 0, 0, gBidItem.getTotalBidding(), false);
		}
	}
}
