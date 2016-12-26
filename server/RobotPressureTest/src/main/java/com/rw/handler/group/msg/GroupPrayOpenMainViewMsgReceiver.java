package com.rw.handler.group.msg;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GroupPrayProto.GroupPrayCommonRspMsg;
import com.rwproto.GroupPrayProto.OpenPrayMainViewRspMsg;
import com.rwproto.GroupPrayProto.PrayEntry;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/**
 * @Author HC
 * @date 2016年12月26日 下午3:31:49
 * @desc
 **/

public class GroupPrayOpenMainViewMsgReceiver extends PrintMsgReciver {

	public GroupPrayOpenMainViewMsgReceiver(Command command, String functionName, String protoType) {
		super(command, functionName, protoType);
	}

	@Override
	public boolean execute(Client client, Response response) {
		try {
			GroupPrayCommonRspMsg rsp = GroupPrayCommonRspMsg.parseFrom(response.getSerializedContent());
			if (!rsp.getIsSuccess()) {// 失败了
				RobotLog.fail(parseFunctionDesc() + "失败" + (rsp.getTipMsg() != null ? ("。原因是：" + rsp.getTipMsg()) : "") + " client.账号=" + client.getAccountId());
				return true;
			}

			OpenPrayMainViewRspMsg openPrayMainViewRsp = rsp.getOpenPrayMainViewRsp();
			List<PrayEntry> entryList = openPrayMainViewRsp.getEntryList();
			if (!entryList.isEmpty()) {
				int size = entryList.size();

				String userId = client.getUserId();

				List<PrayEntry> saveEntryList = new ArrayList<PrayEntry>(size);
				for (int i = 0; i < size; i++) {
					PrayEntry prayEntry = entryList.get(i);
					if (prayEntry == null || userId.equals(prayEntry.getMemberId())) {
						continue;
					}

					saveEntryList.add(prayEntry);
				}

				client.getGroupPrayData().setEntryList(saveEntryList);
			}
		} catch (InvalidProtocolBufferException e) {
			RobotLog.fail("解析帮派祈福的响应消息出现了异常", e);
		}
		return true;
	}

	private String parseFunctionDesc() {
		return functionName + "[" + protoType + "] ";
	}
}