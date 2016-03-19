package com.playerdata.guild;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.guild.GuildGTSMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.PlayerIF;
import com.rwbase.dao.gulid.CfgGuildDAO;
import com.rwbase.dao.gulid.GuildCfg;
import com.rwbase.dao.gulid.faction.Guild;
import com.rwbase.dao.gulid.faction.GuildApplyInfo;
import com.rwbase.dao.gulid.faction.GuildMember;
import com.rwbase.dao.gulid.faction.GuildMemberHolder;
import com.rwbase.dao.gulid.faction.GuildPositionType;
import com.rwbase.dao.gulid.faction.GuildPrivacyType;
import com.rwbase.dao.gulid.faction.GuildSetting;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;


/**
 * 帮派成员控制，需要做多线程保护
 * @author Allen
 *
 */
public class GuildMemberTSMgr {
	
	private String guildId;

	public GuildMemberTSMgr(String guildId) {
		this.guildId = guildId;
	}
	
	private boolean isMember(Player player){
		String playerGuildId = player.getGuildUserMgr().getGuildId();
		return StringUtils.equals(guildId, playerGuildId);
	}
	
	private boolean isMaster(Player player){
		return GuildGTSMgr.getInstance().getById(guildId).getGuildMemberHolder().isMaster(player.getUserId());	
	}
	
	
	private boolean isSlaveMaster(Player player){
		return GuildGTSMgr.getInstance().getById(guildId).getGuildMemberHolder().isSlaveMaster(player.getUserId());	
	}
	

	private GuildDataMgr getGuildDataMgr() {
		return GuildGTSMgr.getInstance().getById(guildId);
	}
	

	/*** 申请 **/
	public synchronized GuildApplyInfo apply(Player player) {

		String oldGuildId = player.getGuildUserMgr().getGuildId();
		GuildDataMgr guildDataMgr = GuildGTSMgr.getInstance().getById(oldGuildId);

		if (guildDataMgr != null) {
			player.NotifyCommonMsg("你已有帮派");
			return null;
		}

		if (!player.getGuildUserMgr().isJoinTimeOk()) {
			player.NotifyCommonMsg("退帮后24小时内不能加入帮派");
			return null;
		}

		GuildDataMgr currentGuildDataMgr = getGuildDataMgr();
		if (currentGuildDataMgr == null) {
			player.NotifyCommonMsg("找不到帮派");
			return null;
		}

		if (currentGuildDataMgr.getGuildApplyInfoHolder().hasApply(player.getUserId())) {
			player.NotifyCommonMsg("已申请过了");
			return null;
		}

		if (player.getGuildUserMgr().getApplyCount() >= 5) {
			player.NotifyCommonMsg("最多同时申请5个帮派");
			return null;
		}

		Guild guild = currentGuildDataMgr.getGuildHolder().get();
		GuildSetting guildSetting = guild.getGuildSetting();
		if (player.getLevel() < guildSetting.getJoinLevel()) {
			player.NotifyCommonMsg("战队等级不足");
			return null;
		}

		GuildPrivacyType privacyType = guildSetting.getPrivacyType();
		if (privacyType == GuildPrivacyType.PRIVATE) {
			player.NotifyCommonMsg("帮派设置为不可加入");
			return null;
		}

		if (privacyType == GuildPrivacyType.NEED_CONFIRM) {

			GuildApplyInfo guildApplyInfo = new GuildApplyInfo();
			guildApplyInfo.setApplyTime(System.currentTimeMillis());
			guildApplyInfo.setPlayerName(player.getUserName());
			guildApplyInfo.setPlayerId(player.getUserId());
			guildApplyInfo.setLevel(player.getLevel());
			guildApplyInfo.setIconId(player.getHeadImage());

			boolean applySuccess = currentGuildDataMgr.getGuildApplyInfoHolder().addItem(guildApplyInfo);
			if (applySuccess) {
				return guildApplyInfo;

			} else {
				player.NotifyCommonMsg("申请失败，请稍后尝试。");
				return null;
			}

		} else {

			if (guild.getDismissTime() > 0) {
				player.NotifyCommonMsg("帮派解散状态中不能加入");
				return null;
			}
			joinGuild(player);

		}

		return null;

	}

