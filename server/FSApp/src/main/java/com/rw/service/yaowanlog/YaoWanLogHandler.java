package com.rw.service.yaowanlog;

import io.netty.channel.ChannelHandlerContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.util.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.charge.data.ChargeParam;
import com.rw.config.YaoWanLogConfig;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.MD5;
import com.rw.manager.GameManager;
import com.rw.netty.ServerHandler;
import com.rw.netty.UserChannelMgr;
import com.rw.service.log.infoPojo.ClientInfo;

/**
 * @Author HC
 * @date 2016年12月14日 上午9:28:45
 * @desc
 **/

public class YaoWanLogHandler {
	private static YaoWanLogHandler handler = new YaoWanLogHandler();

	public static YaoWanLogHandler getHandler() {
		return handler;
	}

	/**
	 * 发送注册日志到要玩
	 * 
	 * @param player
	 * @param clientInfo
	 */
	public void sendRegisterLogHandler(Player player, ClientInfo clientInfo) {
		YaoWanLogConfig instance = YaoWanLogConfig.getInstance();
		if (!instance.isOpen()) {
			return;
		}

		if (clientInfo == null) {
			return;
		}

		String game_name = instance.getGame_name();

		String userId = player.getUserId();

		long time = DateUtils.getSecondLevelMillis() / 1000;
		String imei = clientInfo.getImei();

		// MD5加密
		StringBuilder sb = new StringBuilder();
		sb.append(game_name).append(time).append(imei).append(userId).append(instance.getSecret_key());
		// 填充参数
		String md5String = MD5.getMD5String(sb.toString());
		if (StringUtils.isEmpty(md5String)) {
			GameLog.error("要玩上传日志", "注册", "上传日志到要玩后台出错，MD5加密出现了空");
			return;
		}

		// 获取客户端的IP信息
		String clientIp = clientInfo.getClientIp();
		if (StringUtils.isEmpty(clientIp)) {
			clientIp = getClientIp(userId);
		}

		if (StringUtils.isEmpty(clientIp)) {
			clientIp = player.getTempAttribute().getIp();
		}

		if (StringUtils.isEmpty(clientIp)) {
			clientIp = "127.0.0.1";
		}

		String format = String.format(instance.getRegister_log_required_param(), game_name, time, clientIp, imei, instance.getDefault_browser_ver(), clientInfo.getSystemVersion(), userId, md5String.toLowerCase());
		sendHttpGet(instance.getRegister_log_url(), instance.getRegister_log_action_param() + format);

		// System.err.println(format);
		// String sendHttpGet = sendHttpGet(instance.getRegister_log_url(), instance.getRegister_log_action_param() + format);
		// System.err.println(sendHttpGet);
	}

	/**
	 * 发送登录日志到要玩
	 * 
	 * @param player
	 * @param clientInfo
	 */
	public void sendLoginLogHandler(Player player, ClientInfo clientInfo) {
		YaoWanLogConfig instance = YaoWanLogConfig.getInstance();
		if (!instance.isOpen()) {
			return;
		}

		if (clientInfo == null) {
			return;
		}

		String game_name = instance.getGame_name();

		String userId = player.getUserId();

		long time = DateUtils.getSecondLevelMillis() / 1000;
		String imei = clientInfo.getImei();

		// MD5加密
		int zoneId = GameManager.getZoneId();
		StringBuilder sb = new StringBuilder();
		sb.append(game_name).append(time).append(imei).append(userId).append(zoneId).append(instance.getSecret_key());
		// 填充参数
		String md5String = MD5.getMD5String(sb.toString());
		if (StringUtils.isEmpty(md5String)) {
			GameLog.error("要玩上传日志", "登录", "上传日志到要玩后台出错，MD5加密出现了空");
			return;
		}

		// 获取客户端的IP信息
		String clientIp = clientInfo.getClientIp();
		if (StringUtils.isEmpty(clientIp)) {
			clientIp = getClientIp(userId);
		}

		if (StringUtils.isEmpty(clientIp)) {
			clientIp = player.getTempAttribute().getIp();
		}

		if (StringUtils.isEmpty(clientIp)) {
			clientIp = "127.0.0.1";
		}

		String format = String.format(instance.getLogin_log_required_param(), game_name, time, clientIp, imei, instance.getDefault_browser_ver(), clientInfo.getSystemVersion(), userId, player.getUserName(), zoneId, md5String.toLowerCase());
		sendHttpGet(instance.getLogin_log_url(), instance.getLogin_log_action_param() + format);
		// System.err.println(format);
		// String sendHttpGet = sendHttpGet(instance.getLogin_log_url(), instance.getLogin_log_action_param() + format);
		// System.err.println(sendHttpGet);
	}

	/**
	 * 发送登录日志到要玩
	 * 
	 * @param player
	 * @param clientInfo
	 * @param orderId
	 * @param money 单位是分
	 */
	public void sendChargeLogHandler(Player player, ChargeParam clientInfo, String orderId, int money) {
		YaoWanLogConfig instance = YaoWanLogConfig.getInstance();
		if (!instance.isOpen()) {
			return;
		}

		if (clientInfo == null) {
			return;
		}

		if (money <= 0) {
			return;
		}

		String game_name = instance.getGame_name();

		String userId = player.getUserId();

		long time = DateUtils.getSecondLevelMillis() / 1000;
		String imei = clientInfo.getImei();

		// MD5加密
		int zoneId = GameManager.getZoneId();
		StringBuilder sb = new StringBuilder();
		sb.append(game_name).append(time).append(imei).append(userId).append(zoneId).append(instance.getSecret_key());
		// 填充参数
		String md5String = MD5.getMD5String(sb.toString());
		if (StringUtils.isEmpty(md5String)) {
			GameLog.error("要玩上传日志", "充值", "上传日志到要玩后台出错，MD5加密出现了空");
			return;
		}

		// 获取客户端的IP信息
		String clientIp = getClientIp(userId);
		if (StringUtils.isEmpty(clientIp)) {
			clientIp = player.getTempAttribute().getIp();
		}

		if (StringUtils.isEmpty(clientIp)) {
			clientIp = "127.0.0.1";
		}

		String format = String.format(instance.getCharge_log_required_param(), game_name, time, clientIp, imei, instance.getDefault_browser_ver(), clientInfo.getSysVer(), orderId, (money / 100), userId, player.getUserName(), zoneId, md5String.toLowerCase());
		sendHttpGet(instance.getCharge_log_url(), instance.getCharge_log_action_param() + format);
		// System.err.println(format);
		// String sendHttpGet = sendHttpGet(instance.getCharge_log_url(), instance.getCharge_log_action_param() + format);
		// System.err.println(sendHttpGet);
	}

	/**
	 * 通过Session获取客户端的IP
	 * 
	 * @param userId
	 * @return
	 */
	private String getClientIp(String userId) {
		Long sessionId = UserChannelMgr.getSessionId(userId);
		if (sessionId == null) {
			return null;
		}

		ChannelHandlerContext ctx = ServerHandler.getChannelHandlerContext(sessionId);
		if (ctx == null) {
			return null;
		}

		try {
			InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
			return address.getHostName();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 发送HttpGet请求
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	private String sendHttpGet(String url, String param) {
		try {
			String urlAddress = StringUtils.isEmpty(param) ? url : (url + "?" + param);
			URL httpURL = new URL(urlAddress);
			URLConnection openConnection = httpURL.openConnection();
			openConnection.connect();

			InputStream inputStream = openConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder sb = new StringBuilder();
			String s = "";
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			return sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}
}