package com.analyse.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HistoAnalyzeHelper {

	
	private static Pattern pattern = Pattern.compile("\\s+");
	
	public static void main(String[] args) throws IOException {
		
		
		String filePath = "D:\\dump\\dump\\histo\\t1.histo";
		String filePath2 = "D:\\dump\\dump\\histo\\t2.histo";
		Map<String, Integer> b4 = parse(filePath);
		Map<String, Integer> after = parse(filePath2);
		boolean reverse = true;//反序
		String nameStart = "com.";
		delta(b4, after, reverse, nameStart);
		
	}

	private static Map<String,Integer>  parse( String filePath )throws FileNotFoundException, IOException {
		
		Map<String,Integer> countMap = new HashMap<String,Integer>();
		
		FileInputStream inputstream = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
		String line = br.readLine();
		while(line!=null){
			if(line.contains(":")){
				Matcher matcher = pattern.matcher(line);
				String newLine = matcher.replaceAll(" ");
				String[] split = newLine.split(" ");
				if(split.length==5){
					String className = split[4].trim();
					String instance = split[2].trim();
					countMap.put(className, Integer.valueOf(instance));
				}
			}
			line = br.readLine();
			
		}
		
		br.close();
		return countMap;
	}
	
	private static void delta(Map<String,Integer> before, Map<String,Integer>  after, boolean reverse, String nameStart){
		
		List<HistoInfo> list = new ArrayList<HistoInfo>();
		
		for (Entry<String, Integer> entryTmp : before.entrySet()) {
			String name = entryTmp.getKey();
			int countb4 = entryTmp.getValue();
			int countAfter = 0;
			if(after.containsKey(name)){
				countAfter = after.get(name);
			}
			HistoInfo histoInfo = new HistoInfo(name, countb4, countAfter);
			list.add(histoInfo);
			
		}
		
		Collections.sort(list, new Comparator<HistoInfo>() {

			@Override
			public int compare(HistoInfo source, HistoInfo target) {
				return source.getDelta() - target.getDelta();
			}
		});
		if(reverse){
			Collections.reverse(list);
		}
		
		for (HistoInfo histoInfo : list) {
			if(histoInfo.getName().startsWith(nameStart)){
				System.out.println("delta:"+histoInfo.getDelta()+"     b4:"+histoInfo.getB4()+"        after:"+histoInfo.getAfter()+"       name:"+histoInfo.getName());
			}
		}
		
		
	}
	
	
	
}
