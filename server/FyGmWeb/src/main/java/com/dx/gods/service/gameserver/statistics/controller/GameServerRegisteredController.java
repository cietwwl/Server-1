package com.dx.gods.service.gameserver.statistics.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.dx.gods.common.gameserver.GameServer;
import com.dx.gods.common.gameserver.GameServerManager;
import com.dx.gods.common.net.GSRequestAction;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.UtilTools;
import com.dx.gods.controller.admin.common.DXAdminController;

public class GameServerRegisteredController extends DXAdminController{
	public final static long MAX_QUERY_RANGE = 7 * 24 * 60 * 60 * 1000l;
	private List<GameServer> serverList = new ArrayList<GameServer>();
	private int serverid;
	private String beginDate;
	private String endDate;
	private Map<Long, Integer> resultMap = new HashMap<Long, Integer>();
	
	public String ServerList(){
		HttpServletRequest request = ServletActionContext.getRequest();
			String pId = request.getParameter("projectId");
			List<GameServer> list = GameServerManager.VersionServerMap.get(pId);
			serverList = new ArrayList<GameServer>(list);
			serverList.add(new GameServer(-1, "全服"));
			request.setAttribute("projectId", pId);
			return SUCCESS;
	}
	
	public String queryGameServerRegistered(){
		HttpServletRequest request = ServletActionContext.getRequest();
		
		try{
			serverid = Integer.parseInt(request.getParameter("serverid").toString());
			Date dtbegin = UtilTools.parseDateTime(this.beginDate, "yyyy-MM-dd");
			Date dtend = UtilTools.parseDateTime(this.endDate, "yyyy-MM-dd");
			long startTime = dtbegin.getTime();
			long endTime = dtend.getTime();
			
			if(endTime < startTime){
				throw new Exception("选择的日期有误!");
			}
			if (endTime - startTime > MAX_QUERY_RANGE) {
				throw new Exception("超过了查询的最大间隔,请重选日期！");
			}
			
			List<GameServer> list = new ArrayList<GameServer>();
			if(serverid == -1){
				list = serverList;
			}else{
				GameServer gameServer = GameServerManager.ServerMap.get(serverid);
				list.add(gameServer);
			}
			List<Long> timeRangeList = UtilTools.getTimeRangeList(startTime, endTime);
			resultMap.clear();
			for (GameServer gameServer : list) {
				String strUrl = UtilTools.getServerUrl(gameServer.getId());
				GSRequestAction requestAction = new GSRequestAction();
				requestAction.pushParams(List.class, timeRangeList);
				
				Map<Long, Integer> tempResult = (Map<Long, Integer>)requestAction.remoteCall(strUrl, "com.rw.service.http.requestHandler.ServerStatisticsHandler", "queryRegistered");
				if(list.size() > 0){
					for (Iterator<Entry<Long, Integer>> iterator = tempResult.entrySet().iterator(); iterator.hasNext();) {
						Entry<Long, Integer> entry = iterator.next();
						if(resultMap.containsKey(entry.getKey())){
							Integer temp = resultMap.get(entry.getKey());
							resultMap.put(entry.getKey(), temp+entry.getValue());
						}else{
							resultMap.put(entry.getKey(), entry.getValue());
						}
					}
				}else{
					resultMap = tempResult;
				}
			}
			return SUCCESS;
			
			
		}catch(Exception ex){
			return NetUtils.packMessage(request, ex.getMessage());
		}
		
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setServerList(List<GameServer> serverList) {
		this.serverList = serverList;
	}

	public List<GameServer> getServerList() {
		return serverList;
	}
}
