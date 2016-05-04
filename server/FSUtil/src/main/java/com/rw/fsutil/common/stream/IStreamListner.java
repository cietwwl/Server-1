package com.rw.fsutil.common.stream;

public interface IStreamListner<T> {
	public void onChange(T newValue);

	public void onClose();
}
