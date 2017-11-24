package fabricwebserver;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.wuyiqukuai.fabric.domain.FileMetaData;
import com.wuyiqukuai.fabric.util.DataHandleUtil;
import com.wuyiqukuai.fabric.util.DataJsonUtils;
import com.wuyiqukuai.fabric.util.FileHandle;

public class Jtest {
	
	@Test
	public void testTime() {
		Long handleTime = handleTime("2017-09-18");
		System.out.println(handleTime);
	}
	
	@Test
	public void testTime2() {
		String t = " 2017-09-18   10:20:17";
		//若是任意个空格，也可以分割
		String[] split = t.trim().split("\\s+");
//		String[] split = t.split(" ");
		for (String string : split) {
			System.out.println(string);
		}
	}
	
	@Test
	public void testUUid() {
        UUID uuid=UUID.randomUUID();
        String str = uuid.toString();
        System.out.println(str);
        String uuidStr=str.replace("-", "");
        System.out.println(uuidStr);
	}
	
	@Test
	public void testSha256() {
		String sha256StrJava = DataHandleUtil.getSHA256StrJava("hello");
		System.out.print(sha256StrJava);
	}
	@Test
	public void testMd5() throws NoSuchAlgorithmException, UnsupportedEncodingException {
//		String md5 = DataHandleUtil.EncoderByMd5("admin");
//		System.out.println(md5);
		
		String base64 = DataHandleUtil.getBase64("admin");
		System.out.println(base64);
	}
	

	
	
//	@Autowired
//	private DataFileDao dataFileDao;
	
//	@Test
//	public void test7() {
//		String strJson = "{"+
//		        "'file_total': 9,"+
//		        "'file_begin': 1,"+
//		        "'file_end': 9,"+
//		        "'file_group': ["+
//		        "{"+
//		         "'upload_time': '2017-08-16 14:45:36',"+
//		           "'tx_ids': ["+
//		            "'778cf1fedb7e611c6b98f7da587bc243e6be7ea8af0729730a0223722f6457be'"+
//		           "],"+
//		           "'file_name': 'res.zip',"+
//		           "'file_id': '0e74768ae9d4fab314492a8ed51882072aca2459',"+
//		           "'block_number': 1,"+
//		           "'block_size': 1048576,"+
//		           "'file_size': 195196"+
//		       "}]"+
//		    "}";
//		
//		List<FileInfoJson> jsonToList = DataJsonUtils.jsonToList(strJson, FileInfoJson.class);
//		FileInfoJson fileInfoJson = jsonToList.get(0);
//		System.out.println(fileInfoJson.toString());
//	}
	
