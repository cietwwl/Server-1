package com.fy.utils;

import java.io.File;
import java.util.List;

public class FileUtils {

	public static void sumFiles(File file, List<File> fileList, String fileFilter) {

		if (file.isFile()) {
			fileList.add(file);
		} else if (file.isDirectory()) {
			File[] fileArray = file.listFiles();
			for (File fileTmp : fileArray) {
				// 筛选指定格式的版本文件（指定格式为txt）
				if (fileTmp.getName().indexOf(fileFilter) == -1 && fileTmp.isFile()) {
					continue;
				}
				sumFiles(fileTmp, fileList, fileFilter);

			}
		}
	}
}
