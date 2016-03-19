package com.rw.netty.http.requestHandler;

import java.util.List;

import com.rw.service.http.platformResponse.WhiteListBaseDataResponse;
import com.rwbase.dao.whiteList.TableWhiteList;
import com.rwbase.dao.whiteList.TableWhiteListHolder;

public class WhiteListHandler {
	private final static String PROCESS_CLOSE = "close";
	private final static String PROCESS_ADD = "add";
	private final static String PROCESS_DEL = "del";
	
	public static boolean updateWhiteList(WhiteListBaseDataResponse response){
		List<String> accountList = response.getAccountList();
		String process = response.getProcess();
		for (String accountId : accountList) {
			if(accountId == null){
				continue;
			}
			TableWhiteListHolder holder = new TableWhiteListHolder(accountId);
			TableWhiteList tableWhiteList = holder.getTableWhiteList();

			if (process.equals(PROCESS_CLOSE)) {
				tableWhiteList.setClose(response.isBlnClose());
				holder.saveItem(tableWhiteList);
			} else if (response.getProcess().equals(PROCESS_ADD)) {
				tableWhiteList.setAccountId(accountId);
				tableWhiteList.setClose(response.isBlnClose());
				holder.saveItem(tableWhiteList);
			} else if (process.equals(PROCESS_DEL)) {
				holder.removeItem(accountId);
			}
		}
		return true;
	}
}
