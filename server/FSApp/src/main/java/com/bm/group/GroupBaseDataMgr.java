package com.bm.group;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

import com.rwproto.GroupCommonProto.GroupLogType;
import com.rwproto.GroupCommonProto.GroupState;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.group.helper.GroupHelper;
import com.rw.service.group.helper.GroupRankHelper;
import com.rwbase.dao.group.pojo.cfg.GroupBaseConfigTemplate;
import com.rwbase.dao.group.pojo.cfg.GroupLevelCfg;
import com.rwbase.dao.group.pojo.cfg.GroupSkillLevelTemplate;
import com.rwbase.dao.group.pojo.cfg.dao.GroupConfigCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupLevelCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupSkillLevelCfgDAO;
import com.rwbase.dao.group.pojo.db.GroupBaseData;
import com.rwbase.dao.group.pojo.db.GroupBaseDataHolder;
import com.rwbase.dao.group.pojo.db.GroupLog;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;

/**
 * 帮派基础数据管理类
 * 
 * @author HC
 *
 */
public class GroupBaseDataMgr {

	private GroupBaseDataHolder groupBaseDataHolder;

	public GroupBaseDataMgr(String groupId) {
		groupBaseDataHolder = new GroupBaseDataHolder(groupId);
	}

	/**
	 * 获取帮派的数据
	 *
	 * @return
	 */
	public GroupBaseDataIF getGroupData() {
		return groupBaseDataHolder.getGroupData();
	}

	/**
	 * 获取上次检查帮主离线的时间
	 * 
	 * @return
	 */
	public long getLastCheckTime() {
		GroupBaseData groupData = groupBaseDataHolder.getGroupData();
		if (groupData == null) {
			return 0;
		}

		return groupData.getLastCheckTime();
	}

	/**
	 * 获取距离帮主转换剩余的时间
	 * 
	 * @return
	 */
	public long getDistanceTransferTime() {
		GroupBaseData groupData = groupBaseDataHolder.getGroupData();
		if (groupData == null) {
			return 0;
		}

		return groupData.getDistanceTransferTime();
	}

	/**
	 * 更新检查帮主转换的检查时间
	 * 
	 * @param lastCheckTime
	 * @param distanceTransferTime
	 */
	public synchronized void updateCheckTransferLeaderTime(long lastCheckTime, long distanceTransferTime) {
		GroupBaseData groupData = groupBaseDataHolder.getGroupData();
		if (groupData == null) {
			return;
		}

		groupData.setLastCheckTime(lastCheckTime);
		groupData.setDistanceTransferTime(distanceTransferTime);
	}

	/**
	 * 更新帮派的数据
	 * 
	 * @param player 角色
	 * @param dismissTime 解散的时间
	 * @param state 更新后帮派的状态
	 */
	public synchronized void updateGroupDismissState(Player player, long dismissTime, GroupState state) {
		GroupBaseData groupData = groupBaseDataHolder.getGroupData();
		if (groupData == null) {
			return;
		}

		groupData.setDismissTime(dismissTime);
		groupData.setGroupState((byte) state.getNumber());
		updateAndSynGroupData(player);
	}

	/**
	 * 设置帮派属性
	 * 
	 * @param player 角色
	 * @param iconId 图标
	 * @param declaration 宣言
	 * @param validateType 验证类型
	 * @param applyLevel 接受申请的等级
	 */
	public synchronized void updateGroupSetting(Player player, String iconId, String declaration, int validateType, int applyLevel) {
		GroupBaseData groupData = groupBaseDataHolder.getGroupData();
		if (groupData == null) {
			return;
		}

		// 图标
		if (iconId != null) {
			groupData.setIconId(iconId);
		}

		// 宣言
		if (declaration != null) {
			groupData.setDeclaration(declaration);
		}

		// 验证类型
		if (validateType > 0) {
			groupData.setValidateType((byte) validateType);
		}

		// 接受申请等级
		if (applyLevel > 0) {
			groupData.setApplyLevel((short) applyLevel);
		}

		updateAndSynGroupData(player);
	}

