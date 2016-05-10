package com.fy;

import java.io.PrintWriter;
import java.util.Enumeration;
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

	private  HttpServletRequest request;

	public  void doService() {

		System.out.println("!!!!!!!!!!!!!!!入口" );
		try {
			String jsonContent = receivePost(request);
			
			ContentPojo contentPojo = FastJsonUtil.fromJson(jsonContent, ContentPojo.class);
			
			ChargeLog.info("charge", contentPojo.getCpTradeNo(), jsonContent);
			boolean success  = reqGameServer(jsonContent, contentPojo);
			String result = success?"0":"-1";			
			
			
				PrintWriter writer = response.getWriter();
				writer.write(result);	
				writer.flush();
				writer.close();			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	
	@SuppressWarnings("rawtypes")
	private String receivePost(HttpServletRequest request) throws Exception{
		
		Enumeration parameterNames = request.getParameterNames();
		
		//无语啊 yh post过来的参数是不带参数名的，只能从名字来获取了。
		String json = null;
		while (parameterNames.hasMoreElements()) {
			json = (String) parameterNames.nextElement();
			break;
		}
		
//		ServletInputStream inputStream = request.getInputStream();
//		inputStream.reset();
//		BufferedReader br =  new BufferedReader(new InputStreamReader(inputStream));
//		String line = null;
//		StringBuilder sb = new StringBuilder();
//		while((line = br.readLine())!=null){
//			sb.append(line);
//		}
//		
//		String reqBody = sb.toString();
//		return URLDecoder.decode(reqBody,HTTP.UTF_8);
		return json;
		
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
