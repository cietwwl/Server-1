package com.dx.gods.service.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

import com.dx.gods.common.gameserver.GameServer;
import com.dx.gods.common.gameserver.GameServerManager;
import com.dx.gods.common.gameserver.GameServerRequestType;
import com.dx.gods.common.net.GSRequestAction;
import com.dx.gods.common.utils.GlobalValue;
import com.dx.gods.common.utils.MD5Util;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.UtilTools;
import com.dx.gods.controller.admin.common.DXAdminController;
import com.rw.service.http.response.BaseMsgResponse;

/**
 * 文件上传controller
 * 
 * @author lida
 *
 */
public class FileUploadController extends DXAdminController {

	private String name;
	private File upload;
	private List<GameServer> serverlist;
	private int serverid;

	/**
	 * 处理客户端资源上传
	 * 
	 * @return
	 */
	public String handlerClientFileUpload() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String message = uploadFile(ResourceContainer.RESOURCE_TYPE_CLIENT,
				GlobalValue.CLIENT_FTP_PATH, GlobalValue.UPLOAD_SAVE_PATH,
				request);
		return NetUtils.packMessage(request, message);
	}

	/**
	 * 处理服务器资源上传
	 * 
	 * @return
	 */
	public String handlerFileUpload() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String message = uploadFile(ResourceContainer.RESOURCE_TYPE_SERVER,
				GlobalValue.SERVER_FTP_PATH, GlobalValue.UPLOAD_SAVE_PATH,
				request);
		return NetUtils.packMessage(request, message);
	}

	public String listUpdateServer() {
		Collection<GameServer> values = GameServerManager.ServerMap.values();
		serverlist = new ArrayList<GameServer>(values);
		return SUCCESS;
	}

	private String uploadFile(int type, String path, String localPath,
			HttpServletRequest request) {
		String message = null;
		if (upload != null) {
			try {
				String uploadFileMD5Value = MD5Util.getFileMD5String(upload);

				FileInputStream in = new FileInputStream(upload);
				String subVersionNo = UtilTools.getDateTimeString("yyyyMMddHHmmss");
				String fileName = subVersionNo + ".zip";
				GlobalValue.CheckUploadFolderExist();
				String filePath = localPath + "/" + fileName;

				File file = new File(filePath);
				if (file.exists()) {
					file.delete();
				}
				// 保存到本地 再上传ftp
				FileOutputStream out = new FileOutputStream(file);
				IOUtils.copy(in, out);
				in.close();
				out.close();
				String result = FtpManager.uploadFileToFtp(path, filePath,
						fileName);
				
				/**
				 * 临时大版本号为1(大版本号以后页面输入) fileName为小版本号
				 */
				ResourceVersion resVer = new ResourceVersion("1", subVersionNo,
						file.length(), uploadFileMD5Value, fileName,
						type);
				file.delete();
				updateResourceVersion(resVer, type);
				if (result != null) {
					message = result;
				} else {
					message = "上传成功!!!";
				}
			} catch (Exception e) {
				// TODO: handle exception
				message = "上传文件出现异常：" + e.getMessage();
				e.printStackTrace();

			}
		}
		return message;
	}

	private void updateResourceVersion(ResourceVersion rv, int type) {
		if (type == ResourceContainer.RESOURCE_TYPE_CLIENT) {
			ResourceVersion currentResVersion = ResourceContainer
					.getCurrentResVersion(type);
			rv.setLastMainVersionNo(currentResVersion == null ? "-1" : currentResVersion.getMainVersionNo());
			rv.setLastSubVersionNo(currentResVersion == null ? "-1" : currentResVersion.getSubVersionNo());
		}
		ResourceContainer.setCurrentResVersion(type, rv);
	}

	/**
	 * 通知游戏服务器有新资源
	 * 
	 * @return
	 */
	public String patchServerResource() {
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			serverid = Integer.parseInt(request.getParameter("serverid")
					.toString());
			String message;
			if (serverid == 0) {
				return NetUtils.packMessage(request, "请选择服务器！");
			}
			GameServer gameServer = GameServerManager.ServerMap.get(serverid);
			String strUrl = gameServer.getHttpUrl() + ":"
					+ gameServer.getHttpPort();
			Map<String, Object> params = patchServer(serverid);
			
			GSRequestAction requestAction = new GSRequestAction();
			requestAction.pushParams(Map.class, params);
			BaseMsgResponse msg = (BaseMsgResponse) requestAction.remoteCall(
					strUrl,
					"com.rw.service.http.requestHandler.ManagerServerHandler",
					"updateResHandler");
			return NetUtils.packMessage(request, msg.getMsg());
//			InputStream response = NetUtils.sentHttpPostMsg(strUrl, result);
//			if (response != null) {
//				return NetUtils.packMessage(request,
//						NetUtils.getInputStreamToString(response));
//			} else {
//				return NetUtils.packMessage(request, "通知服务器失败，请重试!");
//			}
		} catch (Exception ex) {
			return NetUtils.packMessage(request, "通知服务器失败，请重试!");
		}
	}
	
	public static Map<String, Object> patchServer(int serverId){
		
		ResourceVersion currentResVersion = ResourceContainer
				.getCurrentResVersion(ResourceContainer.RESOURCE_TYPE_SERVER);
		
		String filename = GlobalValue.SERVER_FTP_PATH + "/"
				+ currentResVersion.getSubVersionNo() + ".zip";
		if (GlobalValue.GM_TYPE == GlobalValue.GM_TYPE_IN) {
			filename = filename.replace("zip", "ipa");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ftp_host", GlobalValue.FTP_HOSTNAME);
		map.put("ftp_port", GlobalValue.FTP_PORT);
		map.put("ftp_login_name", GlobalValue.FTP_LOGIN_NAME);
		map.put("ftp_login_pwd", GlobalValue.FTP_LOGIN_PWD);
		map.put("file_path", filename);
		map.put("file_md5", currentResVersion.getMd5Value());
		return map;
	}

	public String rollBackServerResource() {
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			serverid = Integer.parseInt(request.getParameter("serverid")
					.toString());
			String message;
			if (serverid == 0) {
				return NetUtils.packMessage(request, "请选择服务器！");
			}
			GameServer gameServer = GameServerManager.ServerMap.get(serverid);
			String strUrl = gameServer.getHttpUrl() + ":"
					+ gameServer.getHttpPort();
			StringBuilder sb = new StringBuilder();

			GSRequestAction requestAction = new GSRequestAction();
			BaseMsgResponse msg = (BaseMsgResponse) requestAction.remoteCall(
					strUrl,
					"com.rw.service.http.requestHandler.ManagerServerHandler",
					"rollbackResHandler");
			return NetUtils.packMessage(request, msg.getMsg());
			// NetUtils.packNetMessage("type",
			// GameServerRequestType.TYPE_ROLLBACK_RES, sb);
			// InputStream response = NetUtils.sentHttpPostMsg(strUrl,
			// sb.toString());
			// if (response != null) {
			// return NetUtils.packMessage(request,
			// NetUtils.getInputStreamToString(response));
			// } else {
			// return NetUtils.packMessage(request, "通知服务器失败，请重试!");
			// }
		} catch (Exception ex) {
			return NetUtils.packMessage(request, "通知服务器失败，请重试!");
		}
	}

	/**
	 * 回滚客户端资源
	 * @return
	 */
	public String rollBackClientResource() {
		HttpServletRequest request = ServletActionContext.getRequest();

		try {
			ResourceVersion currentResVersion = ResourceContainer
					.getCurrentResVersion(ResourceContainer.RESOURCE_TYPE_CLIENT);
			if(currentResVersion == null){
				throw new Exception("当前版本没有回滚版本,回滚失败!");
			}
			ConcurrentHashMap<String, ResourceVersion> concurrentHashMap = ResourceContainer.ClientResMap
					.get(currentResVersion.getLastMainVersionNo());
			if (concurrentHashMap != null) {
				ResourceVersion resourceVersion = concurrentHashMap
						.get(currentResVersion.getLastSubVersionNo());
				if (resourceVersion == null) {
					throw new Exception("当前版本没有回滚版本,回滚失败!");
				}
				long currentTimeMillis = System.currentTimeMillis();
				// 删除当前版本
				concurrentHashMap.remove(currentResVersion.getSubVersionNo());
				resourceVersion.setSubVersionNo(UtilTools.getDateTimeString("yyyyMMddHHmmss"));
				ResourceContainer.CurrentResMap
						.put(ResourceContainer.RESOURCE_TYPE_CLIENT,
								resourceVersion);
				return NetUtils.packMessage(request, "回滚成功");
			} else {
				throw new Exception("当前版本没有回滚版本,回滚失败!");
			}
		} catch (Exception ex) {
			return NetUtils.packMessage(request, "回滚资源异常:"+ex.getMessage());
		}
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<GameServer> getServerlist() {
		return serverlist;
	}

	public void setServerlist(List<GameServer> serverlist) {
		this.serverlist = serverlist;
	}

}
