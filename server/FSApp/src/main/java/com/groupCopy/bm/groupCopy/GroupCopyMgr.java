package com.groupCopy.bm.groupCopy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyDonateCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyDonateCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMailCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMailCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.db.ApplyInfo;
import com.groupCopy.rwbase.dao.groupCopy.db.CopyItemDropAndApplyRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.DistRewRecordItem;
import com.groupCopy.rwbase.dao.groupCopy.db.DropAndApplyRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.DropInfo;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyArmyDamageInfo;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyDamegeRankInfo;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyDistIDManager;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMonsterSynStruct;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyRewardDistRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.ItemDropAndApplyTemplate;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyProgress;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyRewardDistRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyTeamInfo;
import com.groupCopy.rwbase.dao.groupCopy.db.ServerGroupCopyDamageRecordMgr;
import com.groupCopy.rwbase.dao.groupCopy.db.TeamHero;
import com.rw.controler.GameLogicTask;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.Email.EmailUtils;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.majorDatas.MajorDataDataHolder;
import com.rwbase.gameworld.GameWorldExecutor;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.GroupCopyBattleProto.CopyBattleRoleStruct;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComRspMsg;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo.Builder;
import com.rwproto.GroupCopyBattleProto.CopyRewardStruct;
import com.rwproto.GroupCopyCmdProto.ArmyHurtStruct;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdReqMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyDonateData;
import com.rwproto.GroupCopyCmdProto.GroupCopyHurtRank;
import com.rwproto.GroupCopyCmdProto.GroupCopyMapStatus;
import com.log.GameLog;
import com.log.LogModule;
import com.monster.cfg.CopyMonsterCfg;
import com.monster.cfg.CopyMonsterCfgDao;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.readonly.PlayerIF;

/**
 * 
 * 
 * @author Allen
 *
 */
public class GroupCopyMgr {

	/**帮派副本关卡数据*/
	private GroupCopyLevelRecordHolder lvRecordHolder;

	/**帮派副本地图数据*/
	private GroupCopyMapRecordHolder mapRecordHolder;

	/**帮派副本奖励分配记录*/
	private GroupCopyRewardDistRecordHolder rewardRecordHolder;
	
	/**帮派副本胜利品及申请列表*/
	private DropAndApplyRecordHolder dropHolder;

	private final String SYSTEM_DIST = "系统自动分配";
	private final String ROLE_DIST = "由%s进行分配";
	
	public final static GroupCopyDamegeRankComparator RANK_COMPARATOR = new GroupCopyDamegeRankComparator();
	public final static DropApplyComparator adComparator = new DropApplyComparator();

	public static final int MAX_RANK_RECORDS = 10;
	
	/**最大赞助上限*/
	private static final int MAX_DONATE_COUNT = 100; 
	
	public GroupCopyMgr(String groupIdP) {
		lvRecordHolder = new GroupCopyLevelRecordHolder(groupIdP);
		mapRecordHolder = new GroupCopyMapRecordHolder(groupIdP);
		rewardRecordHolder = new GroupCopyRewardDistRecordHolder(groupIdP);
		dropHolder = new DropAndApplyRecordHolder(groupIdP);
	}
	
	/**
	 * 系统加载完所有数据后才可以执行此方法
	 */
	public void checkDataFromCfg(){
		lvRecordHolder.checkAndInitData();
		mapRecordHolder.checkAndInitData();
		dropHolder.checkAndInitData();
	}
	
	/**
	 * 开启副本地图
	 * @param mapId
	 * @return
	 */
	public  GroupCopyResult  openMap(Player player, String mapId){
		return GroupCopyMapBL.openOrResetMap(player, mapRecordHolder, lvRecordHolder, mapId);
	}
	
	/**
	 * 重置副本地图
	 * @param player TODO
	 * @param mapId
	 * @return
	 */
	public  GroupCopyResult resetMap(Player player, String mapId){
		GroupCopyResult result =  GroupCopyMapBL.openOrResetMap(player, mapRecordHolder, lvRecordHolder, mapId);
		if(result.isSuccess()){
			//发送重置邮件
			String groupId = GroupHelper.getUserGroupId(player.getUserId());
			Group group = GroupBM.get(groupId);
			GroupCopyMapCfg cfg = GroupCopyMapCfgDao.getInstance().getCfgById(mapId);
			List<? extends GroupMemberDataIF> list = group.getGroupMemberMgr().getMemberSortList(null);
			for (GroupMemberDataIF dataIF : list) {
				GameWorldFactory.getGameWorld().asyncExecute(dataIF.getUserId(), 
						new GroupCopyResetMailTask(dataIF.getUserId(), player.getUserName(), cfg.getName()));
			}
		}
		return result;
	}
	
