package com.gm.gmsender;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import com.log.GameLog;
import com.log.LogModule;

public class GmSenderPool {
	
	private GenericObjectPool senderPool;
	
	public GmSenderPool(GmSenderConfig senderConfig){		
		
		Config config = new Config();
		config.maxActive = 100;
		config.minIdle = 10;
		config.testOnBorrow = true;
		config.testWhileIdle = true;
		config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
		this.senderConfig = senderConfig;
		senderPool = new GenericObjectPool(new GmSenderFactory(senderConfig), config);
	}
	
	public GmSender borrowSender(){
		
		Object borrowObject = null;
		try {
			borrowObject = senderPool.borrowObject();
		} catch (Exception e) {
			GameLog.error(LogModule.GmSender, "GmSenderPool[borrowSender]", "",e);
		}
		
		return borrowObject==null?null:(GmSender)borrowObject;
	}
	public void returnSender(GmSender gmSender){
		if(gmSender!=null){
			try {
				senderPool.returnObject(gmSender);
			} catch (Exception e) {
				GameLog.error(LogModule.GmSender, "GmSenderPool[returnSender]", "",e);
			}
		}
	}



	
	
	
	
}
