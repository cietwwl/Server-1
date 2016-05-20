package com.playerdata.mgcsecret;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.mgcsecret.cfg.BuffBonusCfg;
import com.playerdata.mgcsecret.cfg.BuffBonusCfgDAO;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfg;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfgDAO;
import com.playerdata.mgcsecret.data.MSStageInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.playerdata.mgcsecret.data.UserMagicSecretHolder;
import com.rwproto.MagicSecretProto.MagicSecretRspMsg;
import com.rwproto.MagicSecretProto.msResultType;

// 有积分改变的时候通知排行榜

public class MagicSecretMgr extends MSConditionJudgeMgr{
	// 初始化
	public void init(Player playerP) {
		m_pPlayer = playerP;
		this.userId = playerP.getUserId();
		mChapterHolder = MagicChapterInfoHolder.getInstance();
		userMSHolder = new UserMagicSecretHolder(userId);
	}
	
	public msResultType enterMSFight(String dungeonID){
		if(!judgeUserLevel(dungeonID)) return msResultType.LOW_LEVEL;
		if(!judgeDungeonsCondition(dungeonID)) return msResultType.CONDITION_UNREACH;
		if(!judgeDungeonsLegal(dungeonID)) return msResultType.DATA_ERROR;
		
		//进入副本的时候更新可以选的副本（如果有三个，进入其中一个之后，如果没打过，以后也只有一个选择）
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungeonID);
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, String.valueOf(dungDataCfg.getChapterID()));
		List<MSStageInfo> newStgList = new ArrayList<MSStageInfo>();
		for(MSStageInfo stage : mcInfo.getSelectableStages()){
			if(stage.getStageKey().equalsIgnoreCase(dungeonID))
				newStgList.add(stage);
		}
		mcInfo.setSelectableStages(newStgList);
		
		//设置战斗中的副本(是为了获取奖励的时候，作合法性判断)
		UserMagicSecretData umsData = userMSHolder.get();
		if(umsData.getCurrentDungeonID() != null)
			GameLog.error(LogModule.MagicSecret, userId, String.format("enterMSFight, 进入副本[%s]时，仍有一个战斗dungeonID[%s]没解除", umsData.getCurrentDungeonID()), null);
		umsData.setCurrentDungeonID(dungeonID);
		
		userMSHolder.update(m_pPlayer);
		mChapterHolder.updateItem(m_pPlayer, mcInfo);
		return msResultType.SUCCESS;
	}

	public msResultType exchangeBuff(String chapterID, String buffID){
		if(!judgeBuffLegal(chapterID, buffID)) return msResultType.DATA_ERROR;
		if(!judgeEnoughStar(chapterID, buffID)) return msResultType.NOT_ENOUGH_STAR;
		
		// 将buff从可选列表，转移到已选择列表
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		Iterator<Integer> unselectItor = mcInfo.getUnselectedBuff().iterator();
		while(unselectItor.hasNext()){
			int bID= unselectItor.next();
			if(bID == Integer.parseInt(buffID)) unselectItor.remove();
		}
		mcInfo.getUnselectedBuff().add(Integer.parseInt(buffID));
		
		// 扣除星星数
		BuffBonusCfg buffCfg = BuffBonusCfgDAO.getInstance().getCfgById(buffID);
		mcInfo.setStarCount(mcInfo.getStarCount() - buffCfg.getCost());
		
		userMSHolder.update(m_pPlayer);
		mChapterHolder.updateItem(m_pPlayer, mcInfo);
		return msResultType.SUCCESS;
	}
	
	public void openRewardBox(MagicSecretRspMsg.Builder msRsp) {
		if(!judgeRewardBoxLegal()){
			msRsp.setRstType(msResultType.NO_REWARD_BOX);
		}
	}

	public void getMSSweepReward(MagicSecretRspMsg.Builder msRsp) {
		
	}
	
	public void getSingleReward(MagicSecretRspMsg.Builder msRsp) {
		
	}

	public void getMSRankData(MagicSecretRspMsg.Builder msRsp) {
		
	}
}