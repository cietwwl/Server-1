package com.rw.manager;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.bm.serverStatus.ServerStatus;
import com.bm.serverStatus.ServerStatusMgr;
import com.rw.service.gm.GMHandler;

/*
 * @author HC
 * @date 2016年3月29日 上午11:41:44
 * @Description 
 */
public class ServerSwitch {
	private static boolean serverstatus;// 服务器状态
	private static boolean gmSwitch;// 打开GM
	private static boolean giftCodeOpen = true;// 是否开启兑换码

	public static void initProperty() {
		Resource resource = new ClassPathResource("switch.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			serverstatus = Boolean.parseBoolean(props.getProperty("serverStatus"));
			gmSwitch = Boolean.parseBoolean(props.getProperty("gmSwitch"));
			// 兑换码开启
			giftCodeOpen = props.getProperty("giftCodeOpen").equalsIgnoreCase("true");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void initLogic() {
		if (serverstatus) {
			ServerStatusMgr.setStatus(ServerStatus.OPEN);
		} else {
			ServerStatusMgr.setStatus(ServerStatus.CLOSE);
		}

		GMHandler.getInstance().setActive(gmSwitch);
	}

	public static boolean isGiftCodeOpen() {
		return giftCodeOpen;
	}
}