package com.rw.dataSyn;

import java.util.ArrayList;
import java.util.List;

import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.SynData;
import com.rwproto.DataSynProtos.eSynOpType;

public class SynDataListHolder<T extends SynItem> {

	private Class<T> itemClazz;

	private List<T> m_SynItemList = new ArrayList<T>();

	public List<T> getItemList() {
		return m_SynItemList;
	}

	public SynDataListHolder(Class<T> clazz) {
		itemClazz = clazz;
	}

	public void Syn(MsgDataSyn msgDataSyn) {

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

		} catch (Exception ex) {
			throw (new RuntimeException("SynDataListHolder[Syn] error " + this.getClass(), ex));
		}
	}

	private void updateList(List<SynData> synDataList) {
		List<T> itemListTmp = new ArrayList<T>();
		for (SynData synData : synDataList) {
			T item = DataSynHelper.ToObject(itemClazz, synData.getJsonData());
			itemListTmp.add(item);
		}
		m_SynItemList = itemListTmp;
	}

	private void updateSingle(SynData synData) {
		T newT = DataSynHelper.ToObject(itemClazz, synData.getJsonData());
		T oldT = getById(newT.getId());
		m_SynItemList.remove(oldT);
		m_SynItemList.add(newT);
	}

	private T getById(String id) {
		T target = null;

		for (T item : m_SynItemList) {
			if (id.equals(item.getId())) {
				return item;
			}
		}
		return target;
	}

	private void addSingle(SynData synData) {
		T newT = DataSynHelper.ToObject(itemClazz, synData.getJsonData());
		m_SynItemList.add(newT);
	}

	private void removeSingle(SynData synData) {
		T oldT = getById(synData.getId());
		m_SynItemList.remove(oldT);
	}

}
