package com.rwbase.dao.anglearray;

/*
 * @author HC
 * @date 2016年4月20日 下午8:34:18
 * @Description 
 */
public class AngelArrayUtils {

	/**
	 * 拼接一下万仙阵层记录的Id
	 * 
	 * @param userId
	 * @param floor
	 * @return
	 */
	public static String getAngelArrayFloorDataId(String userId, int floor) {
		return new StringBuilder().append(userId).append("_").append(floor).toString();
	}
}