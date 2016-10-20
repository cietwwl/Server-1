package com.bm.saloon;

import java.util.HashMap;
import java.util.Map;

import com.bm.saloon.impl.comImpl.SaloonBmComImpl;

public class SaloonBmFactory {
	
	private static SaloonBmFactory instance = new SaloonBmFactory();
	public static SaloonBmFactory getInstance(){
		return instance;
	}

	private Map<SaloonType, ISaloonBm> saloonBmMap = new HashMap<SaloonType, ISaloonBm>();
	
	public SaloonBmFactory(){
		saloonBmMap.put(SaloonType.WorldBoss, new SaloonBmComImpl());
	}
	
	public void update(){
		for (ISaloonBm saloonBmTmp : saloonBmMap.values()) {
			saloonBmTmp.update();
		}
	}
	
	public ISaloonBm get(SaloonType saloonType){
		return saloonBmMap.get(saloonType);
	}
	
	
}
