package com.rw.fsutil.dao.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetFileString {

	public static String getFileString(String fileName){
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File("F:\\test.txt")),"UTF-8")
					);
			StringBuilder sb = new StringBuilder();
			String temp;
			while((temp =reader.readLine())!=null){
				sb.append(temp);
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
