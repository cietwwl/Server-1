package com.dx.gods.service.fileupload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.aspectj.weaver.tools.cache.AsynchronousFileCacheBacking.RemoveCommand;

import com.dx.gods.common.log.GMLogger;
import com.dx.gods.common.utils.GlobalValue;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder.Output;

import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;

public class FtpManager {

	/**
	 * ftp下载文件
	 * @param remoteFilePath
	 * @param localFilePath
	 * @param fileName
	 * @return
	 */
	public static String downloadFileInFtp(String remoteFilePath,
			String localFilePath, String fileName) {
		FTPClient ftp = connectFtp(GlobalValue.FTP_HOSTNAME,
				GlobalValue.FTP_PORT, GlobalValue.FTP_LOGIN_NAME,
				GlobalValue.FTP_LOGIN_PWD);
		try {
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			if (GlobalValue.GM_TYPE != GlobalValue.GM_TYPE_INTERNET) {
				remoteFilePath = processFilePath(remoteFilePath);
			}
			GMLogger.error("download: remoteFilePath" + remoteFilePath);
			int lastIndexOf = remoteFilePath.lastIndexOf("/");
			String remotePath = remoteFilePath.substring(0, lastIndexOf);
			String remoteFileName = remoteFilePath.substring(lastIndexOf + 1);
			boolean changeWorkingDirectory = ftp.changeWorkingDirectory(remotePath);
			GMLogger.error("download: changeWorkingDirectory" + changeWorkingDirectory);
			
			File localFile = new File(localFilePath);
			OutputStream output = new FileOutputStream(localFile);
			boolean retrieveFile = ftp.retrieveFile(new String(remoteFilePath.getBytes("UTF-8"), "iso-8859-1"), output);
			GMLogger.error("download: retrieveFile" + retrieveFile);
			output.close();
		} catch (Exception ex) {
			GMLogger.error("ftp服务器下载资源出现异常:" + ex.getMessage());
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
	public static String uploadFileToFtp(String remoteFilePath, String localFilePath, String fileName) {
		FTPClient ftp = connectFtp();
		if(ftp == null){
			GMLogger.error("登陆不了ftp服务器");
			return "登陆ftp服务器异常";
		}
		try {
			FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
			conf.setServerLanguageCode("zh");
			boolean changeWorkingDirectory = ftp.changeWorkingDirectory(remoteFilePath);
			if(!changeWorkingDirectory){
				throw new Exception("上传ftp转换路径异常:"+fileName);
			}
			if (GlobalValue.GM_TYPE == GlobalValue.GM_TYPE_IN) {
				fileName = processFilePath(fileName);
			}
			File localFile = new File(localFilePath);
			FileInputStream input = new FileInputStream(localFile);
			//转二进制类型
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			boolean storeFile = ftp.storeFile(new String(fileName.getBytes("UTF-8"), "iso-8859-1"), input);
			if(!storeFile){
				throw new Exception("上传ftp保存文件异常:"+fileName);
			}
			
			input.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			GMLogger.error(ex.getMessage());
			return "上传资源出现异常：" + ex.getMessage();
		} finally {
			if (ftp != null) {
				stop(ftp);
			}
		}
		return null;
	}
	
	/**
	 * 特殊处理 因为内网ftp限制文件后缀名
	 * @param filePath
	 * @return
	 */
	private static String processFilePath(String filePath){
		filePath = filePath.replace(".zip", ".ipa");
		filePath = filePath.replace(".xml", ".ipa");
		return filePath;
	}
	
	/**
	 * 特殊处理 因为内网ftp限制文件后缀名
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	private static String reprocessFilePath(String filePath, String fileName){	
		int lastIndexOf = filePath.lastIndexOf("\\");
		filePath = filePath.substring(0, lastIndexOf + 1) + fileName;
		return filePath;
	}

	private static FTPClient connectFtp(){
		if(GlobalValue.GM_TYPE == GlobalValue.GM_TYPE_IN){
			return connectFtp(GlobalValue.FTP_HOSTNAME,
					GlobalValue.FTP_PORT, GlobalValue.FTP_LOGIN_NAME,
					GlobalValue.FTP_LOGIN_PWD);
		}else{
			return connectFtp(GlobalValue.SUB_FTP_HOSTNAME,
					GlobalValue.SUB_FTP_PORT, GlobalValue.SUB_FTP_LOGIN_NAME,
					GlobalValue.SUB_FTP_LOGIN_PWD);
		}
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
		GMLogger.error("hostname:" + hostname + ",port:" + port + ",uid:" + uid
				+ ",pwd:" + pwd);
		try {
			FTPClient ftp = new FTPClient();
			FTPClientConfig conf = new FTPClientConfig(GlobalValue.GM_TYPE == GlobalValue.GM_TYPE_IN ? FTPClientConfig.SYST_NT : FTPClientConfig.SYST_UNIX);
			ftp.configure(conf);
			ftp.connect(hostname, port);
			ftp.login(uid, pwd);
			ftp.enterLocalPassiveMode();
			ftp.setControlEncoding("UTF-8");
			return ftp;
		} catch (Exception ex) {
			GMLogger.error("连接ftp服务器异常:" + ex.getMessage());
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
	 * 获取最新的resource config xml
	 * @param path
	 * @return
	 */
	public static String getLatestUpdateRescourceConfigFile(String path) {
		try {
			FTPClient ftp = connectFtp(GlobalValue.FTP_HOSTNAME,
					GlobalValue.FTP_PORT, GlobalValue.FTP_LOGIN_NAME,
					GlobalValue.FTP_LOGIN_PWD);
			if(ftp == null){
				GMLogger.error("登陆不了ftp服务器");
				return null;
			}
			boolean changeWorkingDirectory = ftp.changeWorkingDirectory(path);
			FTPFile[] listFiles = ftp.listFiles();
			if (listFiles == null || listFiles.length == 0) {

				GMLogger.error("ftp指定的文件夹没有文件");
				return null;
			}
			
			long max = -1;
			FTPFile latestFile = null;
			for (FTPFile ftpFile : listFiles) {
				if(ftpFile.isDirectory()){
					continue;
				}
				String name = ftpFile.getName();
				String processFilePath = processFilePath(GlobalValue.VERSION_CONFIG_FILE_NAME);
				name = name.replace(processFilePath.replace(".ipa", ""), "");
				name = name.replace(".ipa", "");
				long temp = Long.parseLong(name);
				if (max < temp) {
					max = temp;
					latestFile = ftpFile;
				}
			}
			
			if (latestFile != null) {
				return latestFile.getName();
			} else {
				return null;
			}
		} catch (Exception ex) {
			GMLogger.error("获取ftp最新文件出错" + ex.getMessage());
			return null;
		}

	}
	
	/**
	 * 获取最新的resource config xml
	 * @param path
	 * @return
	 */
	public static String getLatestUpdateFile(String path) throws Exception {
		try {
			FTPClient ftp = connectFtp(GlobalValue.FTP_HOSTNAME,
					GlobalValue.FTP_PORT, GlobalValue.FTP_LOGIN_NAME,
					GlobalValue.FTP_LOGIN_PWD);
			if(ftp == null){
				GMLogger.error("登陆不了ftp服务器");
				throw new Exception("登陆不了ftp服务器");
			}
			boolean changeWorkingDirectory = ftp.changeWorkingDirectory(path);
			FTPFile[] listFiles = ftp.listFiles();
			if (listFiles == null || listFiles.length == 0) {

				GMLogger.error("ftp指定的文件夹没有文件");
				throw new Exception("ftp指定的文件夹没有文件");
			}
			
			long max = -1;
			FTPFile latestFile = null;
			for (FTPFile ftpFile : listFiles) {
				if(ftpFile.isDirectory()){
					continue;
				}
				String name = ftpFile.getName();
				if (name.indexOf("ipa") == -1) {
					continue;
				}
				name = name.replace(".ipa", "");
				long temp = Long.parseLong(name);
				if (max < temp) {
					max = temp;
					latestFile = ftpFile;
				}
			}
			
			if (latestFile != null) {
				return latestFile.getName();
			} else {
				throw new Exception("ftp指定的文件夹没有文件");
			}
		} catch (Exception ex) {
			GMLogger.error("获取ftp最新文件出错" + ex.getMessage());
			throw new Exception("获取ftp最新文件出错" + ex.getMessage());
		}

	}
}
