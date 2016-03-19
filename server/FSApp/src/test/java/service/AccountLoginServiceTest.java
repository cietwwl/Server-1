
package service;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.google.protobuf.ByteString;
//import com.rw.service.login.account.AccountLoginHandler;
import com.rwproto.AccountLoginProtos.AccountInfo;
import com.rwproto.AccountLoginProtos.AccountLoginRequest;
import com.rwproto.AccountLoginProtos.AccountLoginResponse;
import com.rwproto.AccountLoginProtos.ZoneInfo;
import com.rwproto.AccountLoginProtos.eAccountLoginType;
import com.rwproto.AccountLoginProtos.eLoginResultType;

@ContextConfiguration(locations={"classpath*:applicationContext.xml"})
public class AccountLoginServiceTest extends AbstractJUnit4SpringContextTests{

	

//	@Resource(name="fsNettyControler")
//	private FsNettyControler fsNettyControler;	
	

	private String account = "testAccount";
	private String password = "testpassword";
	@Before
	public void setUp() throws Exception {}

	
	@Test
	public void test() throws Exception {
//		AccountInfo accountInfo = AccountInfo.newBuilder()
//				.setAccountId(account).setPassword(password).build();
//		
//		AccountLoginRequest loginRequest = AccountLoginRequest.newBuilder()
//				.setAccount(accountInfo).setLoginType(eAccountLoginType.ACCOUNT_LOGIN).build();
//		
//		ByteString repByteString =  AccountLoginHandler.getInstance().accountLogin(loginRequest);
//		AccountLoginResponse response = AccountLoginResponse.parseFrom(repByteString);
//		ZoneInfo zoneInfo = response.getLastZone();
//		
//		Assert.assertTrue(response.getResultType() == eLoginResultType.SUCCESS);
//		Assert.assertTrue(zoneInfo!=null);
		
	}


	@After
	public void tearDown() throws Exception {
		
	}


}
