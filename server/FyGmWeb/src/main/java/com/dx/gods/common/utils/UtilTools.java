package com.dx.gods.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.dx.gods.common.gameserver.GameServer;
import com.dx.gods.common.gameserver.GameServerManager;

public class UtilTools {
	
	/**
	 * 返回日期字符串 格式：yyyyMMddHHmmss
	 * @return
	 */
	public static String getDateTimeString(String strFotmat){
		Calendar instance = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(strFotmat);
		String value = format.format(instance.getTime());
		return value;
	}
	
	/**
	 * 字符串转Date
	 * @param strDate
	 * @param format
	 * @return
	 */
	public static Date parseDateTime(String strDate, String format){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(strDate);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static List<File> getFiles(String Path, String filterName){
		File file = new File(Path);
		File[] listFiles = file.listFiles();
		List<File> result = new LinkedList<File>();
		if(listFiles == null){
			return result;
		}
		for (File file2 : listFiles) {
			if (file2.isDirectory()) {
				List<File> files = getFiles(file2.getAbsolutePath(), filterName);
				result.addAll(files);
			} else {
				if (filterName != null) {
					if(filterName.equals("")){
						result.add(file2);
						continue;
					}
					if (file2.getName().indexOf(filterName) != -1) {
						result.add(file2);
						continue;
					}
				}
			}
		}
		return result;
	}
	
	public static HashMap<String, File> getFilesMap(String path, String filterName){
		File file = new File(path);
		File[] listFiles = file.listFiles();
		HashMap<String, File> result = new HashMap<String, File>();
		if(listFiles == null){
			return result;
		}
		for (File file2 : listFiles) {
			if (file2.isDirectory()) {
				if (filterName != null) {
					if (file2.getName().indexOf(filterName) != -1) {
						continue;
					}
				}
				HashMap<String, File> filesMap = getFilesMap(file2.getAbsolutePath(), filterName);
				result.putAll(filesMap);
			} else {
				if (filterName != null) {
					if(filterName.equals("")){
						result.put(file2.getName(), file2);
						continue;
					}
					if (file2.getName().indexOf(filterName) != -1) {
						result.put(file2.getName(), file2);
						continue;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 筛选掉属于filter的内容
	 * @param path
	 * @param filterName
	 * @return
	 */
	public static HashMap<String, File> getFilesMapScreening(String path, String[] filterName){
		File file = new File(path);
		File[] listFiles = file.listFiles();
		HashMap<String, File> result = new HashMap<String, File>();
		if(listFiles == null){
			return result;
		}
		for (File file2 : listFiles) {
			if (file2.isDirectory()) {
				if (filterName != null && !filterName.equals("")) {
					if(checkFilter(file2.getName(), filterName)){
						continue;
					}
				}
				HashMap<String, File> filesMap = getFilesMapScreening(file2.getAbsolutePath(), filterName);
				result.putAll(filesMap);
			} else {
				if (filterName != null) {
					if(filterName.equals("")){
						result.put(file2.getName(), file2);
						continue;
					}
					if (!checkFilter(file2.getName(), filterName)) {
						result.put(file2.getName(), file2);
						continue;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 获取文件夹的路径
	 * filter不为空，则只找和fiterName同名的文件
	 * @param Path
	 * @param filterName
	 * @return
	 */
	public static List<File> getFolders(String Path, String filterName){
		List<File> result =new ArrayList<File>();
		File file = new File(Path);
		File[] listFiles = file.listFiles();
		if(listFiles == null){
			return result;
		}
		for (File file2 : listFiles) {
			if(file2.isDirectory()){
				if(file2.getName().equals(filterName)){
					result.add(file2);
				}
				List<File> temp = getFolders(file2.getAbsolutePath(), filterName);
				result.addAll(temp);
			}
		}
		return result;
	}
	
	/**
	 * 拷贝文件
	 * @param srcFilePath
	 * @param tarFilePath
	 * @return
	 */
	public static File copyFile(String srcFilePath, String tarFilePath){
		int byteread = 0;
		InputStream in = null;
		OutputStream out = null;
		File srcFile = new File(srcFilePath);
		File tarFile = new File(tarFilePath);
		if(tarFile.exists()){
			tarFile.delete();
		}else{
			createFile(tarFilePath);
		}
		try{
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(tarFile);
			byte[] buffer = new byte[1024];
			while((byteread = in.read(buffer)) != -1){
				out.write(buffer, 0 , byteread);
			}
			return tarFile;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(out != null){
				try{
					out.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
			if(in != null){
				try{
					in.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * 创建文件 
	 * 如果上层目录没有 则自动创建
	 * @param path
	 */
	public static void createFile(String path) {
		try {
			File file = new File(path);
			if (file.exists()) {
				
				file.delete();
				file.createNewFile();

			} else {
				createFolder(file.getParent());
				file.createNewFile();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 创建文件夹
	 * 如果上层目录没有 则自动创建
	 * @param path
	 */
	public static void createFolder(String path){
		File folder = new File(path);
		if(!folder.exists()){
			 File parentFile = folder.getParentFile();
			 if(!parentFile.exists()){
				 createFolder(parentFile.getAbsolutePath());
			 }
			 folder.mkdir();
		}
	}
	
	/**
	 * 删除指定路径的文件夹或者文件夹
	 * @param path
	 */
	public static void deletefolder(String path){
		File file = new File(path);
		if(file.isDirectory()){
			File[] listFiles = file.listFiles();
			for (File file2 : listFiles) {
				if(file2.isDirectory()){
					deletefolder(file2.getAbsolutePath());
				}else{
					file2.delete();
				}
			}
		}
		file.delete();
	}
	
	/**
	 * 删除文件
	 * @param path
	 * @param filter
	 */
	public static void deleteFile(String path, String[] filter) {
		File file = new File(path);
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File file2 : listFiles) {
				if (file2.isDirectory()) {
					deleteFile(file2.getAbsolutePath(), filter);
				} else {
					if (checkFilter(file2.getName(), filter)) {
						file2.delete();
					}
				}
			}
		}
	}
	
	/**
	 * 检查过滤
	 * @param name
	 * @param filter
	 * @return
	 */
	public static boolean checkFilter(String name, String[] filter){
		for (String value : filter) {
			if(name.indexOf(value) != -1){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取后台登陆的玩家名字
	 * @return
	 */
	public static String getCurrentUserName(){
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal instanceof UserDetails){
			return  ((UserDetails)principal).getUsername();
		}
		if(principal instanceof Principal){
			return ((Principal)principal).getName();
		}
		return String.valueOf(principal);
	}
	
	/**
	 * 将存放在sourceFilePath目录下的源文件,打包成fileName名称的zip文件,并存放到zipFilePath路径下
	 * @param sourceFilePath 待压缩的文件路径
	 * @param zipFilePath  压缩后存放路径
	 * @param fileName 压缩后文件的名称
	 * @return
	 */
	public static boolean fileToZip(String sourceFilePath, String zipFilePath,
			String fileName) {
		boolean flag = false;
		File sourceFile = new File(sourceFilePath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			if (sourceFile.exists() == false) {
				return false;
			} else {
				File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
				if (zipFile.exists()) {
					zipFile.delete();
				}
				File[] sourceFiles = sourceFile.listFiles();
				if (null == sourceFiles || sourceFiles.length < 1) {
					return false;
				}
				fos = new FileOutputStream(zipFile);
				zos = new ZipOutputStream(new BufferedOutputStream(fos));
				byte[] bufs = new byte[1024 * 10];
				for (int i = 0; i < sourceFiles.length; i++) {
					ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
					zos.putNextEntry(zipEntry);
					fis = new FileInputStream(sourceFiles[i]);
					bis = new BufferedInputStream(fis, 1024 * 10);
					int read = 0;
					while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
						zos.write(bufs, 0, read);
					}
				}
				flag = true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			flag = false;
		} finally {
			try {
				if (null != fos)
					fos.close();
				if (null != fis)
					fis.close();
				if (null != bis)
					bis.close();
				if (null != zos)
					zos.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}

		}
		return flag;
	}
	
	/**
	 * 压缩文件
	 * @param pathName  zip保存的路径
	 * @param scrPathName 压缩的源文件路径
	 * @throws Exception
	 */
	public static void zipCompressByAnd(String pathName, String scrPathName)throws Exception{
		File zipFile = new File(pathName);
		File srcDir = new File(scrPathName);
		
		if(!srcDir.exists()){
			throw new Exception("can not find the source!");
		}
		
		Project prj = new Project();
		Zip zip = new Zip();
		zip.setProject(prj);
		zip.setDestFile(zipFile);
		FileSet fileSet = new FileSet();
		fileSet.setProject(prj);
		fileSet.setDir(srcDir);
		zip.addFileset(fileSet);
		zip.execute();
	}
	
	/**
	 * 获取指定格式的时间字符串
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static String getDateTimeString(long time, String pattern){
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(pattern);
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(time);
		Date date = instance.getTime();
		String result = dateTimeFormat.format(date);
		return result;
		
	}
	
	/**
	 * 返回上一天的时间
	 * @param current
	 * @return
	 */
	public static Calendar getLastDay(Calendar current){
		long result = current.getTimeInMillis() - 24*60*60*1000l;
		current.setTimeInMillis(result);
		return current;
	}
	
	/**
	 * 是不是当天
	 * @param time
	 * @return
	 */
	public static boolean isCurrentDay(long time){
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.HOUR, 0);
		instance.set(Calendar.MINUTE, 0);
		instance.set(Calendar.SECOND, 0);
		if(time >= instance.getTimeInMillis()){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isSameDay(long time1, long time2){
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTimeInMillis(time1);
		c2.setTimeInMillis(time2);
		
		int dayYear1 = c1.get(Calendar.DAY_OF_YEAR);
		int year1 = c1.get(Calendar.YEAR);

		int dayYear2 = c2.get(Calendar.DAY_OF_YEAR);
		int year2 = c2.get(Calendar.YEAR);
		
		if(dayYear1 == dayYear2 && year1 == year2){
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * 执行脚本
	 * @param targetPath          脚本执行地址
	 * @param scriptOriginalPath  脚本备份地址
	 * @return
	 * @throws Exception
	 */
	public static String runScript(String targetPath, String scriptOriginalPath)throws Exception{
		File file = new File(targetPath);
		if(!file.exists()){
			copyFile(scriptOriginalPath, targetPath);
		}
		targetPath.replace("\\", "/");
		file = new File(targetPath);
		Process p = Runtime.getRuntime().exec(file.getAbsolutePath());
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = br.readLine())!=null){
			sb.append(line).append("<br/>");
		}
		br.close();
		return sb.toString();
	}
	
	/**
	 * 文件夹底下所有内容都删除
	 * @param path
	 */
	public static void clearFolder(String path){
		File file = new File(path);
		if(file.isDirectory()){
			File[] listFiles = file.listFiles();
			for (File file2 : listFiles) {
				if(file2.isDirectory()){
					deletefolder(file2.getAbsolutePath());
				}else{
					file2.delete();
				}
			}
		}
	}
	
	/**
	 * 复制文件夹
	 * @param targetPath
	 * @param sourcePath
	 * @param rootPath
	 * @throws Exception
	 */
	public static void copyFolder(String targetPath, String sourcePath,
			String rootPath) throws Exception {
		File backupFile = new File(targetPath);
		if(!backupFile.exists()){
			createFolder(backupFile.getAbsolutePath());
		}
		try {
			File target = new File(sourcePath);
			File[] listFiles = target.listFiles();
			if(listFiles == null){
				return;
			}
			for (File file : listFiles) {
				String absolutePath = file.getAbsolutePath();
				absolutePath = absolutePath.replace("\\", "/");
				String tempPath = absolutePath.substring(rootPath.length(),
						absolutePath.length());
				String newPath = targetPath + "/" + tempPath;
				if (file.isDirectory()) {
					File temp = new File(newPath);
					if (!temp.exists()) {
						temp.mkdir();
					}
					copyFolder(targetPath, absolutePath, rootPath);
				} else {
					copyFile(file.getAbsolutePath(), newPath);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("拷贝文件出错：" + ex.getMessage());
		}
	}

	/**
	 * 获取对应服务器的url
	 * @param serverId
	 * @return
	 */
	public static String getServerUrl(int serverId) {
		GameServer gameServer = GameServerManager.ServerMap.get(serverId);
		if (gameServer != null) {
			return gameServer.getHttpUrl() + ":" + gameServer.getHttpPort();
		} else {
			return null;
		}
	}
	
	/**
	 * 根据时间间隔返回时间列表
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Long> getTimeRangeList(long startTime, long endTime){
		Calendar sc = Calendar.getInstance();
		sc.setTimeInMillis(startTime);
		setDayStartTime(sc);
		
		List<Long> result = new ArrayList<Long>();
		long tempTime = sc.getTimeInMillis();
		while (tempTime <= endTime) {
			result.add(tempTime);
			tempTime = tempTime + 24 * 60 * 60 * 1000;
		}
		return result;
	}
	
	public static void setDayStartTime(Calendar c){
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
	}
}

