package com.rounter.controller;

import org.apache.commons.lang3.tuple.Pair;


public abstract class AbsController<T1,T2> {
	
	/**
	 * 操作前的一些前置动作，例如检查数据，加密解密等
	 * @param param
	 * @return 返回的T1.T2一般对应为状态及数据
	 */
	abstract Pair<T1, T2> beforeOpt(Object ...param);
	
	/**
	 * 操作后的动作，例如检查返回的数据状态，加密等
	 * @param param
	 * @return
	 */
	abstract Pair<T1, T2> afterOpt(Object ... param);

}
