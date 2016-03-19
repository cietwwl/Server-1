package com.gm.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.serverStatus.ServerStatusMgr;
import com.common.playerFilter.PlayerFilterCondition;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.manager.GameManager;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.serverData.ServerGmEmail;

public class GmViewEmailList implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try{
			long taskId = GmUtils.parseLong(request.getArgs(), "taskId");
			List<ServerGmEmail> list = new ArrayList<ServerGmEmail>();
			if(taskId != 0){
				ServerGmEmail gmMail = ServerStatusMgr.getGmMail(taskId);
				list.add(gmMail);
			}else{
				list = ServerStatusMgr.getGmMails();
			}
			int count = 0;
			for (ServerGmEmail serverGmEmail : list) {
				EmailData emailData = serverGmEmail.getSendToAllEmailData();
				if(emailData == null){
					continue;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("serverId", GameManager.getServerId());
				map.put("taskId", serverGmEmail.getEmailTaskId());
				map.put("title", emailData.getTitle());
				map.put("content", emailData.getContent());
				String emailAttachment = emailData.getEmailAttachment();
				map.put("itemDict", emailAttachment);
				map.put("coolTime", emailData.getCoolTime());
				map.put("expireTime", emailData.getExpireTime());
				List<PlayerFilterCondition> conditionList = serverGmEmail.getConditionList();
				String strConditionList = FastJsonUtil.serialize(conditionList);
				map.put("conditionList", strConditionList);
				map.put("status", serverGmEmail.getStatus());
				response.addResult(map);
				count++;
			}
			response.setStatus(0);
			response.setCount(count);
		}catch(Exception ex){
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
