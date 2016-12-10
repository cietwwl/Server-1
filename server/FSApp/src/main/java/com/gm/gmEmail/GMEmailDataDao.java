package com.gm.gmEmail;

import java.util.ArrayList;
import java.util.List;

import com.rw.fsutil.cacheDao.DataKVDao;

public class GMEmailDataDao extends DataKVDao<GMEmail> {
	private static GMEmailDataDao m_Instance = new GMEmailDataDao();

	public static GMEmailDataDao getInstance() {
		return m_Instance;
	}

	public GMEmail getGMEmail(String userId) {
		GMEmail gmEmail = get(userId);
		if (gmEmail == null) {
			gmEmail = new GMEmail();
			List<Long> list = new ArrayList<Long>();
			gmEmail.setTaskIdList(list);
			gmEmail.setUserId(userId);
			update(gmEmail);
		}
		return gmEmail;
	}

	public void updateGmEmailStatus(String userId, long taskId) {
		GMEmail mail = getGMEmail(userId);
		List<Long> taskIdList = mail.getTaskIdList();
		taskIdList.add(taskId);
		update(mail);
	}
}
