package com.rw.controler;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.fsutil.util.SpringContextUtil;

/**
 * <pre>
 * 执行用户回写记录
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class UserCreationWriteBack implements Runnable {

	private DruidDataSource dataSource;
	private JdbcTemplate template;
	private boolean running;
	private ConcurrentLinkedQueue<Object[]> queue = new ConcurrentLinkedQueue<Object[]>();
	private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new SimpleThreadFactory("user_write_back"));
	private int batchSize = 20;
	private final String sql;

	public UserCreationWriteBack() {
		this.dataSource = SpringContextUtil.getBean("dataSourcePF");
		this.template = new JdbcTemplate(dataSource);
		this.sql = "insert into mt_user_mapping (user_id,account_id,open_account,zone_id) values(?,?,?,?)";
	}

	public void addWriteBackTask(String userId, String accountId, String openAccount, int zoneId) {
		queue.offer(new Object[] { userId, accountId, openAccount, zoneId });
		synchronized (this) {
			if (this.running) {
				return;
			} else {
				running = true;
			}
		}
		executor.schedule(this, 200, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		for (;;) {
			Object[] param;
			synchronized (this) {
				param = this.queue.poll();
				if (param == null) {
					this.running = false;
					break;
				}
			}
			list.add(param);
			if (list.size() > batchSize) {
				update(list);
				list.clear();
			}
		}
		int size = list.size();
		if (size <= 0) {
			return;
		}
		update(list);
	}

	private void update(ArrayList<Object[]> list) {
		try {
			int size = list.size();
			if (size == 1) {
				template.update(sql, list.get(0));
			} else {
				template.batchUpdate(sql, list);
			}
		} catch (Throwable t) {
			FSUtilLogger.error("回写登录服数据库异常:" + list, t);
			t.printStackTrace();
		}
	}

}
