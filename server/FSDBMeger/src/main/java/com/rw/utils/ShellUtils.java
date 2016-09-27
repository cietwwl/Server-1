package com.rw.utils;

public class ShellUtils {
	public static void genShell(String path, String shellContent){
		StringBuilder sb = new StringBuilder();
		sb.append("basepath=$(cd `dirname $0`; pwd)").append("\n");
		sb.append("cd $basepath").append("\n");
		sb.append(shellContent);
		FileUtils.saveFile(path, sb.toString());
	}
}
