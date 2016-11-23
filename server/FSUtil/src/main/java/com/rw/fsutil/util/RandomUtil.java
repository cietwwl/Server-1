package com.rw.fsutil.util;

import java.util.Random;

public class RandomUtil {

	private static ThreadLocal<Random> randomThreadLocal = new ThreadLocal<Random>();

	public static Random getRandom() {
		Random random = randomThreadLocal.get();
		if (random == null) {
			random = new Random();
			randomThreadLocal.set(random);
		}
		return random;
	}
	
}
