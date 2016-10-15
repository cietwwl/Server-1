package com.rw.handler.groupCompetition.data.baseinfo;

import com.rw.dataSyn.DataSynHelper;
import com.rw.handler.groupCompetition.util.GCompEventsStatus;
import com.rw.handler.groupCompetition.util.GCompStageType;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GCompBaseInfoHolder {
	
//	private SynDataListHolder<GCompBaseInfo> listHolder = new SynDataListHolder<GCompBaseInfo>(GCompBaseInfo.class);
	private GCompBaseInfo baseInfoData;
	private long lastRunGCompTime;
	
	public void syn(MsgDataSyn msgDataSyn) {
//		listHolder.Syn(msgDataSyn);
//		baseInfoData = listHolder.getItemList().get(0);
		baseInfoData = DataSynHelper.ToObject(GCompBaseInfo.class, msgDataSyn.getSynData(0).getJsonData());
		System.out.println("baseInfoData:" + baseInfoData);
	}
	
	public GCompBaseInfo getGCompBaseInfo() {
		return baseInfoData;
	}
	
	public boolean isEventsStart() {
		GCompEventsStatus status = baseInfoData.getEventStatus();
		return baseInfoData.getCurrentStageType() == GCompStageType.EVENTS && status.sign > GCompEventsStatus.PREPARE.sign && status.sign < GCompEventsStatus.FINISH.sign;
	}

	public long getLastRunGCompTime() {
		return lastRunGCompTime;
	}

	public void setLastRunGCompTime(long lastRunGCompTime) {
		this.lastRunGCompTime = lastRunGCompTime;
	}
}
