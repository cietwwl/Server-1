package com.common;


public class DetectionTool {

	private static final int JDK_8 = 8;
	private static final int JDK_7 = 7;
	private static final int JDK_6 = 6;
	private static final int JDK_5 = 5;
	
	private static final int JAVA_VERSION = getJavaVersion();
	private static final boolean IS_WINDOWS;
	
	static {
		String os = System.getProperty("os.name");
		os = os == null ? "" : os.toLowerCase();
		IS_WINDOWS = os.contains("win");
	}
	
	/**
	 * Return {@code true} if the JVM is running on Windows
	 * @return
	 */
	public static boolean isWindows() {
		return IS_WINDOWS;
	}
	
	public static int javaVersion() {
		return JAVA_VERSION;
	}
	
	private static int getJavaVersion() {
		String jdkVersion = System.getProperty("java.version");
		if (jdkVersion != null && jdkVersion.length() > 0) {
			String bigVer = jdkVersion.substring(0, 3);
			if (bigVer.equals("1.8")) {
				return JDK_8;
			} else if (bigVer.equals("1.7")) {
				return JDK_7;
			} else if (bigVer.equals("1.6")) {
				return JDK_6;
			} else {
				return JDK_5;
			}
		} else {
			return JDK_5;
		}
	}
}
