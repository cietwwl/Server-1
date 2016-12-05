package com.gm.multipletimeshotfix;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;

import com.bm.targetSell.TargetSellManager;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class TargetSellReplace implements Callable<String> {

	//扫描次数
	private int scanCount = 60;
	
	@Override
	public String call() throws Exception {
		FSGameTimerMgr.getInstance().submitSecondTask(new ScanTask(), 1);
		return "SUCCESS";
	}

	
	
	private void Scancel(){
		try {
			
			synchronized(TargetSellManager.class){
				Field f = TargetSellManager.class.getDeclaredField("manager");
				f.setAccessible(true);
				TargetSellManager instance = (TargetSellManager) f.get(null);
				if (instance.getClass() == TargetSellMgrFix.class) {
					//TODO
					return;
				}
				TargetSellMgrFix fix = new TargetSellMgrFix();
				
				fix.getDatas();
				f.set(null, fix);
				System.out.println("检测替换是否成功：" + (TargetSellManager.getInstance() == fix));
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	class ScanTask implements IGameTimerTask{

		@Override
		public String getName() {
			return null;
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal)
				throws Exception {
			Scancel();
			scanCount --;
			if(scanCount > 0){
				FSGameTimerMgr.getInstance().submitSecondTask(this, 1);
			}
			return null;
		}

		@Override
		public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
			
		}

		@Override
		public void rejected(RejectedExecutionException e) {
			
		}

		@Override
		public boolean isContinue() {
			return false;
		}

		@Override
		public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
			return null;
		}
		
		
	}
	
}
