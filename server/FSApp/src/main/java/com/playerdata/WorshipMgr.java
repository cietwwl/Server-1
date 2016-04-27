package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.Email.EmailUtils;
import com.rw.service.ranking.ERankingType;
import com.rw.service.worship.WorshipHandler;
import com.rwbase.common.enu.ECareer;
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
	private static WorshipMgr _instance;
	public static WorshipMgr getInstance(){
		if(_instance == null){
			_instance = new WorshipMgr();
		}
		return _instance;
	}
	
	private TableWorshipDAO worshipDao = TableWorshipDAO.getInstance();
	
	private Map<ECareer, String> successEmailMap = new HashMap<ECareer, String>();//得到第一名
	private Map<ECareer, String> failEmailMap = new HashMap<ECareer, String>();//失去第一名
	
	/**重新排行，第一名排行数据改变*/
	public void changeFirstRanking(ERankingType rankingType){
		ECareer career;
		switch(rankingType){
			case WARRIOR_DAY:
				career = ECareer.Warrior;
				break;
			case SWORDMAN_DAY:
				career = ECareer.SwordsMan;
				break;
			case MAGICAN_DAY:
				career = ECareer.Magican;
				break;
			case PRIEST_DAY:
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
		if(tableWorship == null){
			tableWorship = new TableWorship();
			tableWorship.setCareer(career.getValue());
		}
		sendWorshipReward(career);
		tableWorship.setWorshipItemList(new ArrayList<WorshipItem>());
		tableWorship.setWorshippersList(new ArrayList<String>());
		worshipDao.update(tableWorship);
		PlayerMgr.getInstance().sendPlayerAll(Command.MSG_Worship, getByWorshipedInfo());
	}
	
	/**重排排行榜时发送膜拜奖励*/
	public void sendWorshipReward(ECareer career){
		WorshipInfo info;
		info = WorshipUtils.rankInfoToWorshipInfo(RankingMgr.getInstance().getFirstRankingData(career));
		if(info == null){
			return;
		}
		CfgWorshipReward cfg = CfgWorshipRewardHelper.getInstance().getWorshipRewardCfg(WorshipHandler.BY_WORSHIPPERS_KEY);
		if(cfg == null){
			return;
		}
		String reward = "";
		int size = getWorshipList(career).size();
		if(size > 0){
			reward = cfg.getRewardType() + "~" + cfg.getRewardCount() * size;
		}
		
		WorshipItemData rewardData = WorshipUtils.getRandomRewardData(cfg.getRandomScheme());		
		if(rewardData != null){
			reward += "," + rewardData.getItemId() + "~" + rewardData.getCount();			
		}
		EmailUtils.sendEmail(info.getUserId(), "10019", reward);
	}
	
	/**发送成为第一名邮件*/
	public void sendSuccessEmail(ECareer career, String userId){
		if(successEmailMap.size() == 0){
			successEmailMap.put(ECareer.Warrior, "10011");
			successEmailMap.put(ECareer.SwordsMan, "10012");
			successEmailMap.put(ECareer.Magican, "10013");
			successEmailMap.put(ECareer.Priest, "10014");
		}
		EmailUtils.sendEmail(userId, successEmailMap.get(career));
	}
	
	/**发送失去第一名邮件*/
	public void sendFailEmail(ECareer career, String userId){
		if(failEmailMap.size() == 0){
			failEmailMap.put(ECareer.Warrior, "10015");
			failEmailMap.put(ECareer.SwordsMan, "10016");
			failEmailMap.put(ECareer.Magican, "10017");
			failEmailMap.put(ECareer.Priest, "10018");
		}
		EmailUtils.sendEmail(userId, failEmailMap.get(career));
	}
	
	/**是否可以膜拜*/
	public boolean isWorship(Player player){
		return DateUtils.isResetTime(5, 0, 0, player.getUserGameDataMgr().getLastWorshipTime());
	}
	
	/**添加膜拜者*/
	public void addWorshippers(ECareer career, Player player, WorshipItemData rewardData){
		if(career == null || career == ECareer.None || player == null){
			return;
		}
		TableWorship tableWorship = worshipDao.get(String.valueOf(career.getValue()));
		if(tableWorship == null){
			tableWorship = new TableWorship();
			tableWorship.setCareer(career.getValue());
		}
		if(tableWorship.getWorshipItemList().size() >= 50){
			return;
		}
		tableWorship.getWorshippersList().add(player.getUserId());
		tableWorship.getWorshipItemList().add(WorshipUtils.playerInfoToWorshipItem(player, rewardData));
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
			System.out.println(player.getUserId() + " getByWorshipedInfo");
			player.SendMsg(Command.MSG_Worship, getByWorshipedInfo());
		}
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
		WorshipInfo info;
		info = WorshipUtils.rankInfoToWorshipInfo(RankingMgr.getInstance().getFirstRankingData(ECareer.Warrior));
		if(info != null){
			list.add(info);
		}
		info = WorshipUtils.rankInfoToWorshipInfo(RankingMgr.getInstance().getFirstRankingData(ECareer.SwordsMan));
		if(info != null){
			list.add(info);
		}
		info = WorshipUtils.rankInfoToWorshipInfo(RankingMgr.getInstance().getFirstRankingData(ECareer.Magican));
		if(info != null){
			list.add(info);
		}
		info = WorshipUtils.rankInfoToWorshipInfo(RankingMgr.getInstance().getFirstRankingData(ECareer.Priest));
		if(info != null){
			list.add(info);
		}
		return list;
	}
}
