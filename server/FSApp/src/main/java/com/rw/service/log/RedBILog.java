package com.rw.service.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.rw.fsutil.common.PairValue;
import com.rw.service.log.eLog.eBILogType;
import com.rw.service.log.template.BILogTemplate;

public class RedBILog {

	public static void main(String[] args) throws Exception {
		BILogMgr logMgr = new BILogMgr();
		eBILogType[] eBILogTypeArray = eBILogType.values();
		HashMap<String, LinkedHashMap<String, Integer>> mapping = new HashMap<String, LinkedHashMap<String, Integer>>();
		for (eBILogType type : eBILogTypeArray) {
			BILogTemplate template = logMgr.getBILogTemplate(type);
			if (template == null) {
				continue;
			}
			LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
			String tips = template.getTextTemplate();
			tips = tips.replace("$|", ";;;");
			tips = tips.replace("|$", ";;;");
			tips = tips.replace("$", "");
			String[] array = tips.split(";;;");
			int i = 0;
			for (String s : array) {
				map.put(s, i++);
			}
			mapping.put(type.getLogName(), map);
			if (template.getTextTemplate().contains("ItemChangedEventType_1")) {
				System.out.println(type.getLogName() + "," + template.getTextTemplate().contains("userId") + "," + map.get("ItemChangedEventType_1"));
			}
		}

		Comparator<PairValue<Date, String>> c = new Comparator<PairValue<Date, String>>() {

			@Override
			public int compare(PairValue<Date, String> o1, PairValue<Date, String> o2) {
				return o1.firstValue.compareTo(o2.firstValue);
			}
		};

		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			File dir = new File("E:\\fenxi\\biLog\\biLog");
			String[] list = dir.list();
			HashMap<String, ArrayList<PairValue<Date, String>>> resultMap = new HashMap<String, ArrayList<PairValue<Date, String>>>();
			for (String path : list) {
				File f = new File("E:\\fenxi\\biLog\\biLog\\" + path);
				String[] fList = f.list();
				String name = find(fList);
				if (name == null) {
					continue;
				}
				LinkedHashMap<String, Integer> map = mapping.get(path);
				Integer userIdIndex = map.get("userId");
				if (userIdIndex == null) {
					continue;
				}
				File readFile = new File(f.getAbsolutePath() + "\\" + name);
				BufferedReader reader = new BufferedReader(new FileReader(readFile));
				String temp;
				while ((temp = reader.readLine()) != null) {
					String t = temp.replace("|", ";;;;;");
					String[] tArray = t.split(";;;;;");
					String[] dateArray = tArray[0].split(" ");
					String dateString = dateArray[3] + " " + dateArray[4];
					Date date = formater.parse(dateString);
					String userId = tArray[userIdIndex];
					ArrayList<PairValue<Date, String>> resultList = resultMap.get(userId);
					if (resultList == null) {
						resultList = new ArrayList<PairValue<Date, String>>();
						resultMap.put(userId, resultList);
					}
					resultList.add(new PairValue<Date, String>(date, temp));
				}
				reader.close();
			}
			String basePath = "E:\\fenxi\\biLog\\";
			for (Map.Entry<String, ArrayList<PairValue<Date, String>>> entry : resultMap.entrySet()) {
				PrintWriter writer = new PrintWriter(basePath + entry.getKey());
				Collections.sort(entry.getValue(), c);
				for (PairValue<Date, String> pair : entry.getValue()) {
					writer.append(pair.secondValue + "\r\n");
				}
				writer.flush();
				writer.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static String find(String[] list) {
		for (String s : list) {
			if (s.contains("2016-12-11")) {
				return s;
			}
		}
		return null;
	}
}
