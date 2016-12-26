package com.rw.service.setting;

import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.rw.controler.PlayerCreateHandler;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.dirtyword.CharFilterFactory;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwproto.PlayerSettingProtos.MsgSettingRequest;
import com.rwproto.PlayerSettingProtos.MsgSettingResponse;
import com.rwproto.PlayerSettingProtos.eSettingResultType;

public class SettingHandler {
	private static SettingHandler instance = new SettingHandler();

	protected SettingHandler() {
	}

	public static SettingHandler getInstance() {
		return instance;
	}

	/*
	 * 获取包括修改名字时间，拥有的头像和头像框信息
	 */
	public ByteString getSettingInfo(MsgSettingRequest settingRequest, Player player) {
		MsgSettingResponse.Builder msgResponse = MsgSettingResponse.newBuilder();
		msgResponse.setRequestType(settingRequest.getRequestType());

		player.getSettingMgr().checkOpen();
		int timeLeft = player.getSettingMgr().getChangeNameTimeLeft();
		List<String> headList = player.getSettingMgr().getHeadNameList();
		List<String> headBoxList = player.getSettingMgr().getHeadBoxNameList();

		msgResponse.setTimeLeft(timeLeft);
		msgResponse.addAllHeadNameList(headList);
		msgResponse.addAllHeadBoxNameList(headBoxList);
		msgResponse.setResultType(eSettingResultType.SUCCESS);
		player.getSettingMgr().syn();
		return msgResponse.build().toByteString();
	}

	/*
	 * 重命名
	 */
	public ByteString rename(MsgSettingRequest settingRequest, Player player) {
		MsgSettingResponse.Builder msgResponse = MsgSettingResponse.newBuilder();
		msgResponse.setRequestType(settingRequest.getRequestType());
		String name = settingRequest.getContent();
		if (name.isEmpty() || name == null) {
			msgResponse.setResultType(eSettingResultType.FAIL);
			return msgResponse.build().toByteString();
		} else if (player.getUserGameDataMgr().getGold() < PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.CHANGE_NAME_CONST)) {
			// player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "钻石不足");
			msgResponse.setResultType(eSettingResultType.FAIL);
			msgResponse.setInfo("钻石不足");
			return msgResponse.build().toByteString();
		} else if (!PlayerCreateHandler.getInstance().checkNameLength(name) || CharFilterFactory.getCharFilter().checkWords(name, true, true, true, true)) {
			// player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "昵称不能包含非法字符");
			msgResponse.setInfo("昵称不能包含非法字符");
			msgResponse.setResultType(eSettingResultType.FAIL);
			return msgResponse.build().toByteString();
		}

		long time1 = System.currentTimeMillis() - player.getSettingMgr().getLastChangeNameTime();
		long cdTime = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.CHAGNE_NAME_CDTIME) * 1000;
		if (time1 < cdTime) {
			long leftTime = cdTime - time1;
			msgResponse.setResultType(eSettingResultType.FAIL);
			String timeToCountDown = DateUtils.timeToCountDown(leftTime);
			msgResponse.setInfo("还需要" + timeToCountDown + "才能改名");
			return msgResponse.build().toByteString();

		}

		boolean result = player.SetUserName(name);
		if (!result) {
			msgResponse.setResultType(eSettingResultType.FAIL);
			msgResponse.setInfo("该昵称已存在");
			return msgResponse.build().toByteString();
		}
		player.getSettingMgr().setLastChangeName();
		ItemBagMgr.getInstance().addItem(player, eSpecialItemId.Gold.getValue(), -100);
		msgResponse.setInfo(name);
		msgResponse.setResultType(eSettingResultType.SUCCESS);
		return msgResponse.build().toByteString();
	}

	/*
	 * 使用兑换码，暂未开放
	 */
	public ByteString exchangeCode(MsgSettingRequest settingRequest, Player player) {
		MsgSettingResponse.Builder msgResponse = MsgSettingResponse.newBuilder();
		msgResponse.setRequestType(settingRequest.getRequestType());
		return null;
	}

	/*
	 * 更改头像
	 */
	public ByteString changeHead(MsgSettingRequest settingRequest, Player player) {
		MsgSettingResponse.Builder msgResponse = MsgSettingResponse.newBuilder();
		msgResponse.setRequestType(settingRequest.getRequestType());
		String headName = settingRequest.getContent();
		if (!player.getSettingMgr().checkIfHeadDataHas(headName)) {
			msgResponse.setResultType(eSettingResultType.FAIL);
			return msgResponse.build().toByteString();
		}
		player.SetHeadId(headName);
		msgResponse.setInfo(headName);
		msgResponse.setResultType(eSettingResultType.SUCCESS);
		return msgResponse.build().toByteString();
	}

	/*
	 * 更改头像框
	 */
	public ByteString changeHeadBox(MsgSettingRequest settingRequest, Player player) {
		MsgSettingResponse.Builder msgResponse = MsgSettingResponse.newBuilder();
		msgResponse.setRequestType(settingRequest.getRequestType());
		String headBoxName = settingRequest.getContent();
		if (!player.getSettingMgr().checkIfHeadBoxDataHas(headBoxName)) {
			msgResponse.setResultType(eSettingResultType.FAIL);
			return msgResponse.build().toByteString();
		}
		player.getUserGameDataMgr().setHeadBox(headBoxName);
		msgResponse.setInfo(headBoxName);
		msgResponse.setResultType(eSettingResultType.SUCCESS);
		return msgResponse.build().toByteString();
	}
}
