package com.rw.fsutil.shutdown;

import java.util.ArrayList;
import java.util.List;

/**
 * 停服操作通知
 * 
 * @author lida
 *
 */
public class ShutdownService {
	
	private final static List<IShutdownHandler> ShutdownHandlerList = new ArrayList<IShutdownHandler>();

	public static synchronized void registerShutdownService(IShutdownHandler handler) {
		ShutdownHandlerList.add(handler);
	}

	public static void notifyShutdown() {
		for (IShutdownHandler handler : ShutdownHandlerList) {
			try {
				handler.notifyShutdown();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
