package com.rwbase.common.userEvent;

public class UserEvent {

	private UserEventType eventType;
	
	private Object param;

	public UserEvent(UserEventType eventType, Object param) {
		super();
		this.eventType = eventType;
		this.param = param;
	}

	public UserEventType getEventType() {
		return eventType;
	}

	public Object getParam() {
		return param;
	}
	
	
	
	
}
