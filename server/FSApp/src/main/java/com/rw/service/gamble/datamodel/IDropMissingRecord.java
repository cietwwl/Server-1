package com.rw.service.gamble.datamodel;

public interface IDropMissingRecord {

	public boolean containsRecord(String heroId, int itemId);
	
	public void addToRecord(String heroId, int itemId);
}
