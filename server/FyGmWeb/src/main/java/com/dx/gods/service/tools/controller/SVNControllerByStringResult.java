package com.dx.gods.service.tools.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.struts2.ServletActionContext;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.dx.gods.common.log.GMLogger;
import com.dx.gods.common.utils.GlobalValue;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.SvnUtil;
import com.dx.gods.common.utils.UtilTools;
import com.dx.gods.controller.admin.common.DXAdminController;
import com.dx.gods.service.fileupload.FtpManager;
import com.dx.gods.service.tools.SVNService;
import com.dx.gods.service.tools.SVNWorkCopy;

public class SVNControllerByStringResult extends DXAdminController {
	private InputStream result;
	private List<File> filelist;
	private List<String> filelistFileName;
	
	public String handlerExcelCommitSVN() {

		String userName = UtilTools.getCurrentUserName();
		String savePath = ServletActionContext.getServletContext().getRealPath(
				"");
		HttpServletRequest request = ServletActionContext.getRequest();
		String selectSvnName = request.getParameter("selectSvnName");
		savePath = savePath + "/uploads/";
		File f1 = new File(savePath);
		if (!f1.exists()) {
			f1.mkdirs();
		}

		List<File> list = new ArrayList<File>();
		try {
			getUploadFileList(list, savePath);
			SVNWorkCopy svn = SVNService.SVNMap.get(selectSvnName);
			String workCopyPath = svn.getWorkCopyPath();
			SVNURL svnurl = SvnUtil.parseSVNURL(svn.getSvnPath());
			SVNClientManager svnManager = SvnUtil.readyReadSVN(workCopyPath,
					svn);

			//这个流程要改改，效率很低
			for (File file : list) {
				List<File> filelist = UtilTools.getFiles(svn.getWorkCopyPath(),
						file.getName());
				if (filelist.size() > 0) {

					SvnUtil.update(svnManager, filelist.get(0),
							SVNRevision.HEAD, SVNDepth.INFINITY);

					File copyFile = UtilTools.copyFile(file.getAbsolutePath(),
							filelist.get(0).getAbsolutePath());
					String id = request.getSession().getId();
					// 提交文件到svn服务器
					if (SvnUtil.checkIsNewDirectoryOrFile(svnManager, copyFile)) {
						String path = svn.getSvnPath()
								+ copyFile.getAbsolutePath()
										.replace(svn.getWorkCopyPath(), "")
										.replace("\\", "/");
						svnurl = SVNURL.parseURIEncoded(path);
						SvnUtil.importDirectory(svnManager, copyFile, svnurl,
								userName, true);
					} else {
						SvnUtil.commit(svnManager, copyFile, true, userName);
					}
					// 删除临时文件
					file.delete();
				} else {
					throw new Exception("提交了svn服务器没有的文件，请检查");
				}
			}
			result = NetUtils.packActionResult("上传成功");
		} catch (Exception ex) {
			GMLogger.error("上传Excel to svn出现异常:" + ex.getMessage());
			result = NetUtils.packActionResult("上传Excel to svn出现异常:"
					+ ex.getMessage());
		}
		return "result";
	}
	
	public String handlerClassesCommitFtp() {
		String userName = UtilTools.getCurrentUserName();
		String savePath = ServletActionContext.getServletContext().getRealPath(
				"");
		HttpServletRequest request = ServletActionContext.getRequest();
		savePath = savePath + "/uploads/";
		File f1 = new File(savePath);
		if (!f1.exists()) {
			f1.mkdirs();
		}

		List<File> list = new ArrayList<File>();
		try {
			getUploadFileList(list, savePath);
			if (list.size() > 0) {
				File file = list.get(0);
				String fileName = UtilTools.getDateTimeString("yyyyMMddHHmmss");
				FtpManager.uploadFileToFtp(GlobalValue.CLASSES_FTP_PATH,
						file.getAbsolutePath(), fileName+".zip");
				// 删除临时文件
				file.delete();
			} else {
				throw new Exception("上传的文件失败！");
			}

			result = NetUtils.packActionResult("上传成功");
		} catch (Exception ex) {
			GMLogger.error("上传Excel to svn出现异常:" + ex.getMessage());
			result = NetUtils.packActionResult("上传Excel to svn出现异常:"
					+ ex.getMessage());
		}
		return "result";
	}
	
	
	private void getUploadFileList(List<File> list, String savePath) throws Exception{
		Iterator<File> it = filelist.iterator();
		String name = "";
		
		int count = 0;
		try {
			while (it.hasNext()) {
				File item = it.next();

				name = filelistFileName.get(count);
				if (name == null || name.trim().equals("")) {
					continue;
				}

				File file = new File(savePath + name);
				if (file.exists()) {
					file.delete();
				}
				FileInputStream in = new FileInputStream(item);
				FileOutputStream out = new FileOutputStream(file);
				IOUtils.copy(in, out);
				in.close();
				out.close();
				list.add(file);
			}
		} catch (Exception ex) {
			GMLogger.error("获取上传文件出错：" + ex.getMessage());
			throw new Exception("获取上传文件出错：" + ex.getMessage());
		}
	}

	public InputStream getResult() {
		return result;
	}

	public void setResult(InputStream result) {
		this.result = result;
	}

	public List<File> getFilelist() {
		return filelist;
	}

	public void setFilelist(List<File> filelist) {
		this.filelist = filelist;
	}

	public List<String> getFilelistFileName() {
		return filelistFileName;
	}

	public void setFilelistFileName(List<String> filelistFileName) {
		this.filelistFileName = filelistFileName;
	}
}
