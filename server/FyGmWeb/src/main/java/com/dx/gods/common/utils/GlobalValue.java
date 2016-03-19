package com.dx.gods.common.utils;

import java.io.File;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import sun.misc.GC.LatencyRequest;

import com.dx.gods.common.gameserver.GSService;
import com.dx.gods.common.gameserver.GameServer;
import com.dx.gods.common.gameserver.GameServerManager;
import com.dx.gods.common.log.GMLogger;
import com.dx.gods.common.timer.TimerManger;
import com.dx.gods.service.fileupload.ResourceContainer;
import com.dx.gods.service.tools.ProjectVersion;
import com.dx.gods.service.tools.SVNService;
import com.dx.gods.service.tools.SVNWorkCopy;
import com.dx.gods.service.tools.UpdateService;

public class GlobalValue {
	
	public final static int GM_TYPE_IN = 1;  //内网
	public final static int GM_TYPE_OUT = 2;  //公司外网
	public final static int GM_TYPE_INTERNET = 3;//互联网
	
	public static String UPLOAD_SAVE_PATH = "D:/Upload";
	public static String UPLOAD_CLASS_SAVE_PATH = "D:/clsses";
	public static String COMPARE_FOLDER_PATH = "D:/compare/new";
	public static String CURRENT_FOLDER_PATH = "D:/compare/current";
	public static String COMPARE_PATCH_PATH = "D:/compare/patch";
	public static String SERVER_FTP_PATH = "/patch/server";   			//摆放服务器资源的地方
	public static String CLIENT_FTP_PATH = "/patch/client";   			//摆放客户端资源的地方
	public static String CLASSES_FTP_PATH = "/patch/classes";            //摆放Class资源的地方
	public static String VERSION_FILE_FTP_PATH = "/path/versionfile";   //摆放资源版本文件的地方
	public static String FTP_ROOT_PATH = "/patch";             			//摆放资源信息配置表的地方
	
	public static String FTP_HOSTNAME = "192.168.2.142";
	public static int FTP_PORT = 21;
	public static String FTP_LOGIN_NAME = "wfb";
	public static String FTP_LOGIN_PWD = "qwe123";
	
	public static String SUB_FTP_HOSTNAME = "192.168.2.142";
	public static int SUB_FTP_PORT = 21;
	public static String SUB_FTP_LOGIN_NAME = "wfb";
	public static String SUB_FTP_LOGIN_PWD = "qwe123";

	public static String CONFIG_PATH = "";
	public static long lastUpdateTime = 0;
	
	public static String VERSION_CONFIG_PATH = "D:/GM/Config";
	public static String VERSION_CONFIG_FILE_NAME = "resourceconfig.xml";
	public static int GM_TYPE = 1;
	
	public static String CONVERT_PY_PATH = "";      	 	//转换工具的路径
	public static String CONVERT_PY_CSV_PATH = ""; 		 	//转换csv工具的路径
	public static String CONVERT_PY_RUN_PATH = "";  	 	//转换工具的批处理路径
	public static String CONVERT_PY_RUN_TOOL_CSV_PATH="";	//转换csv工具的批处理路径
	public static String COMPILE_CLASS_BAT_PATH = "";       //编译类的脚本路径
	public static String CLIENT_DIFF_TOOL_PATH = "";        //客户端资源差量工具路径
	public static String CONVERT_PY_TOOL_NAME = "convert.win.py";
	public static String CONVERT_PY_TOOL_CSV_NAME = "convertcsv.jar";
	public static String CONVERT_PY_RUN_TOOL_NAME = "run.bat";
	public static String CONVERT_PY_RUN_TOOL_CSV_NAME = "runcsv.bat";
	public static String COMPILE_CLASS_BAT_NAME = "compileClass.bat";
	public static String CLIENT_DIFF_TOOL_NAME = "BsDiff.exe";
	public static String CLIENT_PATCH_INFO_NAME = "patch.xml";

	public static void setVersionConfigPath(String value){
		VERSION_CONFIG_PATH = value;
	}
	
