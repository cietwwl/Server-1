package com.rw.service.login.game;

import java.util.ArrayList;
import java.util.Map;

import com.playerdata.Player;
import com.rwbase.dao.guide.GuideProgressDAO;
import com.rwbase.dao.guide.PlotProgressDAO;
import com.rwbase.dao.guide.pojo.UserGuideProgress;
import com.rwbase.dao.guide.pojo.UserPlotProgress;
import com.rwbase.dao.version.VersionConfigDAO;
import com.rwbase.dao.version.pojo.VersionConfig;
import com.rwproto.GameLoginProtos.GameLoginResponse;
import com.rwproto.GuidanceProgressProtos.GuidanceProgress;
import com.rwproto.GuidanceProgressProtos.GuidanceResponse;
import com.rwproto.PlotViewProtos.PlotProgress;
import com.rwproto.PlotViewProtos.PlotResponse;


public class LoginSynDataHelper {

	public static void setData(Player player, GameLoginResponse.Builder response){

		setMainCityData(response);
		response.setGuidance(getGuidance(player));
		response.setPlot(getPlot(player));
		
	}
	
	private static void setMainCityData(GameLoginResponse.Builder response) {
	
		response.setVersion(((VersionConfig) VersionConfigDAO.getInstance().getCfgById("version")).getValue());	
		long time = System.currentTimeMillis();
		response.setServerTime(time);
		
	}
	
	
	private static GuidanceResponse.Builder getGuidance(Player player){
		GuidanceResponse.Builder builder = GuidanceResponse.newBuilder();
		UserGuideProgress guide = GuideProgressDAO.getInstance().get(player.getUserId());
		ArrayList<GuidanceProgress> list = new ArrayList<GuidanceProgress>();
	
		if(guide!=null){
			
			for (Map.Entry<Integer, Integer> entry : guide.getProgressMap().entrySet()) {
				GuidanceProgress.Builder b = GuidanceProgress.newBuilder();
				b.setGuideID(entry.getKey());
				b.setProgress(entry.getValue());
				list.add(b.build());
			}
		}
		builder.addAllSavedProgress(list);
		return builder;
	}
	
	private static PlotResponse.Builder getPlot(Player player) {
		PlotResponse.Builder builder = PlotResponse.newBuilder();
		UserPlotProgress plotProgress = PlotProgressDAO.getInstance().get(player.getUserId());
		ArrayList<PlotProgress> list = new ArrayList<PlotProgress>();
		if(plotProgress!=null){
			for (Map.Entry<String, Integer> entry : plotProgress.getProgressMap().entrySet()) {
				PlotProgress.Builder b = PlotProgress.newBuilder();
				b.setPlotID(entry.getKey());
				b.setProgress(entry.getValue());
				list.add(b.build());
			}
		}
		builder.addAllSavedProgress(list);
		return builder;
	
	}
}
