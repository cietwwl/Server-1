package daotest;

import java.util.List;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.bm.rank.IRankEntry;
import com.bm.rank.RankService;
import com.bm.rank.RankType;



@ContextConfiguration(locations={"classpath*:applicationContext.xml"})
public class DaoTester extends AbstractJUnit4SpringContextTests{


	@Test
	public void testDao() {

//		PeakArenaRankDao rankDao = PeakArenaRankDao.getInstance();
//		
//		PeakArenaRankEntry entry = new PeakArenaRankEntry();
//		
//		entry.setUserId("testuserId-3");
//		entry.setScore(3);
//		
//		rankDao.update(entry);
//		PeakArenaRankEntry fromDb = rankDao.get(entry.rankId());
//		System.out.println(fromDb.rankId());
//		System.out.println(fromDb.getScore());
//		System.out.println(fromDb.getUserId());
		
		
		RankService rankService = new RankService(RankType.PEAK_ARENA);		
		List<IRankEntry> rankList = rankService.getRankList(0, 10);
		for (IRankEntry iRankEntry : rankList) {
			System.out.println(iRankEntry.rankId());
		}
		
	}
	
	
	
}