	/**
	 * 进入副本战斗
	 * @param player
	 * @param levelId
	 * @return
	 */
	public  GroupCopyResult  beginFight(Player player, String levelId){
		return GroupCopyLevelBL.beginFight(player, lvRecordHolder, levelId);
	}
	
	/**
	 * 结束战斗
	 * @param player
	 * @param levelId 关卡id
	 * @param mData 客户端返回的怪物数据
	 * @param heroList TODO
	 * @return
	 */
	public  GroupCopyResult  endFight(Player player, String levelId, 
			List<GroupCopyMonsterSynStruct> mData, List<String> heroList){
		//获取伤害
		int damage = getDamage(mData, levelId);
		GroupCopyResult result = GroupCopyLevelBL.endFight(player, lvRecordHolder, levelId, mData, damage);
		if(result.isSuccess()){
			//同步一下副本地图进度
			GroupCopyMapBL.calculateMapProgress(player, lvRecordHolder, mapRecordHolder,levelId);
			//检查是否进入章节前10伤害排行 
			checkDamageRank(player,levelId, damage, heroList);
			//将奖励入放帮派奖励缓存
			if(result.getItem() != null){
				addReward2Group(player,levelId, (CopyRewardInfo.Builder)result.getItem());
				addReward2Role(player, levelId, (CopyRewardInfo.Builder)result.getItem());
			}
			
			//检查一下章节进度，是否通关
			GroupCopyLevelCfg levelCfg = GroupCopyLevelCfgDao.getInstance().getCfgById(levelId);
			GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(levelCfg.getChaterID());
			
			if(mapRecord.getStatus() == GroupCopyMapStatus.FINISH){
				
				final String groupID = GroupHelper.getUserGroupId(player.getUserId());
				if(groupID.equals("")){
					return result;
				}
				GroupCopyDistIDManager.getInstance().addGroupID(groupID);
				final String roleName = player.getUserName();
				final boolean inExtralTime = mapRecord.getRewardTime() >= System.currentTimeMillis();
				final String chaterID = levelCfg.getChaterID();
				//通知帮派发通关邮件
				GameWorldFactory.getGameWorld().asynExecute(new Runnable() {
					
					@Override
					public void run() {
						GroupCopyMailHelper.getInstance().sendGroupCopyFinishMail(groupID, roleName, inExtralTime, chaterID);
					}
				});
			}
		}
		return result;
	}
	
	
	private void addReward2Role(Player player, String levelId, Builder item) {
		int gold = item.getGold();
		List<CopyRewardStruct> rewardList = item.getPersonalRewardList();
		if(gold > 0){
			player.getUserGameDataMgr().addCoin(gold);
		}
		if(!rewardList.isEmpty()){
			for (CopyRewardStruct struct : rewardList) {
				player.getItemBagMgr().addItem(struct.getItemID(), struct.getCount());
			}
		}
		//检查有没有最后一击奖励
		if(item.getFinalHitPrice() != 0){
			Group group = GroupBM.get(player.getUserGroupAttributeDataMgr().getUserGroupAttributeData().getGroupId());
			group.getGroupMemberMgr().updateMemberContribution(player.getUserId(), item.getFinalHitPrice(), false);
		}
	}

	
	private int getDamage(List<GroupCopyMonsterSynStruct> mData, String level){
		GroupCopyProgress nowPro = new GroupCopyProgress(mData);
		GroupCopyLevelRecord record = lvRecordHolder.getByLevel(level);
		if(record.getProgress().getCurrentHp() == 0){
			return nowPro.getTotalHp() - nowPro.getCurrentHp();
		}
		return record.getProgress().getCurrentHp() - nowPro.getCurrentHp();
	}

	
	/**
	 * 添加帮派奖励
	 * @param player
	 * @param levelId
	 * @param item
	 */
	private void addReward2Group(Player player,
			String levelId, Builder item) {

		GroupCopyLevelCfg cfg = GroupCopyLevelCfgDao.getInstance().getCfgById(levelId);
		
		Group group = com.groupCopy.bm.GroupHelper.getGroup(player);
		group.getGroupBaseDataMgr().updateGroupDonate(player, null, 0, cfg.getGroupExp(), 0, true);
		
		GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(cfg.getChaterID());
		CopyItemDropAndApplyRecord dropAndApplyRecord = dropHolder.getItemByID(cfg.getChaterID());
		ItemDropAndApplyTemplate dropApplyRecord = null;
		List<CopyRewardStruct> list = item.getDropList();
		for (CopyRewardStruct d : list) {
			dropApplyRecord = dropAndApplyRecord.getDropApplyRecord(String.valueOf(d.getItemID()));
			dropApplyRecord.addDropItem(d.getCount());
			dropHolder.updateItem(player, dropAndApplyRecord);
			dropApplyRecord = null;
		}
		
		mapRecordHolder.updateItem(player, mapRecord);
	}


