//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// Option: missing-value detection (*Specified/ShouldSerialize*/Reset*) enabled
    
// Generated from: GroupCommon.proto
namespace groupproto
{
    [global::ProtoBuf.ProtoContract(Name=@"GroupValidateType")]
    public enum GroupValidateType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"NON", Value=0)]
      NON = 0,
            
      [global::ProtoBuf.ProtoEnum(Name=@"FIRST_VALIDATE", Value=1)]
      FIRST_VALIDATE = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"NON_VALIDATE", Value=2)]
      NON_VALIDATE = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"JOIN_REFUSED", Value=3)]
      JOIN_REFUSED = 3
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"GroupPost")]
    public enum GroupPost
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"LEADER", Value=1)]
      LEADER = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"ASSISTANT_LEADER", Value=2)]
      ASSISTANT_LEADER = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"OFFICEHOLDER", Value=3)]
      OFFICEHOLDER = 3,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MEMBER", Value=4)]
      MEMBER = 4
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"GroupState")]
    public enum GroupState
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"NORMAL", Value=0)]
      NORMAL = 0,
            
      [global::ProtoBuf.ProtoEnum(Name=@"DISOLUTION", Value=1)]
      DISOLUTION = 1
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"GroupRecommentType")]
    public enum GroupRecommentType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"RANK_RECOMMENT", Value=1)]
      RANK_RECOMMENT = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"RANDOM_RECOMMENT", Value=2)]
      RANDOM_RECOMMENT = 2
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"GroupSkillType")]
    public enum GroupSkillType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"ATTACK", Value=1)]
      ATTACK = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"DEFENCE", Value=2)]
      DEFENCE = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"UNIVERSAL", Value=3)]
      UNIVERSAL = 3
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"GroupLogType")]
    public enum GroupLogType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"NEW_JOIN_GROUP", Value=1)]
      NEW_JOIN_GROUP = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"QUIT_GROUP", Value=2)]
      QUIT_GROUP = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"CHANGE_POST", Value=3)]
      CHANGE_POST = 3,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GROUP_UPGRADE", Value=4)]
      GROUP_UPGRADE = 4,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GROUP_SKILL_REASERCH", Value=5)]
      GROUP_SKILL_REASERCH = 5,
            
      [global::ProtoBuf.ProtoEnum(Name=@"LOG_CANCEL_NOMINATE", Value=6)]
      LOG_CANCEL_NOMINATE = 6,
            
      [global::ProtoBuf.ProtoEnum(Name=@"LOG_KICK_GROUP", Value=7)]
      LOG_KICK_GROUP = 7,
            
      [global::ProtoBuf.ProtoEnum(Name=@"LOG_LEADER_QUIT", Value=8)]
      LOG_LEADER_QUIT = 8,
            
      [global::ProtoBuf.ProtoEnum(Name=@"LOG_LEADER_TIMEOUT_TRANSFER", Value=9)]
      LOG_LEADER_TIMEOUT_TRANSFER = 9,
            
      [global::ProtoBuf.ProtoEnum(Name=@"LOG_LEADER_TRANSFER", Value=10)]
      LOG_LEADER_TRANSFER = 10
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"GroupFunction")]
    public enum GroupFunction
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"TRANSFER_LEADER_POST", Value=1)]
      TRANSFER_LEADER_POST = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"NOMINATE_ASSISTANT_LEADER", Value=2)]
      NOMINATE_ASSISTANT_LEADER = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"NOMINATE_OFFICEHOLDER", Value=3)]
      NOMINATE_OFFICEHOLDER = 3,
            
      [global::ProtoBuf.ProtoEnum(Name=@"CANCEL_NOMINATE", Value=4)]
      CANCEL_NOMINATE = 4,
            
      [global::ProtoBuf.ProtoEnum(Name=@"KICK_OF_GROUP", Value=5)]
      KICK_OF_GROUP = 5,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MODIFY_ANNOUNCEMENT", Value=6)]
      MODIFY_ANNOUNCEMENT = 6,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GROUP_SETTING", Value=7)]
      GROUP_SETTING = 7,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GROUP_EMAIL_ALL", Value=8)]
      GROUP_EMAIL_ALL = 8,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MEMBER_RECEIVE", Value=9)]
      MEMBER_RECEIVE = 9,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GROUP_DONATE", Value=10)]
      GROUP_DONATE = 10,
            
      [global::ProtoBuf.ProtoEnum(Name=@"RESEARCH_GROUP_SKILL", Value=11)]
      RESEARCH_GROUP_SKILL = 11,
            
      [global::ProtoBuf.ProtoEnum(Name=@"STUDY_GROUP_SKILL", Value=12)]
      STUDY_GROUP_SKILL = 12,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GROUP_STORE", Value=13)]
      GROUP_STORE = 13,
            
      [global::ProtoBuf.ProtoEnum(Name=@"JOIN_SECERT_AREA", Value=14)]
      JOIN_SECERT_AREA = 14,
            
      [global::ProtoBuf.ProtoEnum(Name=@"OPEN_GROUP_DUPLICATE", Value=15)]
      OPEN_GROUP_DUPLICATE = 15,
            
      [global::ProtoBuf.ProtoEnum(Name=@"JOIN_GROUP_DUPLICATE", Value=16)]
      JOIN_GROUP_DUPLICATE = 16,
            
      [global::ProtoBuf.ProtoEnum(Name=@"JOIN_GROUP_BATTLE", Value=17)]
      JOIN_GROUP_BATTLE = 17,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MODIFY_GROUP_NAME", Value=18)]
      MODIFY_GROUP_NAME = 18,
            
      [global::ProtoBuf.ProtoEnum(Name=@"DISMISS_THE_GROUP", Value=19)]
      DISMISS_THE_GROUP = 19,
            
      [global::ProtoBuf.ProtoEnum(Name=@"CANCEL_DISMISS_THE_GROUP", Value=20)]
      CANCEL_DISMISS_THE_GROUP = 20
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"GroupDonateType")]
    public enum GroupDonateType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"MONEY_DONATE", Value=1)]
      MONEY_DONATE = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"TOKEN_DONATE", Value=2)]
      TOKEN_DONATE = 2
    }
  
    [global::ProtoBuf.ProtoContract(Name=@"RequestType")]
    public enum RequestType
    {
            
      [global::ProtoBuf.ProtoEnum(Name=@"CREATE_GROUP_TYPE", Value=1)]
      CREATE_GROUP_TYPE = 1,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GET_GROUP_INFO_TYPE", Value=2)]
      GET_GROUP_INFO_TYPE = 2,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GET_GROUP_RANK_INFO_TYPE", Value=3)]
      GET_GROUP_RANK_INFO_TYPE = 3,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MODIFY_ANNOUNCEMENT_TYPE", Value=4)]
      MODIFY_ANNOUNCEMENT_TYPE = 4,
            
      [global::ProtoBuf.ProtoEnum(Name=@"MODIFY_GROUP_NAME_TYPE", Value=5)]
      MODIFY_GROUP_NAME_TYPE = 5,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GROUP_SETTING_TYPE", Value=6)]
      GROUP_SETTING_TYPE = 6,
            
      [global::ProtoBuf.ProtoEnum(Name=@"FIND_GROUP_TYPE", Value=7)]
      FIND_GROUP_TYPE = 7,
            
      [global::ProtoBuf.ProtoEnum(Name=@"APPLY_JOIN_GROUP_TYPE", Value=8)]
      APPLY_JOIN_GROUP_TYPE = 8,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GROUP_MEMBER_RECEIVE_TYPE", Value=9)]
      GROUP_MEMBER_RECEIVE_TYPE = 9,
            
      [global::ProtoBuf.ProtoEnum(Name=@"NOMINATE_POST_TYPE", Value=10)]
      NOMINATE_POST_TYPE = 10,
            
      [global::ProtoBuf.ProtoEnum(Name=@"CANCEL_NOMINATE_TYPE", Value=11)]
      CANCEL_NOMINATE_TYPE = 11,
            
      [global::ProtoBuf.ProtoEnum(Name=@"OPEN_DONATE_VIEW_TYPE", Value=12)]
      OPEN_DONATE_VIEW_TYPE = 12,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GROUP_DONATE_TYPE", Value=13)]
      GROUP_DONATE_TYPE = 13,
            
      [global::ProtoBuf.ProtoEnum(Name=@"TRANSFER_LEADER_POST_TYPE", Value=14)]
      TRANSFER_LEADER_POST_TYPE = 14,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GROUP_EMAIL_FOR_ALL_TYPE", Value=15)]
      GROUP_EMAIL_FOR_ALL_TYPE = 15,
            
      [global::ProtoBuf.ProtoEnum(Name=@"RESEARCH_GROUP_SKILL_TYPE", Value=16)]
      RESEARCH_GROUP_SKILL_TYPE = 16,
            
      [global::ProtoBuf.ProtoEnum(Name=@"STUDY_GROUP_SKILL_TYPE", Value=17)]
      STUDY_GROUP_SKILL_TYPE = 17,
            
      [global::ProtoBuf.ProtoEnum(Name=@"THE_LOG_OF_GROUP_TYPE", Value=18)]
      THE_LOG_OF_GROUP_TYPE = 18,
            
      [global::ProtoBuf.ProtoEnum(Name=@"QUIT_GROUP_TYPE", Value=19)]
      QUIT_GROUP_TYPE = 19,
            
      [global::ProtoBuf.ProtoEnum(Name=@"KICK_MEMBER_TYPE", Value=20)]
      KICK_MEMBER_TYPE = 20,
            
      [global::ProtoBuf.ProtoEnum(Name=@"DISMISS_THE_GROUP_TYPE", Value=21)]
      DISMISS_THE_GROUP_TYPE = 21,
            
      [global::ProtoBuf.ProtoEnum(Name=@"CANCEL_DISMISS_THE_GROUP_TYPE", Value=22)]
      CANCEL_DISMISS_THE_GROUP_TYPE = 22,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GROUP_RECOMMENT_TYPE", Value=23)]
      GROUP_RECOMMENT_TYPE = 23,
            
      [global::ProtoBuf.ProtoEnum(Name=@"GET_APPLY_MEMBER_LIST_TYPE", Value=24)]
      GET_APPLY_MEMBER_LIST_TYPE = 24,
            
      [global::ProtoBuf.ProtoEnum(Name=@"CHECK_GROUP_DATA_TYPE", Value=25)]
      CHECK_GROUP_DATA_TYPE = 25,
            
      [global::ProtoBuf.ProtoEnum(Name=@"OPEN_RESEARCH_SKILL_VIEW_TYPE", Value=26)]
      OPEN_RESEARCH_SKILL_VIEW_TYPE = 26,
            
      [global::ProtoBuf.ProtoEnum(Name=@"OPEN_STUDY_SKILL_VIEW_TYPE", Value=27)]
      OPEN_STUDY_SKILL_VIEW_TYPE = 27
    }
  
}