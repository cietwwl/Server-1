package com.rw.service.http.requestHandler;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.cacheDao.CfgCsvReloader;
import com.rw.fsutil.util.DeCompressUtil;
import com.rw.fsutil.util.FileUtil;
import com.rw.fsutil.util.JsonCfgTransfer;
import com.rw.manager.GameManager;
import com.rw.service.http.GSResponseMgr;
import com.rw.service.http.response.BaseMsgResponse;

@SuppressWarnings("unchecked")
public class ManagerServerHandler {
	
	//服务器下载的更新文件的路径
	public final static String LOCAL_FILE_PATH = System.getProperty("user.dir")
			+ "\\download\\temp.zip";
	//解压的目录的路径
	public final static String DECOMPRESS_PATH = System.getProperty("user.dir")
			+ "\\download\\config";
	//更新前备份一份被修改的文件的保存目录
	public final static String BACKUP_PATH = System.getProperty("user.dir")+"/download/bconfig";
	public static String TARGET_PATH = "";
	
	/**
	 * 停服操作
	 * @return
	 */
	public static BaseMsgResponse shutdownServerHandler() {
		Thread shutdown = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		shutdown.start();
		BaseMsgResponse msg = GSResponseMgr.PackBaseMsg("服务器关闭开始...");
		return msg;
	}
	
	/**
	 * 回滚资源
	 * @return
	 */
	public static BaseMsgResponse rollbackResHandler(){
		String result = rollbackRes();
		BaseMsgResponse msg = GSResponseMgr.PackBaseMsg(result);
		return msg;
	}
	
	/**
	 * 动态更新配置表
	 * @param args
	 * @return
	 */
	public static BaseMsgResponse updateResHandler(Map<String, Object> args){
		String ftp_host = args.get("ftp_host").toString();
		int ftp_port = Integer.parseInt(args.get("ftp_port").toString());
		String ftp_login_name = args.get("ftp_login_name").toString();
		String ftp_login_pwd = args.get("ftp_login_pwd").toString();
		String file_path = args.get("file_path").toString();
		String file_md5 = args.get("file_md5").toString();
		String result = FileUtil.downloadFileInFtp(file_path, LOCAL_FILE_PATH,
				"temp.zip", ftp_host, ftp_port, ftp_login_name, ftp_login_pwd, file_md5);
		if (result == null) {
			result = updateConfigAndReload();
		}
		BaseMsgResponse msg = GSResponseMgr.PackBaseMsg(result);
		return msg;
	}
	
	private static String rollbackRes() {
		try {
			TARGET_PATH = JsonCfgTransfer.class.getResource("/config").toURI().getPath();
			
			GameManager.setReloadconfig(true);
			// 弹出提示框 踢所有在线玩家下线
			Map<String, Player> playerList = PlayerMgr.getInstance()
					.getAllPlayer();
			for (Iterator<Entry<String, Player>> iterator = playerList
					.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, Player> next = iterator.next();
				next.getValue().KickOff("系统更新中");
			}

			// 检查备份文件夹
			File backup = new File(BACKUP_PATH);
			File[] listFiles = backup.listFiles();
			// 有备份就还原并删除
			if (listFiles.length > 0) {
				FileUtil.copyFolder(TARGET_PATH,
						BACKUP_PATH,
						BACKUP_PATH);
				FileUtil.clearFolder(BACKUP_PATH);
			}else{
				throw new Exception("没有备份资源，回滚失败");
			}

			// 覆盖文件 重新加载 该过程暂时不能登录
			CfgCsvReloader.reload();
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ex.getMessage();
		} finally {
			GameManager.setReloadconfig(false);
		}
	}
	

	/**
	 * 解压下载的ftp文件，并覆盖到原来的config文件 最后重新加载
	 */
	public static String updateConfigAndReload() {
		try {
			TARGET_PATH = JsonCfgTransfer.class.getResource("/config").toURI().getPath();
			
			GameManager.setReloadconfig(true);
			// 弹出提示框 踢所有在线玩家下线
			Map<String, Player> playerList = PlayerMgr.getInstance().getAllPlayer();
			for (Iterator<Entry<String, Player>> iterator = playerList.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, Player> next = iterator.next();
				next.getValue().KickOff("系统更新中");
			}
			//更新前清除一下文件夹（预防之前更新有异常）
			FileUtil.deletefolder(DECOMPRESS_PATH);
			
			DeCompressUtil.deCompress(LOCAL_FILE_PATH, DECOMPRESS_PATH);
			FileUtil.clearFolder(BACKUP_PATH);
			FileUtil.copyFolder(BACKUP_PATH, TARGET_PATH, TARGET_PATH);
			FileUtil.moveFile(DECOMPRESS_PATH, DECOMPRESS_PATH, TARGET_PATH);
			StringBuilder sb = new StringBuilder();
			List<File> files = FileUtil.getFiles(DECOMPRESS_PATH, "");
			for (File file : files) {
				sb.append(file.getName()).append("<br/>");
			}
			
			//删除临时文件夹
			FileUtil.deletefolder(DECOMPRESS_PATH);
			

			// 覆盖文件 重新加载 该过程暂时不能登录
			// 覆盖文件 重新加载 该过程暂时不能登录
			CfgCsvReloader.reload();
			
			return sb.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ex.getMessage();
		}finally{
			GameManager.setReloadconfig(false);
		}
	}
	
	public static void main(String[] args){
		System.out.println(ManagerServerHandler.class.getName()); 
		try{
		Class<?> forName = Class.forName("com.rw.service.http.requestHandler.ManagerServerHandler");
		Method[] methods = forName.getMethods();
		for (Method method : methods) {
			
			System.out.println(method.getName());
		}
		}catch(Exception ex){
			ex.printStackTrace();
			
		}
	}
}
