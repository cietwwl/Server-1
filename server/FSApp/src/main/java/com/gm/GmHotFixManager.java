package com.gm;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.gm.task.GMRuntimeUpdate;
import com.gm.util.GmUtils;
import com.gm.util.HotFixFileRecord;
import com.gm.util.HotFixHistoryRecord;
import com.log.GameLog;
import com.rw.fsutil.shutdown.ShutdownService;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.manager.ServerVersionConfig;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class GmHotFixManager {
	
	private static final String _MULTIPLE_TIME_HOT_FIX = "com/gm/multipletimeshotfix"; // 可以多次执行的hot fix
	private static final String _ONE_TIME_HOT_FIX = "com/gm/onetimehotfix"; // 只执行的hot fix
	
	private static final String _MUlTIPLE_TIME_HOT_FIX_PACKAGE = _MULTIPLE_TIME_HOT_FIX.replace("/", ".");
	private static final String _ONE_TIME_HOT_FIX_PACKAGE = _ONE_TIME_HOT_FIX.replace("/", ".");
	
	public static void serverStartComplete() throws Exception {
		deleteOldVersionHotFix();
		executeHotFix();
		ShutdownService.registerShutdownService(new GmShutdownHandler());
	}
	
	public static void shutdown() {
		List<String> multipleTimesFiles = loadAllFiles(_MULTIPLE_TIME_HOT_FIX);
		List<String> ontimeFiles = loadAllFiles(_ONE_TIME_HOT_FIX);
		HotFixFileRecord record = new HotFixFileRecord();
		record.setVersion(ServerVersionConfig.getInstance().getVersion());
		record.setOntimeFiles(ontimeFiles);
		record.setMultipleTimeFiles(multipleTimesFiles);
		GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.HOTFIX_FILES, JsonUtil.writeValue(record));
		
	}
	
	private static List<String> loadAllFiles(String path) {
		List<String> list = new ArrayList<String>();
		URL url = ClassLoader.getSystemResource(path);
		File packageFile = new File(url.getFile());
		File[] files = packageFile.listFiles();
		File temp;
		for (int i = 0; i < files.length; i++) {
			temp = files[i];
			if (!temp.getName().contains("package-info")) {
				list.add(temp.getName());
			}
		}
		return list;
	}
	
	private static void deleteOtherOldFiles(String path, List<String> fileList) {
		URL url = ClassLoader.getSystemResource(path);
		File packageFile = new File(url.getFile());
		File[] files = packageFile.listFiles();
		File file;
		for (int i = 0; i < files.length; i++) {
			file = files[i];
			if (fileList.contains(file.getName())) {
				file.delete();
				GameLog.info("GmHotFixManager", "deleteOtherOldFiles", "删除旧版本的hotfix残留文件：" + file.getName());
			}
		}
	}

	private static void deleteOldVersionHotFix(String path, HotFixHistoryRecord record) {
		URL url = ClassLoader.getSystemResource(path);
		File packageFile = new File(url.getFile());
		File[] files = packageFile.listFiles();
		File temp;
		Map<String, Long> hotfixHistories = record.getHotfixHistories();
		String packageName = path.equals(_MULTIPLE_TIME_HOT_FIX) ? _MUlTIPLE_TIME_HOT_FIX_PACKAGE : _ONE_TIME_HOT_FIX_PACKAGE;
		for (int i = 0; i < files.length; i++) {
			temp = files[i];
			if (!temp.getName().contains("package-info")) {
				String classWholePath = packageName + "." + temp.getName().replace(".class", "");
				if (hotfixHistories.containsKey(classWholePath)) {
					files[i].delete();
					GameLog.info("GmHotFixManager", "deleteOldVersionHotFix", "删除旧版本的hotfix文件：" + classWholePath);
				}
			}
		}
	}
	
	
	// 删除旧版本的hot fix文件
	private static void deleteOldVersionHotFix() throws Exception {
		HotFixHistoryRecord record = GmUtils.getHotUpdateInfoFromHistoryRecord();
		if (record != null) {
			String version = ServerVersionConfig.getInstance().getVersion();
			if (!version.equals(record.getVersion())) {
				deleteOldVersionHotFix(_MULTIPLE_TIME_HOT_FIX, record);
				deleteOldVersionHotFix(_ONE_TIME_HOT_FIX, record);
				Map<String, Long> map = Collections.emptyMap();
				GmUtils.recordHotfixHistory(map);
				
				String attribute = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.HOTFIX_FILES);
				if (attribute != null && (attribute = attribute.trim()).length() > 0) {
					HotFixFileRecord fileRecord = JsonUtil.readValue(attribute, HotFixFileRecord.class);
					deleteOtherOldFiles(_MULTIPLE_TIME_HOT_FIX, fileRecord.getMultipleTimeFiles());
					deleteOtherOldFiles(_ONE_TIME_HOT_FIX, fileRecord.getOntimeFiles());
				} else {
					// 1.0.3.0之前的版本没有记录这个数据
					// 为了避免残留旧的hotfix文件，所以把所有文件都删除
					deleteOtherOldFiles(_MULTIPLE_TIME_HOT_FIX, loadAllFiles(_MULTIPLE_TIME_HOT_FIX));
					deleteOtherOldFiles(_ONE_TIME_HOT_FIX, loadAllFiles(_ONE_TIME_HOT_FIX));
				}
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
