package com.rw.service.log.infoPojo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.bm.login.AccoutBM;
import com.playerdata.Player;
import com.playerdata.UserDataMgr;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.user.accountInfo.TableAccount;

public class RoleGameInfo {

	private String userId;

	private Integer vip;

	private Integer level;
	// 升级前等级，防止跳级频繁
	private Integer levelBeforeUp;

	// 银汉需求mapid，其实为levelid
	private Integer mapid;

	// 局次
	private Integer GamesCode;

	// 战力
	private Integer fighting;

	private Integer careerType;

	private String factionId;
	private String roleCreatedTime;

	private String userCreatedTime;

	// private String onlineTime;

	// 统计信息
	private String statInfo;
	/** 副本统计信息 */
	private String copyInfo;
	/** 活动统计信息 */
	private String activityInfo;
	/** 任务统计信息 */
	private String taskInfo;

	// 在线时长
	private String onlineTime;

	private Integer allfighting;

	public static final int Copy_Normal = 1;// 银汉识别普通本为1

	final private static Field[] fieldList;

	static {
		fieldList = RoleGameInfo.class.getDeclaredFields();
		for (Field field : fieldList) {
			field.setAccessible(true);
		}
	}

	public Map<String, String> getInfoMap() throws Exception {
		Map<String, String> infoMap = new HashMap<String, String>();
		for (Field field : fieldList) {
			try {
				Object value = field.get(this);
				if (value != null) {
					infoMap.put(field.getName(), value.toString());
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return infoMap;
	}

	public static RoleGameInfo fromPlayer(Player player, Map<String, String> moreinfo) {

		RoleGameInfo roleGameInfo = new RoleGameInfo();
		try {
			String playerId = player.getUserId();
			roleGameInfo.setUserId(playerId);
			roleGameInfo.setVip(player.getVip());
			roleGameInfo.setLevel(player.getLevel());
			// roleGameInfo.setFighting(player.getHeroMgr().getFightingTeam());
			// roleGameInfo.setAllfighting(player.getHeroMgr().getFightingAll());
			roleGameInfo.setFighting(player.getHeroMgr().getFightingTeam(player));
			roleGameInfo.setAllfighting(player.getHeroMgr().getFightingAll(player));
			roleGameInfo.setCareerType(player.getCareer());

			UserGroupAttributeDataMgr mgr = UserGroupAttributeDataMgr.getMgr();
			UserGroupAttributeDataIF baseData = mgr.getUserGroupAttributeData(playerId);
			if (baseData != null) {
				// 新创建角色没有帮派，所以要加这个判断
				String groupId = baseData.getGroupId();
				if (!StringUtils.isEmpty(groupId)) {
					roleGameInfo.setFactionId(groupId);
				}
			}

			UserDataMgr userDataMgr = player.getUserDataMgr();
			long roleCreatedTime = userDataMgr.getCreateTime();

			if (roleCreatedTime > 0) {
				roleGameInfo.setRoleCreatedTime(DateUtils.getDateTimeFormatString(roleCreatedTime, "yyyy-MM-dd HH:mm:ss"));
			}
			AccoutBM accountBM = AccoutBM.getInstance();
			String account = userDataMgr.getAccount();
			TableAccount userAccount = accountBM.getByAccountId(account);
			if (userAccount != null) {
				long userCreateTime = userAccount.getRegisterTime();
				if (userCreateTime > 0) {
					roleGameInfo.setUserCreatedTime(DateUtils.getDateTimeFormatString(userCreateTime, "yyyy-MM-dd HH:mm:ss"));
				}
				if (userDataMgr.getZoneLoginInfo() != null) {
					logout(player, roleGameInfo, moreinfo);
					logcopy(player, roleGameInfo, moreinfo);
					logactivity(player, roleGameInfo, moreinfo);
					logtask(player, roleGameInfo, moreinfo);
				}
			}

			// if(player.getCopyRecordMgr().getCalculateState() != null){
			// roleGameInfo.setMapId(player.getCopyRecordMgr().getCalculateState().getLastBattleId());
			// }
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return roleGameInfo;
	}

	private static void logtask(Player player, RoleGameInfo roleGameInfo, Map<String, String> moreinfo) {
		String rewardsinfotask = "";
		if (moreinfo != null) {
			if (moreinfo.containsKey("rewardsinfotask")) {
				rewardsinfotask = moreinfo.get("rewardsinfotask");
			}
		}
		StringBuilder taskInfo = new StringBuilder();
		taskInfo.append("task_time:").append("").append("#").append("task_reward:").append(rewardsinfotask);
		roleGameInfo.setTaskInfo(taskInfo.toString());

	}

	private static void logactivity(Player player, RoleGameInfo roleGameInfo, Map<String, String> moreinfo) {
		String activityTime = "";
		String rewardsinfoactivity = "";
		if (moreinfo != null) {
			if (moreinfo.containsKey("activityTime")) {
				activityTime = moreinfo.get("activityTime");
			}
			if (moreinfo.containsKey("rewardsinfoactivity")) {
				rewardsinfoactivity = moreinfo.get("rewardsinfoactivity");
			}
		}
		StringBuilder activityInfo = new StringBuilder();
		activityInfo.append("activity_time:").append(activityTime).append("#").append("activity_reward:").append(rewardsinfoactivity);
		roleGameInfo.setActivityInfo(activityInfo.toString());

	}

	private static void logcopy(Player player, RoleGameInfo roleGameInfo, Map<String, String> moreinfo) {
		String spcase = "0";
		String nmcase = "0";
		String fighttime = "";
		String rewardsinfocopy = "";
		if (moreinfo != null) {
			if (moreinfo.containsKey("fightTime")) {
				fighttime = moreinfo.get("fightTime");
			}
			if (moreinfo.containsKey("sp_case")) {
				spcase = moreinfo.get("sp_case");
			}
			if (moreinfo.containsKey("nm_case")) {
				nmcase = moreinfo.get("nm_case");
			}
			if (moreinfo.containsKey("rewardsinfocopy")) {
				rewardsinfocopy = moreinfo.get("rewardsinfocopy");
			}
		}
		StringBuilder copyInfo = new StringBuilder();
		copyInfo.append("fight_time:").append(fighttime).append("#").append("sp_case:").append(spcase).append("#").append("nm_case:").append(nmcase).append("#").append("copy_rewards:").append(rewardsinfocopy);
		roleGameInfo.setCopyInfo(copyInfo.toString());

	}

	private static void logout(Player player, RoleGameInfo roleGameInfo, Map<String, String> moreinfo) {
		UserDataMgr userDataMgr = player.getUserDataMgr();
		long onlineTime = (System.currentTimeMillis() - userDataMgr.getZoneLoginInfo().getLoginZoneTime()) / 1000;
		StringBuilder statInfo = new StringBuilder();
		int giftGold = player.getUserGameDataMgr().getGiftGold();
		int chargeGold = player.getUserGameDataMgr().getChargeGold();
		long coin = player.getUserGameDataMgr().getCoin();
		String spcase = "0";
		String nmcase = "0";
		boolean isroleout = false;
		if (moreinfo != null) {
			if (moreinfo.containsKey("sp_case")) {
				spcase = moreinfo.get("sp_case");
				isroleout = true;
			}
			if (moreinfo.containsKey("nm_case")) {
				nmcase = moreinfo.get("nm_case");
			}
		}
		String gang = roleGameInfo.getFactionId();
		if (org.apache.commons.lang3.StringUtils.isBlank(gang)) {
			gang = "0";
		}
		statInfo.append("online_time:").append(onlineTime).append("#").append("main_coin:").append(chargeGold).append("#").append("gift_coin:").append(giftGold).append("#").append("sub_coin:").append(coin);
		if (isroleout) {
			statInfo.append("#").append("sp_case:").append(spcase).append("#").append("nm_case:").append(nmcase).append("#").append("gang:").append(gang);
		}

		roleGameInfo.setStatInfo(statInfo.toString());
		roleGameInfo.setOnlineTime("online_time:" + onlineTime);
	}

	public String getFactionId() {
		return factionId;
	}

	public void setFactionId(String factionId) {
		this.factionId = factionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getVip() {
		return vip;
	}

	public void setVip(Integer vip) {
		this.vip = vip;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getLevelBeforeUp() {
		return levelBeforeUp;
	}

	public void setLevelBeforeUp(Integer levelBeforeUp) {
		this.levelBeforeUp = levelBeforeUp;
	}

	public Integer getMapId() {
		return mapid;
	}

	public void setMapId(Integer mapid) {
		this.mapid = mapid;
	}

	public Integer getGamesCode() {
		return GamesCode;
	}

	public void setGamesCode(Integer gamescode) {
		this.GamesCode = gamescode;
	}

	public Integer getFighting() {
		return fighting;
	}

	public void setFighting(Integer fighting) {
		this.fighting = fighting;
	}

	public Integer getCareerType() {
		return careerType;
	}

	public void setCareerType(Integer careerType) {
		this.careerType = careerType;
	}

	public String getRoleCreatedTime() {
		return roleCreatedTime;
	}

	public void setRoleCreatedTime(String roleCreatedTime) {
		this.roleCreatedTime = roleCreatedTime;
	}

	public String getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(String onlineTime) {
		this.onlineTime = onlineTime;
	}

	public String getStatInfo() {
		return statInfo;
	}

	public void setStatInfo(String statInfo) {
		this.statInfo = statInfo;
	}

	public String getUserCreatedTime() {
		return userCreatedTime;
	}

	public void setUserCreatedTime(String userCreatedTime) {
		this.userCreatedTime = userCreatedTime;
	}

	public String getCopyInfo() {
		return copyInfo;
	}

	public void setCopyInfo(String copyInfo) {
		this.copyInfo = copyInfo;
	}

	public String getActivityInfo() {
		return activityInfo;
	}

	public void setActivityInfo(String activityInfo) {
		this.activityInfo = activityInfo;
	}

	public String getTaskInfo() {
		return taskInfo;
	}

	public void setTaskInfo(String taskInfo) {
		this.taskInfo = taskInfo;
	}

	public Integer getAllfighting() {
		return allfighting;
	}

	public void setAllfighting(Integer allfighting) {
		this.allfighting = allfighting;
	}

}
