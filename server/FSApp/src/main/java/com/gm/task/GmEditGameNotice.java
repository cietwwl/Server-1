package com.gm.task;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.SocketHelper;
import com.rw.manager.GameManager;
import com.rw.service.gamenotice.GameNoticeService;
import com.rwbase.dao.gameNotice.TableGameNotice;

public class GmEditGameNotice implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try{
			int noticeId = -1;
			if (request.getArgs().get("noticeId") != null) {
				noticeId = Integer.parseInt(request.getArgs().get("noticeId").toString());
			}
			String title = (String) request.getArgs().get("title");
			String content = (String)request.getArgs().get("content");
			long startTime = Long.parseLong(request.getArgs().get("startTime").toString());
			long endTime = Long.parseLong(request.getArgs().get("endTime").toString());
			
			if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			content = content.replace("<br>", "*");
			
			boolean insert = false;
			TableGameNotice notice = GameManager.getGameNotice().getNoticeByNoticeId(noticeId);
			if (notice == null) {
				notice =new TableGameNotice();
				insert = true;
			}
			notice.setTitle(title);
			notice.setContent(content);
			notice.setStartTime(startTime);
			notice.setEndTime(endTime);
			
			GameManager.getGameNotice().addGameNotice(notice, insert);
			
			response.setStatus(0);
			response.setCount(1);
		}catch(Exception ex){
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
