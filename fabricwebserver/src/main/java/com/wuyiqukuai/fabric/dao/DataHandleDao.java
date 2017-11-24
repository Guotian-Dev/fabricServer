package com.wuyiqukuai.fabric.dao;

import java.util.List;

import com.wuyiqukuai.fabric.domain.ParamEntity;

public interface DataHandleDao {
	
	public void insertData(List<ParamEntity> jsonToList) throws Exception;

	public void batchInsertData(List<ParamEntity> jsonToList);

	public String queryByKey(String key);

	public void deleteByKey(String key) throws Exception;

	public String queryBlockInfo(String peerName) throws Exception;

	public String queryByKeyAndPeerName(String key, String peerName) throws Exception;

	public String queryBlock() throws Exception;

	public String zz(String skf, String fkf, String je);
	
}
