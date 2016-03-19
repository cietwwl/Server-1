//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// Option: missing-value detection (*Specified/ShouldSerialize*/Reset*) enabled
    
// Generated from: GameLogin.proto
// Note: requires additional types generated from: GuidanceService.proto
// Note: requires additional types generated from: PlotView.proto
namespace GameLogin
{
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"GameLoginRequest")]
  public partial class GameLoginRequest : global::ProtoBuf.IExtensible
  {
    public GameLoginRequest() {}
    
    private eGameLoginType _loginType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"loginType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public eGameLoginType loginType
    {
      get { return _loginType; }
      set { _loginType = value; }
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
    
    private string _password;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"password", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string password
    {
      get { return _password?? ""; }
      set { _password = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool passwordSpecified
    {
      get { return this._password != null; }
      set { if (value == (this._password== null)) this._password = value ? this.password : (string)null; }
    }
    private bool ShouldSerializepassword() { return passwordSpecified; }
    private void Resetpassword() { passwordSpecified = false; }
    
    private int? _zoneId;
    [global::ProtoBuf.ProtoMember(4, IsRequired = false, Name=@"zoneId", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int zoneId
    {
      get { return _zoneId?? default(int); }
      set { _zoneId = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool zoneIdSpecified
    {
      get { return this._zoneId != null; }
      set { if (value == (this._zoneId== null)) this._zoneId = value ? this.zoneId : (int?)null; }
    }
    private bool ShouldSerializezoneId() { return zoneIdSpecified; }
    private void ResetzoneId() { zoneIdSpecified = false; }
    
    private string _nick;
    [global::ProtoBuf.ProtoMember(5, IsRequired = false, Name=@"nick", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string nick
    {
      get { return _nick?? ""; }
      set { _nick = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool nickSpecified
    {
      get { return this._nick != null; }
      set { if (value == (this._nick== null)) this._nick = value ? this.nick : (string)null; }
    }
    private bool ShouldSerializenick() { return nickSpecified; }
    private void Resetnick() { nickSpecified = false; }
    
    private int? _sex;
    [global::ProtoBuf.ProtoMember(6, IsRequired = false, Name=@"sex", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int sex
    {
      get { return _sex?? default(int); }
      set { _sex = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool sexSpecified
    {
      get { return this._sex != null; }
      set { if (value == (this._sex== null)) this._sex = value ? this.sex : (int?)null; }
    }
    private bool ShouldSerializesex() { return sexSpecified; }
    private void Resetsex() { sexSpecified = false; }
    
    private string _clientInfoJson;
    [global::ProtoBuf.ProtoMember(7, IsRequired = false, Name=@"clientInfoJson", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string clientInfoJson
    {
      get { return _clientInfoJson?? ""; }
      set { _clientInfoJson = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool clientInfoJsonSpecified
    {
      get { return this._clientInfoJson != null; }
      set { if (value == (this._clientInfoJson== null)) this._clientInfoJson = value ? this.clientInfoJson : (string)null; }
    }
    private bool ShouldSerializeclientInfoJson() { return clientInfoJsonSpecified; }
    private void ResetclientInfoJson() { clientInfoJsonSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"GameLoginResponse")]
  public partial class GameLoginResponse : global::ProtoBuf.IExtensible
  {
    public GameLoginResponse() {}
    
    private eLoginResultType _resultType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"resultType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public eLoginResultType resultType
    {
      get { return _resultType; }
      set { _resultType = value; }
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
    
    private string _error;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"error", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string error
    {
      get { return _error?? ""; }
      set { _error = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool errorSpecified
    {
      get { return this._error != null; }
      set { if (value == (this._error== null)) this._error = value ? this.error : (string)null; }
    }
    private bool ShouldSerializeerror() { return errorSpecified; }
    private void Reseterror() { errorSpecified = false; }
    
    private string _version;
    [global::ProtoBuf.ProtoMember(4, IsRequired = false, Name=@"version", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string version
    {
      get { return _version?? ""; }
      set { _version = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool versionSpecified
    {
      get { return this._version != null; }
      set { if (value == (this._version== null)) this._version = value ? this.version : (string)null; }
    }
    private bool ShouldSerializeversion() { return versionSpecified; }
    private void Resetversion() { versionSpecified = false; }
    
    private long? _serverTime;
    [global::ProtoBuf.ProtoMember(5, IsRequired = false, Name=@"serverTime", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public long serverTime
    {
      get { return _serverTime?? default(long); }
      set { _serverTime = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool serverTimeSpecified
    {
      get { return this._serverTime != null; }
      set { if (value == (this._serverTime== null)) this._serverTime = value ? this.serverTime : (long?)null; }
    }
    private bool ShouldSerializeserverTime() { return serverTimeSpecified; }
    private void ResetserverTime() { serverTimeSpecified = false; }
    
    private GuidanceService.GuidanceResponse _guidance = null;
    [global::ProtoBuf.ProtoMember(6, IsRequired = false, Name=@"guidance", DataFormat = global::ProtoBuf.DataFormat.Default)]
    [global::System.ComponentModel.DefaultValue(null)]
    public GuidanceService.GuidanceResponse guidance
    {
      get { return _guidance; }
      set { _guidance = value; }
    }
    private PlotView.PlotResponse _plot = null;
    [global::ProtoBuf.ProtoMember(7, IsRequired = false, Name=@"plot", DataFormat = global::ProtoBuf.DataFormat.Default)]
    [global::System.ComponentModel.DefaultValue(null)]
    public PlotView.PlotResponse plot
    {
      get { return _plot; }
      set { _plot = value; }
    }
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
    [global::ProtoBuf.ProtoContract(Name=@"eGameLoginType")]
    public enum eGameLoginType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"GAME_LOGIN", Value=1)]
      GAME_LOGIN = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"LOAD_MAINCITY", Value=2)]
      LOAD_MAINCITY = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"CREATE_ROLE", Value=3)]
      CREATE_ROLE = 3,
            
      [global::ProtoBuf.ProtoEnum(Name=@"repeat", Value=4)]
      repeat = 4
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"eLoginResultType")]
    public enum eLoginResultType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"SUCCESS", Value=1)]
      SUCCESS = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"FAIL", Value=2)]
      FAIL = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"NO_ROLE", Value=3)]
      NO_ROLE = 3,
            
      [global::ProtoBuf.ProtoEnum(Name=@"RepeatSUCCESS", Value=4)]
      RepeatSUCCESS = 4,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ServerMainTain", Value=5)]
      ServerMainTain = 5
    }
  
}