	private void checkDamageRank(Player player, String levelId, int damage, List<String> heroList) {
		try {

			GroupCopyLevelCfg cfg = GroupCopyLevelCfgDao.getInstance().getCfgById(levelId);
			
			ArmyInfo info = ArmyInfoHelper.getArmyInfo(player.getUserId(), heroList);
			GroupCopyArmyDamageInfo damageInfo = armyInfo2DamageInfo(info, player.getUserId());
			damageInfo.setDamage(damage);
			mapRecordHolder.checkDamageRank(cfg.getChaterID(),damageInfo);
			//关卡全服单次伤害排行
			boolean kill = lvRecordHolder.getByLevel(levelId).getProgress().getCurrentHp() == 0;
			ServerGroupCopyDamageRecordMgr.getInstance().checkDamageRank(levelId,damageInfo, player, kill);
			
			//增加成员章节总伤害
			GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(cfg.getChaterID());
			mapRecord.addPlayerDamage(player.getUserName(), damage);
			mapRecordHolder.updateItem(null, mapRecord);
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[checkDamageRank]", "帮派副本战斗结束检查排行榜时出现异常", e);
		}
		
		
	}


	public static GroupCopyArmyDamageInfo armyInfo2DamageInfo(ArmyInfo info, String playerID) {
		GroupCopyArmyDamageInfo damageInfo = new GroupCopyArmyDamageInfo();
		damageInfo.setPlayerID(playerID);
		damageInfo.setTime(System.currentTimeMillis());
		
		GroupCopyTeamInfo teamInfo = new GroupCopyTeamInfo();
		teamInfo.setArmyMagic(info.getArmyMagic());
		String groupName = GroupHelper.getGroupName(damageInfo.getPlayerID());
		teamInfo.setGuildName(groupName);
		
		List<ArmyHero> heroList = info.getHeroList();
		List<TeamHero> heros = new ArrayList<TeamHero>();
		TeamHero tHero;
		for (ArmyHero hero : heroList) {
			tHero = new TeamHero();
			heros.add(initTeamHero(tHero, hero));
		}
		teamInfo.setHeroList(heros);
		tHero = new TeamHero();
		teamInfo.setPlayer(initTeamHero(tHero, info.getPlayer()));
		teamInfo.setPlayerHeadImage(info.getPlayerHeadImage());
		teamInfo.setPlayerName(info.getPlayerName());
		
		damageInfo.setArmy(teamInfo);
		return damageInfo;
	}

	private static TeamHero initTeamHero(TeamHero tHero, ArmyHero hero){
		tHero.setExp(hero.getRoleBaseInfo().getExp());
		tHero.setLevel(hero.getRoleBaseInfo().getLevel());
		tHero.setModeId(hero.getRoleBaseInfo().getModeId());
		tHero.setQualityId(hero.getRoleBaseInfo().getQualityId());
		tHero.setStarLevel(hero.getRoleBaseInfo().getStarLevel());
		tHero.setTemplateId(hero.getRoleBaseInfo().getTemplateId());
		return tHero;
	}
	

	/**
	 * 同步副本地图数据
	 * @param player
	 * @param version
	 */
	public void synMapData(Player player, int version){
		
		mapRecordHolder.synAllData(player, version);
		
	}
	
	/**
	 * 同步副本关卡数据
	 * @param player
	 * @param version
	 */
	public void synLevelData(Player player, int version){
		
		lvRecordHolder.synAllData(player, version);
		
	}
	
	/**
	 * 同步奖励分配记录
	 * @param player
	 * @param version
	 */
	public void synRewardLogData(Player player){
		rewardRecordHolder.synAllData(player);
	}
	
	public void synDropAppyData(Player player, String chaterID){
		dropHolder.synSingleData(player, chaterID);
	}
	
	
	