	@SuppressWarnings("unchecked")
	public static void initConfig(boolean init) {
		File file = new File(CONFIG_PATH);
		if (!file.exists()) {
			System.out.println("file is not exist!!!");
			return;
		}
		
		SAXBuilder builder = new SAXBuilder();
		try {
			long lastModified = file.lastModified();
			if (lastModified != lastUpdateTime) {
				lastUpdateTime = lastModified;
				Document doc = builder.build(file);
				Element rootElement = doc.getRootElement();
				List<Element> elements = rootElement.getChildren();
				for (Element element : elements) {
					if (element.getName().equals("UPLOAD_SAVE_PATH")) {
						GlobalValue.UPLOAD_SAVE_PATH = element.getText();
					}
					if (element.getName().equals("SERVER_FTP_PATH")) {
						GlobalValue.SERVER_FTP_PATH = element.getText();
					}
					if (element.getName().equals("CLIENT_FTP_PATH")) {
						GlobalValue.CLIENT_FTP_PATH = element.getText();
					}
					if (element.getName().equals("FTP_HOSTNAME")) {
						GlobalValue.FTP_HOSTNAME = element.getText();
					}
					if (element.getName().equals("FTP_PORT")) {
						GlobalValue.FTP_PORT = Integer.parseInt(element
								.getText());
					}
					if (element.getName().equals("FTP_LOGIN_NAME")) {
						GlobalValue.FTP_LOGIN_NAME = element.getText();
					}
					if (element.getName().equals("FTP_LOGIN_PWD")) {
						GlobalValue.FTP_LOGIN_PWD = element.getText();
					}

					if (element.getName().equals("SUB_FTP_HOSTNAME")) {
						GlobalValue.SUB_FTP_HOSTNAME = element.getText();
					}
					if (element.getName().equals("SUB_FTP_PORT")) {
						GlobalValue.SUB_FTP_PORT = Integer.parseInt(element
								.getText());
					}
					if (element.getName().equals("SUB_FTP_LOGIN_NAME")) {
						GlobalValue.SUB_FTP_LOGIN_NAME = element.getText();
					}
					if (element.getName().equals("SUB_FTP_LOGIN_PWD")) {
						GlobalValue.SUB_FTP_LOGIN_PWD = element.getText();
					}
					if (element.getName().equals("VERSION_CONFIG_FILE_NAME")) {
						GlobalValue.VERSION_CONFIG_FILE_NAME = element
								.getText();
					}
					if (element.getName().equals("GM_TYPE")) {
						GlobalValue.GM_TYPE = Integer.parseInt(element.getText());
					}
					if (element.getName().equals("VERSION_FILE_FTP_PATH")) {
						GlobalValue.VERSION_FILE_FTP_PATH = element.getText();
					}
					if (element.getName().equals("FTP_ROOT_PATH")) {
						GlobalValue.FTP_ROOT_PATH = element.getText();
					}
					
					if(element.getName().equals("CONVERT_PY_PATH")){
						GlobalValue.CONVERT_PY_PATH = element.getText();
					}
					
					if(element.getName().equals("CONVERT_PY_RUN_PATH")){
						GlobalValue.CONVERT_PY_RUN_PATH = element.getText();
					}
					
					if(element.getName().equals("CONVERT_PY_CSV_PATH")){
						GlobalValue.CONVERT_PY_CSV_PATH = element.getText();
					}
					
					if(element.getName().equals("CONVERT_PY_RUN_TOOL_CSV_PATH")){
						GlobalValue.CONVERT_PY_RUN_TOOL_CSV_PATH = element.getText();
					}
					
					if(element.getName().equals("CLASSES_FTP_PATH")){
						GlobalValue.CLASSES_FTP_PATH = element.getText();
					}
					if (element.getName().equals("COMPILE_CLASS_BAT_PATH")) {
						GlobalValue.COMPILE_CLASS_BAT_PATH = element.getText();
					}
					
					if(element.getName().equals("UPLOAD_CLASS_SAVE_PATH")){
						GlobalValue.UPLOAD_CLASS_SAVE_PATH = element.getText();
					}
					
					if(element.getName().equals("COMPARE_FOLDER_PATH")){
						GlobalValue.COMPARE_FOLDER_PATH = element.getText();
					}
					
					if(element.getName().equals("CURRENT_FOLDER_PATH")){
						GlobalValue.CURRENT_FOLDER_PATH = element.getText();
					}
					
					if(element.getName().equals("COMPARE_PATCH_PATH")){
						GlobalValue.COMPARE_PATCH_PATH = element.getText();
					}
					
					if(element.getName().equals("CLIENT_DIFF_TOOL_PATH")){
						GlobalValue.CLIENT_DIFF_TOOL_PATH = element.getText();
					}
					
					if(element.getName().equals("VERSION_CONFIG_PATH")){
						GlobalValue.VERSION_CONFIG_PATH = element.getText();
					}
					
					GameServerManager.init(element);
					SVNService.init(element);
					if(element.getName().equals("UpdateClass")){
						UpdateService.getInstance().init(element);
					}
				}
				if (init) {
					ResourceContainer.readResourceFile();
					TimerManger.initTimer();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			GMLogger.info(ex.getMessage());
		}
	}

	public static void CheckUploadFolderExist() {
		File file = new File(UPLOAD_SAVE_PATH);
		if (!file.exists()) {
			try {
				file.mkdir();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public static String getCOMPILE_CLASS_BAT_PATH() {
		return COMPILE_CLASS_BAT_PATH;
	}

	public static String getCOMPILE_CLASS_BAT_NAME() {
		return COMPILE_CLASS_BAT_NAME;
	}
}
