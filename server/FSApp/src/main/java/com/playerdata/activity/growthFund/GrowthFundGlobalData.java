package com.playerdata.activity.growthFund;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class GrowthFundGlobalData {

	@JsonProperty("1")
	private AtomicInteger _alreadyBoughtCount; // 已经购买的人数
	private final AtomicBoolean _dirty = new AtomicBoolean(); // 数据是否已经发生过改变
	private final AtomicBoolean _needToSyn = new AtomicBoolean(false); // 是否需要把人数同步到客户端
	
	public GrowthFundGlobalData() {}
	
	public static GrowthFundGlobalData newInstance() {
		GrowthFundGlobalData data = new GrowthFundGlobalData();
		data._alreadyBoughtCount = new AtomicInteger();
		return data;
	}
	
	AtomicBoolean isDirty() {
		return _dirty;
	}
	
	AtomicBoolean isNeedToSyn() {
		return _needToSyn;
	}
	
	/**
	 * 
	 * 获取已经购买的人数
	 * 
	 * @return
	 */
	public int getAlreadyBoughtCount() {
		return _alreadyBoughtCount.get();
	}
	
	/**
	 * 增加一个购买人数
	 */
	public void increaseAlreadyBoughtCount() {
		_alreadyBoughtCount.incrementAndGet();
		_dirty.compareAndSet(false, true);
		_needToSyn.compareAndSet(false, true);
	}
	
	@JsonIgnore
	public void setAlreadyBoughtCount(int count) {
		if (count > 0) {
			_alreadyBoughtCount.getAndSet(count);
			_dirty.compareAndSet(false, true);
			_needToSyn.compareAndSet(false, true);
		}
	}
}
