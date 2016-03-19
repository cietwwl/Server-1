package com.bm.guild;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.playerdata.PlayerMgr;
import com.playerdata.guild.GuildDataMgr;
import com.rwbase.dao.gulid.faction.GuildMember;
import com.rwbase.dao.gulid.faction.GuildMemberHolder;
import com.rwbase.dao.gulid.faction.GuildPositionType;

public class GuildAutoAssignmentHelper {

	private GuildAutoAssignmentHelper(){}


	/*** 三天在不在线到时自动转让 **/
	public static void checkAssignment(GuildDataMgr guildDataHolder) {

		if (guildDataHolder.getGuildHolder().get().getDismissTime() <= 0) {

			GuildMemberHolder guildMemberHolder = guildDataHolder.getGuildMemberHolder();
			GuildMember master = guildMemberHolder.getMaster();

			if (master == null) {
				return;
			}
			if (System.currentTimeMillis() - master.getLoginTime() > 3 * 24 * 3600 * 1000) {
				boolean isBossOnline = PlayerMgr.getInstance().isOnline(master.getUserId());
				if (!isBossOnline) {
					GuildMember assignmentMember = getCandidate(guildMemberHolder);
					if (assignmentMember != null) {
						guildDataHolder.getGuildMemberTSMgr().switchMaster(master, assignmentMember);
					}

				}
			}
		}

	}




	/*** 得到转让玩家 **/
	private static GuildMember getCandidate(GuildMemberHolder guildMemberHolder) {

		GuildMember target = null;
		List<GuildMember> itemList = guildMemberHolder.getItemList();
		if (itemList.size() > 1) {
			Collections.sort(itemList, new Comparator<GuildMember>() {
				public int compare(GuildMember source, GuildMember target) {
					if (source.getPosition().ordinal() > target.getPosition().ordinal()) {
						return 1;

					} else if (source.getPosition() == target.getPosition()) {
						if (source.getJoinTime() > target.getJoinTime()) {
							return 1;
						}
					}
					return 0;
				}
			});
			final int nextIndex = 1;
			target = itemList.get(nextIndex);

		}
		return target;
	}

}