	/**
	 * 更新帮派的名字
	 * 
	 * @param player 角色
	 * @param groupName 帮派的名字
	 */
	public synchronized void updateGroupName(Player player, String groupName) {
		if (StringUtils.isEmpty(groupName)) {
			return;
		}

		GroupBaseData groupData = groupBaseDataHolder.getGroupData();
		if (groupData == null) {
			return;
		}

		groupData.setGroupName(groupName);
		updateAndSynGroupData(player);
		GCompFightingRankMgr.updateGroupBaseInfo(GroupHelper.getGroup(player));
	}

	/**
	 * 更新帮派的公告
	 * 
	 * @param player 角色
	 * @param announcement 帮派的公告
	 */
	public synchronized void updateGroupAnnouncement(Player player, String announcement) {
		if (StringUtils.isEmpty(announcement)) {
			return;
		}

		GroupBaseData groupData = groupBaseDataHolder.getGroupData();
		if (groupData == null) {
			return;
		}

		groupData.setAnnouncement(announcement);
		updateAndSynGroupData(player);
	}

	/**
	 * 更新帮派的捐献
	 * 
	 * @param player
	 * @param logMgr 日志Mgr
	 * @param rewardGroupSupply 奖励的帮派物资
	 * @param rewardGroupExp 奖励的帮派经验
	 * @param rewardToken 发给帮派的令牌数量
	 */
	public synchronized boolean updateGroupDonate(Player player, GroupLogMgr logMgr, int rewardGroupSupply, int rewardGroupExp, int rewardToken, boolean needSyn) {
		GroupBaseData groupData = groupBaseDataHolder.getGroupData();
		if (groupData == null) {
			return false;
		}

		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct != null) {
			if (rewardGroupSupply > 0) {
				int daySupply = groupData.getDaySupplies();
				int dayMaxGroupSupply = gbct.getMaxSupplyLimitPerDay();// 帮派物资
				if (daySupply < dayMaxGroupSupply) {
					int leftGroupSupply = dayMaxGroupSupply - daySupply;
					rewardGroupSupply = leftGroupSupply >= rewardGroupSupply ? rewardGroupSupply : leftGroupSupply;
				} else {
					rewardGroupSupply = 0;
				}
			}

			int dayExp = groupData.getDayExp();
			int dayMaxGroupExp = gbct.getMaxExpLimitPerDay();// 帮派经验
			if (dayExp < dayMaxGroupExp) {
				int leftGroupExp = dayMaxGroupExp - dayExp;
				rewardGroupExp = leftGroupExp >= rewardGroupExp ? rewardGroupExp : leftGroupExp;
			} else {
				rewardGroupExp = 0;
			}
		}

		if (rewardGroupSupply != 0) {
			int supplies = groupData.getSupplies() + rewardGroupSupply;
			if (supplies < 0) {
				return false;
			}

			groupData.setSupplies(supplies);
			if (rewardGroupSupply > 0) {
				groupData.setDaySupplies(groupData.getDaySupplies() + rewardGroupSupply);
			}
		}

		if (rewardGroupExp > 0) {
			addGroupExp(player, groupData, logMgr, rewardGroupExp);
			groupData.setDayExp(groupData.getDayExp() + rewardGroupExp);
		}

		if (rewardToken != 0) {
			int curToken = groupData.getToken();
			int tokens = curToken + rewardToken;
			if (tokens < 0) {
				return false;
			}

			groupData.setToken(tokens);
		}

		if (needSyn) {
			updateAndSynGroupData(player);
		}

