package com.playerdata.groupFightOnline.manager;

import java.util.ArrayList;
import java.util.List;

import com.bm.group.GroupBM;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.bm.GFightHelper;
import com.playerdata.groupFightOnline.cfg.GFightBiddingCfg;
import com.playerdata.groupFightOnline.cfg.GFightBiddingCfgDAO;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFBiddingItem;
import com.playerdata.groupFightOnline.data.GFBiddingItemHolder;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.enums.GFResourceState;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.email.EmailCfgDAO;
import com.rwbase.dao.group.pojo.Group;

public class GFBiddingItemMgr {

	private static GFBiddingItemMgr instance = new GFBiddingItemMgr();

	public static GFBiddingItemMgr getInstance() {
		return instance;
	}

	/**
	 * 获取某个资源点所有的压标信息
	 * 
	 * @param resourceID
	 * @return
	 */
	public List<GFBiddingItem> getResourceItemList(int resourceID) {
		return GFBiddingItemHolder.getInstance().getResourceItemList(resourceID);
	}

	/**
	 * 获取某个公会所有的被压标信息 只用在最后奖励结算的时候，因为是遍历获取的
	 * 
	 * @param resourceID
	 * @param groupID
	 * @return
	 */
	public List<GFBiddingItem> getGroupItemList(int resourceID, String groupID) {
		return GFBiddingItemHolder.getInstance().getGroupItemList(resourceID, groupID);
	}

	/**
	 * 个人压标结果的处理 发邮件通知成功或失败，并发放成功奖励
	 * 
	 * @param player
	 * @param resourceID
	 */
	public void handlePersonalBidResult(GFBiddingItem bidItem, boolean isSuccess) {
		GFightBiddingCfg bidCfg = GFightBiddingCfgDAO.getInstance().getCfgById(String.valueOf(bidItem.getRateID()));
		GFightOnlineResourceCfg resCfg = GFightOnlineResourceCfgDAO.getInstance().getCfgById(bidItem.getResourceID());
		String groupName = "";
		Group group = GroupBM.getInstance().get(bidItem.getBidGroup());
		if (null != group) {
			groupName = group.getGroupBaseDataMgr().getGroupData().getGroupName();
		}
		if (isSuccess) {
			// 计算压标最后的奖励
			List<ItemInfo> bidRewardTotal = new ArrayList<ItemInfo>();
			for (ItemInfo baseItem : bidCfg.getBidCost()) {
				ItemInfo item = new ItemInfo();
				item.setItemID(baseItem.getItemID());
				item.setItemNum(baseItem.getItemNum() * bidCfg.getRate());
				bidRewardTotal.add(item);
			}
			String successContent = EmailCfgDAO.getInstance().getCfgById(String.valueOf(bidCfg.getEmailId())).getContent();
			EmailUtils.sendEmail(bidItem.getUserID(), String.valueOf(bidCfg.getEmailId()), GFightHelper.itemListToString(bidRewardTotal), String.format(successContent, groupName, resCfg.getResName(), bidCfg.getCostCount(), bidCfg.getRate()));
		} else {
			String failContent = EmailCfgDAO.getInstance().getCfgById(String.valueOf(bidCfg.getFailEmailID())).getContent();
			EmailUtils.sendEmail(bidItem.getUserID(), String.valueOf(bidCfg.getFailEmailID()), null, String.format(failContent, groupName, resCfg.getResName(), bidCfg.getCostCount()));
		}
	}

	/**
	 * 个人离开帮派或者帮派解散时，删除个人的压标数据，并返还压标资源
	 * 
	 * @param player
	 * @return
	 */
	public boolean removeAllPersonalBid(Player player) {
		GFightBiddingCfgDAO fgBidCfgDao = GFightBiddingCfgDAO.getInstance();
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		for (GFightOnlineResourceCfg resCfg : GFightOnlineResourceCfgDAO.getInstance().getAllCfg()) {
			GFightOnlineResourceData resData = GFightOnlineResourceMgr.getInstance().get(resCfg.getResID());
			if (resData == null || GFResourceState.FIGHT.equals(resData.getState()))
				continue;
			GFBiddingItem item = GFBiddingItemHolder.getInstance().getItem(player, resCfg.getResID());
			if (item != null) {
				GFightBiddingCfg bidCfg = fgBidCfgDao.getCfgById(String.valueOf(item.getRateID()));
				if (null != bidCfg) {
					// for(ItemInfo bidCostItem : bidCfg.getBidCost()){
					// player.getItemBagMgr().addItem(bidCostItem.getItemID(), bidCostItem.getItemNum());
					// GFBiddingItemHolder.getInstance().removeItem(player, resCfg.getResID());
					// }
					itemBagMgr.addItem(player, bidCfg.getBidCost());
					GFBiddingItemHolder.getInstance().removeItem(player, resCfg.getResID());
				}
			}
		}
		return true;
	}
}
