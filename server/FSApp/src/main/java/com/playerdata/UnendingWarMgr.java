package com.playerdata;



import java.util.HashMap;
import java.util.Map;





import com.rwbase.dao.unendingwar.TableUnendingWar;
import com.rwbase.dao.unendingwar.UnendingWarDAO;


public class UnendingWarMgr{
	private UnendingWarDAO dao = UnendingWarDAO.getInstance();
	private TableUnendingWar _table=null;

	protected Map<Integer, Integer> m_SyncDataList = new HashMap<Integer, Integer>();
	protected Player m_pPlayer = null;
	
	public void init(Player pRole) {
		m_pPlayer = pRole;
	
	}
	
	/***数据库表*****/
	public TableUnendingWar getTable() {
		
		if (_table == null) {
			_table = dao.get(m_pPlayer.getUserId());
			if (_table == null)
			{
				_table=new TableUnendingWar();
				_table.setUserId(m_pPlayer.getUserId());
				_table.setNum(0);
				save();
			}
		
		}
		
		return _table;
	}
	
	public boolean save() {
		if(_table==null)
		{
			return false;
		}
		return dao.update(_table);
	}
	
	private void sendData() {
		//MsgVipResponse.Builder respone = MsgVipResponse.newBuilder();
		//tagPrivilege.Builder vo;
//		for (Iterator<Entry<Integer, Integer>> iter = m_SyncDataList.entrySet().iterator(); iter.hasNext();) {
//			Map.Entry<Integer, Integer> entry = iter.next();
//			vo = tagPrivilege.newBuilder();
//			vo.setKey(entry.getKey());
//			vo.setValue(entry.getValue());
//			respone.addPrivilege(vo);
//		}
		//m_pPlayer.SendMsg(Command.MSG_VIP, respone.build().toByteString());
		
	}
	public void syncPrivilege() {
		
		//putSync(EPrivilegeDef.BUY_SKILL_POINT_OPEN,vipTable.getBuySkillPointOpen());
	
		sendData();
	}
	

	/***每日刷新*****/
	public void refreshConst() {
	    this.getTable().setNum(0);
	    this.getTable().setResetNum(0);
	    save();
	}


	
}