package com.rw.handler.copy.data;

import java.util.HashMap;
import java.util.Map;

import com.config.copyCfg.CopyCfg;
import com.config.copyCfg.CopyCfgDAO;
import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class CopyHolder{
	
	private Map<Integer, Integer> copyTime = new HashMap<Integer, Integer>();
	private SynDataListHolder<CopyLevelRecord> listHolder = new SynDataListHolder<CopyLevelRecord>(CopyLevelRecord.class);
	
	private int currentNormalCopyId = 0;
	private int currentEliteCopyId = 0;
	private int fightingCopyId = 0;
	
	public CopyHolder() { }
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		for(CopyLevelRecord record : listHolder.getItemList()){
			if(record.getPassStar() <= 0) continue;
			CopyCfg cfg = CopyCfgDAO.getInstance().getCfgById(String.valueOf(record.getLevelId()));
			if(cfg == null){
				System.out.println("CopyCfg配置文件不一致[" + record.getLevelId() + "]");
				continue;
			}
			if(cfg.getLevelType() == 0){
				if(record.getLevelId() > currentNormalCopyId){
					currentNormalCopyId = record.getLevelId();
					System.out.println("currentNormalCopyId=====================>>" + currentNormalCopyId);
				}
			}else if(cfg.getLevelType() == 1){
				if(record.getLevelId() > currentEliteCopyId){
					currentEliteCopyId = record.getLevelId();
					System.out.println("currentEliteCopyId=====================>>" + currentEliteCopyId);
				}
			}
		}
	}

	public Map<Integer, Integer> getCopyTime() {
		return copyTime;
	}

	public void setCopyTime(Map<Integer, Integer> copyTime) {
		this.copyTime = copyTime;
	}

	public int getCurrentNormalCopyId() {
		return currentNormalCopyId;
	}

	public int getCurrentEliteCopyId() {
		return currentEliteCopyId;
	}

	public int getFightingCopyId() {
		return fightingCopyId;
	}

	public void setFightingCopyId(int fightingCopyId) {
		this.fightingCopyId = fightingCopyId;
	}
}
