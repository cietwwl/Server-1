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

import com.fy.json.JSONObject;
import com.fy.version.Version;
import com.fy.version.VersionMgr;
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

			Version updateVersion = versionMgr.getUpdateVersion(clientVersion);
			VersionMgr.logger.error("-------------updateVersion is null:" + updateVersion == null);
			if(updateVersion == null){
				Version maxVersion = versionMgr.getMaxVersion(clientVersion);
				updateVersion = new Version();
				updateVersion.setLoginServerDomain(maxVersion.getLoginServerDomain());
				updateVersion.setLogServerAddress(maxVersion.getLogServerAddress());
			}
			String verifyUpdateResult = packVerifyVersionResult(updateVersion);
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
			JSONObject json = new JSONObject(jsonVersion);
			String channel = json.get("channel").toString();
			int main = Integer.parseInt(json.get("main").toString());
			int sub = Integer.parseInt(json.get("sub").toString());
			int third = Integer.parseInt(json.get("third").toString());
			int patch = Integer.parseInt(json.get("patch").toString());
			version = new Version();
			version.setChannel(channel);
			version.setMain(main);
			version.setSub(sub);
			version.setThird(third);
			version.setPatch(patch);
			return version;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private String packVerifyVersionResult(Version updateVersion) {
		JSONObject json = new JSONObject();
		try {
			if (!(updateVersion.getChannel() == null ||  updateVersion.getChannel().equals(""))) {
				json.put("update", 1);
				json.put("name", updateVersion.getName()+".zip");
				json.put("channel", updateVersion.getChannel());
				json.put("main", updateVersion.getMain());
				json.put("sub", updateVersion.getSub());
				json.put("third", updateVersion.getThird());
				json.put("patch", updateVersion.getPatch());
				json.put("cdnDownloadUrl", updateVersion.getCdnDomain()
						+ File.separator + updateVersion.getLocation());
				json.put("cdnBackupDownloadUrl",
						updateVersion.getCdnBackUpDomain() + File.separator
								+ updateVersion.getLocation());
				json.put("md5", updateVersion.getMd5());
				json.put("size", updateVersion.getSize());
				json.put("patchInstall", updateVersion.getPatchInstall());
			} else {
				json.put("update", 0);
			}
			json.put("loginServerDomain", updateVersion.getLoginServerDomain());
			json.put("logServerAddress", updateVersion.getLogServerAddress());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json.toString();
	}

	private Version getClientVersion() {

		return new Version();
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
