package com.fy;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.fy.common.FastJsonUtil;
import com.fy.db.ZoneInfo;
import com.fy.db.ZoneInfoMgr;
import com.fy.http.HttpClientUtil;
import com.opensymphony.xwork2.ActionSupport;

public class ChargeService extends ActionSupport implements ServletRequestAware,ServletResponseAware {

	private static final long serialVersionUID = 5572815701517146091L;

	private HttpServletResponse response;

	private HttpServletRequest request;

	public void doService() {

		String jsonContent = request.getParameter("content");
		try {
			boolean success  = reqGameServer(jsonContent);
			
			String result = success?"0":"-1";			
			
			PrintWriter writer = response.getWriter();
			
			writer.write(result);	
			writer.flush();
			writer.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	private boolean reqGameServer(String jsonContent){
		
		boolean success = false;
		
		try {
			ContentPojo contentPojo = FastJsonUtil.fromJson(jsonContent, ContentPojo.class);
			ZoneInfo targetZone = ZoneInfoMgr.getInstance().getZone(contentPojo.getServerId());
			
			if(targetZone!=null){
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("content", jsonContent);
				
				String resp = HttpClientUtil.post(targetZone.getServerIp(),targetZone.getChargePort(), params);
				success = StringUtils.contains(resp, "ok");				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}

	

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;

	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;

	}

}