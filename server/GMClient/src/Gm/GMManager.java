package Gm;

import io.netty.channel.Channel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Gm.Task.GMAddCoin;
import Gm.Task.GMAddGold;
import Gm.Task.GMGetRankList;
import Gm.Task.GMUserInfo;
import Gm.Task.GmBlockPlayer;
import Gm.Task.GmBlockRelease;
import Gm.Task.GmChatBanPlayer;
import Gm.Task.GmDeleteGameNotice;
import Gm.Task.GmEditGameNotice;
import Gm.Task.GmEditPlatformNotice;
import Gm.Task.GmEmailAll;
import Gm.Task.GmEmailSingleCheck;
import Gm.Task.GmEmailWhiteList;
import Gm.Task.GmFindDaoistList;
import Gm.Task.GmFindHeroList;
import Gm.Task.GmFindMagicList;
import Gm.Task.GmHotUpdateTest;
import Gm.Task.GmKickOffPlayer;
import Gm.Task.GmOnlineCount;
import Gm.Task.GmOpExp;
import Gm.Task.GmResponsePlayerQuestion;
import Gm.Task.GmServerSwitch;
import Gm.Task.GmSwitchBIGm;
import Gm.Task.GmUserDetailInfo;
import Gm.Task.GmViewEmailList;
import Gm.Task.GmViewEquipments;
import Gm.Task.GmViewGameNotice;
import Gm.Task.GmViewPlatformNotice;
import Gm.Task.GmWhiteListModify;
import Gm.Task.GmWhiteListSwitch;
import Json.JsonUtil;

public class GMManager {

	private static final short protno = 11;
	private static Channel channel;
	public static String ACCOUNT_VALUE = "gm";
	public static String PASSWORD_VALUE = "123456"; //passwd
	final static short PROTO_NO = 11;
	
	private final static Map<Integer, AGMHandler> ProcessMap = new HashMap<Integer, AGMHandler>();
	
	

