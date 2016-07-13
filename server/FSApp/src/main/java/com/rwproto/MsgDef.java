// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: MsgDef.proto

package com.rwproto;

public final class MsgDef {
  private MsgDef() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  /**
   * Protobuf enum {@code MsgDef.Command}
   */
  public enum Command
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>MSG_HeartBeat = 100;</code>
     */
    MSG_HeartBeat(0, 100),
    /**
     * <code>MSG_Rs_DATA = 101;</code>
     *
     * <pre>
     *消息确认
     * </pre>
     */
    MSG_Rs_DATA(1, 101),
    /**
     * <code>MSG_DO_MAINROLE_CREATE = 102;</code>
     *
     * <pre>
     *客户端主角初始化
     * </pre>
     */
    MSG_DO_MAINROLE_CREATE(2, 102),
    /**
     * <code>MSG_GET_ROLE_LIST = 104;</code>
     *
     * <pre>
     *获取角色列表
     * </pre>
     */
    MSG_GET_ROLE_LIST(3, 104),
    /**
     * <code>MSG_ROLE = 105;</code>
     *
     * <pre>
     *创建角色
     * </pre>
     */
    MSG_ROLE(4, 105),
    /**
     * <code>MSG_DEL_ROLE = 106;</code>
     *
     * <pre>
     *删除角色...
     * </pre>
     */
    MSG_DEL_ROLE(5, 106),
    /**
     * <code>MSG_CHOOES_ROLE = 107;</code>
     *
     * <pre>
     *选择角色进入游戏
     * </pre>
     */
    MSG_CHOOES_ROLE(6, 107),
    /**
     * <code>MSG_MainService = 108;</code>
     *
     * <pre>
     *首页
     * </pre>
     */
    MSG_MainService(7, 108),
    /**
     * <code>MSG_CopyService = 109;</code>
     *
     * <pre>
     *副本
     * </pre>
     */
    MSG_CopyService(8, 109),
    /**
     * <code>MSG_InitRoleData = 110;</code>
     *
     * <pre>
     * </pre>
     */
    MSG_InitRoleData(9, 110),
    /**
     * <code>MSG_SKILL = 111;</code>
     *
     * <pre>
     *技能
     * </pre>
     */
    MSG_SKILL(10, 111),
    /**
     * <code>MSG_ItemBag = 112;</code>
     *
     * <pre>
     *背包
     * </pre>
     */
    MSG_ItemBag(11, 112),
    /**
     * <code>MSG_Hero = 113;</code>
     *
     * <pre>
     *佣兵
     * </pre>
     */
    MSG_Hero(12, 113),
    /**
     * <code>MSG_GM = 114;</code>
     *
     * <pre>
     *GM命令
     * </pre>
     */
    MSG_GM(13, 114),
    /**
     * <code>MSG_EQUIP = 115;</code>
     *
     * <pre>
     *装备
     * </pre>
     */
    MSG_EQUIP(14, 115),
    /**
     * <code>MSG_RoleAttr = 116;</code>
     *
     * <pre>
     *角色属性
     * </pre>
     */
    MSG_RoleAttr(15, 116),
    /**
     * <code>MSG_MAGIC = 117;</code>
     *
     * <pre>
     *法宝
     * </pre>
     */
    MSG_MAGIC(16, 117),
    /**
     * <code>MSG_GAMBLE = 118;</code>
     *
     * <pre>
     *垂钓
     * </pre>
     */
    MSG_GAMBLE(17, 118),
    /**
     * <code>MSG_CHAT = 119;</code>
     *
     * <pre>
     *聊天
     * </pre>
     */
    MSG_CHAT(18, 119),
    /**
     * <code>MSG_EMAIL = 120;</code>
     *
     * <pre>
     *邮件
     * </pre>
     */
    MSG_EMAIL(19, 120),
    /**
     * <code>MSG_TRIAL = 121;</code>
     *
     * <pre>
     *PVE活动
     * </pre>
     */
    MSG_TRIAL(20, 121),
    /**
     * <code>MSG_RANKING = 122;</code>
     *
     * <pre>
     *排行榜
     * </pre>
     */
    MSG_RANKING(21, 122),
    /**
     * <code>MSG_SYNC_PLAYER = 123;</code>
     *
     * <pre>
     *同步主角数据
     * </pre>
     */
    MSG_SYNC_PLAYER(22, 123),
    /**
     * <code>MSG_SYNC_HERO = 124;</code>
     *
     * <pre>
     *同步佣兵数据
     * </pre>
     */
    MSG_SYNC_HERO(23, 124),
    /**
     * <code>MSG_SYNC_SKILL = 125;</code>
     *
     * <pre>
     *同步主角技能数据
     * </pre>
     */
    MSG_SYNC_SKILL(24, 125),
    /**
     * <code>MSG_SEND_HERO_INFO = 126;</code>
     *
     * <pre>
     *发送佣兵数据
     * </pre>
     */
    MSG_SEND_HERO_INFO(25, 126),
    /**
     * <code>MSG_COMMON_MESSAGE = 127;</code>
     *
     * <pre>
     *消息提示
     * </pre>
     */
    MSG_COMMON_MESSAGE(26, 127),
    /**
     * <code>MSG_DAILY_ACTIVITY = 128;</code>
     *
     * <pre>
     * </pre>
     */
    MSG_DAILY_ACTIVITY(27, 128),
    /**
     * <code>MSG_LOGIN_PLATFORM = 129;</code>
     */
    MSG_LOGIN_PLATFORM(28, 129),
    /**
     * <code>MSG_LOGIN_GAME = 130;</code>
     */
    MSG_LOGIN_GAME(29, 130),
    /**
     * <code>MSG_LOAD_MAINCITY = 131;</code>
     */
    MSG_LOAD_MAINCITY(30, 131),
    /**
     * <code>MSG_PLAYER_OFF_LINE = 132;</code>
     */
    MSG_PLAYER_OFF_LINE(31, 132),
    /**
     * <code>MSG_FRIEND = 133;</code>
     */
    MSG_FRIEND(32, 133),
    /**
     * <code>MSG_SIGN = 134;</code>
     */
    MSG_SIGN(33, 134),
    /**
     * <code>MSG_PEAK_ARENA = 139;</code>
     */
    MSG_PEAK_ARENA(34, 139),
    /**
     * <code>MSG_ARENA = 140;</code>
     */
    MSG_ARENA(35, 140),
    /**
     * <code>MSG_VIP = 141;</code>
     */
    MSG_VIP(36, 141),
    /**
     * <code>MSG_HOT_POINT = 142;</code>
     */
    MSG_HOT_POINT(37, 142),
    /**
     * <code>MSG_SETTING = 143;</code>
     */
    MSG_SETTING(38, 143),
    /**
     * <code>MSG_OtherRoleAttr = 144;</code>
     *
     * <pre>
     *查看其它玩家数据
     * </pre>
     */
    MSG_OtherRoleAttr(39, 144),
    /**
     * <code>MSG_STORE = 145;</code>
     *
     * <pre>
     *商城
     * </pre>
     */
    MSG_STORE(40, 145),
    /**
     * <code>MSG_UnendingWar = 146;</code>
     *
     * <pre>
     *无尽的战火
     * </pre>
     */
    MSG_UnendingWar(41, 146),
    /**
     * <code>MSG_Worship = 147;</code>
     *
     * <pre>
     *膜拜
     * </pre>
     */
    MSG_Worship(42, 147),
    /**
     * <code>MSG_TOWER = 148;</code>
     *
     * <pre>
     *爬塔
     * </pre>
     */
    MSG_TOWER(43, 148),
    /**
     * <code>MSG_TASK = 149;</code>
     *
     * <pre>
     *任务
     * </pre>
     */
    MSG_TASK(44, 149),
    /**
     * <code>MSG_GROUP = 150;</code>
     *
     * <pre>
     *公会基础管理
     * </pre>
     */
    MSG_GROUP(45, 150),
    /**
     * <code>MSG_TIME = 151;</code>
     *
     * <pre>
     *系统时间
     * </pre>
     */
    MSG_TIME(46, 151),
    /**
     * <code>MSG_GUIDE = 152;</code>
     *
     * <pre>
     *引导
     * </pre>
     */
    MSG_GUIDE(47, 152),
    /**
     * <code>MSG_SECRET_AREA = 153;</code>
     *
     * <pre>
     *秘境
     * </pre>
     */
    MSG_SECRET_AREA(48, 153),
    /**
     * <code>MSG_ERRORINFO = 154;</code>
     *
     * <pre>
     *错误信息
     * </pre>
     */
    MSG_ERRORINFO(49, 154),
    /**
     * <code>MSG_SECRET_MEMBER = 155;</code>
     *
     * <pre>
     *秘境玩家
     * </pre>
     */
    MSG_SECRET_MEMBER(50, 155),
    /**
     * <code>MSG_Inlay = 156;</code>
     *
     * <pre>
     *镶嵌宝石
     * </pre>
     */
    MSG_Inlay(51, 156),
    /**
     * <code>MSG_DATA_SYN = 157;</code>
     *
     * <pre>
     *数据同步
     * </pre>
     */
    MSG_DATA_SYN(52, 157),
    /**
     * <code>MSG_BATTLE_TOWER = 158;</code>
     *
     * <pre>
     *试练塔
     * </pre>
     */
    MSG_BATTLE_TOWER(53, 158),
    /**
     * <code>MSG_FASHION = 159;</code>
     *
     * <pre>
     *时装
     * </pre>
     */
    MSG_FASHION(54, 159),
    /**
     * <code>MSG_MainMsg = 160;</code>
     *
     * <pre>
     *主城显示信息、跑马灯
     * </pre>
     */
    MSG_MainMsg(55, 160),
    /**
     * <code>MSG_NEW_GUIDE = 161;</code>
     *
     * <pre>
     *重构的新手引导
     * </pre>
     */
    MSG_NEW_GUIDE(56, 161),
    /**
     * <code>MSG_PLOT = 162;</code>
     *
     * <pre>
     *剧情
     * </pre>
     */
    MSG_PLOT(57, 162),
    /**
     * <code>MSG_PLAYER_LOGOUT = 163;</code>
     */
    MSG_PLAYER_LOGOUT(58, 163),
    /**
     * <code>MSG_DailyGif = 164;</code>
     *
     * <pre>
     *七日礼包
     * </pre>
     */
    MSG_DailyGif(59, 164),
    /**
     * <code>MSG_RED_POINT = 165;</code>
     *
     * <pre>
     *红点
     * </pre>
     */
    MSG_RED_POINT(60, 165),
    /**
     * <code>MSG_FRSH_ACT = 166;</code>
     *
     * <pre>
     *开服活动
     * </pre>
     */
    MSG_FRSH_ACT(61, 166),
    /**
     * <code>MSG_RECONNECT = 167;</code>
     *
     * <pre>
     *断线重连
     * </pre>
     */
    MSG_RECONNECT(62, 167),
    /**
     * <code>MSG_PVE_INFO = 168;</code>
     *
     * <pre>
     *请求pve活动信息
     * </pre>
     */
    MSG_PVE_INFO(63, 168),
    /**
     * <code>MSG_NOTICE = 169;</code>
     *
     * <pre>
     *请求通告
     * </pre>
     */
    MSG_NOTICE(64, 169),
    /**
     * <code>MSG_GROUP_MEMBER_MANAGER = 170;</code>
     *
     * <pre>
     *公会成员管理
     * </pre>
     */
    MSG_GROUP_MEMBER_MANAGER(65, 170),
    /**
     * <code>MSG_GROUP_PERSONAL = 171;</code>
     *
     * <pre>
     *公会个人操作
     * </pre>
     */
    MSG_GROUP_PERSONAL(66, 171),
    /**
     * <code>MSG_GROUP_SKILL = 172;</code>
     *
     * <pre>
     *帮派技能
     * </pre>
     */
    MSG_GROUP_SKILL(67, 172),
    /**
     * <code>MSG_GIFT_CODE = 173;</code>
     *
     * <pre>
     *兑换码
     * </pre>
     */
    MSG_GIFT_CODE(68, 173),
    /**
     * <code>MSG_CHARGE = 174;</code>
     *
     * <pre>
     *充值
     * </pre>
     */
    MSG_CHARGE(69, 174),
    /**
     * <code>MSG_ACTIVITY_COUNTTYPE = 175;</code>
     *
     * <pre>
     *通用活动
     * </pre>
     */
    MSG_ACTIVITY_COUNTTYPE(70, 175),
    /**
     * <code>MSG_PRIVILEGE = 176;</code>
     *
     * <pre>
     *特权数据推送
     * </pre>
     */
    MSG_PRIVILEGE(71, 176),
    /**
     * <code>MSG_ACTIVITY_DATETYPE = 177;</code>
     *
     * <pre>
     *通用活动
     * </pre>
     */
    MSG_ACTIVITY_DATETYPE(72, 177),
    /**
     * <code>MSG_ACTIVITY_RANKTYPE = 178;</code>
     *
     * <pre>
     *通用活动
     * </pre>
     */
    MSG_ACTIVITY_RANKTYPE(73, 178),
    /**
     * <code>MSG_ACTIVITY_TIME_COUNT_TYPE = 179;</code>
     *
     * <pre>
     *通用活动
     * </pre>
     */
    MSG_ACTIVITY_TIME_COUNT_TYPE(74, 179),
    /**
     * <code>MSG_ACTIVITY_DAILY_TYPE = 180;</code>
     *
     * <pre>
     *通用活动每日
     * </pre>
     */
    MSG_ACTIVITY_DAILY_TYPE(75, 180),
    /**
     * <code>MSG_ACTIVITY_VITALITY_TYPE = 181;</code>
     *
     * <pre>
     *通用活动活跃度
     * </pre>
     */
    MSG_ACTIVITY_VITALITY_TYPE(76, 181),
    /**
     * <code>MSG_ACTIVITY_EXCHANGE_TYPE = 182;</code>
     *
     * <pre>
     *通用活动兑换币
     * </pre>
     */
    MSG_ACTIVITY_EXCHANGE_TYPE(77, 182),
    /**
     * <code>MSG_ACTIVITY_DailyDiscount_TYPE = 183;</code>
     *
     * <pre>
     *通用活动每日折扣
     * </pre>
     */
    MSG_ACTIVITY_DailyDiscount_TYPE(78, 183),
    /**
     * <code>MSG_ACTIVITY_VipDiscount_TYPE = 184;</code>
     *
     * <pre>
     *通用活动七日VIp折扣
     * </pre>
     */
    MSG_ACTIVITY_VipDiscount_TYPE(79, 184),
    /**
     * <code>MSG_ACTIVITY_RedEnvelope_TYPE = 185;</code>
     *
     * <pre>
     *通用活动开服红包
     * </pre>
     */
    MSG_ACTIVITY_RedEnvelope_TYPE(80, 185),
    /**
     * <code>MSG_TAOIST = 200;</code>
     *
     * <pre>
     *道术系统
     * </pre>
     */
    MSG_TAOIST(81, 200),
    /**
     * <code>MSG_FIX_EQUIP = 201;</code>
     *
     * <pre>
     *专属装备
     * </pre>
     */
    MSG_FIX_EQUIP(82, 201),
    /**
     * <code>MSG_GROUP_SECRET = 202;</code>
     *
     * <pre>
     *帮派秘境
     * </pre>
     */
    MSG_GROUP_SECRET(83, 202),
    /**
     * <code>MSG_MAGIC_SECRET = 203;</code>
     *
     * <pre>
     *法宝秘境
     * </pre>
     */
    MSG_MAGIC_SECRET(84, 203),
    /**
     * <code>MSG_GROUP_SECRET_MATCH = 204;</code>
     *
     * <pre>
     *帮派秘境探索
     * </pre>
     */
    MSG_GROUP_SECRET_MATCH(85, 204),
    /**
     * <code>MSG_GROUP_FIGHT_ONLINE = 205;</code>
     *
     * <pre>
     *在线帮战
     * </pre>
     */
    MSG_GROUP_FIGHT_ONLINE(86, 205),
    /**
     * <code>MSG_RED_POINT_SERVICE = 206;</code>
     *
     * <pre>
     *红点扩展点击传送
     * </pre>
     */
    MSG_RED_POINT_SERVICE(87, 206),
    /**
     * <code>MSG_FEEDBACK = 995;</code>
     *
     * <pre>
     *客服功能
     * </pre>
     */
    MSG_FEEDBACK(88, 995),
    /**
     * <code>MSG_SDK_VERIFY = 996;</code>
     *
     * <pre>
     *验证sdk登陆
     * </pre>
     */
    MSG_SDK_VERIFY(89, 996),
    /**
     * <code>MSG_NUMERIC_ANALYSIS = 997;</code>
     *
     * <pre>
     *数值测试场景通讯协议
     * </pre>
     */
    MSG_NUMERIC_ANALYSIS(90, 997),
    /**
     * <code>MSG_PLATFORMGS = 998;</code>
     *
     * <pre>
     *登陆服游戏服通讯协议
     * </pre>
     */
    MSG_PLATFORMGS(91, 998),
    /**
     * <code>MSG_GAMEPRESS = 999;</code>
     *
     * <pre>
     *压测协议
     * </pre>
     */
    MSG_GAMEPRESS(92, 999),
    ;

