package com.groupCopy.bm.groupCopy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.groupCopy.playerdata.group.UserGroupCopyMapRecordMgr;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyProgress;
import com.groupCopy.rwbase.dao.groupCopy.db.UserGroupCopyMapRecord;
import com.rwproto.GroupCopyBattleProto.CopyMonsterStruct;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo.Builder;
import com.rwproto.GroupCopyBattleProto.CopyRewardStruct;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.service.dropitem.DropItemManager;
import com.rwbase.dao.copy.pojo.ItemInfo;

/**
 * 帮派副本关卡相关操作
 * @author Alex
 * 2016年6月1日 上午10:25:42
 */
public class GroupCopyLevelBL {

	final private static long MAX_FIGHT_SPAN = 2*60*1000;  //战斗最多持续时间，超过了认为断线，重置关卡状态。
	
	private final static int MAX_FIGHT_COUNT = 2;//每天章节最大挑战次数
	public final static int MAX_ALLOT_COUNT = 2;//每天分配的最大次数
	
	public static GroupCopyResult beginFight(Player player, GroupCopyLevelRecordHolder groupCopyLevelRecordHolder,String level) {
		GroupCopyResult result = GroupCopyResult.newResult();
		GroupCopyLevelRecord groupRecord = groupCopyLevelRecordHolder.getByLevel(level);	
		if(isFighting(groupRecord)){
			result.setSuccess(false);
			Player fighter = PlayerMgr.getInstance().find(groupRecord.getFighterId());
			StringBuilder reason = new StringBuilder("关卡挑战中，挑战者：");
			if(fighter!=null){
				reason.append(fighter.getUserId());
			}
			result.setTipMsg(reason.toString());
		}else{
			
			//检查副本当天挑战次数
			GroupCopyLevelCfg levelCfg = GroupCopyLevelCfgDao.getInstance().getConfig(level);
			if(levelCfg == null){
				result.setSuccess(false);
				result.setTipMsg("无法找到关卡对应的章节");
			}else{
				
				UserGroupCopyMapRecordMgr userRecordMgr = player.getUserGroupCopyRecordMgr();
				UserGroupCopyMapRecord userRecord = userRecordMgr.getByLevel(levelCfg.getChaterID());
				
				if(userRecord.getFightCount() >= MAX_FIGHT_COUNT){
					result.setSuccess(false);
					result.setTipMsg("此章节挑战次数已满！");
				}else{
					groupRecord.setFighterId(player.getUserId());
					groupRecord.setFighting(true);
					groupRecord.setLastBeginFightTime(System.currentTimeMillis());
					
					userRecord.incrFightCount();
					
					boolean success = groupCopyLevelRecordHolder.updateItem(player, groupRecord);
					if(success){
						success = userRecordMgr.updateItem(player, userRecord);
					}
					result.setSuccess(true);
					
				}
				
				
			}
			
			
		}
		
		return result;
	}
	
	private static boolean isFighting(GroupCopyLevelRecord groupRecord){
		return groupRecord.isFighting() && System.currentTimeMillis() < groupRecord.getLastBeginFightTime()+MAX_FIGHT_SPAN;
				
	}
	
	/**
	 * 战斗退出 
	 * @param player
	 * @param recordHolder
	 * @param level
	 * @param mData  客户端返回的怪物数据
	 * @return
	 */
	public static GroupCopyResult endFight(Player player, 
			GroupCopyLevelRecordHolder recordHolder,String level,
			List<CopyMonsterStruct> mData) {
		
		GroupCopyResult result = GroupCopyResult.newResult();
		StringBuilder reason = new StringBuilder();
		
		GroupCopyLevelRecord copyLvRecd = recordHolder.getByLevel(level);	
		
		if(!StringUtils.equals(copyLvRecd.getFighterId(), player.getUserId())){
			result.setSuccess(false);
			reason.append("关卡挑战中，挑战者：");
			Player fighter = PlayerMgr.getInstance().find(copyLvRecd.getFighterId());
			if(fighter!=null){
				reason.append(fighter.getUserId());
			}
			result.setTipMsg(reason.toString());
		}else if(!isFighting(copyLvRecd)){
			result.setSuccess(false);
			reason.append("战斗已超时失效。");
			Player fighter = PlayerMgr.getInstance().find(copyLvRecd.getFighterId());
			if(fighter!=null){
				reason.append(fighter.getUserId());
			}
			result.setTipMsg(reason.toString());
		}else{
			
			
			UserGroupCopyMapRecordMgr userRecordMgr = player.getUserGroupCopyRecordMgr();
			UserGroupCopyMapRecord userRecord = userRecordMgr.getByLevel(level);
			
			
			GroupCopyProgress nowPro = new GroupCopyProgress(mData);
			
			
			
			
			//获取发送奖励
			Builder rewardInfo = calculateAndSendReward(copyLvRecd.getProgress().getProgress(), nowPro.getProgress(),
					level, player);
			
			result.setItem(rewardInfo);
			
			copyLvRecd.setProgress(nowPro);
			
			userRecord.incrFightCount();
			
			copyLvRecd.setFighterId("");
			copyLvRecd.setFighting(false);
			
			boolean success = recordHolder.updateItem(player, copyLvRecd);
			if(success){
				success = userRecordMgr.updateItem(player,userRecord);
			}
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * 计算并发送奖励
	 * @param befPro
	 * @param nowPro
	 * @param level
	 * @param player
	 */
	private static Builder calculateAndSendReward(double befPro, double nowPro,
			String level, Player player) {
		if(befPro >= nowPro ){
			//没有任何伤害不发奖励
			return null;
		}
		
		Builder rewardInfo = CopyRewardInfo.newBuilder();
		
		GroupCopyLevelCfg lvCfg = GroupCopyLevelCfgDao.getInstance().getCfgById(level);
		Map<Integer, String> dropMap = lvCfg.getDropMap();
		List<Integer> groupReward = new ArrayList<Integer>();
		
		
		for (Iterator<Entry<Integer, String>> itr = dropMap.entrySet().iterator(); itr.hasNext();) {
			Entry<Integer,String> type = itr.next();
			if(befPro <type.getKey() && type.getKey() < nowPro){
				groupReward.add(Integer.parseInt(type.getValue()));
			}
			
		}
		//获取帮派奖励列表
		try {
			List<ItemInfo> list = DropItemManager.getInstance().pretreatDrop(player, groupReward, 0, false);
			
			for (ItemInfo i : list) {
				CopyRewardStruct.Builder newBuilder = CopyRewardStruct.newBuilder();
				newBuilder.setCount(i.getItemNum());
				newBuilder.setItemID(i.getItemID());
				rewardInfo.addDrop(newBuilder);
			}
			
			
			
		} catch (DataAccessTimeoutException e1) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyLevelBL[calculateAndSendReward]", 
					"角色帮派副本战斗结束获取帮派奖励列表时异常", e1);
		}
		
		
		//发送个人奖励
		try {
			DropItemManager.getInstance().pretreatDrop(player, lvCfg.getRoleRewardList(), Integer.parseInt(level), false);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (DataAccessTimeoutException e) {
			e.printStackTrace();
		}
		
		
		
		return rewardInfo;
		
	}
	
	

	


	
	

}
