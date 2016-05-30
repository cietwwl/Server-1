package com.rw.service.copypve;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.CopyTrialServiceProtos.MsgTrialRequest;
import com.rwproto.CopyTrialServiceProtos.eTrialType;
import com.rwproto.RequestProtos.Request;

public class CopyTrialService implements FsService {

	private CopyTrialHandler copyTrialHandler = CopyTrialHandler.getInstance();

	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			MsgTrialRequest msgTrialRequest = MsgTrialRequest.parseFrom(request.getBody().getSerializedContent());
			eTrialType trialType = msgTrialRequest.getTrialType();
			switch (trialType) {
			case TRIAL_TYPE_TRIAL:
				result = copyTrialHandler.copyTrial(player, msgTrialRequest);
				break;
			case TRIAL_TYPE_CELESTIAL:
				result = copyTrialHandler.copyCelestial(player, msgTrialRequest);
				break;
			case TRIAL_RESET_COUNT:
				result = copyTrialHandler.resetTrial(player, msgTrialRequest);
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

		return result;
	}

}