    /**
     * <code>MSG_HeartBeat = 100;</code>
     */
    public static final int MSG_HeartBeat_VALUE = 100;
    /**
     * <code>MSG_Rs_DATA = 101;</code>
     *
     * <pre>
     *消息确认
     * </pre>
     */
    public static final int MSG_Rs_DATA_VALUE = 101;
    /**
     * <code>MSG_DO_MAINROLE_CREATE = 102;</code>
     *
     * <pre>
     *客户端主角初始化
     * </pre>
     */
    public static final int MSG_DO_MAINROLE_CREATE_VALUE = 102;
    /**
     * <code>MSG_GET_ROLE_LIST = 104;</code>
     *
     * <pre>
     *获取角色列表
     * </pre>
     */
    public static final int MSG_GET_ROLE_LIST_VALUE = 104;
    /**
     * <code>MSG_ROLE = 105;</code>
     *
     * <pre>
     *创建角色
     * </pre>
     */
    public static final int MSG_ROLE_VALUE = 105;
    /**
     * <code>MSG_DEL_ROLE = 106;</code>
     *
     * <pre>
     *删除角色...
     * </pre>
     */
    public static final int MSG_DEL_ROLE_VALUE = 106;
    /**
     * <code>MSG_CHOOES_ROLE = 107;</code>
     *
     * <pre>
     *选择角色进入游戏
     * </pre>
     */
    public static final int MSG_CHOOES_ROLE_VALUE = 107;
    /**
     * <code>MSG_MainService = 108;</code>
     *
     * <pre>
     *首页
     * </pre>
     */
    public static final int MSG_MainService_VALUE = 108;
    /**
     * <code>MSG_CopyService = 109;</code>
     *
     * <pre>
     *副本
     * </pre>
     */
    public static final int MSG_CopyService_VALUE = 109;
    /**
     * <code>MSG_InitRoleData = 110;</code>
     *
     * <pre>
     * </pre>
     */
    public static final int MSG_InitRoleData_VALUE = 110;
    /**
     * <code>MSG_SKILL = 111;</code>
     *
     * <pre>
     *技能
     * </pre>
     */
    public static final int MSG_SKILL_VALUE = 111;
    /**
     * <code>MSG_ItemBag = 112;</code>
     *
     * <pre>
     *背包
     * </pre>
     */
    public static final int MSG_ItemBag_VALUE = 112;
    /**
     * <code>MSG_Hero = 113;</code>
     *
     * <pre>
     *佣兵
     * </pre>
     */
    public static final int MSG_Hero_VALUE = 113;
    /**
     * <code>MSG_GM = 114;</code>
     *
     * <pre>
     *GM命令
     * </pre>
     */
    public static final int MSG_GM_VALUE = 114;
    /**
     * <code>MSG_EQUIP = 115;</code>
     *
     * <pre>
     *装备
     * </pre>
     */
    public static final int MSG_EQUIP_VALUE = 115;
    /**
     * <code>MSG_RoleAttr = 116;</code>
     *
     * <pre>
     *角色属性
     * </pre>
     */
    public static final int MSG_RoleAttr_VALUE = 116;
    /**
     * <code>MSG_MAGIC = 117;</code>
     *
     * <pre>
     *法宝
     * </pre>
     */
    public static final int MSG_MAGIC_VALUE = 117;
    /**
     * <code>MSG_GAMBLE = 118;</code>
     *
     * <pre>
     *垂钓
     * </pre>
     */
    public static final int MSG_GAMBLE_VALUE = 118;
    /**
     * <code>MSG_CHAT = 119;</code>
     *
     * <pre>
     *聊天
     * </pre>
     */
    public static final int MSG_CHAT_VALUE = 119;
    /**
     * <code>MSG_EMAIL = 120;</code>
     *
     * <pre>
     *邮件
     * </pre>
     */
    public static final int MSG_EMAIL_VALUE = 120;
    /**
     * <code>MSG_TRIAL = 121;</code>
     *
     * <pre>
     *PVE活动
     * </pre>
     */
    public static final int MSG_TRIAL_VALUE = 121;
    /**
     * <code>MSG_RANKING = 122;</code>
     *
     * <pre>
     *排行榜
     * </pre>
     */
    public static final int MSG_RANKING_VALUE = 122;
    /**
     * <code>MSG_SYNC_PLAYER = 123;</code>
     *
     * <pre>
     *同步主角数据
     * </pre>
     */
    public static final int MSG_SYNC_PLAYER_VALUE = 123;
    /**
     * <code>MSG_SYNC_HERO = 124;</code>
     *
     * <pre>
     *同步佣兵数据
     * </pre>
     */
    public static final int MSG_SYNC_HERO_VALUE = 124;
    /**
     * <code>MSG_SYNC_SKILL = 125;</code>
     *
     * <pre>
     *同步主角技能数据
     * </pre>
     */
    public static final int MSG_SYNC_SKILL_VALUE = 125;
    /**
     * <code>MSG_SEND_HERO_INFO = 126;</code>
     *
     * <pre>
     *发送佣兵数据
     * </pre>
     */
    public static final int MSG_SEND_HERO_INFO_VALUE = 126;
    /**
     * <code>MSG_COMMON_MESSAGE = 127;</code>
     *
     * <pre>
     *消息提示
     * </pre>
     */
    public static final int MSG_COMMON_MESSAGE_VALUE = 127;
    /**
     * <code>MSG_DAILY_ACTIVITY = 128;</code>
     *
     * <pre>
     * </pre>
     */
    public static final int MSG_DAILY_ACTIVITY_VALUE = 128;
    /**
     * <code>MSG_LOGIN_PLATFORM = 129;</code>
     */
    public static final int MSG_LOGIN_PLATFORM_VALUE = 129;
    /**
     * <code>MSG_LOGIN_GAME = 130;</code>
     */
    public static final int MSG_LOGIN_GAME_VALUE = 130;
    /**
     * <code>MSG_LOAD_MAINCITY = 131;</code>
     */
    public static final int MSG_LOAD_MAINCITY_VALUE = 131;
    /**
     * <code>MSG_PLAYER_OFF_LINE = 132;</code>
     */
    public static final int MSG_PLAYER_OFF_LINE_VALUE = 132;
    /**
     * <code>MSG_FRIEND = 133;</code>
     */
    public static final int MSG_FRIEND_VALUE = 133;
    /**
     * <code>MSG_SIGN = 134;</code>
     */
    public static final int MSG_SIGN_VALUE = 134;
    /**
     * <code>MSG_PEAK_ARENA = 139;</code>
     */
    public static final int MSG_PEAK_ARENA_VALUE = 139;
    /**
     * <code>MSG_ARENA = 140;</code>
     */
    public static final int MSG_ARENA_VALUE = 140;
    /**
     * <code>MSG_VIP = 141;</code>
     */
    public static final int MSG_VIP_VALUE = 141;
    /**
     * <code>MSG_HOT_POINT = 142;</code>
     */
    public static final int MSG_HOT_POINT_VALUE = 142;
    /**
     * <code>MSG_SETTING = 143;</code>
     */
    public static final int MSG_SETTING_VALUE = 143;
    /**
     * <code>MSG_OtherRoleAttr = 144;</code>
     *
     * <pre>
     *查看其它玩家数据
     * </pre>
     */
    public static final int MSG_OtherRoleAttr_VALUE = 144;
    /**
     * <code>MSG_STORE = 145;</code>
     *
     * <pre>
     *商城
     * </pre>
     */
    public static final int MSG_STORE_VALUE = 145;
    /**
     * <code>MSG_UnendingWar = 146;</code>
     *
     * <pre>
     *无尽的战火
     * </pre>
     */
    public static final int MSG_UnendingWar_VALUE = 146;
    /**
     * <code>MSG_Worship = 147;</code>
     *
     * <pre>
     *膜拜
     * </pre>
     */
    public static final int MSG_Worship_VALUE = 147;
    /**
     * <code>MSG_TOWER = 148;</code>
     *
     * <pre>
     *爬塔
     * </pre>
     */
    public static final int MSG_TOWER_VALUE = 148;
    /**
     * <code>MSG_TASK = 149;</code>
     *
     * <pre>
     *任务
     * </pre>
     */
    public static final int MSG_TASK_VALUE = 149;
    /**
     * <code>MSG_GROUP = 150;</code>
     *
     * <pre>
     *公会基础管理
     * </pre>
     */
    public static final int MSG_GROUP_VALUE = 150;
    /**
     * <code>MSG_TIME = 151;</code>
     *
     * <pre>
     *系统时间
     * </pre>
     */
    public static final int MSG_TIME_VALUE = 151;
    /**
     * <code>MSG_GUIDE = 152;</code>
     *
     * <pre>
     *引导
     * </pre>
     */
    public static final int MSG_GUIDE_VALUE = 152;
    /**
     * <code>MSG_SECRET_AREA = 153;</code>
     *
     * <pre>
     *秘境
     * </pre>
     */
    public static final int MSG_SECRET_AREA_VALUE = 153;
    /**
     * <code>MSG_ERRORINFO = 154;</code>
     *
     * <pre>
     *错误信息
     * </pre>
     */
    public static final int MSG_ERRORINFO_VALUE = 154;
    /**
     * <code>MSG_SECRET_MEMBER = 155;</code>
     *
     * <pre>
     *秘境玩家
     * </pre>
     */
    public static final int MSG_SECRET_MEMBER_VALUE = 155;
    /**
     * <code>MSG_Inlay = 156;</code>
     *
     * <pre>
     *镶嵌宝石
     * </pre>
     */
    public static final int MSG_Inlay_VALUE = 156;
    /**
     * <code>MSG_DATA_SYN = 157;</code>
     *
     * <pre>
     *数据同步
     * </pre>
     */
    public static final int MSG_DATA_SYN_VALUE = 157;
    /**
     * <code>MSG_BATTLE_TOWER = 158;</code>
     *
     * <pre>
     *试练塔
     * </pre>
     */
    public static final int MSG_BATTLE_TOWER_VALUE = 158;
    /**
     * <code>MSG_FASHION = 159;</code>
     *
     * <pre>
     *时装
     * </pre>
     */
    public static final int MSG_FASHION_VALUE = 159;
    /**
     * <code>MSG_MainMsg = 160;</code>
     *
     * <pre>
     *主城显示信息、跑马灯
     * </pre>
     */
    public static final int MSG_MainMsg_VALUE = 160;
    /**
     * <code>MSG_NEW_GUIDE = 161;</code>
     *
     * <pre>
     *重构的新手引导
     * </pre>
     */
    public static final int MSG_NEW_GUIDE_VALUE = 161;
    /**
     * <code>MSG_PLOT = 162;</code>
     *
     * <pre>
     *剧情
     * </pre>
     */
    public static final int MSG_PLOT_VALUE = 162;
    /**
     * <code>MSG_PLAYER_LOGOUT = 163;</code>
     */
    public static final int MSG_PLAYER_LOGOUT_VALUE = 163;
    /**
     * <code>MSG_DailyGif = 164;</code>
     *
     * <pre>
     *七日礼包
     * </pre>
     */
    public static final int MSG_DailyGif_VALUE = 164;
    /**
     * <code>MSG_RED_POINT = 165;</code>
     *
     * <pre>
     *红点
     * </pre>
     */
    public static final int MSG_RED_POINT_VALUE = 165;
    /**
     * <code>MSG_FRSH_ACT = 166;</code>
     *
     * <pre>
     *开服活动
     * </pre>
     */
    public static final int MSG_FRSH_ACT_VALUE = 166;
    /**
     * <code>MSG_RECONNECT = 167;</code>
     *
     * <pre>
     *断线重连
     * </pre>
     */
    public static final int MSG_RECONNECT_VALUE = 167;
    /**
     * <code>MSG_PVE_INFO = 168;</code>
     *
     * <pre>
     *请求pve活动信息
     * </pre>
     */
    public static final int MSG_PVE_INFO_VALUE = 168;
    /**
     * <code>MSG_NOTICE = 169;</code>
     *
     * <pre>
     *请求通告
     * </pre>
     */
    public static final int MSG_NOTICE_VALUE = 169;
    /**
     * <code>MSG_GROUP_MEMBER_MANAGER = 170;</code>
     *
     * <pre>
     *公会成员管理
     * </pre>
     */
    public static final int MSG_GROUP_MEMBER_MANAGER_VALUE = 170;
    /**
     * <code>MSG_GROUP_PERSONAL = 171;</code>
     *
     * <pre>
     *公会个人操作
     * </pre>
     */
    public static final int MSG_GROUP_PERSONAL_VALUE = 171;
    /**
     * <code>MSG_GROUP_SKILL = 172;</code>
     *
     * <pre>
     *帮派技能
     * </pre>
     */
    public static final int MSG_GROUP_SKILL_VALUE = 172;
    /**
     * <code>MSG_GIFT_CODE = 173;</code>
     *
     * <pre>
     *兑换码
     * </pre>
     */
    public static final int MSG_GIFT_CODE_VALUE = 173;
    /**
     * <code>MSG_CHARGE = 174;</code>
     *
     * <pre>
     *充值
     * </pre>
     */
    public static final int MSG_CHARGE_VALUE = 174;
    /**
     * <code>MSG_ACTIVITY_COUNTTYPE = 175;</code>
     *
     * <pre>
     *通用活动
     * </pre>
     */
    public static final int MSG_ACTIVITY_COUNTTYPE_VALUE = 175;
    /**
     * <code>MSG_PRIVILEGE = 176;</code>
     *
     * <pre>
     *特权数据推送
     * </pre>
     */
    public static final int MSG_PRIVILEGE_VALUE = 176;
    /**
     * <code>MSG_ACTIVITY_DATETYPE = 177;</code>
     *
     * <pre>
     *通用活动
     * </pre>
     */
    public static final int MSG_ACTIVITY_DATETYPE_VALUE = 177;
    /**
     * <code>MSG_ACTIVITY_RANKTYPE = 178;</code>
     *
     * <pre>
     *通用活动
     * </pre>
     */
    public static final int MSG_ACTIVITY_RANKTYPE_VALUE = 178;
    /**
     * <code>MSG_ACTIVITY_TIME_COUNT_TYPE = 179;</code>
     *
     * <pre>
     *通用活动
     * </pre>
     */
    public static final int MSG_ACTIVITY_TIME_COUNT_TYPE_VALUE = 179;
    /**
     * <code>MSG_ACTIVITY_DAILY_TYPE = 180;</code>
     *
     * <pre>
     *通用活动每日
     * </pre>
     */
    public static final int MSG_ACTIVITY_DAILY_TYPE_VALUE = 180;
    /**
     * <code>MSG_ACTIVITY_VITALITY_TYPE = 181;</code>
     *
     * <pre>
     *通用活动活跃度
     * </pre>
     */
    public static final int MSG_ACTIVITY_VITALITY_TYPE_VALUE = 181;
    /**
     * <code>MSG_ACTIVITY_EXCHANGE_TYPE = 182;</code>
     *
     * <pre>
     *通用活动兑换币
     * </pre>
     */
    public static final int MSG_ACTIVITY_EXCHANGE_TYPE_VALUE = 182;
    /**
     * <code>MSG_ACTIVITY_DailyDiscount_TYPE = 183;</code>
     *
     * <pre>
     *通用活动每日折扣
     * </pre>
     */
    public static final int MSG_ACTIVITY_DailyDiscount_TYPE_VALUE = 183;
    /**
     * <code>MSG_ACTIVITY_VipDiscount_TYPE = 184;</code>
     *
     * <pre>
     *通用活动七日VIp折扣
     * </pre>
     */
    public static final int MSG_ACTIVITY_VipDiscount_TYPE_VALUE = 184;
    /**
     * <code>MSG_ACTIVITY_RedEnvelope_TYPE = 185;</code>
     *
     * <pre>
     *通用活动开服红包
     * </pre>
     */
    public static final int MSG_ACTIVITY_RedEnvelope_TYPE_VALUE = 185;
    /**
     * <code>MSG_TAOIST = 200;</code>
     *
     * <pre>
     *道术系统
     * </pre>
     */
    public static final int MSG_TAOIST_VALUE = 200;
    /**
     * <code>MSG_FIX_EQUIP = 201;</code>
     *
     * <pre>
     *专属装备
     * </pre>
     */
    public static final int MSG_FIX_EQUIP_VALUE = 201;
    /**
     * <code>MSG_GROUP_SECRET = 202;</code>
     *
     * <pre>
     *帮派秘境
     * </pre>
     */
    public static final int MSG_GROUP_SECRET_VALUE = 202;
    /**
     * <code>MSG_MAGIC_SECRET = 203;</code>
     *
     * <pre>
     *法宝秘境
     * </pre>
     */
    public static final int MSG_MAGIC_SECRET_VALUE = 203;
    /**
     * <code>MSG_GROUP_SECRET_MATCH = 204;</code>
     *
     * <pre>
     *帮派秘境探索
     * </pre>
     */
    public static final int MSG_GROUP_SECRET_MATCH_VALUE = 204;
    /**
     * <code>MSG_GROUP_FIGHT_ONLINE = 205;</code>
     *
     * <pre>
     *在线帮战
     * </pre>
     */
    public static final int MSG_GROUP_FIGHT_ONLINE_VALUE = 205;
    /**
     * <code>MSG_RED_POINT_SERVICE = 206;</code>
     *
     * <pre>
     *红点扩展点击传送
     * </pre>
     */
    public static final int MSG_RED_POINT_SERVICE_VALUE = 206;
    /**
     * <code>MSG_FEEDBACK = 995;</code>
     *
     * <pre>
     *客服功能
     * </pre>
     */
    public static final int MSG_FEEDBACK_VALUE = 995;
    /**
     * <code>MSG_SDK_VERIFY = 996;</code>
     *
     * <pre>
     *验证sdk登陆
     * </pre>
     */
    public static final int MSG_SDK_VERIFY_VALUE = 996;
    /**
     * <code>MSG_NUMERIC_ANALYSIS = 997;</code>
     *
     * <pre>
     *数值测试场景通讯协议
     * </pre>
     */
    public static final int MSG_NUMERIC_ANALYSIS_VALUE = 997;
    /**
     * <code>MSG_PLATFORMGS = 998;</code>
     *
     * <pre>
     *登陆服游戏服通讯协议
     * </pre>
     */
    public static final int MSG_PLATFORMGS_VALUE = 998;
    /**
     * <code>MSG_GAMEPRESS = 999;</code>
     *
     * <pre>
     *压测协议
     * </pre>
     */
    public static final int MSG_GAMEPRESS_VALUE = 999;


