package com.bm.group;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.druid.pool.DruidDataSource;
import com.bm.chat.ChatBM;
import com.bm.groupCopy.GroupCopyLevelBL;
import com.playerdata.Player;
import com.rw.fsutil.cacheDao.IdentityIdGenerator;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.manager.GameManager;
import com.rw.service.Email.EmailUtils;
import com.rw.service.group.helper.GroupRankHelper;
import com.rw.support.FriendSupportFactory;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailCfg;
import com.rwbase.dao.email.EmailCfgDAO;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupSkillLevelTemplate;
import com.rwbase.dao.group.pojo.cfg.dao.GroupSkillLevelCfgDAO;
import com.rwbase.dao.group.pojo.db.GroupBaseData;
import com.rwbase.dao.group.pojo.db.dao.GroupBaseDataDAO;
import com.rwbase.dao.group.pojo.db.dao.GroupLogDataDAO;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.GroupCommonProto.GroupPost;
import com.rwproto.GroupCommonProto.GroupState;

/*
 * @author HC
 * @date 2016年1月19日 下午3:23:11
 * @Description 帮派数据缓存的容器
 */
public class GroupBM {
	private static GroupBM instance = new GroupBM();

	public static GroupBM getInstance() {
		return instance;
	}

	protected GroupBM() {
	}

	private IdentityIdGenerator generator;// 生成帮派Id的产生类
	/** 常驻内存的帮派容器 */
	private ConcurrentHashMap<String, Group> cacheGroupDataMap = new ConcurrentHashMap<String, Group>();
	private GroupIdCache groupIdCache;

	public void init(String dsName, DruidDataSource dataSource) {
		groupIdCache = new GroupIdCache(dsName, dataSource);
	}

	public String getGroupId(String groupName) {
		return groupIdCache.getGroupId(groupName);
	}

	/**
	 * 检查帮派是否存在
	 * 
	 * @param groupId
	 * @return
	 */
	public boolean groupIsExist(String groupId) {
		return get(groupId) != null;
	}

	/**
	 * 查找帮派中有没有申请中的名字
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasName(String name) {
		if (cacheGroupDataMap.isEmpty()) {
			return false;
		}

		for (Entry<String, Group> e : cacheGroupDataMap.entrySet()) {
			Group value = e.getValue();
			if (value == null) {
				continue;
			}

			GroupBaseDataIF groupData = value.getGroupBaseDataMgr().getGroupData();
			if (groupData == null) {
				continue;
			}

			if (groupData.getGroupName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * <pre>
	 * 命中帮派数据，优先从内存中命中
	 * 如果内存中不能命中，再去数据库命中
	 * 如果数据库中都没有，就说明这个帮派没创建
	 * </pre>
	 * 
	 * @param groupId 要命中的帮派Id
	 * @return
	 */
	public Group get(String groupId) {
		if (groupId == null || groupId.isEmpty()) {
			return null;
		}
		Group group = cacheGroupDataMap.get(groupId);
		if (group != null) {// 内存中直接命中
			return group;
		}

		GroupBaseData groupData = GroupBaseDataDAO.getDAO().getGroupData(groupId);
		if (groupData == null) {// 连数据库中没有命中
			return null;
		}

		// 加载到缓存中
		group = new Group(groupId);// 直接加载帮派的成员列表到内存
		cacheGroupDataMap.putIfAbsent(groupId, group);

		// 检查是否要重置每日获取经验和物资上限
		group.getGroupBaseDataMgr().checkOrResetGroupDayExpAndSupplyLimit();
		return group;
	}

