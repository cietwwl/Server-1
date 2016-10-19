package com.rwbase.dao.groupcompetition.pojo;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGroupCompetitionData {

	@JsonProperty("1")
	private List<UserGroupCompetitionScoreRecord> records;

	public List<UserGroupCompetitionScoreRecord> getRecords() {
		return records;
	}

	public void setRecords(List<UserGroupCompetitionScoreRecord> records) {
		this.records = new ArrayList<UserGroupCompetitionScoreRecord>(records);
	}
	
	public void addRecord(UserGroupCompetitionScoreRecord record) {
		this.records.add(record);
	}
	
	public void clear() {
		this.records.clear();
	}
	
}
