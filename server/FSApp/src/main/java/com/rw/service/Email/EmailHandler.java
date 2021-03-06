package com.rw.service.Email;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.alibaba.druid.util.StringUtils;
import com.google.protobuf.ByteString;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.bm.GFightHelper;
import com.rw.dataaccess.processor.EmailCreator;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BilogItemInfo;
import com.rw.service.log.template.EmailLogTemplate;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailItem;
import com.rwbase.dao.email.TableEmail;
import com.rwproto.EmailProtos.EmailInfo;
import com.rwproto.EmailProtos.EmailRequest;
import com.rwproto.EmailProtos.EmailRequestType;
import com.rwproto.EmailProtos.EmailResponse;
import com.rwproto.EmailProtos.EmailResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.TaskProtos.OneKeyResultType;

public class EmailHandler {

	private static EmailHandler instance = new EmailHandler();

	protected EmailHandler() {
	}

	public static EmailHandler getInstance() {
		return instance;
	}

	/** 获取邮件列表 */
	public ByteString getEmailList(Player player, EmailRequest request) {
		EmailResponse.Builder response = EmailResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setResultType(EmailResultType.SUCCESS);
		response.addAllEmailList(EmailItemByEmailInfoList(player.getEmailMgr().getAllEmail()));
		return response.build().toByteString();
	}

	/** 查看邮件 */
	public ByteString checkEmail(Player player, EmailRequest request) {
		EmailResponse.Builder response = EmailResponse.newBuilder();
		response.setRequestType(request.getRequestType());

		if (player.getEmailMgr().checkEmail(request.getEmailId())) {
			response.setResultType(EmailResultType.SUCCESS);
		} else {
			response.setResultType(EmailResultType.FAIL);
		}

		return response.build().toByteString();
	}

