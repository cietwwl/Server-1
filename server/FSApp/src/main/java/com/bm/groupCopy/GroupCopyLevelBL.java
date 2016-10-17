package com.bm.groupCopy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
//import com.monster.cfg.CopyMonsterCfg;
//import com.monster.cfg.CopyMonsterCfgDao;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.battleVerify.MonsterCfg;
import com.playerdata.battleVerify.MonsterCfgDao;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.group.UserGroupCopyMapRecordMgr;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.service.dropitem.DropItemManager;
import com.rwbase.dao.battle.pojo.BattleCfgDAO;
import com.rwbase.dao.battle.pojo.cfg.CopyMonsterInfoCfg;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.rwbase.dao.groupCopy.db.GroupCopyMonsterSynStruct;
import com.rwbase.dao.groupCopy.db.GroupCopyProgress;
import com.rwbase.dao.groupCopy.db.UserGroupCopyMapRecord;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo.Builder;
import com.rwproto.GroupCopyBattleProto.CopyRewardStruct;
import com.rwproto.GroupCopyBattleProto.GroupCopyMonsterData;

/**
 * 帮派副本关卡相关操作
 * @author Alex
 * 2016年6月1日 上午10:25:42
 */
public class GroupCopyLevelBL {

	public final static long MAX_FIGHT_SPAN = 2 * 60 * 1000;  //战斗最多持续时间，超过了认为断线，重置关卡状态。
	public final static long MAX_WAIT_SPAN = 1 * 60 * 1000;  //准备最多持续时间，超过了重置关卡状态。
	
	
	public final static int MAX_ALLOT_COUNT = 1;//每天分配的最大次数
	public final static int STATE_COPY_EMPTY = 0;  //副本空闲
	public final static int STATE_COPY_WAIT = 1;  //副本准备进入
	public final static int STATE_COPY_FIGHT = 2; //副本战斗中
	public final static String COPY_WAIT_TIPS = "准备中...";
	public final static String COPY_FIGHT_TIPS = "战斗中...";
	
	/**
	 * 创建关卡进度
	 * @param level
	 * @return
	 */
	public static GroupCopyProgress createProgress(String level){
		List<GroupCopyMonsterSynStruct> mData = new ArrayList<GroupCopyMonsterSynStruct>();
		try {
			CopyMonsterInfoCfg monsterCfg = BattleCfgDAO.getInstance().getCopyMonsterInfoByCopyID(level);
			
			
			GroupCopyMonsterSynStruct struct = null;
			MonsterCfg monster;
			if(monsterCfg == null){
				GameLog.error(LogModule.GroupCopy, "GroupCopyLevelBL[CreateProgress]", "创建关卡进度出现异常,关卡：【" + level + "】里的怪物列表为空！！", null);
				return null;
			}
			
				for (String id : monsterCfg.getEnemyList()) {
					
					monster = MonsterCfgDao.getInstance().getConfig(id);
					if(monster == null){
						GameLog.error(LogModule.GroupCopy, "GroupCopyLevelBL[CreateProgress]", "创建关卡进度出现异常,找不到关卡：【" + level + "】里的怪物["+id+"]！！", null);
						continue;
					}
					struct = new GroupCopyMonsterSynStruct(monster);
					mData.add(struct);
				}
			
			
			
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyLevelBL[CreateProgress]", "创建关卡进度出现异常", e);
		}
		return new GroupCopyProgress(mData);
		
	}
	
