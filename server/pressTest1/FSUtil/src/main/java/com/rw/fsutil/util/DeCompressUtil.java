package com.rw.fsutil.util;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.types.resources.Archives;
import org.springframework.expression.spel.ast.Projection;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

public class DeCompressUtil {

	/**
	 * 解压zip格式压缩包
	 * 
	 * @param sourceZip
	 * @param destDir
	 * @throws Exception
	 */
	private static void unzip(String sourceZip, String destDir)
			throws Exception {
		try {
			Project project = new Project();
			Expand expand = new Expand();
			expand.setProject(project);
			expand.setSrc(new File(sourceZip));
			expand.setOverwrite(false);
			expand.setDest(new File(destDir));
			expand.setEncoding("gbk");
			expand.execute();
		} catch (Exception e) {
			throw new Exception("解压zip异常");
		}
	}

	/**
	 * 解压rar格式压缩包
	 * 
	 * @param sourceRar
	 * @param destDir
	 * @throws Exception
	 */
	private static void unrar(String sourceRar, String destDir)
			throws Exception {
		Archive archive = null;
		FileOutputStream fos = null;
		try {
			archive = new Archive(new File(sourceRar));
			FileHeader fh = archive.nextFileHeader();
			while (fh != null) {
				if (!fh.isDirectory()) {
					String compressFileName = fh.getFileNameString().trim();
					String destFileName = "";
					String destDirName = "";
					if (File.separator.equals("/")) {
						// 非windows系统
						destFileName = destDir
								+ compressFileName.replaceAll("\\\\", "/");
						destDirName = destFileName.substring(0,
								destFileName.lastIndexOf("/"));
					} else {
						// windows系统
						destFileName = destDir
								+ compressFileName.replaceAll("/", "\\\\");
						destDirName = destFileName.substring(0,
								destFileName.lastIndexOf("\\"));
					}
					File dir = new File(destDirName);
					if (!dir.exists() || !dir.isDirectory()) {
						dir.mkdirs();
					}
					fos = new FileOutputStream(new File(destFileName));
					archive.extractFile(fh, fos);
					fos.close();
					fos = null;
				}
				fh = archive.nextFileHeader();
			}
			archive.close();
			archive = null;
		} catch (Exception e) {
			// TODO: handle exception
			throw new Exception("解压rar异常"+e.getMessage());
		} finally {
			if (fos != null) {
				try {
					fos.close();
					fos = null;

				} catch (Exception e) {
					throw new Exception("解压rar异常"+e.getMessage());
				}
			}
			if (archive != null) {
				try {
					archive.close();
					archive = null;
				} catch (Exception e) {
					throw new Exception("解压rar异常"+e.getMessage());
				}
			}
		}
	}

	/**
	 * 解压缩
	 * @param sourceFile
	 * @param destDir
	 * @throws Exception
	 */
	public static void deCompress(String sourceFile, String destDir)
			throws Exception {
		// 保证文件夹路径最后是"/"或者"\"
		char lastChar = destDir.charAt(destDir.length() - 1);
		if (lastChar != '/' && lastChar != '\\') {
			destDir += File.separator;
		}
		// 根据类型，进行相应的解压缩
		String type = sourceFile.substring(sourceFile.lastIndexOf(".") + 1);
		String result = "";
		try {
			if (type.equals("zip")) {
				DeCompressUtil.unzip(sourceFile, destDir);
			} else if (type.equals("rar")) {
				DeCompressUtil.unrar(sourceFile, destDir);
			}
		} catch (Exception ex) {
			throw new Exception("只支持rar或zip格式的压缩包" + ex.getMessage());
		}
	}
}
