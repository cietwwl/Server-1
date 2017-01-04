package com.bm.groupCopy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.groupCopy.GroupCopyDamegeRankComparator.ApplyItemComparator;
import com.bm.groupCopy.GroupCopyDamegeRankComparator.ApplyRoleComparator;
import com.bm.groupCopy.GroupCopyDamegeRankComparator.DamageComparator;
import com.bm.groupCopy.GroupCopyDamegeRankComparator.DropItemComparator;
import com.common.Utils;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.groupCopy.cfg.GroupCopyDonateCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyDonateCfgDao;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.rwbase.dao.groupCopy.db.ApplyInfo;
import com.rwbase.dao.groupCopy.db.CopyItemDropAndApplyRecord;
import com.rwbase.dao.groupCopy.db.DistRewRecordItem;
import com.rwbase.dao.groupCopy.db.DropAndApplyRecordHolder;
import com.rwbase.dao.groupCopy.db.DropInfo;
import com.rwbase.dao.groupCopy.db.GroupCopyArmyDamageInfo;
import com.rwbase.dao.groupCopy.db.GroupCopyDamegeRankInfo;
import com.rwbase.dao.groupCopy.db.GroupCopyDistIDManager;
import com.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.rwbase.dao.groupCopy.db.GroupCopyMapRecordHolder;
import com.rwbase.dao.groupCopy.db.GroupCopyMonsterSynStruct;
import com.rwbase.dao.groupCopy.db.GroupCopyProgress;
import com.rwbase.dao.groupCopy.db.GroupCopyRewardDistRecordHolder;
import com.rwbase.dao.groupCopy.db.GroupCopyTeamInfo;
import com.rwbase.dao.groupCopy.db.ItemDropAndApplyTemplate;
import com.rwbase.dao.groupCopy.db.ServerGroupCopyDamageRecordMgr;
import com.rwbase.dao.groupCopy.db.TeamHero;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.GroupCopyAdminProto.ApplyItemData;
import com.rwproto.GroupCopyAdminProto.ApplyRewardInfo;
import com.rwproto.GroupCopyAdminProto.ChaterItemData;
import com.rwproto.GroupCopyAdminProto.MemberDamageInfo;
import com.rwproto.GroupCopyAdminProto.MemberInfo;
import com.rwproto.GroupCopyBattleProto.CopyBattleRoleStruct;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo.Builder;
import com.rwproto.GroupCopyBattleProto.CopyRewardStruct;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComRspMsg;
import com.rwproto.GroupCopyCmdProto.ArmyHurtStruct;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdReqMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyDonateData;
import com.rwproto.GroupCopyCmdProto.GroupCopyHurtRank;
import com.rwproto.GroupCopyCmdProto.GroupCopyMapStatus;

/**
 * 
 * 
 * @author Allen
 *
 */
public class GroupCopyMgr {

	/** 帮派副本关卡数据 */
	private GroupCopyLevelRecordHolder lvRecordHolder;

	/** 帮派副本地图数据 */
	private GroupCopyMapRecordHolder mapRecordHolder;

	/** 帮派副本奖励分配记录 */
	private GroupCopyRewardDistRecordHolder rewardRecordHolder;

	/** 帮派副本胜利品及申请列表 */
	private DropAndApplyRecordHolder dropHolder;

	private final String SYSTEM_DIST = "系统自动分配";
	private final String ROLE_DIST = "由%s进行分配";

	public final static GroupCopyDamegeRankComparator RANK_COMPARATOR = new GroupCopyDamegeRankComparator();
	public final static DropItemComparator DROPCOMPARATOR = new DropItemComparator();
	public final static ApplyRoleComparator ROLECOMPARATOR = new ApplyRoleComparator();
	public final static ApplyItemComparator ITEMCOMPARATOR = new ApplyItemComparator();// 章节id比较器，小的在前
	public final static DamageComparator DAMAGECOMPARATOR = new DamageComparator();

	public static final int MAX_RANK_RECORDS = 10;

	/** 最大赞助上限 */
	private static final int MAX_DONATE_COUNT = 100;

	public GroupCopyMgr(String groupIdP) {
		lvRecordHolder = new GroupCopyLevelRecordHolder(groupIdP);// 这里要先初始化关卡再初始化章节地图，因为里面会做检查
		mapRecordHolder = new GroupCopyMapRecordHolder(groupIdP);
		rewardRecordHolder = new GroupCopyRewardDistRecordHolder(groupIdP);
		dropHolder = new DropAndApplyRecordHolder(groupIdP);
		// checkAllChapterProgress();
	}

	// 检查所有副本地图进度，因为副本怪物策划可能会重新配置
	private void checkAllChapterProgress() {
		List<GroupCopyMapRecord> chapterList = mapRecordHolder.getItemList();
		for (GroupCopyMapRecord mapRecord : chapterList) {
			// 检查这个章节的当前关卡
			checkDataInternal(mapRecord);
		}

	}

