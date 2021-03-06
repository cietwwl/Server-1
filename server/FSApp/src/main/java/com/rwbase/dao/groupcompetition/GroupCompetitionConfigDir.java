package com.rwbase.dao.groupcompetition;

public enum GroupCompetitionConfigDir {

	DIR("GroupCompetition");

	private final String _pathFormat;

	private GroupCompetitionConfigDir(String path) {
		this._pathFormat = path + "/" + "%s";
	}

	public String getFullPath(String fileName) {
		return String.format(_pathFormat, fileName);
	}
}
