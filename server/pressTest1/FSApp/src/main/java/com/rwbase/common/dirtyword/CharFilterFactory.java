package com.rwbase.common.dirtyword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.rwbase.common.dirtyword.impl.CharFilterImpl;

/**
 * 字符过滤器工厂
 * 
 * @author Rjx
 */
public class CharFilterFactory {

	private static String DIRTY_WORD_PATH = "dirty.txt";
	private static CharFilterImpl service;

	public static void init() {
		try {
			ArrayList<String> filterConnectors = new ArrayList<String>();
			filterConnectors.add(" ");
			filterConnectors.add("-");
			filterConnectors.add("_");
			filterConnectors.add(".");
			filterConnectors.add(",");
			filterConnectors.add("。");
			ArrayList<Integer> unseeChars = new ArrayList<Integer>();
			for (int i = 0; i < 32; i++) {
				unseeChars.add(i);
			}
			unseeChars.add(127);
			unseeChars.add(129);
			unseeChars.add(160);

			Resource resource = new ClassPathResource(DIRTY_WORD_PATH);
			System.out.println(resource.getFile());
			BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()));
			String temp = reader.readLine();
			if (temp == null) {
				throw new ExceptionInInitializerError("找不到脏词表：" + DIRTY_WORD_PATH);
			}
			ArrayList<String> list = new ArrayList<String>();
			StringTokenizer token = new StringTokenizer(temp, "|");
			while (token.hasMoreElements()) {
				list.add(token.nextToken());
			}
			service = new CharFilterImpl();
			service.init(list, Collections.EMPTY_LIST, unseeChars, filterConnectors);
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	/**
	 * 获取字符过滤器
	 * 
	 * @return
	 */
	public static CharFilter getCharFilter() {
		return service;
	}

	public static void main(String[] args) {
		CharFilterFactory factory = new CharFilterFactory();
		System.out.println(factory.getCharFilter().checkDirtyWord("做.爱", true));
		System.out.println(factory.getCharFilter().replaceDiryWords("你做什么啊做爱吗", "**", true, false));
	}
}