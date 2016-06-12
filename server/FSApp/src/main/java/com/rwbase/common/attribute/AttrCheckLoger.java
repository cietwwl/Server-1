package com.rwbase.common.attribute;

import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class AttrCheckLoger {

    private static Logger attrCheckLog = Logger.getLogger("attrCheckLog");    
    
    public static void logAttr(String compnentName, String id, HashMap<Integer, AttributeItem> attrMap){
    	if(attrCheckLog!=null && attrCheckLog.getLevel() == Level.DEBUG){
    		String attrInfo = "id:"+id+ "\n"+AttributeUtils.partAttrMap2Str(compnentName, attrMap);
    		attrCheckLog.debug(attrInfo);
    	}
    }
	
}
