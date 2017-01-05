package com.gm;

import com.rw.fsutil.shutdown.IShutdownHandler;

public class GmShutdownHandler implements IShutdownHandler {

	@Override
	public void notifyShutdown() {
		GmHotFixManager.shutdown();
	}

}
