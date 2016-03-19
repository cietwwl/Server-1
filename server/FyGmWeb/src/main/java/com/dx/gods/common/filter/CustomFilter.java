package com.dx.gods.common.filter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.dx.gods.common.utils.GlobalValue;

public class CustomFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		GlobalValue.CONFIG_PATH = filterConfig.getServletContext().getRealPath("")+"/WEB-INF/conf/serverconfig.xml";
		//GlobalValue.setVersionConfigPath(filterConfig.getServletContext().getRealPath("")+"/WEB-INF/conf/"+GlobalValue.VERSION_CONFIG_FILE_NAME);
		GlobalValue.initConfig(true);
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
