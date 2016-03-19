package com.rwbase.dao.business;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SevenDayGifInfoHolder {//玩家基本信息
	
	private SevenDayGifInfoDAO sevenDayGifInfoDAO = SevenDayGifInfoDAO.getInstance();
	private SevenDayGifInfo sevenDayGifInfo;
	private boolean modified = false;
	final private eSynType synType = eSynType.SEVEN_DAY_GIF;
	
	public SevenDayGifInfoHolder(Player player) {
		String userId = player.getUserId();
		sevenDayGifInfo =  sevenDayGifInfoDAO.get(userId );
		if(sevenDayGifInfo == null){
			SevenDayGifInfo newItem = new SevenDayGifInfo();
			newItem.setUserId(userId);
			
			boolean success = sevenDayGifInfoDAO.update(newItem);
			if(success){
				sevenDayGifInfo = newItem;
				ClientDataSynMgr.updateData(player, sevenDayGifInfo, synType, eSynOpType.UPDATE_SINGLE);
			}
		}
	}
	

	public void synData(Player player){
		ClientDataSynMgr.synData(player, sevenDayGifInfo, synType, eSynOpType.UPDATE_SINGLE);
	}

	
	public SevenDayGifInfo get(){
		return sevenDayGifInfo;
	}

	public void udpate(Player player){
		modified = true;
		ClientDataSynMgr.updateData(player, sevenDayGifInfo, synType, eSynOpType.UPDATE_SINGLE);
		if(modified){//test  fluash无作用
			sevenDayGifInfoDAO.update(sevenDayGifInfo);
			modified = false;
		}
	}
	
	public void flush(){
		if(modified){
			sevenDayGifInfoDAO.update(sevenDayGifInfo);
			modified = false;
		}
	}



}