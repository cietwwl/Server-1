package com.gm.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.EmailMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.email.EmailItem;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;

public class GmEmailSingleDelete implements IGmTask {

	@SuppressWarnings("unchecked")
	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try {
			response.setStatus(0);
			response.setCount(1);
			Map<String, Object> resultMap = new HashMap<String, Object>();

			Map<String, Object> args = request.getArgs();
			final EmailData emailData = GmEmailHelper.getEmailData(args);

			String roleId = GmUtils.parseString(args, "roleId");
			String mailIds = GmUtils.parseString(args, "mailId");
			final List<String> mailIdList = (List<String>) FastJsonUtil.deserialize(mailIds, List.class);
			Player targetPlayer = PlayerMgr.getInstance().find(roleId);
			if (targetPlayer != null) {
				GameWorldFactory.getGameWorld().asyncExecute(roleId, new PlayerTask() {
					
					@Override
					public void run(Player e) {
						
						for (String mailId : mailIdList) {

							EmailMgr emailMgr = e.getEmailMgr();
							EmailItem emailItem = emailMgr.getEmailItem(mailId);
							if (emailItem != null) {
								emailMgr.delEmail(mailId, false);
							} else {
								GameLog.info(LogModule.GM.getName(), "GmEmailSingleDelete", "GmEmailSingleDelete[doTask] 邮件不存在， userId:" + e.getUserId() + " mailId" + mailId, null);
							}
						}
					}
				});
				
			} else {
				GameLog.info(LogModule.GM.getName(), "GmEmailSingleDelete", "GmEmailSingleDelete[doTask] 没有找到用户 userId:" + roleId, null);
			}

			response.addResult(resultMap);
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}
}
