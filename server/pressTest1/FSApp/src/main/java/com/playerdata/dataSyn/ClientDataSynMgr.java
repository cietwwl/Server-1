package com.playerdata.dataSyn;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.MsgDataSynList;
import com.rwproto.DataSynProtos.MsgDataSynList.Builder;
import com.rwproto.DataSynProtos.SynData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.MsgDef.Command;

/**
 * 同步数据到客户端的Mgr类
 * 
 * @author HC
 *
 */
public class ClientDataSynMgr {

	private ClientDataSynMgr() {
	}

	/**
	 * 同步某些数据列表到客户端
	 * 
	 * @param player
	 * @param serverDataList 要同步到客户端的数据
	 * @param synType <b><i>同步数据模块类型</i></b> {@link eSynType} 例如，同步背包数据----->类型就是{@link eSynType#USER_ITEM_BAG}
	 * @param synOpType <b><i>数据类型</i></b> {@link eSynOpType} 例如，同步的是背包所有数据----->类型就是{@link eSynOpType#UPDATE_LIST}
	 */
	public static void synDataList(Player player, List<?> serverDataList, eSynType synType, eSynOpType synOpType) {
		synDataList(player, serverDataList, synType, synOpType, player.getDataSynVersionHolder().getVersion(synType));
	}

