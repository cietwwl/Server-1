package com.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.fixEquip.cfg.FixEquipCfgChecker;
import com.rw.manager.ServerSwitch;

/**
 * This class saves a map of spring-bean ids to their corresponding interfaces. <br/>
 * Any bean-lookup can use this class getBeanId method to obtain a spring bean only specifying the interface class. <br/>
 * The bean-id-map of this class must be consistent <br/>
 * to the applicationContext.xml file.
 */
public class CfgChecker implements ApplicationContextAware {
	
	
	public void setApplicationContext(ApplicationContext context) {
		if(ServerSwitch.isCheckCfg()){
			
			GameLog.info(LogModule.COMMON.getName(), "CfgChecker", "配置检查开始。。。");
			FixEquipCfgChecker.checkAll();
			GameLog.info(LogModule.COMMON.getName(), "CfgChecker", "配置检查结束。。。");
		}
	}

	

}