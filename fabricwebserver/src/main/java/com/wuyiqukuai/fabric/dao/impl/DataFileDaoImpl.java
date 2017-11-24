package com.wuyiqukuai.fabric.dao.impl;

import org.hyperledger.fabric.filestore.FileStoreDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wuyiqukuai.fabric.dao.DataFileDao;

@Repository
public class DataFileDaoImpl implements DataFileDao{
	
	//若是自动注入不成功，不报错，注意！！！
	// Started Application in 4.843 seconds (JVM running for 53.363) 成功启动
//	@Autowired
//	private DataDriver dataDriver;
	
	private final static Logger logger = LoggerFactory.getLogger(DataFileDaoImpl.class);
	
//	@Autowired
	private FileStoreDriver fileStoreDriver;
	
	@Override
	public String getFileJson(Integer page, Integer rows) {
		
		String fileJson = fileStoreDriver.queryByChainCode(page, rows);
		
		logger.info("-----------------执行查询------------------------");
		
		logger.debug("执行getFileInfo");
		logger.debug("fileJosn->" + fileJson);
		
		return fileJson;
	}

	@Override
	public byte[] getByteByTxId(String txId) {
		try {
			return fileStoreDriver.getPayloadByTxID(txId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
