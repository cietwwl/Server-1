package com.rw.handler.copy.data;

import java.util.HashMap;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class CopyHolder{
	
	private Map<Integer, Integer> copyTime = new HashMap<Integer, Integer>();
	private SynDataListHolder<CopyLevelRecord> listHolder = new SynDataListHolder<CopyLevelRecord>(CopyLevelRecord.class);
	
	public CopyHolder() { }
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
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
