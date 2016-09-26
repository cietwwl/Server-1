package com.rw.service.copypve;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.CopyTrialServiceProtos.MsgTrialRequest;
import com.rwproto.CopyTrialServiceProtos.eTrialType;
import com.rwproto.RequestProtos.Request;

public class CopyTrialService implements FsService<MsgTrialRequest, eTrialType> {

	private CopyTrialHandler copyTrialHandler = CopyTrialHandler.getInstance();

	@Override
	public ByteString doTask(MsgTrialRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			eTrialType trialType = request.getTrialType();
			switch (trialType) {
			case TRIAL_TYPE_TRIAL:
				result = copyTrialHandler.copyTrial(player, request);
				break;
			case TRIAL_TYPE_CELESTIAL:
				result = copyTrialHandler.copyCelestial(player, request);
				break;
			case TRIAL_RESET_COUNT:
				result = copyTrialHandler.resetTrial(player, request);
				break;
			case TRAIL_VALLEY:
				result = copyTrialHandler.copyTrial(player, request);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public MsgTrialRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgTrialRequest msgTrialRequest = MsgTrialRequest.parseFrom(request.getBody().getSerializedContent());
		return msgTrialRequest;
	}

	@Override
	public eTrialType getMsgType(MsgTrialRequest request) {
		// TODO Auto-generated method stub
		return request.getTrialType();
	}

}
