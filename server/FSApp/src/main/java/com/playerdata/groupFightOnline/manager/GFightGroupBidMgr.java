package com.playerdata.groupFightOnline.manager;

import java.util.Iterator;
import java.util.List;

import com.bm.group.GroupBM;
import com.bm.group.GroupBaseDataMgr;
import com.bm.group.GroupMemberMgr;
import com.bm.rank.magicsecret.MSScoreRankMgr;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceDAO;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;
import com.playerdata.mgcsecret.cfg.BuffBonusCfg;
import com.playerdata.mgcsecret.cfg.BuffBonusCfgDAO;
import com.playerdata.mgcsecret.data.MSScoreDataItem;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.mgcsecret.data.UserMagicSecretHolder;
import com.playerdata.mgcsecret.manager.MSConditionJudger;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwproto.GrouFightOnlineProto.GFResourceInfo;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineReqMsg;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;
import com.rwproto.GrouFightOnlineProto.GroupSimpleInfo;
import com.rwproto.MagicSecretProto.MagicSecretRspMsg;
import com.rwproto.MagicSecretProto.msResultType;

/**
 * 在线帮战，用户竞标阶段管理类
 * @author aken
 *
 */
public class GFightGroupBidMgr {
	
	private static class InstanceHolder{
		private static GFightGroupBidMgr instance = new GFightGroupBidMgr();
	}
	
	public static GFightGroupBidMgr getInstance(){
		return InstanceHolder.instance;
	}
	
	private GFightGroupBidMgr() { }
	
	public void getResourceInfo(Player player, GroupFightOnlineRspMsg.Builder gfRsp){
		gfRsp.setSystemTime(System.currentTimeMillis());
		List<GFightOnlineResourceCfg> resCfgs = GFightOnlineResourceDAO.getInstance().getAllCfg();
		for(GFightOnlineResourceCfg cfg : resCfgs){
			GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(String.valueOf(cfg.getResID()));
			if(resData == null) continue;
			
			GroupMemberMgr mgr = new GroupMemberMgr(resData.getOwnerGroupID());
			
			GroupBaseDataIF groupData = GroupBM.get(resData.getOwnerGroupID()).getGroupBaseDataMgr().getGroupData();
			GroupSimpleInfo groupInfo = GroupSimpleInfo.newBuilder().setGroupID(1).build();
			GFResourceInfo gfRes = GFResourceInfo.newBuilder()
			gfRes.
			
			gfRsp.addGfResourceClientDataSynMgr.toClientData(resData);
		}
	}
	
	public void groupBidding(Player player, GroupFightOnlineRspMsg.Builder gfRsp){
		
	}
	
	/**
	 * 获取秘境排行
	 * @param player
	 * @param msRsp
	 */
	public void getMSRankData(Player player, MagicSecretRspMsg.Builder msRsp) {
		List<MSScoreDataItem> rankList = MSScoreRankMgr.getMSScoreRankList();
		int size = rankList.size();
		for(int i = 0; i < size; i++){
			msRsp.addMsRankData(JsonUtil.writeValue(rankList.get(i)));
		}
		msRsp.setSelfRank(MSScoreRankMgr.getRankIndex(player.getUserId()));
		msRsp.setRstType(msResultType.SUCCESS);
	}
	
	/**
	 * 用星星交换章节内的buff
	 * @param player
	 * @param chapterID
	 * @param buffID
	 * @return
	 */
	public msResultType exchangeBuff(Player player, String chapterID, String buffID){
		if(!MSConditionJudger.judgeBuffLegal(player, chapterID, buffID)) return msResultType.DATA_ERROR;
		if(!MSConditionJudger.judgeEnoughStar(player, chapterID, buffID)) return msResultType.NOT_ENOUGH_STAR;
		
		// 将buff从可选列表，转移到已选择列表
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		Iterator<Integer> unselectItor = mcInfo.getUnselectedBuff().iterator();
		while(unselectItor.hasNext()){
			int bID= unselectItor.next();
			if(bID == Integer.parseInt(buffID)) unselectItor.remove();
		}
		mcInfo.getSelectedBuff().add(Integer.parseInt(buffID));
		
		// 扣除星星数
		BuffBonusCfg buffCfg = BuffBonusCfgDAO.getInstance().getCfgById(buffID);
		mcInfo.setStarCount(mcInfo.getStarCount() - buffCfg.getCost());

		UserMagicSecretHolder.getInstance().update(player);
		MagicChapterInfoHolder.getInstance().updateItem(player, mcInfo);
		return msResultType.SUCCESS;
	}
}
