package com.rw.service.PeakArena.datamodel;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.bm.rank.arena.ArenaExtAttribute;

/**
 * 巅峰竞技场扩展属性
 * @author Jamaz
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PeakArenaExtAttribute extends ArenaExtAttribute{
//TODO 巅峰竞技场的排行榜信息基本与竞技场的一样，只是队伍从1队扩展为3对，
//可以考虑保存队伍的图标和战力等简单信息，详细情况再TablePeakArenaData里面再查询
	
	/**
	 * 延长一倍的超时，用于一场战斗结束后，连续打第二场
	 */
	public void extendTimeOut(){
		timeout += originalTimeOut();
	}

	//json 调用
	public PeakArenaExtAttribute() {
	}

	public PeakArenaExtAttribute(int career, int fighting, String name, String headImage, int level) {
		super(career, fighting, name, headImage, level);
	}
}
