package com.rw.platform;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.rw.fsutil.log.EngineLoggerFactory;
import com.rw.netty.client.ClientManager;
import com.rw.service.log.LogService;

public class PlatformFactory {
	private static PlatformService service;
	
	private static int port;
	private static int httpPort;
	private static int platform_connect_num = 5;
	private static int threadSize;
	private static String logServerIp;
	private static int logServerPort;
	
	public static ClientManager clientManager;
	
	public static void init() {
		
		Resource resource = new ClassPathResource("server.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			threadSize = Integer.parseInt(props.getProperty("threadSize"));
			port = Integer.parseInt(props.getProperty("port"));
			httpPort = Integer.parseInt(props.getProperty("httpPort"));
			platform_connect_num = Integer.parseInt(props.getProperty("platform_connect_num"));
			logServerIp = props.getProperty("logServerIp");
			logServerPort = Integer.parseInt(props.getProperty("logServerPort"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (service == null) {
			service = new PlatformService(threadSize,
					EngineLoggerFactory.getLogger("Platform"));
			
		}
		
		clientManager = new ClientManager();
		
		LogService.getInstance().initLogService();
	}
	
	public static PlatformService getPlatformService(){
		return service;
	}

	public static int getPort() {
		return port;
	}

	public static int getHttpPort() {
		return httpPort;
	}

	public static int getPlatform_connect_num() {
		return platform_connect_num;
	}

	public static String getLogServerIp() {
		return logServerIp;
	}

	public static int getLogServerPort() {
		return logServerPort;
	}
}