	/**
	 * 请求是否可以进入关卡
	 * @param player
	 * @param level
	 * @return
	 */
	public GroupCopyBattleComRspMsg.Builder applyEnterCopy(Player player, String level,
			GroupCopyBattleComRspMsg.Builder rspMsg) {
		try {
			GroupCopyLevelRecord lvData = lvRecordHolder.getByLevel(level);
			if(lvData == null){
				GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[applyEnterCopy]", "角色请求进入关卡，找不到关卡id为"+ level
						+ "的记录" , null);
				rspMsg.setTipMsg("服务器繁忙！");
				return rspMsg;
			}
			int status = lvData.getStatus();
			boolean enter = false;
			long curTime = System.currentTimeMillis();

			CopyBattleRoleStruct.Builder roleStruct = null; 
			int leftTime = 0;
			switch (status) {
			case GroupCopyLevelBL.STATE_COPY_EMPTY:
				enter = updateCopyState(player, lvData, GroupCopyLevelBL.STATE_COPY_WAIT);
				break;
			case GroupCopyLevelBL.STATE_COPY_WAIT:
				//检查有没有超时
				leftTime = (int) ((lvData.getLastBeginFightTime() + GroupCopyLevelBL.MAX_WAIT_SPAN - curTime)/1000);
				if(leftTime <= 0 ){
					//已经超时，重置
					enter = updateCopyState(player, lvData, status);
				}else{
					//没有超时，返回关卡内的角色数据
					roleStruct = getRoleStruct(lvData.getFighterId(), leftTime, status);
				}
				break;
			case GroupCopyLevelBL.STATE_COPY_FIGHT:
				//检查有没有超时
				leftTime = (int) ((lvData.getLastBeginFightTime() + GroupCopyLevelBL.MAX_WAIT_SPAN - curTime)/1000);
				if(leftTime <= 0){
					//已经超时，重置
					enter = updateCopyState(player, lvData, GroupCopyLevelBL.STATE_COPY_WAIT);
				}else{
					//没有超时，返回关卡内的角色数据
					roleStruct = getRoleStruct(lvData.getFighterId(), leftTime, status);
				}
				break;
			
			default:
				break;
			}
			
			
			if(roleStruct != null){
				rspMsg.setBattleRole(roleStruct);
			}
			
			rspMsg.setIsSuccess(enter);
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[applyEnterCopy]", "角色请求进入关卡异常", e);
		}
		
