//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// Option: missing-value detection (*Specified/ShouldSerialize*/Reset*) enabled
    
// Generated from: GroupCopyAdminService.proto
namespace groupproto
{
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"GroupCooyAdminCommonReqMsg")]
  public partial class GroupCooyAdminCommonReqMsg : global::ProtoBuf.IExtensible
  {
    public GroupCooyAdminCommonReqMsg() {}
    
    private groupproto.RequestType _reqType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"reqType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public groupproto.RequestType reqType
    {
      get { return _reqType; }
      set { _reqType = value; }
    }
    private string _versionJson;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"versionJson", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string versionJson
    {
      get { return _versionJson?? ""; }
      set { _versionJson = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool versionJsonSpecified
    {
      get { return this._versionJson != null; }
      set { if (value == (this._versionJson== null)) this._versionJson = value ? this.versionJson : (string)null; }
    }
    private bool ShouldSerializeversionJson() { return versionJsonSpecified; }
    private void ResetversionJson() { versionJsonSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"GroupCooyAdminCommonRspMsg")]
  public partial class GroupCooyAdminCommonRspMsg : global::ProtoBuf.IExtensible
  {
    public GroupCooyAdminCommonRspMsg() {}
    
    private groupproto.RequestType _reqType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"reqType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public groupproto.RequestType reqType
    {
      get { return _reqType; }
      set { _reqType = value; }
    }
    private bool _isSuccess;
    [global::ProtoBuf.ProtoMember(2, IsRequired = true, Name=@"isSuccess", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public bool isSuccess
    {
      get { return _isSuccess; }
      set { _isSuccess = value; }
    }
    private string _tipMsg;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"tipMsg", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string tipMsg
    {
      get { return _tipMsg?? ""; }
      set { _tipMsg = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool tipMsgSpecified
    {
      get { return this._tipMsg != null; }
      set { if (value == (this._tipMsg== null)) this._tipMsg = value ? this.tipMsg : (string)null; }
    }
    private bool ShouldSerializetipMsg() { return tipMsgSpecified; }
    private void ResettipMsg() { tipMsgSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
    [global::ProtoBuf.ProtoContract(Name=@"RequestType")]
    public enum RequestType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"GET_COPY_INFO", Value=1)]
      GET_COPY_INFO = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"COPY_OPEN", Value=2)]
      COPY_OPEN = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"COPY_RESET", Value=3)]
      COPY_RESET = 3
    }
  
}