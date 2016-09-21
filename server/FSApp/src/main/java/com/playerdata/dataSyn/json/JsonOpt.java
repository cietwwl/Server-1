package com.playerdata.dataSyn.json;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rwproto.DataSynProtos.MsgDataSyn;

@SynClass
public class JsonOpt {

	private Map<String,String> oriShortMap = new HashMap<String,String>();
	
	@IgnoreSynField
	private AtomicInteger sGen = new AtomicInteger();
	
	@IgnoreSynField
	private boolean doOpt = false;
	
	@IgnoreSynField
	private static JsonOpt noOptInstance = new JsonOpt();
	
	public static JsonOpt newWithOpt(){
		JsonOpt jsonOpt = new JsonOpt();
		jsonOpt.doOpt = true;
		return jsonOpt;
	}
	public static JsonOpt newWithoutOpt(){		
		return noOptInstance;
	}
	
	public String getShort(String target){
		if(!doOpt || target == null){
			return target;
		}
		
		if(!oriShortMap.containsKey(target)){
			String shortValue = ""+sGen.incrementAndGet();
			oriShortMap.put(target, shortValue);
		}
		
		return oriShortMap.get(target);
	}
	
	public String setOptMapStr(MsgDataSyn.Builder msgDataSyn){
		String clientData = ClientDataSynMgr.toClientData(this);
		if(clientData!=null){
			msgDataSyn.setOptMap(clientData);
		}
		return clientData;
	}
	
	
}
