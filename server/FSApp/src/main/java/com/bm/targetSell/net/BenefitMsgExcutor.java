package com.bm.targetSell.net;

import com.rw.fsutil.remote.parse.FSMessageExecutor;
import com.rwbase.gameworld.GameWorldFactory;

public class BenefitMsgExcutor implements FSMessageExecutor<String>{

	@Override
	public void execute(String message) {
		
		GameWorldFactory.getGameWorld().asynExecute(new ResponseTask(message));
	}
	
	
	private class ResponseTask implements Runnable{

		private String content;
		
		
		
		public ResponseTask(String content) {
			this.content = content;
		}



		@Override
		public void run() {
//			System.out.println("recv response:" + content);
			BenefitSystemMsgService.getHandler().doTask(content);
		}
		
	}

}
