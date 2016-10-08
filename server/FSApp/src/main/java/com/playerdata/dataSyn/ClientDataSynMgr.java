package com.playerdata.dataSyn;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.json.JsonOpt;
import com.rw.netty.UserChannelMgr;
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
			
			player.getDataSynVersionHolder().addVersion(synType);
			MsgDataSyn.Builder msgDataSyn = MsgDataSyn.newBuilder();
			JsonOpt jsonOpt = JsonOpt.newWithOpt();
			for (Object serverData : serverDataList) {
				SynData.Builder synData = transferToClientData(serverData,jsonOpt);
				msgDataSyn.addSynData(synData);
			}			
			
			jsonOpt.setOptMapStr(msgDataSyn);
			
			msgDataSyn.setSynOpType(synOpType);
			msgDataSyn.setSynType(synType);
			msgDataSyn.setVersion(newVersion);

			if (serverDataList.isEmpty()) {
				// 列表为空的时候list 的 hashcode是一样的，这个时候要传同步对象本身。
				sendMsg(player, msgDataSyn, synType, msgDataSyn);
			} else {

				sendMsg(player, serverDataList, synType, msgDataSyn);
			}

		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), player.getUserId(), "ClientDataSynMgr[synDataList] synType:" + synType + " synOpType:" + synOpType, e);
		}
	}
	/**
	 * 同步某些数据列表到客户端
	 * 
	 * @param player
	 * @param groupId  分组id
	 * @param serverDataList 要同步到客户端的数据
	 * @param synType <b><i>同步数据模块类型</i></b> {@link eSynType} 例如，同步背包数据----->类型就是{@link eSynType#USER_ITEM_BAG}
	 * @param synOpType <b><i>数据类型</i></b> {@link eSynOpType} 例如，同步的是背包所有数据----->类型就是{@link eSynOpType#UPDATE_LIST}
	 * @param newVersion 推送到客户端的版本号
	 */
	public static void synDataGroupList(Player player, String groupId, List<?> serverDataList, eSynType synType, eSynOpType synOpType, int newVersion) {
		try {
			
			player.getDataSynVersionHolder().addVersion(synType);
			
			JsonOpt jsonOpt = JsonOpt.newWithOpt();
			MsgDataSyn.Builder msgDataSyn = MsgDataSyn.newBuilder();
			for (Object serverData : serverDataList) {
				SynData.Builder synData = transferToClientData(serverData,jsonOpt);
				msgDataSyn.addSynData(synData);
			}
			jsonOpt.setOptMapStr(msgDataSyn);
			
			msgDataSyn.setSynOpType(synOpType);
			msgDataSyn.setSynType(synType);
			msgDataSyn.setVersion(newVersion);
			msgDataSyn.setGroupId(groupId);
			if (serverDataList.isEmpty()) {
				// 列表为空的时候list 的 hashcode是一样的，这个时候要传同步对象本身。
				sendMsg(player, msgDataSyn, synType, msgDataSyn);
			} else {
				
				sendMsg(player, serverDataList, synType, msgDataSyn);
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), player.getUserId(), "ClientDataSynMgr[synDataGroupList] synType:" + synType + " synOpType:" + synOpType, e);
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
			
			JsonOpt jsonOpt = JsonOpt.newWithOpt();
			MsgDataSyn.Builder msgDataSyn = MsgDataSyn.newBuilder();
			SynData.Builder synData = transferToClientData(serverData, jsonOpt);
			jsonOpt.setOptMapStr(msgDataSyn);
			
			msgDataSyn.addSynData(synData);
			msgDataSyn.setSynOpType(synOpType);
			msgDataSyn.setSynType(synType);
			msgDataSyn.setVersion(newVersion);
			sendMsg(player, serverData, synType, msgDataSyn);
		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), player.getUserId(), "ClientDataSynMgr[synData] synType:" + synType + " synOpType:" + synOpType, e);
		}
	}
	
	public static void synDataFiled(Player player, Object serverData, eSynType synType, eSynOpType synOpType, List<String> fieldNameList) {
		try {
			int newVersion = player.getDataSynVersionHolder().addVersion(synType);
			JsonOpt jsonOpt = JsonOpt.newWithOpt();
			SynData.Builder synData = transferToClientData(serverData, fieldNameList,jsonOpt);

			MsgDataSyn.Builder msgDataSyn = MsgDataSyn.newBuilder();
			jsonOpt.setOptMapStr(msgDataSyn);
			
			msgDataSyn.addSynData(synData);
			msgDataSyn.setSynOpType(synOpType);
			msgDataSyn.setSynType(synType);
			msgDataSyn.setVersion(newVersion);
			sendMsg(player, serverData, synType, msgDataSyn);
		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), player.getUserId(), "ClientDataSynMgr[synData] synType:" + synType + " synOpType:" + synOpType, e);
		}
	}

	public static void synDataFiled(Player player, Object serverData, eSynType synType, List<String> fieldNameList) {
		eSynOpType synOpType = eSynOpType.UPDATE_FIELD;
		try {
		
			int newVersion = player.getDataSynVersionHolder().addVersion(synType);
			JsonOpt jsonOpt = JsonOpt.newWithOpt();
			SynData.Builder synData = transferToClientData(serverData, fieldNameList, jsonOpt);

			MsgDataSyn.Builder msgDataSyn = MsgDataSyn.newBuilder();
			jsonOpt.setOptMapStr(msgDataSyn);
			
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
			GameLog.error(LogModule.Util.getName(), player.getUserId(), "ClientDataSynMgr[updateData] synType:" + synType + " synOpType:" + synOpType, e);
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
			GameLog.error(LogModule.Util.getName(), player.getUserId(), "ClientDataSynMgr[updateDataList] synType:" + synType + " synOpType:" + synOpType, e);
		}
	}

	public static String toClientData(Object serverData) {
		ClassInfo4Client serverClassInfo = DataSynClassInfoMgr.getByClass(serverData.getClass());

		String jsonData = null;
		try {
			JsonOpt noOpt = JsonOpt.newWithoutOpt();
			jsonData = serverClassInfo.toJson(serverData,noOpt);
		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), serverData.getClass().toString(), "ClientDataSynMgr[toClientData]", e);
		}
		return jsonData;
	}

	public static SynData.Builder transferToClientData(Object serverData,JsonOpt jsonOpt) throws Exception {
		return transferToClientData(serverData, null,jsonOpt);
	}

	private static SynData.Builder transferToClientData(Object serverData, List<String> synFieldList, JsonOpt jsonOpt) throws Exception {
		ClassInfo4Client serverClassInfo = DataSynClassInfoMgr.getByClass(serverData.getClass());

		String jsonData = null;
		if (synFieldList != null) {
			jsonData = serverClassInfo.toJson(serverData, synFieldList,jsonOpt);
		} else {
			jsonData = serverClassInfo.toJson(serverData,jsonOpt);
		}

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
		SynDataInReqMgr synDataInReqMgr = UserChannelMgr.getSynDataInReqMgr(player.getUserId());
		if(synDataInReqMgr!=null && !synDataInReqMgr.addSynData(serverData, synType, msgDataSyn)){
			Builder msgDataSynList = MsgDataSynList.newBuilder().addMsgDataSyn(msgDataSyn);
			player.SendMsg(Command.MSG_DATA_SYN, msgDataSynList.build().toByteString());
		}
	}
	
	/**
	 * 给多用户同步同一个数据
	 * @param players
	 * @param serverData
	 * @param synType
	 * @param msgDataSyn
	 */
	private static void sendMsgMutiple(List<Player> players, Object serverData, eSynType synType, MsgDataSyn.Builder msgDataSyn) {
		if(null == players) return;
		for(Player player : players){
			msgDataSyn.setVersion(player.getDataSynVersionHolder().getVersion(synType));
			sendMsg(player, serverData, synType, msgDataSyn);
		}
	}
	
	/**
	 * 给多用户同步同一个数据
	 * 
	 * @param players 给多用户同步同一个数据
	 * @param serverData 要同步的数据
	 * @param synType <b><i>同步数据模块类型</i></b> {@link eSynType} 例如，同步背包模块的道具数据----->类型就是{@link eSynType#USER_ITEM_BAG}
	 * @param synOpType <b><i>数据类型</i></b> {@link eSynOpType} 例如同步了一个道具的数据，----->类型就是{@link eSynOpType#UPDATE_SINGLE}
	 */
	public static void synDataMutiple(List<Player> players, Object serverData, eSynType synType, eSynOpType synOpType) {
		try {
			MsgDataSyn.Builder msgDataSyn = MsgDataSyn.newBuilder();
			SynData.Builder synData = transferToClientData(serverData, JsonOpt.newWithOpt());
			msgDataSyn.addSynData(synData);
			msgDataSyn.setSynOpType(synOpType);
			msgDataSyn.setSynType(synType);
			sendMsgMutiple(players, serverData, synType, msgDataSyn);
		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), "synDataMutiple", "ClientDataSynMgr[synData] synType:" + synType + " synOpType:" + synOpType, e);
		}
	}
	
	/**
	 * 将客户端的json字符串转为服务器对象
	 * @param clazz
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public static Object fromClientJson2Data(Class<?> clazz, String json) throws Exception {
		ClassInfo4Client serverClassInfo = DataSynClassInfoMgr.getByClass(clazz);
		return serverClassInfo.fromJson(json);
	}
}