	private boolean joinGuild(Player player) {

		GuildDataMgr guildDataHoder = getGuildDataMgr();
		
		GuildMember guildMember = new GuildMember();
		guildMember.setPosition(GuildPositionType.MEMBER);
		guildMember.setJoinTime(System.currentTimeMillis());
		guildMember.setUserId(player.getUserId());
		guildMember.setPlayerName(player.getUserName());
		guildMember.setIconId(player.getHeadImage());
		guildMember.setLevel(player.getLevel());
		guildMember.setLoginTime(player.getUserGameDataMgr().getLastLoginTime());
		guildMember.setJoinTime(System.currentTimeMillis());

		Guild guild = guildDataHoder.getGuildHolder().get();
		GuildCfg guildCfg = (GuildCfg) CfgGuildDAO.getInstance().getCfgById(guild.getLevel() + "");
		boolean addMemSuccess = guildDataHoder.getGuildMemberHolder().addItem(guildMember, guildCfg);
		if (!addMemSuccess) {
			player.NotifyCommonMsg("帮派创建失败，请稍后尝试。");
		} else {
			player.getGuildUserMgr().setGuildId(guild.getId());
			player.getGuildUserMgr().setGuildName(guild.getGuildSetting().getName());
			guildDataHoder.getGuildLogInfoHolder().addLog(player, guild, 1, "", "");
		}

		return addMemSuccess;
	}

	/*****/
	private boolean isPositionSizeFull(Player player, int position, GuildDataMgr guildDataHoder) {

		Guild guild = guildDataHoder.getGuildHolder().get();
		GuildCfg GuildCfg = (GuildCfg) CfgGuildDAO.getInstance().getCfgById(guild.getLevel() + "");
		int size = 0;
		int maxSize = 2;
		if (GuildCfg != null) {
			if (position == 2) {
				maxSize = GuildCfg.position2;
			} else if (position == 3) {
				maxSize = GuildCfg.position3;
			} else {
				maxSize = 100;
			}

		}

		List<GuildMember> guildMemberList = guildDataHoder.getGuildMemberHolder().getItemList();

		for (GuildMember guildMember : guildMemberList) {
			if (guildMember.getPosition().ordinal() == position) {
				size++;
			}

		}

		if (size >= maxSize) {
			player.NotifyCommonMsg("职位已满");
			return true;
		}

		return false;
	}

	/*** 忽略 **/
	public synchronized boolean ignore(Player player, String applyUserId) {
		
		
		
		GuildDataMgr guildDataHoder = getGuildDataMgr();
		PlayerIF applyUser = PlayerMgr.getInstance().getReadOnlyPlayer(applyUserId);
		if (applyUser == null) {
			player.NotifyCommonMsg("申请玩家不存在.");
			return false;
		}
		
		boolean hasRight = checkOperationRight(player);
		if (!hasRight) {
			// 帮主和副帮主才能审核申请。
			player.NotifyCommonMsg("帮主和副帮主才能审核申请。");
			return false;
		}


		GuildApplyInfo guildApplyInfo = guildDataHoder.getGuildApplyInfoHolder().getItem(applyUserId);
		if (guildApplyInfo == null) {
			player.NotifyCommonMsg("申请已失效，请从新申请。");
			return false;
		}

		boolean removeSuccess = guildDataHoder.getGuildApplyInfoHolder().removeItem(guildApplyInfo);

		if (removeSuccess) {

			if (applyUser != null) {
				Guild guild = guildDataHoder.getGuildHolder().get();
				String title = "帮派申请结果";
				String content = "对不起，您没有通过【" + guild.getGuildSetting().getName() + "】的申请，请选择其它帮派加入。";
				String sendName = "系统";
				GuildMailHelper.sendEmail(applyUser, title, content, sendName);
			}
		}

		return removeSuccess;
	}

