package com.rw.utils;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CommonUtils {

	@SuppressWarnings("rawtypes")
	private static void findClassInPackageByFile(String packageName, String filePath, final boolean recursive, List<Class> clazzs) {
		File dir = new File(filePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// 在给定的目录下找到所有的文件，并且进行条件过滤
		File[] dirFiles = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
				boolean acceptClass = file.getName().endsWith("class");// 接受class文件
				return acceptDir || acceptClass;
			}
		});
		for (File file : dirFiles) {
			if (file.isDirectory()) {
				findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, clazzs);
			} else {
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					clazzs.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static List<Class> getClasssFromPackage(String pack) {
		List<Class> clazzs = new ArrayList<Class>();

		// 是否循环搜索子包
		boolean recursive = true;

		// 包名字
		String packageName = pack;
		// 包名对应的路径名称
		String packageDirName = packageName.replace('.', '/');

		Enumeration<URL> dirs;

		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();

				String protocol = url.getProtocol();

				if ("file".equals(protocol)) {
					System.out.println("file类型的扫描");
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					findClassInPackageByFile(packageName, filePath, recursive, clazzs);
				} else if ("jar".equals(protocol)) {
					System.out.println("jar类型的扫描");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return clazzs;
	}
	
	/**
	 * 比较表结构是否一样
	 * @param oriMap
	 * @param tarMap
	 * @return
	 */
	public static boolean compareTableField(Map<String, Class<?>> oriMap, Map<String, Class<?>> tarMap){
		if(oriMap.size() != tarMap.size()){
			return false;
		}
		for (Iterator<Entry<String, Class<?>>> iterator = oriMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Class<?>> entry = iterator.next();
			String key = entry.getKey();
			if(tarMap.get(key) == null){
				return false;
			}
		}
		return true;
	}
	
	public static Object processStringEscape(Object value){
		if(value == null){
			return value;
		}
		return value.toString().replace("\"", "\\\"");
	}
	
	public static int getTableIndex(String userId, int tableCount) {
		boolean isNumber = true;
		int len = userId.length();
		for (int i = 0; i < len; i++) {
			char c = userId.charAt(i);
			if (!Character.isDigit(c)) {
				isNumber = false;
				break;
			}
		}
		int tableIndex;
		if (isNumber) {
			Long id = Long.parseLong(userId);
			tableIndex = (int) (id % tableCount);
		} else {
			tableIndex = Math.abs(userId.hashCode() % tableCount);
		}
		return tableIndex;
	}
}
