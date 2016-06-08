package com.bm.groupSecret;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;

public class GroupSecretHelper {

	public static String newSecretId(Player player, GroupSecretType groupSecretType) {
		return player.getUserId() + "_" + groupSecretType;
	}

	public static GroupSecretType parseType(String secretId) {

		String typeStr = StringUtils.substringAfter(secretId, "_");

		GroupSecretType type = GroupSecretType.valueOf(typeStr);

		return type;
	}

	public static void main(String[] args) {
		String gold = GroupSecretType.Gold.toString();
		System.out.println(gold);

		GroupSecretType type = GroupSecretType.valueOf(gold);
		System.out.println(type);
	}
}