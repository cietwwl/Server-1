//package com.rw.service.guide;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.google.protobuf.ByteString;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.playerdata.Player;
//import com.rw.service.FsService;
//import com.rwbase.common.enu.eGuideStateDef;
//import com.rwbase.dao.guide.pojo.GuideData;
//import com.rwproto.GuideServiceProtos.GuideRequest;
//import com.rwproto.GuideServiceProtos.GuideRequestType;
//import com.rwproto.GuideServiceProtos.tagGuide;
//import com.rwproto.RequestProtos.Request;
//
//public class GuideService implements FsService {
//
//	@Override
//	public ByteString doTask(Request request, Player player) {
//		ByteString result = null;
//		try {
//			GuideRequest req = GuideRequest.parseFrom(request.getBody().getSerializedContent());
//			GuideRequestType reqType = req.getRequestType();
//			switch (reqType) {
//			case changeState:
//				changeState(req.getGuideList(),player);
//				break;
//
//			default:
//				break;
//			}
//		}catch(InvalidProtocolBufferException e){
//			e.printStackTrace();
//		}
//		return result;
//	}
//	
//	private void changeState(List<tagGuide> list, Player player){
//		GuideData pGuideData;
//		List<GuideData> changeList = new ArrayList<GuideData>();
//		for (tagGuide tagGuide : list) {
//			pGuideData = new GuideData();
//			pGuideData.setId(tagGuide.getId());
//			pGuideData.setState(eGuideStateDef.getDef(tagGuide.getState()));
//			changeList.add(pGuideData);
//		}
//		player.getGuideMgr().ChangeState(changeList);
//	}
//}
