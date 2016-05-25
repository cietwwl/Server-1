//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// Option: missing-value detection (*Specified/ShouldSerialize*/Reset*) enabled
    
// Generated from: DataSyn.proto
namespace DataSyn
{
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"SynData")]
  public partial class SynData : global::ProtoBuf.IExtensible
  {
    public SynData() {}
    
    private string _id;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"id", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string id
    {
      get { return _id; }
      set { _id = value; }
    }
    private string _jsonData;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"jsonData", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string jsonData
    {
      get { return _jsonData?? ""; }
      set { _jsonData = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool jsonDataSpecified
    {
      get { return this._jsonData != null; }
      set { if (value == (this._jsonData== null)) this._jsonData = value ? this.jsonData : (string)null; }
    }
    private bool ShouldSerializejsonData() { return jsonDataSpecified; }
    private void ResetjsonData() { jsonDataSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"MsgDataSynList")]
  public partial class MsgDataSynList : global::ProtoBuf.IExtensible
  {
    public MsgDataSynList() {}
    
    private readonly global::System.Collections.Generic.List<DataSyn.MsgDataSyn> _msgDataSyn = new global::System.Collections.Generic.List<DataSyn.MsgDataSyn>();
    [global::ProtoBuf.ProtoMember(3, Name=@"msgDataSyn", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public global::System.Collections.Generic.List<DataSyn.MsgDataSyn> msgDataSyn
    {
      get { return _msgDataSyn; }
    }
  
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"DataSynVersion")]
  public partial class DataSynVersion : global::ProtoBuf.IExtensible
  {
    public DataSynVersion() {}
    
    private DataSyn.eSynType _synType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"synType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public DataSyn.eSynType synType
    {
      get { return _synType; }
      set { _synType = value; }
    }
    private int _version;
    [global::ProtoBuf.ProtoMember(2, IsRequired = true, Name=@"version", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int version
    {
      get { return _version; }
      set { _version = value; }
    }
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"MsgDataSynVersion")]
  public partial class MsgDataSynVersion : global::ProtoBuf.IExtensible
  {
    public MsgDataSynVersion() {}
    
    private readonly global::System.Collections.Generic.List<DataSyn.DataSynVersion> _version = new global::System.Collections.Generic.List<DataSyn.DataSynVersion>();
    [global::ProtoBuf.ProtoMember(1, Name=@"version", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public global::System.Collections.Generic.List<DataSyn.DataSynVersion> version
    {
      get { return _version; }
    }
  
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"MsgDataSyn")]
  public partial class MsgDataSyn : global::ProtoBuf.IExtensible
  {
    public MsgDataSyn() {}
    
    private DataSyn.eSynType _synType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"synType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public DataSyn.eSynType synType
    {
      get { return _synType; }
      set { _synType = value; }
    }
    private DataSyn.eSynOpType? _synOpType;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"synOpType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public DataSyn.eSynOpType synOpType
    {
      get { return _synOpType?? DataSyn.eSynOpType.UPDATE_LIST; }
      set { _synOpType = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool synOpTypeSpecified
    {
      get { return this._synOpType != null; }
      set { if (value == (this._synOpType== null)) this._synOpType = value ? this.synOpType : (DataSyn.eSynOpType?)null; }
    }
    private bool ShouldSerializesynOpType() { return synOpTypeSpecified; }
    private void ResetsynOpType() { synOpTypeSpecified = false; }
    
    private readonly global::System.Collections.Generic.List<DataSyn.SynData> _SynData = new global::System.Collections.Generic.List<DataSyn.SynData>();
    [global::ProtoBuf.ProtoMember(3, Name=@"SynData", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public global::System.Collections.Generic.List<DataSyn.SynData> SynData
    {
      get { return _SynData; }
    }
  
    private int? _version;
    [global::ProtoBuf.ProtoMember(4, IsRequired = false, Name=@"version", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int version
    {
      get { return _version?? default(int); }
      set { _version = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool versionSpecified
    {
      get { return this._version != null; }
      set { if (value == (this._version== null)) this._version = value ? this.version : (int?)null; }
    }
    private bool ShouldSerializeversion() { return versionSpecified; }
    private void Resetversion() { versionSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
    [global::ProtoBuf.ProtoContract(Name=@"eSynOpType")]
    public enum eSynOpType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"UPDATE_LIST", Value=1)]
      UPDATE_LIST = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"UPDATE_SINGLE", Value=2)]
      UPDATE_SINGLE = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ADD_SINGLE", Value=3)]
      ADD_SINGLE = 3,
            
      [global::ProtoBuf.ProtoEnum(Name=@"REMOVE_SINGLE", Value=4)]
      REMOVE_SINGLE = 4,
            
      [global::ProtoBuf.ProtoEnum(Name=@"UPDATE_FIELD", Value=5)]
      UPDATE_FIELD = 5
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"eSynType")]
    public enum eSynType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"COPY_LEVEL_RECORD", Value=1)]
      COPY_LEVEL_RECORD = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"COPY_MAP_RECORD", Value=2)]
      COPY_MAP_RECORD = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"SECRETAREA_INFO", Value=3)]
      SECRETAREA_INFO = 3,
            
      [global::ProtoBuf.ProtoEnum(Name=@"SECRETAREA_DEF_RECORD", Value=4)]
      SECRETAREA_DEF_RECORD = 4,
            
      [global::ProtoBuf.ProtoEnum(Name=@"SECRETAREA_USER_INFO", Value=5)]
      SECRETAREA_USER_INFO = 5,
            
      [global::ProtoBuf.ProtoEnum(Name=@"SECRETAREA_BATTLE_INFO", Value=6)]
      SECRETAREA_BATTLE_INFO = 6,
            
      [global::ProtoBuf.ProtoEnum(Name=@"SECRETAREA_USER_RECORD", Value=7)]
      SECRETAREA_USER_RECORD = 7,
            
      [global::ProtoBuf.ProtoEnum(Name=@"FASHION_ITEM", Value=8)]
      FASHION_ITEM = 8,
            
      [global::ProtoBuf.ProtoEnum(Name=@"EQUIP_ITEM", Value=9)]
      EQUIP_ITEM = 9,
            
      [global::ProtoBuf.ProtoEnum(Name=@"SKILL_ITEM", Value=10)]
      SKILL_ITEM = 10,
            
      [global::ProtoBuf.ProtoEnum(Name=@"INLAY_ITEM", Value=11)]
      INLAY_ITEM = 11,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ROLE_ATTR_ITEM", Value=12)]
      ROLE_ATTR_ITEM = 12,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ROLE_BASE_ITEM", Value=13)]
      ROLE_BASE_ITEM = 13,
            
      [global::ProtoBuf.ProtoEnum(Name=@"USER_HEROS", Value=14)]
      USER_HEROS = 14,
            
      [global::ProtoBuf.ProtoEnum(Name=@"USER_DATA", Value=15)]
      USER_DATA = 15,
            
      [global::ProtoBuf.ProtoEnum(Name=@"USER_GAME_DATA", Value=16)]
      USER_GAME_DATA = 16,
            
      [global::ProtoBuf.ProtoEnum(Name=@"USER_MAGIC", Value=17)]
      USER_MAGIC = 17,
            
      [global::ProtoBuf.ProtoEnum(Name=@"USER_ITEM_BAG", Value=18)]
      USER_ITEM_BAG = 18,
            
      [global::ProtoBuf.ProtoEnum(Name=@"DailyActivity", Value=19)]
      DailyActivity = 19,
            
      [global::ProtoBuf.ProtoEnum(Name=@"Guild", Value=20)]
      Guild = 20,
            
      [global::ProtoBuf.ProtoEnum(Name=@"Pve_UnendingWar", Value=21)]
      Pve_UnendingWar = 21,
            
      [global::ProtoBuf.ProtoEnum(Name=@"Pve_Trial", Value=22)]
      Pve_Trial = 22,
            
      [global::ProtoBuf.ProtoEnum(Name=@"Store_Data", Value=23)]
      Store_Data = 23,
            
      [global::ProtoBuf.ProtoEnum(Name=@"TASK_DATA", Value=24)]
      TASK_DATA = 24,
            
      [global::ProtoBuf.ProtoEnum(Name=@"VIP_DATA", Value=25)]
      VIP_DATA = 25,
            
      [global::ProtoBuf.ProtoEnum(Name=@"SETTING_DATA", Value=26)]
      SETTING_DATA = 26,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GUILD_USER_INFO", Value=27)]
      GUILD_USER_INFO = 27,
            
      [global::ProtoBuf.ProtoEnum(Name=@"SEVEN_DAY_GIF", Value=28)]
      SEVEN_DAY_GIF = 28,
            
      [global::ProtoBuf.ProtoEnum(Name=@"FRESHER_ATIVITY_DATA", Value=29)]
      FRESHER_ATIVITY_DATA = 29,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ASSISTANT", Value=30)]
      ASSISTANT = 30,
            
      [global::ProtoBuf.ProtoEnum(Name=@"VERSION_COPY", Value=31)]
      VERSION_COPY = 31,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GroupBaseData", Value=32)]
      GroupBaseData = 32,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GroupMemberData", Value=33)]
      GroupMemberData = 33,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GroupLog", Value=34)]
      GroupLog = 34,
            
      [global::ProtoBuf.ProtoEnum(Name=@"UserGroupAttributeData", Value=35)]
      UserGroupAttributeData = 35,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GroupCopyLevel", Value=36)]
      GroupCopyLevel = 36,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GroupCopyMap", Value=37)]
      GroupCopyMap = 37,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GroupCopyReward", Value=38)]
      GroupCopyReward = 38,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GroupApplyMemberData", Value=39)]
      GroupApplyMemberData = 39,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GroupResearchSkill", Value=40)]
      GroupResearchSkill = 40,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GroupStudySkill", Value=41)]
      GroupStudySkill = 41,
            
      [global::ProtoBuf.ProtoEnum(Name=@"Charge", Value=42)]
      Charge = 42,
            
      [global::ProtoBuf.ProtoEnum(Name=@"POWER_INFO", Value=43)]
      POWER_INFO = 43,
            
      [global::ProtoBuf.ProtoEnum(Name=@"USER_TMP_GAME_DATA", Value=44)]
      USER_TMP_GAME_DATA = 44,
            
      [global::ProtoBuf.ProtoEnum(Name=@"HERO_FETTERS", Value=45)]
      HERO_FETTERS = 45,
            
      [global::ProtoBuf.ProtoEnum(Name=@"FIX_NORM_EQUIP", Value=46)]
      FIX_NORM_EQUIP = 46,
            
      [global::ProtoBuf.ProtoEnum(Name=@"FIX_EXP_EQUIP", Value=47)]
      FIX_EXP_EQUIP = 47,
            
      [global::ProtoBuf.ProtoEnum(Name=@"UserGroupSecretData", Value=48)]
      UserGroupSecretData = 48,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GroupSecretData", Value=49)]
      GroupSecretData = 49,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MagicSecretData", Value=50)]
      MagicSecretData = 50,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MagicChapterData", Value=51)]
      MagicChapterData = 51,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ActivityCountType", Value=60)]
      ActivityCountType = 60,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ActivityTimeCardType", Value=61)]
      ActivityTimeCardType = 61,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ActivityRateType", Value=62)]
      ActivityRateType = 62,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ActivityDateType", Value=63)]
      ActivityDateType = 63,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ActivityTimeSaleType", Value=64)]
      ActivityTimeSaleType = 64,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ActivityDateSaleType", Value=65)]
      ActivityDateSaleType = 65,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ActivityRankType", Value=66)]
      ActivityRankType = 66,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ActivityExchangeType", Value=67)]
      ActivityExchangeType = 67,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ActivityTimeCountType", Value=68)]
      ActivityTimeCountType = 68,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ActivityDailyType", Value=69)]
      ActivityDailyType = 69,
            
      [global::ProtoBuf.ProtoEnum(Name=@"QuestionList", Value=80)]
      QuestionList = 80
    }
  
}