    public final int getNumber() { return value; }

    public static Command valueOf(int value) {
      switch (value) {
        case 100: return MSG_HeartBeat;
        case 101: return MSG_Rs_DATA;
        case 102: return MSG_DO_MAINROLE_CREATE;
        case 104: return MSG_GET_ROLE_LIST;
        case 105: return MSG_ROLE;
        case 106: return MSG_DEL_ROLE;
        case 107: return MSG_CHOOES_ROLE;
        case 108: return MSG_MainService;
        case 109: return MSG_CopyService;
        case 110: return MSG_InitRoleData;
        case 111: return MSG_SKILL;
        case 112: return MSG_ItemBag;
        case 113: return MSG_Hero;
        case 114: return MSG_GM;
        case 115: return MSG_EQUIP;
        case 116: return MSG_RoleAttr;
        case 117: return MSG_MAGIC;
        case 118: return MSG_GAMBLE;
        case 119: return MSG_CHAT;
        case 120: return MSG_EMAIL;
        case 121: return MSG_TRIAL;
        case 122: return MSG_RANKING;
        case 123: return MSG_SYNC_PLAYER;
        case 124: return MSG_SYNC_HERO;
        case 125: return MSG_SYNC_SKILL;
        case 126: return MSG_SEND_HERO_INFO;
        case 127: return MSG_COMMON_MESSAGE;
        case 128: return MSG_DAILY_ACTIVITY;
        case 129: return MSG_LOGIN_PLATFORM;
        case 130: return MSG_LOGIN_GAME;
        case 131: return MSG_LOAD_MAINCITY;
        case 132: return MSG_PLAYER_OFF_LINE;
        case 133: return MSG_FRIEND;
        case 134: return MSG_SIGN;
        case 139: return MSG_PEAK_ARENA;
        case 140: return MSG_ARENA;
        case 141: return MSG_VIP;
        case 142: return MSG_HOT_POINT;
        case 143: return MSG_SETTING;
        case 144: return MSG_OtherRoleAttr;
        case 145: return MSG_STORE;
        case 146: return MSG_UnendingWar;
        case 147: return MSG_Worship;
        case 148: return MSG_TOWER;
        case 149: return MSG_TASK;
        case 150: return MSG_GROUP;
        case 151: return MSG_TIME;
        case 152: return MSG_GUIDE;
        case 153: return MSG_SECRET_AREA;
        case 154: return MSG_ERRORINFO;
        case 155: return MSG_SECRET_MEMBER;
        case 156: return MSG_Inlay;
        case 157: return MSG_DATA_SYN;
        case 158: return MSG_BATTLE_TOWER;
        case 159: return MSG_FASHION;
        case 160: return MSG_MainMsg;
        case 161: return MSG_NEW_GUIDE;
        case 162: return MSG_PLOT;
        case 163: return MSG_PLAYER_LOGOUT;
        case 164: return MSG_DailyGif;
        case 165: return MSG_RED_POINT;
        case 166: return MSG_FRSH_ACT;
        case 167: return MSG_RECONNECT;
        case 168: return MSG_PVE_INFO;
        case 169: return MSG_NOTICE;
        case 170: return MSG_GROUP_MEMBER_MANAGER;
        case 171: return MSG_GROUP_PERSONAL;
        case 172: return MSG_GROUP_SKILL;
        case 173: return MSG_GIFT_CODE;
        case 174: return MSG_CHARGE;
        case 175: return MSG_ACTIVITY_COUNTTYPE;
        case 176: return MSG_PRIVILEGE;
        case 177: return MSG_ACTIVITY_DATETYPE;
        case 178: return MSG_ACTIVITY_RANKTYPE;
        case 179: return MSG_ACTIVITY_TIME_COUNT_TYPE;
        case 180: return MSG_ACTIVITY_DAILY_TYPE;
        case 181: return MSG_ACTIVITY_VITALITY_TYPE;
        case 182: return MSG_ACTIVITY_EXCHANGE_TYPE;
        case 183: return MSG_ACTIVITY_DailyDiscount_TYPE;
        case 184: return MSG_ACTIVITY_VipDiscount_TYPE;
        case 185: return MSG_ACTIVITY_RedEnvelope_TYPE;
        case 200: return MSG_TAOIST;
        case 201: return MSG_FIX_EQUIP;
        case 202: return MSG_GROUP_SECRET;
        case 203: return MSG_MAGIC_SECRET;
        case 204: return MSG_GROUP_SECRET_MATCH;
        case 205: return MSG_GROUP_FIGHT_ONLINE;
        case 206: return MSG_RED_POINT_SERVICE;
        case 995: return MSG_FEEDBACK;
        case 996: return MSG_SDK_VERIFY;
        case 997: return MSG_NUMERIC_ANALYSIS;
        case 998: return MSG_PLATFORMGS;
        case 999: return MSG_GAMEPRESS;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Command>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<Command>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Command>() {
            public Command findValueByNumber(int number) {
              return Command.valueOf(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.rwproto.MsgDef.getDescriptor().getEnumTypes().get(0);
    }

    private static final Command[] VALUES = values();

    public static Command valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private Command(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:MsgDef.Command)
  }


  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\014MsgDef.proto\022\006MsgDef*\267\017\n\007Command\022\021\n\rMS" +
      "G_HeartBeat\020d\022\017\n\013MSG_Rs_DATA\020e\022\032\n\026MSG_DO" +
      "_MAINROLE_CREATE\020f\022\025\n\021MSG_GET_ROLE_LIST\020" +
      "h\022\014\n\010MSG_ROLE\020i\022\020\n\014MSG_DEL_ROLE\020j\022\023\n\017MSG" +
      "_CHOOES_ROLE\020k\022\023\n\017MSG_MainService\020l\022\023\n\017M" +
      "SG_CopyService\020m\022\024\n\020MSG_InitRoleData\020n\022\r" +
      "\n\tMSG_SKILL\020o\022\017\n\013MSG_ItemBag\020p\022\014\n\010MSG_He" +
      "ro\020q\022\n\n\006MSG_GM\020r\022\r\n\tMSG_EQUIP\020s\022\020\n\014MSG_R" +
      "oleAttr\020t\022\r\n\tMSG_MAGIC\020u\022\016\n\nMSG_GAMBLE\020v" +
      "\022\014\n\010MSG_CHAT\020w\022\r\n\tMSG_EMAIL\020x\022\r\n\tMSG_TRI",
      "AL\020y\022\017\n\013MSG_RANKING\020z\022\023\n\017MSG_SYNC_PLAYER" +
      "\020{\022\021\n\rMSG_SYNC_HERO\020|\022\022\n\016MSG_SYNC_SKILL\020" +
      "}\022\026\n\022MSG_SEND_HERO_INFO\020~\022\026\n\022MSG_COMMON_" +
      "MESSAGE\020\177\022\027\n\022MSG_DAILY_ACTIVITY\020\200\001\022\027\n\022MS" +
      "G_LOGIN_PLATFORM\020\201\001\022\023\n\016MSG_LOGIN_GAME\020\202\001" +
      "\022\026\n\021MSG_LOAD_MAINCITY\020\203\001\022\030\n\023MSG_PLAYER_O" +
      "FF_LINE\020\204\001\022\017\n\nMSG_FRIEND\020\205\001\022\r\n\010MSG_SIGN\020" +
      "\206\001\022\023\n\016MSG_PEAK_ARENA\020\213\001\022\016\n\tMSG_ARENA\020\214\001\022" +
      "\014\n\007MSG_VIP\020\215\001\022\022\n\rMSG_HOT_POINT\020\216\001\022\020\n\013MSG" +
      "_SETTING\020\217\001\022\026\n\021MSG_OtherRoleAttr\020\220\001\022\016\n\tM",
      "SG_STORE\020\221\001\022\024\n\017MSG_UnendingWar\020\222\001\022\020\n\013MSG" +
      "_Worship\020\223\001\022\016\n\tMSG_TOWER\020\224\001\022\r\n\010MSG_TASK\020" +
      "\225\001\022\016\n\tMSG_GROUP\020\226\001\022\r\n\010MSG_TIME\020\227\001\022\016\n\tMSG" +
      "_GUIDE\020\230\001\022\024\n\017MSG_SECRET_AREA\020\231\001\022\022\n\rMSG_E" +
      "RRORINFO\020\232\001\022\026\n\021MSG_SECRET_MEMBER\020\233\001\022\016\n\tM" +
      "SG_Inlay\020\234\001\022\021\n\014MSG_DATA_SYN\020\235\001\022\025\n\020MSG_BA" +
      "TTLE_TOWER\020\236\001\022\020\n\013MSG_FASHION\020\237\001\022\020\n\013MSG_M" +
      "ainMsg\020\240\001\022\022\n\rMSG_NEW_GUIDE\020\241\001\022\r\n\010MSG_PLO" +
      "T\020\242\001\022\026\n\021MSG_PLAYER_LOGOUT\020\243\001\022\021\n\014MSG_Dail" +
      "yGif\020\244\001\022\022\n\rMSG_RED_POINT\020\245\001\022\021\n\014MSG_FRSH_",
      "ACT\020\246\001\022\022\n\rMSG_RECONNECT\020\247\001\022\021\n\014MSG_PVE_IN" +
      "FO\020\250\001\022\017\n\nMSG_NOTICE\020\251\001\022\035\n\030MSG_GROUP_MEMB" +
      "ER_MANAGER\020\252\001\022\027\n\022MSG_GROUP_PERSONAL\020\253\001\022\024" +
      "\n\017MSG_GROUP_SKILL\020\254\001\022\022\n\rMSG_GIFT_CODE\020\255\001" +
      "\022\017\n\nMSG_CHARGE\020\256\001\022\033\n\026MSG_ACTIVITY_COUNTT" +
      "YPE\020\257\001\022\022\n\rMSG_PRIVILEGE\020\260\001\022\032\n\025MSG_ACTIVI" +
      "TY_DATETYPE\020\261\001\022\032\n\025MSG_ACTIVITY_RANKTYPE\020" +
      "\262\001\022!\n\034MSG_ACTIVITY_TIME_COUNT_TYPE\020\263\001\022\034\n" +
      "\027MSG_ACTIVITY_DAILY_TYPE\020\264\001\022\037\n\032MSG_ACTIV" +
      "ITY_VITALITY_TYPE\020\265\001\022\037\n\032MSG_ACTIVITY_EXC",
      "HANGE_TYPE\020\266\001\022$\n\037MSG_ACTIVITY_DailyDisco" +
      "unt_TYPE\020\267\001\022\"\n\035MSG_ACTIVITY_VipDiscount_" +
      "TYPE\020\270\001\022\"\n\035MSG_ACTIVITY_RedEnvelope_TYPE" +
      "\020\271\001\022\017\n\nMSG_TAOIST\020\310\001\022\022\n\rMSG_FIX_EQUIP\020\311\001" +
      "\022\025\n\020MSG_GROUP_SECRET\020\312\001\022\025\n\020MSG_MAGIC_SEC" +
      "RET\020\313\001\022\033\n\026MSG_GROUP_SECRET_MATCH\020\314\001\022\033\n\026M" +
      "SG_GROUP_FIGHT_ONLINE\020\315\001\022\032\n\025MSG_RED_POIN" +
      "T_SERVICE\020\316\001\022\021\n\014MSG_FEEDBACK\020\343\007\022\023\n\016MSG_S" +
      "DK_VERIFY\020\344\007\022\031\n\024MSG_NUMERIC_ANALYSIS\020\345\007\022" +
      "\023\n\016MSG_PLATFORMGS\020\346\007\022\022\n\rMSG_GAMEPRESS\020\347\007",
      "B\025\n\013com.rwprotoB\006MsgDef"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
