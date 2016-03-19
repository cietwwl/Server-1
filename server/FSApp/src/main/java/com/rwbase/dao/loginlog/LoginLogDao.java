package com.rwbase.dao.loginlog;

import java.util.ArrayList;
import java.util.List;

import com.rw.fsutil.cacheDao.DataRdbDao;
import com.rwbase.dao.user.User;

public class LoginLogDao extends DataRdbDao<LoginLog>{
	private static LoginLogDao m_instance = new LoginLogDao();
	
	public static LoginLogDao getInstance(){
		return m_instance;
	}
	
	public boolean saveOrUpdate(LoginLog target){
		return super.saveOrUpdate(target);
	}
	
	/**
	 * 查询指定时间的登陆情况
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<LoginLog> queryLoginLog(long startTime, long endTime){
		List<LoginLog> result = new ArrayList<LoginLog>();
		
		String sql = "select * from login_log where loginTime >= "+startTime+" and loginTime <= "+endTime;
		
		result = findBySql(sql);
		return result;
	}
	
	/**
	 * 查询指定角色指定时间的登陆情况
	 * @param startTime
	 * @param endTime
	 * @param list
	 * @return
	 */
	public List<LoginLog> queryLoginLog(long startTime, long endTime, List<User> list){
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		int count = 0;
		for (User user : list) {
			count++;
			sb.append(user.getUserId());
			if(count < list.size()){
				sb.append(",");
			}
		}
		sb.append(")");
		String sql = "select * from login_log where loginTime >= " + startTime
				+ " and loginTime <= " + endTime + " and userId in "
				+ sb.toString();
		List<LoginLog> result = new ArrayList<LoginLog>();
		result = findBySql(sql);
		return result;
	}
}
