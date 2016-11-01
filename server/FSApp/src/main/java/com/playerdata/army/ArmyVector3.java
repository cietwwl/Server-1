package com.playerdata.army;

import com.playerdata.dataSyn.annotation.SynClass;


/**
 * 自定义的三维坐标
 * @author Alex
 *
 * 2016年9月29日 下午5:09:05
 */
@SynClass
public class ArmyVector3 {

	
	float x;
	float y;
	float z;
	
	public ArmyVector3(){}
	
	public ArmyVector3(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getZ() {
		return z;
	}
	public void setZ(float z) {
		this.z = z;
	}
	
	
	
}
