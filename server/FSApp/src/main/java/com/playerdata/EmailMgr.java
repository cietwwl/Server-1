package com.playerdata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.alibaba.druid.util.StringUtils;
import com.playerdata.common.PlayerEventListener;
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
		TableEmail email = new TableEmail();
		email.setUserId(player.getUserId());
		m_emailDAO.update(email);
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
			delEmail(item.getEmailId());
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
		return getTableEmail().getEmailList().size() > 0;
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
			if (!data.isChecked() && StringUtils.isEmpty(data.getEmailAttachment())) {
				data.setChecked(true);
			}
			checkUnread();
			return true;
		} else {
			return false;
		}
	}

	public void delEmail(String emailId) {
		getTableEmail().getEmailList().remove(emailId);
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
