package com.bm.targetSell.param;

import com.common.HPCUtil;

public enum ERoleAttrs {
	r_Level(1),                      	//等级
	r_VipLevel(2),						//vip等级
	r_Charge(3),						//充值
	r_TeamPower(4),				 	//五人战力
	r_AllPower(5),						//全员战力
	r_CreateTime(6),					//创建时间
	r_LastLoginTime(7),				//登陆时间
	r_Coin(8),							//玩家拥有的金币数量
	r_Power(9), 						//玩家当前体力值
	r_EmbattleQuality(10),              //上阵英雄的品阶
	r_EmbattleCarrer(11), 				//上阵英雄的品阶
	
	
	//英雄
	r_jiangziyaStar(12),
	r_nezha(13),
	r_yangjian(14),
	r_longjigongzhu(15),
	r_moliqing(16),
	r_muzha(17),
	r_leizhenzi(18),
	r_lijing(19),
	r_randengdaoren(20),
	r_wuji(21),
	r_lixingba(22),
	r_molishou(23),
	r_yashen(24),
	r_molihong(25),
	r_jizha(26),
	r_weihu(27),
	r_yunzhongzi(28),
	r_huanglongzhenren(29),
	r_molihai(30),
	r_daoxingtianzun(31),
	r_gaoyouquan(32),
	r_dongtianjun(33),
	r_guangchengzi(34),
	r_ximeier(35),
	r_shengongbao(36),
	r_daji(37),
	r_wenzhong(38),
	r_chenqi(39),
	r_zhenglun(40),
	r_zhangtianjun(41),
	r_yuantianjun(42),
	r_jinguangshengmu(43),
	r_lingbaodafashi(44),
	r_qiongxiao(45),
	r_wangmo(46),

	;
	private int id;
	private ERoleAttrs(int id){
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public String getIdStr(){
		return String.valueOf(id);
	}

	private static ERoleAttrs[] array;

	static {
		ERoleAttrs[] temp = ERoleAttrs.values();
		Object[] copy = HPCUtil.toMappedArray(temp, "id");
		array = new ERoleAttrs[copy.length];
	}

	public static ERoleAttrs getRoleAttrs(int type) {
		return array[type];
	}
	
	public static ERoleAttrs[] getAll(){
		return array;
	}
}
