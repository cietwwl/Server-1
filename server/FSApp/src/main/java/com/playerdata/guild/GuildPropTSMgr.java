package com.playerdata.guild;

import org.apache.commons.lang3.StringUtils;

import com.bm.guild.GuildGTSMgr;
import com.playerdata.Player;
import com.rwbase.dao.gulid.CfgGuildLevelDAO;
import com.rwbase.dao.gulid.faction.Guild;
import com.rwbase.dao.gulid.faction.GuildMember;
import com.rwbase.dao.gulid.faction.GuildMemberHolder;



/**
 * 帮派等级等需要做线程安全保护的数据改动操作
 * 
 * @author Allen
 *
 */
public class GuildPropTSMgr {
	
	private String guildId;

	public GuildPropTSMgr(String guildId) {
		this.guildId = guildId;
	}

	/*** 解散 **/
	public synchronized boolean dismiss(Player player) {
		if(isMaster(player)){
			player.NotifyCommonMsg("你不是帮主");
			return false;
		}
		
		
		String guildId = player.getGuildUserMgr().getGuildId();
		GuildDataMgr guildDataHolder = getById(guildId);

		Guild guild = guildDataHolder.getGuildHolder().get();
		boolean opSuccess = false;
		if (guild.getDismissTime() <= 0) {
			if (guild.getDismissCount() > 0 && System.currentTimeMillis() - guild.getDismissTime() < 24 * 3600 * 1000) {
				player.NotifyCommonMsg("24小时内只能解散一次");
				return false;
			}
			guild.setDismissTime(System.currentTimeMillis());
			guild.setDismissCount(1);
			player.NotifyCommonMsg("开始解散倒计时");
			GuildMailHelper.sendAllGuildMemberEmail(guildDataHolder, "解散帮派", "您的帮派已被帮主【" + player.getUserName() + "】解散。24小时后将自动退出该帮派。", "");
			opSuccess = true;
		}

		return opSuccess;

	}
	
	private boolean isMember(Player player){
		String playerGuildId = player.getGuildUserMgr().getGuildId();
		return StringUtils.equals(guildId, playerGuildId);
	}
	
	private boolean isMaster(Player player){
		return GuildGTSMgr.getInstance().getById(guildId).getGuildMemberHolder().isMaster(player.getUserId());	
	}

	private GuildDataMgr getById(String guildId) {
		return GuildGTSMgr.getInstance().getById(guildId);
	}

	/*** 取消解散 **/
	public synchronized boolean cancelDismiss(Player player) {
		if(isMaster(player)){
			player.NotifyCommonMsg("你不是帮主");
			return false;
		}
		
		String guildId = player.getGuildUserMgr().getGuildId();
		GuildDataMgr guildDataHolder = getById(guildId);


		boolean opSuccess = false;
	
		Guild guild = guildDataHolder.getGuildHolder().get();

		if (guild.getDismissTime() > 0) {

			guild.setDismissTime(0);
			guild.setDismissCount(0);
			player.NotifyCommonMsg("取消解散倒计时");
			opSuccess = true;
		}
	

		return opSuccess;

	}



	/*** 帮派玩家每消耗1点体力值，帮派就获得1点活跃值。 **/
	public synchronized boolean addActiveValue(Player player, int addCount) {
		if(isMember(player)){
			player.NotifyCommonMsg("你不是帮派成员");
			return false;
		}
		
		
		String guildId = player.getGuildUserMgr().getGuildId();
		GuildDataMgr guildDataHolder = getById(guildId);
		boolean success = false;
		if (guildDataHolder != null) {
			guildDataHolder.getGuildHolder().addActivity(addCount);
			success = true;
		}
		return success;

	}

	

	// 捐献
	public synchronized boolean donate(Player player, int num) {
		if(isMember(player)){
			player.NotifyCommonMsg("你不是帮派成员");
			return false;
		}

		if (num <= 0) {
			return false;
		}
		String guildId = player.getGuildUserMgr().getGuildId();
		GuildDataMgr guildDataHolder = getById(guildId);
		
		if (guildDataHolder == null) {
			return false;
		}

		GuildMemberHolder guildMemberHolder = guildDataHolder.getGuildMemberHolder();
		GuildMember guildMember = guildMemberHolder.getItem(player.getUserId());
		if (guildMember == null) {
			return false;
		}

		if (num > player.getUserGameDataMgr().getCoin()) {
			player.NotifyCommonMsg("金币不足");
			return false;
		}

		if (guildMember.isDonate()) {
			player.NotifyCommonMsg("今天已捐献过了");
			return false;
		}
		boolean costSuccess = (player.getUserGameDataMgr().addCoin(-num)==0);

		if(costSuccess){
			guildMember.setDonate(true);
			int donateNum = num / 1000;
			player.getGuildUserMgr().addGuildCoin(donateNum);
			guildMember.setTotalContribute(guildMember.getTotalContribute() + donateNum);
			guildMember.setContribute(guildMember.getContribute() + donateNum);
			guildMemberHolder.updateItem(guildMember);
			guildDataHolder.getGuildHolder().addContribute(donateNum);
			player.NotifyCommonMsg("捐献成功");
			return true;
		}else{
			player.NotifyCommonMsg("金币扣除失败，请稍后尝试.");
			return false;
		}

	}

	public synchronized boolean gmAdd(Player player, int type) {
		String guildId = player.getGuildUserMgr().getGuildId();
		GuildDataMgr guildDataHolder = getById(guildId);
		
		if (guildDataHolder == null) {
			return false;
		}
		
		if (type == 1) {
			int donateNum = 10000;

			GuildMemberHolder guildMemberHolder = guildDataHolder.getGuildMemberHolder();
			GuildMember guildMember = guildMemberHolder.getItem(player.getUserId());
			if (guildMember == null) {
				return false;
			}
			guildMember.setDonate(true);
			player.getGuildUserMgr().addGuildCoin(donateNum);
			guildMember.setTotalContribute(guildMember.getTotalContribute() + donateNum);
			guildMember.setContribute(guildMember.getContribute() + donateNum);
			guildMemberHolder.updateItem(guildMember);
			guildDataHolder.getGuildHolder().addContribute(donateNum);
			player.NotifyCommonMsg("捐献成功");
			return true;
		}
		return false;

	}
	
	// 升级
	public synchronized boolean upgrade(Player player) {
		if(isMaster(player)){
			player.NotifyCommonMsg("你不是帮主");
			return false;
		}
		
		String guildId = player.getGuildUserMgr().getGuildId();
		GuildDataMgr guildDataHolder = getById(guildId);
		
		if (guildDataHolder == null || !guildDataHolder.getGuildMemberHolder().isMaster(player.getUserId())) {
			return false;
		}

		Guild guild = guildDataHolder.getGuildHolder().get();

		int guildFunType = 1;
		int currentLevel = guild.getLevel();
		int cost = CfgGuildLevelDAO.getInstance().getUpNum(guildFunType, currentLevel);

		if (cost > guild.getContribute()) {
			player.NotifyCommonMsg("帮派贡献不足");
			return false;
		}

		if (currentLevel >= 7) {
			player.NotifyCommonMsg("已是最高级");
			return false;
		}
		
		guildDataHolder.getGuildHolder().setLevel(currentLevel+1);
		guildDataHolder.getGuildHolder().addContribute(-cost);
		player.NotifyCommonMsg("升级成功");
		player.getStoreMgr().AddStore();
		return true;
	}
	


}
