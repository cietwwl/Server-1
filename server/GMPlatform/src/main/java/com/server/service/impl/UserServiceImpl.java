package com.server.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.beans.User;
import com.server.dao.UserMapper;
import com.server.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserMapper dao;

	
	
	
	@Override
	public boolean doUserLogin(User u) {

		User user = dao.selectByPrimaryKey(u.getAccount());
		if(user == null){
			return false;
		}else if(StringUtils.equals(user.getPassword(), u.getPassword())){
			return true;
		}
		return false;
	}

}
