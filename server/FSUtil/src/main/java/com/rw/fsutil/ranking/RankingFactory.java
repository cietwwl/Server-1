package com.rw.fsutil.ranking;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.common.TypeIdentification;
import com.rw.fsutil.ranking.impl.ListRankingImpl;
import com.rw.fsutil.ranking.impl.RankingDataManager;
import com.rw.fsutil.ranking.impl.RankingImpl;
import com.rw.fsutil.shutdown.IShutdownHandler;
import com.rw.fsutil.shutdown.ShutdownService;

/**
 * <pre>
 * 排行榜工厂，负责排行榜的初始化和缓存各类型的排行榜
 * 排行榜数据常驻内存
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class RankingFactory {

	private static HashMap<Integer, ListRankingImpl> srankingMap; // 列表排行榜的集合
	private static HashMap<Integer, RankingImpl> rankingMap; // 排行榜集合
	private static boolean isInit; // 控制RankingFactory只能被init一次
	private static AtomicLong generator; // 排行榜条目序列生成器
	private static RankingEntrySequence sequence = new RankingEntrySequence() {

		@Override
		public long assignId() {
			return generator.incrementAndGet();
		}
	};

	/**
	 * <pre>
	 *  初始化所有类型的排行榜与列表排行榜数据
	 *  由逻辑通过{@link RankingConfig}与{@link ListRankingConfig}定义
	 *  初始化后排行榜的数据会一直常驻内存，由每个类型的配置的{@link RankingConfig#getUpdatePeriodMinutes()}方法定义持久化的周期
	 *  此方法只能被调用一次，多次调用会抛出{@link IllegalStateException}，建议在服务器启动时调用
	 *  调用过程中实例化排行榜条目发生异常会抛出{@link ExceptionInInitializerError}
	 * </pre>
	 * 
	 * @param rankingConfigs
	 * @param srankingConfigs
	 */
	public static synchronized void init(List<? extends RankingConfig> rankingConfigs, List<? extends ListRankingConfig> srankingConfigs, ScheduledExecutorService rankingService, ScheduledExecutorService listRankingService) {
		if (isInit) {
			throw new IllegalStateException("RankingFactory has been initialized");
		}
		generator = new AtomicLong(RankingDataManager.getMaxPrimaryKey());
		// TODO 排行榜还没处理
		HashMap<Integer, RankingImpl> rankingMap_ = new HashMap<Integer, RankingImpl>();
		for (int i = rankingConfigs.size(); --i >= 0;) {
			RankingConfig config = rankingConfigs.get(i);
			int type = config.getType();
			int maxCapacity = config.getMaxCapacity();
			Class<? extends RankingExtension> extensionClass = config.getRankingExtension();
			int periodMinutes = config.getUpdatePeriodMinutes();
			String name = config.getName();
			try {
				RankingImpl ranking = new RankingImpl(type, maxCapacity, name, extensionClass.newInstance(), periodMinutes, sequence, rankingService);
				rankingMap_.put(type, ranking);
			} catch (Throwable t) {
				throw new ExceptionInInitializerError(t);
			}
		}
		HashMap<Integer, ListRankingImpl> srankingMap_ = new HashMap<Integer, ListRankingImpl>();
		for (int i = srankingConfigs.size(); --i >= 0;) {
			ListRankingConfig config = srankingConfigs.get(i);
			int type = config.getType();
			int maxCapacity = config.getMaxCapacity();
			Class<? extends ListRankingExtension> extensionClass = config.getSRankingExtension();
			int periodMinutes = config.getUpdatePeriodMinutes();

			try {
				ListRankingImpl srankingImpl = new ListRankingImpl(type, maxCapacity, periodMinutes, extensionClass.newInstance(), listRankingService);
				srankingMap_.put(type, srankingImpl);
			} catch (Throwable t) {
				throw new ExceptionInInitializerError(t);
			}
		}
		rankingMap = rankingMap_;
		srankingMap = srankingMap_;
		// 注册到停服处理器
		ShutdownService.registerShutdownService(new IShutdownHandler() {

			@Override
			public void notifyShutdown() {
				for (RankingImpl ranking : rankingMap.values()) {
					try {
						ranking.updateToDB();
						FSUtilLogger.info("停服保存排行榜：" + ranking.getName());
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				for (ListRankingImpl ranking : srankingMap.values()) {
					try {
						ranking.updateToDB();
						FSUtilLogger.info("停服保存竞技场：" + ranking.getType());
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}

			}
		});
		isInit = true;
	}

	/**
	 * <pre>
	 * 通过{@link TypeIdentification}获取指定类型的列表排行榜
	 * 返回类型ListRanking (String, XXExtAttribute defined in XXExtension)
	 * </pre>
	 * 
	 * @param typeId
	 * @return ListRanking
	 */
	public static ListRanking getSRanking(TypeIdentification typeId) {
		if (typeId == null) {
			return null;
		}
		return srankingMap.get(typeId.getTypeValue());
	}

	/**
	 * 获取指定类型的列表排行榜
	 * 
	 * @param type
	 * @return
	 */
	public static ListRanking getSRanking(int type) {
		return srankingMap.get(type);
	}

	/**
	 * 获取指定类型的排行榜
	 * 
	 * @param type
	 * @return
	 */
	public static Ranking getRanking(int type) {
		return rankingMap.get(type);
	}

	/**
	 * 通过{@link TypeIdentification}获取指定类型的排行榜
	 * 
	 * @param typeId
	 * @return
	 */
	public static <C extends Comparable<C>, E> Ranking<C, E> getRanking(TypeIdentification typeId) {
		if (typeId == null) {
			return null;
		}
		return rankingMap.get(typeId.getTypeValue());
	}

	/**
	 * 排行榜条目序列生成器
	 * 
	 * @author Jamaz
	 *
	 */
	public interface RankingEntrySequence {

		public long assignId();
	}

}
