package com.rw.service.gamble.datamodel;

public class DropMissingRecordDefaultImpl implements IDropMissingRecord {

	@Override
	public boolean containsRecord(String heroId, int itemId) {
		return false;
	}

	@Override
	public void addToRecord(String heroId, int itemId) {
		
	}

}
