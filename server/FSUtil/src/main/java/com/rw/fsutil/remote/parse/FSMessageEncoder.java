package com.rw.fsutil.remote.parse;

public interface FSMessageEncoder<SendMessage> {

	public byte[] encode(SendMessage msg);

}