	private void checkDataInternal(GroupCopyMapRecord record) {
		GroupCopyLevelRecord levelRecord;
		GroupCopyMapCfg mapCfg = GroupCopyMapCfgDao.getInstance().getCfgById(record.getChaterID());
		Set<String> lvList = mapCfg.getLvList();
		String id = mapCfg.getStartLvID();
		GroupCopyLevelCfg cfg = GroupCopyLevelCfgDao.getInstance().getCfgById(id);

		// 找到章节内第一个进度不为1的关卡id
		levelRecord = lvRecordHolder.getByLevel(id);
		int checkCount = 0;// 控制检查次数
		while (levelRecord.getProgress().getProgress() == 1 && checkCount <= 50) {
			if (lvList.contains(cfg.getNextLevelID())) {
				id = cfg.getNextLevelID();
				cfg = GroupCopyLevelCfgDao.getInstance().getCfgById(id);
				levelRecord = lvRecordHolder.getByLevel(id);
			} else {
				break;
			}
		}

		if (!record.getCurLevelID().equals(id)) {
			GameLog.error(LogModule.GroupCopy.name(), "GroupCopyMgr", "帮派副本发现章节[" + record.getId() + "]进度与实际关卡进度不匹配，副本当前章节" + record.getCurLevelID() + ",实际章节：" + id + ",进行重新设置保存");
			record.setCurLevelID(id);
			mapRecordHolder.updateItem(null, record);
		}
	}

	public static void calculateMapProgress(Player player, GroupCopyLevelRecordHolder levelRecordHolder, GroupCopyMapRecordHolder mapRecordHolder, String levelId) {

		int totalHp = 0;
		int currentHp = 0;
		GroupCopyLevelRecord lvRecord;
		GroupCopyProgress progress;
		GroupCopyLevelCfg cfg = GroupCopyLevelCfgDao.getInstance().getCfgById(levelId);
		GroupCopyMapCfg mapCfg = GroupCopyMapCfgDao.getInstance().getCfgById(cfg.getChaterID());
		Set<String> lvList = mapCfg.getLvList();
		for (String id : lvList) {
			lvRecord = levelRecordHolder.getByLevel(id);
			progress = lvRecord.getProgress();
			totalHp += progress.getTotalHp();
			currentHp += progress.getCurrentHp();
		}
		double p = Utils.div((totalHp - currentHp), totalHp, 5);
		p = p > 1.0 ? 1 : p;
		// 检查一下当前章节副本关卡id
		GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(cfg.getChaterID());
		lvRecord = levelRecordHolder.getByLevel(levelId);
		if (lvRecord.getProgress().getProgress() == 1) {
			// 已经通关，设置下一个关卡id
			if (lvList.contains(cfg.getNextLevelID())) {
				mapRecord.setCurLevelID(cfg.getNextLevelID());
			}

		}

		mapRecord.setProgress(p);
		// 如果全部通关
		if (p == 1) {
			mapRecord.setStatus(GroupCopyMapStatus.FINISH);
		}

		mapRecordHolder.updateItem(player, mapRecord);

	}

	/**
	 * 开启副本地图
	 * 
	 * @param mapId
	 * @return
	 */
	public GroupCopyResult openMap(Player player, String mapId) {
		return GroupCopyMapBL.openOrResetMap(player, mapRecordHolder, lvRecordHolder, mapId);
	}

	/**
	 * 重置副本地图
	 * 
	 * @param player TODO
	 * @param mapId
	 * @return
	 */
	public GroupCopyResult resetMap(Player player, String mapId) {
		GroupCopyResult result = GroupCopyMapBL.openOrResetMap(player, mapRecordHolder, lvRecordHolder, mapId);
		if (result.isSuccess()) {
			// 发送重置邮件
			String groupId = GroupHelper.getUserGroupId(player.getUserId());
			Group group = GroupBM.get(groupId);
			GroupCopyMapCfg cfg = GroupCopyMapCfgDao.getInstance().getCfgById(mapId);
			List<? extends GroupMemberDataIF> list = group.getGroupMemberMgr().getMemberSortList(null);
			for (GroupMemberDataIF dataIF : list) {
				GameWorldFactory.getGameWorld().asyncExecute(dataIF.getUserId(), new GroupCopyResetMailTask(dataIF.getUserId(), player.getUserName(), cfg.getName()));
			}
		}
		return result;
	}

	/**
	 * 进入副本战斗
	 * 
	 * @param player
	 * @param levelId
	 * @return
	 */
	public GroupCopyResult beginFight(Player player, String levelId) {
		return GroupCopyLevelBL.beginFight(player, lvRecordHolder, levelId);
	}

