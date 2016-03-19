//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// Option: missing-value detection (*Specified/ShouldSerialize*/Reset*) enabled
    
// Generated from: DailyActivityService.proto
namespace DailyActivityService
{
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"MsgDailyActivityRequest")]
  public partial class MsgDailyActivityRequest : global::ProtoBuf.IExtensible
  {
    public MsgDailyActivityRequest() {}
    
    private EDailyActivityRequestType _requestType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"requestType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public EDailyActivityRequestType requestType
    {
      get { return _requestType; }
      set { _requestType = value; }
    }
    private int? _TaskId;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"TaskId", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int TaskId
    {
      get { return _TaskId?? default(int); }
      set { _TaskId = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool TaskIdSpecified
    {
      get { return this._TaskId != null; }
      set { if (value == (this._TaskId== null)) this._TaskId = value ? this.TaskId : (int?)null; }
    }
    private bool ShouldSerializeTaskId() { return TaskIdSpecified; }
    private void ResetTaskId() { TaskIdSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"MsgDailyActivityResponse")]
  public partial class MsgDailyActivityResponse : global::ProtoBuf.IExtensible
  {
    public MsgDailyActivityResponse() {}
    
    private EDailyActivityRequestType _responseType;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"responseType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public EDailyActivityRequestType responseType
    {
      get { return _responseType; }
      set { _responseType = value; }
    }
    private readonly global::System.Collections.Generic.List<DailyActivityInfo> _taskList = new global::System.Collections.Generic.List<DailyActivityInfo>();
    [global::ProtoBuf.ProtoMember(2, Name=@"taskList", DataFormat = global::ProtoBuf.DataFormat.Default)]
    public global::System.Collections.Generic.List<DailyActivityInfo> taskList
    {
      get { return _taskList; }
    }
  
    private eDailyActivityResultType? _resultType;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"resultType", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public eDailyActivityResultType resultType
    {
      get { return _resultType?? eDailyActivityResultType.SUCCESS; }
      set { _resultType = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool resultTypeSpecified
    {
      get { return this._resultType != null; }
      set { if (value == (this._resultType== null)) this._resultType = value ? this.resultType : (eDailyActivityResultType?)null; }
    }
    private bool ShouldSerializeresultType() { return resultTypeSpecified; }
    private void ResetresultType() { resultTypeSpecified = false; }
    
    private int? _TaskId;
    [global::ProtoBuf.ProtoMember(4, IsRequired = false, Name=@"TaskId", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int TaskId
    {
      get { return _TaskId?? default(int); }
      set { _TaskId = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool TaskIdSpecified
    {
      get { return this._TaskId != null; }
      set { if (value == (this._TaskId== null)) this._TaskId = value ? this.TaskId : (int?)null; }
    }
    private bool ShouldSerializeTaskId() { return TaskIdSpecified; }
    private void ResetTaskId() { TaskIdSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
  [global::System.Serializable, global::ProtoBuf.ProtoContract(Name=@"DailyActivityInfo")]
  public partial class DailyActivityInfo : global::ProtoBuf.IExtensible
  {
    public DailyActivityInfo() {}
    
    private int _taskId;
    [global::ProtoBuf.ProtoMember(1, IsRequired = true, Name=@"taskId", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int taskId
    {
      get { return _taskId; }
      set { _taskId = value; }
    }
    private int? _canGetReward;
    [global::ProtoBuf.ProtoMember(2, IsRequired = false, Name=@"canGetReward", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int canGetReward
    {
      get { return _canGetReward?? default(int); }
      set { _canGetReward = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool canGetRewardSpecified
    {
      get { return this._canGetReward != null; }
      set { if (value == (this._canGetReward== null)) this._canGetReward = value ? this.canGetReward : (int?)null; }
    }
    private bool ShouldSerializecanGetReward() { return canGetRewardSpecified; }
    private void ResetcanGetReward() { canGetRewardSpecified = false; }
    
    private int? _currentProgress;
    [global::ProtoBuf.ProtoMember(3, IsRequired = false, Name=@"currentProgress", DataFormat = global::ProtoBuf.DataFormat.TwosComplement)]
    public int currentProgress
    {
      get { return _currentProgress?? default(int); }
      set { _currentProgress = value; }
    }
    [global::System.Xml.Serialization.XmlIgnore]
    [global::System.ComponentModel.Browsable(false)]
    public bool currentProgressSpecified
    {
      get { return this._currentProgress != null; }
      set { if (value == (this._currentProgress== null)) this._currentProgress = value ? this.currentProgress : (int?)null; }
    }
    private bool ShouldSerializecurrentProgress() { return currentProgressSpecified; }
    private void ResetcurrentProgress() { currentProgressSpecified = false; }
    
    private global::ProtoBuf.IExtension extensionObject;
    global::ProtoBuf.IExtension global::ProtoBuf.IExtensible.GetExtensionObject(bool createIfMissing)
      { return global::ProtoBuf.Extensible.GetExtensionObject(ref extensionObject, createIfMissing); }
  }
  
    [global::ProtoBuf.ProtoContract(Name=@"EDailyActivityRequestType")]
    public enum EDailyActivityRequestType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"Task_List", Value=0)]
      Task_List = 0,
            
      [global::ProtoBuf.ProtoEnum(Name=@"Task_Finish", Value=1)]
      Task_Finish = 1
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"eDailyActivityResultType")]
    public enum eDailyActivityResultType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"SUCCESS", Value=1)]
      SUCCESS = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"FAIL", Value=2)]
      FAIL = 2
    }
  
}