	/*** 通过 **/
	public synchronized boolean pass(Player player, String applyUserId) {
		GuildDataMgr guildDataHoder = getGuildDataMgr();
		PlayerIF applyUser = PlayerMgr.getInstance().getReadOnlyPlayer(applyUserId);
		if (applyUser == null) {
			player.NotifyCommonMsg("申请玩家不存在.");
			return false;
		}

		boolean hasRight = checkOperationRight(player);
		if (!hasRight) {
			// 帮主和副帮主才能审核申请。
			player.NotifyCommonMsg("帮主和副帮主才能审核申请。");
			return false;
		}

		Guild guild = guildDataHoder.getGuildHolder().get();
		if (guild == null) {
			player.NotifyCommonMsg("帮派已解散。");
			return false;
		}

		if (guild.getDismissTime() > 0) {
			player.NotifyCommonMsg("帮派解散状态中不能操作。");
			return false;
		}

		GuildApplyInfo guildApplyInfo = guildDataHoder.getGuildApplyInfoHolder().getItem(applyUserId);
		if (guildApplyInfo == null) {
			player.NotifyCommonMsg("申请已失效，请从新申请。");
			return false;
		}
		boolean removeSuccess = guildDataHoder.getGuildApplyInfoHolder().removeItem(guildApplyInfo);

		boolean joinSuccess = false;
		if (removeSuccess) {
			if (StringUtils.isNotBlank(applyUser.getGuildUserMgr().getGuildId())) {
				player.NotifyCommonMsg("【" + applyUser.getTableUser().getUserId() + "】已加入别的帮派");
				return false;
			} else {
				joinSuccess = joinGuild(player);
			}

		}

		return joinSuccess;
	}
	

	private boolean checkOperationRight(Player player) {
		boolean hasRight = isMaster(player)||isSlaveMaster(player);
		return hasRight;
	}



	public synchronized void exitGuild(Player player) {
		
		GuildDataMgr guildDataHoder = getGuildDataMgr();
		Guild guild = guildDataHoder.getGuildHolder().get();
		if (guild == null) {
			return;
		}

		GuildMemberHolder guildMemberHolder = guildDataHoder.getGuildMemberHolder();
		GuildMember guildMember = guildMemberHolder.getItem(player.getUserId());
		if (guildMember == null) {
			return;
		}
		boolean removeSuccess = guildMemberHolder.removeItem(guildMember);
		if (removeSuccess) {
			player.getGuildUserMgr().cleanWhenExit();
			player.NotifyCommonMsg("退出成功");
		}

	}

	public synchronized boolean kick(Player player, String targetUserId) {
		boolean hasRight = checkOperationRight(player);
		if (!hasRight) {
			// 帮主和副帮主才能审核申请。
			player.NotifyCommonMsg("帮主和副帮主才能审核申请.");
			return false;
		}
		GuildDataMgr guildDataHoder = getGuildDataMgr();
		Guild guild = guildDataHoder.getGuildHolder().get();
		if (guild == null) {
			player.NotifyCommonMsg("帮派已解散.");
			return false;
		}
		if (player.getUserId() == targetUserId) {
			player.NotifyCommonMsg("不能踢自己.");
			return false;
		}

		GuildMemberHolder guildMemberHolder = guildDataHoder.getGuildMemberHolder();
		GuildMember targetMember = guildMemberHolder.getItem(targetUserId);

		boolean removeSuccess = guildMemberHolder.removeItem(targetMember);
		if (removeSuccess) {
			GameWorldFactory.getGameWorld().asyncExecute(targetUserId, new PlayerTask() {
				@Override
				public void run(Player player) {
					player.getGuildUserMgr().cleanWhenExit();
				}
			});

			player.NotifyCommonMsg("操作成功.");
		}

		return removeSuccess;

	}

	/*** 转让 **/
	public synchronized boolean assignment(Player player, String targetUserId) {
		GuildDataMgr guildDataHolder = getGuildDataMgr();
		if(!commonCheck(player, guildDataHolder)){
			return false;
		}

		
		GuildMemberHolder guildMemberHolder = guildDataHolder.getGuildMemberHolder();
		GuildMember selfMember = guildMemberHolder.getItem(player.getUserId());
		if (!isMaster(player)) {
			player.NotifyCommonMsg("你不是帮主");
			return false;
		}

		
		GuildMember targetMember = guildMemberHolder.getItem(player.getUserId());
		if(targetMember == null){
			player.NotifyCommonMsg("目标用户不是帮派成员。");
			return false;
		}
		
		selfMember.setPosition(targetMember.getPosition());
		guildMemberHolder.updateItem(selfMember);
		targetMember.setPosition(GuildPositionType.MASTER);
		guildMemberHolder.updateItem(targetMember);
		return true;
	}

