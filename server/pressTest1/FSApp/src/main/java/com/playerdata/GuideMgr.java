package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rwbase.dao.guide.TableGuideDAO;
import com.rwbase.dao.guide.pojo.GuideData;
import com.rwbase.dao.guide.pojo.TableGuide;
import com.rwproto.GuideServiceProtos.GuideRequestType;
import com.rwproto.GuideServiceProtos.GuideResponse;
import com.rwproto.GuideServiceProtos.tagGuide;
import com.rwproto.MsgDef.Command;

public class GuideMgr {
	private TableGuide m_TableGuide;
	
	private Player m_pPlayer = null;
	//初始化
	public boolean init(Player pOwner)
	{
		m_pPlayer  = pOwner;
		m_TableGuide = TableGuideDAO.getInstance().get(pOwner.getUserId());
		if(m_TableGuide == null){
			m_TableGuide = new TableGuide();
			m_TableGuide.setUserId(pOwner.getUserId());
		}
		return true;
	}
	public boolean save() {
		return TableGuideDAO.getInstance().update(m_TableGuide);
	}
	
	public void ChangeState(List<GuideData> list){
		HashMap<Integer, GuideData> pGuideMap = m_TableGuide.getGuideMap();
		for (GuideData guideData : list) {
			if(pGuideMap.containsKey(guideData.getId())){
				pGuideMap.get(guideData.getId()).setState(guideData.getState());
			}else{
				GuideData newdata = new GuideData();
				newdata.setId(guideData.getId());
				newdata.setState(guideData.getState());
				pGuideMap.put(guideData.getId(), newdata);
			}
		}
	}
	
	public void syncGuideAll(){
		HashMap<Integer, GuideData> pGuideMap = getGuideMap();
		List<GuideData> list = new ArrayList<GuideData>();
		for (Iterator<Entry<Integer,GuideData>> iterator = pGuideMap.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<Integer,GuideData> dic = iterator.next();
			list.add(dic.getValue());
		}
		sendList(list);
	}

	
	private void sendList(List<GuideData> list) {
		GuideResponse.Builder  resp = GuideResponse.newBuilder();
		resp.setRequestType(GuideRequestType.SyncGuide);
		tagGuide.Builder tag;
		for (GuideData pGuideData : list) {
			tag = tagGuide.newBuilder();
			tag.setId(pGuideData.getId());
			tag.setState(pGuideData.getState().ordinal());
			resp.addGuide(tag);
		}
		m_pPlayer.SendMsg(Command.MSG_GUIDE, resp.build().toByteString());
	}
	
	public HashMap<Integer, GuideData> getGuideMap() {
		return m_TableGuide.getGuideMap();
	}
}
