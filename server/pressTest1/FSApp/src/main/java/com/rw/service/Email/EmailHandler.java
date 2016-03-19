package com.rw.service.Email;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailItem;
import com.rwproto.EmailProtos.EmailInfo;
import com.rwproto.EmailProtos.EmailRequest;
import com.rwproto.EmailProtos.EmailRequestType;
import com.rwproto.EmailProtos.EmailResponse;
import com.rwproto.EmailProtos.EmailResultType;
import com.rwproto.MsgDef.Command;

public class EmailHandler {	
	
	private static EmailHandler instance;
	
	private EmailHandler(){}
	
	public static EmailHandler getInstance(){
		if(instance == null){
			instance = new EmailHandler();
		}
		return instance;
	}
	
	/**获取邮件列表*/
	public ByteString getEmailList(Player player, EmailRequest request){
		EmailResponse.Builder response = EmailResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setResultType(EmailResultType.SUCCESS);
		response.addAllEmailList(EmailItemByEmailInfoList(player.getEmailMgr().getAllEmail()));
		return response.build().toByteString();
	}
	
	/**查看邮件*/
	public ByteString checkEmail(Player player, EmailRequest request){
		EmailResponse.Builder response = EmailResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		
		if(player.getEmailMgr().checkEmail(request.getEmailId())){
			response.setResultType(EmailResultType.SUCCESS);
		}else{
			response.setResultType(EmailResultType.FAIL);
		}		
		return response.build().toByteString();
	}

	/**领取附件*/
	public ByteString getAttachment(Player player, EmailRequest request){
		EmailResponse.Builder response = EmailResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		EmailItem item = player.getEmailMgr().getEmailItem(request.getEmailId());
		//判断是否可以领取
		if(isAttachmentCanTake(player, item)){
			
			response.setResultType(EmailResultType.SUCCESS);
			player.getItemBagMgr().addItemByPrizeStr(item.getEmailAttachment());
			item.setReceive(true);
			item.setChecked(true);
			player.getEmailMgr().checkUnread();
			if(item.getDeleteType() == EEmailDeleteType.GET_DELETE.getValue()){
				player.getEmailMgr().delEmail(item.getEmailId());
				pushEmailList(player);
			}
		}else{
			pushEmailList(player);
			response.setResultType(EmailResultType.FAIL);			
		}
		return response.build().toByteString();
	}
	
	private boolean isAttachmentCanTake(Player player, EmailItem item){
		
		boolean okToTake = true;
		long current = System.currentTimeMillis();
		boolean isInCoolTime = current < item.getCoolTime();
		boolean isBeforBeginTime = current < item.getBeginTime();
		boolean isAfterEndTime = item.getEndTime() > 0 && current > item.getEndTime();
		
		if(item.isReceive()){
			okToTake = false;
		}else if(isInCoolTime){
			player.NotifyCommonMsg("附件暂未能领取。请稍后。");
			okToTake = false;
		}else if(isBeforBeginTime){
			player.NotifyCommonMsg("附件尚未到可领取时间,请耐心等待。");
			okToTake = false;			
		}else if(isAfterEndTime){
			player.NotifyCommonMsg("附件领取时间已过。");
			okToTake = false;			
		}
		return okToTake;
	}
	
	/**推送邮件列表*/
	public void pushEmailList(Player player){
		EmailResponse.Builder response = EmailResponse.newBuilder();
		response.setRequestType(EmailRequestType.Email_List);
		response.setResultType(EmailResultType.SUCCESS);
		response.addAllEmailList(EmailItemByEmailInfoList(player.getEmailMgr().getAllEmail()));
		player.SendMsg(Command.MSG_EMAIL, response.build().toByteString());
	}
	
	public ByteString responseEmailMsg(EmailRequestType requestType, EmailResultType result){
		EmailResponse.Builder response = EmailResponse.newBuilder();
		response.setRequestType(requestType);
		response.setResultType(result);
		return response.build().toByteString();
	}
	
	private List<EmailInfo> EmailItemByEmailInfoList(List<EmailItem> list){
		List<EmailInfo> resultList = new ArrayList<EmailInfo>();
		for(EmailItem item : list){
			resultList.add(EmailItemByEmailInfo(item));
		}
		return resultList;
	}
	
	private EmailInfo EmailItemByEmailInfo(EmailItem item){
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
		
		if(item.getDeleteType() != EEmailDeleteType.GET_DELETE.getValue()){
			long time = item.getDeadlineTimeInMill() - Calendar.getInstance().getTimeInMillis();
			int day = (int)(time / 24 / 60/ 60 / 1000);
			int hour = (int)(time / 60 / 60 / 1000);
			String timeStr = "";
			if(day > 0){
				timeStr = day + "天";
			}else {
				if(hour == 0){
					hour = 1;
				}
				timeStr = hour + "小时";
			}
			info.setExpirationTime(timeStr + "后删除");
		}else{
			info.setExpirationTime("领取后删除");
		}
		return info.build();
	}
}
