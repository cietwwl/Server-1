package com.gm;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.gm.task.GMRuntimeUpdate;
import com.gm.util.GmUtils;
import com.gm.util.HotFixHistoryRecord;
import com.log.GameLog;
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
		HotFixHistoryRecord record = GmUtils.getHotUpdateInfoFromHistoryRecord();
		if (record != null) {
			String version = ServerVersionConfig.getInstance().getVersion();
			if (!version.equals(record.getVersion())) {
				deleteOldVersionHotFix(_MULTIPLE_TIME_HOT_FIX);
				deleteOldVersionHotFix(_ONE_TIME_HOT_FIX);
				Map<String, Long> map = Collections.emptyMap();
				GmUtils.recordHotfixHistory(map);
			} else {
				GMRuntimeUpdate.addHistories(record.getHotfixHistories());
			}
		}
	}

	/**
	 * 
	 * 自动执行热更任务
	 * 
	 * @throws Exception
	 */
	private static void executeHotFix() throws Exception {
		List<Class<? extends Callable<?>>> multipleTimesHotFixes = GmUtils.getMultipleTimesHotFixes();
		if (multipleTimesHotFixes.size() > 0) {
			Map<String, Long> executeRecords = new LinkedHashMap<String, Long>();
			long currentTimeMillis = System.currentTimeMillis();
			for (Class<? extends Callable<?>> clazz : multipleTimesHotFixes) {
				Object obj = clazz.newInstance().call();
				GameLog.info("GmHotFixManager", "executeHotFix", "自动执行hotfix，class=" + clazz.getName() + "，结果=" + obj);
				executeRecords.put(clazz.getName(), currentTimeMillis);
			}
			GMRuntimeUpdate.addHistories(executeRecords);
			GmUtils.recordHotfixHistory(GMRuntimeUpdate.getHistories());
		}
	}
}
