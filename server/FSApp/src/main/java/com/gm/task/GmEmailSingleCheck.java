package com.gm.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.email.EmailItem;

public class GmEmailSingleCheck implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try {
			response.setStatus(0);
			response.setCount(1);
			Map<String, Object> args = request.getArgs();
			final EmailData emailData = GmEmailHelper.getEmailData(args);

			String roleId = GmUtils.parseString(args, "roleId");
			Player targetPlayer = PlayerMgr.getInstance().find(roleId);
			long taskId = emailData.getTaskId();
			if (targetPlayer != null) {
				List<EmailItem> allEmail = targetPlayer.getEmailMgr()
						.getAllEmail();
				List<GmEmailRepItem> repItemList = new ArrayList<GmEmailRepItem>();
				for (EmailItem emailItem : allEmail) {
					repItemList.add(toRepItem(emailItem));
				}
				for (GmEmailRepItem gmEmailRepItem : repItemList) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", gmEmailRepItem.getId());
					map.put("title", gmEmailRepItem.getTitle());
					map.put("content", gmEmailRepItem.getContent());
					map.put("toolList", gmEmailRepItem.getToolList());
					map.put("sendTime", gmEmailRepItem.getSendTime());
					map.put("receiveTime", gmEmailRepItem.getSendTime());
					map.put("expireTime", gmEmailRepItem.getExpireTime());
					response.addResult(map);
				}
				
			} else {
				GameLog.info(LogModule.GM.getName(), "GmEmailSingleSend",
						"GmEmailSingleSend[doTask] 没有找到用户 userId:" + roleId,
						null);
			}

		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

	private GmEmailRepItem toRepItem(EmailItem emailItem) {
		GmEmailRepItem repItem = new GmEmailRepItem();
		repItem.setId(emailItem.getEmailId());
		repItem.setTitle(emailItem.getTitle());
		repItem.setContent(emailItem.getContent());
		repItem.setTaskId(emailItem.getTaskId());

		repItem.setBeginTime(emailItem.getBeginTime());
		repItem.setEndTime(emailItem.getEndTime());
		repItem.setCoolTime(emailItem.getCoolTime());
		repItem.setExpireTime(emailItem.getDeadlineTimeInMill());

		handleAttachment(emailItem, repItem);

		return repItem;
	}

	private void handleAttachment(EmailItem emailItem, GmEmailRepItem repItem) {
		String attachment = emailItem.getEmailAttachment();
		if (StringUtils.isNotBlank(attachment)) {
			String[] split = attachment.split(",");
			List<GmItem> itemList = new ArrayList<GmItem>();
			for (String itemTmp : split) {
				String[] itemTmpSplit = itemTmp.split("~");
				if (itemTmpSplit.length == 2) {
					GmItem gmItem = new GmItem();
					gmItem.setCode(Integer.valueOf(itemTmpSplit[0]));
					gmItem.setAmount(Integer.valueOf(itemTmpSplit[1]));
				}
			}
			repItem.setToolList(itemList);
		}
	}

}
