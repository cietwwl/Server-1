package com.playerdata.guild;

import java.util.List;

import com.bm.guild.GuildGTSMgr;
import com.playerdata.Player;
import com.rwbase.dao.gulid.faction.GuildHolder;
import com.rwbase.dao.gulid.faction.GuildMember;
import com.rwbase.dao.gulid.faction.GuildPrivacyType;
import com.rwbase.dao.gulid.faction.GuildSetting;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;

/**
 * GuilSetting只能帮助修改不需要做数据多线程保护
 * 
 * @author Allen
 *
 */
public class GuildSettingMgr {

	private String guildId;

	public GuildSettingMgr(String guildId) {
		this.guildId = guildId;
	}

	// 改公告
	public boolean updataNotice(Player player, String notice) {

		boolean success = false;
		if(isMaster(player)){
			getGuildSetting().setDes(notice);
			update();
			success = true;
			player.NotifyCommonMsg("帮派宣言修改成功");
		}
		return success;

	}
	private boolean isMaster(Player player){
		GuildDataMgr guildDataHolder = GuildGTSMgr.getInstance().getById(guildId);
		return guildDataHolder.getGuildMemberHolder().isMaster(player.getUserId());
	}

	// 改名字
	public boolean updataName(Player player, final String guildName) {
		if (GuildGTSMgr.getInstance().checkName(guildName)) {
			player.NotifyCommonMsg("帮派名字已存在");
			return false;
		}

		boolean success = false;
		if (isMaster(player)) {
			GuildSetting guildSetting = getGuildSetting();
			if (guildSetting.getChangeName() < 1) {
				guildSetting.setChangeName(1);
			} else if (player.getUserGameDataMgr().getGold() < 100 || player.getUserGameDataMgr().addGold(-100) != 0) {
				player.NotifyCommonMsg("钻石不足");
				return false;
			}

			getGuildSetting().setName(guildName);
			update();
			
			success = true;
			player.NotifyCommonMsg("帮派宣言修改成功");

			GuildDataMgr guildDataHolder = GuildGTSMgr.getInstance().getById(guildId);
			List<GuildMember> memberList = guildDataHolder.getGuildMemberHolder().getItemList();
			for (GuildMember guildMember : memberList) {
				GameWorldFactory.getGameWorld().asyncExecute(guildMember.getUserId(), new PlayerTask() {

					@Override
					public void run(Player otherPlayer) {
						otherPlayer.getGuildUserMgr().setGuildName(guildName);
					}
				});
			}

		}
		return success;

	}

	// 改类形
	public boolean changeType(Player player, int typeOrdinal) {
		boolean success = false;
		if (isMaster(player)) {

			GuildPrivacyType privacyType = GuildPrivacyType.valueOf(typeOrdinal);
			if (privacyType != null) {
				getGuildSetting().setPrivacyType(privacyType);
				update();
				success = true;
				player.NotifyCommonMsg("操作成功.");
			}
		}

		return success;
	}

	// 改加入等级
	public boolean changeJoinLevel(Player player, int level) {

		if (level < 0 || level > 100) {
			return false;
		}
		
		boolean success = false;
		if (isMaster(player)) {

			getGuildSetting().setJoinLevel(level);
			update();
			success = true;
			player.NotifyCommonMsg("操作成功.");
		}

		return success;
	}

	// 改icon
	public boolean changeIcon(Player player, int iconId) {

		if (iconId <= 0) {
			return false;
		}

		boolean success = false;
		if (isMaster(player)) {
			
			if (iconId > 100) {
				// 大于100是icon 小于是iconbox...
				getGuildSetting().setIcon(iconId);
			} else {
				getGuildSetting().setIconBox(iconId);
			}
			update();
			success = true;
			player.NotifyCommonMsg("操作成功.");
			
		}
		
		return success;

	}

	private GuildHolder getGuildHolder() {
		GuildHolder guildHolder = GuildGTSMgr.getInstance().getById(guildId).getGuildHolder();
		return guildHolder;
	}

	private GuildSetting getGuildSetting() {
		return getGuildHolder().get().getGuildSetting();
	}

	private void update() {
		getGuildHolder().update();
	}
	

}
