package com.gm.task;

import java.util.Map;

import com.bm.serverStatus.ServerStatusMgr;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.rwbase.dao.serverData.GmNoticeInfo;
import com.rwbase.dao.serverData.ServerGmNotice;

public class GmEditGmNotice implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		response.setStatus(0);
		response.setCount(1);
		try {
			Map<String, Object> args = request.getArgs();
			int noticeId = GmUtils.parseInt(args, "noticeId");
			String title = GmUtils.parseString(args, "title");
			String content = GmUtils.parseString(args, "content");
			long startTime = GmUtils.parseLong(args, "startTime");
			long endTime = GmUtils.parseLong(args, "endTime");
			int cycleInterval = GmUtils.parseInt(args, "cycleInterval");
			int priority = GmUtils.parseInt(args, "priority");
			
			if (noticeId == 0) {
				createNewGmNotice(title, content, startTime, endTime, cycleInterval, priority);
			}else{
				ServerGmNotice gmNotice = ServerStatusMgr.getGmNotice(noticeId);
				if(gmNotice == null){
					createNewGmNotice(title, content, startTime, endTime, cycleInterval, priority);
				}else{
					GmNoticeInfo noticeInfo = gmNotice.getNoticeInfo();
					initGmNoticeInfo(noticeInfo, title, content, startTime, endTime, cycleInterval, priority);
					ServerStatusMgr.editGmNotice(gmNotice, false);
				}
			}
			
		} catch (Exception ex) {

		}
		return response;
	}
	
	private void createNewGmNotice(String title, String content, long startTime, long endTime, int cycleInterval, int priority){
		ServerGmNotice notice = new ServerGmNotice();
		GmNoticeInfo info  =new GmNoticeInfo();
		initGmNoticeInfo(info, title, content, startTime, endTime, cycleInterval, priority);
		notice.setNoticeInfo(info);
		ServerStatusMgr.editGmNotice(notice, true);
	}
	
	private void initGmNoticeInfo(GmNoticeInfo info, String title, String content, long startTime, long endTime, int cycleInterval, int priority){
		info.setTitle(title);
		info.setContent(content);
		info.setStartTime(startTime*1000);
		info.setEndTime(endTime * 1000);
		info.setCycleInterval(cycleInterval);
		info.setPriority(priority);
	}

}
