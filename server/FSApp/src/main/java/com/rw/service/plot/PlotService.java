package com.rw.service.plot;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwbase.dao.guide.PlotProgressDAO;
import com.rwbase.dao.guide.pojo.UserPlotProgress;
import com.rwproto.PlotViewProtos.PlotProgress;
import com.rwproto.PlotViewProtos.PlotResponse;
import com.rwproto.RequestProtos.Request;

public class PlotService implements FsService {

	@Override
	public ByteString doTask(Request request, Player player) {
		PlotResponse.Builder builder = PlotResponse.newBuilder();
		UserPlotProgress plotProgress = PlotProgressDAO.getInstance().get(player.getUserId());
		ArrayList<PlotProgress> list = new ArrayList<PlotProgress>();
		if (plotProgress == null) {
			builder.addAllSavedProgress(list);
			return builder.build().toByteString();
		}
		for (Map.Entry<String, Integer> entry : plotProgress.getProgressMap().entrySet()) {
			PlotProgress.Builder b = PlotProgress.newBuilder();
			b.setPlotID(entry.getKey());
			b.setProgress(entry.getValue());
			list.add(b.build());
		}
		builder.addAllSavedProgress(list);
		return builder.build().toByteString();
	}

}
