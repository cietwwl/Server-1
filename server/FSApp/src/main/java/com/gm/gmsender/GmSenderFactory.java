package com.gm.gmsender;

import java.io.IOException;

import org.apache.commons.pool.PoolableObjectFactory;

import com.log.GameLog;
import com.log.LogModule;

public class GmSenderFactory implements PoolableObjectFactory{
	
	
	private GmSenderConfig senderConfig;
	
	public GmSenderFactory(GmSenderConfig senderConfig){
		this.senderConfig = senderConfig;
	}
	
	@Override
	public Object makeObject() {
		
		GmSender gmSender = null;
		try {
			gmSender = new GmSender(senderConfig);
		} catch (IOException e) {
			GameLog.error(LogModule.GmSender, "GmSenderFactory[makeObject]", "socket 初始化出错",e);
		}
		
		return gmSender;
	}

	@Override
	public void destroyObject(Object obj) throws Exception {
		if(obj!=null){
			GmSender gmSender = (GmSender)obj;
			gmSender.destroy();
		}
		
	}

	@Override
	public boolean validateObject(Object obj) {
		//will add to pool if success
		boolean pass = false;
		if(obj!=null){
			GmSender gmSender = (GmSender)obj;
			pass = gmSender.isAvailable();
		}
		return pass;
	}

	@Override
	public void activateObject(Object obj) throws Exception {
		//call when borrowObject  
		
	}

	@Override
	public void passivateObject(Object obj) throws Exception {
		//call when returnObject
		
	}

}
