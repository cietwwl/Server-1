package com.playerdata.groupFightOnline.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.group.GroupBM;
import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.bm.GFightHelper;
import com.playerdata.groupFightOnline.cfg.GFightBiddingCfg;
import com.playerdata.groupFightOnline.cfg.GFightBiddingCfgDAO;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFBiddingItem;
import com.playerdata.groupFightOnline.data.GFFinalRewardItem;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupHolder;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.playerdata.groupFightOnline.enums.GFRewardType;
import com.rw.service.Email.EmailUtils;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.email.EmailCfg;
import com.rwbase.dao.email.EmailCfgDAO;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwproto.GroupCommonProto;

public class GFightOnlineGroupMgr {
	
	private static GFightOnlineGroupMgr instance = new GFightOnlineGroupMgr();
	
	public static GFightOnlineGroupMgr getInstance() {
		return instance;
	}

	public GFightOnlineGroupData get(String groupId) {
		if(StringUtils.isBlank(groupId)) return null;
		
		GFightOnlineGroupData groupData = GFightOnlineGroupHolder.getInstance().get(groupId);
		if(groupData == null) {
			initGroupData(groupId);
			groupData =  GFightOnlineGroupHolder.getInstance().get(groupId);
		}
		return groupData;
	}
	
	private void initGroupData(String groupId) {
		GFightOnlineGroupData data = new GFightOnlineGroupData();
		data.setGroupID(groupId);
		GFightOnlineGroupHolder.getInstance().add(data);
	}
	
	public void clearCurrentLoopData(String groupId){
		GFightOnlineGroupData data = get(groupId);
		data.clearCurrentLoopData();
		GFightOnlineGroupHolder.getInstance().update(data);
	}

	public GFightOnlineGroupData getByUser(Player player) {
		String groupID = GroupHelper.getUserGroupId(player.getUserId());
		if(StringUtils.isNotBlank(groupID)){
			return get(groupID);
		}
		return null;
	}
	
	public void update(Player player, GFightOnlineGroupData data, boolean isUpdateBidRank) {
		GFightOnlineGroupHolder.getInstance().update(data);
		
		if(isUpdateBidRank) {
			GFGroupBiddingRankMgr.addOrUpdateGFGroupBidRank(player, data);
		}
	}
	
	public void update(Player player, GFightOnlineGroupData data) {
		update(player, data, false);
	}
	
	/**
	 * 只用来同步所有的帮派信息
	 * 帮派的其它防守队伍，需要用请求
	 * @param player
	 */
	public void synAllData(Player player, int resourceID, int version){
		List<GFGroupBiddingItem> bidList = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		List<String> groupIdList = new ArrayList<String>();
		for(GFGroupBiddingItem item : bidList) {
			GFightOnlineGroupData groupData = get(item.getGroupID());
			if(groupData != null) {				
				groupIdList.add(groupData.getGroupID());
			}
		}
		if(groupIdList.size() > 0){
			GFightOnlineGroupHolder.getInstance().synAllData(player, groupIdList);
		}
	}
	
	/**
	 * 增加防守队伍总数
	 * @param groupId
	 * @param count
	 */
	public synchronized void addDefenderCount(String groupId, int count) {
		GFightOnlineGroupData groupData = get(groupId);
		groupData.addDefenderCount(count);		
		GFightOnlineGroupHolder.getInstance().update(groupData);
	}
	
	/**
	 * 减少存活队伍总数
	 * @param groupId
	 * @param count
	 */
	public synchronized void deductAliveCount(String groupId) {
		GFightOnlineGroupData groupData = get(groupId);
		groupData.deductAliveCount();
		GFightOnlineGroupHolder.getInstance().update(groupData);
	}
	
	/**
	 * 发放帮战胜利成员奖励
	 * @param groupId
	 */
	public void dispatchVictoryReward(String groupId){
		GFightOnlineGroupData groupData = GFightOnlineGroupMgr.getInstance().get(groupId);
		if(groupData == null || groupData.getResourceID() <= 0) return;
		Group group = GroupBM.get(groupId);
		if(null == group) return;
		List<? extends GroupMemberDataIF> groupMem = group.getGroupMemberMgr().getMemberSortList(null);
		String groupName = group.getGroupBaseDataMgr().getGroupData().getGroupName();
		long currentTime = System.currentTimeMillis();
		GFightOnlineResourceCfg resCfg = GFightOnlineResourceCfgDAO.getInstance().getCfgById(String.valueOf(groupData.getResourceID()));
		for(GroupMemberDataIF member : groupMem){
			//构造奖励内容
			GFFinalRewardItem finalRewardItem = new GFFinalRewardItem();
			finalRewardItem.setEmailId(resCfg.getVictoryEmailID());
			
			EmailCfg emailCfg = EmailCfgDAO.getInstance().getCfgById(String.valueOf(resCfg.getVictoryEmailID()));
			if(emailCfg != null){
				finalRewardItem.setRewardDesc(String.format(emailCfg.getContent(), groupName, resCfg.getResName()));
				finalRewardItem.setEmailIconPath(emailCfg.getSubjectIcon());
			}
			
			finalRewardItem.setResourceID(groupData.getResourceID());
			if(member.getPost() == GroupCommonProto.GroupPost.LEADER.getNumber()){
				finalRewardItem.setRewardContent(resCfg.getVictoryLeaderRewardItems());
			}else{
				finalRewardItem.setRewardContent(resCfg.getVictoryMemRewardItems());
			}
			finalRewardItem.setRewardGetTime(currentTime);
			finalRewardItem.setRewardID(GFFinalRewardMgr.getInstance().getRewardID(member.getUserId(), groupData.getResourceID(), GFRewardType.GFightSuccessReward));
			finalRewardItem.setRewardOwner(GFFinalRewardMgr.getInstance().getOwnerID(member.getUserId(), groupData.getResourceID()));
			finalRewardItem.setRewardType(GFRewardType.GFightSuccessReward.getValue());
			finalRewardItem.setUserID(member.getUserId());
			
			GFFinalRewardMgr.getInstance().addGFReward(member.getUserId(), groupData.getResourceID(), finalRewardItem);
		}
	}
	
