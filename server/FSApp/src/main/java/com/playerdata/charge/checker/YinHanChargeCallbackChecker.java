package com.playerdata.charge.checker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.log.GameLog;
import com.playerdata.charge.IChargeCallbackChecker;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.ChargeCfgDao;
import com.playerdata.charge.dao.ChargeRecord;
import com.playerdata.charge.util.MD5Encrypt;
import com.rw.chargeServer.ChargeContentPojo;
import com.rw.fsutil.util.jackson.JsonUtil;

public class YinHanChargeCallbackChecker implements IChargeCallbackChecker<ChargeContentPojo> {

	public static final String appID = "1012"; // 银汉的AppId
	public static final String appKey = "6489CD1B7E9AE5BD8311435"; // 银汉的AppKey

	private void appendField(StringBuilder strBld, String value) {
		if (value == null || value.length() == 0) {
			value = "";
		}
		strBld.append(value).append("|");
	}

	@Override
	public boolean checkChargeCallback(ChargeContentPojo content) {
		/*
		 * sign=md5(cpTradeNo|gameId|userId|roleId|serverId|channelId|itemId|itemAmount|privateField|money|status|privateKey) 字段如果为null则用空的字符串代替
		 */
		if (content.getSign() == null) {
			GameLog.error("YinHanChargeCallbackChecker", content.getRoleId(), "订单签名为空！订单号：" + content.getCpTradeNo());
			return false;
		}
		StringBuilder strBld = new StringBuilder();
		this.appendField(strBld, content.getCpTradeNo());
		this.appendField(strBld, appID);
		this.appendField(strBld, content.getUserId());
		this.appendField(strBld, content.getRoleId());
		this.appendField(strBld, String.valueOf(content.getServerId()));
		this.appendField(strBld, content.getChannelId());
		this.appendField(strBld, content.getItemId());
		this.appendField(strBld, String.valueOf(content.getItemAmount()));
		this.appendField(strBld, content.getPrivateField());
		this.appendField(strBld, String.valueOf(content.getMoney()));
		this.appendField(strBld, content.getStatus());
		strBld.append(appKey);
		String sign = MD5Encrypt.MD5Encode(strBld.toString()).toLowerCase();
		if (content.getSign().equals(sign)) {
			return true;
		} else {
			// GameLog.error("YinHanChargeCallbackChecker", content.getRoleId(), "签名匹配！订单号：" + content.getCpTradeNo() + "，订单签名：" + content.getSign() + "，本地生成签名：" + sign + "，签名原串：" +
			// strBld.toString());
			System.err.println("签名匹配！订单号：" + content.getCpTradeNo() + "，订单签名：" + content.getSign() + "，本地生成签名：" + sign + "，签名原串：" + strBld.toString());
			return false;
		}
	}

	@Override
	public ChargeRecord generateChargeRecord(ChargeContentPojo content) {
		ChargeRecord record = new ChargeRecord();
		record.setUserId(content.getRoleId());
		record.setSdkUserId(content.getUserId());
		record.setTradeNo(content.getCpTradeNo());
		int money = content.getMoney();
		if (money != -1) { // IOS版本的money会是-1
			record.setMoney(money);
		} else {
			money = content.getItemAmount();
			if (money > 0) {
				record.setMoney(content.getItemAmount() * 10); // itemAmount是钻石，记录是分，所以需要乘以10
			} else {
				ChargeCfg cfg = ChargeCfgDao.getInstance().getCfgById(content.getItemId());
				if (cfg != null) {
					record.setMoney(cfg.getMoneyCount());
				}
			}
		}
		record.setCurrencyType(content.getCurrencyType());
		record.setChannelId(content.getChannelId());
		String itemId;
		if ((itemId = content.getItemId()) != null && itemId.length() > 0) {
			record.setItemId(itemId);
		} else if ((itemId = content.getPrivateField()) != null && itemId.length() > 0) {
			record.setItemId(itemId.split(",")[0]);
		} else {
			record.setItemId("");
		}
		record.setChargeTime(System.currentTimeMillis());
		return record;
	}

	public static void main(String[] args) throws IOException {
		YinHanChargeCallbackChecker checker = new YinHanChargeCallbackChecker();
		BufferedReader br = new BufferedReader(new FileReader(new File("D:\\work\\chargeOrder.txt")));
		String line;
		while ((line = br.readLine()) != null) {
			ChargeContentPojo pojo = JsonUtil.readValue(line, ChargeContentPojo.class);
			boolean value = checker.checkChargeCallback(pojo);
			System.out.println(pojo.getCpTradeNo() + ":" + value);
		}
		br.close();
	}
}
