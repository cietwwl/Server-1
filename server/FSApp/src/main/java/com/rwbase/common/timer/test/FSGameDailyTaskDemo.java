package com.rwbase.common.timer.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class FSGameDailyTaskDemo implements IGameTimerTask {

	@Override
	public String getName() {
		return "FSGameDailyTaskDemo";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		String osName = System.getProperty("os.name");
		if(osName != null && osName.toLowerCase().contains("windows")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			File f = new File("D:\\DailyTest.txt");
			if(!f.exists()) {
				f.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(f, true);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(sdf.format(new Date(timeSignal.getAssumeExecuteTime())));
			bw.write("\n");
			bw.close();
		}
		return "SUCCESS";
	}

	@Override
	public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
		
	}

	@Override
	public void rejected(RejectedExecutionException e) {
		
	}

	@Override
	public boolean isContinue() {
		return true;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		return null;
	}

}
