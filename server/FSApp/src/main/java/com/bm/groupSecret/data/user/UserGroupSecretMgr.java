package com.bm.groupSecret.data.user;

import java.util.ArrayList;
import java.util.List;

import com.bm.groupSecret.GroupSecretHelper;
import com.bm.groupSecret.GroupSecretType;
import com.bm.groupSecret.data.group.GroupSecretDataHolder;
import com.bm.groupSecret.data.user.UserGroupSecretData;
import com.bm.groupSecret.data.user.UserGroupSecretDataHolder;
import com.playerdata.Player;

public class UserGroupSecretMgr {

	private static UserGroupSecretMgr instance = new UserGroupSecretMgr();

	public static UserGroupSecretMgr getInstance() {
		return instance;
	}

	public List<String> getUserSecretIds(Player player) {

		UserGroupSecretData userGroupSecretData = UserGroupSecretDataHolder.getInstance().get(player);
		List<String> joinSecretIdList = userGroupSecretData.getJoinSecretIdList();
		List<String> owenSecretIdList = userGroupSecretData.getOwenSecretIdList();

		List<String> secretIdList = new ArrayList<String>();
		secretIdList.addAll(owenSecretIdList);
		secretIdList.addAll(joinSecretIdList);

		return secretIdList;

	}

	public void addOwnenSecretId(Player player, String secretId) {
		UserGroupSecretData userGroupSecretData = UserGroupSecretDataHolder.getInstance().get(player);
		List<String> owenSecretIdList = userGroupSecretData.getOwenSecretIdList();
		owenSecretIdList.add(secretId);
		UserGroupSecretDataHolder.getInstance().update(player);
	}

	public void removeOwnenSecretId(Player player, String secretId) {
		UserGroupSecretData userGroupSecretData = UserGroupSecretDataHolder.getInstance().get(player);
		List<String> owenSecretIdList = userGroupSecretData.getOwenSecretIdList();
		owenSecretIdList.remove(secretId);
		UserGroupSecretDataHolder.getInstance().update(player);
	}

	public void addJoinSecretId(Player player, String secretId) {
		UserGroupSecretData userGroupSecretData = UserGroupSecretDataHolder.getInstance().get(player);
		List<String> joinSecretIdList = userGroupSecretData.getJoinSecretIdList();
		joinSecretIdList.add(secretId);
		UserGroupSecretDataHolder.getInstance().update(player);
	}

	public void removeJoinSecretId(Player player, String secretId) {
		UserGroupSecretData userGroupSecretData = UserGroupSecretDataHolder.getInstance().get(player);
		List<String> joinSecretIdList = userGroupSecretData.getJoinSecretIdList();
		joinSecretIdList.remove(secretId);
		UserGroupSecretDataHolder.getInstance().update(player);
	}

	public List<GroupSecretType> getUserSecretTypes(Player player) {
		List<GroupSecretType> typeList = new ArrayList<GroupSecretType>();
		List<String> userSecretIds = getUserSecretIds(player);
		for (String secretId : userSecretIds) {
			GroupSecretType parseType = GroupSecretHelper.parseType(secretId);
			typeList.add(parseType);
		}
		return typeList;
	}

	public void synUserSecrets(Player player) {
		List<String> secretIdList = getUserSecretIds(player);
		GroupSecretDataHolder.getInstance().synList(player, secretIdList);
	}

	public void synSingleSecret(Player player, String secretId) {
		GroupSecretDataHolder.getInstance().synSingle(player, secretId);
	}
}