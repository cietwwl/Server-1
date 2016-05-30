package com.rw.fsutil.common.stream;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 这个类不允许多线程进行更新
 * @author franky
 *
 * @param <T>
 */
public class StreamImpl<T> implements IStream<T> {
	private T last=null;
	private boolean isClose=false;
	private Collection<IStreamListner<T>> listners;
	
	public StreamImpl(){}
	public StreamImpl(T initValue){
		last = initValue;
	}
	
	public void hold(T newVal){
		last = newVal;
	}
	
	public void fire(T newVal){
		if (isClose) {
			System.out.println("Warning: stream is close, can not fire again");
			return;
		}
		if (listners != null){
			IStreamListner<T>[] localListners = synchronizedCopy();
			if (localListners == null) {
				System.out.println("ERROR: stream fired loop detected, does not allow fire again");
				return;
			}
			for (IStreamListner<T> listner : localListners) {
				if (listner != null){
					try {
						listner.onChange(newVal);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			isFiring = false;
		}
		last = newVal;
	}
	
	private boolean isFiring = false;
	
	private synchronized IStreamListner<T>[] synchronizedCopy() {
		if (isFiring) return null;
		isFiring = true;
		@SuppressWarnings("unchecked")
		IStreamListner<T>[] array = new IStreamListner[listners.size()];
		return listners.toArray(array);
	}
	
	public void close(){
		if (isClose) {
			return;
		}
		isClose = false;
		if (listners != null){
			IStreamListner<T>[] localListners = synchronizedCopy();
			for (IStreamListner<T> listner : localListners) {
				if (listner != null){
					listner.onClose(this);
				}
			}
			listners = null;
		}
	}
	
	@Override
	public T sample() {
		return last;
	}

	@Override
	public void subscribe(IStreamListner<T> listner) {
		if (listner == null){
			System.out.println("Warning: null listner parameter!");
			return;
		}
		if (listners == null){
			listners = new ArrayList<IStreamListner<T>>();
		}
		listners.add(listner);
	}
	
	@Override
	public void unsubscribe(IStreamListner<T> listner) {
		if (listner == null){
			System.out.println("Warning: null listner parameter!");
			return;
		}
		if (listners == null){
			return;
		}
		listners.remove(listner);
	}

}
