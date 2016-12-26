package com.fy.address;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ChannelAddressInfoDao {

	private static Field[] fields = null;
	public static ChannelAddressInfo fromFile(File file) throws IOException, Exception {

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
		ChannelAddressInfo channelAddressInfo = new ChannelAddressInfo();
		
		String name = file.getName();
		name = name.replace(".path", "");
		channelAddressInfo.setChannel(name);
		
		
		try {

			Map<String, String> propMap = new HashMap<String, String>();
			String line = reader.readLine();
			while (line != null && !line.equals("")) {
				String[] split = line.split("=");
				if (split.length == 2) {
					propMap.put(split[0].trim(), split[1].trim());
				}
				line = reader.readLine();
			}
			for (Field fieldTmp : fields) {

				String value = propMap.get(fieldTmp.getName());
				if (value != null) {
					fieldTmp.set(channelAddressInfo, value);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		} finally {
			reader.close();
		}
		
		return channelAddressInfo;
	}
	
}
