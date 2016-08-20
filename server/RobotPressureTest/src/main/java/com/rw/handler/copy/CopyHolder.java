package com.rw.handler.copy;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.eSynType;

public class CopyHolder{
	
	private Map<Integer, Integer> copyTime = new HashMap<Integer, Integer>();
	
	public CopyHolder() { }
	
	public void syn(MsgDataSyn msgDataSyn) {
		int i = 0;
		i++;
		System.out.println(i);		
	}

	public Map<Integer, Integer> getCopyTime() {
		return copyTime;
	}

	public void setCopyTime(Map<Integer, Integer> copyTime) {
		this.copyTime = copyTime;
	}


	
	
	
}
