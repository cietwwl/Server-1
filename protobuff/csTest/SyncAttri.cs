//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// Option: missing-value detection (*Specified/ShouldSerialize*/Reset*) enabled
    
// Generated from: SyncAttri.proto
namespace SyncAttri
{
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"TagAttriData")]
  public partial class TagAttriData : global::ProtoBuf.IExtensible
  {
    public TagAttriData() {}
    
    private int _AttrId;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"AttrId", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int AttrId
    {
      get { return _AttrId; }
      set { _AttrId = value; }
    }
    private string _AttValueStr;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"AttValueStr", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string AttValueStr
    {
      get { return _AttValueStr?? ""; }
      set { _AttValueStr = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool AttValueStrSpecified
    {
      get { return this._AttValueStr != null; }
      set { if (value == (this._AttValueStr== null)) this._AttValueStr = value ? this.AttValueStr : (string)null; }
    }
    private bool ShouldSerializeAttValueStr() { return AttValueStrSpecified; }
    private void ResetAttValueStr() { AttValueStrSpecified = false; }
    
    private double? _AttValue;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"AttValue", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public double AttValue
    {
      get { return _AttValue?? default(double); }
      set { _AttValue = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool AttValueSpecified
    {
      get { return this._AttValue != null; }
      set { if (value == (this._AttValue== null)) this._AttValue = value ? this.AttValue : (double?)null; }
    }
    private bool ShouldSerializeAttValue() { return AttValueSpecified; }
    private void ResetAttValue() { AttValueSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"MsgSyncAttriResponse")]
  public partial class MsgSyncAttriResponse : global::ProtoBuf.IExtensible
  {
    public MsgSyncAttriResponse() {}
    
    private string _roleId;
    [global::ProtoBuf.ProtoMember(1, IsRequired = false, Name=@"roleId", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string roleId
    {
      get { return _roleId?? ""; }
      set { _roleId = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool roleIdSpecified
    {
      get { return this._roleId != null; }
      set { if (value == (this._roleId== null)) this._roleId = value ? this.roleId : (string)null; }
    }
    private bool ShouldSerializeroleId() { return roleIdSpecified; }
    private void ResetroleId() { roleIdSpecified = false; }
    
    private readonly global::System.Collections.Generic.List<SyncAttri.TagAttriData> _syncDatas = new global::System.Collections.Generic.List<SyncAttri.TagAttriData>();
    [global::ProtoBuf.ProtoMember(2, Name=@"syncDatas", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public global::System.Collections.Generic.List<SyncAttri.TagAttriData> syncDatas
    {
      get { return _syncDatas; }
    }
  
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
}