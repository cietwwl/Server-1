package com.rw.manager;

import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.bm.serverStatus.ServerStatus;
import com.bm.serverStatus.ServerStatusMgr;
import com.rw.fsutil.dao.cache.CacheLoggerSwitch;
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
	private static boolean checkCfg = false;
	private static boolean printEncode = false;
	private static boolean openCacheLog = true;
	private static boolean openTraceLogger = true;
	private static boolean openTargetSell = false;//是否开启精准营销
	private static int targetSellTestModel;//精准营销是否为测试模式：0否，1是
	private static boolean testCharge = false;//是否测试充值

	public static void initProperty() {
		Resource resource = new ClassPathResource("switch.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			serverstatus = Boolean.parseBoolean(props.getProperty("serverStatus"));
			gmSwitch = Boolean.parseBoolean(props.getProperty("gmSwitch"));
			// 兑换码开启
			giftCodeOpen = props.getProperty("giftCodeOpen").equalsIgnoreCase("true");
			checkCfg = props.getProperty("checkCfg").equalsIgnoreCase("true");
			openCacheLog = Boolean.parseBoolean(props.getProperty("openCacheLog"));
			String openTraceLogger_ = props.getProperty("openTraceLogger");
			if (openTraceLogger_ != null) {
				openTraceLogger = Boolean.parseBoolean(openTraceLogger_);
			}
			openTargetSell = Boolean.parseBoolean(props.getProperty("openTargetSell"));
			targetSellTestModel = Integer.parseInt(props.getProperty("targetSellTestModel"));
		} catch (Exception e) {
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
		CacheLoggerSwitch.getInstance().setCacheLoggerSwitch(openCacheLog);
	}

	public static boolean isGiftCodeOpen() {
		return giftCodeOpen;
	}

	public static boolean isCheckCfg() {
		return checkCfg;
	}

	public static boolean isPrintEncode() {
		return printEncode;
	}

	public static boolean isOpenTraceLogger() {
		return openTraceLogger;
	}

	public static int getTargetSellTest() {
		return targetSellTestModel;
	}

	public static boolean isOpenTargetSell() {
		return openTargetSell;
	}

	public static boolean isTestCharge() {
		return testCharge;
	}

	public static void setTestCharge(boolean testCharge) {
		ServerSwitch.testCharge = testCharge;
	}

	
}