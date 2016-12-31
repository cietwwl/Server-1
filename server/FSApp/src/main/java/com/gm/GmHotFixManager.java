package com.gm;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

import com.gm.util.GmUtils;
import com.log.GameLog;
import com.rw.fsutil.common.Pair;
import com.rw.manager.ServerVersionConfig;

public class GmHotFixManager {
	
	private static final String _MULTIPLE_TIME_HOT_FIX = "com/gm/multipletimeshotfix"; // 可以多次执行的hot fix
	private static final String _ONE_TIME_HOT_FIX = "com/gm/onetimehotfix"; // 只执行的hot fix
	
	public static void serverStartComplete() throws Exception {
		deleteOldVersionHotFix();
		executeHotFix();
	}
	
	private static void deleteOldVersionHotFix(String path) {
		URL url = ClassLoader.getSystemResource(path);
		File packageFile = new File(url.getFile());
		File[] files = packageFile.listFiles();
		File temp;
		for (int i = 0; i < files.length; i++) {
			temp = files[i];
			if (!temp.getName().contains("package-info")) {
				files[i].delete();
			}
		}
	}
	
	// 删除旧版本的hot fix文件
	private static void deleteOldVersionHotFix() throws Exception {
		Pair<String, List<String>> pair = GmUtils.getHotUpdateInfo();
		String version = ServerVersionConfig.getInstance().getVersion();
		String hotFixVersion = pair.getT1();
		if (!version.equals(hotFixVersion)) {
			deleteOldVersionHotFix(_MULTIPLE_TIME_HOT_FIX);
			deleteOldVersionHotFix(_ONE_TIME_HOT_FIX);
		}
	}

	/**
	 * 
	 * 自动执行热更任务
	 * 
	 * @throws Exception
	 */
	private static void executeHotFix() throws Exception {
		URL url = ClassLoader.getSystemResource(_MULTIPLE_TIME_HOT_FIX);
		String packagePath = url.getFile();
		File packageFile = new File(url.getFile());
		File[] files = packageFile.listFiles();
		if (files.length > 0) {
			String systemPath = packagePath.substring(1, packagePath.indexOf("com")).replace("/", File.separator);
			File temp;
			for (int i = 0, length = files.length; i < length; i++) {
				temp = files[i];
				if (temp.getName().endsWith(".class")) {
					String classPath = temp.getPath().replace(systemPath, "").replace("\\", ".").replace("/", ".").replace(".class", "");
					Class<?> loadClass = ClassLoader.getSystemClassLoader().loadClass(classPath);
					if (Callable.class.isAssignableFrom(loadClass)) {
						// 是Callable类型的任务才执行
						@SuppressWarnings("unchecked")
						Callable<? extends Object> task = (Callable<? extends Object>) loadClass.newInstance();
						Object result = task.call();
						GameLog.info("GmHotFixManager", "executeHotFix", "自动执行热更，class：" + classPath + "，执行结果：" + result);
					}
				}
			}
		}
	}
}
