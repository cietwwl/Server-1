package com.fy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.fy.address.ChannelAddressInfo;
import com.fy.constant.Constant;
import com.fy.json.JSONException;
import com.fy.json.JSONObject;
import com.fy.json.JSONUtil;
import com.fy.lua.LuaInfo;
import com.fy.lua.LuaMgr;
import com.fy.utils.DateTimeUtils;
import com.fy.utils.DeviceUtil;
import com.fy.version.Version;
import com.fy.version.VersionDao;
import com.fy.version.VersionMgr;
import com.fy.version.activity.ChannelAddressCfg;
import com.fy.version.activity.ChannelAddressCfgDao;
import com.fy.version.activity.VersionUpdateCfg;
import com.fy.version.activity.VersionUpdateCfgDao;
import com.opensymphony.xwork2.ActionSupport;

public class VerService extends ActionSupport implements ServletRequestAware,
		ServletResponseAware {

	private static final long serialVersionUID = 5572815701517146091L;

	private HttpServletResponse response;

	private HttpServletRequest request;

	private VersionMgr versionMgr;

	public void doService() throws IOException {

		try {
			ServletInputStream inputStream = request.getInputStream();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i = -1;
			while((i=inputStream.read())!=-1){
				baos.write(i);
			}

			String jsonString = baos.toString();
			if(jsonString.isEmpty()){
				return;
			}
			Version clientVersion = getClientVersion(jsonString);
			String cpuType = clientVersion.getCpuType();
			String deviceModel = clientVersion.getDeviceModel();
			System.out.println("------------cpuType:" + cpuType + "deviceModel:" + deviceModel);
			String luaChannel = clientVersion.getChannel();
			String newLuaChannel = "";
			if (!StringUtils.isBlank(clientVersion.getCpuType()) && !StringUtils.isBlank(clientVersion.getDeviceModel())) {
				boolean bln64 = DeviceUtil.checkIos32Or64(deviceModel, cpuType);
				if (!bln64) {
					newLuaChannel = luaChannel + "32";
				}
			}
			System.out.println("-----------------------luaChannel" + luaChannel);
			LuaInfo channelLuaInfo = LuaMgr.getInstance().getChannelLuaInfo(newLuaChannel);
			if (channelLuaInfo == null) {
				channelLuaInfo = LuaMgr.getInstance().getChannelLuaInfo(luaChannel);
			}
			List<Version> updateVersionList = versionMgr.getUpdateVersion(clientVersion);
			VersionMgr.logger.error("-------------updateVersion is null:" + updateVersionList.isEmpty());
			
			ChannelAddressInfo channelAddressInfo = VersionDao.getInstance().getChannelAddressInfoByPackageName(clientVersion.getChannel(), clientVersion.getPackageName());

			if (!Boolean.valueOf(channelAddressInfo.getUpdateResSwitch())) {
				updateVersionList.clear();
				Version updateVersion = new Version();
				updateVersion.setPackageName(clientVersion.getPackageName());
				updateVersionList.add(updateVersion);
			} else {
				if (updateVersionList.isEmpty()) {
					Version updateVersion = new Version();
					updateVersion.setPackageName(clientVersion.getPackageName());
					updateVersionList.add(updateVersion);
				}
			}
			for(Version updateVersion : updateVersionList){
				if(channelLuaInfo != null){
					updateVersion.setLuaFileMd5(channelLuaInfo.getFilesmd5());
				}
				updateVersion.setLuaAction("lua");
				updateVersion.setLuaVerifySwitch(Boolean.valueOf(channelAddressInfo.getLuaVerifySwitch()));
			}
			System.out.println("---------------channelLuaInfo.getFilesmd5()" + channelLuaInfo.getFilesmd5());
			String verifyUpdateResult = packVerifyVersionResult2(updateVersionList, clientVersion, channelAddressInfo);
			ServletOutputStream out = response.getOutputStream();
			out.write(verifyUpdateResult.getBytes("UTF-8"));
			out.flush();
			inputStream.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Version getClientVersion(String jsonVersion) {
		if (jsonVersion == null || jsonVersion.length() <= 0) {
			return null;
		}
		Version version = null;
		try {
			version = new Version();
			JSONObject json = new JSONObject(jsonVersion);
			String channel = json.get("channel").toString();
			int main = Integer.parseInt(json.get("main").toString());
			int sub = Integer.parseInt(json.get("sub").toString());
			int third = Integer.parseInt(json.get("third").toString());
			int patch = Integer.parseInt(json.get("patch").toString());
			String cpuType = json.get("cpuType").toString();
			String deviceModel = json.get("deviceModel").toString();
			version = new Version();
			version.setChannel(channel);
			version.setMain(main);
			version.setSub(sub);
			version.setThird(third);
			version.setPatch(patch);
			version.setCpuType(cpuType);
			version.setDeviceModel(deviceModel);
			String packageName = getJsonValue("package", json);
			version.setPackageName(packageName);
			return version;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String getJsonValue(String key,JSONObject json){
		String result = null;
		try{
			result = json.get(key).toString();
		}catch(Exception ex){
			result ="";
		}
		return result;
	}

	private String packVerifyVersionResult(Version updateVersion, Version currentVersion, ChannelAddressInfo channelAddressInfo) {
		JSONObject json = new JSONObject();
		try {
			if (!(updateVersion.getChannel() == null ||  updateVersion.getChannel().equals(""))) {
				if (updateVersion.getPatchInstall().equals(Constant.PATCH_LINK)) {
					packageBrowserLink(updateVersion, json, currentVersion);
				} else {
					packageDownloadInfo(updateVersion, json, channelAddressInfo);
				}
				json.put("patchInstall", updateVersion.getPatchInstall());
			} else {
				json.put("update", 0);
			}
			json.put("luaFileMd5", updateVersion.getLuaFileMd5());
			json.put("luaAction", updateVersion.getLuaAction());
			json.put("luaVerifySwitch", updateVersion.isLuaVerifySwitch());
			json.put("loginServerDomain", channelAddressInfo.getLoginServerDomain());
			json.put("logServerAddress", channelAddressInfo.getLogServerAddress());
			json.put("checkServerURL", channelAddressInfo.getCheckServerURL());
			json.put("checkServerPayURL", channelAddressInfo.getCheckServerPayURL());
			json.put("backUrl", channelAddressInfo.getBackUrl());
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json.toString();
	}
	
	private String packVerifyVersionResult2(List<Version> updateVersions, Version currentVersion, ChannelAddressInfo channelAddressInfo) {
		
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < updateVersions.size(); i++){
			buff.append(packVerifyVersionResult(updateVersions.get(i), currentVersion, channelAddressInfo));
			if(i != updateVersions.size() -1) buff.append("@");
		}
		return buff.toString();
	}

	private void packageBrowserLink(Version updateVersion, JSONObject json, Version currentVersion)
			throws JSONException {
		String currentVersionNo = updateVersion.getCurrentVersionNo();
		String packageName = currentVersion.getPackageName();
		VersionUpdateCfg cfg = VersionUpdateCfgDao.getInstance().getCfgByKey(currentVersionNo);
		if (cfg == null) {
			json.put("update", 0);
		} else {
			json.put("update", 1);
			String forceUpdateTime = cfg.getForceUpdateTime();
			long time = DateTimeUtils.getTime(forceUpdateTime, "yyyy-MM-dd");
			boolean force = false;
			String tips;
			if (time < System.currentTimeMillis()) {
				force = true;
				tips = cfg.getForceTips();
			} else {
				tips = cfg.getUpdateTips();
			}
			json.put("tips", tips);
			json.put("force", force);
			System.out.println("package name:" + packageName);
			ChannelAddressCfg channelAddressCfg = ChannelAddressCfgDao.getInstance().getCfgByKey(packageName);
			String downloadAddress = channelAddressCfg.getDownloadAddress();
			json.put("url", downloadAddress);
			json.put("reward", cfg.getRewards() == null ? "" : cfg.getRewards());
		}
	}

	private void packageDownloadInfo(Version updateVersion, JSONObject json, ChannelAddressInfo channelAddressInfo)
			throws JSONException {
		json.put("update", 1);
		json.put("name", updateVersion.getName() + ".zip");
		json.put("channel", updateVersion.getChannel());
		json.put("main", updateVersion.getMain());
		json.put("sub", updateVersion.getSub());
		json.put("third", updateVersion.getThird());
		json.put("patch", updateVersion.getPatch());
		json.put("cdnDownloadUrl", channelAddressInfo.getCdnDomain()
				+ File.separator + updateVersion.getLocation());
		json.put("cdnBackupDownloadUrl",
				channelAddressInfo.getCdnBackUpDomain() + File.separator
						+ updateVersion.getLocation());
		json.put("md5", updateVersion.getMd5());
		json.put("size", updateVersion.getSize());
	}

	public void setVersionMgr(VersionMgr versionMgr) {
		this.versionMgr = versionMgr;
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
