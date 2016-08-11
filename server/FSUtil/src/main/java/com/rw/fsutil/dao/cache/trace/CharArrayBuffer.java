package com.rw.fsutil.dao.cache.trace;

import java.util.Arrays;

public class CharArrayBuffer {

	private char[] value;

	private int count;

	private final int maxCapacity;

	public CharArrayBuffer(int initCapacity, int capacity) {
		this.value = new char[initCapacity];
		this.maxCapacity = capacity;
	}

	public CharArrayBuffer append(String str) {
		if (str == null)
			str = "null";
		int len = str.length();
		if (len == 0)
			return this;
		int newCount = count + len;
		if (newCount > value.length)
			expandCapacity(newCount);
		str.getChars(0, len, value, count);
		count = newCount;
		return this;
	}

	public CharArrayBuffer append(Object obj) {
		return append(String.valueOf(obj));
	}

	public CharArrayBuffer append(char c) {
		int newCount = count + 1;
		if (newCount > value.length)
			expandCapacity(newCount);
		value[count++] = c;
		return this;
	}

	public CharArrayBuffer append(char str[]) {
		int newCount = count + str.length;
		if (newCount > value.length)
			expandCapacity(newCount);
		System.arraycopy(str, 0, value, count, str.length);
		count = newCount;
		return this;
	}

	void expandCapacity(int minimumCapacity) {
		int newCapacity = (value.length + 1) * 2;
		if (newCapacity < 0) {
			newCapacity = Integer.MAX_VALUE;
		} else if (minimumCapacity > newCapacity) {
			newCapacity = minimumCapacity;
		}
		value = Arrays.copyOf(value, newCapacity);
	}

	public int getCharArrayCapacity() {
		return value.length;
	}

	public int size() {
		return count;
	}

	public char[] getValue() {
		return value;
	}

	public void clear() {
		this.count = 0;
		if (value.length > maxCapacity) {
			this.value = new char[maxCapacity];
		}
	}
}
