package com.rwbase.dao.groupSkill.db;

public class GroupSkillItemHolder {

	// final private String goupId;
	// final private MapItemStore<GroupSkillItem> itemStore;
	// final private eSynType synType = eSynType.FASHION_ITEM;
	//
	// public GroupSkillItemHolder(String groupIdP) {
	// goupId = groupIdP;
	// itemStore = new MapItemStore<GroupSkillItem>("groupId", goupId, GroupSkillItem.class);
	// }
	//
	// /*
	// * 获取用户已经拥有的时装
	// */
	// public List<GroupSkillItem> getItemList()
	// {
	//
	// List<GroupSkillItem> itemList = new ArrayList<GroupSkillItem>();
	// Enumeration<GroupSkillItem> mapEnum = itemStore.getEnum();
	// while (mapEnum.hasMoreElements()) {
	// GroupSkillItem item = (GroupSkillItem) mapEnum.nextElement();
	// itemList.add(item);
	// }
	//
	// return itemList;
	// }
	//
	// public void updateItem(Player player, GroupSkillItem item){
	// itemStore.updateItem(item);
	// ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	// }
	//
	// public GroupSkillItem getItem(String itemId){
	// return itemStore.getItem(itemId);
	// }
	//
	// public boolean removeItem(Player player, GroupSkillItem item){
	//
	// boolean success = itemStore.removeItem(item.getId());
	// if(success){
	// ClientDataSynMgr.updateData(player, item, synType, eSynOpType.REMOVE_SINGLE);
	// }
	// return success;
	// }
	//
	// public boolean addItem(Player player, GroupSkillItem item){
	//
	// boolean addSuccess = itemStore.addItem(item);
	// if(addSuccess){
	// ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
	// }
	// return addSuccess;
	// }
	//
	// public void synAllData(Player player, int version){
	// List<GroupSkillItem> itemList = getItemList();
	// ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	// }
	//
	//
	// public void flush(){
	// itemStore.flush();
	// }
}
