package com.rw.service.giftcode;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.StringUtils;

import com.gm.giftCenter.GiftCodeItem;
import com.gm.giftCenter.GiftCodeResponse;
import com.gm.giftCenter.GiftCodeSenderBm;
import com.gm.giftCenter.GiftItem;
import com.gm.gmsender.GmCallBack;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.UserDataMgr;
import com.rw.service.Email.EmailUtils;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.giftcode.GiftCodeData;
import com.rwbase.dao.giftcode.dao.GiftCodeDataDAO;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwproto.GiftCodeProto.RequestType;
import com.rwproto.GiftCodeProto.ResultType;
import com.rwproto.GiftCodeProto.UseGiftCodeRspMsg;
import com.rwproto.MsgDef.Command;

/*
 * @author HC
 * @date 2016年3月18日 上午10:35:24
 * @Description 
 */
public class GiftCodeHandler {
	enum CODE_STATE {
		// 1:可使用,2:已使用,3：激活码不存在,4:玩家已领取过该礼包，5：已过期
		CODE_SUCCESS(1, "兑换成功"), CODE_USED(2, "兑换码已使用"), CODE_NOT_EXIST(3, "激活码不存在"), CODE_USED_BY_PLAYER(4, "您已经领取过该礼包"), CODE_TIME_OUT(5, "兑换码已过期");

		public final int type;// 兑换码类型
		public final String tip;// 提示信息

		private CODE_STATE(int type, String tip) {
			this.type = type;
			this.tip = tip;
		}
	};

	private static GiftCodeHandler handler = new GiftCodeHandler();

	public static GiftCodeHandler getHandler() {
		return handler;
	}

	private static final Boolean PRESENT = Boolean.TRUE;
	
	private final ConcurrentHashMap<String, Boolean> processingUserIds = new ConcurrentHashMap<String, Boolean>();
	
	/**
	 * 使用兑换码处理类
	 * 
	 * @param player
	 * @param code 兑换码
	 * @return
	 */
	public ByteString useGiftCodeHandler(final Player player, final String code) {
		final String userId = player.getUserId();
		UseGiftCodeRspMsg.Builder rsp = UseGiftCodeRspMsg.newBuilder();
		rsp.setReqType(RequestType.USE_CODE);
		if (processingUserIds.containsKey(userId)) {
			rsp.setResultType(ResultType.FAIL);
			rsp.setTipMsg("你的请求正在处理中，请稍后！");
			return rsp.build().toByteString();
		}
		if (StringUtils.isEmpty(code)) {
			rsp.setResultType(ResultType.FAIL);
			rsp.setTipMsg("兑换码不能为空");
			return rsp.build().toByteString();
		}

		final String account = player.getUserDataMgr().getAccount();
		User user = UserDataDao.getInstance().getByUserId(userId);
		if (user == null) {
			rsp.setResultType(ResultType.FAIL);
			rsp.setTipMsg("用户数据不存在");
			return rsp.build().toByteString();
		}

		GmCallBack<GiftCodeResponse> callBack = new GmCallBack<GiftCodeResponse>() {

			@Override
			public void doCallBack(GiftCodeResponse gmResponse) {
				try {
					UseGiftCodeRspMsg.Builder rsp = UseGiftCodeRspMsg.newBuilder();
					rsp.setReqType(RequestType.HAS_RESULT);
					if (gmResponse == null) {
						rsp.setResultType(ResultType.FAIL);
						rsp.setTipMsg("兑换失败");
						player.SendMsg(Command.MSG_GIFT_CODE, rsp.build().toByteString());
						return;
					}

					int gift_id = gmResponse.getGift_id();
					int type = gmResponse.getType();

					String activeCode = code;
					String giftPackageId = String.valueOf(gift_id);
					String giftPackageType = String.valueOf(type);
					String optype = "";
					String factionId = gmResponse.getGuildId();
					StringBuilder giftReward = new StringBuilder();

					if (type == CODE_STATE.CODE_SUCCESS.type) {// 兑换成功
						// 邮件内容
						final EmailData emailData = new EmailData();
						emailData.setTitle(gmResponse.getTitle());
						String content = gmResponse.getContent();
						emailData.setContent(StringUtils.isEmpty(content) ? "" : content);
						emailData.setDeleteType(EEmailDeleteType.GET_DELETE);

						List<GiftItem> itemData = gmResponse.getItemData();
						if (itemData != null) {
							StringBuilder sb = new StringBuilder();
							for (int i = 0, size = itemData.size(); i < size; i++) {
								GiftItem item = itemData.get(i);
								sb.append(item.getItemCode()).append("~").append(item.getCount());
								giftReward.append(item.getItemCode()).append("@").append(item.getCount());
								if (i < size - 1) {
									sb.append(",");
									giftReward.append("&");
								}
							}
							emailData.setEmailAttachment(sb.toString());
						}

						EmailUtils.sendEmail(userId, emailData);

						rsp.setResultType(ResultType.SUCCESS);
						rsp.setTipMsg(CODE_STATE.CODE_SUCCESS.tip);

						// 记录到数据库
						GiftCodeData codeData = new GiftCodeData();
						codeData.setCode(code);
						codeData.setGiftId(gift_id);
						codeData.setUserId(userId);
						codeData.setUseTime(System.currentTimeMillis());
						GiftCodeDataDAO.getDAO().addGiftCodeData(codeData);

						optype = "package_win";
					} else if (type == CODE_STATE.CODE_USED.type) {// 已经被使用
						rsp.setResultType(ResultType.FAIL);
						rsp.setTipMsg(CODE_STATE.CODE_USED.tip);
					} else if (type == CODE_STATE.CODE_NOT_EXIST.type) {// 兑换码不存在
						rsp.setResultType(ResultType.FAIL);
						rsp.setTipMsg(CODE_STATE.CODE_NOT_EXIST.tip);
					} else if (type == CODE_STATE.CODE_USED_BY_PLAYER.type) {// 已经领取了这个激活码
						rsp.setResultType(ResultType.FAIL);
						rsp.setTipMsg(CODE_STATE.CODE_USED_BY_PLAYER.tip);
					} else if (type == CODE_STATE.CODE_TIME_OUT.type) {// 兑换码过期
						rsp.setResultType(ResultType.FAIL);
						rsp.setTipMsg(CODE_STATE.CODE_TIME_OUT.tip);
					} else {
						rsp.setResultType(ResultType.FAIL);
						rsp.setTipMsg("兑换失败");
					}

					player.SendMsg(Command.MSG_GIFT_CODE, rsp.build().toByteString());

					// 记日志
					if (!optype.equals("package_win")) {
						optype = "package_fail";
					}
					BILogMgr.getInstance().logGiftPackage(player, activeCode, giftPackageId, giftPackageType, optype, factionId, giftReward.toString());
				} finally {
					processingUserIds.remove(player.getUserId());
				}
			}
		};

		
		UserDataMgr userDataMgr = player.getUserDataMgr();
		ZoneRegInfo zoneRegInfo = userDataMgr.getZoneRegInfo();
		int channelId = Integer.parseInt(zoneRegInfo.getRegChannelId());
		

		GiftCodeItem codeItem = new GiftCodeItem(code, userId, account, channelId, callBack);
		if (GiftCodeSenderBm.getInstance().add(codeItem)) {
			processingUserIds.put(userId, PRESENT);
		}

		if (!rsp.hasResultType()) {
			rsp.setResultType(ResultType.FAIL);
			rsp.setTipMsg("兑换码已提交，请稍候！");
		}
		return rsp.build().toByteString();
	}
}