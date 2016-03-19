package com.playerdata.guild;

import java.util.List;

import com.playerdata.PlayerMgr;
import com.playerdata.readonly.PlayerIF;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.gulid.faction.GuildMember;

public class GuildMailHelper {

	private GuildMailHelper(){}
	
	public static void sendPlayerEmail(PlayerIF otherPlayer, String title, String content, String sendName) {
		if (otherPlayer != null) {
			EmailData emailData = new EmailData();
			emailData.setTitle(title);
			emailData.setContent(content);
			emailData.setDeleteType(EEmailDeleteType.DELAY_TIME);
			emailData.setDelayTime(3600 * 24 * 7);
			if (sendName != "") {
				emailData.setSender(sendName);
			} else {
				emailData.setSender("帮派邮件");
			}

			EmailUtils.sendEmail(otherPlayer.getTableUser().getUserId(), emailData);
		}

	}
	
	public static void sendAllGuildMemberEmail(GuildDataMgr guildDataHolder, String title, String content, String sendName) {
		List<GuildMember> list = guildDataHolder.getGuildMemberHolder().getItemList();
		for (GuildMember item : list) {
			PlayerIF otherPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(item.getUserId());
			sendPlayerEmail(otherPlayer, title, content, sendName);
		}

	}
	
	public static void sendEmail(PlayerIF target, String title, String content, String sendName) {
		if (target != null) {
			EmailData emailData = new EmailData();
			emailData.setTitle(title);
			emailData.setContent(content);
			emailData.setDeleteType(EEmailDeleteType.DELAY_TIME);
			emailData.setDelayTime(3600 * 24 * 7);
			if (sendName != "") {
				emailData.setSender(sendName);
			} else {
				emailData.setSender("帮派邮件");
			}

			EmailUtils.sendEmail(target.getTableUser().getUserId(), emailData);
		}

	}

	
}
