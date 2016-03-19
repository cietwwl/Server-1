package com.rw.config;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;

import com.google.protobuf.ByteString;

public class RawConfigReader {
	public static byte[] ReadConfig(String configFileName) {
		byte[] result = null;
		try {
			URL url = RawConfigReader.class.getResource("/config/" + configFileName);
			if (url == null) return result;
			String csvFn = url.getFile();
			if (csvFn == null) return result;
			File csvFile = new File(csvFn);
			
			FileInputStream fin = new FileInputStream(csvFile);
			byte[] buf = new byte[fin.available()];
			int readCount = fin.read(buf);
			if (readCount > 0) {
				if (readCount < buf.length) {
					result = Arrays.copyOf(buf, readCount);
				} else {
					result = buf;
				}
			}
			fin.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}
	public static ByteString ReadConfigForProto(String configFileName) {
		ByteString result = null;
		try {
			URL url = RawConfigReader.class.getResource("/config/" + configFileName);
			if (url == null) return result;
			String csvFn = url.getFile();
			if (csvFn == null) return result;
			File csvFile = new File(csvFn);
			
			FileInputStream fin = new FileInputStream(csvFile);
			byte[] buf = new byte[fin.available()];
			int readCount = fin.read(buf);
			if (readCount > 0) {
				result = ByteString.copyFrom(buf, 0, readCount);
			}
			fin.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}
}
