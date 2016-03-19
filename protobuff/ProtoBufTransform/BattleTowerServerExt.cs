using System.Collections.Generic;
namespace BattleTowerServer{
	public partial interface IBattleTowerConfig{
		int EveryFloorSweepTime{get;}
		string ChestDescription{get;}
	}
	public partial class BattleTowerConfig : IBattleTowerConfig{
	}
	public partial interface IBattleTowerCommonRspMsg{
		IERequestType ReqType{get;}
		IEResponseState RspState{get;}
		byte[] rspBody{get;}
		IBattleTowerConfig Config{get;}
	}
	public partial class BattleTowerCommonRspMsg : IBattleTowerCommonRspMsg{
		public IERequestType ReqType{get{return this.reqType;}}
		public IEResponseState RspState{get{return this.rspState;}}
		public IBattleTowerConfig Config{get{return this.config;}}
	}
	public partial interface IResetRspMsg{
		int defaultChanllengeCount{get;}
	}
	public partial class ResetRspMsg : IResetRspMsg{
	}
	public partial interface IOpenMainViewRspMsg{
		IOverFriendInfoMsg OverFriendInfoMsg{get;}
		int leftResetTimes{get;}
		int highestFloor{get;}
		int BossInfoMsgCount{get;}
		IEnumerable<IBossInfoMsg> BossInfoMsgList{get;}
		int sweepLeftTotalTime{get;}
		int sweepFloor{get;}
		int sweepFloorLeftTime{get;}
		int RewardInfoMsgCount{get;}
		IEnumerable<IRewardInfoMsg> RewardInfoMsgList{get;}
		bool NeedSweepEnd{get;}
	}
	public partial class OpenMainViewRspMsg : IOpenMainViewRspMsg{
		public IOverFriendInfoMsg OverFriendInfoMsg{get{return this.overFriendInfoMsg;}}
		public int BossInfoMsgCount{get{return this.bossInfoMsg.Count;}}
		public IEnumerable<IBossInfoMsg> BossInfoMsgList{get{return this.bossInfoMsg.ToReadonly<BossInfoMsg,IBossInfoMsg>();}}
		public int RewardInfoMsgCount{get{return this.rewardInfoMsg.Count;}}
		public IEnumerable<IRewardInfoMsg> RewardInfoMsgList{get{return this.rewardInfoMsg.ToReadonly<RewardInfoMsg,IRewardInfoMsg>();}}
	}
	public partial interface IOpenChallengeViewRspMsg{
		int groupId{get;}
		bool isFirst{get;}
		int RewardInfoMsgCount{get;}
		IEnumerable<IRewardInfoMsg> RewardInfoMsgList{get;}
		int copyId{get;}
	}
	public partial class OpenChallengeViewRspMsg : IOpenChallengeViewRspMsg{
		public int RewardInfoMsgCount{get{return this.rewardInfoMsg.Count;}}
		public IEnumerable<IRewardInfoMsg> RewardInfoMsgList{get{return this.rewardInfoMsg.ToReadonly<RewardInfoMsg,IRewardInfoMsg>();}}
	}
	public partial interface IGetFriendBattleTowerRankInfoRspMsg{
		int friendSize{get;}
		int RankingRoleInfoMsgCount{get;}
		IEnumerable<IRankingRoleInfoMsg> RankingRoleInfoMsgList{get;}
		int pageIndex{get;}
	}
	public partial class GetFriendBattleTowerRankInfoRspMsg : IGetFriendBattleTowerRankInfoRspMsg{
		public int RankingRoleInfoMsgCount{get{return this.rankingRoleInfoMsg.Count;}}
		public IEnumerable<IRankingRoleInfoMsg> RankingRoleInfoMsgList{get{return this.rankingRoleInfoMsg.ToReadonly<RankingRoleInfoMsg,IRankingRoleInfoMsg>();}}
	}
	public partial interface IGetStrategyListRspMsg{
		int RankingRoleInfoMsgCount{get;}
		IEnumerable<IRankingRoleInfoMsg> RankingRoleInfoMsgList{get;}
	}
	public partial class GetStrategyListRspMsg : IGetStrategyListRspMsg{
		public int RankingRoleInfoMsgCount{get{return this.rankingRoleInfoMsg.Count;}}
		public IEnumerable<IRankingRoleInfoMsg> RankingRoleInfoMsgList{get{return this.rankingRoleInfoMsg.ToReadonly<RankingRoleInfoMsg,IRankingRoleInfoMsg>();}}
	}
	public partial interface IOpenTryLuckViewRspMsg{
		int copperKeyNum{get;}
		int silverKeyNum{get;}
		int goldKeyNum{get;}
	}
	public partial class OpenTryLuckViewRspMsg : IOpenTryLuckViewRspMsg{
	}
	public partial interface ISweepStartRspMsg{
		int BossInfoMsgCount{get;}
		IEnumerable<IBossInfoMsg> BossInfoMsgList{get;}
	}
	public partial class SweepStartRspMsg : ISweepStartRspMsg{
		public int BossInfoMsgCount{get{return this.bossInfoMsg.Count;}}
		public IEnumerable<IBossInfoMsg> BossInfoMsgList{get{return this.bossInfoMsg.ToReadonly<BossInfoMsg,IBossInfoMsg>();}}
	}
	public partial interface ISweepEndRspMsg{
		int RewardInfoMsgCount{get;}
		IEnumerable<IRewardInfoMsg> RewardInfoMsgList{get;}
	}
	public partial class SweepEndRspMsg : ISweepEndRspMsg{
		public int RewardInfoMsgCount{get{return this.rewardInfoMsg.Count;}}
		public IEnumerable<IRewardInfoMsg> RewardInfoMsgList{get{return this.rewardInfoMsg.ToReadonly<RewardInfoMsg,IRewardInfoMsg>();}}
	}
	public partial interface IUseLuckyKeyRspMsg{
		int RewardInfoMsgCount{get;}
		IEnumerable<IRewardInfoMsg> RewardInfoMsgList{get;}
	}
	public partial class UseLuckyKeyRspMsg : IUseLuckyKeyRspMsg{
		public int RewardInfoMsgCount{get{return this.rewardInfoMsg.Count;}}
		public IEnumerable<IRewardInfoMsg> RewardInfoMsgList{get{return this.rewardInfoMsg.ToReadonly<RewardInfoMsg,IRewardInfoMsg>();}}
	}
	public partial interface IChallengeStartRspMsg{
		IBossInfoMsg BossInfoMsg{get;}
	}
	public partial class ChallengeStartRspMsg : IChallengeStartRspMsg{
		public IBossInfoMsg BossInfoMsg{get{return this.bossInfoMsg;}}
	}
	public partial interface IChallengeEndRspMsg{
		int RewardInfoMsgCount{get;}
		IEnumerable<IRewardInfoMsg> RewardInfoMsgList{get;}
	}
	public partial class ChallengeEndRspMsg : IChallengeEndRspMsg{
		public int RewardInfoMsgCount{get{return this.rewardInfoMsg.Count;}}
		public IEnumerable<IRewardInfoMsg> RewardInfoMsgList{get{return this.rewardInfoMsg.ToReadonly<RewardInfoMsg,IRewardInfoMsg>();}}
	}
	public partial interface IChallengeBossEndRspMsg{
		int RewardInfoMsgCount{get;}
		IEnumerable<IRewardInfoMsg> RewardInfoMsgList{get;}
	}
	public partial class ChallengeBossEndRspMsg : IChallengeBossEndRspMsg{
		public int RewardInfoMsgCount{get{return this.rewardInfoMsg.Count;}}
		public IEnumerable<IRewardInfoMsg> RewardInfoMsgList{get{return this.rewardInfoMsg.ToReadonly<RewardInfoMsg,IRewardInfoMsg>();}}
	}
	public partial interface IOverFriendInfoMsg{
		string headIcon{get;}
		int level{get;}
		string name{get;}
		int floorGap{get;}
	}
	public partial class OverFriendInfoMsg : IOverFriendInfoMsg{
	}
	public partial interface IBossInfoMsg{
		int bossId{get;}
		int bossCfgId{get;}
		long bossRemainTime{get;}
		int bossInFloor{get;}
	}
	public partial class BossInfoMsg : IBossInfoMsg{
	}
	public partial interface IRewardInfoMsg{
		int type{get;}
		int count{get;}
	}
	public partial class RewardInfoMsg : IRewardInfoMsg{
	}
	public partial interface IRankingRoleInfoMsg{
		int rankIndex{get;}
		string name{get;}
		string headIcon{get;}
		int highestFloor{get;}
		int level{get;}
		string magicIcon{get;}
		int RankingHeroInfoMsgCount{get;}
		IEnumerable<IRankingHeroInfoMsg> RankingHeroInfoMsgList{get;}
		bool isMyself{get;}
	}
	public partial class RankingRoleInfoMsg : IRankingRoleInfoMsg{
		public int RankingHeroInfoMsgCount{get{return this.rankingHeroInfoMsg.Count;}}
		public IEnumerable<IRankingHeroInfoMsg> RankingHeroInfoMsgList{get{return this.rankingHeroInfoMsg.ToReadonly<RankingHeroInfoMsg,IRankingHeroInfoMsg>();}}
	}
	public partial interface IRankingHeroInfoMsg{
		string heroId{get;}
		int starNum{get;}
		int quality{get;}
		int level{get;}
		bool isMainRole{get;}
	}
	public partial class RankingHeroInfoMsg : IRankingHeroInfoMsg{
	}
}
