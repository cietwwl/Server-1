package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EmailData;

public class GmEmailSingleSend implements IGmTask{

	
	
	
	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Map<String, Object> args = request.getArgs();
		final EmailData emailData = GmEmailHelper.getEmailData(args);
		
		String roleId = (String)args.get("roleId");
		Player targetPlayer = PlayerMgr.getInstance().find(roleId);
		long taskId = emailData.getTaskId();
		if(targetPlayer!=null){
			if(!targetPlayer.getEmailMgr().containsEmailWithTaskId(taskId)){
				EmailUtils.sendEmail(targetPlayer.getUserId(), emailData);
			}else{
				GameLog.info(LogModule.GM.getName(), "GmEmailSingleSend", "GmEmailSingleSend[doTask] 用户已经存在相同taskId的邮件， userId:"+roleId+" taskId"+taskId, null);
			}
		}else{
			GameLog.info(LogModule.GM.getName(), "GmEmailSingleSend", "GmEmailSingleSend[doTask] 没有找到用户 userId:"+roleId, null);
		}

		response.addResult(resultMap );
		return response;
	}


	

}
