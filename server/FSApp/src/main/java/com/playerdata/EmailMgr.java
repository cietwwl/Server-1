package com.playerdata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.alibaba.druid.util.StringUtils;
import com.playerdata.common.PlayerEventListener;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.EmailLogTemplate;
import com.rwbase.dao.email.EmailItem;
import com.rwbase.dao.email.TableEmail;
import com.rwbase.dao.email.TableEmailDAO;

public class EmailMgr implements PlayerEventListener {

	private TableEmailDAO m_emailDAO = TableEmailDAO.getInstance();
	private String userId;

	public TableEmail getTableEmail() {
		return m_emailDAO.get(userId);
	}

	// 初始化
	public void init(Player player) {
		this.userId = player.getUserId();
	}

	@Override
	public void notifyPlayerCreated(Player player) {
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		this.checkRemoveOverdue();
	}

	/** 检查删除过期邮件 */
	public void checkRemoveOverdue() {
		TableEmail email = getTableEmail();
		List<EmailItem> removeList = new ArrayList<EmailItem>();
		Iterator<EmailItem> it = email.getEmailList().values().iterator();
		while (it.hasNext()) {
			EmailItem item = it.next();
			if (isExpire(item)) {
				removeList.add(item);
			}
		}
		for (EmailItem item : removeList) {
			delEmail(item.getEmailId(), true);
		}
	}

	public List<EmailItem> getAllEmail() {
		checkRemoveOverdue();
		TableEmail email = getTableEmail();
		List<EmailItem> returnList = new ArrayList<EmailItem>();
		Iterator<EmailItem> it = email.getEmailList().values().iterator();
		while (it.hasNext()) {
			EmailItem item = it.next();
			returnList.add(item);
		}
		return returnList;
	}

	public boolean hasEmail() {
		for(EmailItem item: getTableEmail().getEmailList().values()){
			if(!item.isChecked()){
				return true;
			}
		}
		return false;
	}
	

	public boolean containsEmailWithTaskId(long taskId) {
		boolean contains = false;
		Iterator<EmailItem> it = getTableEmail().getEmailList().values().iterator();
		while (it.hasNext()) {
			EmailItem item = it.next();
			if (taskId == item.getTaskId()) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public EmailItem getEmailItem(String id) {
		return getTableEmail().getEmailList().get(id);
	}

	public boolean checkEmail(String id) {
		EmailItem data = getTableEmail().getEmailList().get(id);
		if (data != null) {
			if (!data.isChecked() /*&& StringUtils.isEmpty(data.getEmailAttachment())*/) {
				data.setChecked(true);
				if(StringUtils.isEmpty(data.getEmailAttachment())){
					BILogMgr.getInstance().logEmail(userId, data, EmailLogTemplate.EamilOpType.EMAIL_OPEN);
				}
			}
			checkUnread();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param emailId
	 * @param blnAuto 到期邮件则为自动删除，系统删除均blnAuto = false
	 */
	public void delEmail(String emailId, boolean blnAuto) {
		EmailItem emailItem = getTableEmail().getEmailList().remove(emailId);
		BILogMgr.getInstance().logEmail(userId, emailItem, blnAuto ? EmailLogTemplate.EamilOpType.EMAIL_DELETE :  EmailLogTemplate.EamilOpType.EMAIL_AUTO_DELETE);
	}

	public boolean isExpire(EmailItem email) {
		return email.getDeadlineTimeInMill() < Calendar.getInstance().getTimeInMillis();
	}

	public boolean save() {
		m_emailDAO.update(userId);
		return true;
	}

	public void checkUnread() {
	}

}