	@Test
	public void test6() {
		String strJson = "{"+
        "'file_total': 9,"+
        "'file_begin': 1,"+
        "'file_end': 9,"+
        "'file_group': ["+
        "{"+
         "'upload_time': '2017-08-16 14:45:36',"+
           "'tx_ids': ["+
            "'778cf1fedb7e611c6b98f7da587bc243e6be7ea8af0729730a0223722f6457be'"+
           "],"+
           "'file_name': 'res.zip',"+
           "'file_id': '0e74768ae9d4fab314492a8ed51882072aca2459',"+
           "'block_number': 1,"+
           "'block_size': 1048576,"+
           "'file_size': 195196"+
       "}]"+
    "}";
		
		Map<String, Object> map = DataJsonUtils.fromJson(strJson, Map.class);
		System.out.println(map);
		
		Object total = map.get("file_total");
		
		Object object = map.get("file_group");
		
		String json = DataJsonUtils.toJson(object);
		
		System.out.println("json" + json);
		
		List<FileMetaData> jsonToList = DataJsonUtils.jsonToList(json, FileMetaData.class);
		
		System.out.println(jsonToList);
		
		System.out.println(jsonToList.get(0).toString());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void test5() {
//		BlockFile bf = new BlockFile(System.currentTimeMillis());
//		
//		String json = DataJsonUtils.toJson(bf);
//		System.out.print(bf.getUploadTime());
//		
//		System.out.println(json);
		//{"uploadTime":1502850596038}
		
//		System.out.println(System.currentTimeMillis());
		
//		BlockFile bf = new BlockFile(System.currentTimeMillis());
//		
//		String json = DataJsonUtils.toJsonContainDate(bf);
//		System.out.println(json);
//		
//		BlockFile bf2 = DataJsonUtils.fromJsonContainDate(json, BlockFile.class);
//		System.out.println(bf2.getUploadTime());
		
		
		String string = String.valueOf(System.currentTimeMillis());
		
		System.out.println(string);
		
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		
		String rq = format.format(new Date(Long.valueOf(string)));
		
		System.out.println(rq);
		
		Date date = null;
		try {
			date = format.parse(rq);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(date.getTime());
		
		
//		String json2 = "{'uploadTime':1502850596038}";
//		
//		BlockFile fromJson = DataJsonUtils.fromJsonContainDate(json2, BlockFile.class);
//		System.out.println("************************************");
//		System.out.println(fromJson.getUploadTime());
		
		
	}
	
//	@Test
//	public void test4() {
//		
//		DataFileDaoImpl dt = new DataFileDaoImpl();
//		
//		String fileInfo = dt.getFileInfo(1, 2);
//		System.out.println("第一次：" + fileInfo);
//		
//		String fileInfo1 = dt.getFileInfo(1, 2);
//		System.out.println("第二次：" + fileInfo1);
//	}
	
	
	
	@Test
	public void test3() {
		//1618793583 B  
		File file = new File("C:/res/dawenjian.zip");
		int fSize = Integer.parseInt(String.valueOf(file.length()));
		System.out.println(fSize);
		
		int fileBlockCount = FileHandle.fileSplit(FileHandle.MB, fSize);
		System.out.println("块数" + fileBlockCount);
		
		System.out.println("-------------------------------------");
		
		
		System.out.println(fSize / 1000000 + "MB");
		System.out.println(fSize % 1000000 + "B");
		
		System.out.println(fSize / 1000000 + 1 + "块");
	}
	
	@Test
	public void test2() {
		
		//3bd84aa92896264f0a65435340666971
		long start = System.currentTimeMillis();
		String MD5HASH = FileHandle.getHash("C:/res/dawenjian.zip", "MD5");
		System.out.println(MD5HASH);
		System.out.println((System.currentTimeMillis() - start) / 1000 + "s");
	}
	
	@Test
	public void test() {
		String str = "{'channel':[{'peerName':'peer0.org1.example.com','channelHeight':'2','channelName':'foo','currentBlockHash':'d440ea565ee4352da97e273b82d3007262bcf13e625411adce0df11b5d3622ab'}],'blockMap':[{'blockHash':'dc040be48c09610214a9c41f78560b85ad9dc3f4935baf9e7b8ca3783b763e08','blockHeight':'1','preBlockHash':'edd1e85fd1460e80b97ff58c65b04643d2b300b47f9a4fc87e5abfd63fce9a14'},{'blockHash':'3372a07c361ba3942a6a9ef7ca113e48f3f9ac6ea3e70cc5b1a3b2ff0aa73144','blockHeight':'0','preBlockHash':'0'}]}";
//		Map<String, List<Map<String, String>>> jsonToDcQt = DataJsonUtils.jsonToDcQt(str, String.class);
		Map<String, List> jsonToMap = DataJsonUtils.jsonToMap(str, List.class);
		System.out.println(jsonToMap);
		
		List list = jsonToMap.get("channel");
		System.out.println(list);
		
		List list2 = jsonToMap.get("blockMap");
		System.out.println(list2);
		
	}
	
//	@Test
//	public void test1() {
//		
//		String fileInfo = DataJsonUtils.fileInfo();
//		
//		Map<String, Object> jsonToMap = DataJsonUtils.jsonToMap(fileInfo,Object.class);
//		
//		String oneFile = DataJsonUtils.toJson(jsonToMap.get("file_group"));
//		
//		System.out.println("oneFile->" + oneFile);
//		
//		List<BlockFileInfo> jsonToList = DataJsonUtils.jsonToList(oneFile, BlockFileInfo.class);
//		
//		System.out.println("jsonToList->" + jsonToList);
//		
//		List<BlockFileInfo> fileInfoList = (List<BlockFileInfo>) jsonToMap.get("file_group");
//		
//		System.out.println(fileInfo);
//		System.out.println(fileInfoList);
//	}
	
	//将时间转换为毫秒
	public static Long handleTime(String time) {
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd"); 
		
		Long ms = null;
		
		Date date = null;
		try {
			System.out.println(time);
			date = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(date != null) {
			ms = date.getTime();
		}
		
		return ms;
	}

}
