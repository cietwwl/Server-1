using System.Collections.Generic;
namespace Group{
	public partial interface IMemberInfo{
		string headIcon{get;}
		int level{get;}
		int job{get;}
		int vipLevel{get;}
		string memberName{get;}
		IGroupPost MemberPost{get;}
		int privateContribution{get;}
		string memberId{get;}
		int logoutTime{get;}
	}
	public partial class MemberInfo : IMemberInfo{
		public IGroupPost MemberPost{get{return this.memberPost;}}
	}
	public partial interface IApplyMemberInfo{
		string headIcon{get;}
		int level{get;}
		string name{get;}
		int fighting{get;}
		long applyTime{get;}
		string memberId{get;}
	}
	public partial class ApplyMemberInfo : IApplyMemberInfo{
	}
	public partial interface IGroupRankEntryInfo{
		int rankIndex{get;}
		string groupIcon{get;}
		string groupName{get;}
		int groupLevel{get;}
		int groupExp{get;}
		int groupMemberNum{get;}
	}
	public partial class GroupRankEntryInfo : IGroupRankEntryInfo{
	}
	public partial interface IGroupSimpleInfo{
		string groupId{get;}
		string groupName{get;}
		string headIcon{get;}
		int groupLevel{get;}
		int groupMemberNum{get;}
		string groupDeclaration{get;}
		int rankIndex{get;}
	}
	public partial class GroupSimpleInfo : IGroupSimpleInfo{
	}
	public partial interface IGroupLogInfo{
		long logDistanceTime{get;}
	}
	public partial class GroupLogInfo : IGroupLogInfo{
	}
	public partial interface ICreateGroupReqMsg{
		string groupName{get;}
		string icon{get;}
	}
	public partial class CreateGroupReqMsg : ICreateGroupReqMsg{
	}
	public partial interface ICreateGroupRspMsg{
		string groupName{get;}
		string groupIcon{get;}
		string groupId{get;}
		int groupRankIndex{get;}
		int groupLevel{get;}
		int groupExp{get;}
		int groupMemberNum{get;}
		int groupSupplies{get;}
		string announcement{get;}
		int MemberInfoCount{get;}
		IEnumerable<IMemberInfo> MemberInfoList{get;}
		IGroupPost Post{get;}
		IGroupValidateType ValidateType{get;}
		int applyLevel{get;}
	}
	public partial class CreateGroupRspMsg : ICreateGroupRspMsg{
		public int MemberInfoCount{get{return this.memberInfo.Count;}}
		public IEnumerable<IMemberInfo> MemberInfoList{get{return this.memberInfo.ToReadonly<MemberInfo,IMemberInfo>();}}
		public IGroupPost Post{get{return this.post;}}
		public IGroupValidateType ValidateType{get{return this.validateType;}}
	}
	public partial interface IGetGroupInfoRspMsg{
		string groupName{get;}
		string groupIcon{get;}
		string groupId{get;}
		string groupRankIndex{get;}
		int groupLevel{get;}
		int groupExp{get;}
		int groupMemberNum{get;}
		int groupSupplies{get;}
		string announcement{get;}
		int MemberInfoCount{get;}
		IEnumerable<IMemberInfo> MemberInfoList{get;}
		IGroupPost Post{get;}
		IGroupValidateType ValidateType{get;}
		int applyLevel{get;}
	}
	public partial class GetGroupInfoRspMsg : IGetGroupInfoRspMsg{
		public int MemberInfoCount{get{return this.memberInfo.Count;}}
		public IEnumerable<IMemberInfo> MemberInfoList{get{return this.memberInfo.ToReadonly<MemberInfo,IMemberInfo>();}}
		public IGroupPost Post{get{return this.post;}}
		public IGroupValidateType ValidateType{get{return this.validateType;}}
	}
	public partial interface IGetGroupRankRspMsg{
		int GroupRankEntryInfoCount{get;}
		IEnumerable<IGroupRankEntryInfo> GroupRankEntryInfoList{get;}
	}
	public partial class GetGroupRankRspMsg : IGetGroupRankRspMsg{
		public int GroupRankEntryInfoCount{get{return this.groupRankEntryInfo.Count;}}
		public IEnumerable<IGroupRankEntryInfo> GroupRankEntryInfoList{get{return this.groupRankEntryInfo.ToReadonly<GroupRankEntryInfo,IGroupRankEntryInfo>();}}
	}
	public partial interface IModifyAnnouncementReqMsg{
		string announcement{get;}
	}
	public partial class ModifyAnnouncementReqMsg : IModifyAnnouncementReqMsg{
	}
	public partial interface IModifyAnnouncementRspMsg{
		string announcement{get;}
	}
	public partial class ModifyAnnouncementRspMsg : IModifyAnnouncementRspMsg{
	}
	public partial interface IModifyGroupNameReqMsg{
		string groupName{get;}
	}
	public partial class ModifyGroupNameReqMsg : IModifyGroupNameReqMsg{
	}
	public partial interface IGroupSettingReqMsg{
		string groupIcon{get;}
		string declaration{get;}
		IGroupValidateType ValidateType{get;}
		int applyLevel{get;}
	}
	public partial class GroupSettingReqMsg : IGroupSettingReqMsg{
		public IGroupValidateType ValidateType{get{return this.validateType;}}
	}
	public partial interface IFindGroupReqMsg{
		string groupId{get;}
	}
	public partial class FindGroupReqMsg : IFindGroupReqMsg{
	}
	public partial interface IFindGroupRspMsg{
		IGroupSimpleInfo GroupSimpleInfo{get;}
	}
	public partial class FindGroupRspMsg : IFindGroupRspMsg{
		public IGroupSimpleInfo GroupSimpleInfo{get{return this.groupSimpleInfo;}}
	}
	public partial interface IApplyJoinGroupReqMsg{
		string groupId{get;}
	}
	public partial class ApplyJoinGroupReqMsg : IApplyJoinGroupReqMsg{
	}
	public partial interface IGroupMemberReceiveReqMsg{
		bool isReceive{get;}
		string applyMemberId{get;}
	}
	public partial class GroupMemberReceiveReqMsg : IGroupMemberReceiveReqMsg{
	}
	public partial interface IGroupMemberReceiveRspMsg{
		int RemoveMemberIdCount{get;}
		IEnumerable<string> RemoveMemberIdList{get;}
	}
	public partial class GroupMemberReceiveRspMsg : IGroupMemberReceiveRspMsg{
		public int RemoveMemberIdCount{get{return this.removeMemberId.Count;}}
		public IEnumerable<string> RemoveMemberIdList{get{return this.removeMemberId.ToReadonly<string,string>();}}
	}
	public partial interface IGroupNominatePostReqMsg{
		string memberId{get;}
		IGroupPost Post{get;}
	}
	public partial class GroupNominatePostReqMsg : IGroupNominatePostReqMsg{
		public IGroupPost Post{get{return this.post;}}
	}
	public partial interface IGroupCancelNominatePostReqMsg{
		string memberId{get;}
	}
	public partial class GroupCancelNominatePostReqMsg : IGroupCancelNominatePostReqMsg{
	}
	public partial interface IOpenDonateViewRspMsg{
		int leftDonateTimes{get;}
		int privateContribution{get;}
		int OpenDonateIdCount{get;}
		IEnumerable<int> OpenDonateIdList{get;}
		int totalDonateTimes{get;}
	}
	public partial class OpenDonateViewRspMsg : IOpenDonateViewRspMsg{
		public int OpenDonateIdCount{get{return this.openDonateId.Count;}}
		public IEnumerable<int> OpenDonateIdList{get{return this.openDonateId.ToReadonly<int,int>();}}
	}
	public partial interface IGroupDonateReqMsg{
		int donateId{get;}
	}
	public partial class GroupDonateReqMsg : IGroupDonateReqMsg{
	}
	public partial interface IGroupDonateRspMsg{
		int groupLevel{get;}
		int groupExp{get;}
		int groupSupplies{get;}
		int leftDonateTimes{get;}
		int privateContribution{get;}
		int totalDonateTimes{get;}
	}
	public partial class GroupDonateRspMsg : IGroupDonateRspMsg{
	}
	public partial interface ITransferGroupLeaderPostReqMsg{
		string memberId{get;}
	}
	public partial class TransferGroupLeaderPostReqMsg : ITransferGroupLeaderPostReqMsg{
	}
	public partial interface IGroupEmailForAllReqMsg{
		string emailTitle{get;}
		string emailContent{get;}
	}
	public partial class GroupEmailForAllReqMsg : IGroupEmailForAllReqMsg{
	}
	public partial interface IResearchGroupSkillReqMsg{
		string skillId{get;}
	}
	public partial class ResearchGroupSkillReqMsg : IResearchGroupSkillReqMsg{
	}
	public partial interface IResearchGroupSkillRspMsg{
		string skillId{get;}
		int skillLevel{get;}
	}
	public partial class ResearchGroupSkillRspMsg : IResearchGroupSkillRspMsg{
	}
	public partial interface IStudyGroupSkillReqMsg{
		string skillId{get;}
		int skillLevel{get;}
	}
	public partial class StudyGroupSkillReqMsg : IStudyGroupSkillReqMsg{
	}
	public partial interface IStudyGroupSkillRspMsg{
		string skillId{get;}
		int skillLevel{get;}
	}
	public partial class StudyGroupSkillRspMsg : IStudyGroupSkillRspMsg{
	}
	public partial interface IGetLogOfGroupRspMsg{
		int GroupLogCount{get;}
		IEnumerable<IGroupLogInfo> GroupLogList{get;}
	}
	public partial class GetLogOfGroupRspMsg : IGetLogOfGroupRspMsg{
		public int GroupLogCount{get{return this.groupLog.Count;}}
		public IEnumerable<IGroupLogInfo> GroupLogList{get{return this.groupLog.ToReadonly<GroupLogInfo,IGroupLogInfo>();}}
	}
	public partial interface IKickMemberReqMsg{
		string memberId{get;}
	}
	public partial class KickMemberReqMsg : IKickMemberReqMsg{
	}
	public partial interface IKickMemberRspMsg{
		string memberId{get;}
	}
	public partial class KickMemberRspMsg : IKickMemberRspMsg{
	}
	public partial interface IGroupRecommentReqMsg{
		IGroupRecommentType RecommentType{get;}
	}
	public partial class GroupRecommentReqMsg : IGroupRecommentReqMsg{
		public IGroupRecommentType RecommentType{get{return this.recommentType;}}
	}
	public partial interface IGroupRecommentRspMsg{
		int GroupSimpleInfoCount{get;}
		IEnumerable<IGroupSimpleInfo> GroupSimpleInfoList{get;}
	}
	public partial class GroupRecommentRspMsg : IGroupRecommentRspMsg{
		public int GroupSimpleInfoCount{get{return this.groupSimpleInfo.Count;}}
		public IEnumerable<IGroupSimpleInfo> GroupSimpleInfoList{get{return this.groupSimpleInfo.ToReadonly<GroupSimpleInfo,IGroupSimpleInfo>();}}
	}
	public partial interface IGroupCommonReqMsg{
		IRequestType ReqType{get;}
		byte[] reqBody{get;}
	}
	public partial class GroupCommonReqMsg : IGroupCommonReqMsg{
		public IRequestType ReqType{get{return this.reqType;}}
	}
	public partial interface IGroupCommonRspMsg{
		IRequestType ReqType{get;}
		bool isSuccess{get;}
		string tipMsg{get;}
		byte[] rspBody{get;}
	}
	public partial class GroupCommonRspMsg : IGroupCommonRspMsg{
		public IRequestType ReqType{get{return this.reqType;}}
	}
}