	/**
	 * 创建帮派
	 * 
	 * @param player 角色
	 * @param groupName 帮派的名字
	 * @param icon 帮派的图标
	 * @param defaultValidateType 默认的验证类型
	 * @param defaultApplyLevel 默认的验证通过等级
	 * @return
	 */
	public synchronized Group create(Player player, String groupName, String icon, int defaultValidateType, int defaultApplyLevel) {
		if (hasName(groupName)) {
			return null;
		}

		String newGroupId = newGroupId();
		long now = System.currentTimeMillis();
		// 帮派基础信息
		GroupBaseData groupData = new GroupBaseData();
		groupData.setId(newGroupId);
		groupData.setGroupName(groupName);
		groupData.setCreateTime(now);
		groupData.setGroupLevel(1);
		groupData.setIconId(icon);
		groupData.setToLevelTime(now);
		groupData.setValidateType(defaultValidateType);
		groupData.setApplyLevel(defaultApplyLevel);
		groupData.setCreateUserId(player.getUserId());
		groupData.setAnnouncement("");
		groupData.setDeclaration("");

		// 初始化帮派初始开启可以研发的技能列表
		GroupSkillLevelCfgDAO dao = GroupSkillLevelCfgDAO.getDAO();
		List<Integer> allSkillIdList = dao.getAllSkillIdList();
		for (int i = 0, size = allSkillIdList.size(); i < size; i++) {
			Integer skillId = allSkillIdList.get(i);

			List<GroupSkillLevelTemplate> skillTmpList = dao.getSkillLevelTmpList(skillId);
			for (int j = 0, tmpSize = skillTmpList.size(); j < tmpSize; j++) {
				GroupSkillLevelTemplate skillLevelTmp = skillTmpList.get(j);

				boolean canOpen = true;
				Map<Integer, Integer> researchCondation = skillLevelTmp.getResearchCondation();
				for (Entry<Integer, Integer> e : researchCondation.entrySet()) {
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

		GroupBaseDataDAO groupDao = GroupBaseDataDAO.getDAO();
		// TODO HC 现在不能捕获到数据库抛出的异常，只能当作这里就是出现了名字重复
		if (!groupDao.update(groupData)) {
			return null;
		}

		// 把排行榜放入到内存中
		Group group = new Group(newGroupId);
		if (cacheGroupDataMap.putIfAbsent(newGroupId, group) != null) {
			return null;
		}

		// 放入成员
		group.getGroupMemberMgr().addMemberData(player.getUserId(), newGroupId, player.getUserName(), player.getHeadImage(), player.getTemplateId(), player.getLevel(), player.getVip(), player.getCareer(), GroupPost.LEADER_VALUE, 0, now, now, false, player.getHeadFrame(),
				GroupCopyLevelBL.MAX_ALLOT_COUNT);

		return group;
	}

	/**
	 * 解散帮派
	 * 
	 * @param groupId
	 */
	public synchronized void dismiss(final String groupId) {
		Group group = get(groupId);
		if (group == null) {
			return;
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return;
		}

		// 不是解散状态
		if (groupData.getGroupState() != GroupState.DISOLUTION_VALUE) {
			return;
		}

		EmailCfg emailCfg = EmailCfgDAO.getInstance().getEmailCfg(GroupConst.DISMISS_GROUP_MAIL_ID);

		final long now = System.currentTimeMillis();
		// 删除帮派基础数据
		GroupBaseDataDAO.getDAO().delete(groupId);
		// 删除帮派成员
		GroupMemberMgr groupMemberMgr = group.getGroupMemberMgr();

		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm:ss");
		String time = sdf.format(new Date(now));
		String newContent = String.format(emailCfg.getContent(), groupData.getGroupName(), time);

		// 邮件内容
		final EmailData emailData = new EmailData();
		emailData.setTitle(emailCfg.getTitle());
		emailData.setContent(newContent);
		emailData.setDeleteType(EEmailDeleteType.valueOf(emailCfg.getDeleteType()));
		emailData.setDelayTime(emailCfg.getDelayTime());// 整个帮派邮件只保留7天
		emailData.setSender(emailCfg.getSender());

		// 成员任务
		PlayerTask memberPlayerTask = new PlayerTask() {

			@Override
			public void run(Player player) {
				player.getUserGroupAttributeDataMgr().updateDataWhenQuitGroup(player, now);
				EmailUtils.sendEmail(player.getUserId(), emailData);
				// 通知好友更改更新帮派名字
				FriendSupportFactory.getSupport().notifyFriendInfoChanged(player);
				// 清除所有的秘境消息
				ChatBM.getInstance().clearAllGroupSecretChatMessage(player.getUserId());
			}
		};

		// 申请成员任务
		PlayerTask applyPlayerTask = new PlayerTask() {

			@Override
			public void run(Player player) {
				player.getUserGroupAttributeDataMgr().updateDataWhenRefuseByGroup(player, groupId);
			}
		};

		// 移除所有的正式成员
		groupMemberMgr.removeAllMember(applyPlayerTask, memberPlayerTask);
		// 移除帮派日志
		GroupLogDataDAO.getDAO().delete(groupId);
		// 从各个排行榜中移除
		GroupRankHelper.getInstance().removeRanking(groupId);
	}

	/**
	 * 更新帮派数据
	 */
	public void flush() {
		for (Entry<String, Group> e : cacheGroupDataMap.entrySet()) {
			Group group = e.getValue();
			if (group == null) {
				continue;
			}

			group.flush();
		}
	}

	/**
	 * 新创建一个帮派Id
	 * 
	 * @return
	 */
	private String newGroupId() {
		if (generator == null) {
			DruidDataSource dataSource = SpringContextUtil.getBean("dataSourceMT");
			if (dataSource == null) {
				throw new ExceptionInInitializerError("获取dataSource失败");
			}
			generator = new IdentityIdGenerator("group_identifier", dataSource);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(GameManager.getServerId());
		sb.append(generator.generateId());
		return sb.toString();
	}

	/**
	 * 检查所有在线帮派的每日经验等限制
	 */
	public void checkOrAllGroupDayLimit() {
		for (Entry<String, Group> e : cacheGroupDataMap.entrySet()) {
			Group group = e.getValue();
			if (group == null) {
				continue;
			}

			group.getGroupBaseDataMgr().checkOrResetGroupDayExpAndSupplyLimit();
		}
	}
}