		return rspMsg;
	}

	/**
	 * @param roleID
	 * @param leftTimeSec
	 * @param status
	 * @return
	 */
	private CopyBattleRoleStruct.Builder getRoleStruct(String roleID, int leftTimeSec, int status){
		PlayerIF fighter = PlayerMgr.getInstance().getReadOnlyPlayer(roleID);
		
		CopyBattleRoleStruct.Builder roleStruct = CopyBattleRoleStruct.newBuilder();
		roleStruct.setRoleIcon(fighter.getHeadImage());
		roleStruct.setRoleName(fighter.getUserName());
		roleStruct.setState(GroupCopyLevelBL.getCopyStateTips(status));
		roleStruct.setLv(fighter.getLevel());
		roleStruct.setLeftTime(leftTimeSec);
		roleStruct.setRoleID(roleID);
		return roleStruct;
	}
	

	/**
	 * 更新关卡状态
	 * @param player
	 * @param lvData
	 * @param state TODO
	 * @return
	 */
	private boolean updateCopyState(Player player, GroupCopyLevelRecord lvData, int state) {
		try {
			lvData.setFighterId(player.getUserId());
			lvData.setLastBeginFightTime(System.currentTimeMillis());;
			lvData.setStatus(state);
			lvRecordHolder.updateItem(player, lvData);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	
	//处理赞助
	public GroupCopyResult donateBuff(Player player, Group group, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyResult result = GroupCopyResult.newResult();
		GroupCopyDonateData data = reqMsg.getDonateData();
		boolean suc = false;
		result.setTipMsg("操作失败！");
		try {
			//判断一下是否有足够的钻石
			GroupCopyDonateCfg donateCfg = GroupCopyDonateCfgDao.getInstance().getCfgById(String.valueOf(data.getDonateTime()));
			if(donateCfg == null){
				result.setTipMsg("找不到次数为" + data.getDonateTime() + "的配置！");
				result.setSuccess(suc);
				return result;
			}
			
			boolean engough = player.getUserGameDataMgr().isGoldEngough(-donateCfg.getGold());
			if(!engough){
				result.setTipMsg("钻石不足～");
				result.setSuccess(suc);
				return result;
			}

			synchronized (this) {
				GroupCopyLevelRecord record = lvRecordHolder.getByLevel(data.getLevel());
				if(record == null){
					GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[donateBuff]", "角色赞助Buff找不到关卡id为"+ data.getLevel() 
							+ "的记录" , null);
					result.setTipMsg("服务器繁忙！");
					result.setSuccess(suc);
					return result;
				}
				
				if((record.getBuffCount() + donateCfg.getIncreValue()) > MAX_DONATE_COUNT){
					result.setTipMsg("超过赞助上限！");
					result.setSuccess(suc);
					return result;
				}
				record.addRoleDonate(player.getUserName(), donateCfg.getGold());
				record.addBuff(donateCfg.getIncreValue());
				//扣钱  加帮贡
				int code = player.getUserGameDataMgr().addGold(-donateCfg.getGold());
						
				if(code == 0){
					player.getUserGroupAttributeDataMgr().useUserGroupContribution(donateCfg.getContribution());
					lvRecordHolder.updateItem(player, record);
					result.setTipMsg("赞助成功");
					suc = true;
				}
				
			}
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[donateBuff]", "赞助buff出现异常", e);
		}
		
		result.setSuccess(suc);
		return result;
	}

	
	//发送帮派前10排行榜信息
	public GroupCopyResult getDamageRank(Player player, Group g,
			GroupCopyCmdReqMsg reqMsg) {
		GroupCopyResult result = GroupCopyResult.newResult();
		String chaterID = reqMsg.getId();
		GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(chaterID);
		if(mapRecord == null){
			result.setSuccess(false);
			result.setTipMsg("找不到对应id为"+chaterID+"的地图配置！");
			return result;
		}
		GroupCopyDamegeRankInfo rankInfo = mapRecord.getDamegeRankInfo();
		GroupCopyHurtRank.Builder hr = GroupCopyHurtRank.newBuilder();
		ArmyHurtStruct.Builder struct;
		for (GroupCopyArmyDamageInfo item : rankInfo.getDamageRank()) {
			struct = ArmyHurtStruct.newBuilder();
			struct.setHeadIcon(item.getArmy().getPlayerHeadImage());
			struct.setRoleName(item.getArmy().getPlayerName());
			struct.setLv(item.getArmy().getPlayer().getLevel());
			struct.setKillTime(item.getTime());
			struct.setDamage(item.getDamage());
			hr.addRankData(struct);
		}
		
		result.setItem(hr);
		result.setSuccess(true);
		result.setTipMsg("操作成功！");
		return result;
	}

	
	
	
	
	/**
	 * 取消/申请物品
	 * @param player
	 * @param reqMsg
	 * @param apply TODO true=申请物品， false=取消申请
	 * @return
	 */
	public GroupCopyResult ApplyOrCancelItem(Player player, GroupCopyCmdReqMsg reqMsg, boolean apply) {
		//保存下帮派id
		GroupCopyDistIDManager.getInstance().addGroupID(GroupHelper.getUserGroupId(player.getUserId()));
		
		GroupCopyResult result = GroupCopyResult.newResult();
		
		String chaterID = reqMsg.getId();
		String itemID = reqMsg.getItemID();
		CopyItemDropAndApplyRecord record = dropHolder.getItemByID(chaterID);
		if(record == null){
			result.setSuccess(false);
			result.setTipMsg("找不到对应章节id为"+chaterID+"的掉落记录！");
			return result;
		}
		synchronized (record) {
			//检查是否有旧的申请记录,如果有，要去掉
			clearBeforeApplyRecord(player, record);
			if(apply){
				ItemDropAndApplyTemplate applyTemplate = record.getDropApplyRecord(itemID);
				ApplyInfo info = new ApplyInfo(player.getUserId(), player.getUserName(), System.currentTimeMillis());
				applyTemplate.addApplyRole(info);
				
			}
			
			//添加入新的记录
			dropHolder.updateItem(player, record);
			result.setSuccess(true);
		}
		
		
		return result;
	}

	/**
	 * 清除角色之前的申请记录
	 * @param player
	 * @param record
	 * @return
	 */
	private void clearBeforeApplyRecord(Player player, CopyItemDropAndApplyRecord record){
		
		//TODO 这样做并不安全，因为可能会有其他线程正在遍历这个map，而这里直接进行删除，可以会导致另一个线程出错 ---Alex
		Map<String, ItemDropAndApplyTemplate> map = record.getDaMap();
		ApplyInfo beforeApply = null;
		ItemDropAndApplyTemplate target = null;
		
		for (Iterator<ItemDropAndApplyTemplate> itr = map.values().iterator(); itr.hasNext();) {
			ItemDropAndApplyTemplate entry = itr.next();
			for (ApplyInfo i : entry.getApplyData()) {
				if(i.getRoleID().equals(player.getUserId())){
					beforeApply = i;
					break;
				}
			}
			if(beforeApply != null){
				target = entry;
				break;
			}
		}
		if(beforeApply != null){
			boolean delete = target.deleteApplyData(beforeApply);
			if(!delete){
				GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[clearBeforeApplyRecord]", "清除记录出错", null);
			}
		}
	}

	
	/**
	 * 检查并发送帮派定时奖励
	 * @param groupName
	 */
	public void checkAndSendGroupPriceMail(String groupName){
		try {
			List<CopyItemDropAndApplyRecord> itemList = dropHolder.getItemList();
			//检查每个章节
			List<ApplyInfo> applyInfo = new ArrayList<ApplyInfo>();
			List<DropInfo> dropInfo = new ArrayList<DropInfo>();
			long time = System.currentTimeMillis();
			for (CopyItemDropAndApplyRecord record : itemList) {
				boolean send = false;
				Collection<ItemDropAndApplyTemplate> map = record.getDaMap().values();
				//TODO 这里要进行优化，因为在这里直接遍历再进行操作，可能会有问题 ---Alex
				
				for (ItemDropAndApplyTemplate template : map) {
//					System.err.println("发放道具：" + template.getItemID());
					applyInfo.addAll(template.getApplyData());
					if(applyInfo == null || applyInfo.isEmpty())
						continue;
					dropInfo.addAll(template.getDropInfoList());
					if(dropInfo == null || dropInfo.isEmpty())
						continue;
					
					//如果申请人和物品都有数据，则进行分发
					Collections.sort(applyInfo, adComparator);
					Collections.sort(dropInfo, adComparator);
					ApplyInfo apply = applyInfo.get(0);
					DropInfo drop = dropInfo.get(0);
					boolean sendMail = GroupCopyMailHelper.getInstance().checkAndSendMail(template, drop, apply, groupName);
					if(sendMail){
						send = true;
//						System.err.println("发放道具成功：" + template.getItemID());
						template.deleteApply(drop, apply);
						
						DistRewRecordItem item = new DistRewRecordItem(template.getItemID(), apply.getRoleName(), time, getDistStr(apply));
						//添加分配记录
						rewardRecordHolder.addDistRecord(item);
					}
					
					applyInfo.clear();
					dropInfo.clear();
				}
				if(send){
					dropHolder.updateItem(null, record);
				}
			}
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[chekcAdSendGroupPriceMail]", "发送帮派奖励出现异常", e);
		}
	}

	
	
	/**
	 * 获取分配字符串
	 * @param apply
	 * @return
	 */
	private String getDistStr(ApplyInfo apply) {
		String str = "";
		if(StringUtils.isEmpty(apply.getDistRoleName())){
			str = SYSTEM_DIST;
		}else{
			str = String.format(ROLE_DIST, apply.getDistRoleName());
		}
		return str;
	}

	/**
	 * 获取当前章节帮派内伤害最高角色名,如果没有返回空字串
	 * @param chaterID
	 * @return
	 */
	public String getFirstDamageRoleName(String chaterID){
		GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(chaterID);
		if(mapRecord == null){
			return "";
		}
		GroupCopyArmyDamageInfo damageInfo = mapRecord.getDamegeRankInfo().getDamageRank().get(0);
		return damageInfo.getArmy().getPlayerName();
	}

	/**
	 * 检查角色在最高伤害名次，如果没有则返回0
	 * @param chaterID
	 * @param userId
	 * @return
	 */
	public int getRoleInDamageRankIndex(String chaterID, String userId) {
		int index  = 0;
		GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(chaterID);
		if(mapRecord == null){
			return index;
		}
		LinkedList<GroupCopyArmyDamageInfo> list = mapRecord.getDamegeRankInfo().getDamageRank();
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).getPlayerID().equals(userId)){
				index = i + 1;
				break;
			}
		}
		return index;
	}

	
	
	
}
