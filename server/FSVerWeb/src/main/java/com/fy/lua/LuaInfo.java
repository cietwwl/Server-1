package com.fy.lua;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class LuaInfo {
	private String channel;
	private String fileslocation;
	private String lualocation;
	private String filesmd5;
	private long filesize;
	private String luamd5;
	private long luasize;
	private String luaFilesName;
	private String luaFileMapName;

	public String getLuaFilesName() {
		return luaFilesName;
	}

	public void setLuaFilesName(String luaFilesName) {
		this.luaFilesName = luaFilesName;
	}

	public String getLuaFileMapName() {
		return luaFileMapName;
	}

	public void setLuaFileMapName(String luaFileMapName) {
		this.luaFileMapName = luaFileMapName;
	}

	public String getFileslocation() {
		return fileslocation;
	}

	public void setFileslocation(String fileslocation) {
		this.fileslocation = fileslocation;
	}

	public String getLualocation() {
		return lualocation;
	}

	public void setLualocation(String lualocation) {
		this.lualocation = lualocation;
	}

	public String getFilesmd5() {
		return filesmd5;
	}

	public void setFilesmd5(String filesmd5) {
		this.filesmd5 = filesmd5;
	}

	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}

	public String getLuamd5() {
		return luamd5;
	}

	public void setLuamd5(String luamd5) {
		this.luamd5 = luamd5;
	}

	public long getLuasize() {
		return luasize;
	}

	public void setLuasize(long luasize) {
		this.luasize = luasize;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	private static Field[] fields = null;

	public static LuaInfo fromFile(File file) throws IOException, Exception {

		try {
			if (fields == null) {
				Field[] declaredFields = LuaInfo.class.getDeclaredFields();
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
		LuaInfo version = new LuaInfo();
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
					Class<?> clazz = (Class<?>) fieldTmp.getType();
					if (clazz == long.class || clazz == Long.class) {
						fieldTmp.set(version, Long.parseLong(value));
					} else {
						fieldTmp.set(version, value);
					}

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		} finally {
			reader.close();
		}

		return version;

	}
}
