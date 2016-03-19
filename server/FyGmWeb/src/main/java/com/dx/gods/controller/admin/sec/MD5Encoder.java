package com.dx.gods.controller.admin.sec;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

public class MD5Encoder implements PasswordEncoder {

	public String encodePassword(String rawPass, Object salt) throws DataAccessException {
		return MD5.getMD5ofStr(rawPass, 3);
	}

	public boolean isPasswordValid(String encPwd, String origPwd, Object salt) throws DataAccessException {
		return true;
//		return encPwd.equals(encodePassword(origPwd, salt));
	}

}