	public static GroupCopyResult beginFight(Player player, GroupCopyLevelRecordHolder groupCopyLevelRecordHolder,String level) {
		GroupCopyResult result = GroupCopyResult.newResult();
		result.setTipMsg("操作成功");
		GroupCopyLevelRecord lvRecord = groupCopyLevelRecordHolder.getByLevel(level);	
		if(isFighting(lvRecord, player)){
			result.setSuccess(false);
			Player fighter = PlayerMgr.getInstance().find(lvRecord.getFighterId());
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
				UserGroupCopyMapRecord userRecord = userRecordMgr.getByChaterID(levelCfg.getChaterID());
				if(userRecord == null){
					result.setSuccess(false);
					result.setTipMsg("无法找到关卡对应的章节个人数据");
					return result;
				}
				
				
				if(userRecord.getLeftFightCount() <= 0){//暂时测试改为<  
					result.setSuccess(false);
					result.setTipMsg("此章节挑战次数已满！");
				}else{
					lvRecord.setFighterId(player.getUserId());
					lvRecord.setStatus(STATE_COPY_FIGHT);
					lvRecord.setLastBeginFightTime(System.currentTimeMillis());
					GroupCopyProgress progress = lvRecord.getProgress();
					if(progress == null){
						result.setSuccess(false);
						result.setTipMsg("数据错误!");
						return result;
					}
					
					
					GroupCopyMonsterData.Builder b = GroupCopyMonsterData.newBuilder();

					//将怪物数据转换成json
					List<GroupCopyMonsterSynStruct> getmDatas = progress.getmDatas();
//					StringBuilder sb = new StringBuilder("发送关卡["+level+"]怪物数据：\n");
					for (GroupCopyMonsterSynStruct m : getmDatas) {
						String data = ClientDataSynMgr.toClientData(m);
//						sb.append(data).append("\n");
						b.addMonsterData(data);
					}
//					System.out.println(sb.toString());
					result.setItem(b);
					
					boolean success = groupCopyLevelRecordHolder.updateItem(player, lvRecord);
					if(success){
						success = userRecordMgr.updateItem(player, userRecord);
					}
					result.setSuccess(true);
				}
				
			}
			
		}
		
