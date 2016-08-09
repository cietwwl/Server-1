package com.rw.fsutil.dao.cache;

import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

public class BufferedByteWriter {

	private final int max;
	private final int compressSize;
	private byte[] array;

	public BufferedByteWriter(int max, int compressSize) {
		this.max = max;
		this.compressSize = compressSize;
		this.array = new byte[max * 6];
	}

	private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
	private AtomicInteger size = new AtomicInteger();

	public void add(String content) {
		queue.offer(content);
		int current = size.addAndGet(content.length());
		if (current >= max) {

		}
	}

	public void flush() {
		int current = size.get();
		for (;;) {
			String text = queue.poll();
			if (text == null) {
				break;
			}
			size.getAndAdd(-text.length());
			try {
				byte[] temp = text.getBytes("UTF-8");
				CharBuffer cb  = CharBuffer.allocate(1024 * 1024 * 4);
				char[] array = new char[1024];
				StringBuilder sb = new StringBuilder();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
}