	/**
	 * 结束战斗
	 * 
	 * @param player
	 * @param levelId 关卡id
	 * @param mData 客户端返回的怪物数据
	 * @param heroList TODO
	 * @return
	 */
	public GroupCopyResult endFight(Player player, String levelId, List<GroupCopyMonsterSynStruct> mData, List<String> heroList) {
		GroupCopyResult result = GroupCopyResult.newResult();
		// 检查一下副本当前关卡是否为目标关卡
		GroupCopyLevelCfg levelCfg = GroupCopyLevelCfgDao.getInstance().getCfgById(levelId);
		GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(levelCfg.getChaterID());
		if (!levelId.equals(mapRecord.getCurLevelID())) {
			result.setSuccess(false);
			result.setTipMsg("当前关卡已经通关");
			return result;
		}

		// 获取伤害
		long damage = getDamage(mData, levelId);
		result = GroupCopyLevelBL.endFight(player, lvRecordHolder, levelId, mData, damage);
		if (result.isSuccess()) {
			// 同步一下副本地图进度
			GroupCopyMapBL.calculateMapProgress(player, lvRecordHolder, mapRecordHolder, levelId);
			// 检查是否进入章节前10伤害排行
			checkDamageRank(player, levelId, damage, heroList);
			// 将奖励入放帮派奖励缓存
			if (result.getItem() != null) {
				addReward2Group(player, levelId, (CopyRewardInfo.Builder) result.getItem());
				addReward2Role(player, levelId, (CopyRewardInfo.Builder) result.getItem());
			}

			// 检查一下章节进度，是否通关

			if (mapRecord.getStatus() == GroupCopyMapStatus.FINISH) {

				final String groupID = GroupHelper.getUserGroupId(player.getUserId());
				if (groupID.equals("")) {
					return result;
				}

				GroupCopyDistIDManager.getInstance().addGroupID(groupID);
				final String roleName = player.getUserName();
				final boolean inExtralTime = mapRecord.getRewardTime() >= System.currentTimeMillis();
				final String chaterID = levelCfg.getChaterID();
				// 通知帮派发通关邮件
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
		if (gold > 0) {
			player.getUserGameDataMgr().addCoin(gold);
		}
		if (!rewardList.isEmpty()) {
			// for (CopyRewardStruct struct : rewardList) {
			// player.getItemBagMgr().addItem(struct.getItemID(), struct.getCount());
			// }
			List<ItemInfo> addList = new ArrayList<ItemInfo>(rewardList.size());
			for (CopyRewardStruct struct : rewardList) {
				addList.add(new ItemInfo(struct.getItemID(), struct.getCount()));
			}
			ItemBagMgr.getInstance().addItem(player, addList);
		}
		// 检查有没有最后一击奖励
		if (item.getFinalHitPrice() != 0) {
			String userId = player.getUserId();
			Group group = GroupBM.get(UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(userId).getGroupId());
			group.getGroupMemberMgr().updateMemberContribution(userId, item.getFinalHitPrice(), false);
		}
	}

	private long getDamage(List<GroupCopyMonsterSynStruct> mData, String level) {
		GroupCopyProgress nowPro = new GroupCopyProgress(mData);

		GroupCopyLevelRecord record = lvRecordHolder.getByLevel(level);
		if (record.getProgress().getCurrentHp() == 0) {
			return nowPro.getTotalHp() - nowPro.getCurrentHp();
		}
		long damage = record.getProgress().getCurrentHp() - nowPro.getCurrentHp();
		if (damage <= 0) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[getDamage]", "帮派副本[" + level + "]战斗结束，客户端同步数据不正确，进入战斗前怪物总HP:" + record.getProgress().getCurrentHp() + ",战斗后总HP" + nowPro.getCurrentHp() + ",请检查关卡内是否存在加血技能的怪物！！！", null);
		}
		return damage;
	}

	/**
	 * 添加帮派奖励
	 * 
	 * @param player
	 * @param levelId
	 * @param item
	 */
	private void addReward2Group(Player player, String levelId, Builder item) {

		GroupCopyLevelCfg cfg = GroupCopyLevelCfgDao.getInstance().getCfgById(levelId);
		// 发放帮派经验
		Group group = com.rw.service.group.helper.GroupHelper.getGroup(player);
		group.getGroupBaseDataMgr().updateGroupDonate(player, null, 0, cfg.getGroupExp(), 0, true);

		GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(cfg.getChaterID());
		CopyItemDropAndApplyRecord dropAndApplyRecord = dropHolder.getItemByID(cfg.getChaterID());
		List<CopyRewardStruct> list = item.getDropList();
		for (CopyRewardStruct d : list) {
			ItemDropAndApplyTemplate dropApplyRecord = dropAndApplyRecord.getDropApplyRecord(String.valueOf(d.getItemID()));
			dropApplyRecord.addDropItem(d.getCount());
		}
		dropHolder.updateItem(player, dropAndApplyRecord);

		mapRecordHolder.updateItem(player, mapRecord);
	}

	private void checkDamageRank(Player player, String levelId, long damage, List<String> heroList) {
		try {

			GroupCopyLevelCfg cfg = GroupCopyLevelCfgDao.getInstance().getCfgById(levelId);

			ArmyInfo info = ArmyInfoHelper.getArmyInfo(player.getUserId(), heroList);
			GroupCopyArmyDamageInfo damageInfo = armyInfo2DamageInfo(info, player);
			damageInfo.setDamage(damage);
			mapRecordHolder.checkDamageRank(cfg.getChaterID(), damageInfo);
			// 关卡全服单次伤害排行
			boolean kill = lvRecordHolder.getByLevel(levelId).getProgress().getCurrentHp() == 0;
			ServerGroupCopyDamageRecordMgr.getInstance().checkDamageRank(levelId, damageInfo, player, kill);

			// 增加成员章节总伤害
			GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(cfg.getChaterID());
			mapRecord.addPlayerDamage(player.getUserId(), damage);
			mapRecordHolder.updateItem(null, mapRecord);
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[checkDamageRank]", "帮派副本战斗结束检查排行榜时出现异常", e);
		}

	}

	public static GroupCopyArmyDamageInfo armyInfo2DamageInfo(ArmyInfo info, Player player) {
		GroupCopyArmyDamageInfo damageInfo = new GroupCopyArmyDamageInfo();
		damageInfo.setPlayerID(player.getUserId());
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
		teamInfo.setPlayerHeadFrame(player.getHeadFrame());
		damageInfo.setArmy(teamInfo);

		return damageInfo;
	}

	private static TeamHero initTeamHero(TeamHero tHero, ArmyHero hero) {
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
	 * 
	 * @param player
	 * @param version
	 */
	public void synMapData(Player player, int version) {

		mapRecordHolder.synAllData(player, version);

	}

	/**
	 * 同步副本关卡数据
	 * 
	 * @param player
	 * @param version
	 */
	public void synLevelData(Player player, int version) {

		lvRecordHolder.synAllData(player, version);

	}

	/**
	 * 同步奖励分配记录
	 * 
	 * @param player
	 * @param version
	 */
	public void synRewardLogData(Player player) {
		rewardRecordHolder.synAllData(player);
	}

	public void synDropAppyData(Player player, String chaterID) {
		dropHolder.synSingleData(player, chaterID);
	}

	/**
	 * 请求是否可以进入关卡
	 * 
	 * @param player
	 * @param level
	 * @return
	 */
	public GroupCopyBattleComRspMsg.Builder applyEnterCopy(Player player, String level, GroupCopyBattleComRspMsg.Builder rspMsg) {
		try {
			GroupCopyLevelRecord lvData = lvRecordHolder.getByLevel(level);
			if (lvData == null) {
				GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[applyEnterCopy]", "角色请求进入关卡，找不到关卡id为" + level + "的记录", null);
				rspMsg.setTipMsg("服务器繁忙！");
				return rspMsg;
			}
			// 检查当前关卡是否已经通关
			if (lvData.getProgress().getProgress() == 1.0) {
				rspMsg.setTipMsg("当前关卡已通关！");
				return rspMsg;
			}

			// 检查一下当前的关卡是否为章节的当前关卡
			GroupCopyLevelCfg levelCfg = GroupCopyLevelCfgDao.getInstance().getCfgById(level);
			GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(levelCfg.getChaterID());
			if (!mapRecord.getCurLevelID().equals(level)) {
				rspMsg.setTipMsg("当前关卡已通关！");
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
				// 检查有没有超时
				leftTime = (int) ((lvData.getLastBeginFightTime() + GroupCopyLevelBL.MAX_WAIT_SPAN - curTime) / 1000);
				if (leftTime <= 0) {
					// 已经超时，重置
					enter = updateCopyState(player, lvData, status);
				} else {
					// 没有超时，返回关卡内的角色数据
					roleStruct = getRoleStruct(lvData.getFighterId(), leftTime, status);
				}
				break;
			case GroupCopyLevelBL.STATE_COPY_FIGHT:
				// 检查有没有超时
				leftTime = (int) ((lvData.getLastBeginFightTime() + GroupCopyLevelBL.MAX_FIGHT_SPAN - curTime) / 1000);
				if (leftTime <= 0) {
					// 已经超时，重置
					enter = updateCopyState(player, lvData, GroupCopyLevelBL.STATE_COPY_WAIT);
				} else {
					// 没有超时，返回关卡内的角色数据
					roleStruct = getRoleStruct(lvData.getFighterId(), leftTime, status);
				}
				break;

			default:
				break;
			}

			if (roleStruct != null) {
				rspMsg.setBattleRole(roleStruct);
				rspMsg.setTipMsg(roleStruct.getRoleName() + "正在关卡内战斗");
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
	private CopyBattleRoleStruct.Builder getRoleStruct(String roleID, int leftTimeSec, int status) {
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
	 * 
	 * @param player
	 * @param lvData
	 * @param state TODO targetState
	 * @return
	 */
	private boolean updateCopyState(Player player, GroupCopyLevelRecord lvData, int state) {
		try {
			lvData.setFighterId(player.getUserId());
			lvData.setLastBeginFightTime(System.currentTimeMillis());
			;
			lvData.setStatus(state);
			lvRecordHolder.updateItem(player, lvData);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 处理赞助
	public GroupCopyResult donateBuff(Player player, Group group, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyResult result = GroupCopyResult.newResult();
		GroupCopyDonateData data = reqMsg.getDonateData();
		boolean suc = false;
		result.setTipMsg("操作失败！");
		try {
			// 判断一下是否有足够的钻石
			GroupCopyDonateCfg donateCfg = GroupCopyDonateCfgDao.getInstance().getCfgById(String.valueOf(data.getDonateTime()));
			if (donateCfg == null) {
				result.setTipMsg("找不到次数为" + data.getDonateTime() + "的配置！");
				result.setSuccess(suc);
				return result;
			}

			boolean engough = player.getUserGameDataMgr().isGoldEngough(-donateCfg.getGold());
			if (!engough) {
				result.setTipMsg("钻石不足～");
				result.setSuccess(suc);
				return result;
			}

			synchronized (this) {
				GroupCopyLevelRecord record = lvRecordHolder.getByLevel(data.getLevel());
				if (record == null) {
					GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[donateBuff]", "角色赞助Buff找不到关卡id为" + data.getLevel() + "的记录", null);
					result.setTipMsg("服务器繁忙！");
					result.setSuccess(suc);
					return result;
				}

				if ((record.getBuffCount() + donateCfg.getIncreValue()) > MAX_DONATE_COUNT) {
					result.setTipMsg("超过赞助上限！");
					result.setSuccess(suc);
					return result;
				}
				record.addRoleDonate(player.getUserName(), donateCfg.getGold());
				record.addBuff(donateCfg.getIncreValue());
				// 扣钱 加帮贡
				int code = player.getUserGameDataMgr().addGold(-donateCfg.getGold());

				if (code == 0) {
					UserGroupAttributeDataMgr.getMgr().useUserGroupContribution(player.getUserId(), donateCfg.getContribution());
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

	// 发送帮派前10排行榜信息
	public GroupCopyResult getDamageRank(Player player, Group g, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyResult result = GroupCopyResult.newResult();
		String chaterID = reqMsg.getId();
		GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(chaterID);
		if (mapRecord == null) {
			result.setSuccess(false);
			result.setTipMsg("找不到对应id为" + chaterID + "的地图配置！");
			return result;
		}
		GroupCopyDamegeRankInfo rankInfo = mapRecord.getDamegeRankInfo();
		GroupCopyHurtRank.Builder hr = GroupCopyHurtRank.newBuilder();
		ArmyHurtStruct.Builder struct;
		for (GroupCopyArmyDamageInfo item : rankInfo.getDamageRank()) {
			struct = ArmyHurtStruct.newBuilder();
			Player role = PlayerMgr.getInstance().find(item.getPlayerID());
			struct.setHeadIcon(role.getHeadImage());
			struct.setRoleName(role.getUserName());
			struct.setLv(item.getArmy().getPlayer().getLevel());
			struct.setKillTime(item.getTime());
			struct.setDamage(item.getDamage());
			struct.setHeadFrame(role.getHeadFrame());
			hr.addRankData(struct);
		}

		result.setItem(hr);
		result.setSuccess(true);
		result.setTipMsg("操作成功！");
		return result;
	}

	/**
	 * 取消/申请物品
	 * 
	 * @param player
	 * @param reqMsg
	 * @param apply TODO true=申请物品， false=取消申请
	 * @return
	 */
	public GroupCopyResult ApplyOrCancelItem(Player player, GroupCopyCmdReqMsg reqMsg, boolean apply) {
		// 保存下帮派id
		GroupCopyDistIDManager.getInstance().addGroupID(GroupHelper.getUserGroupId(player.getUserId()));

		GroupCopyResult result = GroupCopyResult.newResult();

		String chaterID = reqMsg.getId();
		String itemID = reqMsg.getItemID();
		CopyItemDropAndApplyRecord record = dropHolder.getItemByID(chaterID);
		if (record == null) {
			result.setSuccess(false);
			result.setTipMsg("找不到对应章节id为" + chaterID + "的掉落记录！");
			return result;
		}
		GroupCopyMapCfg mapCfg = GroupCopyMapCfgDao.getInstance().getConfig(chaterID);
		if (!mapCfg.getWarPriceList().contains(itemID)) {
			result.setSuccess(false);
			result.setTipMsg("找不到对应章节id为" + itemID + "的掉落道具！");
			return result;
		}
		// 检查是否有旧的申请记录,如果有，要去掉
		clearBeforeApplyRecord(player.getUserId(), record);
		if (apply) {
			ItemDropAndApplyTemplate applyTemplate = record.getDropApplyRecord(itemID);
			ApplyInfo info = new ApplyInfo(player.getUserId(), player.getUserName(), System.currentTimeMillis());
			applyTemplate.addApplyRole(info);

		}

		// 添加入新的记录
		dropHolder.updateItem(player, record);
		result.setSuccess(true);

		return result;
	}

	/**
	 * 清除角色之前的申请记录
	 * 
	 * @param userID
	 * @param record
	 * @return
	 */
	private void clearBeforeApplyRecord(String userID, CopyItemDropAndApplyRecord record) {
		Map<String, ItemDropAndApplyTemplate> map = record.getDaMap();
		ApplyInfo beforeApply = null;
		ItemDropAndApplyTemplate target = null;

		for (Iterator<ItemDropAndApplyTemplate> itr = map.values().iterator(); itr.hasNext();) {
			ItemDropAndApplyTemplate entry = itr.next();
			for (ApplyInfo i : entry.getApplyData()) {
				if (i.getRoleID().equals(userID)) {
					beforeApply = i;
					break;
				}
			}
			if (beforeApply != null) {
				target = entry;
				break;
			}
		}
		if (beforeApply != null) {
			boolean delete = target.deleteApplyData(beforeApply);
			if (!delete) {
				GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[clearBeforeApplyRecord]", "清除记录出错", null);
			}
		}
	}

	/**
	 * 检查并发送帮派定时奖励
	 * 
	 * @param groupName
	 */
	public void checkAndSendGroupPriceMail(Group group) {
		try {
			String groupName = group.getGroupBaseDataMgr().getGroupData().getGroupName();
			List<CopyItemDropAndApplyRecord> itemList = dropHolder.getItemList();
			// 检查每个章节
			List<ApplyInfo> applyInfo = new ArrayList<ApplyInfo>();
			List<DropInfo> dropInfo = new ArrayList<DropInfo>();
			long time = System.currentTimeMillis();
			for (CopyItemDropAndApplyRecord record : itemList) {
				boolean send = false;
				Collection<ItemDropAndApplyTemplate> map = record.getDaMap().values();

				for (ItemDropAndApplyTemplate template : map) {
					applyInfo.addAll(template.getApplyData());
					if (applyInfo == null || applyInfo.isEmpty())
						continue;
					dropInfo.addAll(template.getDropInfoList());
					if (dropInfo == null || dropInfo.isEmpty())
						continue;

					// 如果申请人和物品都有数据，则进行分发
					Collections.sort(applyInfo, ROLECOMPARATOR);
					Collections.sort(dropInfo, DROPCOMPARATOR);
					ApplyInfo apply = null;
					DropInfo drop = dropInfo.get(0);
					boolean match = false;
					// 找到符合的申请人，在物品掉落后进入帮派的不可以分 --按宇超要求，去掉这个限制 14.12.2016 by Alex
					for (int i = 0; i < applyInfo.size(); i++) {
						apply = applyInfo.get(i);
						GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(apply.getRoleID(), false);
						// if(memberData == null || (drop.getTime() < memberData.getReceiveTime())){
						if (memberData == null) {
							// GameLog.warn(LogModule.GroupCopy.getName(), "GroupCopyMgr[sendMail]", String.format(
							// "Group ID[%s], item drop time [%s], role[%s] join group time [%s], he can't get item",group.getGroupBaseDataMgr().getGroupData().getGroupId(),
							// DateUtils.getDateTimeFormatString(drop.getTime(), "yyyy-MM-dd HH:mm:ss"),apply.getRoleName(),
							// DateUtils.getDateTimeFormatString(memberData.getReceiveTime(), "yyyy-MM-dd hh:m:ss")));
							continue;
						}
						match = true;
						break;
					}
					if (!match) {
						// 找不到合条件的分配者，则不分配此物品
						continue;
					}

					boolean sendMail = sendGroupPriceMailAndRecord(template.getItemID(), apply, groupName, time);
					if (sendMail) {
						send = true;
						GameLog.warn(LogModule.GroupCopy.getName(), "GroupCopyMgr[sendMail]", String.format("发放道具成功，道具：[%s],接收角色[%s],时间：[%s]", template.getItemID(), apply.getRoleName(), DateUtils.getDateTimeFormatString(time, "yyyy-MM-dd HH:mm:ss")));
						// System.err.println(String.format("发放道具成功，道具：[%s],接收角色[%s],时间：[%s]", template.getItemID(), apply.getRoleName(),
						// DateUtils.getDateTimeFormatString(time, "yyyy-MM-dd HH:mm:ss")));
						template.deleteApply(drop, apply);

					}

					applyInfo.clear();
					dropInfo.clear();
				}
				if (send) {
					dropHolder.updateItem(null, record);
				}
			}
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyMgr[chekcAdSendGroupPriceMail]", "发送帮派奖励出现异常", e);
		}
	}

	/**
	 * 发送帮派奖励邮件
	 * 
	 * @param itemID 奖励道具ID
	 * @param apply 收件人
	 * @param groupName 帮派名
	 * @param sendTime 发送时间
	 * @return
	 */
	public boolean sendGroupPriceMailAndRecord(int itemID, ApplyInfo apply, String groupName, long sendTime) {
		boolean sendMail = GroupCopyMailHelper.getInstance().checkAndSendMail(itemID, apply, groupName);
		if (sendMail) {
			DistRewRecordItem item = new DistRewRecordItem(itemID, apply.getRoleName(), sendTime, getDistStr(apply));
			// 添加分配记录
			rewardRecordHolder.addDistRecord(item);
		}
		return sendMail;
	}

	/**
	 * 获取分配字符串
	 * 
	 * @param apply
	 * @return
	 */
	private String getDistStr(ApplyInfo apply) {
		String str = "";
		if (StringUtils.isEmpty(apply.getDistRoleName())) {
			str = SYSTEM_DIST;
		} else {
			str = String.format(ROLE_DIST, apply.getDistRoleName());
		}
		return str;
	}

	/**
	 * 获取当前章节帮派内伤害最高角色名,如果没有返回空字串
	 * 
	 * @param chaterID
	 * @return
	 */
	public String getFirstDamageRoleName(String chaterID) {
		GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(chaterID);
		if (mapRecord == null) {
			return "";
		}
		GroupCopyArmyDamageInfo damageInfo = mapRecord.getDamegeRankInfo().getDamageRank().get(0);
		return damageInfo.getArmy().getPlayerName();
	}

	/**
	 * 检查角色在最高伤害名次，如果没有则返回0
	 * 
	 * @param chaterID
	 * @param userId
	 * @return
	 */
	public int getRoleInDamageRankIndex(String chaterID, String userId) {
		int index = 0;
		GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(chaterID);
		if (mapRecord == null) {
			return index;
		}
		LinkedList<GroupCopyArmyDamageInfo> list = mapRecord.getDamegeRankInfo().getDamageRank();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getPlayerID().equals(userId)) {
				index = i + 1;
				break;
			}
		}

		return index;
	}

	/**
	 * 获取所有奖励申请情况
	 * 
	 * @param player
	 * @return
	 */
	public GroupCopyResult applyAllRewardInfo(Player player) {
		GroupCopyResult result = GroupCopyResult.newResult();

		ApplyRewardInfo.Builder builder = ApplyRewardInfo.newBuilder();
		// 计算下次分配时间
		int leftMin = 0;
		int hour = DateUtils.getCurrentHour();
		if (hour < 11) {
			hour = 11 - hour;
			leftMin = hour * 60 + (60 - DateUtils.getCurMinuteOfHour());
		} else {
			leftMin = 60 - DateUtils.getCurMinuteOfHour();
		}

		// 组装数据
		builder.setNextTime(leftMin);

		List<CopyItemDropAndApplyRecord> list = dropHolder.getItemList();
		// 根据chaterID 排一下序
		Collections.sort(list, ITEMCOMPARATOR);

		ChaterItemData.Builder chaterData;
		for (CopyItemDropAndApplyRecord record : list) {
			chaterData = ChaterItemData.newBuilder();
			chaterData.setChaterID(record.getChaterID());
			for (ItemDropAndApplyTemplate datemplate : record.getDaMap().values()) {
				if (!datemplate.noDropItem()) {
					ApplyItemData.Builder data = ApplyItemData.newBuilder();
					data.setItemID(datemplate.getItemID());
					data.setApplyCount(datemplate.getApplyData().size());
					chaterData.addItemData(data);
				}
			}
			if (chaterData.getItemDataCount() > 0) {
				builder.addChaterData(chaterData);
			}
		}

		result.setItem(builder);
		result.setSuccess(true);
		return result;
	}

	/**
	 * 获取所有角色的章节伤害信息
	 * 
	 * @param group TODO
	 * @param mapID
	 * @param itemID
	 * @return
	 */
	public GroupCopyResult applyAllRoleDamageInfo(Group group, String mapID, int itemID) {
		GroupCopyResult result = GroupCopyResult.newResult();
		MemberDamageInfo.Builder damageInfo = MemberDamageInfo.newBuilder();

		try {

			List<? extends GroupMemberDataIF> memberList = group.getGroupMemberMgr().getMemberSortList(null);

			ItemDropAndApplyTemplate template = dropHolder.getItemApplyDataByID(mapID, itemID);
			if (template == null) {
				result.setSuccess(false);
				result.setTipMsg("无此物品数据!");
				return result;
			}
			long dropTime = template.firstDropTime();
			List<MemberInfo.Builder> applyList = new ArrayList<MemberInfo.Builder>();
			List<MemberInfo.Builder> unApplyList = new ArrayList<MemberInfo.Builder>();
			List<ApplyInfo> applyData = template.getApplyData();
			for (GroupMemberDataIF m : memberList) {
				MemberInfo.Builder member = MemberInfo.newBuilder();
				Player role = PlayerMgr.getInstance().find(m.getUserId());
				member.setUseID(m.getUserId());
				member.setHeadIcon(m.getHeadId());
				member.setHeadbox(role.getHeadFrame());

				member.setLv(m.getLevel());
				member.setRoleName(m.getName());
				member.setDamage(getRoleDamage(m.getUserId(), mapID));
				if (m.getReceiveTime() > dropTime) {
					member.setCanDist(false);
				} else {
					member.setCanDist(true);
				}
				if (getRoleApplyInfo(m.getUserId(), applyData) != null) {
					applyList.add(member);

				} else {
					unApplyList.add(member);

				}
			}

			if (applyList.size() > 0) {
				Collections.sort(applyList, DAMAGECOMPARATOR);
			}
			if (unApplyList.size() > 0) {
				Collections.sort(unApplyList, DAMAGECOMPARATOR);
			}

			for (MemberInfo.Builder applyInfo : applyList) {
				damageInfo.addApplyRoleList(applyInfo);
			}
			for (MemberInfo.Builder builder : unApplyList) {
				damageInfo.addUnApplyRoleList(builder);
			}

			result.setItem(damageInfo);
			result.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 获取角色在当前章节的总伤害
	 * 
	 * @param playerID
	 * @param chaterID
	 * @return
	 */
	private long getRoleDamage(String playerID, String chaterID) {
		GroupCopyMapRecord record = mapRecordHolder.getItemByID(chaterID);
		Long damage = record.getGroupRoleDamageMap().get(playerID);
		if (damage == null) {
			return 0;
		}
		return damage;
	}

	private ApplyInfo getRoleApplyInfo(String roleID, List<ApplyInfo> list) {
		ApplyInfo roleApply = null;
		for (ApplyInfo info : list) {
			if (info.getRoleID().equals(roleID)) {
				roleApply = info;
				break;
			}
		}
		return roleApply;
	}

	/**
	 * 分配奖励给选择的角色
	 * 
	 * @param group TODO
	 * @param role
	 * @param mapID
	 * @param itemID
	 * @param distRoleName TODO
	 * @return
	 */
	public GroupCopyResult distReward2Role(Group group, Player role, String mapID, int itemID, String distRoleName) {
		GroupCopyResult result = GroupCopyResult.newResult();
		try {
			// 先找到章节的奖励
			CopyItemDropAndApplyRecord record = dropHolder.getItemByID(mapID);

			ItemDropAndApplyTemplate template = record.getDaMap().get(String.valueOf(itemID));

			// 检查是否还有可以奖励的道具

			List<DropInfo> tempList = new ArrayList<DropInfo>();
			tempList.addAll(template.getDropInfoList());
			Collections.sort(tempList, DROPCOMPARATOR);
			;
			DropInfo dropInfo = tempList.get(0);
			if (dropInfo == null) {
				result.setSuccess(false);
				result.setTipMsg("道具数量不足");
				return result;
			}
			GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(role.getUserId(), false);
			if (memberData.getReceiveTime() > dropInfo.getTime()) {
				result.setSuccess(false);
				result.setTipMsg("不可将道具分配给比道具掉落更迟的玩家");
				return result;
			}

			// 可分配，则修改记录
			ApplyInfo oldData = getRoleApplyInfo(role.getUserId(), template.getApplyData());
			// if(oldData != null){
			// template.deleteApplyData(oldData);
			// }
			ApplyInfo applyInfo = new ApplyInfo(role.getUserId(), role.getUserName(), System.currentTimeMillis());
			applyInfo.setDistRoleName(distRoleName);
			// template.addApplyRole(applyInfo);//这里不再加回去，策划改为实时发送邮件
			boolean send = sendGroupPriceMailAndRecord(itemID, applyInfo, group.getGroupBaseDataMgr().getGroupData().getGroupName(), System.currentTimeMillis());
			if (send) {
				template.deleteApply(dropInfo, oldData);
				dropHolder.updateItem(role, record);
			}

			result.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * 角色离开帮派(包括主动和被动)
	 * 
	 * @param kickMemberId
	 */
	public void nofityCreateRoleLeaveTask(final String kickMemberId) {
		// 避免影响客户端响应消息，这里使用异步操作

		GameWorldFactory.getGameWorld().asynExecute(new Runnable() {

			@Override
			public void run() {
				// clear role apply war price data
				List<CopyItemDropAndApplyRecord> daList = dropHolder.getItemList();
				List<ApplyInfo> tempData = new ArrayList<ApplyInfo>();
				for (CopyItemDropAndApplyRecord record : daList) {
					Map<String, ItemDropAndApplyTemplate> map = record.getDaMap();
					boolean remove = false;
					for (ItemDropAndApplyTemplate item : map.values()) {
						tempData.clear();
						tempData.addAll(item.getApplyData());
						for (ApplyInfo d : tempData) {
							if (d.getRoleID().equals(kickMemberId)) {
								remove = item.deleteApplyData(d);
							}
						}
					}
					if (remove) {
						dropHolder.updateItem(null, record);
					}
				}

			}
		});
	}

}
