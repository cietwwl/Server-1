package com.rw.handler.majordata;

import java.util.List;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;


public class MajorDataholder {
	private static class InstanceHolder{
		private static MajorDataholder instance = new MajorDataholder();
	}
	
	public static MajorDataholder getInstance(){
		return InstanceHolder.instance;
	}
	
	private SynDataListHolder<MajorData> listHolder = new SynDataListHolder<MajorData>(MajorData.class);
	
	private MajorData majordata = new MajorData();
	
	public void syn(MsgDataSyn msgDataSyn){
		listHolder.Syn(msgDataSyn);
		
		List<MajorData> itemList = listHolder.getItemList();
		
		for (MajorData majorData : itemList) {
			this.majordata = majorData;
		}
		
	}
	
	public boolean CheckEnoughCoin(){
		if(majordata.getCoin() > 1000000){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean CheckEnoughGold(){
		if(majordata.getGold() > 1000){
			return true;
		}else{
			return false;
		}
	}
}
