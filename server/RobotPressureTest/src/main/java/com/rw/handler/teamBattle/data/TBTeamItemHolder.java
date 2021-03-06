package com.rw.handler.teamBattle.data;

import java.util.List;

import com.rw.actionHelper.ActionRateHelper;
import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class TBTeamItemHolder{
	
	private final ActionRateHelper rateHelper;
	private TBTeamItem teamItem = null;
	
	private SynDataListHolder<TBTeamItem> listHolder = new SynDataListHolder<TBTeamItem>(TBTeamItem.class);
	
	public TBTeamItemHolder(ActionRateHelper rateHelper){
		this.rateHelper = rateHelper;
	}
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<TBTeamItem> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			teamItem = itemList.get(i);
		}
	}
	
	public TBTeamItem getTeamData(){
		return teamItem;
	}
}