		return result;
	}
	
	/**
	 * 检查关卡是否在战斗状态
	 * @param groupRecord
	 * @param player TODO
	 * @return
	 */
	public static boolean isFighting(GroupCopyLevelRecord groupRecord, Player player){
		
		if(groupRecord.getStatus() == STATE_COPY_EMPTY){
			return false;
		}
		if(groupRecord.getStatus() == STATE_COPY_FIGHT){
			//检查战斗是否超时
			long curTime = System.currentTimeMillis();
			long endTime = groupRecord.getLastBeginFightTime() + MAX_FIGHT_SPAN;
			if(endTime < curTime){
				return false;
			}
			
		}
		if(groupRecord.getStatus() == STATE_COPY_WAIT){
			if(groupRecord.getFighterId().equals(player.getUserId())){
				//准备和战斗是同一个，允许进入
				return false;
			}else{
				//不是同一个人，检查是否状态超时
				long curTime = System.currentTimeMillis();
				long endTime = groupRecord.getLastBeginFightTime() + MAX_WAIT_SPAN;
				if(endTime > curTime){
					return true;
				}
			}
		}
		return false;
				
	}
	
	public static String getCopyStateTips(int state){
		String tips = null;
		switch (state) {
		case STATE_COPY_FIGHT:
			tips = COPY_FIGHT_TIPS;
			break;
		case STATE_COPY_WAIT:
			tips = COPY_WAIT_TIPS;
			break;
		default:
			break;
		}
		return tips;
	}
	
	/**
	 * 战斗退出 
	 * @param player
	 * @param recordHolder
	 * @param level
	 * @param mData  客户端返回的怪物数据
	 * @param damage TODO
	 * @return
	 */
	public static GroupCopyResult endFight(Player player, 
			GroupCopyLevelRecordHolder recordHolder,String level,
			List<GroupCopyMonsterSynStruct> mData, long damage) {
		
		GroupCopyResult result = GroupCopyResult.newResult();
		StringBuilder reason = new StringBuilder();
		
		GroupCopyLevelRecord copyLvRecd = recordHolder.getByLevel(level);	
		
		if(!StringUtils.equals(copyLvRecd.getFighterId(), player.getUserId()) && !StringUtils.equals(copyLvRecd.getFighterId(), "")){
			result.setSuccess(false);
			reason.append("关卡挑战中，挑战者：");
			Player fighter = PlayerMgr.getInstance().find(copyLvRecd.getFighterId());
			if(fighter!=null){
				reason.append(fighter.getUserId());
			}
			result.setTipMsg(reason.toString());
		
		}else{
			
			//检查一下伤害  怪物有可能会自己加血
//			if(damage < 0){
//				result.setSuccess(false);
//				result.setTipMsg("关卡怪物数据有异常!");
//				GameLog.error(LogModule.GroupCopy, "GroupCopyLevelBL[endFight]", "帮派副本关卡战斗结束，发现前后端怪物数据不同步", null);
//				StringBuilder sb  = new StringBuilder("进入关卡前怪物及对应HP:\n");
//				List<GroupCopyMonsterSynStruct> list = copyLvRecd.getProgress().getmDatas();
//				for (GroupCopyMonsterSynStruct struct : list) {
//					sb.append("怪物id:[").append(struct.getId()).append("]").append(",curHP:[").append(struct.getCurHP()).append("]\n");
//				}
//				System.out.println(sb.toString());
//				sb  = new StringBuilder("战斗结束后怪物及对应HP:\n");
//				for (GroupCopyMonsterSynStruct struct : mData) {
//					sb.append("怪物id:[").append(struct.getId()).append("]").append(",curHP:[").append(struct.getCurHP()).append("]\n");
//				}
//				System.out.println(sb.toString());
//				return result;
//			}
			
			
			UserGroupCopyMapRecordMgr userRecordMgr = player.getUserGroupCopyRecordMgr();
			GroupCopyLevelCfg cfg = GroupCopyLevelCfgDao.getInstance().getCfgById(level);
			UserGroupCopyMapRecord userRecord = userRecordMgr.getByChaterID(cfg.getChaterID());
			if(userRecord == null){
				result.setSuccess(false);
				reason.append("地图id为").append(cfg.getChaterID()).append("找不到id为").append(level).append("的关卡");
				result.setTipMsg(reason.toString());
				return result;
			}
			
			
			GroupCopyProgress nowPro = new GroupCopyProgress(mData);
			
			//获取发送奖励
			Builder rewardInfo = calculateAndSendReward(copyLvRecd.getProgress().getProgress(), nowPro.getProgress(),
					level, player, damage);
			
			
			
			result.setItem(rewardInfo);
			
			//新创建一个对象这样set进去，会不会有问题
			//不可以这样set进去，避免前端只同步回来没有死的怪物数据，没有在上一波已经死的怪物,这样set进去就会造成数据丢失  ----by Alex
//			copyLvRecd.setProgress(nowPro);
			//扣除关卡内怪物的血量
			for (GroupCopyMonsterSynStruct m : mData) {
				copyLvRecd.getProgress().setmData(m);
			}
			copyLvRecd.getProgress().initProgress();//重新计算进度
			
			
			
			userRecord.incrFightCount();
			
			copyLvRecd.setFighterId("");
			copyLvRecd.setStatus(STATE_COPY_EMPTY);
			
			boolean success = recordHolder.updateItem(player, copyLvRecd);
			if(success){
				success = userRecordMgr.updateItem(player,userRecord);
			}
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * 计算奖励
	 * @param befPro
	 * @param nowPro
	 * @param level
	 * @param player
	 * @param damage TODO
	 */
	private static Builder calculateAndSendReward(double befPro, double nowPro,
			String level, Player player, long damage) {
		if(damage == 0 ){
			//没有任何伤害不发奖励
			return null;
		}
		
		Builder rewardInfo = CopyRewardInfo.newBuilder();
		
		GroupCopyLevelCfg lvCfg = GroupCopyLevelCfgDao.getInstance().getCfgById(level);
		Map<Integer, String> dropMap = lvCfg.getDropMap();
		List<Integer> groupReward = new ArrayList<Integer>();
		
		//计算掉落方案
		for (Iterator<Entry<Integer, String>> itr = dropMap.entrySet().iterator(); itr.hasNext();) {
			Entry<Integer,String> type = itr.next();
			if(befPro * 100 <type.getKey() && type.getKey() <= nowPro * 100){
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
		
		
		//计算个人奖励
		List<ItemInfo> list;
		try {
			list = DropItemManager.getInstance().pretreatDrop(player, lvCfg.getRoleRewardList(), 0, false);
			for (ItemInfo i : list) {
				CopyRewardStruct.Builder newBuilder = CopyRewardStruct.newBuilder();
				newBuilder.setCount(i.getItemNum());
				newBuilder.setItemID(i.getItemID());
				rewardInfo.addPersonalReward(newBuilder);
			}
		} catch (DataAccessTimeoutException e) {
			e.printStackTrace();
		}
		
		//个人奖励的 金币=min（伤害*0.05，100000）
		int gold = (int) (damage*0.05 > 100000 ? 100000 : damage*0.05);
		gold = gold > 0 ? gold : 0;
		rewardInfo.setGold(gold);//暂时这样计算
		//检查是否最后一击
		if(nowPro == 1){
			rewardInfo.setFinalHitPrice(lvCfg.getFinalHitReward());
		}
			
		
		
		return rewardInfo;
		
	}

	
	
	

	
	


	
	

}
