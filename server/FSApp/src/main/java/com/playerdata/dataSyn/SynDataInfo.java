package com.playerdata.dataSyn;

import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.eSynType;

public class SynDataInfo {

	private eSynType synType;
	
	private MsgDataSyn.Builder content;
	
	public SynDataInfo(eSynType synTypeP,MsgDataSyn.Builder contentP){
		this.synType = synTypeP;
		this.content = contentP;
	}


	public eSynType getSynType() {
		return synType;
	}



	public MsgDataSyn.Builder getContent() {
		return content;
	}
	
	
	
}
