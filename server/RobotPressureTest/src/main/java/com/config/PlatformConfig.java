package com.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import com.rw.common.RobotLog;

/*
 * 平台的配置信息
 * @author HC
 * @date 2015年12月14日 下午3:49:22
 * @Description 
 */
public class PlatformConfig {
	private static String platformHost;// 平台Host
	private static String platformPort;// 端口
//	private static String serverIds;// 平台Host
	private static String zoneId;

	// private static String robotNameStart;// 创建机器人的抬头

	public static void InitPlatformConfig() {
		String path = PlatformConfig.class.getResource("/").getPath();
		RobotLog.info("文件的路径：" + path);
		File file = new File(path + "PlatformConfig.properties");
		if (!file.exists()) {
			RobotLog.info("未发现<PlatformConfig.properties>配置文件");
			System.exit(0);
			return;
		}

		Properties p = new Properties();
		try {
			p.load(new FileInputStream(file));

			// 对变量进行赋值操作
			Field[] declaredFields = PlatformConfig.class.getDeclaredFields();
			for (Field field : declaredFields) {
				String name = field.getName();
				String property = p.getProperty(name);
				field.set(null, property);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		RobotLog.info("平台Host：" + platformHost + " 端口：" + platformPort);
	}

	/**
	 * 获取平台的Host
	 * 
	 * @return
	 */
	public static String getPlatformHost() {
		return platformHost;
	}

	/**
	 * 获取平台的端口
	 * 
	 * @return
	 */
	public static int getPlatformPort() {
		return Integer.parseInt(platformPort);
	}

	/**
	 * 获取机器人的抬头
	 * 
	 * @return
	 */
	public static String getRobotNameStart() {
		// return (robotNameStart == null || robotNameStart.isEmpty()) ? "robot" : robotNameStart;
		return "robot";
	}

	public static void main(String[] args) {
		InitPlatformConfig();
	}
	
	public static int getZoneId(){
		return Integer.valueOf(zoneId);
	}

//	public static List<Integer> getServerIdList() {
//		List<Integer> serverIdList = new ArrayList<Integer>();
//		if(serverIds!=null){
//			String[] split = serverIds.split(",");
//			for (String idTmp : split) {
//				serverIdList.add(Integer.valueOf(idTmp.trim()));
//			}
//		}
//		
//		return serverIdList;
//	}
	
}