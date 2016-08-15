package com.rw.dataSyn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.rw.common.RobotLog;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.SynData;
import com.rwproto.DataSynProtos.eSynOpType;

public class SynDataListHolder<T extends SynItem> {

	private Class<T> itemClazz;

	private List<T> m_SynItemList = new ArrayList<T>();

	public synchronized List<T> getItemList() {
		List<T> copyList = new ArrayList<T>(m_SynItemList);
		return copyList;
	}

	public SynDataListHolder(Class<T> clazz) {
		itemClazz = clazz;
	}

	public synchronized void Syn(MsgDataSyn msgDataSyn) {

		try {
			List<SynData> synDataList = msgDataSyn.getSynDataList();
			eSynOpType opType = msgDataSyn.getSynOpType();
			
			switch (opType) {
			case UPDATE_LIST:
				updateList(synDataList);
				break;
			case UPDATE_SINGLE:
				updateSingle(synDataList.get(0));
				break;
			case ADD_SINGLE:
				addSingle(synDataList.get(0));
				break;
			case REMOVE_SINGLE:
				removeSingle(synDataList.get(0));
				break;

			default:
				break;
			}

		} catch (Throwable ex) {
			RobotLog.fail(ex.getMessage(),ex );
			ex.printStackTrace();
		}
	}

	private  void updateList(List<SynData> synDataList) {
		List<T> itemListTmp = new ArrayList<T>();
		try{
		for (SynData synData : synDataList) {
			T item = DataSynHelper.ToObject(itemClazz, synData.getJsonData());
			itemListTmp.add(item);
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		m_SynItemList = itemListTmp;
	}

	private void updateSingle(SynData synData) {
		T newT = DataSynHelper.ToObject(itemClazz, synData.getJsonData());
		update(newT);
	}

	private void update(T newItem) {
		for (Iterator iterator = m_SynItemList.iterator(); iterator.hasNext();) {
			T tempItem = (T) iterator.next();
			if(tempItem.getId().equals(newItem.getId())){
				iterator.remove();
			}
		}
		m_SynItemList.add(newItem);
	}
	
	private void remove(String Id){
		for (Iterator iterator = m_SynItemList.iterator(); iterator.hasNext();) {
			T tempItem = (T) iterator.next();
			if(tempItem.getId().equals(Id)){
				iterator.remove();
			}
		}
	}

	private void addSingle(SynData synData) {
		T newT = DataSynHelper.ToObject(itemClazz, synData.getJsonData());
		m_SynItemList.add(newT);
	}

	private void removeSingle(SynData synData) {
		remove(synData.getId());
	}

}
