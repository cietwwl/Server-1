package com.playerdata.groupFightOnline.bm;

import org.apache.commons.lang3.StringUtils;

import com.bm.group.GroupBM;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.data.UserGFightOnlineHolder;
import com.playerdata.groupFightOnline.dataForClient.DefendArmySimpleInfo;
import com.playerdata.groupFightOnline.enums.GFArmyState;
import com.playerdata.groupFightOnline.enums.GFResourceState;
import com.playerdata.groupFightOnline.manager.GFightOnlineResourceMgr;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupFunctionCfg;
import com.rwbase.dao.group.pojo.cfg.dao.GroupFunctionCfgDAO;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwproto.GrouFightOnlineProto.GFResultType;


class GFightConditionJudge {
	private static class InstanceHolder{
		private static GFightConditionJudge instance = new GFightConditionJudge();
	}
	
	public static GFightConditionJudge getInstance(){
		return InstanceHolder.instance;
	}
	
	private GFightConditionJudge() { }
	
	public GFResultType canBidForGroup(Player player, int resourceID, int bidCount) {
		
		return GFResultType.SUCCESS;
	}
	
	/**
	 * 判断是否有竞标权限
	 * @param player
	 * @return
	 */
	public boolean haveAuthorityToBid(Player player){
		String groupID = GroupHelper.getUserGroupId(player.getUserId());
		if(StringUtils.isBlank(groupID)) return false;
		Group group = GroupBM.get(groupID);
		if(group == null) return false;
		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(player.getUserId(), false);
		GroupFunctionCfg funCfg = GroupFunctionCfgDAO.getDAO().getCfgById(GFightConst.GF_BID_AUTHORITY_ID);
		return funCfg.getPostList().indexOf(String.valueOf(memberData.getPost())) >= 0;
	}
	
	/**
	 * 竞标等级判断
	 * @param player
	 * @return
	 */
	public boolean isLevelEnoughForBid(Player player){
		String groupID = GroupHelper.getUserGroupId(player.getUserId());
		if(StringUtils.isBlank(groupID)) return false;
		Group group = GroupBM.get(groupID);
		if(group == null) return false;
		GroupFunctionCfg funCfg = GroupFunctionCfgDAO.getDAO().getCfgById(GFightConst.GF_BID_AUTHORITY_ID);
		return group.getGroupBaseDataMgr().getGroupData().getGroupLevel() >= funCfg.getNeedGroupLevel();
	}
	
	/**
	 * 竞标资源判断并扣除
	 * @param player
	 * @param resourceID
	 * @param oriCount
	 * @param newCount
	 * @return
	 */
	public boolean isEnoughGroupToken(Player player, int oriCount, int newCount){
		String groupID = GroupHelper.getUserGroupId(player.getUserId());
		if(StringUtils.isBlank(groupID)) return false;
		Group group = GroupBM.get(groupID);
		if(group == null) return false;
		// 当前令牌数
		int currentToken = group.getGroupBaseDataMgr().getGroupData().getToken();
		if(newCount - oriCount > currentToken) return false;
		// 扣除令牌数
		group.getGroupBaseDataMgr().updateGroupDonate(player, null, 0, 0, oriCount - newCount, true);
		return true;
	}
	
	public boolean isBidPeriod(int resourceID) {
		GFightOnlineResourceData resData = GFightOnlineResourceMgr.getInstance().get(resourceID);
		return GFResourceState.BIDDING.equals(resData.getState());
	}
	
	public boolean isPreparePeriod(int resourceID) {
		GFightOnlineResourceData resData = GFightOnlineResourceMgr.getInstance().get(resourceID);
		return GFResourceState.PREPARE.equals(resData.getState());
	}
	
	public boolean isFightPeriod(int resourceID) {
		GFightOnlineResourceData resData = GFightOnlineResourceMgr.getInstance().get(resourceID);
		if(resData == null) return false;
		return GFResourceState.FIGHT.equals(resData.getState());
	}
	
	public boolean isRestPeriod(int resourceID) {
		GFightOnlineResourceData resData = GFightOnlineResourceMgr.getInstance().get(resourceID);
		return GFResourceState.REST.equals(resData.getState());
	}
	
	public boolean isLegalBidCount(int resourceID, int oriCount, int newCount) {
		GFightOnlineResourceCfg cfg = GFightOnlineResourceCfgDAO.getInstance().getCfgById(String.valueOf(resourceID));
		if(oriCount == 0 && newCount < cfg.getBiddingBaseCost()) return false;	//小于起始值
		if(newCount - oriCount < cfg.getBiddingAddCost()) return false;		//小于最小增长值
		return true;
	}
	
	/**
	 * 判断个人是否有选中的对手
	 * @param userID
	 * @return
	 */
	public boolean haveSelectedEnimy(String userID){
		UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(userID);
		if(userGFData == null) return false;
		DefendArmySimpleInfo defenderSimple = userGFData.getRandomDefender();
		if(defenderSimple == null) return false;
		return true;
	}
	
//	/**
//	 * 锁定的时间是否过期
//	 * @param defenderSimple
//	 * @return
//	 */
//	public boolean isLockExpired(DefendArmySimpleInfo defenderSimple){
//		GFDefendArmyItem defender = GFDefendArmyMgr.getInstance().getItem(defenderSimple.getGroupID(), defenderSimple.getDefendArmyID());
//		if(defender == null) return true;
//		if(System.currentTimeMillis() - defenderSimple.getLockArmyTime() > GFightConst.LOCK_ITEM_MAX_TIME)
//			return false;
//		return false;
//	}
	
	/**
	 * 锁定的时间是否过期（选择锁定）
	 * @param defenderSimple
	 * @return
	 */
	public boolean isLockExpired(DefendArmySimpleInfo defenderSimple){
		if(!GFArmyState.SELECTED.equals(defenderSimple.getState())) return true;
		if(System.currentTimeMillis() - defenderSimple.getLockArmyTime() > GFightConst.LOCK_ITEM_MAX_TIME)
			return true;
		return false;
	}
	
	
	/**
	 * 锁定的时间是否过期（战斗锁定）
	 * @param defenderSimple
	 * @return
	 */
	public boolean isFightExpired(DefendArmySimpleInfo defenderSimple){
		if(!GFArmyState.FIGHTING.equals(defenderSimple.getState())) return true;
		if(System.currentTimeMillis() - defenderSimple.getLockArmyTime() > GFightConst.FIGHT_LOCK_ITEM_MAX_TIME)
			return true;
		return false;
	}
}
