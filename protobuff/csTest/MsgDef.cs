//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// Option: missing-value detection (*Specified/ShouldSerialize*/Reset*) enabled
    
// Generated from: MsgDef.proto
namespace MsgDef
{
    [global::ProtoBuf.ProtoContract(Name=@"Command")]
    public enum Command
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_HeartBeat", Value=100)]
      MSG_HeartBeat = 100,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_Rs_DATA", Value=101)]
      MSG_Rs_DATA = 101,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_DO_MAINROLE_CREATE", Value=102)]
      MSG_DO_MAINROLE_CREATE = 102,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_GET_ROLE_LIST", Value=104)]
      MSG_GET_ROLE_LIST = 104,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_ROLE", Value=105)]
      MSG_ROLE = 105,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_DEL_ROLE", Value=106)]
      MSG_DEL_ROLE = 106,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_CHOOES_ROLE", Value=107)]
      MSG_CHOOES_ROLE = 107,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_MainService", Value=108)]
      MSG_MainService = 108,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_CopyService", Value=109)]
      MSG_CopyService = 109,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_InitRoleData", Value=110)]
      MSG_InitRoleData = 110,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_SKILL", Value=111)]
      MSG_SKILL = 111,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_ItemBag", Value=112)]
      MSG_ItemBag = 112,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_Hero", Value=113)]
      MSG_Hero = 113,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_GM", Value=114)]
      MSG_GM = 114,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_EQUIP", Value=115)]
      MSG_EQUIP = 115,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_RoleAttr", Value=116)]
      MSG_RoleAttr = 116,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_MAGIC", Value=117)]
      MSG_MAGIC = 117,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_GAMBLE", Value=118)]
      MSG_GAMBLE = 118,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_CHAT", Value=119)]
      MSG_CHAT = 119,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_EMAIL", Value=120)]
      MSG_EMAIL = 120,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_TRIAL", Value=121)]
      MSG_TRIAL = 121,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_RANKING", Value=122)]
      MSG_RANKING = 122,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_SYNC_PLAYER", Value=123)]
      MSG_SYNC_PLAYER = 123,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_SYNC_HERO", Value=124)]
      MSG_SYNC_HERO = 124,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_SYNC_SKILL", Value=125)]
      MSG_SYNC_SKILL = 125,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_SEND_HERO_INFO", Value=126)]
      MSG_SEND_HERO_INFO = 126,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_COMMON_MESSAGE", Value=127)]
      MSG_COMMON_MESSAGE = 127,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_DAILY_ACTIVITY", Value=128)]
      MSG_DAILY_ACTIVITY = 128,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_LOGIN_PLATFORM", Value=129)]
      MSG_LOGIN_PLATFORM = 129,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_LOGIN_GAME", Value=130)]
      MSG_LOGIN_GAME = 130,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_LOAD_MAINCITY", Value=131)]
      MSG_LOAD_MAINCITY = 131,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_PLAYER_OFF_LINE", Value=132)]
      MSG_PLAYER_OFF_LINE = 132,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_FRIEND", Value=133)]
      MSG_FRIEND = 133,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_SIGN", Value=134)]
      MSG_SIGN = 134,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_PEAK_ARENA", Value=139)]
      MSG_PEAK_ARENA = 139,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_ARENA", Value=140)]
      MSG_ARENA = 140,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_VIP", Value=141)]
      MSG_VIP = 141,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_HOT_POINT", Value=142)]
      MSG_HOT_POINT = 142,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_SETTING", Value=143)]
      MSG_SETTING = 143,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_OtherRoleAttr", Value=144)]
      MSG_OtherRoleAttr = 144,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_STORE", Value=145)]
      MSG_STORE = 145,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_UnendingWar", Value=146)]
      MSG_UnendingWar = 146,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_Worship", Value=147)]
      MSG_Worship = 147,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_TOWER", Value=148)]
      MSG_TOWER = 148,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_TASK", Value=149)]
      MSG_TASK = 149,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_GROUP", Value=150)]
      MSG_GROUP = 150,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_TIME", Value=151)]
      MSG_TIME = 151,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_GUIDE", Value=152)]
      MSG_GUIDE = 152,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_SECRET_AREA", Value=153)]
      MSG_SECRET_AREA = 153,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_ERRORINFO", Value=154)]
      MSG_ERRORINFO = 154,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_SECRET_MEMBER", Value=155)]
      MSG_SECRET_MEMBER = 155,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_Inlay", Value=156)]
      MSG_Inlay = 156,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_DATA_SYN", Value=157)]
      MSG_DATA_SYN = 157,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_BATTLE_TOWER", Value=158)]
      MSG_BATTLE_TOWER = 158,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_FASHION", Value=159)]
      MSG_FASHION = 159,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_MainMsg", Value=160)]
      MSG_MainMsg = 160,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_NEW_GUIDE", Value=161)]
      MSG_NEW_GUIDE = 161,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_PLOT", Value=162)]
      MSG_PLOT = 162,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_PLAYER_LOGOUT", Value=163)]
      MSG_PLAYER_LOGOUT = 163,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_DailyGif", Value=164)]
      MSG_DailyGif = 164,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_RED_POINT", Value=165)]
      MSG_RED_POINT = 165,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_FRSH_ACT", Value=166)]
      MSG_FRSH_ACT = 166,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_RECONNECT", Value=167)]
      MSG_RECONNECT = 167,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_PVE_INFO", Value=168)]
      MSG_PVE_INFO = 168,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_NOTICE", Value=169)]
      MSG_NOTICE = 169,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_GROUP_MEMBER_MANAGER", Value=170)]
      MSG_GROUP_MEMBER_MANAGER = 170,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_GROUP_PERSONAL", Value=171)]
      MSG_GROUP_PERSONAL = 171,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_GROUP_SKILL", Value=172)]
      MSG_GROUP_SKILL = 172,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_GIFT_CODE", Value=173)]
      MSG_GIFT_CODE = 173,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_CHARGE", Value=174)]
      MSG_CHARGE = 174,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_ACTIVITY_COUNTTYPE", Value=175)]
      MSG_ACTIVITY_COUNTTYPE = 175,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_PRIVILEGE", Value=176)]
      MSG_PRIVILEGE = 176,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_ACTIVITY_DATETYPE", Value=177)]
      MSG_ACTIVITY_DATETYPE = 177,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_ACTIVITY_RANKTYPE", Value=178)]
      MSG_ACTIVITY_RANKTYPE = 178,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_ACTIVITY_TIME_COUNT_TYPE", Value=179)]
      MSG_ACTIVITY_TIME_COUNT_TYPE = 179,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_ACTIVITY_DAILY_TYPE", Value=180)]
      MSG_ACTIVITY_DAILY_TYPE = 180,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_TAOIST", Value=200)]
      MSG_TAOIST = 200,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_FIX_EQUIP", Value=201)]
      MSG_FIX_EQUIP = 201,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_GROUP_SECRET", Value=202)]
      MSG_GROUP_SECRET = 202,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_MAGIC_SECRET", Value=203)]
      MSG_MAGIC_SECRET = 203,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_SDK_VERIFY", Value=996)]
      MSG_SDK_VERIFY = 996,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_NUMERIC_ANALYSIS", Value=997)]
      MSG_NUMERIC_ANALYSIS = 997,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_PLATFORMGS", Value=998)]
      MSG_PLATFORMGS = 998,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MSG_GAMEPRESS", Value=999)]
      MSG_GAMEPRESS = 999
    }
  
}