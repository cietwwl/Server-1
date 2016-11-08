package com.rw.trace.stat;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;

public class PrintBits implements Callable<Void> {

	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		Class<?> c = Class.forName("java.nio.Bits");
		Field f1 = c.getDeclaredField("reservedMemory");
		f1.setAccessible(true);
		System.out.println(f1.get(null));
		Field f2 = c.getDeclaredField("maxMemory");
		f2.setAccessible(true);
		System.out.println(f2.get(null));
	}

	@Override
	public Void call() throws Exception {
		Class<?> c = Class.forName("java.nio.Bits");
		Field f1 = c.getDeclaredField("reservedMemory");
		f1.setAccessible(true);
		System.out.println("bits print:" + f1.get(null));
		Field f2 = c.getDeclaredField("maxMemory");
		f2.setAccessible(true);
		System.out.println("bits print:" + f2.get(null));
		PrintWriter w = new PrintWriter("bits.log");
		w.write(f1.get(null) + "," + f2.get(null));
		w.flush();
		w.close();
		return null;
	}
}
