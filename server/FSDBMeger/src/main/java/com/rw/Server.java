package com.rw;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.rw.db.dao.DBMgr;
import com.rw.thread.DBThreadFactory;

public class Server {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		PropertyConfigurator.configure(Server.class.getClassLoader().getResource("log4j.properties"));
		new ClassPathXmlApplicationContext(new String[] {"classpath:applicationContext.xml"});
		DBThreadFactory.getInstance().init(16);
		DBMergeMgr.getInstance().StartMeger();
	}
}
