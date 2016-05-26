package com.rw.service.Email;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailCfg;
import com.rwbase.dao.email.EmailCfgDAO;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.email.EmailItem;
import com.rwbase.dao.email.TableEmail;
import com.rwbase.dao.email.TableEmailDAO;

public class EmailUtils {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

	/**
	 * 发送邮件
	 * 
	 * @param userId
	 *            收件人
	 * @param cfgId
	 *            配置表ＩＤ
	 * */
	public static boolean sendEmail(String userId, String cfgId) {
		return sendEmail(userId, cfgId, "");
	}

	/**
	 * 发送邮件
	 * 
	 * @param userId
	 *            收件人
	 * @param cfgId
	 *            配置表ＩＤ
	 * @param args
	 *            替换邮件内容
	 * */
	public static boolean sendEmail(String userId, String cfgId, List<String> args) {
		return sendEmail(userId, cfgId, "", args);
	}

	
	
	/**
	 * 发送邮件
	 * 
	 * @param userId
	 *            收件人
	 * @param cfgId
	 *            配置表ＩＤ
	 * @param itemMap Map<物品id,物品数量>
	 *            物品附件
	 * */
	public static boolean sendEmail(String userId, String cfgId, Map<Integer,Integer> itemMap) {
		StringBuilder attachBuilder = new StringBuilder();
		Set<Entry<Integer, Integer>> entrySet = itemMap.entrySet();
		for (Entry<Integer, Integer> entryTmp : entrySet) {
			attachBuilder.append(entryTmp.getKey()).append("~").append(entryTmp.getValue()).append(",");
		}
		
		String attachment = StringUtils.removeEnd(attachBuilder.toString(), ",");
		
		return sendEmail(userId, cfgId, attachment);
	}
	/**
	 * 发送邮件
	 * 
	 * @param userId
	 *            收件人
	 * @param cfgId
	 *            配置表ＩＤ
	 * @param attachment
	 *            附件
	 * */
	public static boolean sendEmail(String userId, String cfgId, String attachment) {
		return sendEmail(userId, cfgId, attachment, new ArrayList<String>());
	}

	/**
	 * 发送邮件
	 * 
	 * @param userId
	 *            收件人
	 * @param cfgId
	 *            配置表ＩＤ
	 * @param attachment
	 *            附件
	 * @param args
	 *            替换邮件内容
	 * */
	public static boolean sendEmail(String userId, String cfgId, String attachment, List<String> args) {
		EmailData emailData = createEmailData(cfgId, attachment, args);
		return sendEmail(userId, emailData);
	}

	public static boolean sendEmail(String userId, String cfgId, String attachment, long sendTime) {
		EmailData data = createEmailData(cfgId, attachment,new ArrayList<String>());
		return sendEmail(userId, data , sendTime);
	}

	/**
	 * 通过邮件配置ID创建一个EMailData对象
	 * @param cfgId
	 * @param attachment
	 * @return
	 */
	public static EmailData createEmailData(String cfgId, String attachment,List<String> args) {
		EmailData emailData = new EmailData();
		EmailCfg cfg = EmailCfgDAO.getInstance().getEmailCfg(cfgId);
		if (!StringUtils.isEmpty(attachment)) {
			emailData.setEmailAttachment(attachment);
		} else {
			emailData.setEmailAttachment(cfg.getAttachment());
		}
		emailData.setTitle(cfg.getTitle());
		emailData.setContent(cfg.getContent());
		emailData.setSender(cfg.getSender());
		emailData.setCheckIcon(cfg.getCheckIcon());
		emailData.setSubjectIcon(cfg.getSubjectIcon());
		emailData.setDeleteType(EEmailDeleteType.valueOf(cfg.getDeleteType()));
		emailData.setDelayTime(cfg.getDelayTime());
		emailData.setDeadlineTime(cfg.getDeadlineTime());
		emailData.replaceContent(args);
		return emailData;
	}

	/**
	 * 发送邮件
	 * 
	 * @param userId
	 *            收件人
	 * @param emailData
	 *            邮件数据
	 * */
	public static boolean sendEmail(String userId, EmailData emailData, long sendTime) {
		TableEmail otherTable = getOtherTableFriend(userId);
		if (otherTable == null) {
			return false;
		}
		setEamil(otherTable, emailData, sendTime);
		TableEmailDAO.getInstance().update(otherTable);
		return true;
	}

	public static void setEamil(TableEmail otherTable, EmailData emailData, long sendTime) {
		EmailItem item = new EmailItem();
		item.setEmailId(UUID.randomUUID().toString());
		item.setTaskId(emailData.getTaskId());
		item.setEmailAttachment(emailData.getEmailAttachment());
		item.setChecked(false);
		item.setTitle(emailData.getTitle());
		item.setContent(emailData.getContent());
		item.setSender(emailData.getSender());
		item.setCheckIcon(emailData.getCheckIcon());
		item.setSubjectIcon(emailData.getSubjectIcon());
		item.setDeleteType(emailData.getDeleteType().getValue());
		item.setSendTime(sendTime);
		item.setCoolTime(emailData.getCoolTime());
		item.setBeginTime(emailData.getBeginTime());
		item.setEndTime(emailData.getEndTime());
		if (emailData.isDeadline()) {
			try {
				item.setDeadlineTimeInMill(format.parse(emailData.getDeadlineTime()).getTime());
			} catch (ParseException e) {
				GameLog.error("邮件删除日期填写错误，邮件发送失败");
			}
		} else {
			item.setDeadlineTimeInMill(System.currentTimeMillis() + emailData.getDelayTime() * 1000L);
		}
		otherTable.addEmail(item);
	}

	/**
	 * 发送邮件
	 * 
	 * @param userId
	 *            收件人
	 * @param emailData
	 *            邮件数据
	 * */
	public static boolean sendEmail(String userId, EmailData emailData) {
		return sendEmail(userId, emailData, Calendar.getInstance().getTimeInMillis());
	}

	/**
	 * 删除gm邮件
	 * 
	 * @param userId
	 * @param taskId
	 * @return
	 */
	public static boolean deleteEmail(String userId, long taskId) {
		TableEmail otherTable = getOtherTableFriend(userId);
		Map<String, EmailItem> emailMap = otherTable.getEmailList();
		for (Iterator<Entry<String, EmailItem>> iterator = emailMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, EmailItem> entry = iterator.next();
			EmailItem item = entry.getValue();
			if (item.getTaskId() == taskId) {
				iterator.remove();
			}
		}
		TableEmailDAO.getInstance().update(otherTable);
		return true;
	}

	private static TableEmail getOtherTableFriend(String userId) {
		return TableEmailDAO.getInstance().get(userId);
	}
}