	static{
//		ProcessMap.put(1001, new GmServerSwitch());
//		ProcessMap.put(20014, new GmEmailWhiteList());
//		ProcessMap.put(20015, new GmEmailAll());
//		ProcessMap.put(20005, new GmEditGameNotice());
//		ProcessMap.put(20006, new GmViewGameNotice());
//		ProcessMap.put(20007, new GmDeleteGameNotice());
//		ProcessMap.put(4001, new GmSwitchBIGm());
//		ProcessMap.put(20003, new GmEditPlatformNotice());
//		ProcessMap.put(20004, new GmViewPlatformNotice());
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		if(args.length < 3){
//			System.out.println("Argument fail!");
//			return;
//		}

//		
//		String ip = args[0];
//		int port = Integer.parseInt(args[1]);
//		String pwd = args[1];
//		int process = Integer.parseInt(args[1]);
//		PASSWORD_VALUE = pwd;
//		AGMHandler handler = ProcessMap.get(process);
//		if(handler == null){
//			return;
//		}
//		
//		String ip = "192.168.2.137";
//		int port = 12345;
		
//		GmOnlineLimitModify handler = new GmOnlineLimitModify(); 
//		GmEmailWhiteList handler = new GmEmailWhiteList();
//		GmViewEmailList handler = new GmViewEmailList();
//		GmEmailAll handler = new GmEmailAll();
//		GmServerSwitch handler = new GmServerSwitch();
//		GmDeleteGameNotice handler = new GmDeleteGameNotice();
//		GmServerSwitch handler = new GmServerSwitch();
//		GmSwitchBIGm handler = new GmSwitchBIGm();
//		GmOnlineCount handler = new GmOnlineCount();
//		GmKickOffPlayer handler = new GmKickOffPlayer();
//		GmWhiteListModify handler = new GmWhiteListModify();
//		GmWhiteListSwitch handler = new GmWhiteListSwitch();
//		GmExecuteGMCommand handler = new GmExecuteGMCommand();
//		GmServerStatus handler = new GmServerStatus();
//		GMUserInfo handler = new GMUserInfo();
//		GmEditPlatformNotice handler = new GmEditPlatformNotice();
//		GmEditGameNotice handler = new GmEditGameNotice();
//		GmViewGameNotice handler = new GmViewGameNotice();
//		GmViewPlatformNotice handler = new GmViewPlatformNotice();
//		GmEmailSingleSend handler = new GmEmailSingleSend(); 
//		GmUserDetailInfo handler = new GmUserDetailInfo();
//		GMGetRankList handler = new GMGetRankList();
//		int type = Integer.parseInt(args[2]);
////		int type = 3;
//		String rankType = GMGetRankList.RankTypeMap.get(type);
//		handler.setParams(new String[]{rankType});
//		GmEmailSingleCheck handler = new GmEmailSingleCheck();
//		GmViewEquipments handler = new GmViewEquipments();
//		GMAddCoin handler = new GMAddCoin();
//		GMAddGold handler = new GMAddGold();
//		GmOpExp handler = new GmOpExp();
//		GmHotUpdateTest handler = new GmHotUpdateTest();
//		GmOnlineCount handler = new GmOnlineCount();
//		GmChatBanPlayer handler = new GmChatBanPlayer();
//		GmBlockPlayer handler = new GmBlockPlayer();
//		GmBlockRelease handler = new GmBlockRelease();
//		GmResponsePlayerQuestion handler = new GmResponsePlayerQuestion(); 
//		GmFindHeroList handler = new GmFindHeroList();
//		GmFindMagicList handler = new GmFindMagicList();
		GmFindDaoistList handler =new GmFindDaoistList();
		
		
		GmRequest request = handler.createGmRequest();
//		processGmRequest(request, ip, port);
		
		
//		processGmRequest(request, "119.29.19.55", 10091);
//		processGmRequest(request, "119.29.157.158", 12345);
//		processGmRequest(request, "119.29.163.123", 12345);
//		processGmRequest(request, "119.29.163.123", 7098);
//		processGmRequest(request, "119.29.111.118", 7098);
		processGmRequest(request, "192.168.2.124", 12345);
//		processGmRequest(request, "119.29.162.42", 12346);
//		processGmRequest(request, "119.29.111.118", 7098);
//		processGmRequest(request, "192.168.2.233", 12345);
	}
	
	public static void Menu(){
		System.out.println("---------------------------------------");
		System.out.println("1 给所有玩家发送邮件");
		System.out.println("1 给所有玩家发送邮件");
		System.out.println("---------------------------------------");
	}

	public static byte[] dataFormat(short protno, String json) throws UnsupportedEncodingException {
		// 包头计算：包头(short,4个字节) = 包体长度%255(1个字节) + 包体长度&0x00ffffff(3个字节)
		byte[] jsonbytes= json.getBytes("utf-8");
		short jsonLen = (short) jsonbytes.length;
		int bodyLength = 2 + 2 + jsonLen;
		bodyLength = 1;
		
		int header = ((bodyLength % 255) << 24) | (bodyLength & 0x00ffffff);
		ByteBuffer dataInfo = ByteBuffer.allocate(8 + jsonLen);
		// 转换为小端模式，默认为大端。
		// dataInfo.order(ByteOrder.LITTLE_ENDIAN);
		// 设置包头
		dataInfo.putInt(header);
//		System.out.println("header:" + header);
		// 包体计算：包体=协议号(2个字节) + json字符串长度(2字节) + json字符串内容
		dataInfo.putShort(protno);
		dataInfo.putShort(jsonLen);
//		System.out.println("jsonLen:" + jsonLen);
		dataInfo.put(jsonbytes);
		return dataInfo.array();
	}
	
	public static <T> T read(DataInputStream input, Class<T> clazz) throws IOException{

		T content = null;
		int len = input.readInt();
		int firstLen = len >>> 24;
		int realLength = len & 0x00ffffff;
		if (firstLen == (realLength % 255)) {
			short readShort = input.readShort();
			if (PROTO_NO == readShort) {
				short jsonLength = input.readShort();

				byte[] jsonBody = new byte[jsonLength];
				input.read(jsonBody);
				String json = new String(jsonBody, "utf-8");
				content = JsonUtil.readValue(json,clazz);
				System.out.println(json);
			}

		}
		
		return content;
	}
	

	public static void processGmRequest(GmRequest request, String ip, int port) {
		Socket socket = null;

		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port));
			socket.setSoTimeout(10000);
			socket.setSendBufferSize(100*1024);
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			String json = JsonUtil.writeValue(request);
			System.out.println(json);
//			File file = new File("D://20040.txt");
//			InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
//			BufferedReader bufferedReader = new BufferedReader(reader);
//			String json = bufferedReader.readLine();
//			bufferedReader.close();
			byte[] dataFormat = dataFormat(protno, json);

			out.write(dataFormat);
			out.flush();

			GmResponse read = read(input, GmResponse.class);
			
			System.out.println(read.getStatus() == 0 ? "success" : "fail");
			if (read.getResult().size() > 0) {
				List<Map<String, Object>> list = read.getResult();
				for (Map<String, Object> map : list) {
					for (Iterator<Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
						Entry<String, Object> entry = iterator.next();
						System.out.print(entry.getKey() + ":" + entry.getValue());
					}
				}
			}
			out.close();
			input.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
