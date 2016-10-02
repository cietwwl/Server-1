package com.fy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.HTTP;
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

	private  HttpServletRequest request;

	public  void doService() {

		System.out.println("!!!!!!!!!!!!!!!入口" );
		try {
			String jsonContent = receivePost(request);
			
			ContentPojo contentPojo = FastJsonUtil.fromJson(jsonContent, ContentPojo.class);
			
			ChargeLog.info("charge", contentPojo.getCpTradeNo(), jsonContent);
			boolean success  = reqGameServer(jsonContent, contentPojo);
			
			
			
				PrintWriter writer = response.getWriter();
				writer.write(result);	
				writer.flush();
				writer.close();			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			ChargeLog.info("charge", "订单初始化失败", "异常申请：json为空");
		}
	}

	
	@SuppressWarnings({"deprecation"})
	private String receivePost(HttpServletRequest request) throws Exception{
		
		BufferedReader br =  new BufferedReader(new InputStreamReader(request.getInputStream()));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while((line = br.readLine())!=null){
			sb.append(line);
		}
		
		String reqBody = sb.toString();
		return URLDecoder.decode(reqBody,HTTP.UTF_8);
		
	}


	public   boolean reqGameServer(String jsonContent,ContentPojo contentPojo){
		
		boolean success = false;
		
		try {
			
			ZoneInfo targetZone = ZoneInfoMgr.getInstance().getZone(contentPojo.getServerId());
			
			if(targetZone!=null){
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("content", jsonContent);
				System.out.println("ip= " + targetZone.getServerIp() + "  port=" + targetZone.getChargePort());
				String resp = HttpClientUtil.post(targetZone.getServerIp(),targetZone.getChargePort(), params);
				success = StringUtils.contains(resp, "ok");
				ChargeLog.info("charge", contentPojo.getCpTradeNo(), "游戏服处理结果："+resp);
			}else{
				System.out.println("targetZone == null ");
			}
			
		} catch (Exception e) {			
			ChargeLog.error("charge", contentPojo.getCpTradeNo(), "请求游戏服处理异常",e);
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
