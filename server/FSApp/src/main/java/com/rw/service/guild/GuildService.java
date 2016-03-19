package com.rw.service.guild;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GuildProtos.EGuildType;
import com.rwproto.GuildProtos.GuildRequest;
import com.rwproto.RequestProtos.Request;

public class GuildService implements FsService {
	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			GuildRequest req = GuildRequest.parseFrom(request.getBody().getSerializedContent());
			EGuildType requestType = req.getType();
			switch (requestType) {
			case SelectMsg:
//				result = GuildHandler.getInstance().baseList(player);
				break;
			case CreateMsg:

//				result = GuildHandler.getInstance().create(req, player);
				break;

			case dismiss:
//				result = GuildHandler.getInstance().dismiss(req, player);
				break;
				
			case MyMsg:

//				result = GuildHandler.getInstance().getMyGuildInfo(player);
				break;
			case apply:

//				result = GuildHandler.getInstance().apply(req, player);
				break;

			case ignore:

//				result = GuildHandler.getInstance().ignore(req, player);
				break;

			case pass:
//				result = GuildHandler.getInstance().pass(req, player);
				break;

			case exit:
//				result = GuildHandler.getInstance().exit(req, player);
				break;

			case kick:
//				result = GuildHandler.getInstance().kick(req, player);
				break;

			case promote:
//				result = GuildHandler.getInstance().promote(req, player);
				break;

			case demotion:
//				result = GuildHandler.getInstance().demotion(req, player);
				break;

			case assignment:
//				result = GuildHandler.getInstance().assignment(req, player);
				break;


			case setEmail:
//				result = GuildHandler.getInstance().setEmail(req, player);
				break;
			case updataNotice:
//				result = GuildHandler.getInstance().updataNotice(req, player);
				break;
			case donate:
//				result = GuildHandler.getInstance().donate(req, player);
				break;
			case uplevel:
//				result = GuildHandler.getInstance().uplevel(req, player);
				break;
			case log:
//				result = GuildHandler.getInstance().log(req, player);
				break;
			case updataUnlevel:
//				result = GuildHandler.getInstance().updataUnlevel(req, player);
				break;

			case updataIcon:
//				result = GuildHandler.getInstance().updataIcon(req, player);
				break;

			case updataName:
//				result = GuildHandler.getInstance().updataName(req, player);
				break;

			case updataType:
//				result = GuildHandler.getInstance().updataType(req, player);
				break;

			default:
				break;
			}

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return result;
	}

}
