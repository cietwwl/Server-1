package com.rw;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientPool {

	private static Map<String, Client> clientMap = new ConcurrentHashMap<String, Client>();
	
	public static void put(Client client){
		clientMap.put(client.getAccountId(), client);
	}
	
	public static Client getByAccountId(String accountId){
		return clientMap.get(accountId);
	}
	
	public static Client remove(String accountId){
		return clientMap.remove(accountId);
	}
}
