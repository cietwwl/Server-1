package com.server.service.impl;

import java.util.Date;

import com.server.dao.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.beans.User;
import com.server.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserMapper dao;



	public boolean doUserLogin(User u) {
		
		User user = dao.selectByPrimaryKey(u.getAccount());
		if(user == null){
			return false;
		}else if(StringUtils.equals(user.getPassword(), u.getPassword())){
			Date date = new Date(System.currentTimeMillis());
			user.setLastlogintime(date);
			dao.updateByPrimaryKeySelective(user);
			return true;
		}
		return false;
	}





	public boolean checkUserExist(String account) {
		User user = dao.selectByPrimaryKey(account);
		if(user != null){
			return true;
		}
		return false;
	}





	public boolean registerUser(User user) {
		Date date = new Date(System.currentTimeMillis());
		user.setLastlogintime(date);
		user.setCreatetime(date);
		int selective = dao.insertSelective(user);
		return selective <= 0;
	}

	
}
