package com.dx.gods.service.fileupload;

/**
 * 资源对象
 * @author lida
 *
 */
public class ResourceVersion {

	private String mainVersionNo;  		//大版本号
	private String subVersionNo;   		//小版本号 也是资源文件名
	private long fileSize;         		//文件大小
	private String md5Value;       		//文件的md5值
	private String fileName;       		//文件名称
	private int ResourceType;      		//版本类型
	private String lastMainVersionNo = "-1";  	//上一版本大版本号
	private String lastSubVersionNo = "-1";	//上一版本小版本号
	
	public ResourceVersion(String mainVersionNo, String subVersionNo, long fileSize, String md5Value, String fileName, int type){
		this.mainVersionNo = mainVersionNo;
		this.subVersionNo = subVersionNo;
		this.fileSize = fileSize;
		this.md5Value = md5Value;
		this.fileName = fileName;
		this.ResourceType = type;
	}

	public String getFileName() {
		return fileName;
	}

	public String getMainVersionNo() {
		return mainVersionNo;
	}

	public String getSubVersionNo() {
		return subVersionNo;
	}

	public long getFileSize() {
		return fileSize;
	}

	public String getMd5Value() {
		return md5Value;
	}

	public int getResourceType() {
		return ResourceType;
	}

	public void setMainVersionNo(String mainVersionNo) {
		this.mainVersionNo = mainVersionNo;
	}

	public void setSubVersionNo(String subVersionNo) {
		this.subVersionNo = subVersionNo;
	}

	public String getLastMainVersionNo() {
		return lastMainVersionNo;
	}

	public void setLastMainVersionNo(String lastMainVersionNo) {
		this.lastMainVersionNo = lastMainVersionNo;
	}

	public String getLastSubVersionNo() {
		return lastSubVersionNo;
	}

	public void setLastSubVersionNo(String lastSubVersionNo) {
		this.lastSubVersionNo = lastSubVersionNo;
	}
	
}
