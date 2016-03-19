package com.dx.gods.service.gameserver.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.dx.gods.common.gameserver.GameServer;
import com.dx.gods.common.gameserver.GameServerManager;
import com.dx.gods.common.net.GSRequestAction;
import com.dx.gods.common.utils.UtilTools;
import com.rw.service.http.response.ActiveUserDataResponse;
import com.rw.service.http.response.RetainedUserDataResponse;


public class GameServerStatisticsAction {
	/**
	 * 查询玩家注册情况
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Map queryGameServerRegistered(String beginDate, String endDate, String serverId, String pid){
		Map result = new HashMap();
		try{
			int serverid = Integer.parseInt(serverId);
			Date dtbegin = UtilTools.parseDateTime(beginDate, "yyyy-MM-dd");
			Date dtend = UtilTools.parseDateTime(endDate, "yyyy-MM-dd");
			long startTime = dtbegin.getTime();
			long endTime = dtend.getTime();
			
			List<GameServer> list = new ArrayList<GameServer>();
			if(serverid == -1){
				list = GameServerManager.VersionServerMap.get(pid);
			}else{
				GameServer gameServer = GameServerManager.ServerMap.get(serverid);
				list.add(gameServer);
			}
			List<Long> timeRangeList = UtilTools.getTimeRangeList(startTime, endTime);
			
			Map<Long, Integer> resultMap = new HashMap<Long, Integer>();
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
			
			Set<Long> keySet = resultMap.keySet();
			List<Long> timeList = new LinkedList<Long>(keySet);
			Collections.sort(timeList, timeSort);
			
			
			//封装界面数据
			LinkedList allData = new LinkedList();
			String[] categories = new String[timeRangeList.size()];
			ArrayList series = new ArrayList();
			ArrayList chartData = new ArrayList();
			LinkedHashMap map = new LinkedHashMap();
			int index = 0;
			
			for (Long time : timeList) {
				Integer value = resultMap.get(time);
				String strDate = UtilTools.getDateTimeString(time, "yyyy-MM-dd");
				categories[index] = strDate;
				
				chartData.add(value);
				index++;
				ArrayList tableData = new ArrayList();
				tableData.add(strDate);
				tableData.add(value);
				allData.add(tableData);
			}
			map.put("name", "注册人数");
			map.put("data", chartData);
			series.add(map);
			
			result.put("chart", series);
			result.put("categories", categories);
			result.put("table", allData);
			result.put("result", "success");
			return result;
		}catch(Exception ex){
			ex.printStackTrace();
			result.put("result", ex.getMessage());
			return result;
		}
	}

	/**
	 * 查看玩家当前等级分布
	 * @param serverId
	 * @param pid
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Map queryGameServerLevelSpread(String serverId, String pid){
		Map result = new HashMap();
		
		try{
			int serverid = Integer.parseInt(serverId);
			List<GameServer> list = new ArrayList<GameServer>();
			if(serverid == -1){
				list = GameServerManager.VersionServerMap.get(pid);
			}else{
				GameServer gameServer = GameServerManager.ServerMap.get(serverid);
				list.add(gameServer);
			}
			//<level, count>
			Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
			for (GameServer gameServer : list) {
				String strUrl = UtilTools.getServerUrl(gameServer.getId());
				GSRequestAction requestAction = new GSRequestAction();
				
				Map<Integer, Integer> tempResult = (Map<Integer, Integer>)requestAction.remoteCall(strUrl, "com.rw.service.http.requestHandler.ServerStatisticsHandler", "queryLevelSpread");
				if(list.size() > 0){
					for (Iterator<Entry<Integer, Integer>> iterator = tempResult.entrySet().iterator(); iterator.hasNext();) {
						Entry<Integer, Integer> entry = iterator.next();
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
			
			
			List<Integer> levelList = new ArrayList<Integer>(resultMap.keySet());
			Collections.sort(levelList, new Comparator<Integer>() {

				@Override
				public int compare(Integer o1, Integer o2) {
					// TODO Auto-generated method stub
					if(o1 > o2){
						return 1;
					}else{
						return -1;
					}
				}
			});
			
			//封装界面数据
			ArrayList allData = new ArrayList();
			String[] categories = new String[resultMap.size()];
			ArrayList series = new ArrayList();
			ArrayList chartData = new ArrayList();
			LinkedHashMap map = new LinkedHashMap();
			int index = 0;
			for (Integer lv : levelList) {
				Integer value = resultMap.get(lv);
				resultMap.get(lv);
				categories[index] = lv.toString();
				
				chartData.add(value);
				index++;
				ArrayList tableData = new ArrayList();
				tableData.add(lv);
				tableData.add(value);
				allData.add(tableData);
			}
			map.put("name", "等级分布");
			map.put("data", chartData);
			series.add(map);
			
			result.put("chart", series);
			result.put("categories", categories);
			result.put("table", allData);
			result.put("result", "success");
			return result;
		}catch(Exception ex){
			ex.printStackTrace();
			result.put("result", ex.getMessage());
			return result;
		}
	}

	/**
	 * 查看活跃玩家的信息
	 * @param serverId
	 * @param pid
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Map queryGameServerActiveUser(String beginDate, String endDate, String serverId, String pid){
		Map result = new HashMap();
		try{
			int serverid = Integer.parseInt(serverId);
			Date dtbegin = UtilTools.parseDateTime(beginDate, "yyyy-MM-dd");
			Date dtend = UtilTools.parseDateTime(endDate, "yyyy-MM-dd");
			long startTime = dtbegin.getTime();
			long endTime = dtend.getTime();
			
			List<GameServer> list = new ArrayList<GameServer>();
			if(serverid == -1){
				list = GameServerManager.VersionServerMap.get(pid);
			}else{
				GameServer gameServer = GameServerManager.ServerMap.get(serverid);
				list.add(gameServer);
			}
			List<Long> timeRangeList = UtilTools.getTimeRangeList(startTime, endTime);
			
			Map<Long, ActiveUserDataResponse> resultMap = new HashMap<Long, ActiveUserDataResponse>();
			for (GameServer gameServer : list) {
				String strUrl = UtilTools.getServerUrl(gameServer.getId());
				GSRequestAction requestAction = new GSRequestAction();
				requestAction.pushParams(long.class, dtbegin.getTime());
				requestAction.pushParams(long.class, dtend.getTime());
				
				Map<Long, ActiveUserDataResponse> tempResult = (Map<Long, ActiveUserDataResponse>)requestAction.remoteCall(strUrl, "com.rw.service.http.requestHandler.ServerStatisticsHandler", "queryActiveUserData");
				if(list.size() > 0){
					for (Iterator<Entry<Long, ActiveUserDataResponse>> iterator = tempResult.entrySet().iterator(); iterator.hasNext();) {
						Entry<Long, ActiveUserDataResponse> entry = iterator.next();
						ActiveUserDataResponse value = entry.getValue();
						if(resultMap.containsKey(entry.getKey())){
							ActiveUserDataResponse temp = resultMap.get(entry.getKey());
							temp.setCreateNum(temp.getCreateNum()+value.getCreateNum());
							temp.setActiveNum(temp.getActiveNum()+value.getActiveNum());
						}else{
							resultMap.put(entry.getKey(), entry.getValue());
						}
					}
				}else{
					resultMap = tempResult;
				}
			}
			
			Set<Long> keySet = resultMap.keySet();
			List<Long> timeList = new ArrayList<Long>(keySet);
			Collections.sort(timeList, timeSort);
			
			//封装界面数据
			ArrayList allData = new ArrayList();
			String[] categories = new String[timeRangeList.size()];
			ArrayList series = new ArrayList();
			ArrayList chartData = new ArrayList();
			LinkedHashMap map = new LinkedHashMap();
			int index = 0;
			for (Long time : timeList) {
				ActiveUserDataResponse activeUserDataResponse = resultMap.get(time);
				String strDate = UtilTools.getDateTimeString(time, "yyyy-MM-dd");
				categories[index] = strDate;
				
				chartData.add(activeUserDataResponse.getActiveNum()); 
				index++;
				ArrayList tableData = new ArrayList();
				tableData.add(strDate);
				tableData.add(activeUserDataResponse.getCreateNum());
				tableData.add(activeUserDataResponse.getActiveNum());
				allData.add(tableData);
			}
			map.put("name", "活跃玩家");
			map.put("data", chartData);
			series.add(map);
			
			result.put("chart", series);
			result.put("categories", categories);
			result.put("table", allData);
			result.put("result", "success");
			return result;
		}catch(Exception ex){
			ex.printStackTrace();
			result.put("result", ex.getMessage());
			return result;
		}
	}

	/**
	 * 查看玩家留存的信息
	 * @param beginDate
	 * @param endDate
	 * @param serverId
	 * @param pid
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Map queryGameServerRetainedData(String beginDate, String endDate, String serverId, String pid){
		Map result = new HashMap();
		try{
			int serverid = Integer.parseInt(serverId);
			Date dtbegin = UtilTools.parseDateTime(beginDate, "yyyy-MM-dd");
			Date dtend = UtilTools.parseDateTime(endDate, "yyyy-MM-dd");
			long startTime = dtbegin.getTime();
			long endTime = dtend.getTime();
			
			List<GameServer> list = new ArrayList<GameServer>();
			if(serverid == -1){
				list = GameServerManager.VersionServerMap.get(pid);
			}else{
				GameServer gameServer = GameServerManager.ServerMap.get(serverid);
				list.add(gameServer);
			}
			List<Long> timeRangeList = UtilTools.getTimeRangeList(startTime, endTime);
			
			Map<Long, RetainedUserDataResponse> resultMap = new HashMap<Long, RetainedUserDataResponse>();
			for (GameServer gameServer : list) {
				String strUrl = UtilTools.getServerUrl(gameServer.getId());
				GSRequestAction requestAction = new GSRequestAction();
				requestAction.pushParams(long.class, startTime);
				requestAction.pushParams(long.class, endTime);
				
				Map<Long, RetainedUserDataResponse> tempResult = (Map<Long, RetainedUserDataResponse>)requestAction.remoteCall(strUrl, "com.rw.service.http.requestHandler.ServerStatisticsHandler", "retainedUserData");
				if(list.size() > 0){
					for (Iterator<Entry<Long, RetainedUserDataResponse>> iterator = tempResult.entrySet().iterator(); iterator.hasNext();) {
						Entry<Long, RetainedUserDataResponse> entry = iterator.next();
						if(resultMap.containsKey(entry.getKey())){
							RetainedUserDataResponse temp = resultMap.get(entry.getKey());
							RetainedUserDataResponse value = entry.getValue();
							temp.setCreateNum(temp.getCreateNum()+ value.getCreateNum());
							Map<Long, Integer> retainedMap = value.getRetainedMap();
							Map<Long, Integer> mainRetainedMap = temp.getRetainedMap();
							for (Iterator<Entry<Long, Integer>> it = retainedMap
									.entrySet().iterator(); it.hasNext();) {
								Entry<Long, Integer> e = it.next();
								Long key = e.getKey();
								Integer v = e.getValue();
								if (mainRetainedMap.containsKey(key)) {
									int count = mainRetainedMap.get(key);
									mainRetainedMap.put(key, v + count);
								} else {
									mainRetainedMap.put(key, v);
								}
							}
						}else{
							resultMap.put(entry.getKey(), entry.getValue());
						}
					}
				}else{
					resultMap = tempResult;
				}
			}
			
			Set<Long> keySet = resultMap.keySet();
			List<Long> timeList = new ArrayList<Long>(keySet);
			Collections.sort(timeList, timeSort);
			
			//封装界面数据
			ArrayList allData = new ArrayList();
			String[] categories = new String[timeRangeList.size()];
			ArrayList series = new ArrayList();
			ArrayList chartData = new ArrayList();
			LinkedHashMap map = new LinkedHashMap();
			int index = 0;
			for (Long time : timeList) {
				RetainedUserDataResponse retainedUserDataResponse = resultMap.get(time);
				String strDate = UtilTools.getDateTimeString(time, "yyyy-MM-dd");
				categories[index] = strDate;
				int createNum = retainedUserDataResponse.getCreateNum();
				index++;
				ArrayList tableData = new ArrayList();
				tableData.add(strDate);
				tableData.add(createNum);
				Map<Long, Integer> retainedMap = retainedUserDataResponse.getRetainedMap();
				//1-7 15 30天的留存
				Integer[] ary = new Integer[]{1,2,3,4,5,6,7,15,30};
				for (Integer day : ary) {
					long temptime = time + day * 24 * 60 * 60 * 1000l;
					if(retainedMap.size() == 0){
						tableData.add(0);
						continue;
					}
					boolean blnAdd = false;
					for (Iterator<Entry<Long, Integer>> it = retainedMap.entrySet().iterator(); it.hasNext();) {
						Entry<Long, Integer> e = it.next();
						if(UtilTools.isSameDay(temptime, e.getKey())){
							double dv = (double)e.getValue() / createNum * 100;
							DecimalFormat decimalFormat = new DecimalFormat("#.00");
							String format = decimalFormat.format(dv);
							tableData.add(format+"%");
							blnAdd = true;
						}
					}
					if(!blnAdd){
						tableData.add(0);
					}
				}
				
				allData.add(tableData);
			}
			map.put("name", "留存");
			map.put("data", chartData);
			series.add(map);
			
			result.put("chart", series);
			result.put("categories", categories);
			result.put("table", allData);
			result.put("result", "success");
			return result;
		}catch(Exception ex){
			ex.printStackTrace();
			result.put("result", ex.getMessage());
			return result;
		}
	}

	private Comparator<Long> timeSort = new Comparator<Long>() {
		
		@Override
		public int compare(Long o1, Long o2) {
			// TODO Auto-generated method stub
			if(o1 > o2){
				return -1;
			}else{
				return 1;
			}
		}
	};
}
