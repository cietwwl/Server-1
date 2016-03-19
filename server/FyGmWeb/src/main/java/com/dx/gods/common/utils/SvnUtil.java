package com.dx.gods.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.internal.wc.patch.SVNPatch;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnStatus;

import com.dx.gods.common.log.GMLogger;
import com.dx.gods.service.tools.SVNWorkCopy;

public class SvnUtil {
	public static void setupLibrary() {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}

	public static SVNURL parseSVNURL(String svnPath) {
		try {
			return SVNURL.parseURIEncoded(svnPath);
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 验证登录svn
	 */
	public static SVNClientManager authSvn(String svnRoot, String username,
			String password) {
		// 初始化版本库
		setupLibrary();

		// 创建库连接
		SVNRepository repository = null;
		try {
			repository = SVNRepositoryFactory.create(SVNURL
					.parseURIEncoded(svnRoot));
		} catch (SVNException e) {
			e.printStackTrace();
			GMLogger.error("创建svn库连接异常:"+e.getMessage());
			return null;
		}

		// 身份验证
		ISVNAuthenticationManager authManager = SVNWCUtil

		.createDefaultAuthenticationManager(username, password);

		// 创建身份验证管理器
		repository.setAuthenticationManager(authManager);

		DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
		SVNClientManager clientManager = SVNClientManager.newInstance(options,
				authManager);
		return clientManager;
	}

	/**
	 * Make directory in svn repository
	 * 
	 * @param clientManager
	 * @param url
	 *            eg: http://svn.ambow.com/wlpt/bsp/trunk
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	public static SVNCommitInfo makeDirectory(SVNClientManager clientManager,
			SVNURL url, String commitMessage) {
		try {
			return clientManager.getCommitClient().doMkDir(
					new SVNURL[] { url }, commitMessage);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Imports an unversioned directory into a repository location denoted by a
	 * destination URL
	 * 
	 * @param clientManager
	 * @param localPath
	 *            a local unversioned directory or singal file that will be
	 *            imported into a repository;
	 * @param dstURL
	 *            a repository location where the local unversioned
	 *            directory/file will be imported into
	 * @param commitMessage
	 * @param isRecursive
	 *            递归
	 * @return
	 */
	public static SVNCommitInfo importDirectory(SVNClientManager clientManager,
			File localPath, SVNURL dstURL, String commitMessage,
			boolean isRecursive) {
		try {
			return clientManager.getCommitClient().doImport(localPath, dstURL,
					commitMessage, null, true, true,
					SVNDepth.fromRecurse(isRecursive));
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Puts directories and files under version control
	 * 
	 * @param clientManager
	 *            SVNClientManager
	 * @param wcPath
	 *            work copy path
	 */
	public static void addEntry(SVNClientManager clientManager, File wcPath) {
		try {
			clientManager.getWCClient().doAdd(new File[] { wcPath }, true,
					false, false, SVNDepth.INFINITY, false, false, true);
		} catch (SVNException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Collects status information on a single Working Copy item
	 * 
	 * @param clientManager
	 * @param wcPath
	 *            local item's path
	 * @param remote
	 *            true to check up the status of the item in the repository,
	 *            that will tell if the local item is out-of-date (like '-u'
	 *            option in the SVN client's 'svn status' command), otherwise
	 *            false
	 * @return
	 * @throws SVNException
	 */
	public static SVNStatus showStatus(SVNClientManager clientManager,
			File wcPath, boolean remote) {
		SVNStatus status = null;
		try {
			status = clientManager.getStatusClient().doStatus(wcPath, remote);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Commit work copy's change to svn
	 * 
	 * @param clientManager
	 * @param wcPath
	 *            working copy paths which changes are to be committed
	 * @param keepLocks
	 *            whether to unlock or not files in the repository
	 * @param commitMessage
	 *            commit log message
	 * @return
	 * @throws SVNException
	 */
	public static SVNCommitInfo commit(SVNClientManager clientManager,
			File wcPath, boolean keepLocks, String commitMessage) {
		try {
			return clientManager.getCommitClient().doCommit(
					new File[] { wcPath }, keepLocks, commitMessage, null,
					null, true, false, SVNDepth.INFINITY);
		} catch (SVNException e) {
			e.printStackTrace();
			GMLogger.error("svn commit文件异常:"+e.getMessage());
		}
		return null;
	}
	
	public static SVNCommitInfo commit(SVNClientManager clientManager, File[] wcPaths, boolean keepLocks, String commitMessage){
		try {
			return clientManager.getCommitClient().doCommit(
					wcPaths, keepLocks, commitMessage, null,
					null, true, false, SVNDepth.INFINITY);
		} catch (SVNException e) {
			e.printStackTrace();
			GMLogger.error("svn commit文件异常:"+e.getMessage());
		}
		return null;
	}

	/**
	 * 如果svn上面没有该文件 则需要调用该方法 导入文件到svn服务器中
	 */
	public static SVNCommitInfo importFile(SVNClientManager clientManager,
			File wcPath, String localPath, String svnPath,
			String importMessage, boolean recursive) {
		SVNCommitClient commitClient = clientManager.getCommitClient();

		String path = svnPath
				+ wcPath.getAbsolutePath().replace(localPath, "")
						.replace("\\", "/");
		SVNURL svnurl;
		try {
			svnurl = SVNURL.parseURIEncoded(path);
			SVNCommitInfo doImport = commitClient.doImport(wcPath, svnurl,
					importMessage, false);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 检查本地文件或者文件在svn服务器的状态 true 为新 false 已存在
	 * 
	 * @param clientManager
	 * @param wcPath
	 * @return
	 */
	public static boolean checkIsNewDirectoryOrFile(
			SVNClientManager clientManager, File wcPath) {
		SVNStatusClient statusClient = clientManager.getStatusClient();
		try {
			SVNStatus doStatus = statusClient.doStatus(wcPath, true);
			if (doStatus == null) {
				return true;
			} else {
				return false;
			}
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}
	}
	
	/**
	 * 获取本地文件的版本号
	 * @param clientManager
	 * @param file
	 * @return
	 */
	public static long getFileSVNVersionNo(SVNClientManager clientManager, File file){
		SVNStatusClient statusClient = clientManager.getStatusClient();
		try{ 
			SVNStatus status = statusClient.doStatus(file, false);
			if(status == null){
				return 0;
			}else{
				SVNRevision revision = status.getCommittedRevision();
				return revision.getNumber();
				
			}
		}catch(SVNException e){
			return 0;
		}
	}
	
	public static boolean checkFileIsModify(SVNClientManager clientManager,
			File file) {
		SVNStatusClient statusClient = clientManager.getStatusClient();
		try {
			SVNStatus status = statusClient.doStatus(file, false);
			if (status == null) {
				return false;
			} else {
				SVNStatusType contentsStatus = status.getContentsStatus();
				return contentsStatus.equals(SVNStatusType.STATUS_MODIFIED);

			}
		} catch (SVNException e) {
			return false;
		}
	}

	public static long[] update(SVNClientManager clientManager, File[] wcPaths, SVNRevision updateToRevision, SVNDepth depth){
		SVNUpdateClient updateClient = clientManager.getUpdateClient();
		/*
		 * sets externals not to be ignored during the update
		 */
		updateClient.setIgnoreExternals(false);

		/*
		 * returns the number of the revision wcPath was updated to
		 */
		try {
			return updateClient.doUpdate(wcPaths, updateToRevision, depth,
					false, false);
		} catch (SVNException e) {
			e.printStackTrace();
			GMLogger.error("svn update文件异常:"+e.getMessage());
		}
		return null;
	}
	
	public static long update(SVNClientManager clientManager, File wcPath,
			SVNRevision updateToRevision, SVNDepth depth) {
		SVNUpdateClient updateClient = clientManager.getUpdateClient();

		/*
		 * sets externals not to be ignored during the update
		 */
		updateClient.setIgnoreExternals(false);

		/*
		 * returns the number of the revision wcPath was updated to
		 */
		try {
			return updateClient.doUpdate(wcPath, updateToRevision, depth,
					false, false);
		} catch (SVNException e) {
			e.printStackTrace();
			GMLogger.error("svn update文件异常:"+e.getMessage());
		}
		return 0;
	}
	
	/**
	 * 调用svn命令执行更新
	 * @return
	 */
	public static String updateSVN(String path) throws Exception {
		try {
			Process p = Runtime.getRuntime().exec("svn update \"" + path+"\"");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("<br/>");
			}
			br.close();
			return sb.toString();
		} catch (Exception ex) {
			throw new Exception("update svn exception:" + ex.getMessage());
		}
	}

	/**
	 * recursively checks out a working copy from url into wcDir
	 * 
	 * @param clientManager
	 * @param url
	 *            a repository location from where a Working Copy will be
	 *            checked out
	 * @param revision
	 *            the desired revision of the Working Copy to be checked out
	 * @param destPath
	 *            the local path where the Working Copy will be placed
	 * @param depth
	 *            checkout的深度，目录、子目录、文件
	 * @return
	 * @throws SVNException
	 */
	public static long checkout(SVNClientManager clientManager, SVNURL url,
			SVNRevision revision, File destPath, SVNDepth depth) {

		SVNUpdateClient updateClient = clientManager.getUpdateClient();
		/*
		 * sets externals not to be ignored during the checkout
		 */
		updateClient.setIgnoreExternals(false);
		/*
		 * returns the number of the revision at which the working copy is
		 */
		try {
			return updateClient.doCheckout(url, destPath, revision, revision,
					depth, false);
		} catch (SVNException e) {
			e.printStackTrace();
			GMLogger.error("svn checkout文件异常:"+e.getMessage());
		}
		return 0;
	}

	/**
	 * 确定path是否是一个工作空间
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isWorkingCopy(File path) {
		if (!path.exists()) {
			System.out.println("can not find the path!!!");
			return false;
		}
		try {
			if (null == SVNWCUtil.getWorkingCopyRoot(path, false)) {
				return false;
			}
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 确定一个URL在SVN上是否存在
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isURLExist(SVNURL url, String username,
			String password) {
		try {
			SVNRepository svnRepository = SVNRepositoryFactory.create(url);
			ISVNAuthenticationManager authManager = SVNWCUtil
					.createDefaultAuthenticationManager(username, password);
			svnRepository.setAuthenticationManager(authManager);
			SVNNodeKind nodeKind = svnRepository.checkPath("", -1);
			return nodeKind == SVNNodeKind.NONE ? false : true;
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static SVNClientManager readyReadSVN(String workCopyPath, SVNWorkCopy svn){
		File workCopyFolder = new File(workCopyPath);
		if (!workCopyFolder.exists()) {
			workCopyFolder.mkdir();
		}
		// 这里的svn的用户名暂时用固定的，以后改成谁登陆用谁的用户名和密码
		SVNClientManager svnManager = SvnUtil.authSvn(svn.getSvnPath(),
				svn.getLoginName(), svn.getLoginPwd());
		return svnManager;
	}
	
	
}
