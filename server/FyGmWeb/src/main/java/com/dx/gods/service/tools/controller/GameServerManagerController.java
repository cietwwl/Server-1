package com.dx.gods.service.tools.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.tools.ant.taskdefs.condition.Http;

import com.dx.gods.common.gameserver.GameServer;
import com.dx.gods.common.gameserver.GameServerManager;
import com.dx.gods.common.gameserver.GameServerRequestType;
import com.dx.gods.common.gameserver.GameServiceRequestType;
import com.dx.gods.common.log.EnumLog;
import com.dx.gods.common.log.GMLogger;
import com.dx.gods.common.log.GSLogger;
import com.dx.gods.common.log.LoggerManager;
import com.dx.gods.common.log.data.LoggerData;
import com.dx.gods.common.net.GSRequestAction;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.UtilTools;
import com.dx.gods.controller.admin.common.DXAdminController;
import com.rw.service.http.response.BaseMsgResponse;

public class GameServerManagerController extends DXAdminController{
	
	private int serverid;
	private List<GameServer> serverlist;
	private List<LoggerData> logList;
	
	public String listWindowsServer() {
		
		serverlist = new ArrayList<GameServer>();
		for (Iterator<Entry<Integer, GameServer>> iterator = GameServerManager.ServerMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, GameServer> next = iterator.next();
			GameServer gameServer = next.getValue();
			if(!gameServer.isIslinux()){
				serverlist.add(gameServer);
			}	
		}
		return SUCCESS;
	}
	
	public String listLinuxServer(){
		serverlist = new ArrayList<GameServer>();
		for (Iterator<Entry<Integer, GameServer>> iterator = GameServerManager.ServerMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, GameServer> next = iterator.next();
			GameServer gameServer = next.getValue();
			if(gameServer.isIslinux()){
				serverlist.add(gameServer);
			}	
		}
		return SUCCESS;
	}
	
	public String shutdownServer() {
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			serverid = Integer.parseInt(request.getParameter("serverid")
					.toString());
			GameServer gameServer = GameServerManager.ServerMap.get(serverid);
			String strUrl = gameServer.getHttpUrl() + ":"
					+ gameServer.getHttpPort();

			GSRequestAction requestAction = new GSRequestAction();
			BaseMsgResponse msg = (BaseMsgResponse) requestAction.remoteCall(
					strUrl,
					"com.rw.service.http.requestHandler.ManagerServerHandler",
					"shutdownServerHandler");
			return NetUtils.packMessage(request, msg.getMsg());
		} catch (Exception ex) {
			return NetUtils.packMessage(request, "通知服务器失败，请重试!");
		}
	}
	
	public String shutdownLinuxServer(){
		HttpServletRequest request = ServletActionContext.getRequest();
		serverid = Integer.parseInt(request.getParameter("serverid").toString());
		GameServer gameServer = GameServerManager.ServerMap.get(serverid);
		String strUrl = GameServerManager.getGSServiceUrl();
		StringBuilder sb = new StringBuilder();
		NetUtils.packNetMessage("type", GameServiceRequestType.TYPE_DISTRIBUTION, sb);
		NetUtils.packNetMessage("id", gameServer.getId(), sb);
		NetUtils.packNetMessage("processtype", GameServiceRequestType.TYPE_SHUTDOWN_SERVER, sb);
		InputStream response = NetUtils.sentHttpPostMsg(strUrl, sb.toString());
		if (response != null) {
			String result = NetUtils.getInputStreamToString(response);
			GSLogger.recordOperatorLog(UtilTools.getCurrentUserName(), "停服维护服务器");
			return NetUtils.packMessage(request, result);
		} else {
			return NetUtils.packMessage(request, "通知服务器失败，请重试!");
		}
	}
	
	public String updateLinuxServer(){
		HttpServletRequest request = ServletActionContext.getRequest();
		serverid = Integer.parseInt(request.getParameter("serverid").toString());
		GameServer gameServer = GameServerManager.ServerMap.get(serverid);
		String strUrl = GameServerManager.getGSServiceUrl();
		StringBuilder sb = new StringBuilder();
		NetUtils.packNetMessage("type", GameServiceRequestType.TYPE_DISTRIBUTION, sb);
		NetUtils.packNetMessage("id", gameServer.getId(), sb);
		NetUtils.packNetMessage("processtype", GameServiceRequestType.TYPE_UPDATE_SERVER, sb);
		InputStream response = NetUtils.sentHttpPostMsg(strUrl, sb.toString());
		if (response != null) {
			String result = NetUtils.getInputStreamToString(response);
			GSLogger.recordOperatorLog(UtilTools.getCurrentUserName(), "更新服务器");
			return NetUtils.packMessage(request, result);
		} else {
			return NetUtils.packMessage(request, "通知服务器失败，请重试!");
		}
	}
	
	public String startLinuxServer(){
		HttpServletRequest request = ServletActionContext.getRequest();
		serverid = Integer.parseInt(request.getParameter("serverid").toString());
		GameServer gameServer = GameServerManager.ServerMap.get(serverid);
		String strUrl = GameServerManager.getGSServiceUrl();
		StringBuilder sb = new StringBuilder();
		NetUtils.packNetMessage("type", GameServiceRequestType.TYPE_DISTRIBUTION, sb);
		NetUtils.packNetMessage("id", gameServer.getId(), sb);
		NetUtils.packNetMessage("processtype", GameServiceRequestType.TYPE_START_SERVER, sb);
		InputStream response = NetUtils.sentHttpPostMsg(strUrl, sb.toString());
		if (response != null) {
			String result = NetUtils.getInputStreamToString(response);
			GSLogger.recordOperatorLog(UtilTools.getCurrentUserName(), "启动服务器");
			return NetUtils.packMessage(request, result);
		} else {
			return NetUtils.packMessage(request, "通知服务器失败，请重试!");
		}
	}
	
	public String showLogList() {
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			logList = LoggerManager.readLog(EnumLog.GS_LOG.getType(), 20);
			return SUCCESS;
		} catch (Exception ex) {
			return NetUtils.packMessage(request, "通知服务器失败，请重试!");
		}
	}

	public List<LoggerData> getLogList() {
		return logList;
	}

	public void setLogList(List<LoggerData> logList) {
		this.logList = logList;
	}

	public List<GameServer> getServerlist() {
		return serverlist;
	}

	public void setServerlist(List<GameServer> serverlist) {
		this.serverlist = serverlist;
	}
}
