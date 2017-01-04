package com.fy.version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 更新优先级
 * 整包更新>资源更新>代码更新
 * @author lida
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Version {

	// name=chanel_v.*.*.*_patch(0 完整包, >1 patch)
	private String name;

	private String location;

	private int main; // 主版本号(整包更新)

	private int sub; // 次版本号

	private int third; // 代码更新，third和patch必须有一个为0

	private String channel;

	private int patch;//资源更新，third和patch必须有一个为0
	
	private String priority = "0";//是否需要立即强制重启后再继续（0表示不需要）

	private String md5;

	private String size;

	private String loginServerDomain;

	private String cdnDomain;

	private String cdnBackUpDomain;
	
	private String logServerAddress;
	
	/****************************用于Ios***************/
	private String checkServerURL;   	//审核服地址
	
	private String checkServerPayURL;  	//审核服支付地址
	
	private String backUrl;      		//审核时记得填写，否则有可能在审核过程中请求到正式服
	/****************************用于Ios***************/
	
	private String patchInstall = "0";
	
	private String packageName = "";
	
	private String luaFileMd5 = "";
	
	private String luaAction;
	
	private String cpuType;
	
	private String deviceModel;
	
	private boolean luaVerifySwitch = true;     //lua校验开关
	
	private boolean updateResSwitch = true;     //资源开关

	public String getLuaAction() {
		return luaAction;
	}

	public void setLuaAction(String luaAction) {
		this.luaAction = luaAction;
	}

	public String getLuaFileMd5() {
		return luaFileMd5;
	}

	public void setLuaFileMd5(String luaFileMd5) {
		this.luaFileMd5 = luaFileMd5;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setLogServerAddress(String logServerAddress) {
		this.logServerAddress = logServerAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getMain() {
		return main;
	}

	public void setMain(int main) {
		this.main = main;
	}

	public int getSub() {
		return sub;
	}

	public void setSub(int sub) {
		this.sub = sub;
	}

	public int getThird() {
		return third;
	}

	public void setThird(int third) {
		this.third = third;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getPatch() {
		return patch;
	}

	public void setPatch(int patch) {
		this.patch = patch;
	}

	public long getSize() {
		return Long.parseLong(size);
	}

	public void setSize(long size) {
		this.size = String.valueOf(size);
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getCdnDomain() {
		return cdnDomain;
	}

	public void setCdnDomain(String cdnDomain) {
		this.cdnDomain = cdnDomain;
	}

	public String getCdnBackUpDomain() {
		return cdnBackUpDomain;
	}

	public void setCdnBackUpDomain(String cdnBackUpDomain) {
		this.cdnBackUpDomain = cdnBackUpDomain;
	}

	public String getLoginServerDomain() {
		return loginServerDomain;
	}

	public String getLogServerAddress() {
		return logServerAddress;
	}

	public void setLoginServerDomain(String loginServerDomain) {
		this.loginServerDomain = loginServerDomain;
	}

	public String getPatchInstall() {
		return patchInstall;
	}

	public void setPatchInstall(String patchInstall) {
		this.patchInstall = patchInstall;
	}

	public int getPriority() {
		return Integer.parseInt(priority);
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getCpuType() {
		return cpuType;
	}

	public void setCpuType(String cpuType) {
		this.cpuType = cpuType;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	
	public boolean isLuaVerifySwitch() {
		return luaVerifySwitch;
	}

	public void setLuaVerifySwitch(boolean luaVerifySwitch) {
		this.luaVerifySwitch = luaVerifySwitch;
	}

	public boolean isUpdateResSwitch() {
		return updateResSwitch;
	}

	public void setUpdateResSwitch(boolean updateResSwitch) {
		this.updateResSwitch = updateResSwitch;
	}

	public String getCheckServerURL() {
		return checkServerURL;
	}

	public void setCheckServerURL(String checkServerURL) {
		this.checkServerURL = checkServerURL;
	}

	public String getCheckServerPayURL() {
		return checkServerPayURL;
	}

	public void setCheckServerPayURL(String checkServerPayURL) {
		this.checkServerPayURL = checkServerPayURL;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	/**
	 * 是否是整合的资源包
	 * 
	 * <note>channel相同，main相同，sub>0即可</note>
	 * @param target
	 * @return
	 */
	public boolean targetIsTotalPatch(Version target) {
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && target.sub > 0;
	}
	
	/**
	 * 是否是单个资源补丁包（目标全量包下的）
	 * @param target
	 * @return
	 */
	public boolean targetIsVerPatch(Version target) {
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && this.sub == target.sub && this.third == target.third
				&& target.patch > 0;
	}
	
	/**
	 * 是否是单个代码补丁包（目标全量包下的）
	 * @param target
	 * @return
	 */
	public boolean targetIsVerCodePatch(Version target){
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && this.sub == target.sub && target.third > 0
				&& this.patch == target.patch;
	}

	/**
	 * 是否是同一个全量包
	 * @param target
	 * @return
	 */
	public boolean isSameCompVer(Version target) {
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main /**&& this.sub == target.sub && this.third == target.third*/;
	}
	
	/**
	 * 是否是全量包
	 * @return
	 */
	public boolean isMainVer(){
		return this.main > 0 && this.sub == 0 && this.third == 0 && this.patch == 0;
	}
	
	/**
	 * 是否是更加新的全量包
	 * @param target
	 * @return
	 */
	public boolean isNewerCompVer(Version target){
		return StringUtils.equals(this.channel, target.channel) && this.main > target.main /**&& this.sub >= target.sub && this.third >= target.third*/;
	}
	
	/**
	 * 是否是更新的资源包
	 * @param target
	 * @return
	 */
	public boolean isBigPatch(Version target){
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main /**&& this.sub == target.sub && this.third == target.third*/
				&& this.patch > target.patch;
	}
	
	/**
	 * 是否是相同的代码补丁包
	 * @param target
	 * @return
	 */
	public boolean isSameCodePath(Version target){
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && this.third == target.third;
	}
	
	/**
	 * 是否是更加新代码补丁包
	 * @param target
	 * @return
	 */
	public boolean isBigCodePath(Version target){
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && this.third > target.third;
	}
	
	public String getCurrentVersionNo() {
		return this.main + "." + this.sub + "." + this.third + "_" + this.patch;
	}

	private static Field[] fields = null;

	// name=chanel_v.*.*.*_patch(0 完整包, >1 patch)
	public static Version fromFile(File file) throws IOException, Exception {

		try {
			if (fields == null) {
				Field[] declaredFields = Version.class.getDeclaredFields();
				for (Field field : declaredFields) {
					field.setAccessible(true);
				}
				fields = declaredFields;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}

		BufferedReader reader = new BufferedReader(new FileReader(file));
		Version version = new Version();
		try {

			Map<String, String> propMap = new HashMap<String, String>();
			String line = reader.readLine();
			while (line != null && !line.equals("")) {
				String[] split = line.split("=");
				if (split.length == 2) {
					propMap.put(split[0].trim(), split[1].trim());
				}
				line = reader.readLine();
			}
			for (Field fieldTmp : fields) {
				if (fieldTmp.getName().equals("name")) {
					String versionline = propMap.get("name");
					handelVersionLine(version, versionline);
				} else {
					String value = propMap.get(fieldTmp.getName());
					if (value != null) {
						fieldTmp.set(version, value);
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		} finally {
			reader.close();
		}
		
		return version;

	}

	private static Version handelVersionLine(Version version, String versionline) {
		String[] versionSplit = versionline.trim().split("_");
		if (versionSplit.length == 3) {
			version.setName(versionline);
			String channel = versionSplit[0];
			version.setChannel(channel);
			String verTmp = versionSplit[1];

			setVersionInfo(version, verTmp);

			String patch = versionSplit[2];
			version.setPatch(Integer.valueOf(patch));

		}
		return version;
	}

	private static void setVersionInfo(Version version, String verTmp) {
		System.out.println();
		String[] verTmpSplit = verTmp.split("\\.");

		String main = verTmpSplit[1];
		String sub = verTmpSplit[2];
		String third = verTmpSplit[3];

		version.setMain(Integer.valueOf(main));
		version.setSub(Integer.valueOf(sub));
		version.setThird(Integer.valueOf(third));
	}
}
