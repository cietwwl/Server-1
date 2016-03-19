package com.rw.service;

import com.google.protobuf.ByteString;
import com.rw.account.Account;
import com.rwproto.RequestProtos.Request;


public interface RequestService {
	
	public ByteString doTask(Request request, Account account);
	
}
