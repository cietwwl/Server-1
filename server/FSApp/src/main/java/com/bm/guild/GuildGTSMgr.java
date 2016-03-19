package com.bm.guild;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.GlobalDataMgr;
import com.playerdata.Player;
import com.playerdata.guild.GuildDataMgr;
import com.playerdata.guild.GuildMailHelper;
import com.rwbase.dao.gulid.CfgGuildDAO;
import com.rwbase.dao.gulid.GuildCfg;
import com.rwbase.dao.gulid.faction.Guild;
import com.rwbase.dao.gulid.faction.GuildDAO;
import com.rwbase.dao.gulid.faction.GuildMember;
import com.rwbase.dao.gulid.faction.GuildMemberHolder;
import com.rwbase.dao.gulid.faction.GuildPositionType;
import com.rwbase.dao.gulid.faction.GuildPrivacyType;
import com.rwbase.dao.gulid.faction.GuildSetting;



/**
 * 帮派的创建和数据管理，数据修改要做线程保护
 * @author Administrator
 *
 */
public class GuildGTSMgr {

	private static GuildGTSMgr m_instance = new GuildGTSMgr();

	private static Map<String, GuildDataMgr> uuidHolderMap = new ConcurrentHashMap<String, GuildDataMgr>();

	private static Map<String, GuildDataMgr> nameHolderMap = new ConcurrentHashMap<String, GuildDataMgr>();

	private GuildGTSMgr() {
	}

	public static GuildGTSMgr getInstance() {
		return m_instance;
	}

	public void init() {
		List<Guild> allguild = GuildDAO.getInstance().getAll();
		for (Guild guild : allguild) {
			GuildDataMgr guildDataHolder = new GuildDataMgr(guild);
			uuidHolderMap.put(guild.getId(), guildDataHolder);
			nameHolderMap.put(guild.getGuildSetting().getName(), guildDataHolder);
		}
	}
	
	public void onNewDay(Player player) {

		String userId = player.getUserId();
		GuildDataMgr guildDataHolder = getById(userId);
		GuildMemberHolder guildMemberHolder = guildDataHolder.getGuildMemberHolder();
		GuildMember guildMember = guildMemberHolder.getItem(userId);
		if (guildMember != null) {
			guildMember.setDonate(false);
			guildMemberHolder.updateItem(guildMember);
		}

	}
	
	public void checkAssignMent(){
		Collection<GuildDataMgr> values = uuidHolderMap.values();
		for (GuildDataMgr guildDataHolder : values) {
			GuildAutoAssignmentHelper.checkAssignment(guildDataHolder);
		}
	}

	public GuildDataMgr getById(String guildId) {
		GuildDataMgr holder = null;
		if (StringUtils.isNotBlank(guildId)) {
			holder = uuidHolderMap.get(guildId);
		}
		return holder;
	}

	public synchronized boolean checkName(String guildName) {
		return nameHolderMap.containsKey(guildName);
	}

	public synchronized Guild createGuild(Player player, String guildName, int iocnId) {
//		String guildId = player.getGuildUserMgr().getGuildId();
		String guildId = "";
		if (checkName(guildName) || guildId != null && guildId != "") {
			player.NotifyCommonMsg("帮派名字已存在");
			return null;
		}

		if (!player.getGuildUserMgr().isJoinTimeOk()) {
			player.NotifyCommonMsg("退帮后24小时内不能加入帮派");
			return null;
		}

		if (player.getUserGameDataMgr().getGold() < 500) {
			player.NotifyCommonMsg("钻石不足。");
			return null;
		}

		if (player.getUserGameDataMgr().addGold(-500) != 0) {
			player.NotifyCommonMsg("钻石扣除失败，请稍后尝试或请联系客服。");
			return null;
		}
		Guild guild = new Guild();
		guild.setId(UUID.randomUUID().toString());
		guild.setLevel(1);
		guild.setGuildNum(GlobalDataMgr.getAndSaveGuildId());
		guild.setCreateTimer(System.currentTimeMillis());

		GuildSetting guildSetting = new GuildSetting();
		guildSetting.setName(guildName);
		guildSetting.setPrivacyType(GuildPrivacyType.PRIVATE);
		guildSetting.setJoinLevel(1);
		guildSetting.setIconBox(101);
		guildSetting.setIcon(iocnId);
		guildSetting.setDes("见过这么强大的帮派吗？，没见过就进来看看吧!");

		guild.setGuildSetting(guildSetting);

		boolean addSuccess = GuildDAO.getInstance().add(guild);
		if (!addSuccess) {
			player.NotifyCommonMsg("帮派创建失败，请稍后尝试。");
			return null;
		}

		player.getGuildUserMgr().setGuildId(guild.getId());
		player.getGuildUserMgr().setGuildName(guildName);
		GuildDataMgr guildDataHolder = new GuildDataMgr(guild);
		uuidHolderMap.put(guild.getId(), guildDataHolder);
		nameHolderMap.put(guild.getGuildSetting().getName(), guildDataHolder);

		GuildMember guildMember = new GuildMember();
		guildMember.setPosition(GuildPositionType.MASTER);
		guildMember.setUserId(player.getUserId());
		guildMember.setPlayerName(player.getUserName());
		guildMember.setIconId(player.getHeadImage());
		guildMember.setLevel(player.getLevel());
		guildMember.setLoginTime(player.getUserGameDataMgr().getLastLoginTime());
		guildMember.setJoinTime(System.currentTimeMillis());

		GuildCfg guildCfg = (GuildCfg) CfgGuildDAO.getInstance().getCfgById(guild.getLevel() + "");
		boolean addMemSuccess = guildDataHolder.getGuildMemberHolder().addItem(guildMember, guildCfg);
		if (!addMemSuccess) {
			player.NotifyCommonMsg("帮派创建失败，请稍后尝试。");
			return null;
		}

		GuildMailHelper.sendPlayerEmail(player, "[" + guildName + "]创建成功", "[" + player.getUserName() + "]大人，您的帮派[" + guildName
				+ "]创建成功！现在你可以进入帮派页面管理您的帮派，并且招募帮会人员。祝你在封神世界游戏愉快！", "帮派管理员");
		player.getStoreMgr().AddStore();
		guildDataHolder.getGuildLogInfoHolder().addLog(player, guild, 12, "", guildName);
		return guild;
	}


}
