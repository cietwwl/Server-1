package com.rw.platform;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.bm.notice.NoticeMgr;
import com.rw.fsutil.log.EngineLoggerFactory;
import com.rw.netty.client.ClientManager;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.LogService;
import com.rwbase.common.config.CfgMgr;
import com.rwbase.gameworld.GameWorldFactory;

public class PlatformFactory {
	private static PlatformService service;

	private static int port;
	private static int httpPort;
	private static int platform_connect_num = 5;
	private static int threadSize;
	private static String logServerIp;
	private static int logServerPort;
	private static int defaultCapacity;
	private static int rounterPort;//直通车服监听端口

	public static ClientManager clientManager;

	public static void init() {
		GameWorldFactory.init(64, 16);
		Resource resource = new ClassPathResource("server.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			threadSize = Integer.parseInt(props.getProperty("threadSize"));
			port = Integer.parseInt(props.getProperty("port"));
			httpPort = Integer.parseInt(props.getProperty("httpPort"));
			platform_connect_num = Integer.parseInt(props
					.getProperty("platform_connect_num"));
			logServerIp = props.getProperty("logServerIp");
			logServerPort = Integer
					.parseInt(props.getProperty("logServerPort"));
			String pDefaultCapacity = props.getProperty("defaultCapacity");
			if (pDefaultCapacity != null) {
				defaultCapacity = Integer.parseInt(pDefaultCapacity);
			}
			String giftRounterPort = props.getProperty("rounterPort");
			if(giftRounterPort != null){
				rounterPort = Integer.parseInt(giftRounterPort);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (service == null) {
			service = new PlatformService(threadSize,
					EngineLoggerFactory.getLogger("Platform"));

		}

		clientManager = new ClientManager();
		BILogMgr.getInstance().initLogger();
		LogService.getInstance().initLogService();
		CfgMgr.getInstance().init();
		NoticeMgr.getInstance().initNotice();
	}

	public static PlatformService getPlatformService() {
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

	public static int getDefaultCapacity() {
		return defaultCapacity;
	}

	public static int getRounterPort() {
		return rounterPort;
	}
}
