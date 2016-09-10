package com.playerdata.teambattle.dataException;

public class NoTeamException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NoTeamException(String message) {
        super(message);
    }
}
