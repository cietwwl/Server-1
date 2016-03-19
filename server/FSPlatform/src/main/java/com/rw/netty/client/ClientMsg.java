package com.rw.netty.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientMsg {
	private ConcurrentLinkedQueue<GSMessage> ProcessMsgList = new ConcurrentLinkedQueue<GSMessage>();
	private HashMap<String, List<GSMessage>> ProcessMap = new HashMap<String, List<GSMessage>>();
	
	
	public void addProcessMsg(GSMessage gsMsg){
			ProcessMsgList.add(gsMsg);
			if(ProcessMap.containsKey(gsMsg.getAccountId())){
				List<GSMessage> list = ProcessMap.get(gsMsg.getAccountId());
				list.add(gsMsg);
			}else{
				List<GSMessage> list = new ArrayList<GSMessage>();
				list.add(gsMsg);
				ProcessMap.put(gsMsg.getAccountId(), list);
			}
		
	}
	
	public void removeProcessMsgByAccountId(String accountId) {
		List<GSMessage> list = ProcessMap.get(accountId);
		if (list == null || list.size() <= 0) {
			return;
		}
		for (GSMessage gsMsg : list) {
			ProcessMsgList.remove(gsMsg);
		}
		ProcessMap.remove(accountId);
	}
	
	public GSMessage pollMsg(){
		GSMessage gsMsg = this.ProcessMsgList.poll();
		List<GSMessage> list = ProcessMap.get(gsMsg.getAccountId());
		if (list.remove(gsMsg)) {
			if (list.size() <= 0) {
				ProcessMap.remove(gsMsg.getAccountId());
			}
		}
		return gsMsg;
	}
	
	public int getMsgListSize(){
		return ProcessMsgList.size();
	}
	
	
}
