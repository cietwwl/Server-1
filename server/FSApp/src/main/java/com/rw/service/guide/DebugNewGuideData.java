package com.rw.service.guide;

import com.google.protobuf.ByteString;
import com.rw.config.RawConfigReader;

public class DebugNewGuideData {
	private static DebugNewGuideData instance;
	public static DebugNewGuideData getInstance(){
		if (instance == null){
			instance = new DebugNewGuideData();
		}
		return instance;
	}
	public static void Clear(){
		instance = null;
	}
	
	private ByteString ActionsData;
	private ByteString ConditionalsData;
	private ByteString ConductressData;
	private ByteString GuidanceData;

	public ByteString getActionsData() {
		return ActionsData;
	}

	public ByteString getConditionalsData() {
		return ConditionalsData;
	}

	public ByteString getConductressData() {
		return ConductressData;
	}

	public ByteString getGuidanceData() {
		return GuidanceData;
	}

	public void ClearData() {
		ActionsData = null;
		ConditionalsData = null;
		ConductressData = null;
		GuidanceData = null;
	}

	public boolean RefreshConfig() {
		String[] configFileNames = { "Actions.csv", "Conditionals.csv", "Conductress.csv", "Guidance.csv" };
		ByteString configdata;
		configdata = RawConfigReader.ReadConfigForProto("Guidance/" + configFileNames[0]);
		if (configdata != null) {
			ActionsData = configdata;
		}
		configdata = RawConfigReader.ReadConfigForProto("Guidance/" + configFileNames[1]);
		if (configdata != null) {
			ConditionalsData = configdata;
		}
		configdata = RawConfigReader.ReadConfigForProto("Guidance/" + configFileNames[2]);
		if (configdata != null) {
			ConductressData = configdata;
		}
		configdata = RawConfigReader.ReadConfigForProto("Guidance/" + configFileNames[3]);
		if (configdata != null) {
			GuidanceData = configdata;
		}
		return ActionsData != null && ConditionalsData != null && ConductressData != null && GuidanceData != null; 
	}
}
