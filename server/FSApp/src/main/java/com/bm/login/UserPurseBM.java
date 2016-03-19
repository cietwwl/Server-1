package com.bm.login;

import org.apache.commons.lang3.StringUtils;

import com.rwbase.dao.user.account.UserPurse;
import com.rwbase.dao.user.account.UserPurseDAO;

public class UserPurseBM {
	
	private UserPurseDAO userPurseDAO = UserPurseDAO.getInstance();
	private static  UserPurseBM instance = new UserPurseBM();
	private UserPurseBM(){}
	public static UserPurseBM getInstance(){
		return instance;
	}
	
	public UserPurse getUserPurseById(String userId){
		if(StringUtils.isBlank(userId)){
			return null;
		}
		UserPurse userPurse = userPurseDAO.get(userId);
		return userPurse;
	}
	
	public boolean updateUserPurse(UserPurse userPurse){
		return userPurseDAO.update(userPurse);
	}
	
	
}
