package com.rw.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.rw.fsutil.dao.cache.DataCacheFactory;

public class PrintServerState {

	public static void startPrintState() {
		try {
			final PrintWriter w = new PrintWriter(new FileWriter(new File("tmpLogger2"), true));
			w.append("查询开始\n");
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
			executor.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					w.append("===================================\n");
					w.append("===================================\n");
					Map<Class<?>, Integer> map = DataCacheFactory.getCacheStat();
					int count = 0;
					for (Map.Entry<Class<?>, Integer> entry : map.entrySet()) {
						w.append("{").append(entry.getKey().getSimpleName()).append("=").append(String.valueOf(entry.getValue())).append("} ");
						if (++count % 5 == 0) {
							w.append("\n");
						}
					}
					w.append("\n");
					w.append("===================================\n");
					w.append("===================================\n");
					w.flush();
				}
			}, 0, 1, TimeUnit.MINUTES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
