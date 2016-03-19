package com.dx.gods.controller.admin.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DevelopLogController extends DXAdminController {
	
	private List<String> list;
	private String key;
	private String fileName;

	public String getLogList() {
		String resin_path = System.getProperty("user.dir");
		File folder = new File(resin_path + "/logs/"); // 获得指定路径
		FileFilter fileFilter = new FileFilter(); // 文件名过滤器
		File[] files = folder.listFiles(fileFilter);
		list = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			list.add(files[i].getName());
		}
		return SUCCESS;
	}

	public String readFile() {
		String resin_path = System.getProperty("user.dir");
		File file = new File(resin_path + "/logs/" + fileName); // 获得指定路径
		list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line != null && key != null && !line.contains(key)) {
					continue;
				}
				list.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public class FileFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.startsWith("gm_log");
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public List<String> getList() {
		return list;
	}

}
