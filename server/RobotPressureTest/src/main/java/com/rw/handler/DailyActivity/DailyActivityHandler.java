package com.rw.handler.DailyActivity;

import com.rw.Robot;
import com.rw.handler.mainService.MainHandler;

public class DailyActivityHandler {
	private static DailyActivityHandler handler = new DailyActivityHandler();

	public static DailyActivityHandler getHandler() {
		return handler;
	}
	/**消费300钻*/
	public boolean Const(Robot robot) {
		// TODO Auto-generated method stub
		boolean isConstEnough = true;
		for(int i = 0;i<3;i++){
			boolean issucc =robot.gambleByGold();
			System.out.println("@@@@@@@@@@单抽" + issucc+ System.currentTimeMillis());
			if(!issucc){
				isConstEnough = false;
				break;
			}
		}
		
		
		
		
		
		return isConstEnough;
	}
	
	
	
	
	
	
	
}
