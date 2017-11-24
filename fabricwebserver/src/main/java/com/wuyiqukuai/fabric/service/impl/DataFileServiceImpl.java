package com.wuyiqukuai.fabric.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wuyiqukuai.fabric.dao.DataFileDao;
import com.wuyiqukuai.fabric.domain.FileMetaData;
import com.wuyiqukuai.fabric.service.DataFileService;
import com.wuyiqukuai.fabric.util.DataJsonUtils;

@Service
public class DataFileServiceImpl implements DataFileService {

	@Autowired
	private DataFileDao dataFileDao;

	private final static Logger logger = LoggerFactory.getLogger(DataFileServiceImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getFilesByPage(String startTime, String endTime, Integer page, Integer rows) {

		Map<String, Object> rtMap = new HashMap<String, Object>();

		String fileJson = dataFileDao.getFileJson(page, rows);
		
		Map<String, Object> map = DataJsonUtils.fromJson(fileJson, Map.class);
		
		logger.debug("map:" + map);

		Object total = map.get("file_total");

		Object object = map.get("file_group");

		String json = DataJsonUtils.toJson(object);

		List<FileMetaData> fileList = DataJsonUtils.jsonToList(json, FileMetaData.class);
		
		//如果时间不为null进行过滤
		if((startTime != null && startTime != "") && (endTime != null && endTime != "")) {
			
			List<FileMetaData> newFileList = new ArrayList<FileMetaData>();
			
			Long sT = handleTime(startTime);
			Long eT = handleTime(endTime);
			
			Iterator<FileMetaData> iterator = fileList.iterator();
			
			int count = 0;
			
			while(iterator.hasNext()) {
				FileMetaData file = iterator.next();
				String upload_time = file.getUpload_time();
				
				String[] split = upload_time.trim().split("\\s+");
				
				Long f = handleTime(split[0]);
				
				if(f >= sT && f <= eT) {
					newFileList.add(file);
					count ++;
				}
			}
			
			rtMap.put("total", count);
			rtMap.put("rows", newFileList);
			
			return rtMap;
		}
		
		//结束时间为空(开始时间有效)
		if((startTime != null && startTime != "") && (endTime == "" || endTime == null)) {
			List<FileMetaData> newFileList = new ArrayList<FileMetaData>();
			
//			System.out.println("endTime=>" + endTime);
			
			Long sT = handleTime(startTime);
			
			Iterator<FileMetaData> iterator = fileList.iterator();
			
			int count = 0;
			
			while(iterator.hasNext()) {
				FileMetaData file = iterator.next();
				String upload_time = file.getUpload_time();
				
				String[] split = upload_time.trim().split("\\s+");
				
				Long f = handleTime(split[0]);
				
				if(f >= sT) {
					newFileList.add(file);
					count ++;
				}
			}
			
			rtMap.put("total", count);
			rtMap.put("rows", newFileList);
		} else {
			rtMap.put("total", total);
			rtMap.put("rows", fileList);
		}

		logger.debug("rtMap:" + rtMap);

		return rtMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getTxidsByFileName(String fileName, String file_uuid, Integer page, Integer rows) {

		List<String> rtList = new ArrayList<String>();

		String fileJson = dataFileDao.getFileJson(page, rows);

		logger.debug("fileJsonTxids->" + fileJson);

		Map<String, Object> map = DataJsonUtils.fromJson(fileJson, Map.class);

		logger.debug("map:" + map);

		Object object = map.get("file_group");

		String json = DataJsonUtils.toJson(object);

		List<FileMetaData> fileList = DataJsonUtils.jsonToList(json, FileMetaData.class);

		Iterator<FileMetaData> fileIt = fileList.iterator();

		while (fileIt.hasNext()) {
			FileMetaData next = fileIt.next();
			if (fileName.equals(next.getFile_name()) && file_uuid.equals(next.getFile_uuid())) {
				rtList.addAll(next.getTx_ids());
				break;
			}
		}

		return rtList;
	}

	@Override
	public byte[] getBytesByTxId(String txId) {
		return dataFileDao.getByteByTxId(txId);
	}
	
	//将时间转换为毫秒
	public static Long handleTime(String time) {
//		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		
		//时间取到日
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
	
	//2017-09-18
	//2017-09-19

}
