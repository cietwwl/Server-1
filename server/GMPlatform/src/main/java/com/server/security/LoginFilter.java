package com.server.security;

import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.AccessControlFilter;

public class LoginFilter extends AccessControlFilter{
	
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		
		Subject subject = getSubject(request, response);
		String [] rolesArray = (String[]) mappedValue;
		if(rolesArray == null || rolesArray.length == 0){
			
			return true;
		}
		Set<String> asSet = CollectionUtils.asSet(rolesArray);
		
		for (String string : asSet) {
			if(subject.hasRole(string)){
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
	
		return true;
	}


	
}
