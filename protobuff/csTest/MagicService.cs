//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// Option: missing-value detection (*Specified/ShouldSerialize*/Reset*) enabled
    
// Generated from: MagicService.proto
namespace MagicService
{
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"MagicItemData")]
  public partial class MagicItemData : global::ProtoBuf.IExtensible
  {
    public MagicItemData() {}
    
    private string _Id;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"Id", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string Id
    {
      get { return _Id; }
      set { _Id = value; }
    }
    private int _Count;
    [global::ProtoBuf.ProtoMember(2, IsRequired = true, Name=@"Count", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int Count
    {
      get { return _Count; }
      set { _Count = value; }
    }
    private int? _CriticalForgeType;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"CriticalForgeType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int CriticalForgeType
    {
      get { return _CriticalForgeType?? default(int); }
      set { _CriticalForgeType = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool CriticalForgeTypeSpecified
    {
      get { return this._CriticalForgeType != null; }
      set { if (value == (this._CriticalForgeType== null)) this._CriticalForgeType = value ? this.CriticalForgeType : (int?)null; }
    }
    private bool ShouldSerializeCriticalForgeType() { return CriticalForgeTypeSpecified; }
    private void ResetCriticalForgeType() { CriticalForgeTypeSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"MsgMagicRequest")]
  public partial class MsgMagicRequest : global::ProtoBuf.IExtensible
  {
    public MsgMagicRequest() {}
    
    private eMagicType _MagicType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"MagicType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public eMagicType MagicType
    {
      get { return _MagicType; }
      set { _MagicType = value; }
    }
    private string _id;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"id", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string id
    {
      get { return _id?? ""; }
      set { _id = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool idSpecified
    {
      get { return this._id != null; }
      set { if (value == (this._id== null)) this._id = value ? this.id : (string)null; }
    }
    private bool ShouldSerializeid() { return idSpecified; }
    private void Resetid() { idSpecified = false; }
    
    private int? _state;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"state", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int state
    {
      get { return _state?? default(int); }
      set { _state = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool stateSpecified
    {
      get { return this._state != null; }
      set { if (value == (this._state== null)) this._state = value ? this.state : (int?)null; }
    }
    private bool ShouldSerializestate() { return stateSpecified; }
    private void Resetstate() { stateSpecified = false; }
    
    private readonly global::System.Collections.Generic.List<MagicItemData> _magicItemData = new global::System.Collections.Generic.List<MagicItemData>();
    [global::ProtoBuf.ProtoMember(4, Name=@"magicItemData", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public global::System.Collections.Generic.List<MagicItemData> magicItemData
    {
      get { return _magicItemData; }
    }
  
    private bool? _autoForge;
    [global::ProtoBuf.ProtoMember(5, IsRequired = false, Name=@"autoForge", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public bool autoForge
    {
      get { return _autoForge?? default(bool); }
      set { _autoForge = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool autoForgeSpecified
    {
      get { return this._autoForge != null; }
      set { if (value == (this._autoForge== null)) this._autoForge = value ? this.autoForge : (bool?)null; }
    }
    private bool ShouldSerializeautoForge() { return autoForgeSpecified; }
    private void ResetautoForge() { autoForgeSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"MsgMagicResponse")]
  public partial class MsgMagicResponse : global::ProtoBuf.IExtensible
  {
    public MsgMagicResponse() {}
    
    private eMagicType _magicType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"magicType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public eMagicType magicType
    {
      get { return _magicType; }
      set { _magicType = value; }
    }
    private eMagicResultType? _eMagicResultType;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"eMagicResultType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public eMagicResultType eMagicResultType
    {
      get { return _eMagicResultType?? eMagicResultType.SUCCESS; }
      set { _eMagicResultType = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool eMagicResultTypeSpecified
    {
      get { return this._eMagicResultType != null; }
      set { if (value == (this._eMagicResultType== null)) this._eMagicResultType = value ? this.eMagicResultType : (eMagicResultType?)null; }
    }
    private bool ShouldSerializeeMagicResultType() { return eMagicResultTypeSpecified; }
    private void ReseteMagicResultType() { eMagicResultTypeSpecified = false; }
    
    private int? _newMagicModelId;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"newMagicModelId", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int newMagicModelId
    {
      get { return _newMagicModelId?? default(int); }
      set { _newMagicModelId = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool newMagicModelIdSpecified
    {
      get { return this._newMagicModelId != null; }
      set { if (value == (this._newMagicModelId== null)) this._newMagicModelId = value ? this.newMagicModelId : (int?)null; }
    }
    private bool ShouldSerializenewMagicModelId() { return newMagicModelIdSpecified; }
    private void ResetnewMagicModelId() { newMagicModelIdSpecified = false; }
    
    private int? _criticalRamdom;
    [global::ProtoBuf.ProtoMember(4, IsRequired = false, Name=@"criticalRamdom", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int criticalRamdom
    {
      get { return _criticalRamdom?? default(int); }
      set { _criticalRamdom = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool criticalRamdomSpecified
    {
      get { return this._criticalRamdom != null; }
      set { if (value == (this._criticalRamdom== null)) this._criticalRamdom = value ? this.criticalRamdom : (int?)null; }
    }
    private bool ShouldSerializecriticalRamdom() { return criticalRamdomSpecified; }
    private void ResetcriticalRamdom() { criticalRamdomSpecified = false; }
    
    private string _resultTip;
    [global::ProtoBuf.ProtoMember(5, IsRequired = false, Name=@"resultTip", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public string resultTip
    {
      get { return _resultTip?? ""; }
      set { _resultTip = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool resultTipSpecified
    {
      get { return this._resultTip != null; }
      set { if (value == (this._resultTip== null)) this._resultTip = value ? this.resultTip : (string)null; }
    }
    private bool ShouldSerializeresultTip() { return resultTipSpecified; }
    private void ResetresultTip() { resultTipSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
    [global::ProtoBuf.ProtoContract(Name=@"eMagicType")]
    public enum eMagicType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"Magic_TAKE", Value=0)]
      Magic_TAKE = 0,
            
      [global::ProtoBuf.ProtoEnum(Name=@"Magic_FORGE", Value=1)]
      Magic_FORGE = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"Magic_SMELT", Value=2)]
      Magic_SMELT = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"Magic_Upgrade", Value=3)]
      Magic_Upgrade = 3,
            
      [global::ProtoBuf.ProtoEnum(Name=@"Magic_Random", Value=4)]
      Magic_Random = 4
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"eMagicResultType")]
    public enum eMagicResultType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"SUCCESS", Value=1)]
      SUCCESS = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"FAIL", Value=2)]
      FAIL = 2
    }
  
}