package com.rwbase.common.dirtyword.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.rwbase.common.dirtyword.CharFilter;
import com.rwbase.common.dirtyword.MappedCharFilter;

/**
 * 脏词管理器
 * 
 * @author Rjx
 */
public class CharFilterImpl implements CharFilter {

	private CharNode dirtyRoot;
	private CharNode lowerDirtyRoot;
	private CharNode sensitiveRoot;
	private CharNode lowerSensitiveRoot;
	private boolean[] fiterConnectors;
	private int maxConnectorIndex;
	private boolean[] unseeChars;
	private int maxUnseeIndex;
	static HashSet<String> set = new HashSet<String>();
	private static HashMap<Class, Integer> statistics = new HashMap<Class, Integer>();

	static void fill(CharNodeContainer origal, CharNodeContainer ignoreCase, List<String> wordList) {
		for (int i = wordList.size(); --i >= 0;) {
			set.add(wordList.get(i));
		}
		for (String s : set) {
			origal.add(s, 0);
			ignoreCase.add(s.toLowerCase(), 0);
		}
	}

	static void count(Class clazz) {
		Integer value = statistics.get(clazz);
		value = value == null ? 1 : value + 1;
		statistics.put(clazz, value);
	}

	static Map<Class, Integer> getStatistics() {
		return statistics;
	}

	// @Override
	public void init(List<String> dirtyWords, List<String> sensitiveWords, List<Integer> unseeChars, List<String> filterConnectors) {
		CharNodeContainer origal = new CharNodeContainer((char) -1);
		CharNodeContainer lowerCaseContainer = new CharNodeContainer((char) -1);
		// Element tmpE = null;
		// // 读取脏词表数据文档
		// tmpE = root.element("dirtyWords");
		if (dirtyWords != null) {
			fill(origal, lowerCaseContainer, dirtyWords);
			this.dirtyRoot = origal.toCharNode();
			this.lowerDirtyRoot = lowerCaseContainer.toCharNode();
		}
		// 清空
		set.clear();
		origal = new CharNodeContainer((char) -1);
		lowerCaseContainer = new CharNodeContainer((char) -1);
		// tmpE = root.element("sensitiveWord");
		if (sensitiveWords != null) {
			fill(origal, lowerCaseContainer, sensitiveWords);
			this.sensitiveRoot = origal.toCharNode();
			this.lowerSensitiveRoot = origal.toCharNode();
		}

		TreeSet<Integer> treeset = new TreeSet<Integer>();
		// 读取隐形字符表数据文档
		for (int i = unseeChars.size(); --i >= 0;) {
			treeset.add(unseeChars.get(i));
		}
		if (treeset.size() > 0) {
			this.unseeChars = new boolean[treeset.last() + 1];
			for (int unsee : treeset) {
				this.unseeChars[unsee] = true;
			}
			this.maxUnseeIndex = unseeChars.size();
		}else{
			this.unseeChars = new boolean[0];
			this.maxUnseeIndex = 0;
		}
		// 读取隐形字符表数据文档
		// tmpE = root.element("filterConnectors");
		treeset.clear();
		for (Iterator<String> it = filterConnectors.iterator(); it.hasNext();) {
			String conntect = it.next();
			char[] array = conntect.toCharArray();
			if (array.length != 1) {
				throw new ExceptionInInitializerError("过滤词连接字符多于一个char");
			}
			treeset.add((int) array[0]);
		}

		if (treeset.size() > 0) {
			// 初始化过滤词连接字符
			this.fiterConnectors = new boolean[treeset.last() + 1];
			for (int connector : treeset) {
				this.fiterConnectors[connector] = true;
			}
			this.maxConnectorIndex = this.fiterConnectors.length;
		} else {
			this.maxConnectorIndex = 0;
			this.fiterConnectors = new boolean[0];
		}
		// fiterConnectors = new boolean[33];
		// fiterConnectors[32] = true;
		// this.maxConnectorIndex = 33;
	}

	@Override
	public boolean checkWords(String content, boolean checkUnseeChar, boolean checkSensitiveWord, boolean checkDirtyWord, boolean isIgnoreCase) {
		int length = content.length();
		if (checkUnseeChar) {
			for (int i = 0; i < length; i++) {
				char c = content.charAt(i);
				if (c < maxUnseeIndex && unseeChars[c]) {
					return true;
				}
			}
		}

		if (!checkSensitiveWord && !checkDirtyWord) {
			return false;
		}

		CharNode dirty;
		CharNode sensitive;
		if (isIgnoreCase) {
			dirty = this.lowerDirtyRoot;
			sensitive = this.lowerSensitiveRoot;
			content = content.toLowerCase();
		} else {
			dirty = this.dirtyRoot;
			sensitive = this.sensitiveRoot;
		}

		if (checkSensitiveWord && checkWords(sensitive, content) != null) {
			return true;
		}
		return !checkDirtyWord || checkWords(dirty, content) != null;
	}