	/**
	 * 同步某些数据列表到客户端
	 * 
	 * @param player
	 * @param serverDataList 要同步到客户端的数据
	 * @param synType <b><i>同步数据模块类型</i></b> {@link eSynType} 例如，同步背包数据----->类型就是{@link eSynType#USER_ITEM_BAG}
	 * @param synOpType <b><i>数据类型</i></b> {@link eSynOpType} 例如，同步的是背包所有数据----->类型就是{@link eSynOpType#UPDATE_LIST}
	 * @param newVersion 推送到客户端的版本号
	 */
	public static void synDataList(Player player, List<?> serverDataList, eSynType synType, eSynOpType synOpType, int newVersion) {
		try {
			MsgDataSyn.Builder msgDataSyn = MsgDataSyn.newBuilder();
			for (Object serverData : serverDataList) {
				SynData.Builder synData = transferToClientData(serverData);
				msgDataSyn.addSynData(synData);
			}
			msgDataSyn.setSynOpType(synOpType);
			msgDataSyn.setSynType(synType);
			msgDataSyn.setVersion(newVersion);
			
			if(serverDataList.isEmpty()){
				//列表为空的时候list 的 hashcode是一样的，这个时候要传同步对象本身。
				sendMsg(player, msgDataSyn, synType, msgDataSyn);
			}else{
				
				sendMsg(player, serverDataList, synType, msgDataSyn);
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), player.getUserId(), "ClientDataSynMgr[synDataList] synType:" + synType + " synOpType:"
					+ synOpType, e);
		}
	}

	/**
	 * 同步某些数据到客户端
	 * 
	 * @param player
	 * @param serverData 要同步的数据
	 * @param synType <b><i>同步数据模块类型</i></b> {@link eSynType} 例如，同步背包模块的道具数据----->类型就是{@link eSynType#USER_ITEM_BAG}
	 * @param synOpType <b><i>数据类型</i></b> {@link eSynOpType} 例如同步了一个道具的数据，----->类型就是{@link eSynOpType#UPDATE_SINGLE}
	 */
	public static void synData(Player player, Object serverData, eSynType synType, eSynOpType synOpType) {
		synData(player, serverData, synType, synOpType, player.getDataSynVersionHolder().getVersion(synType));
	}

	/**
	 * 同步某些数据到客户端
	 * 
	 * @param player
	 * @param serverData 要同步的数据
	 * @param synType <b><i>同步数据模块类型</i></b> {@link eSynType} 例如，同步背包模块的道具数据----->类型就是{@link eSynType#USER_ITEM_BAG}
	 * @param synOpType <b><i>数据类型</i></b> {@link eSynOpType} 例如同步了一个道具的数据，----->类型就是{@link eSynOpType#UPDATE_SINGLE}
	 * @param newVersion 要同步到前台的客户端版本
	 */
	public static void synData(Player player, Object serverData, eSynType synType, eSynOpType synOpType, int newVersion) {
		try {
			MsgDataSyn.Builder msgDataSyn = MsgDataSyn.newBuilder();

			SynData.Builder synData = transferToClientData(serverData);
			msgDataSyn.addSynData(synData);
			msgDataSyn.setSynOpType(synOpType);
			msgDataSyn.setSynType(synType);
			msgDataSyn.setVersion(newVersion);
			sendMsg(player, serverData, synType, msgDataSyn);
		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), player.getUserId(), "ClientDataSynMgr[synData] synType:" + synType + " synOpType:" + synOpType, e);
		}
	}

	/**
	 * 更新数据的时候，推送到客户端
	 * 
	 * @param player
	 * @param serverData 数据
	 * @param synType <b><i>同步数据模块类型</i></b> {@link eSynType} 例如，更新背包模块的道具数据----->类型就是{@link eSynType#USER_ITEM_BAG}
	 * @param synOpType <b><i>数据类型</i></b> {@link eSynOpType} 例如更新了一个道具的数据，----->类型就是{@link eSynOpType#UPDATE_SINGLE}
	 */
	public static void updateData(Player player, Object serverData, eSynType synType, eSynOpType synOpType) {
		try {
			player.getDataSynVersionHolder().addVersion(synType);
			synData(player, serverData, synType, synOpType);
		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), player.getUserId(),
					"ClientDataSynMgr[updateData] synType:" + synType + " synOpType:" + synOpType, e);
		}
	}

	/**
	 * 更新多个数据的时候，推送到客户端
	 * 
	 * @param player
	 * @param serverData 数据
	 * @param synType <b><i>同步数据模块类型</i></b> {@link eSynType} 例如，更新背包模块的道具数据----->类型就是{@link eSynType#USER_ITEM_BAG}
	 * @param synOpType <b><i>数据类型</i></b> {@link eSynOpType} 例如更新了多个道具的数据，----->类型就是{@link eSynOpType#UPDATE_LIST}
	 */
	public static void updateDataList(Player player, List<?> serverDataList, eSynType synType, eSynOpType synOpType) {
		try {
			player.getDataSynVersionHolder().addVersion(synType);
			synDataList(player, serverDataList, synType, synOpType);
		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), player.getUserId(), "ClientDataSynMgr[updateDataList] synType:" + synType + " synOpType:"
					+ synOpType, e);
		}
	}

	public static SynData.Builder transferToClientData(Object serverData) throws Exception {
		ClassInfo4Client serverClassInfo = DataSynClassInfoMgr.getByClass(serverData.getClass());

		String jsonData = serverClassInfo.toJson(serverData);
		String id = serverClassInfo.getId(serverData);
		if (StringUtils.isBlank(id)) {
			id = "id";
		}
		SynData.Builder synData = SynData.newBuilder().setId(id);
		if (StringUtils.isNotBlank(jsonData)) {
			synData.setJsonData(jsonData);
		}
		return synData;
	}

	private static void sendMsg(Player player, Object serverData, eSynType synType, MsgDataSyn.Builder msgDataSyn) {
		SynDataInReqMgr synDataInReqMgr = player.getSynDataInReqMgr();
		if (synDataInReqMgr.isInReq()) {
			SynDataInfo synDataInfo = new SynDataInfo(synType, msgDataSyn);
			synDataInReqMgr.addSynData(serverData, synDataInfo);

		} else {
			Builder msgDataSynList = MsgDataSynList.newBuilder().addMsgDataSyn(msgDataSyn);
			player.SendMsg(Command.MSG_DATA_SYN, msgDataSynList.build().toByteString());
		}
	}
}