	private boolean commonCheck(Player player, GuildDataMgr guildDataHolder) {
		if (guildDataHolder == null) {
			player.NotifyCommonMsg("你还没有参加任何帮派。");
			return false;
		}
		
		if (guildDataHolder.getGuildHolder().get().getDismissTime() > 0) {
			player.NotifyCommonMsg("帮派解散状态中不能操作.");
			return false;
		}
		return true;
	}

	/*** 升职 **/
	public synchronized boolean promote(Player player, String targetUserId) {
		
		if (!isMember(player)) {
			player.NotifyCommonMsg("你不是该帮派成员.");
			return false;
		}
		if (player.getUserId() == targetUserId) {
			player.NotifyCommonMsg("不能对自己执行该操作.");
			return false;
		}
		
		GuildDataMgr guildDataMgr = getGuildDataMgr();
		
		if(!commonCheck(player, guildDataMgr)){
			return false;
		}

		GuildMemberHolder guildMemberHolder = guildDataMgr.getGuildMemberHolder();
		
		GuildMember selfMember = guildMemberHolder.getItem(player.getUserId());		
		GuildMember targetMember = guildMemberHolder.getItem(player.getUserId());
		if(targetMember == null){
			player.NotifyCommonMsg("目标用户不是帮派成员。");
			return false;
		}
		

		
		//帮助和副帮主才可以做该操作
		if (selfMember.getPosition() != GuildPositionType.MASTER || selfMember.getPosition() != GuildPositionType.SLAVE_MASTER) {
			player.NotifyCommonMsg("权限不足");
			return false;
		}

		if (selfMember.getPosition().ordinal() <= targetMember.getPosition().ordinal()) {
			player.NotifyCommonMsg("权限不足");
			return false;
		}

		if (isPositionSizeFull(player, targetMember.getPosition().ordinal() - 1, guildDataMgr)) {
			return false;
		}

		GuildPositionType newPosition = GuildPositionType.valueOf(targetMember.getPosition().ordinal() - 1);
		targetMember.setPosition(newPosition);
		guildMemberHolder.updateItem(targetMember);
		
		player.NotifyCommonMsg("操作成功");
		return true;
	}


	/*** 将职 **/
	public synchronized boolean demote(Player player, String targetUserId) {

		if (player.getUserId() == targetUserId) {
			player.NotifyCommonMsg("不能对自己执行该操作.");
			return false;
		}
		
		GuildDataMgr guildDataMgr = getGuildDataMgr();
		
		if(!commonCheck(player, guildDataMgr)){
			return false;
		}

		GuildMemberHolder guildMemberHolder = guildDataMgr.getGuildMemberHolder();
		
		GuildMember selfMember = guildMemberHolder.getItem(player.getUserId());		
		GuildMember targetMember = guildMemberHolder.getItem(player.getUserId());
		if(targetMember == null){
			player.NotifyCommonMsg("目标用户不是帮派成员。");
			return false;
		}
		

		
		//帮助和副帮主才可以做该操作
		if (selfMember.getPosition() != GuildPositionType.MASTER || selfMember.getPosition() != GuildPositionType.SLAVE_MASTER) {
			player.NotifyCommonMsg("权限不足");
			return false;
		}

		if (selfMember.getPosition().ordinal() <= targetMember.getPosition().ordinal()) {
			player.NotifyCommonMsg("权限不足");
			return false;
		}

		if (isPositionSizeFull(player, targetMember.getPosition().ordinal() - 1, guildDataMgr)) {
			return false;
		}

		GuildPositionType newPosition = GuildPositionType.valueOf(targetMember.getPosition().ordinal() - 1);
		if(newPosition != null){
			targetMember.setPosition(newPosition);
			guildMemberHolder.updateItem(targetMember);
			player.NotifyCommonMsg("操作成功");
			return true;
		}else{
			player.NotifyCommonMsg("操作失败");
			return false;
		}
	}

	public synchronized void switchMaster(GuildMember master, GuildMember assignmentMember) {
		
		GuildDataMgr guildDataMgr = getGuildDataMgr();
		GuildMemberHolder guildMemberHolder = guildDataMgr.getGuildMemberHolder();
		
		assignmentMember.setPosition(GuildPositionType.MASTER);
		master.setPosition(GuildPositionType.MEMBER);
		guildMemberHolder.updateItem(assignmentMember);
		guildMemberHolder.updateItem(master);
	}

}
