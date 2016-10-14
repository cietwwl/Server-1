package com.rw.handler.groupCompetition.data.guess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.rw.Client;
import com.rw.common.RobotLog;
import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GCQuizEventItemHolder {
	private static GCQuizEventItemHolder instance = new GCQuizEventItemHolder();
	
	public static GCQuizEventItemHolder getInstance() {
		return instance;
	}
	
	private Map<String, GCQuizEventItem> selfDetailList = new HashMap<String, GCQuizEventItem>();
	private Map<String, GCQuizEventItem> canQuizList = new HashMap<String, GCQuizEventItem>();
	
	private SynDataListHolder<GCQuizEventItem> listHolder = new SynDataListHolder<GCQuizEventItem>(GCQuizEventItem.class);
	
	public void syn(Client client, MsgDataSyn msgDataSyn, boolean isSelfDetail) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<GCQuizEventItem> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			GCQuizEventItem ugfData = itemList.get(i);
			if(isSelfDetail){
				if(StringUtils.isNotBlank(ugfData.getWinGroupId())){
					//竞猜已经出结果
					GCompUserQuizItem userQuizItem = client.getUserQuizItemHolder().getUserQuizData(ugfData.getMatchId());
					if(null != userQuizItem){
						if(StringUtils.equals(ugfData.getWinGroupId(), userQuizItem.getGroupId())){
							RobotLog.info(String.format("GCQuizEventItemHolder[get] syn 玩家[%s]竞猜[%s]成功，奖励已通过邮件发放", userQuizItem.getUserID(), userQuizItem.getMatchId()));
						}else{
							RobotLog.info(String.format("GCQuizEventItemHolder[get] syn 玩家[%s]竞猜[%s]失败...", userQuizItem.getUserID(), userQuizItem.getMatchId()));
						}
					}
				}
				selfDetailList.put(ugfData.getId(), ugfData);
			}else{
				canQuizList.put(ugfData.getId(), ugfData);
			}
		}
	}
	
	public GCQuizEventItem getCanQuizItem(){
		for(Entry<String, GCQuizEventItem> entry : canQuizList.entrySet()){
			if(!selfDetailList.containsKey(entry.getKey())){
				return entry.getValue();
			}
		}
		return null;
	}
}
