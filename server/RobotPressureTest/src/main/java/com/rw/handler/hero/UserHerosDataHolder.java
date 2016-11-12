package com.rw.handler.hero;

import java.util.ArrayList;
import java.util.List;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class UserHerosDataHolder {
	private SynDataListHolder<TableUserHero> listHolder = new SynDataListHolder<TableUserHero>(TableUserHero.class);
	private TableUserHero tableUserHero;
	
	private static UserHerosDataHolder instance = new UserHerosDataHolder();
	
	public static UserHerosDataHolder getInstance(){
		return instance;
	}
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);

		List<TableUserHero> itemList = listHolder.getItemList();

		this.tableUserHero = itemList.get(0);
	}
	
	public TableUserHero getTableUserHero(){
		return this.tableUserHero;
	}
}
