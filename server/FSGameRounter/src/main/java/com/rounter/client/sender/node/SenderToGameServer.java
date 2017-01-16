package com.rounter.client.sender.node;

import com.rounter.param.IRequestData;
import com.rounter.param.IResponseData;
import com.rounter.service.IResponseHandler;

public class SenderToGameServer{
	
	ChannelNodeManager chNodeMgr;
	
	public ChannelNodeManager getChNodeMgr() {
		return chNodeMgr;
	}
	
	public void setChNodeMgr(ChannelNodeManager chNodeMgr) {
		this.chNodeMgr = chNodeMgr;
	}
	
	public void sendMsgToGameServer(IRequestData reqData, IResponseHandler resHandler, IResponseData resData) {
		try {
			chNodeMgr.getProperChannelNode().sendMessage(reqData, resHandler, resData);
		} catch (Exception e) {
			e.printStackTrace();
			resHandler.handleSendFailResponse(resData);
			synchronized (resData) {
				resData.notifyAll();
			}
		}
	}
}