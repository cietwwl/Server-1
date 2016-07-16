package com.rw.service.role;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.bm.arena.ArenaBM;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.dao.user.CfgChangeRoleInfoDAO;
import com.rwbase.dao.user.pojo.ChangeRoleInfoCfg;
import com.rwproto.RoleServiceProtos.RoleRequest;
import com.rwproto.RoleServiceProtos.RoleRequestType;
import com.rwproto.RoleServiceProtos.RoleResponse;
import com.rwproto.RoleServiceProtos.RoleResponse.Builder;
import com.rwproto.RoleServiceProtos.RoleResultType;
import com.rwproto.RoleServiceProtos.TagAttr;

public class RoleHandler {
	private static RoleHandler instance = new RoleHandler();
	private static boolean isAdvance;
	private HashMap<Integer, Double> m_Attr;

	private RoleHandler() {
	}

	public static RoleHandler getInstance() {
		return instance;
	}

	public ByteString selectCareer(RoleRequest req, Player player) {
		Builder response = RoleResponse.newBuilder();
		response.setType(RoleRequestType.SELECT_CAREER);
		RoleResultType result = RoleResultType.FAIL;

		int careerType = req.getCareerType();
		String reson = "";
		if (req.getCareerType() >= 1 && req.getCareerType() <= 4) {
			player.SetCareer(careerType);
			player.onCareerChange(careerType,player.getSex());
			player.getUserGameDataMgr().setRookieFlag(2);
			// 任务
			player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Transfer);
			result = RoleResultType.SUCCESS;
			player.getUserGameDataMgr().setCarrerChangeTime();
		} else {
			reson = "参数错误";
		}

		return response.setResultReason(reson).setResult(result).build()
				.toByteString();
	}

	public ByteString changeInfo(RoleRequest req, Player player) {
		ByteString result;
		Builder response = RoleResponse.newBuilder();
		response.setType(RoleRequestType.CHANGE_ROLE);
		RoleResultType resultType = RoleResultType.FAIL;
		result = response.setResultReason("参数错误").setResult(resultType).build()
				.toByteString();

		long totalMin = (player.getUserGameDataMgr().getLastChangeInfoTime() - new Date().getTime()) / (1000 * 60);
		ChangeRoleInfoCfg cfg = CfgChangeRoleInfoDAO.getInstance().getCfgByTime(totalMin);
		if (totalMin <= 0 || cfg != null) {
			int oldCareer = player.getCareer();
			int career = req.getCareerType();
			int sex = req.getSex();

			int cost = 0;
			cost += career != player.getCareer() && cfg != null ? cfg
					.getJobCost() : 0;
			cost += sex != player.getSex() && cfg != null ? cfg.getSexCost() : 0;
			if (cost > player.getUserGameDataMgr().getGold()) {
				result = response.setResultReason("金币不足").setResult(resultType)
						.build().toByteString();
				return result;
			}
			player.getUserGameDataMgr().addGold(-cost);
			boolean changeCareer = career != player.getCareer();
			if (changeCareer) {
				player.SetCareer(career);
				player.getMainRoleHero().getEquipMgr().changeEquip(player, player.getMainRoleHero().getUUId());
			}
			if (sex != player.getSex()) {
				player.getUserDataMgr().setSex(player.getSex() == 0 ? 1 : 0);
			}
			player.getFashionMgr().changeSuitCareer();
			player.onCareerChange(career,sex);
			// 任务
			player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Change_Career, 1);
			ChangeRoleInfoCfg changeCfg = CfgChangeRoleInfoDAO.getInstance()
					.getCfg();
			long changeTime = new Date().getTime() + changeCfg.getTime() * 60
					* 1000;
			player.getUserGameDataMgr().setLastChangeInfoTime(changeTime);

			resultType = RoleResultType.SUCCESS;
			player.getUserGameDataMgr().setCarrerChangeTime();
			
			result = response.setResult(resultType).build().toByteString();
			if(changeCareer){
				ArenaBM.getInstance().deleteArenaData(player,oldCareer);
			}
			
		}
		return result;
	}

	/**
	 * 职业进阶
	 * 
	 * @param player
	 * @param preLevel
	 */
	public void careerAdvance(Player player, int preLevel) {
		// isAdvance = false;
		// AdvanceCfg cfg =
		// AdvanceCfgDAO.getInstance().getCfg(player.getTemplateId());
		// if(cfg != null && cfg.getAdvanceLevel() != -1){
		// if(cfg.getAdvanceLevel() <= player.getLevel() && preLevel <
		// cfg.getAdvanceLevel()){
		// m_Attr = new HashMap<Integer, Double>();
		// String[] attrStrArr = cfg.getAttrArr().split("_");
		// for (String attrStr : attrStrArr) {
		// eAttrIdDef eAttr = eAttrIdDef.getDef(Integer.parseInt(attrStr));
		// if(eAttr != null){
		// m_Attr.put(Integer.parseInt(attrStr),
		// player.getAttrMgr().getAttr(eAttr));
		// }
		// }
		// player.changeInfo(cfg.getNextPlayerId());
		// isAdvance = true;
		// }
		// }
	}

	/**
	 * 职业进阶
	 * 
	 * @param req
	 * @param player
	 * @return
	 */
	public ByteString careerAdvance(RoleRequest req, Player player) {
		ByteString result;
		Builder response = RoleResponse.newBuilder();
		response.setType(RoleRequestType.CAREER_ADVANCE);
		RoleResultType resultType = isAdvance ? RoleResultType.SUCCESS
				: RoleResultType.FAIL;
		if (isAdvance && m_Attr != null) {
			Iterator<Entry<Integer, Double>> iter1 = m_Attr.entrySet()
					.iterator();
			while (iter1.hasNext()) {
				Map.Entry<Integer, Double> entry1 = iter1.next();

				TagAttr.Builder item = TagAttr.newBuilder();
				item.setAttrType(entry1.getKey());
				item.setAttrValue(entry1.getValue());
				response.addAttrArr(item);
			}
		}
		result = response.setResult(resultType).build().toByteString();
		return result;
	}
}
