package com.rwbase.dao.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.DataRdbDao;
import com.rw.fsutil.log.SqlLog;
import com.rw.manager.GameManager;




public class UserDataDao extends  DataRdbDao<User>{

	private static UserDataDao m_instance = new UserDataDao();
	
	public static UserDataDao getInstance(){
		return m_instance;
	}

	
	public boolean validateName(String userName)
    {	
		List<User> result = this.findBySql("select userName from user where userName = '" + userName + "'");
		if (result != null && result.size() > 0)
		 {
			 return true;
		 }
		 
		return false;
    }

	
	public User getByUserName(String userName)
    {
	
		User userTable=this.findOneByKey("userName",userName);
	
		return userTable;
    }
	
	/****获取对应该服务器的帐号******/
	public User getByAccoutAndZoneId(String account,int zoneId)
    {
		List<User> list=this.findListByKey("account",account);
		if(list!=null)
		{
			for(int i=0;i<list.size();i++)
			{
				if(list.get(i).getZoneId()==zoneId)
				{
					return list.get(i);
				}
			}
			
		}
		
		return null;
    }
	

 
	public User getByUserId(String userId)
    {
    	
    	return this.getObject(userId);
     
    }

	public List<User> getAllUserTable()
    {
		String sql = "SELECT * from user ORDER BY level DESC limit 300";
		return this.findBySql(sql);
//		return this.getAll();
    }
	

	public boolean saveOrUpdate(User target)  {
		return super.saveOrUpdate(target);
	}

	/**
	 * 查询注册的人数
	 * @param registeredTime
	 * @return
	 */
	public Map<Long, Integer> queryRegistered(List<Long> registeredTime) {
		Map<Long, Integer> map = new HashMap<Long, Integer>();
		for (Long time : registeredTime) {
			String sql = "select * from user where createTime>= " + time
					+ " and createTime <=" + (time + 24 * 60 * 60 * 1000);
			List<User> result = findBySql(sql);
			map.put(time, result.size());
		}

		return map;
	}
	
	/**
	 * 查询等级分布
	 * @return
	 */
	public Map<Integer, Integer> queryLevelSpread() {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		String sql = "select level, count(*) as num from `user` GROUP BY `level`";
		List<Map<String, Object>> queryForList = queryForList(sql);
		for (Map<String, Object> map : queryForList) {
			int level = 0;
			int num = 0;
			for (Iterator<Entry<String, Object>> iterator = map.entrySet()
					.iterator(); iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				if (entry.getKey().equals("level")) {
					level = Integer.parseInt(entry.getValue().toString());
				}
				if (entry.getKey().equals("num")) {
					num = Integer.parseInt(entry.getValue().toString());
				}
			}
			result.put(level, num);
		}
		return result;
	}
	
	/**
	 * 查询创建角色
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<User> queryCreateRole(long startTime, long endTime){
		List<User> result = new ArrayList<User>();
		String sql = "select * from `user` where createTime > " + startTime
				+ " and createTime < " + endTime;
		result = findBySql(sql);
		return result;
	}
	/**
	 * 查询all角色
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<User> queryAll(){
		List<User> result = new ArrayList<User>();
		String sql = "select * from `user` where userid like '"+GameManager.getServerId()+"%'";
		result = findBySql(sql);
		return result;
	}
}
