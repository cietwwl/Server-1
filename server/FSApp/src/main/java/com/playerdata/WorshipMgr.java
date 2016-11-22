package com.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bm.rank.RankType;
import com.common.Utils;
import com.google.protobuf.ByteString;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;
import com.rw.netty.UserChannelMgr;
import com.rw.service.Email.EmailUtils;
import com.rwbase.common.enu.ECareer;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.worship.CfgWorshipRewardHelper;
import com.rwbase.dao.worship.TableWorship;
import com.rwbase.dao.worship.TableWorshipDAO;
import com.rwbase.dao.worship.WorshipUtils;
import com.rwbase.dao.worship.pojo.CfgWorshipReward;
import com.rwbase.dao.worship.pojo.WorshipItem;
import com.rwbase.dao.worship.pojo.WorshipItemData;
import com.rwproto.MsgDef.Command;
import com.rwproto.WorshipServiceProtos.EWorshipRequestType;
import com.rwproto.WorshipServiceProtos.EWorshipResultType;
import com.rwproto.WorshipServiceProtos.WorshipInfo;
import com.rwproto.WorshipServiceProtos.WorshipResponse;

public class WorshipMgr {
	public static final int MAX_RECORD_COUNT = 50;//数据库保存记录的上限
	private static WorshipMgr _instance = new WorshipMgr();
	
	public static WorshipMgr getInstance(){
		return _instance;
	}
	
	private TableWorshipDAO worshipDao = TableWorshipDAO.getInstance();
	

	
	/**重新排行，第一名排行数据改变*/
	public synchronized void changeFirstRanking(RankType rankingType){
		ECareer career;
		switch(rankingType){
			case WARRIOR_ARENA_DAILY:
				career = ECareer.Warrior;
				break;
			case SWORDMAN_ARENA_DAILY:
				career = ECareer.SwordsMan;
				break;
			case MAGICAN_ARENA_DAILY:
				career = ECareer.Magican;
				break;
			case PRIEST_ARENA_DAILY:
				career = ECareer.Priest;
				break;
			default:
				career = ECareer.None;
				break;
		}
		if(career == ECareer.None){
			return;
		}
		
		TableWorship tableWorship = worshipDao.get(String.valueOf(career.getValue()));
		sendWorshipReward(career);
		tableWorship.clear();
		worshipDao.update(tableWorship);
//		PlayerMgr.getInstance().sendPlayerAll(Command.MSG_Worship, getByWorshipedInfo());
		UserChannelMgr.broadcastMsgForMainMsg(Command.MSG_Worship,"TopChanged", getByWorshipedInfo());
	}
	
	/**重排排行榜时发送膜拜奖励*/
	public void sendWorshipReward(ECareer career){
		WorshipInfo info;
		info = WorshipUtils.rankInfoToWorshipInfo(RankingMgr.getInstance().getFirstRankingData(career));
		if(info == null){
			return;
		}
		int size = getWorshipList(career).size();
		CfgWorshipReward cfg = CfgWorshipRewardHelper.getInstance().getByWorshipRewardCfgByCount(size);
		if(cfg == null){
			return;
		}
		String reward = cfg.getRewardStr();
		EmailData emailData = EmailUtils.createEmailData("10019", reward, new ArrayList<String>());
		String content = String.format(emailData.getContent(), size);
		emailData.setContent(content);
		EmailUtils.sendEmail(info.getUserId(), emailData);
	}
	

	/**是否可以膜拜*/
	public boolean isWorship(Player player){
		return isWorship(player.getUserGameDataMgr().getLastWorshipTime());
	}
	
	/**是否可以膜拜*/
	public boolean isWorship(long lastWorshipTime){
		return DateUtils.isResetTime(5, 0, 0, lastWorshipTime);
	}
	
	/**添加膜拜者*/
	public synchronized void addWorshippers(ECareer career, Player player, WorshipItemData rewardData){
		if(career == null || career == ECareer.None || player == null){
			return;
		}
		TableWorship tableWorship = worshipDao.get(String.valueOf(career.getValue()));
		List<WorshipItem> list = tableWorship.getWorshipItemList();
		if(list.size() >= MAX_RECORD_COUNT){
			Collections.sort(list, WorshipUtils.comparator);
			tableWorship.remove(list.get(list.size() - 1));
		}
		tableWorship.add(WorshipUtils.playerInfoToWorshipItem(player, rewardData));
		worshipDao.update(tableWorship);
	}
	
	/**获取膜拜者列表*/
	public List<WorshipInfo> getWorshipList(ECareer career){
		if(career == null || career == ECareer.None){
			return new ArrayList<WorshipInfo>();
		}
		TableWorship tableWorship = worshipDao.get(String.valueOf(career.getValue()));
		if(tableWorship == null){
			return new ArrayList<WorshipInfo>();
		}
		return WorshipUtils.toWorshipList(tableWorship.getWorshipItemList());
	}
	
	/**领取膜拜奖励*/
	public int getWorshipReward(Player player, ECareer career){
		int result = 0;
		if(career == null || career == ECareer.None){
			return result;
		}
		RankingLevelData levelData = RankingMgr.getInstance().getFirstRankingData(career);
		if(levelData == null || levelData.getJob() != career.getValue()){
			return 0;
		}
		TableWorship tableWorship = worshipDao.get(String.valueOf(career.getValue()));
		if(tableWorship == null){
			return result;
		}
		for(WorshipItem item : tableWorship.getWorshipItemList()){
			if(item.isCanReceive()){
				item.setCanReceive(false);
				result++;
			}			
		}
		worshipDao.update(tableWorship);
		return result;
	}
	
	/**推送被膜拜者*/
	public void pushByWorshiped(Player player){
		if(player != null){
			player.SendMsg(Command.MSG_Worship, getWorshipState(player));
		}
	}
	
	private ByteString getWorshipState(Player player) {
		
		List<WorshipInfo> list = null;
		if(DateUtils.dayChanged(GameManager.getOpenTime())){
			list = getByWorshipedList();
		}else{
			list = new ArrayList<WorshipInfo>();
		}
		WorshipResponse.Builder response = WorshipResponse.newBuilder();
		response.setRequestType(EWorshipRequestType.WORSHIP_STATE);
		response.setResultType(EWorshipResultType.SUCCESS);
		response.setCanWorship(isWorship(player));
		response.addAllByWorshippedList(list);
		return response.build().toByteString();
	}

	public ByteString getByWorshipedInfo(){
		List<WorshipInfo> list = getByWorshipedList();
		WorshipResponse.Builder response = WorshipResponse.newBuilder();
		response.setRequestType(EWorshipRequestType.BY_WORSHIPPED_LIST);
		response.setResultType(EWorshipResultType.SUCCESS);
		response.addAllByWorshippedList(list);
		return response.build().toByteString();
	}
	
	
	public List<WorshipInfo> getByWorshipedList(){
		List<WorshipInfo> list = new ArrayList<WorshipInfo>();
		ECareer[] careerList = ECareer.values();
		RankingMgr helper = RankingMgr.getInstance();
		for (ECareer eCareer : careerList) {
			WorshipInfo info = WorshipUtils.rankInfoToWorshipInfo(helper.getFirstRankingData(eCareer));
			if(info != null){
				list.add(info);
			}
		}
		return list;
	}

	
}
