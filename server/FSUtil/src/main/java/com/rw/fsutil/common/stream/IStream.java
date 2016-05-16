package com.rw.fsutil.common.stream;

public interface IStream<T> {
	/**
	 * 最后一次更新的值，可能为空
	 * 订阅者可以在响应消息的时候调用sample方法获取变化前的值
	 * @return
	 */
	public T sample();

	/**
	 * 订阅数据变化的通知，以及数据流关闭的通知
	 * @param listner
	 */
	public void subscribe(IStreamListner<T> listner);
	
	public void unsubscribe(IStreamListner<T> listner);
}