		return true;
	}

	/**
	 * 帮派等级研发
	 * 
	 * @param player
	 * @param needGroupSupply
	 * @param skillId
	 * @param skillLevel
	 * @param researchCondation 研发的前置条件
	 * @return
	 */
	public synchronized String updateGroupDataWhenResearchSkill(Player player, int needGroupSupply, int skillId, int skillLevel, Map<Integer, Integer> researchCondation) {
		GroupBaseData groupData = groupBaseDataHolder.getGroupData();
		if (groupData == null) {
			return "帮派不存在";
		}

		int hasLevel = groupData.getResearchedSkillLevel(skillId);
		String userId = player.getUserId();
		if (hasLevel < 0) {
			GameLog.error("研发帮派技能", userId, String.format("技能Id[%s]，还没有被研发", skillId));
			return "需要前置技能满级后才能研发";
		}

		if (skillLevel - hasLevel != 1) {
			GameLog.error("研发帮派技能", userId, String.format("技能Id[%s]，已经研发的等级[%s]，请求研发等级[%s]差别不符", skillId, hasLevel, skillLevel));
			return "请求了非法的等级";
		}

		int supplies = groupData.getSupplies();
		if (needGroupSupply > supplies) {
			GameLog.error("研发帮派技能", userId, String.format("当前帮派的物资[%s],需要的物资是[%s]", supplies, needGroupSupply));
			return "帮派物资不足";
		}

		// 检查前置条件
		if (researchCondation != null) {
			for (Entry<Integer, Integer> entry : researchCondation.entrySet()) {
				int needSkillId = entry.getKey().intValue();
				int needSkillLevel = entry.getValue().intValue();
				if (!groupData.checkHasResearchedSkill(needSkillId, needSkillLevel)) {
					GameLog.error("研发帮派技能", userId, String.format("需要技能Id是[%s],技能等级是[%s],条件未达成", needSkillId, needSkillLevel));
					return "需要前置技能满级后才能研发";
				}
			}
		}

		// 扣物资
		updateGroupDonate(player, null, -needGroupSupply, 0, 0, false);
		// 更新帮派研发技能数据
		groupData.addOrUpdateResearchSkill(skillId, skillLevel, -1, -1);

		// 初始化帮派初始开启可以研发的技能列表
		GroupSkillLevelCfgDAO dao = GroupSkillLevelCfgDAO.getDAO();
		List<Integer> allSkillIdList = dao.getAllSkillIdList();
		for (int i = 0, size = allSkillIdList.size(); i < size; i++) {
			Integer sId = allSkillIdList.get(i);
			if (groupData.hasResearchedSkill(sId)) {
				continue;
			}

			List<GroupSkillLevelTemplate> skillTmpList = dao.getSkillLevelTmpList(sId);
			for (int j = 0, tmpSize = skillTmpList.size(); j < tmpSize; j++) {
				GroupSkillLevelTemplate skillLevelTmp = skillTmpList.get(j);

				boolean canOpen = true;
				Map<Integer, Integer> condations = skillLevelTmp.getResearchCondation();
				for (Entry<Integer, Integer> e : condations.entrySet()) {
					if (!groupData.checkHasResearchedSkill(e.getKey(), e.getValue())) {
						canOpen = false;
						break;
					}
				}

				if (canOpen) {
					groupData.addOrUpdateResearchSkill(skillLevelTmp.getSkillId(), 0, -1, -1);
				}
			}
		}

		updateAndSynGroupData(player);
		// 更新数据并同步数据到前端
		updateAndSynGroupSkillData(player);
		return "";
	}

	/**
	 * 增加帮派经验值
	 * 
	 * @param groupData
	 * @param groupExp
	 */
	public boolean addGroupExp(Player player, GroupBaseData groupData, GroupLogMgr logMgr, int groupExp) {
		if (groupExp <= 0) {
			return false;
		}

		GroupLevelCfgDAO dao = GroupLevelCfgDAO.getDAO();
		int curExp = groupData.getGroupExp();
		int curLevel = groupData.getGroupLevel();
		int oldLevel = curLevel;// 旧等级

		GroupLevelCfg levelTmp = dao.getLevelCfg(curLevel);
		if (levelTmp == null) {
			return false;
		}

		int totalExp = curExp + groupExp;// 当前总经验值
		int needExp = levelTmp.getNeedExp();// 升级需要的经验值

		boolean isFull = false;
		while (totalExp >= needExp) {
			totalExp -= needExp;

			levelTmp = dao.getLevelCfg(curLevel + 1);
			if (levelTmp == null) {
				isFull = true;
				curExp = needExp;// 当前经验等于满级经验
				break;
			}

			needExp = levelTmp.getNeedExp();
			curLevel++;
			curExp = totalExp;
		}

		if (!isFull) {
			curExp = totalExp;
		}

		long now = System.currentTimeMillis();

		groupData.setGroupLevel(curLevel);
		groupData.setGroupExp(curExp);

		if (oldLevel != curLevel) {// 如果两个等级不一样
			groupData.setToLevelTime(now);
			if (logMgr != null) {
				GroupLog log = new GroupLog();
				log.setLogType(GroupLogType.GROUP_UPGRADE_VALUE);
				log.setTime(now);
				log.setGroupLevel(curLevel);
				logMgr.addLog(player, log);
			}
		}

		// 更新下排行榜的扩展属性
		GroupRankHelper.updateTheTypeForGroupRankExtension(groupData.getId());
		return oldLevel != curLevel;
	}

	/**
	 * 检查技能是否能被学习
	 * 
	 * @param studySkillId 要学习的帮派技能Id
	 * @param studySkillLevel 要学习的帮派技能等级
	 * @return
	 */
	public boolean checkGroupSkillCanStudy(int studySkillId, int studySkillLevel) {
		GroupBaseData groupData = groupBaseDataHolder.getGroupData();
		if (groupData == null) {
			return false;
		}

		return groupData.checkHasResearchedSkill(studySkillId, studySkillLevel);
	}

	/**
	 * 检查或者重置数据
	 * 
	 * @return
	 */
	public synchronized boolean checkOrResetGroupDayExpAndSupplyLimit() {
		GroupBaseData groupData = groupBaseDataHolder.getGroupData();
		if (groupData == null) {
			return false;
		}

		if (!DateUtils.isResetTime(5, 0, 0, groupData.getUpdateLimitTime())) {
			return false;
		}

		groupData.setDayExp(0);
		groupData.setDaySupplies(0);
		groupData.setUpdateLimitTime(System.currentTimeMillis());
		updateAndSynGroupData(null);
		return true;
	}

	/**
	 * 更新帮派基础的数据
	 * 
	 * @param player
	 */
	public void updateAndSynGroupData(Player player) {
		flush();
		groupBaseDataHolder.incrementGroupDataVersion();
		if (player != null) {
			groupBaseDataHolder.synGroupData(player, -1);
		}
	}

	/**
	 * 更新帮派技能的数据
	 * 
	 * @param player
	 */
	public void updateAndSynGroupSkillData(Player player) {
		flush();
		groupBaseDataHolder.incrementGroupSkillVersion();
		if (player != null) {
			groupBaseDataHolder.synGroupSkillData(player, -1);
		}
	}

	/**
	 * 刷新帮派数据
	 */
	public void flush() {
		groupBaseDataHolder.updateGroupData2DB();
	}

	/**
	 * 同步帮派技能数据
	 * 
	 * @param player
	 * @param version
	 */
	public void synGroupSkillData(Player player, int version) {
		groupBaseDataHolder.synGroupSkillData(player, version);
	}

	/**
	 * 同步帮派基础数据
	 * 
	 * @param player
	 * @param version
	 */
	public void synGroupData(Player player, int version) {
		groupBaseDataHolder.synGroupData(player, version);
	}

	public void setGroupSupplier(int s) {
		groupBaseDataHolder.setGroupSupplier(s);
		
	}
}