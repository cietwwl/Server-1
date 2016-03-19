package com.rw.fsutil.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;



public class FileUtil {

	/**
	 * ftp下载文件
	 * @param remoteFilePath
	 * @param localFilePath
	 * @param fileName
	 * @return
	 */
	public static String downloadFileInFtp(String remoteFilePath,
			String localFilePath, String fileName, 
			String ftp_hostName, int ftp_port, String ftp_login_name, String ftp_login_pwd, String file_md5) {
		FTPClient ftp = connectFtp(ftp_hostName,
				ftp_port, ftp_login_name,
				ftp_login_pwd);
		try {
			int lastIndexOf = remoteFilePath.lastIndexOf("/");
			String remotePath = remoteFilePath.substring(0, lastIndexOf);
			String remoteFileName = remoteFilePath.substring(lastIndexOf+1);
			boolean changeWorkingDirectory = ftp.changeWorkingDirectory(remotePath);
			File localFile = new File(localFilePath);
			OutputStream output = new FileOutputStream(localFile);
			ftp.retrieveFile(new String(remoteFileName.getBytes("GBK"),"ISO-8859-1"), output);
//			ftp.retrieveFile(remoteFilePath, output);
			output.close();
			localFile = new File(localFilePath);
			//校验md5
			String localFile_md5 = MD5.getFileMD5String(localFile);
			if(!localFile_md5.equals(file_md5)){
				//删除无效文件
				localFile.delete();
				return "下载资源和服务器资源不匹配，请重新更新";
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
			return "ftp服务器下载资源出现异常:" + ex.getMessage();
		} finally {
			if (ftp != null) {
				stop(ftp);
			}
		}
		return null;
	}

	/**
	 * 上传文件到ftp
	 * @param filePath
	 * @param localFilePath
	 * @param fileName
	 * @return
	 */
	public static String uploadFileToFtp(String remoteFilePath, String localFilePath,
			String fileName, String ftp_hostName, 
			int ftp_port, String ftp_login_name, String ftp_login_pwd) {
		FTPClient ftp = connectFtp(ftp_hostName,
				ftp_port, ftp_login_name,
				ftp_login_pwd);
		try {

			if (ftp == null) {
				return "资源上传ftp服务器出现异常";
			}
			ftp.setControlEncoding("GBK");
			FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
			conf.setServerLanguageCode("zh");
			boolean changeWorkingDirectory = ftp.changeWorkingDirectory(remoteFilePath);
			
			File localFile = new File(localFilePath);
			FileInputStream input = new FileInputStream(localFile);
			//转二进制类型
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.storeFile(fileName, input);
			
			input.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "上传资源出现异常：" + ex.getMessage();
		} finally {
			if (ftp != null) {
				stop(ftp);
			}
		}
		return null;
	}

	/**
	 * 连接ftp
	 * @param hostname
	 * @param port
	 * @param uid
	 * @param pwd
	 * @return
	 */
	private static FTPClient connectFtp(String hostname, int port, String uid,
			String pwd) {
		try {
			FTPClient ftp = new FTPClient();
			ftp.connect(hostname, port);
			ftp.login(uid, pwd);
			return ftp;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * ftp断开连接
	 * @param ftp
	 */
	private static void stop(FTPClient ftp) {
		if (ftp != null) {
			try {
				ftp.disconnect();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 移动文件
	 * 
	 * 热加载调用
	 * 源和目标的文件结构是一样的
	 * @param rootPath 文件夹的根目录
	 * @param srcPath  源文件的路径
	 * @param tarPath  目标文件的路径
	 * @throws Exception
	 */
	public static void moveFile(String rootPath, String srcPath, String tarPath)throws Exception{
		File src = new File(srcPath);
		
		if(src.isDirectory()){
			File[] listFiles = src.listFiles();
			for (File file : listFiles) {
				if(file.isDirectory()){
					moveFile(rootPath, file.getAbsolutePath(), tarPath);
					
				}else{
					String targetFilePath = getTargetFilePath(file, rootPath, tarPath);
					copyFile(file.getAbsolutePath(), targetFilePath);
				}
			}
		}else{
			String targetFilePath = getTargetFilePath(src, rootPath, tarPath);
			copyFile(src.getAbsolutePath(), targetFilePath);
		}
		
	}
	
	private static String getTargetFilePath(File src, String srcPath, String tarPath){
		String srcFilePath = src.getAbsolutePath();
		String path = srcFilePath.substring(srcPath.length(), srcFilePath.length());
		return tarPath + path;
		
	}
	
	public static void copyFolder(String targetPath, String sourcePath,
			String rootPath) throws Exception {
		File backupFile = new File(targetPath);
		if(!backupFile.exists()){
			backupFile.mkdir();
		}
		try {
			File target = new File(sourcePath);
			File[] listFiles = target.listFiles();
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
	 * 复制文件
	 * @param srcFilePath
	 * @param tarFilePath
	 */
	public static void copyFile(String srcFilePath, String tarFilePath) throws Exception{
		int byteread = 0;
		InputStream in = null;
		OutputStream out = null;
		File srcFile = new File(srcFilePath);
		File tarFile = new File(tarFilePath);
		if(tarFile.exists()){
			tarFile.delete();
		}
		try{
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(tarFile);
			byte[] buffer = new byte[1024];
			while((byteread = in.read(buffer)) != -1){
				out.write(buffer, 0 , byteread);
			}
		}catch(Exception ex){
			throw new Exception("srcFilePath" + srcFilePath + "tarFilePath"
					+ tarFilePath + "复制文件出错" + ex.getMessage());
		}finally{
			if(out != null){
				try{
					out.close();
				}catch(IOException ex){
					ex.printStackTrace();
					throw new Exception("srcFilePath" + srcFilePath + "tarFilePath"
							+ tarFilePath + "复制文件出错" + ex.getMessage());
				}
			}
			if(in != null){
				try{
					in.close();
				}catch(IOException ex){
					ex.printStackTrace();
					throw new Exception("srcFilePath" + srcFilePath + "tarFilePath"
							+ tarFilePath + "复制文件出错" + ex.getMessage());
				}
			}
		}
	}
	
	/**
	 * 根文件夹也删除
	 * @param path
	 */
	public static void deletefolder(String path){
		clearFolder(path);
		File file = new File(path);
		file.delete();
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
	
	public static List<File> getFiles(String Path, String filterName){
		File file = new File(Path);
		File[] listFiles = file.listFiles();
		List<File> result = new LinkedList<File>();
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
}
