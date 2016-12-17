package com.rounter;

public class RounterContent {
	
	private final long id;
    private final String content;

    public RounterContent(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

}
