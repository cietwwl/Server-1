package com.rwbase.gameworld;

import com.rw.fsutil.log.EngineLoggerFactory;

public class GameWorldFactory {

	private static GameWorldExecutor executor;

	/**
	 * <pre>
	 * 初始化游戏世界
	 * 随时业务改变，初始化参数可能会有所变化
	 * </pre>
	 * 
	 * @param gameLogicThreadSize
	 */
	public static synchronized void init(int gameLogicThreadSize,int asynThreadSize) {
		if (executor == null) {
			executor = new GameWorldExecutor(gameLogicThreadSize, EngineLoggerFactory.getLogger("GameWorld"),asynThreadSize);
		}
	}

	/**
	 * 获取游戏世界接口
	 * 
	 * @return
	 */
	public static GameWorld getGameWorld() {
		return executor;
	}

}
