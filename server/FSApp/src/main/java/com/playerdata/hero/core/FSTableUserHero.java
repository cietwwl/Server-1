package com.playerdata.hero.core;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 
 * <pre>
 * 对应原来的TableUserHero，由于同步新增英雄是用这个对象同步，所以重构也需要有一种这类型的对象
 * 只是原来是缓存起来的，现在是每次都创建一个新的同步到客户端
 * 简单来说就是重构不想修改原来的同步机制，所以沿用这种
 * </pre>
 * @since 2016-07-16 16:29
 * @author CHEN.P
 *
 */
@Table(name = "mt_user_hero")
@SynClass
public class FSTableUserHero {

	@Id
	private final String userId;
	final List<String> heroIds;
	
	public FSTableUserHero(String pUserId, List<String> pHeroIds) {
		this.userId = pUserId;
		this.heroIds = pHeroIds;
	}
}
