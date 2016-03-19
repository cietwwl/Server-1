package com.bm.player;

import java.util.HashMap;
import java.util.Map;

/*
 * @author HC
 * @date 2016年2月1日 下午3:37:58
 * @Description 观察者工厂
 */
public class ObserverFactory {
	public static enum ObserverType {
		PLAYER_CHANER;// 角色改变
	}

	private static ObserverFactory instance;

	public static ObserverFactory getInstance() {
		if (instance == null) {
			instance = new ObserverFactory();
		}

		return instance;
	}

	private Map<Integer, Observer> observerMap;// 观察者

	private ObserverFactory() {
		observerMap = new HashMap<Integer, Observer>();
	}

	public void initFactory() {
		addObserver(new PlayerChangePopertyObserver());
	}

	/**
	 * 获取观察者实例
	 * 
	 * @param type
	 * @return
	 */
	public Observer getObserver(ObserverType type) {
		if (observerMap == null || observerMap.isEmpty()) {
			return null;
		}

		return observerMap.get(type.ordinal());
	}

	/**
	 * 增加一个观察者
	 * 
	 * @param observer
	 */
	private void addObserver(Observer observer) {
		if (observer == null) {
			return;
		}

		observerMap.put(observer.getObserverType(), observer);
	}
}