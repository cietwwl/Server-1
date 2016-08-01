package com.rw.netty.http.requestHandler;

import java.util.List;

import com.rw.service.http.platformResponse.WhiteListBaseDataResponse;
import com.rw.service.http.request.ResponseObject;
import com.rwbase.dao.whiteList.TableWhiteList;
import com.rwbase.dao.whiteList.TableWhiteListDAO;
import com.rwbase.dao.whiteList.TableWhiteListHolder;

public class WhiteListHandler {
	private final static String PROCESS_CLOSE = "close";
	private final static String PROCESS_ADD = "add";
	private final static String PROCESS_DEL = "del";
	
	public static ResponseObject updateWhiteList(WhiteListBaseDataResponse response){
		List<String> accountList = response.getAccountList();
		String process = response.getProcess();
		for (String accountId : accountList) {
			if(accountId == null){
				continue;
			}
			TableWhiteListHolder holder = new TableWhiteListHolder(accountId);
			TableWhiteList tableWhiteList = holder.getTableWhiteList();
			
			TableWhiteList item = TableWhiteListDAO.getInstance().getWhiteListItem(accountId);

			if (process.equals(PROCESS_CLOSE)) {
				item.setClose(response.isBlnClose());
				TableWhiteListDAO.getInstance().updateWhiteList(item);
			} else if (response.getProcess().equals(PROCESS_ADD)) {
				item.setAccountId(accountId);
				item.setClose(response.isBlnClose());
				TableWhiteListDAO.getInstance().addWhiteList(item);
				TableWhiteListDAO.getInstance().addWhiteList(item);
			} else if (process.equals(PROCESS_DEL)) {
				TableWhiteListDAO.getInstance().removeWhiteList(accountId);
			}
		}
		
		ResponseObject result = new ResponseObject();
		result.setSuccess(true);
		
		return result;
	}
}
