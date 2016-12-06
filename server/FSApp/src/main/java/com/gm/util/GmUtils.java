package com.gm.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.playerdata.ItemCfgHelper;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.log.GmLog;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.item.pojo.ItemBaseCfg;

public class GmUtils {
	
private static final DocumentBuilderFactory documentBuilderFactory;
	
	static {
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
	}
	
	public static Pair<String, List<String>> getHotUpdateInfo() throws Exception {
		URL url = ClassLoader.getSystemResource("runtimeupdate.xml");
		Document document = documentBuilderFactory.newDocumentBuilder().parse(new File(url.getFile()));
		Node root = document.getFirstChild();
		NodeList nodeList = root.getChildNodes();
		List<String> list = Collections.emptyList();
		String version = "";
		for (int i = 0, length = nodeList.getLength(); i < length; i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("updateList")) {
				NodeList updateList = node.getChildNodes();
				list = new ArrayList<String>(updateList.getLength());
				for (int j = 0, updateSize = updateList.getLength(); j < updateSize; j++) {
					Node updateNode = updateList.item(j);
					if (updateNode.getNodeName().equals("classPath")) {
						list.add(updateNode.getTextContent().trim());
					}
				}
			} else if (node.getNodeName().equals("version")) {
				version = node.getTextContent().trim();
			}
		}
		return Pair.Create(version, list);
	}

	
	public static boolean parseBoolean(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		boolean result = false;
		if(object == null){
			return result;
		}else{
			
			try {
				result = Boolean.parseBoolean(object.toString());
			} catch (Exception ex) {
				result = false;
			}
			return result;
		}
	}
	
	public static String parseString(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		if(object == null){
			return "";
		}else{
			return object.toString();
		}
	}
	
	public static int parseInt(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		if(object == null){
			return 0;
		}else{
			int value = 0;
			try {
				value = Integer.parseInt(object.toString());
			} catch (Exception ex) {
				value = 0;
			}
			return value;
		}
	}
	
	public static long parseLong(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		if(object == null){
			return 0;
		}else{
			long value = 0;
			try {
				value = Long.parseLong(object.toString());
			} catch (Exception ex) {
				value = 0;
			}
			return value;
		}
	}
	
	public static byte[] dataFormat(short protno, String json) throws UnsupportedEncodingException {
		// 包头计算：包头(short,4个字节) = 包体长度%255(1个字节) + 包体长度&0x00ffffff(3个字节)
		byte[] jsonbytes = json.getBytes("utf-8");
		short jsonLen = (short) jsonbytes.length;
		int bodyLength = 2 + 2 + jsonLen;
		bodyLength = 1;

		int header = ((bodyLength % 255) << 24) | (bodyLength & 0x00ffffff);
		ByteBuffer dataInfo = ByteBuffer.allocate(8 + jsonLen);
		// 转换为小端模式，默认为大端。
		// dataInfo.order(ByteOrder.LITTLE_ENDIAN);
		// 设置包头
		dataInfo.putInt(header);

		// 包体计算：包体=协议号(2个字节) + json字符串长度(2字节) + json字符串内容
		dataInfo.putShort(protno);
		dataInfo.putShort(jsonLen);

		dataInfo.put(jsonbytes);
		return dataInfo.array();
	}
	
	public static <T> T read(DataInputStream input, Class<T> clazz) throws IOException {
		T content = null;
		input.readInt();
		input.readShort();
		short jsonLength = input.readShort();

		byte[] jsonBody = new byte[jsonLength];
		input.read(jsonBody);
		String json = new String(jsonBody, "utf-8");
		content = FastJsonUtil.deserialize(json, clazz);
		GmLog.info("SocketHelper[read] 处理gm请求：" + json);
		return content;
	}
	
	public static boolean checkAttachItemIegal(String attachment){
		if (StringUtils.isNotBlank(attachment)) {
			String[] split = attachment.split(",");
			for (String itemTmp : split) {
				if (StringUtils.isEmpty(itemTmp)) {
					continue;
				}
				String[] itemTmpSplit = itemTmp.split("~");
				if (itemTmpSplit.length == 2) {
					int itemCode = Integer.valueOf(itemTmpSplit[0]);
					eSpecialItemId def = eSpecialItemId.getDef(itemCode);
					if (def != null) {
						if (def == eSpecialItemId.eSpecial_End) {
							return false;
						}
					}else if(ItemCfgHelper.isFashionSpecialItem(itemCode)){
						return true;
					} else {
						ItemBaseCfg itemBaseCfg = ItemCfgHelper
								.GetConfig(itemCode);
						if (itemBaseCfg == null) {
							return false;
						}
					}

				} else {
					return false;
				}
			}
		}
		return true;
	}
}
