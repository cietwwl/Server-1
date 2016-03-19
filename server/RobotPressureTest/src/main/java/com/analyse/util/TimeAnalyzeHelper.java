package com.analyse.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class TimeAnalyzeHelper {

	
	public static void main(String[] args) throws IOException {
		
		
		String startwith = "|+---";
		String filePath = "C:\\Users\\Administrator\\Desktop\\logTmp.txt";
		printWithStart(startwith, filePath);
		
		
	}

	private static void printWithStart(String startwith, String filePath)
			throws FileNotFoundException, IOException {
		FileInputStream inputstream = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
		String line = br.readLine();
		while(line!=null){
			if(!line.startsWith(startwith+"+") &&!line.startsWith(startwith+"0")  ){
				System.out.println(line);
			}
			line = br.readLine();
			
		}
		
		br.close();
	}
	
	
}
