package com.wuyiqukuai.fabric.service;

import java.util.List;
import java.util.Map;

public interface DataFileService {
	
	public Map<String, Object> getFilesByPage(String startTime, String endTime, Integer page, Integer rows);

	public List<String> getTxidsByFileName(String fileName, String file_uuid, Integer page, Integer rows);

	public byte[] getBytesByTxId(String txId);
	
}
