package com.fy.utils;

public class DeviceUtil {
	
    private final static String PhoneSign = "iPhone";
    private final static String PadSign = "iPad";
    private final static String PodSign = "iPod";
	
	public static boolean checkIos32Or64(String deviceModel, String cpuType) {
		if (cpuType.equals("arm64")) {
			String[] value = deviceModel.split(",");
			if (value.length != 2) {
				return false;
			}
			String model = value[0];
			// iPhone
			if (deviceModel.indexOf(PhoneSign) != -1) {
				int versionNo = Integer.parseInt(model.replace(PhoneSign, ""));
				if (versionNo >= 6) {
					return true;
				}
			}
			// iPad
			if (deviceModel.indexOf(PadSign) != -1) {
				int versionNo = Integer.parseInt(model.replace(PadSign, ""));
				if (versionNo >= 4) {
					return true;
				}
			}
			//iPod
			if (deviceModel.indexOf(PodSign) != -1) {
				int versionNo = Integer.parseInt(model.replace(PodSign, ""));
				if (versionNo >= 6) {
					return true;
				}
			}
		}
		return false;
	}
}
