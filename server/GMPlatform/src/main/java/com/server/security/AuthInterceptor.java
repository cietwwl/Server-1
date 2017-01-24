package com.server.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.server.constant.GlobalKey;

/**
 * 自定义的拦截器，这里只是做了登录检查，后期开放了shiro，这个可以不用
 * @author Alex
 * 2017年1月19日 下午12:13:29
 */
public class AuthInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Object attribute = request.getSession().getAttribute(GlobalKey.USER_SESSION_KEY);
		if(attribute == null){
			//还没有登录，重定向到登录页面
			response.sendRedirect("../index.html");
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		
	}

}
