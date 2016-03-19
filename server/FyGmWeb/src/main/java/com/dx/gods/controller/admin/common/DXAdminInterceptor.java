package com.dx.gods.controller.admin.common;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dx.gods.controller.admin.sec.ActionAuths;
import com.dx.gods.controller.admin.sec.ActionAuthsBLL;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class DXAdminInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = -7124772283750917775L;
	private ActionAuthsBLL actionAuthsBLL;

	public String intercept(ActionInvocation invocation) throws Exception {
		try {
			Collection<GrantedAuthority> set = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
			String action = invocation.getProxy().getActionName();
			ActionAuths actionAuths = actionAuthsBLL.getByName(action);
			
			if (actionAuths != null && actionAuths.getRoles() != null) {
				for (GrantedAuthority grantedAuthority : set) {
					for (String s : actionAuths.getAuths()) {
						if (grantedAuthority.getAuthority().equals(s)) {
							String invoke = invocation.invoke();
							return invoke;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "input";
	}


	public void setActionAuthsBLL(ActionAuthsBLL actionAuthsBLL) {
		this.actionAuthsBLL = actionAuthsBLL;
	}

}