	/**
	 * 发放帮战失败成员奖励
	 * @param groupId
	 */
	public void dispathchFailReward(String groupId){
		GFightOnlineGroupData groupData = GFightOnlineGroupMgr.getInstance().get(groupId);
		if(groupData == null || groupData.getResourceID() <= 0) return;
		Group group = GroupBM.get(groupId);
		if(null == group) return;
		List<? extends GroupMemberDataIF> groupMem = group.getGroupMemberMgr().getMemberSortList(null);
		String groupName = group.getGroupBaseDataMgr().getGroupData().getGroupName();
		long currentTime = System.currentTimeMillis();
		GFightOnlineResourceCfg resCfg = GFightOnlineResourceCfgDAO.getInstance().getCfgById(String.valueOf(groupData.getResourceID()));
		for(GroupMemberDataIF member : groupMem){
			//构造奖励内容
			GFFinalRewardItem finalRewardItem = new GFFinalRewardItem();
			finalRewardItem.setEmailId(resCfg.getFailEmailID());
			
			EmailCfg emailCfg = EmailCfgDAO.getInstance().getCfgById(String.valueOf(resCfg.getFailEmailID()));
			if(emailCfg != null){
				finalRewardItem.setRewardDesc(String.format(emailCfg.getContent(), groupName, resCfg.getResName()));
				finalRewardItem.setEmailIconPath(emailCfg.getSubjectIcon());
			}
			
			finalRewardItem.setResourceID(groupData.getResourceID());
			if(member.getPost() == GroupCommonProto.GroupPost.LEADER.getNumber()){
				finalRewardItem.setRewardContent(resCfg.getFailLeaderRewardItems());
			}else{
				finalRewardItem.setRewardContent(resCfg.getFailMemRewardItems());
			}
			finalRewardItem.setRewardGetTime(currentTime);
			finalRewardItem.setRewardID(GFFinalRewardMgr.getInstance().getRewardID(member.getUserId(), groupData.getResourceID(), GFRewardType.GFihgtFailReward));
			finalRewardItem.setRewardOwner(GFFinalRewardMgr.getInstance().getOwnerID(member.getUserId(), groupData.getResourceID()));
			finalRewardItem.setRewardType(GFRewardType.GFihgtFailReward.getValue());
			finalRewardItem.setUserID(member.getUserId());
			
			GFFinalRewardMgr.getInstance().addGFReward(member.getUserId(), groupData.getResourceID(), finalRewardItem);
		}
	}
	
	/**
	 * 发放帮派被压标奖励
	 * @param groupId
	 */
	public void dispathchBidOnReward(String groupId){
		
		GFightOnlineGroupData groupData = GFightOnlineGroupMgr.getInstance().get(groupId);
		if(groupData == null || groupData.getResourceID() <= 0) return;
		GFightOnlineResourceCfg resCfg = GFightOnlineResourceCfgDAO.getInstance().getCfgById(String.valueOf(groupData.getResourceID()));
		if(resCfg == null) return;
		List<GFBiddingItem> bidItems = GFBiddingItemMgr.getInstance().getResourceItemList(groupData.getResourceID());
		int totalRateOnGroup = 0;
		int ratePersonCount = 0;
		GFightBiddingCfgDAO fgBidCfgDao = GFightBiddingCfgDAO.getInstance();
		//处理个人压标结果
		for(GFBiddingItem item : bidItems){
			if(StringUtils.equals(item.getBidGroup(), groupId)){
				GFightBiddingCfg bidCfg = fgBidCfgDao.getCfgById(String.valueOf(item.getRateID()));
				totalRateOnGroup += bidCfg.getRate();
				ratePersonCount++;
				GFBiddingItemMgr.getInstance().handlePersonalBidResult(item, true);
			}else{
				GFBiddingItemMgr.getInstance().handlePersonalBidResult(item, false);
			}
		}
		
		if(totalRateOnGroup > 0){
			//计算压标数量对应的奖励
			GFightBiddingCfg bidCfg = fgBidCfgDao.getCfgById("1");
			if(totalRateOnGroup > bidCfg.getVictoryMaxRate()) totalRateOnGroup = bidCfg.getVictoryMaxRate();
			List<ItemInfo> victoryBase = bidCfg.getVictoryRewardItem();
			List<ItemInfo> victoryReward = new ArrayList<ItemInfo>();
			for(ItemInfo baseItem : victoryBase){
				ItemInfo item = new ItemInfo();
				item.setItemID(baseItem.getItemID());
				item.setItemNum(baseItem.getItemNum() * totalRateOnGroup);
				victoryReward.add(item);
			}
			Group group = GroupBM.get(groupId);
			if(null == group) return;
			//发放被压标的帮派成员奖励
			List<? extends GroupMemberDataIF> groupMem = group.getGroupMemberMgr().getMemberSortList(null);
			for(GroupMemberDataIF member : groupMem){
				String bidonContent = EmailCfgDAO.getInstance().getCfgById(String.valueOf(bidCfg.getVictoryRewardEmailId())).getContent();
				EmailUtils.sendEmail(member.getUserId(), String.valueOf(bidCfg.getVictoryRewardEmailId()),
						GFightHelper.itemListToString(victoryReward), String.format(bidonContent, resCfg.getResName(), ratePersonCount));
			}
		}
	}
}