	/**
	 * 检查是否包些词
	 * 
	 * @param nodeRoot
	 * @param content
	 * @return
	 */
	private String checkWords(CharNode nodeRoot, String content) {
		int length = content.length();
		for (int i = 0; i < length; i++) {
			char c = content.charAt(i);
			CharNode node = nodeRoot.getNext(c);
			if (node == null) {
				continue;
			}
			String dirtyString = node.content;
			if (dirtyString != null) {
				return dirtyString;
			}
			// 第一个符合的下标，记录下先
			int index = i + 1;
			for (; index < length; index++) {
				char nextChar = content.charAt(index);
				CharNode nextNode = node.getNext(nextChar);
				if (nextNode == null) {
					if (nextChar < maxConnectorIndex && fiterConnectors[nextChar]) {
						continue;
					}
					break;
				}
				dirtyString = nextNode.content;
				if (dirtyString != null) {
					return dirtyString;
				}
				node = nextNode;
			}
		}
		return null;
	}

	@Override
	public String removeUnseeChar(String content) {
		int length = content.length();
		int start = 0;
		StringBuilder sb = null;
		for (int i = 0; i < length; i++) {
			char c = content.charAt(i);
			if (c < 0 || c >= maxUnseeIndex || !unseeChars[c]) {
				continue;
			}
			if (sb == null) {
				sb = new StringBuilder(length - 1);
			}
			for (; start < i; start++) {
				sb.append(content.charAt(start));
			}
			start = i + 1;
		}
		if (sb == null) {
			return content;
		}
		if (start < length) {
			for (; start < length; start++) {
				sb.append(content.charAt(start));
			}
		}
		return sb.toString();
	}

	@Override
	public String replaceDiryWords(String content, String replaceWord, boolean isClearUnseeChar, boolean isIgnoreCase) {
		if (isClearUnseeChar) {
			content = removeUnseeChar(content);
		}
		String oldContent = content;
		CharNode root;
		if (isIgnoreCase) {
			root = lowerDirtyRoot;
			content = content.toLowerCase();
		} else {
			root = dirtyRoot;
		}
		StringBuilder sb = null;
		int length = content.length();
		int lastDiryIndex = 0;
		for (int i = 0; i < length; i++) {
			char c = content.charAt(i);
			CharNode node = root.getNext(c);
			if (node == null) {
				continue;
			}
			String dirtyString = node.content;
			// 第一个符合的下标，记录下先
			int firstIndex = i;
			int index = firstIndex + 1;
			for (; index < length; index++) {
				char nextChar = content.charAt(index);
				CharNode nextNode = node.getNext(nextChar);
				if (nextNode == null) {
					if (nextChar < maxConnectorIndex && fiterConnectors[nextChar]) {
						continue;
					}
					break;
				}
				String str = nextNode.content;
				if (str != null) {
					dirtyString = str;
				}
				node = nextNode;
			}
			if (dirtyString != null) {
				if (sb == null) {
					sb = new StringBuilder(length + 10);
				}
				// 将之前的放进去
				for (; lastDiryIndex < firstIndex; lastDiryIndex++) {
					sb.append(oldContent.charAt(lastDiryIndex));
				}
				sb.append(replaceWord);
				i = index - 1;
				lastDiryIndex = index;
			}
		}
		if (sb == null) {
			return oldContent;
		}
		if (lastDiryIndex < length) {
			for (; lastDiryIndex < length; lastDiryIndex++) {
				sb.append(oldContent.charAt(lastDiryIndex));
			}
		}
		return sb.toString();
	}

	@Override
	public String checkDirtyWord(String content, boolean isIgnoreCase) {
		CharNode root;
		if (isIgnoreCase) {
			root = lowerDirtyRoot;
			content = content.toLowerCase();
		} else {
			root = dirtyRoot;
		}
		return this.checkWords(root, content);
	}

	@Override
	public String checkSensitiveWord(String content, boolean isIgnoreCase) {
		CharNode root;
		if (isIgnoreCase) {
			root = lowerSensitiveRoot;
			content = content.toLowerCase();
		} else {
			root = sensitiveRoot;
		}
		return this.checkWords(root, content);
	}

	/**
	 * 获取所有字符串的不同大小写的排列组合
	 * 
	 * @param str
	 * @return
	 */
	Set<String> getAllCaseStringSet(String str) {
		HashSet<String> list = new HashSet<String>();
		char[] array = new char[str.length()];
		toAllStringSet(str, array, 0, list);
		return list;
	}

	void toAllStringSet(String str, char[] array, int count, HashSet<String> list) {
		char c = str.charAt(count);
		c = Character.toLowerCase(c);
		for (int i = 2; --i >= 0;) {
			if (i == 0) {
				char newChar = Character.toUpperCase(c);
				if (c == newChar) {
					continue;
				}
				c = newChar;
			}
			array[count] = c;
			if (count == (array.length - 1)) {
				list.add(new String(array));
			} else {
				toAllStringSet(str, array, count + 1, list);
			}
		}
	}

	/**
	 * 构造一个映射的字符集
	 * 
	 * @return
	 */
	@Override
	public MappedCharFilter newMappedCharFilter() {
		return new MappedCharFilterImpl();
	}

}
