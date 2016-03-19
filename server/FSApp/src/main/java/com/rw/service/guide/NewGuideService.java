package com.rw.service.guide;

import java.util.ArrayList;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwbase.dao.guide.GuideProgressDAO;
import com.rwbase.dao.guide.pojo.UserGuideProgress;
import com.rwproto.GuidanceProgressProtos.GuidanceProgress;
import com.rwproto.GuidanceProgressProtos.GuidanceResponse;
import com.rwproto.RequestProtos.Request;

public class NewGuideService implements FsService{

	@Override
	public ByteString doTask(Request request, Player player) {
		GuidanceResponse.Builder builder = GuidanceResponse.newBuilder();
		UserGuideProgress guide = GuideProgressDAO.getInstance().get(player.getUserId());
		ArrayList<GuidanceProgress> list = new ArrayList<GuidanceProgress>();
		if (guide == null) {
			builder.addAllSavedProgress(list);
			return builder.build().toByteString();
		}
		for (Map.Entry<Integer, Integer> entry : guide.getProgressMap().entrySet()) {
			GuidanceProgress.Builder b = GuidanceProgress.newBuilder();
			b.setGuideID(entry.getKey());
			b.setProgress(entry.getValue());
			list.add(b.build());
		}
		builder.addAllSavedProgress(list);
		return builder.build().toByteString();
	}

}
