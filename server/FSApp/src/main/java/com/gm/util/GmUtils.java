package com.gm.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.ItemCfgHelper;
import com.rw.fsutil.log.GmLog;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.manager.ServerVersionConfig;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class GmUtils {
	
	public static final String MULTIPLE_TIME_HOT_FIX = "com/gm/multipletimeshotfix"; // 可以多次执行的hot fix
	public static final String ONE_TIME_HOT_FIX = "com/gm/onetimehotfix"; // 只执行的hot fix
	
	public static final String recordPath = "./.hotfix_history";
	
	private static List<Class<? extends Callable<?>>> getHotFixClasses(String path) throws Exception {
		URL url = ClassLoader.getSystemResource(path);
		String packagePath = url.getFile();
		File packageFile = new File(url.getFile());
		File[] files = packageFile.listFiles();
		List<Class<? extends Callable<?>>> list;
		if (files.length > 0) {
			list = new ArrayList<Class<? extends Callable<?>>>(files.length);
			String systemPath = packagePath.substring(1, packagePath.indexOf("com")).replace("/", File.separator);
			ClassLoader loader = ClassLoader.getSystemClassLoader();
			File temp;
			for (int i = 0, length = files.length; i < length; i++) {
				temp = files[i];
				if (temp.getName().endsWith(".class") && !temp.getName().contains("$")) { // 是class并且非子类
					String classPath = temp.getPath().replace(systemPath, "").replace("\\", ".").replace("/", ".").replace(".class", "");
					if (classPath.startsWith(".")) {
						classPath = classPath.substring(1, classPath.length());
					}
					Class<?> loadedClass = loader.loadClass(classPath);
					if (Callable.class.isAssignableFrom(loadedClass)) {
						@SuppressWarnings("unchecked")
						Class<? extends Callable<?>> callableClass = (Class<? extends Callable<?>>) loadedClass;
						list.add(callableClass);
					}
				}
			}
		} else {
			list = Collections.emptyList();
		}
		return list;
	}
	
	public static List<Class<? extends Callable<?>>> getAllHotFixes() throws Exception {
		List<Class<? extends Callable<?>>> returnList = new ArrayList<Class<? extends Callable<?>>>();
		returnList.addAll(getHotFixClasses(ONE_TIME_HOT_FIX));
		returnList.addAll(getHotFixClasses(MULTIPLE_TIME_HOT_FIX));
		return returnList;
	}
	
	public static List<Class<? extends Callable<?>>> getMultipleTimesHotFixes() throws Exception {
		return getHotFixClasses(MULTIPLE_TIME_HOT_FIX);
	}
	
	public static HotFixHistoryRecord getHotUpdateInfoFromHistoryRecord() throws Exception {
//		Pair<String, Map<String, Long>> result = Pair.Create("", null);
//		File file = new File(recordPath);
//		String version = null;
//		if (file.exists()) {
//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
//			version = br.readLine();
//			String line;
//			Map<String, Long> list = new LinkedHashMap<String, Long>();
//			while ((line = br.readLine()) != null) {
//				line = line.trim();
//				String[] record = line.split(":");
//				list.put(record[1], Long.parseLong(record[2]));
//			}
//			br.close();
//			result.setT1(version);
//			result.setT2(list);
//		}
//		return result;
		String attribute = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.HOTFIX_HISTORY);
		if (attribute != null && (attribute = attribute.trim()).length() > 0) {
			return JsonUtil.readValue(attribute, HotFixHistoryRecord.class);
		}
		return null;
	}
	
	public static void recordHotfixHistory(Map<String, Long> hotUpdateHistory) throws Exception {
//		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(GmUtils.recordPath, false), "UTF-8");
//		BufferedWriter bw = new BufferedWriter(osw);
//		bw.write(ServerVersionConfig.getInstance().getVersion());
//		bw.newLine();
//		for (Iterator<String> keyItr = hotUpdateHistory.keySet().iterator(); keyItr.hasNext();) {
//			String path = keyItr.next();
//			Long time = hotUpdateHistory.get(path);
//			bw.write("classPath:" + path + ":" + time);
//			bw.newLine();
//		}
//		bw.flush();
//		bw.close();
		HotFixHistoryRecord record = new HotFixHistoryRecord();
		record.setVersion(ServerVersionConfig.getInstance().getVersion());
		record.setHotfixHistories(hotUpdateHistory);
		GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.HOTFIX_HISTORY, JsonUtil.writeValue(record));
	}

	
	public static boolean parseBoolean(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		boolean result = false;
		if(object == null){
			return result;
		}else{
			
			try {
				result = Boolean.parseBoolean(object.toString());
			} catch (Exception ex) {
				result = false;
			}
			return result;
		}
	}
	
	public static String parseString(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		if(object == null){
			return "";
		}else{
			return object.toString();
		}
	}
	
	public static int parseInt(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		if(object == null){
			return 0;
		}else{
			int value = 0;
			try {
				value = Integer.parseInt(object.toString());
			} catch (Exception ex) {
				value = 0;
			}
			return value;
		}
	}
	
	public static long parseLong(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		if(object == null){
			return 0;
		}else{
			long value = 0;
			try {
				value = Long.parseLong(object.toString());
			} catch (Exception ex) {
				value = 0;
			}
			return value;
		}
	}
	
	public static byte[] dataFormat(short protno, String json) throws UnsupportedEncodingException {
		// 包头计算：包头(short,4个字节) = 包体长度%255(1个字节) + 包体长度&0x00ffffff(3个字节)
		byte[] jsonbytes = json.getBytes("utf-8");
		short jsonLen = (short) jsonbytes.length;
		int bodyLength = 2 + 2 + jsonLen;
		bodyLength = 1;

		int header = ((bodyLength % 255) << 24) | (bodyLength & 0x00ffffff);
		ByteBuffer dataInfo = ByteBuffer.allocate(8 + jsonLen);
		// 转换为小端模式，默认为大端。
		// dataInfo.order(ByteOrder.LITTLE_ENDIAN);
		// 设置包头
		dataInfo.putInt(header);

		// 包体计算：包体=协议号(2个字节) + json字符串长度(2字节) + json字符串内容
		dataInfo.putShort(protno);
		dataInfo.putShort(jsonLen);

		dataInfo.put(jsonbytes);
		return dataInfo.array();
	}
	
	public static <T> T read(DataInputStream input, Class<T> clazz) throws IOException {
		T content = null;
		input.readInt();
		input.readShort();
		short jsonLength = input.readShort();

		byte[] jsonBody = new byte[jsonLength];
		input.read(jsonBody);
		String json = new String(jsonBody, "utf-8");
		content = FastJsonUtil.deserialize(json, clazz);
		GmLog.info("SocketHelper[read] 处理gm请求：" + json);
		return content;
	}
	
	public static boolean checkAttachItemIegal(String attachment){
		if (StringUtils.isNotBlank(attachment)) {
			String[] split = attachment.split(",");
			for (String itemTmp : split) {
				if (StringUtils.isEmpty(itemTmp)) {
					continue;
				}
				String[] itemTmpSplit = itemTmp.split("~");
				if (itemTmpSplit.length == 2) {
					int itemCode = Integer.valueOf(itemTmpSplit[0]);
					eSpecialItemId def = eSpecialItemId.getDef(itemCode);
					if (def != null) {
						if (def == eSpecialItemId.eSpecial_End) {
							return false;
						}
					}else if(ItemCfgHelper.isFashionSpecialItem(itemCode)){
						return true;
					} else {
						ItemBaseCfg itemBaseCfg = ItemCfgHelper
								.GetConfig(itemCode);
						if (itemBaseCfg == null) {
							return false;
						}
					}

				} else {
					return false;
				}
			}
		}
		return true;
	}
}
