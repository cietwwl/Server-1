//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// Option: missing-value detection (*Specified/ShouldSerialize*/Reset*) enabled
    
// Generated from: PlatformGSMsg.proto
namespace PlatformGSMsg
{
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"UserInfoRequest")]
  public partial class UserInfoRequest : global::ProtoBuf.IExtensible
  {
    public UserInfoRequest() {}
    
    private ePlatformGSMsgType _platformGSMsgType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"platformGSMsgType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public ePlatformGSMsgType platformGSMsgType
    {
      get { return _platformGSMsgType; }
      set { _platformGSMsgType = value; }
    }
    private string _userId;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"userId", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string userId
    {
      get { return _userId?? ""; }
      set { _userId = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool userIdSpecified
    {
      get { return this._userId != null; }
      set { if (value == (this._userId== null)) this._userId = value ? this.userId : (string)null; }
    }
    private bool ShouldSerializeuserId() { return userIdSpecified; }
    private void ResetuserId() { userIdSpecified = false; }
    
    private string _accountId;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"accountId", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string accountId
    {
      get { return _accountId?? ""; }
      set { _accountId = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool accountIdSpecified
    {
      get { return this._accountId != null; }
      set { if (value == (this._accountId== null)) this._accountId = value ? this.accountId : (string)null; }
    }
    private bool ShouldSerializeaccountId() { return accountIdSpecified; }
    private void ResetaccountId() { accountIdSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"UserInfoResponse")]
  public partial class UserInfoResponse : global::ProtoBuf.IExtensible
  {
    public UserInfoResponse() {}
    
    private ePlatformGSMsgType _platformGSMsgType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"platformGSMsgType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public ePlatformGSMsgType platformGSMsgType
    {
      get { return _platformGSMsgType; }
      set { _platformGSMsgType = value; }
    }
    private string _accountId;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"accountId", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string accountId
    {
      get { return _accountId?? ""; }
      set { _accountId = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool accountIdSpecified
    {
      get { return this._accountId != null; }
      set { if (value == (this._accountId== null)) this._accountId = value ? this.accountId : (string)null; }
    }
    private bool ShouldSerializeaccountId() { return accountIdSpecified; }
    private void ResetaccountId() { accountIdSpecified = false; }
    
    private int? _level;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"level", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int level
    {
      get { return _level?? default(int); }
      set { _level = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool levelSpecified
    {
      get { return this._level != null; }
      set { if (value == (this._level== null)) this._level = value ? this.level : (int?)null; }
    }
    private bool ShouldSerializelevel() { return levelSpecified; }
    private void Resetlevel() { levelSpecified = false; }
    
    private int? _vipLevel;
    [global::ProtoBuf.ProtoMember(4, IsRequired = false, Name=@"vipLevel", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int vipLevel
    {
      get { return _vipLevel?? default(int); }
      set { _vipLevel = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool vipLevelSpecified
    {
      get { return this._vipLevel != null; }
      set { if (value == (this._vipLevel== null)) this._vipLevel = value ? this.vipLevel : (int?)null; }
    }
    private bool ShouldSerializevipLevel() { return vipLevelSpecified; }
    private void ResetvipLevel() { vipLevelSpecified = false; }
    
    private string _headImage;
    [global::ProtoBuf.ProtoMember(5, IsRequired = false, Name=@"headImage", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string headImage
    {
      get { return _headImage?? ""; }
      set { _headImage = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool headImageSpecified
    {
      get { return this._headImage != null; }
      set { if (value == (this._headImage== null)) this._headImage = value ? this.headImage : (string)null; }
    }
    private bool ShouldSerializeheadImage() { return headImageSpecified; }
    private void ResetheadImage() { headImageSpecified = false; }
    
    private int? _career;
    [global::ProtoBuf.ProtoMember(6, IsRequired = false, Name=@"career", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int career
    {
      get { return _career?? default(int); }
      set { _career = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool careerSpecified
    {
      get { return this._career != null; }
      set { if (value == (this._career== null)) this._career = value ? this.career : (int?)null; }
    }
    private bool ShouldSerializecareer() { return careerSpecified; }
    private void Resetcareer() { careerSpecified = false; }
    
    private string _userName;
    [global::ProtoBuf.ProtoMember(7, IsRequired = false, Name=@"userName", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string userName
    {
      get { return _userName?? ""; }
      set { _userName = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool userNameSpecified
    {
      get { return this._userName != null; }
      set { if (value == (this._userName== null)) this._userName = value ? this.userName : (string)null; }
    }
    private bool ShouldSerializeuserName() { return userNameSpecified; }
    private void ResetuserName() { userNameSpecified = false; }
    
    private long? _lastLoginTime;
    [global::ProtoBuf.ProtoMember(8, IsRequired = false, Name=@"lastLoginTime", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public long lastLoginTime
    {
      get { return _lastLoginTime?? default(long); }
      set { _lastLoginTime = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool lastLoginTimeSpecified
    {
      get { return this._lastLoginTime != null; }
      set { if (value == (this._lastLoginTime== null)) this._lastLoginTime = value ? this.lastLoginTime : (long?)null; }
    }
    private bool ShouldSerializelastLoginTime() { return lastLoginTimeSpecified; }
    private void ResetlastLoginTime() { lastLoginTimeSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"UserStatusRequest")]
  public partial class UserStatusRequest : global::ProtoBuf.IExtensible
  {
    public UserStatusRequest() {}
    
    private ePlatformGSMsgType _platformGSMsgType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"platformGSMsgType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public ePlatformGSMsgType platformGSMsgType
    {
      get { return _platformGSMsgType; }
      set { _platformGSMsgType = value; }
    }
    private int? _userId;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"userId", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int userId
    {
      get { return _userId?? default(int); }
      set { _userId = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool userIdSpecified
    {
      get { return this._userId != null; }
      set { if (value == (this._userId== null)) this._userId = value ? this.userId : (int?)null; }
    }
    private bool ShouldSerializeuserId() { return userIdSpecified; }
    private void ResetuserId() { userIdSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"UserStatusResponse")]
  public partial class UserStatusResponse : global::ProtoBuf.IExtensible
  {
    public UserStatusResponse() {}
    
    private ePlatformGSMsgType _platformGSMsgType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"platformGSMsgType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public ePlatformGSMsgType platformGSMsgType
    {
      get { return _platformGSMsgType; }
      set { _platformGSMsgType = value; }
    }
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
    [global::ProtoBuf.ProtoContract(Name=@"ePlatformGSMsgType")]
    public enum ePlatformGSMsgType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"USER_INFO", Value=1)]
      USER_INFO = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"USER_STATUS", Value=2)]
      USER_STATUS = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GAME_SERVER_STATUS", Value=3)]
      GAME_SERVER_STATUS = 3
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"eServerStatusType")]
    public enum eServerStatusType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"SERVER_OPEN", Value=1)]
      SERVER_OPEN = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"SERVER_SHUTDOWN", Value=2)]
      SERVER_SHUTDOWN = 2
    }
  
}