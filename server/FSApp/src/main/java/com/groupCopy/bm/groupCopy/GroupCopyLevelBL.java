package com.groupCopy.bm.groupCopy;

import org.apache.commons.lang3.StringUtils;

import com.groupCopy.playerdata.group.UserGroupCopyLevelRecordMgr;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.UserGroupCopyLevelRecord;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;

public class GroupCopyLevelBL {

	final private static long MAX_FIGHT_SPAN = 2*60*1000;  //战斗最多持续时间，超过了认为断线，重置关卡状态。
	
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
			UserGroupCopyLevelRecordMgr userRecordMgr = player.getUserGroupCopyLevelRecordMgr();
			UserGroupCopyLevelRecord userRecord = userRecordMgr.getByLevel(level);
			
			groupRecord.setFighterId(player.getUserId());
			groupRecord.setFighting(true);
			groupRecord.setLastBeginFightTime(System.currentTimeMillis());
			
			userRecord.incrFightCount();
			
			boolean success = groupCopyLevelRecordHolder.updateItem(player, groupRecord);
			if(success){
				success = userRecordMgr.updateItem(userRecord);
			}
			result.setSuccess(true);
			
		}
		
		return result;
	}
	
	private static boolean isFighting(GroupCopyLevelRecord groupRecord){
		return groupRecord.isFighting() && System.currentTimeMillis() < groupRecord.getLastBeginFightTime()+MAX_FIGHT_SPAN;
				
	}
	
	public static GroupCopyResult endFight(Player player, GroupCopyLevelRecordHolder groupCopyLevelRecordHolder,String level) {
		
		GroupCopyResult result = GroupCopyResult.newResult();
		StringBuilder reason = new StringBuilder();
		
		GroupCopyLevelRecord groupRecord = groupCopyLevelRecordHolder.getByLevel(level);	
		
		if(!StringUtils.equals(groupRecord.getFighterId(), player.getUserId())){
			result.setSuccess(false);
			reason.append("关卡挑战中，挑战者：");
			Player fighter = PlayerMgr.getInstance().find(groupRecord.getFighterId());
			if(fighter!=null){
				reason.append(fighter.getUserId());
			}
			result.setTipMsg(reason.toString());
		}else if(!isFighting(groupRecord)){
			result.setSuccess(false);
			reason.append("战斗已超时失效。");
			Player fighter = PlayerMgr.getInstance().find(groupRecord.getFighterId());
			if(fighter!=null){
				reason.append(fighter.getUserId());
			}
			result.setTipMsg(reason.toString());
		}else{
			UserGroupCopyLevelRecordMgr userRecordMgr = player.getUserGroupCopyLevelRecordMgr();
			UserGroupCopyLevelRecord userRecord = userRecordMgr.getByLevel(level);
			
			groupRecord.addProgress(20);
			userRecord.incrFightCount();
			
			boolean success = groupCopyLevelRecordHolder.updateItem(player, groupRecord);
			if(success){
				success = userRecordMgr.updateItem(userRecord);
			}
			result.setSuccess(true);
		}
		return result;
	}

}
