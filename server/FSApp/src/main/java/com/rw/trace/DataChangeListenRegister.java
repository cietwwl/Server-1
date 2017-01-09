package com.rw.trace;

import com.bm.targetSell.listener.ChargeDataListener;
import com.bm.targetSell.listener.UserDataListener;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.DataRdbDao;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.cache.trace.DataChangedEvent;
import com.rw.fsutil.dao.cache.trace.DataChangedVisitor;
import com.rw.fsutil.dao.cache.trace.MapItemChangedEvent;
import com.rw.fsutil.dao.cache.trace.MapItemChangedListener;
import com.rw.fsutil.dao.cache.trace.SingleChangedListener;
import com.rw.trace.listener.FSHeroDataListener;
import com.rw.trace.listener.FixExpEquipDataListener;
import com.rw.trace.listener.FixNorEquipDataListener;
import com.rw.trace.listener.InlayDataListener;
import com.rw.trace.listener.ItemDataListener;
import com.rw.trace.listener.MainRoleDataListener;
import com.rw.trace.listener.MajorDataListener;
import com.rw.trace.listener.SpriteAttachDataListener;
import com.rw.trace.listener.UserGameDataListener;

/**
 * <pre>
 * 数据变化监听注册器
 * {@link DataChangedVisitor}必须指定为{@link SingleChangedListener}或{@link MapItemChangedListener}<br/>
 * {@link SingleChangedListener}与{@link DataChangedEvent}对应，监听普通数据类型如{@link DataKVDao}、{@link DataRdbDao}<br/>
 * {@link MapItemChangedListener}与{@link MapItemChangedEvent}对应，监听{@link IMapItem}数据类型<br/>
 * 补充：
 * traceClass->被监听数据类型，必须是在DataTraceRegistrator中有注册
 * listenerClass->监听实现类，必须有一个参数为空的构造函数，而且监听器本身不应有实例变量
 * </pre>
 * 
 * @author Jamaz
 *
 */
public enum DataChangeListenRegister {

	ITEM_DATA(DataTraceRegistrator.ITEM_DATA, ItemDataListener.class), 
	USER_GAME_DATA(DataTraceRegistrator.USER_GAME_DATA, UserGameDataListener.class),
	MAJOR_DATA(DataTraceRegistrator.MAJOR_DATA, MajorDataListener.class),
	USERDATA(DataTraceRegistrator.USER, UserDataListener.class),
	CHARGEDATA(DataTraceRegistrator.CHARGE_DATA, ChargeDataListener.class),
	MAINROLE(DataTraceRegistrator.MAIN_ROLE, MainRoleDataListener.class),
	HERODATA(DataTraceRegistrator.HERO, FSHeroDataListener.class),
	FIXEXPEQUIPDATA(DataTraceRegistrator.FIX_EXP_EQUIP_ITEM, FixExpEquipDataListener.class),
	FIXNOREQUIPDATA(DataTraceRegistrator.FIX_NOMR_EQUIP_ITEM, FixNorEquipDataListener.class),
	INLAYDATA(DataTraceRegistrator.INLAY_ITEM, InlayDataListener.class),
	SPRITEATTACHITEM(DataTraceRegistrator.SPRITEATTACHITEM, SpriteAttachDataListener.class),
	
	;
	private DataChangeListenRegister(DataTraceRegistrator traceClass, Class<? extends DataChangedVisitor<?>> listenerClass) {
		this.traceClass = traceClass;
		this.listenerClass = listenerClass;
	}

	private DataTraceRegistrator traceClass;// 被监听数据类，必须在DataTraceRegistrator中有注册
	private Class<? extends DataChangedVisitor<?>> listenerClass;// 必须有一个参数为空的构造函数，而且监听器本身不应有实例变量

	public DataTraceRegistrator getTraceClass() {
		return traceClass;
	}

	public Class<? extends DataChangedVisitor<?>> getListenerClass() {
		return listenerClass;
	}
}
