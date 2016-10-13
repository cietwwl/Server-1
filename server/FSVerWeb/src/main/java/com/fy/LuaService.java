package com.fy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.fy.address.AddressInfo;
import com.fy.json.JSONUtil;
import com.fy.lua.LuaInfo;
import com.fy.lua.LuaMgr;
import com.fy.lua.validate.LuaRequestType;
import com.fy.lua.validate.LuaValidateRequest;
import com.fy.lua.validate.LuaValidateResponse;
import com.opensymphony.xwork2.ActionSupport;

public class LuaService extends ActionSupport implements ServletRequestAware,
		ServletResponseAware {

	private static final long serialVersionUID = 5572815701517146092L;

	private HttpServletResponse response;

	private HttpServletRequest request;
	
	private LuaMgr luaMgr;

	public void doService() throws IOException {

		try {
			ServletInputStream inputStream = request.getInputStream();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i = -1;
			while ((i = inputStream.read()) != -1) {
				baos.write(i);
			}

			String jsonString = baos.toString();
			
			LuaValidateRequest luaValidateRequest = JSONUtil.readValue(jsonString, LuaValidateRequest.class);
			
			String channel = luaValidateRequest.getChannel();
			
			LuaInfo channelLuaInfo = luaMgr.getChannelLuaInfo(channel);
			System.out.println("request lua service");
			LuaValidateResponse luaValidateResponse = new LuaValidateResponse();
			if(channelLuaInfo != null){
				int type = luaValidateRequest.getType();
				System.out.println("request lua service:type:" + type);
				
				luaValidateResponse.setResult(true);
				if(type == LuaRequestType.DOWNLOAD_LUAFILE){
					luaValidateResponse.setDownloadPath(AddressInfo.getInstance().getCdnDomain()+File.separator+channelLuaInfo.getFileslocation());
					luaValidateResponse.setBackupDownloadPath(AddressInfo.getInstance().getCdnBackUpDomain()+File.separator+channelLuaInfo.getFileslocation());
					luaValidateResponse.setDownloadSize(channelLuaInfo.getFilesize());
					luaValidateResponse.setMd5Value(channelLuaInfo.getFilesmd5());
					luaValidateResponse.setFileName(channelLuaInfo.getLuaFileMapName());
				}else{
					luaValidateResponse.setDownloadPath(AddressInfo.getInstance().getCdnDomain()+File.separator+channelLuaInfo.getLualocation());
					luaValidateResponse.setBackupDownloadPath(AddressInfo.getInstance().getCdnBackUpDomain()+File.separator+channelLuaInfo.getLualocation());
					luaValidateResponse.setDownloadSize(channelLuaInfo.getFilesize());
					luaValidateResponse.setMd5Value(channelLuaInfo.getLuamd5());
					luaValidateResponse.setFileName(channelLuaInfo.getLuaFilesName());
				}
			}else{
				luaValidateResponse.setResult(false);
			}
			
			String luaValidateResult = JSONUtil.writeValue(luaValidateResponse);
			ServletOutputStream out = response.getOutputStream();
			out.write(luaValidateResult.getBytes("UTF-8"));
			out.flush();
			inputStream.close();
			out.close();
			
		} catch (Exception ex) {

		}
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void setLuaMgr(LuaMgr luaMgr) {
		this.luaMgr = luaMgr;
	}
}
