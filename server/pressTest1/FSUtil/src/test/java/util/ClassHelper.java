package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;

public class ClassHelper {

	private static String template = "<bean class=\"className\"  init-method=\"initJsonCfg\" />";
	
	public static void main(String[] args) throws Exception {
		File targetDir = new File("D:\\fengshen\\fsbranch\\Server\\FSApp\\target\\classes\\com\\rwbase\\dao");
		
		getClasses(targetDir, CfgCsvDao.class);
	}

//	/**
//	 * 获取同一路径下所有子类或接口实现类
//	 *
//	 * @param intf
//	 * @return
//	 * @throws IOException
//	 * @throws ClassNotFoundException
//	 */
//	public static List<Class<?>> getAllAssignedClass(Class<?> cls)
//			throws IOException, ClassNotFoundException {
//		List<Class<?>> classes = new ArrayList<Class<?>>();
//		for (Class<?> c : getFiles(cls)) {
//			if (cls.isAssignableFrom(c) && !cls.equals(c)) {
//				classes.add(c);
//			}
//		}
//		return classes;
//	}
//
//	/**
//	 * 取得当前类路径下的所有类
//	 *
//	 * @param cls
//	 * @return
//	 * @throws IOException
//	 * @throws ClassNotFoundException
//	 */
//	public static List<Class<?>> getFiles(Class<?> cls) throws IOException,
//			ClassNotFoundException {
//		String pk = cls.getPackage().getName();
//		String path = pk.replace('.', '/');
//		ClassLoader classloader = Thread.currentThread()
//				.getContextClassLoader();
//		URL url = classloader.getResource(path);
//		return getClasses(new File(url.getFile()), pk);
//	}
//
//	/**
//	 * 迭代查找类
//	 *
//	 * @param dir
//	 * @param pk
//	 * @return
//	 * @throws ClassNotFoundException
//	 */
//	private static List<Class<?>> getClasses(File dir, String pk)throws ClassNotFoundException {
//		List<Class<?>> classes = new ArrayList<Class<?>>();
//		if (!dir.exists()) {
//			return classes;
//		}
//		for (File f : dir.listFiles()) {
//			if (f.isDirectory()) {
//				classes.addAll(getClasses(f, pk + "." + f.getName()));
//			}
//			String name = f.getName();
//			if (name.endsWith(".class")) {
//				classes.add(Class.forName(pk + "." + name.substring(0, name.length() - 6)));
//			}
//		}
//		return classes;
//	}
	
	private static void getClasses(File targetDir, Class<?> parent) throws Exception{
		List<Class<?>> beans = new ArrayList<Class<?>>();
		List<File> fileList = new ArrayList<File>();
		getFiles(targetDir, fileList);
		for (File file : fileList) {
			String path = file.getAbsolutePath();
			String className = toClass(path);
			if(className.contains("SkillCfgDAO")){
				System.out.println("tt");
			}
			try {
				Class<?> forName = Class.forName(className);
				
				if(parent.isAssignableFrom(forName)){
					beans.add(forName);
					System.out.println(template.replace("className", className));
				}
				
			} catch (Throwable e) {
				//donothing
			}
			
		}
		System.out.println(beans.size());
		
		
	}
	
	private static String toClass(String path) {
		String className = StringUtils.substringBetween(path, "D:\\fengshen\\fsbranch\\Server\\FSApp\\target\\classes\\", ".class");
		className = className.replace("\\", ".");
		
		return className;
	}

	private static void getFiles(File target, List<File> fileList){
		if(target.isFile()){
			fileList.add(target);
		}else if(target.isDirectory()){
			File[] files = target.listFiles();
			for (File fileTmp : files) {
				getFiles(fileTmp, fileList);
			}
		}
		
	}
	
	
	
	
	
}