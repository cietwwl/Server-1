//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// Option: missing-value detection (*Specified/ShouldSerialize*/Reset*) enabled
    
// Generated from: GroupMemberMgr.proto
// Note: requires additional types generated from: GroupCommon.proto
namespace groupproto
{
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"GroupMemberReceiveReqMsg")]
  public partial class GroupMemberReceiveReqMsg : global::ProtoBuf.IExtensible
  {
    public GroupMemberReceiveReqMsg() {}
    
    private bool _isReceive;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"isReceive", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public bool isReceive
    {
      get { return _isReceive; }
      set { _isReceive = value; }
    }
    private string _applyMemberId;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"applyMemberId", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string applyMemberId
    {
      get { return _applyMemberId?? ""; }
      set { _applyMemberId = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool applyMemberIdSpecified
    {
      get { return this._applyMemberId != null; }
      set { if (value == (this._applyMemberId== null)) this._applyMemberId = value ? this.applyMemberId : (string)null; }
    }
    private bool ShouldSerializeapplyMemberId() { return applyMemberIdSpecified; }
    private void ResetapplyMemberId() { applyMemberIdSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"GroupMemberReceiveRspMsg")]
  public partial class GroupMemberReceiveRspMsg : global::ProtoBuf.IExtensible
  {
    public GroupMemberReceiveRspMsg() {}
    
    private readonly global::System.Collections.Generic.List<string> _removeMemberId = new global::System.Collections.Generic.List<string>();
    [global::ProtoBuf.ProtoMember(1, Name=@"removeMemberId", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public global::System.Collections.Generic.List<string> removeMemberId
    {
      get { return _removeMemberId; }
    }
  
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"GroupNominatePostReqMsg")]
  public partial class GroupNominatePostReqMsg : global::ProtoBuf.IExtensible
  {
    public GroupNominatePostReqMsg() {}
    
    private string _memberId;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"memberId", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string memberId
    {
      get { return _memberId; }
      set { _memberId = value; }
    }
    private groupproto.GroupPost _post;
    [global::ProtoBuf.ProtoMember(2, IsRequired = true, Name=@"post", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public groupproto.GroupPost post
    {
      get { return _post; }
      set { _post = value; }
    }
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"GroupCancelNominatePostReqMsg")]
  public partial class GroupCancelNominatePostReqMsg : global::ProtoBuf.IExtensible
  {
    public GroupCancelNominatePostReqMsg() {}
    
    private string _memberId;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"memberId", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string memberId
    {
      get { return _memberId; }
      set { _memberId = value; }
    }
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"GroupEmailForAllReqMsg")]
  public partial class GroupEmailForAllReqMsg : global::ProtoBuf.IExtensible
  {
    public GroupEmailForAllReqMsg() {}
    
    private string _emailTitle;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"emailTitle", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string emailTitle
    {
      get { return _emailTitle; }
      set { _emailTitle = value; }
    }
    private string _emailContent;
    [global::ProtoBuf.ProtoMember(2, IsRequired = true, Name=@"emailContent", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string emailContent
    {
      get { return _emailContent; }
      set { _emailContent = value; }
    }
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"KickMemberReqMsg")]
  public partial class KickMemberReqMsg : global::ProtoBuf.IExtensible
  {
    public KickMemberReqMsg() {}
    
    private string _memberId;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"memberId", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string memberId
    {
      get { return _memberId; }
      set { _memberId = value; }
    }
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"GroupMemberMgrCommonReqMsg")]
  public partial class GroupMemberMgrCommonReqMsg : global::ProtoBuf.IExtensible
  {
    public GroupMemberMgrCommonReqMsg() {}
    
    private groupproto.RequestType _reqType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"reqType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public groupproto.RequestType reqType
    {
      get { return _reqType; }
      set { _reqType = value; }
    }
    private string _version;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"version", DataFormat = global::ProtoBuf.DataFormat.Default)]
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
    
    private groupproto.GroupMemberReceiveReqMsg _groupMemberReceiveReq = null;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"groupMemberReceiveReq", DataFormat = global::ProtoBuf.DataFormat.Default)]
    [global::System.ComponentModel.DefaultValue(null)]
    public groupproto.GroupMemberReceiveReqMsg groupMemberReceiveReq
    {
      get { return _groupMemberReceiveReq; }
      set { _groupMemberReceiveReq = value; }
    }
    private groupproto.GroupNominatePostReqMsg _groupNominatePostReq = null;
    [global::ProtoBuf.ProtoMember(4, IsRequired = false, Name=@"groupNominatePostReq", DataFormat = global::ProtoBuf.DataFormat.Default)]
    [global::System.ComponentModel.DefaultValue(null)]
    public groupproto.GroupNominatePostReqMsg groupNominatePostReq
    {
      get { return _groupNominatePostReq; }
      set { _groupNominatePostReq = value; }
    }
    private groupproto.GroupCancelNominatePostReqMsg _groupCancelNominatePostReq = null;
    [global::ProtoBuf.ProtoMember(5, IsRequired = false, Name=@"groupCancelNominatePostReq", DataFormat = global::ProtoBuf.DataFormat.Default)]
    [global::System.ComponentModel.DefaultValue(null)]
    public groupproto.GroupCancelNominatePostReqMsg groupCancelNominatePostReq
    {
      get { return _groupCancelNominatePostReq; }
      set { _groupCancelNominatePostReq = value; }
    }
    private groupproto.GroupEmailForAllReqMsg _groupEmailForAllReq = null;
    [global::ProtoBuf.ProtoMember(6, IsRequired = false, Name=@"groupEmailForAllReq", DataFormat = global::ProtoBuf.DataFormat.Default)]
    [global::System.ComponentModel.DefaultValue(null)]
    public groupproto.GroupEmailForAllReqMsg groupEmailForAllReq
    {
      get { return _groupEmailForAllReq; }
      set { _groupEmailForAllReq = value; }
    }
    private groupproto.KickMemberReqMsg _kickMemberReq = null;
    [global::ProtoBuf.ProtoMember(7, IsRequired = false, Name=@"kickMemberReq", DataFormat = global::ProtoBuf.DataFormat.Default)]
    [global::System.ComponentModel.DefaultValue(null)]
    public groupproto.KickMemberReqMsg kickMemberReq
    {
      get { return _kickMemberReq; }
      set { _kickMemberReq = value; }
    }
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"GroupMemberMgrCommonRspMsg")]
  public partial class GroupMemberMgrCommonRspMsg : global::ProtoBuf.IExtensible
  {
    public GroupMemberMgrCommonRspMsg() {}
    
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
    
    private groupproto.GroupMemberReceiveRspMsg _groupMemberReceiveRsp = null;
    [global::ProtoBuf.ProtoMember(4, IsRequired = false, Name=@"groupMemberReceiveRsp", DataFormat = global::ProtoBuf.DataFormat.Default)]
    [global::System.ComponentModel.DefaultValue(null)]
    public groupproto.GroupMemberReceiveRspMsg groupMemberReceiveRsp
    {
      get { return _groupMemberReceiveRsp; }
      set { _groupMemberReceiveRsp = value; }
    }
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
}