	/** 领取附件 */
	public ByteString getAttachment(Player player, EmailRequest request) {
		EmailResponse.Builder response = EmailResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		EmailItem item = player.getEmailMgr().getEmailItem(request.getEmailId());

		// 判断是否可以领取
		String result = isAttachmentCanTake(player, item);
		if (result.equals("")) {

			response.setResultType(EmailResultType.SUCCESS);
			ItemBagMgr.getInstance().addItemByPrizeStr(player, item.getEmailAttachment());
			item.setReceive(true);
			item.setChecked(true);
			player.getEmailMgr().checkUnread();
			if (item.getDeleteType() == EEmailDeleteType.GET_DELETE.getValue()) {
				player.getEmailMgr().delEmail(item.getEmailId(), false);
				pushEmailList(player);
			}
			BILogMgr.getInstance().logEmail(player.getUserId(), item, EmailLogTemplate.EamilOpType.EMAIL_OPEN);
		} else {
			pushEmailList(player);
			response.setResultType(EmailResultType.FAIL);
			response.setFailResult(result);
		}

		// 新手大礼包的活动日志
		if (StringUtils.equals(item.getCfgid(), EmailCreator.email)) {
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.CREATROLE_REWARDS_EMAIL, 0, 0);
			List<BilogItemInfo> rewardslist = BilogItemInfo.fromStr(item.getEmailAttachment());
			String rewardInfoActivity = BILogTemplateHelper.getString(rewardslist);
			BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.CREATROLE_REWARDS_EMAIL, 0, true, 0, rewardInfoActivity, 0);
		}
		player.getEmailMgr().save();
		return response.build().toByteString();
	}

	/** 领取所有的附件 */
	public OneKeyResultType getAllAttachment(Player player, HashMap<Integer, Integer> rewardMap) {
		TableEmail tableEmail = player.getEmailMgr().getTableEmail();
		if (null == tableEmail) {
			return OneKeyResultType.DATA_ERROR;
		}
		List<EmailItem> emailList = tableEmail.getEmailArrayList();
		if (null == emailList || emailList.isEmpty()) {
			return OneKeyResultType.NO_REWARD;
		}
		boolean isPushEmail = false;
		for (EmailItem item : emailList) {
			// 判断是否可以领取
			String result = isAttachmentCanTake(player, item);
			if (result.equals("")) {
				item.setReceive(true);
				item.setChecked(true);
				player.getEmailMgr().checkUnread();
				if (item.getDeleteType() == EEmailDeleteType.GET_DELETE.getValue()) {
					player.getEmailMgr().delEmail(item.getEmailId(), false);
				}
				isPushEmail = true;
				// 记录奖励
				List<ItemInfo> rewardList = GFightHelper.stringToItemList(item.getEmailAttachment(), "~");
				for (ItemInfo rewardItem : rewardList) {
					Integer haveCount = rewardMap.get(rewardItem.getItemID());
					if (null == haveCount)
						haveCount = rewardItem.getItemNum();
					else
						haveCount += rewardItem.getItemNum();
					rewardMap.put(rewardItem.getItemID(), haveCount);
				}
			}
			// 新手大礼包的活动日志
			if (StringUtils.equals(item.getCfgid(), EmailCreator.email)) {
				BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.CREATROLE_REWARDS_EMAIL, 0, 0);
				List<BilogItemInfo> rewardslist = BilogItemInfo.fromStr(item.getEmailAttachment());
				String rewardInfoActivity = BILogTemplateHelper.getString(rewardslist);
				BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.CREATROLE_REWARDS_EMAIL, 0, true, 0, rewardInfoActivity, 0);
			}
		}
		if (isPushEmail) {
			pushEmailList(player);
			player.getEmailMgr().save();
			return OneKeyResultType.OneKey_SUCCESS;
		} else {
			return OneKeyResultType.NO_REWARD;
		}
	}

	private String isAttachmentCanTake(Player player, EmailItem item) {

		long current = System.currentTimeMillis();
		boolean isInCoolTime = current < item.getCoolTime();
		boolean isBeforBeginTime = current < item.getBeginTime();
		boolean isAfterEndTime = item.getEndTime() > 0 && current > item.getEndTime();
		String result = "";
		if (item.isReceive()) {
			result = "附件已领取。";
		} else if (isInCoolTime) {
			String strCoolTime = DateUtils.getDateTimeFormatString(item.getCoolTime(), "yyyy-MM-dd HH:mm:ss");
			result = "冻结时间为" + strCoolTime + ",结束后可领取附件奖励。";
		} else if (isBeforBeginTime) {
			result = "附件尚未到可领取时间,请耐心等待。";
		} else if (isAfterEndTime) {
			result = "附件领取时间已过。";
		} else if (StringUtils.isEmpty(item.getEmailAttachment())) {
			result = "没有附件";
		}
		return result;
	}

	/** 推送邮件列表 */
	public void pushEmailList(Player player) {
		EmailResponse.Builder response = EmailResponse.newBuilder();
		response.setRequestType(EmailRequestType.Email_List);
		response.setResultType(EmailResultType.SUCCESS);
		response.addAllEmailList(EmailItemByEmailInfoList(player.getEmailMgr().getAllEmail()));
		player.SendMsg(Command.MSG_EMAIL, response.build().toByteString());
	}

	public ByteString responseEmailMsg(EmailRequestType requestType, EmailResultType result) {
		EmailResponse.Builder response = EmailResponse.newBuilder();
		response.setRequestType(requestType);
		response.setResultType(result);
		return response.build().toByteString();
	}

	private List<EmailInfo> EmailItemByEmailInfoList(List<EmailItem> list) {
		List<EmailInfo> resultList = new ArrayList<EmailInfo>();
		for (EmailItem item : list) {
			resultList.add(EmailItemByEmailInfo(item));
		}
		return resultList;
	}

	private EmailInfo EmailItemByEmailInfo(EmailItem item) {
		EmailInfo.Builder info = EmailInfo.newBuilder();
		info.setEmailId(item.getEmailId());
		info.setEmailAttachment(item.getEmailAttachment());
		info.setIsReceive(item.isReceive());
		info.setIsChecked(item.isChecked());
		info.setSendTime(item.getSendTime());
		info.setTitle(item.getTitle());
		info.setContent(item.getContent());
		info.setSender(item.getSender());
		info.setCheckIcon(item.getCheckIcon());
		info.setSubjectIcon(item.getSubjectIcon());

		if (item.getDeleteType() != EEmailDeleteType.GET_DELETE.getValue()) {
			long time = item.getDeadlineTimeInMill() - Calendar.getInstance().getTimeInMillis();
			int day = (int) (time / 24 / 60 / 60 / 1000);
			int hour = (int) (time / 60 / 60 / 1000);
			String timeStr = "";
			if (day > 0) {
				timeStr = day + "天";
			} else {
				if (hour == 0) {
					hour = 1;
				}
				timeStr = hour + "小时";
			}
			info.setExpirationTime(timeStr + "后删除");
		} else {
			info.setExpirationTime("领取后删除");
		}
		return info.build();
	}
}
