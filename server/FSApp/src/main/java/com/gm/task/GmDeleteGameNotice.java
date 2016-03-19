package com.gm.task;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.SocketHelper;
import com.rw.manager.GameManager;
import com.rw.service.gamenotice.GameNoticeService;
import com.rwbase.dao.gameNotice.TableGameNotice;

public class GmDeleteGameNotice implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try {
			
			int noticeId = Integer.parseInt(request.getArgs().get("noticeId").toString());

			
			if (noticeId < 0) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}

			TableGameNotice notice = GameManager.getGameNotice().getNoticeByNoticeId(noticeId);
			if (notice != null) {
				GameManager.getGameNotice().removeGameNotice(noticeId);
			}
			response.setStatus(0);
			response.setCount(1);
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
