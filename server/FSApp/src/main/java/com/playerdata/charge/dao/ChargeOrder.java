package com.playerdata.charge.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.chargeServer.ChargeContentPojo;


/***
 * 充值订单记录
 * @author Allen
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargeOrder implements Comparable<ChargeOrder>{
	

	//订单号
	private String cpTradeNo;
	//接收时间
	private long receiveTime;
	public String getCpTradeNo() {
		return cpTradeNo;
	}
	public void setCpTradeNo(String cpTradeNo) {
		this.cpTradeNo = cpTradeNo;
	}
	public long getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(long receiveTime) {
		this.receiveTime = receiveTime;
	}
	@Override
	public int compareTo(ChargeOrder target) {		
		return receiveTime>target.getReceiveTime()?1:-1;
	}
	
	public static ChargeOrder fromReq(ChargeContentPojo contentPojo){
		ChargeOrder chargeOrder = new ChargeOrder();
		chargeOrder.setCpTradeNo(contentPojo.getCpTradeNo());
		chargeOrder.setReceiveTime(System.currentTimeMillis());
		return chargeOrder;
	}
	
	public static void main(String[] args) {
		List<ChargeOrder> orderList = new ArrayList<ChargeOrder>();
		ChargeOrder chargeOrderA = new ChargeOrder();
		chargeOrderA.setReceiveTime(1L);
		ChargeOrder chargeOrderB = new ChargeOrder();
		chargeOrderB.setReceiveTime(2L);
		ChargeOrder chargeOrderC = new ChargeOrder();
		chargeOrderC.setReceiveTime(3L);
		orderList.add(chargeOrderB);
		orderList.add(chargeOrderA);
		orderList.add(chargeOrderC);
		
		Collections.sort(orderList);
//		for (ChargeOrder chargeOrder : orderList) {
//			System.out.println(chargeOrder.getReceiveTime());
//		}
		
		
		ChargeOrder chargeOrderD = new ChargeOrder();
		chargeOrderD.setReceiveTime(4L);
		orderList.remove(0);
		orderList.add(0, chargeOrderD);
		
	
		Collections.sort(orderList);
		for (ChargeOrder chargeOrder : orderList) {
			System.out.println(chargeOrder.getReceiveTime());
		}
	}

	
	
	
}
