//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// Option: missing-value detection (*Specified/ShouldSerialize*/Reset*) enabled
    
// Generated from: MainMsg.proto
namespace MainMsg
{
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"MainMsgResponse")]
  public partial class MainMsgResponse : global::ProtoBuf.IExtensible
  {
    public MainMsgResponse() {}
    
    private int? _id;
    [global::ProtoBuf.ProtoMember(1, IsRequired = false, Name=@"id", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int id
    {
      get { return _id?? default(int); }
      set { _id = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool idSpecified
    {
      get { return this._id != null; }
      set { if (value == (this._id== null)) this._id = value ? this.id : (int?)null; }
    }
    private bool ShouldSerializeid() { return idSpecified; }
    private void Resetid() { idSpecified = false; }
    
    private EMsgType? _type;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"type", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public EMsgType type
    {
      get { return _type?? EMsgType.PmdMsg; }
      set { _type = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool typeSpecified
    {
      get { return this._type != null; }
      set { if (value == (this._type== null)) this._type = value ? this.type : (EMsgType?)null; }
    }
    private bool ShouldSerializetype() { return typeSpecified; }
    private void Resettype() { typeSpecified = false; }
    
    private string _info1;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"info1", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string info1
    {
      get { return _info1?? ""; }
      set { _info1 = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool info1Specified
    {
      get { return this._info1 != null; }
      set { if (value == (this._info1== null)) this._info1 = value ? this.info1 : (string)null; }
    }
    private bool ShouldSerializeinfo1() { return info1Specified; }
    private void Resetinfo1() { info1Specified = false; }
    
    private string _info2;
    [global::ProtoBuf.ProtoMember(4, IsRequired = false, Name=@"info2", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string info2
    {
      get { return _info2?? ""; }
      set { _info2 = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool info2Specified
    {
      get { return this._info2 != null; }
      set { if (value == (this._info2== null)) this._info2 = value ? this.info2 : (string)null; }
    }
    private bool ShouldSerializeinfo2() { return info2Specified; }
    private void Resetinfo2() { info2Specified = false; }
    
    private string _info3;
    [global::ProtoBuf.ProtoMember(5, IsRequired = false, Name=@"info3", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string info3
    {
      get { return _info3?? ""; }
      set { _info3 = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool info3Specified
    {
      get { return this._info3 != null; }
      set { if (value == (this._info3== null)) this._info3 = value ? this.info3 : (string)null; }
    }
    private bool ShouldSerializeinfo3() { return info3Specified; }
    private void Resetinfo3() { info3Specified = false; }
    
    private string _info4;
    [global::ProtoBuf.ProtoMember(6, IsRequired = false, Name=@"info4", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string info4
    {
      get { return _info4?? ""; }
      set { _info4 = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool info4Specified
    {
      get { return this._info4 != null; }
      set { if (value == (this._info4== null)) this._info4 = value ? this.info4 : (string)null; }
    }
    private bool ShouldSerializeinfo4() { return info4Specified; }
    private void Resetinfo4() { info4Specified = false; }
    
    private string _info5;
    [global::ProtoBuf.ProtoMember(7, IsRequired = false, Name=@"info5", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string info5
    {
      get { return _info5?? ""; }
      set { _info5 = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool info5Specified
    {
      get { return this._info5 != null; }
      set { if (value == (this._info5== null)) this._info5 = value ? this.info5 : (string)null; }
    }
    private bool ShouldSerializeinfo5() { return info5Specified; }
    private void Resetinfo5() { info5Specified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"MainMsgRequest")]
  public partial class MainMsgRequest : global::ProtoBuf.IExtensible
  {
    public MainMsgRequest() {}
    
    private int? _id;
    [global::ProtoBuf.ProtoMember(1, IsRequired = false, Name=@"id", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int id
    {
      get { return _id?? default(int); }
      set { _id = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool idSpecified
    {
      get { return this._id != null; }
      set { if (value == (this._id== null)) this._id = value ? this.id : (int?)null; }
    }
    private bool ShouldSerializeid() { return idSpecified; }
    private void Resetid() { idSpecified = false; }
    
    private EMsgType? _type;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"type", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public EMsgType type
    {
      get { return _type?? EMsgType.PmdMsg; }
      set { _type = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool typeSpecified
    {
      get { return this._type != null; }
      set { if (value == (this._type== null)) this._type = value ? this.type : (EMsgType?)null; }
    }
    private bool ShouldSerializetype() { return typeSpecified; }
    private void Resettype() { typeSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
    [global::ProtoBuf.ProtoContract(Name=@"EMsgType")]
    public enum EMsgType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"PmdMsg", Value=0)]
      PmdMsg = 0,
            
      [global::ProtoBuf.ProtoEnum(Name=@"OtherMsg", Value=1)]
      OtherMsg = 1
    }
  
}