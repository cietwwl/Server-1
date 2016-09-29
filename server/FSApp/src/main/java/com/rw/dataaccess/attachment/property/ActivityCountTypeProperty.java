package com.rw.dataaccess.attachment.property;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityCountTypeProperty {

//	private Integer id;
//	
//	@Override
//	public Integer getId() {
//		
//		// TODO Auto-generated method stub
//		return id;
//	}
//	
//	public void get(String userId,int cfgId) {
//		
//		String key = userId + "_"+cfgId;
//		
//		RoleExtPropertyStoreCache<ActivityCountTypeProperty> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_COUNTTYPE, ActivityCountTypeProperty.class);
//		try {
//			PlayerExtPropertyStore<ActivityCountTypeProperty> store= storeCache.getAttachmentStore(userId);
//			ActivityCountTypeProperty type = store.get(cfgId);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//private String userId;// 对应的角色Id
//	
//	@CombineSave
//	private int count;
//
//	@CombineSave
//	private String cfgId;
//	
//	@CombineSave
//	private boolean closed = false;
//
//	
//	@CombineSave
//	private List<ActivityCountTypeSubItem> subItemList = new ArrayList<ActivityCountTypeSubItem>();
//	
//	
//	@CombineSave
//	private String version ;
//	
//	@CombineSave
//	private long redPointLastTime;
//	
//	@CombineSave
//	private String enumId;
//	
//	
//	
//	
//	
//	public String getEnumId() {
//		return enumId;
//	}
//
//	public void setEnumId(String enumId) {
//		this.enumId = enumId;
//	}
//
//	public long getRedPointLastTime() {
//		return redPointLastTime;
//	}
//
//	public void setRedPointLastTime(long redPointLastTime) {
//		this.redPointLastTime = redPointLastTime;
//	}
//	
//	@CombineSave
//	private boolean isTouchRedPoint;	
//
//	public boolean isTouchRedPoint() {
//		return isTouchRedPoint;
//	}
//
//	public void setTouchRedPoint(boolean isTouchRedPoint) {
//		this.isTouchRedPoint = isTouchRedPoint;
//	}
//
//	public void reset(ActivityCountTypeCfg cfg,List<ActivityCountTypeSubItem> sublist){
//		cfgId = cfg.getId();
//		closed = false;
//		count=0;
//		version = cfg.getVersion();
//		subItemList = sublist;
//		isTouchRedPoint = false;
//	}
//
//	public String getVersion() {
//		return version;
//	}
//
//	public void setVersion(String version) {
//		this.version = version;
//	}
//
//
//	//重置活动
//	public void reset(){
//		subItemList = new ArrayList<ActivityCountTypeSubItem>();
//		count = 0;
//	}
//
//
//	public void setId(Integer id) {
//		this.id = id;
//	}
//
//	public int getCount() {
//		return count;
//	}
//
//	public void setCount(int count) {
//		this.count = count;
//	}
//
//	public List<ActivityCountTypeSubItem> getSubItemList() {
//		return subItemList;
//	}
//
//	public void setSubItemList(List<ActivityCountTypeSubItem> subItemList) {
//		this.subItemList = subItemList;
//	}
//
//	public String getUserId() {
//		return userId;
//	}
//
//	public void setUserId(String userId) {
//		this.userId = userId;
//	}
//
//	public String getCfgId() {
//		return cfgId;
//	}
//
//	public void setCfgId(String cfgId) {
//		this.cfgId = cfgId;
//	}
//
//	public boolean isClosed() {
//		return closed;
//	}
//
//	public void setClosed(boolean closed) {
//		this.closed = closed;
//	}
//
//	

	

}
