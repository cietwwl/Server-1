package com.playerdata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rwbase.dao.hotPoint.EHotPointType;
import com.rwbase.dao.hotPoint.TableHotPoint;
import com.rwbase.dao.hotPoint.TableHotPointDAO;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.HotPointServiceProtos.HotPointInfo;
import com.rwproto.HotPointServiceProtos.HotPointResponse;
import com.rwproto.MsgDef.Command;

public class HotPointMgr {
	private static TableHotPointDAO hotPointDAO = TableHotPointDAO.getInstance();
	
	/**登陆时推送红点状态*/
	public static void loadPushHotPointState(Player player){
//		List<HotPointInfo> list = new ArrayList<HotPointInfo>();
//		TableHotPoint tableHotPoint = hotPointDAO.get(player.getUserId());
//		if(tableHotPoint != null){
//			Iterator it = tableHotPoint.getHotPointList().entrySet().iterator();
//			while(it.hasNext()){
//				Map.Entry<EHotPointType, Boolean> entry = (Map.Entry<EHotPointType, Boolean>)it.next();
//				createHotPointInfo(list, entry.getKey(), entry.getValue());
//			}
//			pushHotPoint(player, list);
//		}
	}
    
	/**红点数据改变*/
    public static void changeHotPointState(String userId, final EHotPointType hotPointType, final boolean value){
//    	TableHotPoint tableHotPoint = hotPointDAO.get(userId);
//    	if(tableHotPoint == null){
//    		tableHotPoint = new TableHotPoint();
//    		tableHotPoint.setUserId(userId);
//    	}
//    	boolean isUpdate = false;
//    	if(tableHotPoint.getHotPointList().containsKey(hotPointType)){
//    		if(tableHotPoint.getHotPointList().get(hotPointType) == value){
//    			isUpdate = false;   			
//    		}else{
//    			tableHotPoint.getHotPointList().put(hotPointType, value);
//    			isUpdate = true;
//    		}
//    	}else{
//    		tableHotPoint.getHotPointList().put(hotPointType, value);
//    		isUpdate = true;
//    	}
//    	if(isUpdate){
//    		hotPointDAO.update(tableHotPoint);
//    		boolean isOnline = PlayerMgr.getInstance().isOnline(userId);
//        	if(isOnline){
//        		GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerTask() {
//					@Override
//					public void run(Player player) {
//						pushHotPoint(player, createHotPointInfo(new ArrayList<HotPointInfo>(), hotPointType, value));
//						
//					}
//				});
//        	}
//    	}
    }
    
    /**推送推点列表*/
    private static void pushHotPoint(Player player, List<HotPointInfo> list){
    	HotPointResponse.Builder response = HotPointResponse.newBuilder();
    	response.addAllHotPointList(list);		
		player.SendMsg(Command.MSG_HOT_POINT, response.build().toByteString());
    }
    
    private static List<HotPointInfo> createHotPointInfo(List<HotPointInfo> list,EHotPointType hotPointType, boolean value){
    	HotPointInfo.Builder hotPointInfo = HotPointInfo.newBuilder();
		hotPointInfo.setType(hotPointType.getValue());
		hotPointInfo.setValue(value);
		list.add(hotPointInfo.build());
		return list;
    }
}
