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
	
	private String patchInstall = "0";
	
	private String packageName = "";
	
	private String luaFileMd5 = "";
	
	private String luaAction;

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
	
	public boolean targetIsVerPatch(Version target) {
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && this.sub == target.sub && this.third == target.third
				&& target.patch > 0;
	}
	
	public boolean targetIsVerCodePatch(Version target){
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && this.sub == target.sub && target.third > 0
				&& this.patch == target.patch;
	}

	// 全量包版本比较
	public boolean isSameCompVer(Version target) {
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && this.sub == target.sub /**&& this.third == target.third*/;
	}
	
	public boolean isMainVer(){
		return this.main > 0 && this.sub == 0 && this.third == 0 && this.patch == 0;
	}
	
	//是否最新版本
	public boolean isLatestCompVer(Version target){
		return StringUtils.equals(this.channel, target.channel) && this.main > target.main /**&& this.sub >= target.sub && this.third >= target.third*/;
	}

	// patch比较
	public boolean isSamePatch(Version target) {
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && this.sub == target.sub /**&& this.third == target.third*/
				&& this.patch == target.patch;
	}
	
	// 找不到对应的version的时候，就找到
	public boolean isBigPath(Version target){
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && this.sub == target.sub /**&& this.third == target.third*/
				&& this.patch > target.patch;
	}
	
	//版本相同
	public boolean isSameCodePath(Version target){
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && this.sub == target.sub && this.third == target.third
				/**&& this.patch == target.patch*/;
	}
	
	//版本更新
	public boolean isBigCodePath(Version target){
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && this.sub == target.sub && this.third > target.third
				/**&& this.patch == target.patch*/;
	}
	
	public boolean isSamePath(Version target){
		return StringUtils.equals(this.channel, target.channel) && this.main == target.main && this.sub == target.sub && this.third == target.third
				&& this.patch == target.patch;
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
