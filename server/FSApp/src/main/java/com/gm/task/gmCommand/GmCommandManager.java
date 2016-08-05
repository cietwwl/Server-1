package com.gm.task.gmCommand;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.log.GameLog;

@SuppressWarnings("rawtypes")
public class GmCommandManager {
	
	public final static Map<String, IGmCommand> CommandMap = new HashMap<String, IGmCommand>();
	
	@SuppressWarnings("unchecked")
	public static void loadCommandClass(){
		
		try{
		List<Class> list = getClasssFromPackage("com.gm.task.gmCommand");
		for (Class value : list) {
			Annotation annotation = value.getAnnotation(GmCommand.class);
			if(annotation != null){
				IGmCommand obj = (IGmCommand)value.newInstance();
				CommandMap.put(obj.getName(), obj);
			}
		}
		}catch(Exception ex){
			GameLog.error("GM", "-1", ex.getMessage(), null);
		}
	}
	
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
	
	private static List<Class> getClasssFromPackage(String pack) {
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
	 * 执行gm command
	 * @param param
	 * @return
	 */
	public static String executeGMCommand(String param){
		int firstSplit = param.indexOf('#');
		String commandName = param.substring(0, firstSplit);
		String commandParam = param.substring(firstSplit+1);
		IGmCommand handler = CommandMap.get(commandName);
		return handler.executeGMCommand(commandParam);
	}
}
