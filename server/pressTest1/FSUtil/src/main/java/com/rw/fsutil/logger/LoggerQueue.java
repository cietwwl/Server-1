package com.rw.fsutil.logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Statement;
import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
import com.rw.fsutil.util.SpringContextUtil;

public class LoggerQueue {

	private ConcurrentLinkedQueue<LoggerSender> queue;
	private static String ID = "id";
	private static String INFO = "info";
	private static long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);
	private final JdbcTemplate template;
	private final String sql;
	private final String deleteSql;
	private final String tableName;
	private final String host;
	private final int port;
	private final ScheduledThreadPoolExecutor timedPool;
	private final AtomicInteger taskCount = new AtomicInteger();
	private final int maxTryTimes;
	private final int recoverMaxCount = 3000;
	private final int receiveTimeoutMillis;
	private final AtomicReference<TryConnectRecorder> recorder;

	public LoggerQueue(String tableName, String dsName, String host, int port, int receiveTimeoutMillis) {
		this(tableName, host, port, receiveTimeoutMillis, (DruidDataSource) SpringContextUtil.getBean(dsName));
	}

	public LoggerQueue(String tableName, String host, int port, int receiveTimeoutMillis, DruidDataSource dataSource) {
		if (dataSource == null) {
			throw new ExceptionInInitializerError("获取DataSource失败：" + dataSource);
		}
		this.host = host;
		this.port = port;
		this.queue = new ConcurrentLinkedQueue<LoggerSender>();
		this.template = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		this.sql = "insert into " + tableName + "(info) values (?)";
		this.deleteSql = "delete from " + tableName + " where id = ?";
		this.timedPool = new ScheduledThreadPoolExecutor(16, new SimpleThreadFactory("logger pool"));
		this.tableName = tableName;
		this.maxTryTimes = 5;
		this.receiveTimeoutMillis = receiveTimeoutMillis;
		TryConnectRecorder r = new TryConnectRecorder(System.currentTimeMillis());
		this.recorder = new AtomicReference<TryConnectRecorder>(r);
		recover();
	}

	private void recover() {
		List<Map<String, Object>> list = template.queryForList("select id,info from " + tableName + " limit " + recoverMaxCount);
		for (Map<String, Object> entry : list) {
			Long id = (Long) entry.get(ID);
			String content = (String) entry.get(INFO);
			LoggerObject logObj = new LoggerObject(id, content);
			logObj.submit();
		}
	}

	public long insertIntoDB(final String content) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		template.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, content);
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	public void delete(final long id) {
		// 处理删除的返回值
		template.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(deleteSql);
				ps.setLong(1, id);
				return ps;
			}
		});
	}

	/**
	 * 添加一条日志
	 */
	public void addLogger(String content) {
		// 这里的模型应该是同步入本地库/日志，异步发送网络请求
		long id = insertIntoDB(content);
		long currentTime = System.currentTimeMillis();
		TryConnectRecorder old = recorder.get();
		long timeMillis = old.getRecordTimeMillis();
		if ((currentTime - timeMillis) > ONE_MINUTE) {
			TryConnectRecorder newRecorder = new TryConnectRecorder(currentTime);
			this.recorder.compareAndSet(old, newRecorder);
		}
		TryConnectRecorder current = recorder.get();
		if (current.getSuccessTimes().get() <= 0 && current.getFailTimes().get() > 10) {
			return;
		}

		LoggerObject loggerObject = new LoggerObject(id, content);
		// 记录数据库
		// 发送网络请求
		// 响应网络请求
		loggerObject.submit();
	}

	private class LoggerObject implements Callable<Void> {

		private final long id;
		private final String content;
		// private LinkedList<String> failReasons;
		// private String lastFailReason;
		private int failTimes;

		public LoggerObject(long id, String content) {
			this.id = id;
			this.content = content;
		}

		public void addFailReason(String reason) {
			// if (this.failReasons == null) {
			// this.failReasons = new LinkedList<String>();
			// }
			// this.failReasons.add(reason);
			failTimes++;
			// this.lastFailReason = reason;
		}

		public int getFailSize() {
			// return failReasons == null ? 0 : failReasons.size();
			return failTimes;
		}

		private void submit() {
			timedPool.schedule(this, getFailSize(), TimeUnit.MINUTES);
			taskCount.incrementAndGet();
		}

		@Override
		public Void call() throws Exception {
			int count = 0;
			try {
				for (;;) {
					LoggerSender sender = getSender();
					if (sender == null) {
						addFailReason(SendResult.SOCKET_NOT_AVAILABLE.getDesc());
						if (getFailSize() < maxTryTimes && taskCount.get() < recoverMaxCount) {
							submit();
						}
						break;
					} else {
						SendResult result = sender.sendLogger(content);
						if (result == SendResult.SUCCESS) {
							// TODO 当删除失败失需要捕捉失败原因,超时需要重复提交任务，一段时间后删除
							delete(id);
							break;
						} else if (result == SendResult.SOCKET_NOT_AVAILABLE) {
							if (++count >= maxTryTimes) {
								if (taskCount.get() < recoverMaxCount) {
									submit();
								}
								break;
							}
						} else {
							// TODO 一定次数后update到db，写入失败原因和失败次数
							addFailReason(result.getDesc());
							break;
						}
					}
				}
			} finally {
				taskCount.decrementAndGet();
			}
			return null;
		}
	}

	public LoggerSender getSender() {
		LoggerSender sender = queue.poll();
		if (sender != null) {
			return sender;
		}
		try {
			sender = new LoggerSender(host, port, receiveTimeoutMillis);
			LoggerQueue.this.recorder.get().getSuccessTimes().incrementAndGet();
			return sender;
		} catch (Exception ex) {
			// 建立连接失败，隔一段时间
			// 暂时屏蔽
			// ex.printStackTrace();
			LoggerQueue.this.recorder.get().getFailTimes().incrementAndGet();
			return null;
		}
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

}
