package com;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class HotFixClassCreator {

	public static final String MULTIPLE_TIME_HOT_FIX = "com/gm/multipletimeshotfix"; // 可以多次执行的hot fix
	public static final String ONE_TIME_HOT_FIX = "com/gm/onetimehotfix"; // 只执行的hot fix
	
	private static void copyFiles(String path, File root) throws Exception {
		File dir = new File(root.getPath() + File.separator + path);
		URL url = ClassLoader.getSystemResource(path);
		File packageFile = new File(url.getFile());
		File[] files = packageFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			File temp = files[i];
			if (temp.getName().endsWith(".class")) {
				if(!dir.exists()) {
					dir.mkdirs();
				}
				File copy = new File(dir.getPath() + File.separator + temp.getName());
				copy.createNewFile();
				FileOutputStream fos = new FileOutputStream(copy);
				FileInputStream fis = new FileInputStream(temp);
				byte[] bs = new byte[1024];
				int readCount;
				while ((readCount = fis.read(bs)) > 0) {
					fos.write(bs, 0, readCount);
				}
				fos.flush();
				fos.close();
				fis.close();
			}
		}
	}
	
	private static void deleteFiles(File file) {
		if (file.isFile()) {
			file.delete();
		} else {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFiles(files[i]);
			}
			file.delete();
		}
	}
	
	private static void compress(File file , ZipOutputStream out, String baseDir) throws Exception {
		if(file.isDirectory()) {
			compressDir(file, out, baseDir);
		} else {
			compressFile(file, out, baseDir);
		}
	}
	
	private static void compressDir(File dir, ZipOutputStream out, String baseDir) throws Exception {
		File[] files = dir.listFiles();
		for(int i = 0; i < files.length; i++) {
			compress(files[i], out, baseDir + dir.getName() + File.separator);
		}
	}
	
	private static void compressFile(File file, ZipOutputStream out, String baseDir) throws Exception {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		ZipEntry entry = new ZipEntry(baseDir + file.getName());
		out.putNextEntry(entry);
		int count;
		byte[] data = new byte[1024];
		while((count = bis.read(data)) > 0) {
			out.write(data, 0, count);
		}
		out.flush();
		bis.close();
	}
	
	private static void zip() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
		File zipFile = new File("./" + "hotfix_" + sdf.format(new Date()) + ".zip");
		if(zipFile.exists()) {
			zipFile.delete();
		}
		FileOutputStream fos = new FileOutputStream(zipFile);
		CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());
		ZipOutputStream out = new ZipOutputStream(cos);
		String baseDir = "";
		File file = new File(TARGET_PATH);
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			compress(files[i], out, baseDir);
		}
		out.close();
	}
	
	private static final String TARGET_PATH = "./hotFix";

	public static void main(String[] args) throws Exception {
		File target = new File(TARGET_PATH);
		if(target.exists()) {
			deleteFiles(target);
		}
		target.mkdir();
		copyFiles(ONE_TIME_HOT_FIX, target);
		copyFiles(MULTIPLE_TIME_HOT_FIX, target);
		zip();
	}
}
