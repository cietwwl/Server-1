package com.fy.address;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fy.version.VersionDao;

public class ChannelAddressInfoDao {

	private static Field[] fields = null;
	public static Map<String, ChannelAddressInfo> fromFile(File file) throws IOException, Exception {

		try {
			if (fields == null) {
				Field[] declaredFields = ChannelAddressInfo.class.getDeclaredFields();
				for (Field field : declaredFields) {
					field.setAccessible(true);
				}
				fields = declaredFields;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}
		
		

		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String channelName = file.getName();
		channelName = channelName.replace(".path", "");
		
		
		Map<String, ChannelAddressInfo> ChannelAddressMap = new HashMap<String, ChannelAddressInfo>();
		//<version, map<name, value>>
		HashMap<String, HashMap<String, String>> pathMap = new HashMap<String, HashMap<String, String>>();
		
		try {
			
			HashMap<String, String> propMap = new HashMap<String, String>();
			String line = reader.readLine();
			
			ChannelAddressInfo channelAddressInfo = new ChannelAddressInfo();
			channelAddressInfo.setChannel(channelName);
			String packageName = VersionDao.DEFAULT_PACKAGENAME;
			
			while (line != null && !line.equals("")) {
				String[] split = line.split("=");
				if (split.length == 2) {
					String valueName = split[0].trim();
					String value = split[1].trim();
					if(valueName.equals("packageName")){
						ChannelAddressMap.put(packageName, channelAddressInfo);
						pathMap.put(packageName, propMap);
						packageName = value;
						propMap = new HashMap<String, String>();
						channelAddressInfo = new ChannelAddressInfo();
					}else{
						propMap.put(valueName, value);
					}
				}
				line = reader.readLine();
			}
			ChannelAddressMap.put(packageName, channelAddressInfo);
			pathMap.put(packageName, propMap);
			
			for (Iterator<Entry<String, HashMap<String, String>>> iterator = pathMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, HashMap<String, String>> entry = iterator.next();
				HashMap<String, String> map = entry.getValue();
				String key = entry.getKey();
				ChannelAddressInfo info = ChannelAddressMap.get(key);
				if(info == null){
					continue;
				}
				
				for (Field fieldTmp : fields) {

					String value = map.get(fieldTmp.getName());
					if (value != null) {
						fieldTmp.set(info, value);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		} finally {
			reader.close();
		}
		
		return ChannelAddressMap;
	}
	
}
