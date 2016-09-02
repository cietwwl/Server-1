package com.rw.service.plot;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwbase.dao.guide.PlotProgressDAO;
import com.rwbase.dao.guide.pojo.UserPlotProgress;
import com.rwproto.MsgDef.Command;
import com.rwproto.PlotViewProtos.PlotProgress;
import com.rwproto.PlotViewProtos.PlotResponse;
import com.rwproto.RequestProtos.Request;

public class PlotService implements FsService <Request, Command>{

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

	@Override
	public Request parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		return request;
	}

	@Override
	public Command getMsgType(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

}
